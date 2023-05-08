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
package kinugasa.util;

import kinugasa.game.system.GameSystem;

/**
 * ���w���[�e�B���e�B�ł�.
 * <br>
 * <br>
 * �O�p�֐��̒l��0.5�x���݂Ōv�Z����܂��B���̒l�� �ŏ��̌Ăяo�����ɃL���b�V������܂��B<br>
 * �����W�F�l���[�^��kinugasa.util.Random�N���X���g�p���Ă��������B<br>
 * <br>
 *
 * @version 3.0.0 - 2011/09/16_20:58<br>
 * @version 3.1.2 - 2011/11/03_17:04<br>
 * @version 4.0.0 - 2013/1/03_06:18<br>
 * @version 5.0.0 - 2013/01/11_17:25:19<br>
 * @version 5.1.0 - 2013/02/07_09:17<br>
 * @author Shinacho<br>
 */
public final class MathUtil {

	/**
	 * �C���X�^���X���ł��܂���.<br>
	 */
	private MathUtil() {
	}
	/**
	 * sin�̒l���L���b�V�������z��ł�.
	 */
	private static final float[] sinTable = new float[720];
	/**
	 * cos�̒l���L���b�V�������z��ł�.
	 */
	private static final float[] cosTable = new float[720];

	public static void init() {
		if (GameSystem.isDebugMode()) {
			System.out.println("kinugasa.util.MathUtil.init()");
		}
		for (int i = 0; i < sinTable.length; i++) {
			sinTable[i] = (float) Math.sin(Math.toRadians((float) i / 2f));
		}
		for (int i = 0; i < cosTable.length; i++) {
			cosTable[i] = (float) Math.cos(Math.toRadians((float) i / 2f));
		}
	}

	/**
	 * �T�C���g���i�[�����P���x�z����쐬���܂�.
	 *
	 * @param fq �T�C���g�̎��g��.<br>
	 * @param mag �g��{��.<br>
	 * @param max �Ƃ̍ő�l.<br>
	 *
	 * @return �w�肵���̈�̃T�C���g���\�z�ł���l���i�[���ꂽ�z��.<br>
	 */
	public static float[] createSinWaveArray(float fq, float mag, float max) {
		float[] reAry = new float[(int) (max / fq)];
		for (int i = 0; i < reAry.length; i++) {
			reAry[i] = (float) Math.sin(Math.toRadians(i * fq)) * mag;
		}
		return reAry;
	}

	/**
	 * �T�C���g���i�[�����P���x�z����쐬���܂�.
	 *
	 * @param fq �T�C���g�̎��g��.<br>
	 * @param mag �g��{��.<br>
	 * @param max �Ƃ̍ő�l.<br>
	 * @param reverse true���w�肷��Ɣg�`���t���Ƀ\�[�g���܂�.<br>
	 *
	 * @return �w�肵���̈�̃T�C���g���\�z�ł���l���i�[���ꂽ�z��.<br>
	 */
	public static float[] createSinWaveArray(float fq, float mag, float max, boolean reverse) {
		float[] reAry = new float[(int) (max / fq)];
		for (int i = 0; i < reAry.length; i++) {
			reAry[i] = (float) Math.sin(Math.toRadians(i * fq)) * mag;
			if (reverse) {
				reAry[i] = -reAry[i];
			}
		}
		return reAry;
	}

	/**
	 * �T�C�����擾���܂�.<br>
	 *
	 * @param angle �x���@�̊p�x�𑗐M���܂��B<br>
	 *
	 * @return �T�C���̒l��Ԃ��܂��B<br>
	 */
	public static float sin(float angle) {
		int index = (int) (angle + angle);
		if (index < sinTable.length && index >= 0) {
			return sinTable[index];
		}
		return sin(angle < 0 ? angle + 360 : angle - 360);
	}

	/**
	 * �R�T�C�����擾���܂�.
	 *
	 * @param angle �x���@�̊p�x�𑗐M���܂��B<br>
	 *
	 * @return �R�T�C���̒l��Ԃ��܂��B<br>
	 */
	public static float cos(float angle) {
		int index = (int) (angle + angle);
		if (index < cosTable.length && index >= 0) {
			return cosTable[index];
		}
		return cos(angle < 0 ? angle + 360 : angle - 360);
	}
}
