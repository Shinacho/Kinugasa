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
package kinugasa.object;

/**
 * クローニング可能な特定のアルゴリズムや状態をカプセル化するための抽象クラスです.
 * <br>
 * 全てのモデルの実装は、cloneメソッドを適切にオーバーライドする必要があります。<br>
 * <br>
 * ほとんどのモデルでは、モデルが持つ要素によって、他のインスタンスとの比較が出来ることが期待されます。<br>
 * quealsおよびhashCodeを適切にオーバーライドする必要があります。<br>
 * <br>
 *
 * @version 1.0.0 - 2012/07/14_16:58:06.<br>
 * @version 2.0.0 - 2013/01/11_17:10:31.<br>
 * @author Shinacho.<br>
 */
public abstract class Model implements Cloneable {

	/**
	 * 新しいモデルを作成します.
	 */
	public Model() {
	}

	/**
	 * このモデルのクローンを返します.
	 * クローンはObjectクラスの機能を使って行われます。<br>
	 * 全てのモデルの実装は、cloneメソッドを適切にオーバーライドする必要があります。<br>
	 *
	 * @return このモデルと同じクラスの新しいインスタンスを返します。<br>
	 */
	@Override
	public Model clone() {
		try {
			return (Model) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}

	/**
	 * あるモデルobjのクローンを返します.これはobj.clone()と同じ動作です.
	 *
	 * @param <T> クローンするクラスです。<br>
	 * @param obj クローンするモデルを指定します。<br>
	 *
	 * @return objのクローンを返します。<br>
	 */
	public static <T extends Model> T clone(T obj) {
		return (T) obj.clone();
	}
}
