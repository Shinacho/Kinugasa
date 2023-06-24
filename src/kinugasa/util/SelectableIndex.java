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
