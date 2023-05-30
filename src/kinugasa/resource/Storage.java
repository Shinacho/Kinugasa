/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kinugasa.resource;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kinugasa.game.GameLog;
import kinugasa.game.system.GameSystem;

/**
 * アルゴリズムなどの命名可能なオブジェクトを格納するマップです.
 * <br>
 * このクラスは、Nameableを実装したクラスをHashMapに登録し、容易にアクセスできるようにします。<br>
 * ストレージには、同じ名前のオブジェクトを登録することは出来ません。 ストレージの容量は、自動的に拡大されます。<br>
 * <br>
 * ゲーム中、1つの場所にNameableの実装を保存したい場合は、 このクラスを継承することで、唯一の保存領域を作成することが出来ます。<br>
 * このクラスは、シリアライズ可能ではありません。そのような機能は、サブクラスで 定義する必要があります。<br>
 * <br>
 *
 * @param <T> このストレージが使用する命名可能なオブジェクトを指定します。<br>
 *
 * @version 1.0.0 - 2012/11/18_0:14:31<br>
 * @version 1.0.2 - 2013/01/12_22:16:16<br>
 * @version 1.1.0 - 2013/02/19_00:49<br>
 * @version 1.1.2 - 2013/04/13_19:31<br>
 * @version 2.0.0 - 2013/04/20_17:57<br>
 * @author Shinacho<br>
 */
public class Storage<T extends Nameable> implements Iterable<T> {

	/**
	 * Tを保管するマップです.
	 */
	private HashMap<String, T> map;

	/**
	 * 新しいストレージを作成します.
	 */
	public Storage() {
		map = new HashMap<String, T>(32);
	}

	/**
	 * 新しいストレージを作成します.
	 *
	 * @param initialSize マップの初期容量を指定します。<br>
	 */
	public Storage(int initialSize) {
		map = new HashMap<String, T>(initialSize);
	}

	/**
	 * 指定した名前のオブジェクトを取得します.
	 *
	 * @param key 取得するオブジェクトの名前を指定します。<br>
	 *
	 * @return 指定した名前を持つオブジェクトを返します。<br>
	 *
	 * @throws NameNotFoundException 存在しない名前を指定した場合に投げられます。<br>
	 */
	public T get(String key) throws NameNotFoundException {
		if (!contains(key)) {
			throw new NameNotFoundException("! > Storage(" + getClass() + ") : get : not found : key=[" + key + "]");
		}
		return map.get(key);
	}

	public List<T> filter(Predicate<? super T> p) {
		return stream().filter(p).collect(Collectors.toList());
	}

	public T firstOf(Predicate<? super T> p) {
		return stream().filter(p).collect(Collectors.toList()).get(0);
	}

	public T first() {
		return asList().get(0);
	}

	/**
	 * 指定したキーの要素が含まれている場合に、それを取得します.<br>
	 *
	 * @param key 取得するオブジェクトのキーを指定します。<br>
	 *
	 * @return 指定したキーのオブジェクトが含まれていればそれを、含まれていなければnullを返します。<br>
	 */
	public T getOrNull(String key) {
		return map.get(key);
	}

	/**
	 * このストレージに追加されているオブジェクトをすべて取得します.
	 * このメソッドの戻り値は参照ではありません。新しく作成されたコレクションです。<br>
	 *
	 * @return 保管されているすべてのオブジェクトのコレクションを返します。コレクションに格納される順番は
	 * ストレージに追加された順番と一致しません。<br>
	 */
	public Collection<T> getAll() {
		return map.values();
	}

	/**
	 * このストレージに追加されているオブジェクトをすべて取得します. このメソッドの戻り値は参照ではありません。新しく作成されたリストです。<br>
	 *
	 * @return 保管されているすべてのオブジェクトのリストを返します。リストに格納される順番は ストレージに追加された順番と一致しません。<br>
	 */
	public List<T> asList() {
		return new ArrayList<T>(getAll());
	}

