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

import kinugasa.object.Model;

/**
 * 配列のインデックスの遷移順序を定義します.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_15:42:02<br>
 * @author Shinacho<br>
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
