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
package kinugasa.util;


/**
 * インデックスの値を選択できるインデックスのカウンタです.
 * <br>
 * このモデルでは、要求されたインデックスが、配列の長さを超える場合は
 * 配列の最後の要素を参照するインデックスを返します。<br>
 * 0未満の場合は0を返します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/02/12_12:43:13<br>
 * @author Shinacho<br>
 * <br>
 *
 */
public class SelectableIndex extends ArrayIndexModel {

	private static final long serialVersionUID = -2030495902937694525L;
	/** インデックスの現在の値です. */
	private int currentIndex;

	/**
	 * インデックスが0のモデルを作成します.
	 */
	public SelectableIndex() {
		this(0);
	}

	/**
	 * 指定されたインデックスのモデルを作成します.
	 *
	 * @param currentIndex インデックスの初期値を指定します。<br>
	 */
	public SelectableIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	@Override
	public int index(int arrayLength) {
		return currentIndex < 0 ? 0 : currentIndex >= arrayLength ? arrayLength - 1 : currentIndex;
	}

	/**
	 * インデックスをインクリメントします.
	 */
	public void next() {
		currentIndex++;
	}

	/**
	 * インデックスをデクリメントします.
	 */
	public void prev() {
		currentIndex--;
	}

	@Override
	public int getIndex() {
		return currentIndex;
	}

	/**
	 * インデックスを設定します.
	 *
	 * @param currentIndex インデックスを指定します。<br>
	 */
	public void setIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	@Override
	public SelectableIndex clone() {
		return (SelectableIndex) super.clone();
	}

	@Override
	public String toString() {
		return "SelectableIndex{" + "currentIndex=" + currentIndex + '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SelectableIndex other = (SelectableIndex) obj;
		if (this.currentIndex != other.currentIndex) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + this.currentIndex;
		return hash;
	}
}
