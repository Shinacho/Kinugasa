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

/**
 * �L�[�{�[�h�̃L�[���w�肷�邽�߂̗񋓂ł�.
 * <br>
 * Keys�͑Ή�����L�[��AWT�L�[�R�[�h�������܂��B
 * AWT�L�[�R�[�h�ɂ��ẮAjava.awt.event.KeyEvent�N���X�̃��t�@�����X���Q�Ƃ��Ă��������B<br>
 * <br>
 *
 * @see java.awt.event.KeyEvent
 *
 * @version 1.0.0 - 2013/01/14_13:04:22<br>
 * @version 1.1.0 - 2015/06/19_22:49<br>
 * @author Dra0211<br>
 */
public enum Keys {

	/**
	 * ���L�[�̏�ł�.
	 */
	UP(java.awt.event.KeyEvent.VK_UP),
	/**
	 * ���L�[�̉��ł�.
	 */
	DOWN(java.awt.event.KeyEvent.VK_DOWN),
	/**
	 * ���L�[�̍��ł�.
	 */
	LEFT(java.awt.event.KeyEvent.VK_LEFT),
	/**
	 * ���L�[�̉E�ł�.
	 */
	RIGHT(java.awt.event.KeyEvent.VK_RIGHT),
	/**
	 * W�L�[�Ɠ����ł�.
	 */
	WASD_UP(java.awt.event.KeyEvent.VK_W),
	/**
	 * S�L�[�Ɠ����ł�.
	 */
	WASD_DOWN(java.awt.event.KeyEvent.VK_S),
	/**
	 * A�L�[�Ɠ����ł�.
	 */
	WASD_LEFT(java.awt.event.KeyEvent.VK_A),
	/**
	 * D�L�[�Ɠ����ł�.
	 */
	WASD_RIGHT(java.awt.event.KeyEvent.VK_D),
	/**
	 * NUMPAD��8�Ɠ����ł�.
	 */
	PAD_UP(java.awt.event.KeyEvent.VK_NUMPAD8),
	/**
	 * NUMPAD��2�Ɠ����ł�.
	 */
	PAD_DOWN(java.awt.event.KeyEvent.VK_NUMPAD2),
	/**
	 * NUMPAD��4�Ɠ����ł�.
	 */
	PAD_LEFT(java.awt.event.KeyEvent.VK_NUMPAD4),
	/**
	 * NUMPAD��6�Ɠ����ł�.
	 */
	PAD_RIGHT(java.awt.event.KeyEvent.VK_NUMPAD6),
	/**
	 * NUMPAD��7�Ɠ����ł�.
	 */
	PAD_LEFTUP(java.awt.event.KeyEvent.VK_NUMPAD7),
	/**
	 * NUMPAD��9�Ɠ����ł�.
	 */
	PAD_RIGHTUP(java.awt.event.KeyEvent.VK_NUMPAD9),
	/**
	 * NUMPAD��1�Ɠ����ł�.
	 */
	PAD_LEFTDOWN(java.awt.event.KeyEvent.VK_NUMPAD1),
	/**
	 * NUMPAD��3�Ɠ����ł�.
	 */
	PAD_RIGHTDOWN(java.awt.event.KeyEvent.VK_NUMPAD3),
	/**
	 * NUMPAD��5�Ɠ����ł�.
	 */
	PAD_CENTER(java.awt.event.KeyEvent.VK_NUMPAD5),
	/**
	 * �X�y�[�X�L�[�ł�.
	 */
	SPACE(java.awt.event.KeyEvent.VK_SPACE),
	/**
	 * CTRL�L�[�ł�.
	 */
	CTRL(java.awt.event.KeyEvent.VK_CONTROL),
	/**
	 * �V�t�g�L�[�ł�.
	 */
	SHIFT(java.awt.event.KeyEvent.VK_SHIFT),
	/**
	 * �G���^�[�L�[�ł�.
	 */
	ENTER(java.awt.event.KeyEvent.VK_ENTER),
	/**
	 * �G�X�P�[�v�L�[�ł�.
	 */
	ESCAPE(java.awt.event.KeyEvent.VK_ESCAPE),
	/**
	 * �o�b�N�X�y�[�X�L�[�ł�.
	 */
	BACK_SPACE(java.awt.event.KeyEvent.VK_BACK_SPACE),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	A(java.awt.event.KeyEvent.VK_A),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	B(java.awt.event.KeyEvent.VK_B),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	C(java.awt.event.KeyEvent.VK_C),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	D(java.awt.event.KeyEvent.VK_D),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	E(java.awt.event.KeyEvent.VK_E),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	F(java.awt.event.KeyEvent.VK_F),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	G(java.awt.event.KeyEvent.VK_G),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	H(java.awt.event.KeyEvent.VK_H),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	I(java.awt.event.KeyEvent.VK_I),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	J(java.awt.event.KeyEvent.VK_J),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	K(java.awt.event.KeyEvent.VK_K),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	L(java.awt.event.KeyEvent.VK_L),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	M(java.awt.event.KeyEvent.VK_M),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	N(java.awt.event.KeyEvent.VK_N),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	O(java.awt.event.KeyEvent.VK_O),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	P(java.awt.event.KeyEvent.VK_P),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	Q(java.awt.event.KeyEvent.VK_Q),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	R(java.awt.event.KeyEvent.VK_R),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	S(java.awt.event.KeyEvent.VK_S),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	T(java.awt.event.KeyEvent.VK_T),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	U(java.awt.event.KeyEvent.VK_U),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	V(java.awt.event.KeyEvent.VK_V),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	W(java.awt.event.KeyEvent.VK_W),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	X(java.awt.event.KeyEvent.VK_X),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	Y(java.awt.event.KeyEvent.VK_Y),
	/**
	 * �Ή�����L�[��\���܂�.
	 */
	Z(java.awt.event.KeyEvent.VK_Z),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_1(java.awt.event.KeyEvent.VK_NUMPAD1),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_2(java.awt.event.KeyEvent.VK_NUMPAD2),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_3(java.awt.event.KeyEvent.VK_NUMPAD3),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_4(java.awt.event.KeyEvent.VK_NUMPAD4),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_5(java.awt.event.KeyEvent.VK_NUMPAD6),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_6(java.awt.event.KeyEvent.VK_NUMPAD6),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_7(java.awt.event.KeyEvent.VK_NUMPAD7),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_8(java.awt.event.KeyEvent.VK_NUMPAD8),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_9(java.awt.event.KeyEvent.VK_NUMPAD9),
	/**
	 * NUM_PAD�̐����L�[�ł�.
	 */
	PAD_0(java.awt.event.KeyEvent.VK_NUMPAD0),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_1(java.awt.event.KeyEvent.VK_1),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_2(java.awt.event.KeyEvent.VK_2),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_3(java.awt.event.KeyEvent.VK_3),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_4(java.awt.event.KeyEvent.VK_4),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_5(java.awt.event.KeyEvent.VK_5),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_6(java.awt.event.KeyEvent.VK_6),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_7(java.awt.event.KeyEvent.VK_7),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_8(java.awt.event.KeyEvent.VK_8),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_9(java.awt.event.KeyEvent.VK_9),
	/**
	 * NUM_PAD�łȂ��i���o�[�L�[�ł�.
	 */
	NUM_0(java.awt.event.KeyEvent.VK_0),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F1(java.awt.event.KeyEvent.VK_F1),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F2(java.awt.event.KeyEvent.VK_F2),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F3(java.awt.event.KeyEvent.VK_F3),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F4(java.awt.event.KeyEvent.VK_F4),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F5(java.awt.event.KeyEvent.VK_F5),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F6(java.awt.event.KeyEvent.VK_F6),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F7(java.awt.event.KeyEvent.VK_F7),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F8(java.awt.event.KeyEvent.VK_F8),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F9(java.awt.event.KeyEvent.VK_F9),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F10(java.awt.event.KeyEvent.VK_F10),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F11(java.awt.event.KeyEvent.VK_F11),
	/**
	 * �����̃L�[�̓t�@���N�V�����L�[���`���܂�.
	 */
	F12(java.awt.event.KeyEvent.VK_F12),
	/**
	 * INSERT�L�[�ł�.
	 */
	INSERT(java.awt.event.KeyEvent.VK_INSERT),
	/**
	 * DELETE�L�[�ł�.
	 */
	DELETE(java.awt.event.KeyEvent.VK_DELETE),
	/**
	 * HOME�L�[�ł�.
	 */
	HOME(java.awt.event.KeyEvent.VK_HOME),
	/**
	 * END�L�[�ł�.
	 */
	END(java.awt.event.KeyEvent.VK_END),
	/**
	 * PAGEUP�L�[�ł�.
	 */
	PAGE_UP(java.awt.event.KeyEvent.VK_PAGE_UP),
	/**
	 * PAGEDOWN�L�[�ł�.
	 */
	PAGE_DOWN(java.awt.event.KeyEvent.VK_PAGE_DOWN),
	/**
	 * @�ł��B
	 */
	AT(java.awt.event.KeyEvent.VK_AT),;
	/**
	 * �L�[��AWT�L�[�ԍ�.
	 */
	private final int keyCode;

	/**
	 * AWT�̃o�[�`�����L�[�ԍ����擾���܂�.
	 *
	 * @return �Ή�����AWT�L�[�R�[�h��Ԃ��܂�.<br>
	 */
	public int getKeyCode() {
		return keyCode;
	}

	private Keys(int keyCode) {
		this.keyCode = keyCode;
	}
}
