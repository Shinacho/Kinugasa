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
 * @author Shinacho<br>
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
