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
package kinugasa.util;

import kinugasa.object.Model;

/**
 * 配列のインデックスの遷移順序を定義します.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_15:42:02<br>
 * @author Dra0211<br>
 */
public abstract class ArrayIndexModel extends Model {

	private static final long serialVersionUID = -6398853184708789620L;

	/**
	 * 特定のアルゴリズムにしたがって、配列のインデックスを返します.
	 *
	 * このメソッドでは、インデックスの遷移を自動で行います。<br>
	 * したがって、次の呼び出しではアルゴリズムに沿った
	 * 「次の値」を返します。<br>
	 *
	 * @param arrayLength 配列の長さ(length)を送信します。<br>
	 *
	 * @return 配列のインデックスを返します。インデックスは0からarrayLength-1である必要があります。<br>
	 */
	public abstract int index(int arrayLength);

	/**
	 * 現在のインデックスを返します.
	 *
	 * このメソッドでは、インデックスを変更せずに、現在値を返します。<br>
	 *
	 * @return インデックスの現在値を返します。<br>
	 */
	public abstract int getIndex();

	@Override
	public ArrayIndexModel clone() {
		return (ArrayIndexModel) super.clone();
	}
}
