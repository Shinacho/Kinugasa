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
package kinugasa.game;

import java.awt.Toolkit;

/**
 * �v���C���[�̎��s���̒萔�t�B�[���h�ɃA�N�Z�X���邽�߂̃N���X�ł�.
 * <br>
 * ���̃N���X�̏��́ASystem.getProperty�����Toolkit����擾����܂��B<br>
 * �܂��A���̃N���X�́A�v���C���[�̓��̓f�o�C�X�̎g�p�󋵂��i�[����B��̏ꏊ�Ƃ��Ȃ�܂��B ���̒l�̓Q�[���̊J�n���̂ݎw��ł��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/02/11_0:12:16<br>
 * @author Dra0211<br>
 */
public final class PlayerConstants {

	private PlayerConstants() {
		SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
		OS_NAME = System.getProperty("os.name");
		HOME_PATH = System.getProperty("user.home");
		USER_NAME = System.getProperty("user.name");
		DESKTOP_PATH = HOME_PATH + "/Desktop/";
		USER_COUNTRY = System.getProperty("user.country");
	}
	/**
	 * �B��̃C���X�^���X�ł�.
	 */
	private static final PlayerConstants INSTANCE = new PlayerConstants();

	/**
	 * �C���X�^���X���擾���܂�.
	 *
	 * @return �B��̃C���X�^���X���擾���܂�.<br>
	 */
	public static PlayerConstants getInstance() {
		return INSTANCE;
	}
	/**
	 * �v���C���[���g�p���Ă����ʂ̕��ł�.
	 */
	public final int SCREEN_WIDTH;
	/**
	 * �v���C���[���g�p���Ă����ʂ̍����ł�.
	 */
	public final int SCREEN_HEIGHT;
	/**
	 * OS�̖��O�ł�.
	 */
	public final String OS_NAME;
	/**
	 * �v���C���[�̃z�[���f�B���N�g���ł�.
	 */
	public final String HOME_PATH;
	/**
	 * �v���C���[��OS�A�J�E���g���ł�.
	 */
	public final String USER_NAME;
	/**
	 * �v���C���[�̃f�X�N�g�b�v�̃p�X�ł�.
	 */
	public final String DESKTOP_PATH;
	/**
	 * �v���C���[�̍��Ђł�.
	 */
	public final String USER_COUNTRY;
	/**
	 * �}�E�X�̎g�p�󋵂ł�.
	 */
	private boolean usingMouse;
	/**
	 * �L�[�{�[�h�̎g�p�󋵂ł�.
	 */
	private boolean usingKeyboard;

	private boolean usingGamePad;

	/**
	 * �Q�[���J�n���̐ݒ�ɂ���ăL�[�{�[�h�� �g�p�ł����Ԃł��邩�𒲂ׂ܂�.
	 *
	 * @return �L�[�{�[�h���g�p����ݒ�ŃQ�[�����J�n�����ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean isUsingKeyboard() {
		return usingKeyboard;
	}

	/**
	 * �Q�[���J�n���̐ݒ�ɂ���ă}�E�X�� �g�p�ł����Ԃł��邩�𒲂ׂ܂�.
	 *
	 * @return �}�E�X���g�p����ݒ�ŃQ�[�����J�n�����ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean isUsingMouse() {
		return usingMouse;
	}

	/**
	 * �L�[�{�[�h�̎g�p�ۂ�ݒ肵�܂�.
	 *
	 * @param usingKeyboard �g�p�\�ȏꍇ��true�𑗐M���܂��B<br>
	 */
	void setUsingKeyboard(boolean usingKeyboard) {
		this.usingKeyboard = usingKeyboard;
	}

	/**
	 * �}�E�X�̎g�p�ۂ�ݒ肵�܂�.
	 *
	 * @param usingMouse �g�p�\�ȏꍇ��true�𑗐M���܂��B<br>
	 */
	void setUsingMouse(boolean usingMouse) {
		this.usingMouse = usingMouse;
	}

	void setUsingGamePad(boolean usingGamePad) {
		this.usingGamePad = usingGamePad;
	}

	public boolean isUsingGamePad() {
		return usingGamePad;
	}

}
