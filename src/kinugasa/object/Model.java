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
