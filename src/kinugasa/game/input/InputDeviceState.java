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
package kinugasa.game.input;

import kinugasa.object.Model;


/**
 * 入力デバイスの状態を格納するモデルのスーパークラスです.
 * <br>
 * 入力デバイスの状態は、クローニング可能です。
 * <br>
 * 状態は変更されないように、データをカプセル化する必要があります。<br>
 * <br>
 * @version 1.0.0 - 2013/04/20_21:27:02<br>
 * @author Shinacho<br>
 */
public abstract class InputDeviceState extends Model {

	/**
	 * サブクラスからのみインスタンス化できます.
	 */
	protected InputDeviceState() {
	}

	/**
	 * 検査時点で、何らかの入力があるかを調べます.
	 *
	 * このメソッドによって検査される、デバイスの要素は、実装によって異なります。<br>
	 *
	 * @return 何らかの入力がある場合にtrueを返します。<br>
	 */
	public abstract boolean isAnyInput();

	/**
	 * 検査時点で、何らかのボタンが押されているかを調べます.
	 *
	 * このメソッドによって検査される、デバイスの要素は、実装によって異なります。<br>
	 *
	 * @return 何らかのボタンが押されている場合はtrueを返します。<br>
	 */
	public abstract boolean isAnyButtonInput();

	/**
	 * 検査時点で、何も入力されていないかを調べます.
	 *
	 * このメソッドによって検査される、デバイスの要素は、実装によって異なります。<br>
	 *
	 * @return 何も入力されていない場合にtrueを返します。<br>
	 */
	public abstract boolean isEmptyInput();

	@Override
	public InputDeviceState clone() {
		return (InputDeviceState) super.clone();
	}
}
