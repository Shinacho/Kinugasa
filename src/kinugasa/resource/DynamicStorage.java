/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
 * @author Dra0211<br>
 */
public abstract class DynamicStorage<T extends Nameable & Input>
		extends Storage<T> implements Input {

	/**
	 * 新しいストレージを作成します.
	 *
	 * @param initialSize ストレージの初期容量を指定します。<br>
	 */
	public DynamicStorage(int initialSize) {
		super(initialSize);
	}

	/**
	 * 新しいストレージを作成します.
	 */
	public DynamicStorage() {
	}

	/**
	 * 全ての要素をロードします.
	 *
	 * @return このストレージを返します。<br>
	 */
	@Override
	public DynamicStorage<T> load() {
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
