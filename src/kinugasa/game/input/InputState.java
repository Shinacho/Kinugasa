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

import kinugasa.game.PlayerConstants;
import kinugasa.object.FourDirection;

/**
 * 入力デバイスの状態を格納します.
 * <br>
 * このクラスは、プレイヤーが使用できるデバイスの状態を一元管理します。<br>
 * このクラスを使用することで、入力状態を簡単に検査できます。<br>
 * <br>
 * 入力状態の更新には、それなりの時間的コストがかかります。<br>
 * また、getInstanceが実行されたときに、入力状態がリフレッシュされます。
 * getInstanceの使用は、1ループ中1回にとどめてください。複数回実行すると、「前回の入力状態」が不正になり、正しく判定できません。<br>
 * <br>
 * <br>
 * ゲーム起動時の設定によって、使用しないデバイスがある場合、
 * そのデバイスの状態は常にnullです。したがって、例えばゲームパッドが接続されていない状態でゲームパッドの検査を呼び出すとNPEになります。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:55:04<br>
 * @author Dra0211<br>
 */
public final class InputState extends InputDeviceState {

	private static final long serialVersionUID = 2349722781837661059L;
	/**
	 * 現在のキーボードの状態.
	 */
	private KeyState keyState = null;
	/**
	 * 前回のキーボードの状態.
	 */
	private KeyState prevKeyState = null;
	/**
	 * 現在のマウスの状態.
	 */
	private MouseState mouseState = null;
	/**
	 * 前回のマウスの状態.
	 */
	private MouseState prevMouseState = null;
	//
	/**
	 * 唯一のインスタンスです.
	 */
	private static final InputState INSTANCE = new InputState();

	private GamePadState gpState;
	private GamePadState prevGpState;

	/**
	 * シングルトンクラスです. getInstanceを使用してください。<br>
	 */
	private InputState() {
		if (PlayerConstants.getInstance().isUsingKeyboard()) {
			keyState = KeyConnection.createClearState();
		}
		if (PlayerConstants.getInstance().isUsingMouse()) {
			mouseState = MouseConnection.createClearState();
		}
	}

	/**
	 * インスタンスを取得します. このメソッドを呼び出すと、現在の入力状態が検査されます。<br>
	 *
	 * @return InputStateのインスタンスを返します。<br>
	 */
	@Deprecated
	public static InputState getInstance() {
		INSTANCE.refresh();
		return INSTANCE;
	}

	/**
	 * 入力状態を更新します.
	 */
	private void refresh() {
		if (PlayerConstants.getInstance().isUsingMouse()) {
			prevMouseState = mouseState;
			mouseState = MouseConnection.getState();
		}
		if (PlayerConstants.getInstance().isUsingKeyboard()) {
			prevKeyState = keyState;
			keyState = KeyConnection.getState();
		}
		if (PlayerConstants.getInstance().isUsingGamePad()) {
			prevGpState = gpState;
			gpState = new GamePadState(GamePadConnection.getNativeState(0));
		}

	}

	@Override
	public boolean isAnyInput() {
		boolean result = false;
		if (PlayerConstants.getInstance().isUsingMouse()) {
			result |= mouseState.isAnyInput();
		}
		if (PlayerConstants.getInstance().isUsingGamePad()) {
			result |= gpState.isAnyInput();
		}
		return result | keyState.isAnyInput();
	}

	@Override
	public boolean isAnyButtonInput() {
		boolean result = false;
		if (PlayerConstants.getInstance().isUsingMouse()) {
			result |= mouseState.isAnyButtonInput();
		}
		if (PlayerConstants.getInstance().isUsingGamePad()) {
			result |= gpState.isAnyInput();
		}
		return result | keyState.isAnyButtonInput();
	}

	@Override
	public boolean isEmptyInput() {
		boolean result = true;
		if (PlayerConstants.getInstance().isUsingMouse()) {
			result &= mouseState.isEmptyInput();
		}
		if (PlayerConstants.getInstance().isUsingGamePad()) {
			result &= gpState.isEmptyInput();
		}
		return result & keyState.isEmptyInput();
	}

	/**
	 * キーボードのキーが押されているかを検査します.
	 *
	 * @param key 判定するキーを指定します。<br>
	 * @param type 前回の入力状態を検査するかを決定します。<br>
	 * @return 指定されたキーが現在押されている場合、trueを返します。<br>
	 */
	public boolean isPressed(Keys key, InputType type) {
		return type == InputType.CONTINUE
				? keyState.isPressed(key)
				: keyState.isPressed(key) && !prevKeyState.isPressed(key);
	}

