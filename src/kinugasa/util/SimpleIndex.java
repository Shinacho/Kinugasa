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


/**
 * 配列の要素に、順番にアクセスする基本的なインデックスのカウンタです.
 * <br>
 * indexメソッドが呼び出されるたびに、インデックスの値がカウントされます。
 * インデックスの値が最大値に達したとき（配列の要素数-1）次の呼び出しでは
 * インデックスの値が0に戻されます。<br>
 * <br>
 * この実装は、内部でカウンタの値を保持するため、1つの配列要素に対して、1つの
 * インデックスモデルが必要です。<br>
 *
 * @version 1.0.0 - 2013/01/12_17:12:36<br>
 * @author Dra0211<br>
 */
public class SimpleIndex extends ArrayIndexModel {

	private static final long serialVersionUID = -5792984578642711312L;
	/** 現在のインデックスです. */
	private int index;

	/**
	 * 0からスタートし、右方向へループするインデックスモデルを作成します.
	 */
	public SimpleIndex() {
		this(0);
	}

	/**
	 * 指定されたインデックスからスタートし、右方向へループするインデックスモデルを作成します.
	 *
	 * @param index インデックスの初期値を指定します。<br>
	 */
	public SimpleIndex(int index) {
		this.index = index;
	}

	@Override
	public int index(int arrayLength) {
		index++;
		if (index >= arrayLength) {
			index = 0;
		}
		return index;
	}

	/**
	 * インデックスの現在値を設定します.
	 *
	 * @param index カウンタの値を指定します。<br>
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public ArrayIndexModel clone() {
		return super.clone();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + this.index;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SimpleIndex other = (SimpleIndex) obj;
		if (this.index != other.index) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SimpleIndex{" + "index=" + index + '}';
	}
}