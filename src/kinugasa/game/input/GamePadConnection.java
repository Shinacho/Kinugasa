/*
 * The MIT License
 *
 * Copyright 2014 Shinacho.
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
 * .
 * <br>
 *
 * @version 1.0.0 - 2014/09/20<br>
 * @author Shinacho<br>
 * <br>
 */
public final class GamePadConnection{

	private GamePadConnection() {
	}

	public static void init() throws RuntimeException {
		if(load)throw new RuntimeException("dll is already loaded.");
		try {
			System.loadLibrary("KGP");
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		load= true;
	}
	private static boolean load = false;

	public static native float[] getNativeState(int playerIndex);

	public static native void free();

	public static final int LENGTH = 21;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_A = 0;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_B = 1;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_X = 2;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_Y = 3;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_LB = 4;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_RB = 5;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_LEFT_STICK = 6;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_RIGHT_STICK = 7;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_POV_UP = 8;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_POV_DOWN = 9;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_POV_LEFT = 10;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_POV_RIGHT = 11;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_START = 12;
	/**
	 * �����̒l�̓Q�[���p�b�h�̃f�W�^���{�^�������ʂ��܂�.
	 */
	public static final int BUTTON_BACK = 13;
	/**
	 * �Q�[���p�b�h�̃A�i���O�g���K�[�����ʂ��܂�.
	 */
	public static final int TRIGGER_LEFT = 14;
	/**
	 * �Q�[���p�b�h�̃A�i���O�g���K�[�����ʂ��܂�.
	 */
	public static final int TRIGGER_RIGHT = 15;
	/**
	 * �Q�[���p�b�h�̃A�i���O�X�e�B�b�N�����ʂ��܂�.
	 */
	public static final int THUMB_STICK_LEFT_X = 16;
	/**
	 * �Q�[���p�b�h�̃A�i���O�X�e�B�b�N�����ʂ��܂�.
	 */
	public static final int THUMB_STICK_LEFT_Y = 17;
	/**
	 * �Q�[���p�b�h�̃A�i���O�X�e�B�b�N�����ʂ��܂�.
	 */
	public static final int THUMB_STICK_RIGHT_X = 18;
	/**
	 * �Q�[���p�b�h�̃A�i���O�X�e�B�b�N�����ʂ��܂�.
	 */
	public static final int THUMB_STICK_RIGHT_Y = 19;
	/**
	 * �Q�[���p�b�h�̎��ʏ�Ԃ��i�[����Ă���ꏊ�̃C���f�b�N�X�ł�.
	 */
	public static final int CONNECTION = 20;
	//
	/**
	 * dll�ɂ���Ē�`�����A�l�C�e�B�u�ȁuFALSE�v�̏�Ԃł�.
	 */
	public static final int NATIVE_FALSE = 0;
	/**
	 * dll�ɂ���Ďg�p�����A�g���K�[�̓��͂̍ŏ��l�ł�.
	 */
	public static final int TRIGGER_MIN = 0;
	/**
	 * dll�ɂ���Ďg�p�����A�g���K�[�̓��͂̍ő�l�ł�.
	 */
	public static final int TRIGGER_MAX = 255;
	/**
	 * dll�ɂ���Ďg�p�����A�X�e�B�b�N�̓��͂̍ŏ��l�ł�.
	 */
	public static final int THUMBSTICK_MIN = Short.MIN_VALUE;
	/**
	 * dll�ɂ���Ďg�p�����A�g���K�[�̓��͂̃f�t�H���g�l�ł�.
	 */
	public static final int THUMBSTICK_CENTER = 0;
	/**
	 * dll�ɂ���Ďg�p�����A�X�e�B�b�N�̓��͂̍ő�l�ł�.
	 */
	public static final int THUMBSTICK_MAX = Short.MAX_VALUE;
	/**
	 * dll�ɂ���Ďg�p�����A�g���K�[�̓��͂̍ŏ��l����̍ő�l�܂ł̐�Βl�ł�.
	 */
	public static final int THUMSTICK_ABS_MAX = 65534;
}
