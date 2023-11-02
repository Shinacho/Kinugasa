/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.resource.db;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kinugasa.game.GameLog;
import kinugasa.game.Nullable;
import kinugasa.game.system.GameSystem;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.util.Random;
import kinugasa.game.NotNull;

/**
 * メモリ上になければDBを読みに行くStorageの拡張です。 DB呼び出し部分はサブクラスで定義する必要がありますが、それ以外の処理は自動で行われます。
 * DBに接続されていないときや、テーブルがない時は例外を出しません。 DB上のデータを操作する機能は持ちません。
 *
 * @vesion 1.0.0 - May 28, 2023_9:58:53 AM<br>
 * @author Shinacho<br>
 */
public abstract class DBStorage<T extends Nameable> extends Storage<T> implements Nameable {

	private String name;

	public DBStorage() {
	}

	public DBStorage(String name) {
		this.name = name;
	}

	public DBStorage(boolean allLoad) {
		this();
		loadAll();
	}

	public DBStorage(String name, boolean allLoad) {
		this(name);
		loadAll();
	}

	@Override
	public String getName() {
		return name;
	}

	private boolean allLoaded = false;

	//DBに変化があった場合、リロードすることは可能
	public final void loadAll() {
		if (DBConnection.getInstance().isUsing() && !allLoaded) {
			addAll(selectAll());
		}
		allLoaded = true;
	}

	public boolean isAllLoaded() {
		return allLoaded;
	}

	//全量搭載済みフラグを初期化しなければ全量リロードできない。
	public void setAllLoaded(boolean allLoaded) {
		this.allLoaded = allLoaded;
	}

	@Nullable
	protected abstract T select(String id) throws KSQLException;

	@NotNull
	protected abstract List<T> selectAll() throws KSQLException;

	protected abstract int count() throws KSQLException;

	public final void clearAllMemMap() {
		super.clear();
	}

	public final List<T> getMemData() {
		return super.asList();
	}

	@Override
	public final List<T> asList() {
		LinkedHashMap<String, T> l = new LinkedHashMap<>();
		for (Map.Entry<String, T> e : getDirect().entrySet()) {
			l.put(e.getKey(), e.getValue());
		}
		if (DBConnection.getInstance().isUsing() && !allLoaded) {
			try {
				for (var t : selectAll()) {
					if (!l.containsKey(t.getName())) {
						l.put(t.getName(), t);
					}
				}
			} catch (KSQLException e) {
				GameLog.print(e);
			}
		}
		return l.values().stream().collect(Collectors.toList());
	}

	@Override
	public final List<T> getAll(String... v) {
		return asList().stream().filter(p -> Arrays.asList(v).contains(p.getName())).collect(Collectors.toList());
	}

	@Override
	public final Stream<T> stream() {
		return asList().stream();
	}

	@Override
	public final void forEach(Consumer<? super T> c) {
		asList().forEach(c);
	}

	@Override
	public final List<T> asSortedList(Comparator<? super T> c) {
		List<T> list = asList();
		list.sort(c);
		return list;
	}

	@Override
	public final Iterator<T> iterator() {
		return asList().iterator();
	}

	@Override
	public final Set<String> keySet() {
		Set<String> set = super.keySet();
		if (DBConnection.getInstance().isUsing() && !allLoaded) {
			try {
				set.addAll(selectAll().stream().map(p -> p.getName()).collect(Collectors.toSet()));
			} catch (KSQLException e) {
				GameLog.print(e);
			}
		}
		return set;
	}

	@Override
	public final Map<String, T> getAsNewMap(String... names) {
		Map<String, T> res = new HashMap<>();
		res.putAll(super.getAsNewMap(names));
		if (DBConnection.getInstance().isUsing() && !allLoaded) {
			try {
				for (String v : names) {
					T obj = select(v);
					res.put(obj.getName(), obj);
				}
			} catch (KSQLException e) {
				GameLog.print(e);
			}
		}
		return res;
	}

