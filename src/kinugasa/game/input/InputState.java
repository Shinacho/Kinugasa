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
 * ���̓f�o�C�X�̏�Ԃ��i�[���܂�.
 * <br>
 * ���̃N���X�́A�v���C���[���g�p�ł���f�o�C�X�̏�Ԃ��ꌳ�Ǘ����܂��B<br>
 * ���̃N���X���g�p���邱�ƂŁA���͏�Ԃ��ȒP�Ɍ����ł��܂��B<br>
 * <br>
 * ���͏�Ԃ̍X�V�ɂ́A����Ȃ�̎��ԓI�R�X�g��������܂��B<br>
 * �܂��AgetInstance�����s���ꂽ�Ƃ��ɁA���͏�Ԃ����t���b�V������܂��B
 * getInstance�̎g�p�́A1���[�v��1��ɂƂǂ߂Ă��������B��������s����ƁA�u�O��̓��͏�ԁv���s���ɂȂ�A����������ł��܂���B<br>
 * <br>
 * <br>
 * �Q�[���N�����̐ݒ�ɂ���āA�g�p���Ȃ��f�o�C�X������ꍇ�A
 * ���̃f�o�C�X�̏�Ԃ͏��null�ł��B���������āA�Ⴆ�΃Q�[���p�b�h���ڑ�����Ă��Ȃ���ԂŃQ�[���p�b�h�̌������Ăяo����NPE�ɂȂ�܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:55:04<br>
 * @author Dra0211<br>
 */
public final class InputState extends InputDeviceState {

	private static final long serialVersionUID = 2349722781837661059L;
	/**
	 * ���݂̃L�[�{�[�h�̏��.
	 */
	private KeyState keyState = null;
	/**
	 * �O��̃L�[�{�[�h�̏��.
	 */
	private KeyState prevKeyState = null;
	/**
	 * ���݂̃}�E�X�̏��.
	 */
	private MouseState mouseState = null;
	/**
	 * �O��̃}�E�X�̏��.
	 */
	private MouseState prevMouseState = null;
	//
	/**
	 * �B��̃C���X�^���X�ł�.
	 */
	private static final InputState INSTANCE = new InputState();

	private GamePadState gpState;
	private GamePadState prevGpState;

	/**
	 * �V���O���g���N���X�ł�. getInstance���g�p���Ă��������B<br>
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
	 * �C���X�^���X���擾���܂�. ���̃��\�b�h���Ăяo���ƁA���݂̓��͏�Ԃ���������܂��B<br>
	 *
	 * @return InputState�̃C���X�^���X��Ԃ��܂��B<br>
	 */
	@Deprecated
	public static InputState getInstance() {
		INSTANCE.refresh();
		return INSTANCE;
	}

	/**
	 * ���͏�Ԃ��X�V���܂�.
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
	 * �L�[�{�[�h�̃L�[��������Ă��邩���������܂�.
	 *
	 * @param key ���肷��L�[���w�肵�܂��B<br>
	 * @param type �O��̓��͏�Ԃ��������邩�����肵�܂��B<br>
	 * @return �w�肳�ꂽ�L�[�����݉�����Ă���ꍇ�Atrue��Ԃ��܂��B<br>
	 */
	public boolean isPressed(Keys key, InputType type) {
		return type == InputType.CONTINUE
				? keyState.isPressed(key)
				: keyState.isPressed(key) && !prevKeyState.isPressed(key);
	}

	/**
	 * �Q�[���p�b�h�܂��̓L�[�{�[�h�̓��͂����邩�������܂��B
	 * ���̃��\�b�h�́A�Q�[���p�b�h���Ȃ����ł��Q�[���p�b�h���ڑ�����Ă��邩�̌������ȗ����邱�Ƃ��ł��܂��B
	 *
	 * @param b �Q�[���p�b�h�̃{�^���B
	 * @param k �L�[�{�[�h�̃L�[�B
	 * @param t �C���v�b�g�^�C�v�B
	 * @return b�܂���k��������Ă���ꍇTRUE��Ԃ��܂��B
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
	 * �}�E�X�̃{�^����������Ă��邩���������܂�.
	 *
	 * @param button ���肷��{�^�����w�肵�܂��B<br>
	 * @param type �O��̓��͏�Ԃ��������邩�����肵�܂��B<br>
	 * @return �w�肳�ꂽ�{�^�������݉�����Ă���ꍇ�Atrue��Ԃ��܂��B<br>
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
	 * ���݂̃L�[�{�[�h�̏�Ԃ��擾���܂�. �L�[�{�[�h���g�p�ł��Ȃ��ꍇ�Anull��Ԃ��܂��B<br>
	 *
	 * @return �L�[�{�[�h�̏�Ԃ�Ԃ��܂��B<br>
	 */
	public KeyState getKeyState() {
		return keyState;
	}

	/**
	 * �O��̃L�[�{�[�h�̏�Ԃ��擾���܂�. �L�[�{�[�h���g�p�ł��Ȃ��ꍇ�Anull��Ԃ��܂��B<br>
	 *
	 * @return �L�[�{�[�h�̏�Ԃ�Ԃ��܂��B<br>
	 */
	public KeyState getPrevKeyState() {
		return prevKeyState;
	}

	/**
	 * ���݂̃}�E�X�̏�Ԃ��擾���܂�. �}�E�X���g�p�ł��Ȃ��ꍇ�Anull��Ԃ��܂��B<br>
	 *
	 * @return �}�E�X�̏�Ԃ�Ԃ��܂��B<br>
	 */
	public MouseState getPrevMouseState() {
		return prevMouseState;

	}

	/**
	 * �O��̃}�E�X�̏�Ԃ��擾���܂�. �}�E�X���g�p�ł��Ȃ��ꍇ�Anull��Ԃ��܂��B<br>
	 *
	 * @return �}�E�X�̏�Ԃ�Ԃ��܂��B<br>
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