	/**
	 * ゲームパッドまたはキーボードの入力があるか検査します。
	 * このメソッドは、ゲームパッドがない環境でもゲームパッドが接続されているかの検査を省略することができます。
	 *
	 * @param b ゲームパッドのボタン。
	 * @param k キーボードのキー。
	 * @param t インプットタイプ。
	 * @return bまたはkが押されている場合TRUEを返します。
	 */
	public boolean isPressed(GamePadButton b, Keys k, InputType t) {
		if (gpState == null) {
			return isPressed(k, t);
		}
		return isPressed(k, t) || isPressed(b, t);
	}

	public boolean isPressedOr(InputType type, Keys... keys) {
		boolean result = false;
		for (Keys key : keys) {
			result |= isPressed(key, type);
		}
		return result;
	}

	public boolean isPressedAnd(InputType type, Keys... keys) {
		for (Keys key : keys) {
			if (!isPressed(key, type)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * マウスのボタンが押されているかを検査します.
	 *
	 * @param button 判定するボタンを指定します。<br>
	 * @param type 前回の入力状態を検査するかを決定します。<br>
	 * @return 指定されたボタンが現在押されている場合、trueを返します。<br>
	 */
	public boolean isPressed(MouseButtons button, InputType type) {
		if (!PlayerConstants.getInstance().isUsingMouse()) {
			return false;
		}
		return type == InputType.CONTINUE
				? mouseState.isPressed(button)
				: mouseState.isPressed(button) && !prevMouseState.isPressed(button);
	}

	/**
	 * 現在のキーボードの状態を取得します. キーボードが使用できない場合、nullを返します。<br>
	 *
	 * @return キーボードの状態を返します。<br>
	 */
	public KeyState getKeyState() {
		return keyState;
	}

	/**
	 * 前回のキーボードの状態を取得します. キーボードが使用できない場合、nullを返します。<br>
	 *
	 * @return キーボードの状態を返します。<br>
	 */
	public KeyState getPrevKeyState() {
		return prevKeyState;
	}

	/**
	 * 現在のマウスの状態を取得します. マウスが使用できない場合、nullを返します。<br>
	 *
	 * @return マウスの状態を返します。<br>
	 */
	public MouseState getPrevMouseState() {
		return prevMouseState;

	}

	/**
	 * 前回のマウスの状態を取得します. マウスが使用できない場合、nullを返します。<br>
	 *
	 * @return マウスの状態を返します。<br>
	 */
	public MouseState getMouseState() {
		return mouseState;
	}

	public GamePadState getGamePadState() {
		return gpState;
	}

	public boolean isPressed(GamePadButton button, InputType type) {
		if (!PlayerConstants.getInstance().isUsingGamePad()) {
			return false;
		}
		return type == InputType.CONTINUE
				? gpState.buttons.isPressed(button)
				: gpState.buttons.isPressed(button) && !prevGpState.buttons.isPressed(button);
	}

	public boolean gamePadStickLeftIs(FourDirection dir, InputType type) {
		if (!PlayerConstants.getInstance().isUsingMouse()) {
			return false;
		}
		return type == InputType.CONTINUE
				? gpState.sticks.LEFT.is(dir)
				: gpState.sticks.LEFT.is(dir) && !prevGpState.sticks.LEFT.is(dir);
	}

	public boolean gamePadStickRightIs(FourDirection dir, InputType type) {
		if (!PlayerConstants.getInstance().isUsingMouse()) {
			return false;
		}
		return type == InputType.CONTINUE
				? gpState.sticks.RIGHT.is(dir)
				: gpState.sticks.RIGHT.is(dir) && !prevGpState.sticks.LEFT.is(dir);
	}

	@Override
	public InputState clone() {
		InputState result = (InputState) super.clone();

		if (this.mouseState != null) {
			result.mouseState = this.mouseState.clone();
		}
		if (this.prevMouseState != null) {
			result.prevMouseState = this.prevMouseState.clone();
		}
		if (this.keyState != null) {
			result.keyState = this.keyState.clone();
		}
		if (this.prevKeyState != null) {
			result.prevKeyState = this.prevKeyState.clone();
		}
		if (this.gpState != null) {
			result.gpState = this.gpState.clone();
		}
		if (this.prevGpState != null) {
			result.prevGpState = this.prevGpState.clone();
		}

		return result;
	}

	@Override
	public String toString() {
		return "InputState{" + "keyState=" + keyState + ", prevKeyState=" + prevKeyState + ", mouseState=" + mouseState + ", prevMouseState=" + prevMouseState + '}';
	}

}
