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
package kinugasa.resource;

import java.util.Collection;
import java.util.function.Consumer;
import kinugasa.util.ArrayUtil;

/**
 * 要素のロード／開放機能を追加したストレージの実装です.
 * <br>
 * このストレージの拡張は、Freeableを実装します。Freeableの機能は、全ての要素に適用されます。
 * isLoadedは1つ以上の要素がロードされている場合にtrueを返します。全ての要素がロードされているかを検査するには
 * isLoadedAllを使用します。<br>
 * <br>
 *
 * @param <T> このストレージが保存する命名可能で開放可能な型を指定します。<br>
 *
 * @version 1.0.0 - 2012/11/18_0:14:31<br>
 * @version 1.0.2 - 2013/01/12_22:16:16<br>
 * @version 1.1.0 - 2013/02/19_00:49<br>
 * @version 1.1.2 - 2013/04/13_19:31<br>
 * @version 1.4.0 - 2013/04/28_23:40<br>
 * @author Shinacho<br>
 */
public abstract class InputObjectStorage<T extends Nameable & Input>
		extends Storage<T> implements Input {

	/**
	 * 新しいストレージを作成します.
	 *
	 * @param initialSize ストレージの初期容量を指定します。<br>
	 */
	public InputObjectStorage(int initialSize) {
		super(initialSize);
	}

	/**
	 * 新しいストレージを作成します.
	 */
	public InputObjectStorage() {
	}

	/**
	 * 全ての要素をロードします.
	 *
	 * @return このストレージを返します。<br>
	 */
	@Override
	public InputObjectStorage<T> load() {
		for (T obj : this) {
			obj.load();
		}
		return this;
	}

	/**
	 * 指定した名前を持つオブジェクトを、ロードしてから取得します.
	 *
	 * @param name オブジェクトの名前を指定します。<br>
	 * @return ロードされたオブジェクトを返します。<br>
	 * @throws NameNotFoundException 指定した名前を持つオブジェクトがこのストレージに含まれていない
	 * 時に投げられます。<br>
	 */
	public T load(String name) throws NameNotFoundException {
		T obj = get(name);
		obj.load();
		return obj;
	}

	/**
	 * 全ての要素を開放します.
	 *
	 */
	@Override
	public void dispose() {
		for (T obj : this) {
			obj.dispose();
		}
	}

	/**
	 * 指定した名前を持つオブジェクトを、開放から取得します.
	 *
	 * @param name オブジェクトの名前を指定します。<br>
	 * @return 開放されたオブジェクトを返します。<br>
	 * @throws NameNotFoundException 指定した名前を持つオブジェクトがこのストレージに含まれていない
	 * 時に投げられます。<br>
	 */
	public T dispose(String name) throws NameNotFoundException {
		T obj = get(name);
		obj.dispose();
		return obj;
	}

	/**
	 * 指定された全ての要素をロードします.
	 *
	 * @param names ロードする要素の名前を指定します。<br>
	 */
	public void loadAll(String... names) {
		for (T obj : this) {
			if (ArrayUtil.contains(names, obj.getName())) {
				obj.load();
			}
		}
	}

	/**
	 * 指定された全ての要素を開放します.
	 *
	 * @param names 開放する要素の名前を指定します。<br>
	 */
	public void freeAll(String... names) {
		for (T obj : this) {
			if (ArrayUtil.contains(names, obj.getName())) {
				obj.dispose();
			}
		}
	}

	/**
	 * 指定された名前を持つオブジェクト以外を全て開放します.
	 *
	 * @param names 開放しないオブジェクトの名前を送信します。<br>
	 */
	public void exFree(String... names) {
		for (T obj : this) {
			if (!ArrayUtil.contains(names, obj.getName())) {
				obj.dispose();
			}
		}
	}

	@Override
	public InputStatus getStatus() {
		return isEmpty() ? InputStatus.NOT_LOADED : InputStatus.LOADED;
	}

	/**
	 * 全ての要素がロードされているかを検査します.
	 *
	 * @return 全ての要素がロードされている場合はtrueを返します。<br>
	 */
	public boolean isLoadedAll() {
		for (T obj : this) {
			if (!(obj.getStatus() == InputStatus.LOADED)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 指定した名前を持つオブジェクトが、ロードされているかを調べます.
	 *
	 * @param name オブジェクトの名前を指定します。<br>
	 * @return 指定した名前を持つオブジェクトのisLoadedを返します。<br>
	 * @throws NameNotFoundException 指定した名前を持つオブジェクトがこのストレージに含まれていない
	 * 時に投げられます。<br>
	 */
	public boolean isLoaded(String name) throws NameNotFoundException {
		return contains(name) ? get(name).getStatus() == InputStatus.LOADED : false;
	}

	@Override
	public void forEach(Consumer<? super T> c) {
		super.forEach(c); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void addAll(Collection<? extends T> values) throws DuplicateNameException {
		super.addAll(values); //To change body of generated methods, choose Tools | Templates.
	}

	
}
