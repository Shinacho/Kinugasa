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
 * @author Dra0211<br>
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