	@Override
	public final void printAll(PrintStream stream, boolean valueOut) {
		if (!GameSystem.isDebugMode()) {
			return;
		}
		stream.println("> DBStorage : class=[" + getClass() + "]----------------");
		GameLog.print("> DBStorage : class=[" + getClass() + "]----------------");
		stream.println("--MEM");
		GameLog.print("--MEM");
		for (T obj : getDirect().values()) {
			stream.print("  " + obj.getName() + (valueOut ? obj : ""));
			GameLog.print("  " + obj.getName() + (valueOut ? obj : ""));
			stream.println();
		}
		if (DBConnection.getInstance().isUsing() && !allLoaded) {
			try {
				stream.println("--DB");
				GameLog.print("--DB");
				for (T obj : selectAll()) {
					stream.print("  " + obj.getName() + (valueOut ? obj : ""));
					GameLog.print("  " + obj.getName() + (valueOut ? obj : ""));
					stream.println();
				}
			} catch (KSQLException e) {
				GameLog.print(e);
			}
		}
		stream.println("> Storage : ------------------------");
		GameLog.print("> Storage : ------------------------");
	}

	@Override
	public final void printAll(PrintStream stream) {
		printAll(stream, false);
	}

	@Override
	public final boolean isEmpty() {
		if (DBConnection.getInstance().isUsing()) {
			try {
				return super.isEmpty() && count() == 0;
			} catch (KSQLException e) {
				GameLog.print(e);
			}
		}
		return super.isEmpty();
	}

	public final boolean isEmptyMem() {
		return super.isEmpty();
	}

	@Override
	public final void clear() {
		super.clear();
	}

	@Override
	public final int size() {
		if (DBConnection.getInstance().isUsing() && !allLoaded) {
			try {
				return super.size() + count();
			} catch (KSQLException e) {
				GameLog.print(e);
			}
		}
		return super.size();
	}

	public final int memSize() {
		return super.size();
	}

	@Override
	public final boolean contains(T obj) {
		return contains(obj.getName());
	}

	@Override
	public final boolean containsAll(String... keys) {
		Set<String> set = asList().stream().map(p -> p.getName()).collect(Collectors.toSet());
		boolean res = true;
		for (String k : keys) {
			res &= set.contains(k);
		}
		return res;
	}

	@Override
	public final T random() {
		return Random.randomChoice(asList());
	}

	@Override
	public final boolean contains(String key) {
		if (DBConnection.getInstance().isUsing() && !allLoaded) {
			try {
				return super.contains(key) || select(key) != null;
			} catch (KSQLException e) {
				GameLog.print(e);
			}
		}
		return super.contains(key);
	}

	@Override
	public final Collection<T> getAll() {
		return asList();
	}

	@Override
	public final T getOrNull(String key) {
		if (contains(key)) {
			return get(key);
		}
		return null;
	}

	@Override
	public final T first() {
		return isEmpty() ? null : asList().get(0);
	}

	@Override
	public final T firstOf(Predicate<? super T> p) {
		return asList().stream().filter(p).findFirst().get();
	}

	@Override
	public final List<T> filter(Predicate<? super T> p) {
		return asList().stream().filter(p).collect(Collectors.toList());
	}

	@Override
	public final T get(String key) throws NameNotFoundException {
		if (getDirect().containsKey(key)) {
			return getDirect().get(key);
		}
		if (DBConnection.getInstance().isUsing() && !allLoaded) {
			try {
				T obj = select(key);
				if (obj == null) {
					throw new NameNotFoundException("DBStorage : " + key + " is no found");
				}
				return obj;
			} catch (KSQLException e) {
				GameLog.print(e);
			}
		}
		throw new NameNotFoundException("DBStorage : " + key + " is no found");
	}

	@Override
	public final Spliterator<T> spliterator() {
		return asList().spliterator();
	}

}
