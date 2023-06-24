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

import java.util.BitSet;

/**
 * キー入力の状態を格納します.
 * <br>
 * キーの状態はBitSetクラスを使用して格納されます。<br>
 * あるキーの状態にアクセスするためのインデックスはAWTキーコードまたは それをラップするKeys列挙を使用します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_13:16:01<br>
 * @author Shinacho<br>
 */
public class KeyState extends InputDeviceState {

	/**
	 * キーの状態を格納する配列.
	 */
	private BitSet keySet;

	/**
	 * パッケージアクセス.
	 *
	 * @param state キーの状態.ディープコピーを作成する.<br>
	 */
	KeyState(BitSet state) {
		this.keySet = (BitSet) state.clone();
	}

	/**
	 * キーの状態を取得します. 1回だけの判定は「前回の検査状態」を保持しておくことで実現可能です.<br>
	 *
	 * @param k キー.<br>
	 *
	 * @return 押されているときTRUEを返す.<br>
	 */
	public boolean isPressed(Keys k) {
		return keySet.get(k.getKeyCode());
	}

	/**
	 * キーの状態を取得します. このメソッドは、isPressed(Keys)よりも高速に動作することがあります.<br>
	 *
	 * @param index キーのインデックス。KeyEventクラスの定数を使用してください。<br>
	 *
	 * @return 押されているときにTRUEを返す.<br>
	 */
	public boolean isPressed(int index) {
		return keySet.size() <= index ? false : keySet.get(index);
	}

	@Override
	public String toString() {
		return "KeyState{" + "keySet=" + keySet + '}';
	}

	@Override
	public KeyState clone() {
		KeyState result = (KeyState) super.clone();
		result.keySet = (BitSet) this.keySet.clone();
		return result;
	}

	@Override
	public boolean isAnyInput() {
		return !keySet.isEmpty();
	}

	@Override
	public boolean isAnyButtonInput() {
		return !keySet.isEmpty();
	}

	@Override
	public boolean isEmptyInput() {
		return keySet.isEmpty();
	}

}