	/**
	 * 指定した名前を持つオブジェクトが格納されているかを調べます.
	 *
	 * @param key 検索するオブジェクトの名前を指定します。<br>
	 *
	 * @return 指定した名前のオブジェクトが含まれている場合はtrueを返します。<br>
	 */
	public boolean contains(String key) {
		return map.containsKey(key);
	}

	/**
	 * ランダムな要素を返します。
	 *
	 * @return ランダムに選択された要素。要素がない場合はnullを返す。
	 */
	public T random() {
		if (isEmpty()) {
			return null;
		}
		List<T> list = asList();
		Collections.shuffle(list);
		return list.get(0);
	}

	/**
	 * 指定した名前を持つオブジェクトが、すべて格納されているかを調べます.
	 *
	 * @param keys 検索するオブジェクトの名前を指定します。<br>
	 *
	 * @return 指定した名前が全て含まれている場合に限り、trueを返します。<br>
	 */
	public boolean containsAll(String... keys) {
		for (String key : keys) {
			if (!contains(key)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 指定したオブジェクトが格納されているかを調べます.
	 *
	 * @param obj 検索するオブジェクトを指定します。<br>
	 *
	 * @return 指定したオブジェクトが含まれている場合はtrueを返します。<br>
	 */
	public boolean contains(T obj) {
		return contains(obj.getName());
	}

	@Deprecated
	public final Map<String, T> getDirect() {
		return map;
	}

	/**
	 * 新しいオブジェクトをマップに追加します.
	 *
	 * @param val 追加するオブジェクトを指定します。<br>
	 *
	 * @throws DuplicateNameException valの名前が既に使用されているときに投げられます。<br>
	 */
	public void add(T val) throws DuplicateNameException {
		if (val.getName() == null) {
			throw new NameNotFoundException("null key : " + this);
		}
		if (getDirect().containsKey(val.getName())) {
			throw new DuplicateNameException("! > Storage : add : duplicate name : name=[" + val.getName() + "] : ");
		}
		map.put(val.getName(), val);
	}

	/**
	 * 新しいオブジェクトをマップに追加します.
	 *
	 * @param values 追加するオブジェクトを指定します。<br>
	 *
	 * @throws DuplicateNameException valの名前が既に使用されているときに投げられます。<br>
	 */
	public void addAll(T... values) throws DuplicateNameException {
		addAll(Arrays.asList(values));
	}

	public void addAll(Storage<? extends T> s) throws DuplicateNameException {
		addAll(s.asList());
	}

	/**
	 * 新しいオブジェクトをマップに追加します.
	 *
	 * @param values 追加するオブジェクトを指定します。<br>
	 *
	 * @throws DuplicateNameException valの名前が既に使用されているときに投げられます。<br>
	 */
	public void addAll(Collection<? extends T> values) throws DuplicateNameException {
		for (T value : values) {
			add(value);
		}
	}

	/**
	 * オブジェクトを、上書きで追加します. このメソッドは同じ名前を持つオブジェクトが登録されている場合に上書きします。<br>
	 *
	 * @param val 追加するオブジェクトを指定します。<br>
	 */
	public void put(T val) {
		map.put(val.getName(), val);
	}

	/**
	 * 複数のオブジェクトを上書きで追加します.
	 *
	 * @param values 追加するオブジェクトを指定します。<br>
	 */
	public void putAll(T... values) {
		putAll(Arrays.asList(values));
	}

	/**
	 * 複数のオブジェクトを上書きで追加します.
	 *
	 * @param values 追加するオブジェクトを指定します。<br>
	 */
	public void putAll(Collection<? extends T> values) {
		for (T value : values) {
			put(value);
		}
	}

	/**
	 * 指定した名前を持つオブジェクトをマップから削除します.
	 *
	 * @param key 削除するオブジェクトの名前を指定します。<br>
	 */
	public void remove(String key) {
		map.remove(key);
	}

	/**
	 * オブジェクトをマップから削除します.
	 *
	 * @param val 削除するオブジェクトを指定します。<br>
	 */
	public void remove(T val) {
		remove(val.getName());
	}

	/**
	 * 指定した名前を持つオブジェクトをマップから削除します.
	 *
	 * @param keys 削除するオブジェクトの名前を指定します。<br>
	 */
	public void removeAll(String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}

	/**
	 * オブジェクトをマップから削除します.
	 *
	 * @param values 削除するオブジェクトを指定します。<br>
	 */
	public void removeAll(T... values) {
		for (T key : values) {
			remove(key.getName());
		}
	}

	/**
	 * オブジェクトをマップから削除します.
	 *
	 * @param values 削除するオブジェクトを指定します。<br>
	 */
	public void removeAll(Collection<? extends T> values) {
		for (T key : values) {
			remove(key.getName());
		}
	}

	/**
	 * マップに追加されているオブジェクトの数を取得します.
	 *
	 * @return マップの要素数を返します。<br>
	 */
	public int size() {
		return map.size();
	}

	/**
	 * マップからすべてのオブジェクトを削除します.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * マップの要素数が空であるかを調べます.
	 *
	 * @return マップが空の場合はtrueを返します。<br>
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * 現在保持している全てのオブジェクトをストリームに出力します. このメソッドはデバッグ用です。<br>
	 *
	 * @param stream 書き出すストリームを指定します。<br>
	 */
	public void printAll(PrintStream stream) {
		printAll(stream, false);
	}

	/**
	 * 現在保持している全てのオブジェクトをストリームに出力します. このメソッドはデバッグ用です。<br>
	 *
	 * @param stream 書き出すストリームを指定します。<br>
	 * @param valueOut trueを指定すると値も出力します。<br>
	 */
	public void printAll(PrintStream stream, boolean valueOut) {
		if (!GameSystem.isDebugMode()) {
			return;
		}
		stream.println("> Storage : class=[" + getClass() + "]----------------");
		GameLog.print("> Storage : class=[" + getClass() + "]----------------");
		for (T obj : map.values()) {
			stream.print("  " + obj.getName() + (valueOut ? obj : ""));
			GameLog.print("  " + obj.getName() + (valueOut ? obj : ""));
			stream.println();
		}
		stream.println("> Storage : ------------------------");
		GameLog.print("> Storage : ------------------------");
	}

	/**
	 * 指定した名前を持つオブジェクトを新しいマップに格納して返します. 存在しない名前を指定した場合は、その名前は無視されます。戻り値の
	 * マップには、存在が確認されたオブジェクトだけが格納されます。<br>
	 *
	 * @param names 戻り値に追加するオブジェクトの名前を指定します。<br>
	 *
	 * @return 指定した名前を持つオブジェクトを新しいマップに格納して返します。<br>
	 */
	public Map<String, T> getAsNewMap(String... names) {
		Map<String, T> result = new HashMap<String, T>(names.length);
		for (String name : names) {
			if (contains(name)) {
				result.put(name, get(name));
			}
		}
		return result;
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	/**
	 * 全ての要素を参照できるイテレータを返します. 要素の順番は、HashSetに依存します。並び順を設定する必要がある場合は
	 * asListを使用してください。<br>
	 *
	 * @return このストレージの要素を参照するイテレータを返します。<br>
	 */
	@Override
	public Iterator<T> iterator() {
		return map.values().iterator();
	}

	public List<T> asSortedList(Comparator<? super T> c) {
		List<T> list = new ArrayList<>(asList());
		Collections.sort(list, c);
		return list;
	}

	@Override
	public void forEach(Consumer<? super T> c) {
		asList().forEach(c);
	}

	public Stream<T> stream() {
		return asList().stream();
	}

	public List<T> getAll(String... v) {
		List<T> result = new ArrayList<>();
		for (String s : v) {
			result.add(get(s));
		}
		return result;
	}

	@Override
	public String toString() {
		return "Storage{" + "map=" + map + '}';
	}
}
