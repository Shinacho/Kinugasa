/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;

/**
 * �L�[�{�[�h�̓��͏�Ԃ��������邽�߂�Ke��Listener�̎����ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:49:55<br>
 * @author Shinacho<br>
 */
public class KeyConnection implements KeyListener {

	/**
	 * �L�[��Ԃ��i�[�����r�b�g�Z�b�g�ł�.
	 */
	private static final BitSet keySet = new BitSet(512);
	/**
	 * �B��̃C���X�^���X�ł�.
	 */
	private static final KeyConnection INSTANCE = new KeyConnection();
	/**
	 * �������͂���Ă��Ȃ���Ԃ�KeyState�ł�.
	 */
	private static final KeyState CLEAR_STATE = new KeyState(new BitSet(512));

	/**
	 * �V���O���g���N���X�ł�.�C���X�^���X���ł��܂���.<br>
	 */
	private KeyConnection() {
	}

	static KeyConnection getInstance() {
		return INSTANCE;
	}

	/**
	 * �R���|�[�l���g�ɂ��̃��X�i��ǉ����܂�.
	 *
	 * @param component ���X�i��o�^����R���|�[�l���g�𑗐M���܂��B<br>
	 */
	public static void setListener(Component component) {
		component.addKeyListener(INSTANCE);
	}

	/**
	 * ��̃L�[�X�e�[�g�𔭍s���܂�. ���̃��\�b�h�͏�ɓ����C���X�^���X��Ԃ��܂��B<br>
	 *
	 * @return ��̃L�[�X�e�[�g��Ԃ��܂��B����������Ă��Ȃ���Ԃł��B<br>
	 */
	public static KeyState createClearState() {
		return CLEAR_STATE;
	}

	/**
	 * ���݂̓��͏�Ԃ��擾���܂�.
	 *
	 * @return ���݂̃L�[���͏�Ԃł��B<br>
	 */
	public static KeyState getState() {
		return new KeyState(KeyConnection.keySet);
	}

	/**
	 * �L�[���X�i�̎����ł�.
	 *
	 * @param ke �L�[�C�x���g�����M����܂��B<br>
	 */
	@Override
	public void keyTyped(KeyEvent ke) {
	}

	/**
	 * �L�[���X�i�̎����ł�.
	 *
	 * @param ke �L�[�C�x���g�����M����܂��B<br>
	 */
	@Override
	public void keyPressed(KeyEvent ke) {
		keySet.set(ke.getKeyCode());
	}

	/**
	 * �L�[���X�i�̎����ł�.
	 *
	 * @param ke �L�[�C�x���g�����M����܂��B<br>
	 */
	@Override
	public void keyReleased(KeyEvent ke) {
		keySet.clear(ke.getKeyCode());
	}
}
