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
package kinugasa.util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * �����ƃ_�C�X�̃G�~�����[�g��񋟂��܂�.
 * <br>
 * �����W�F�l���[�^�𗘗p������@��2����܂��B<br>
 * 1�́A�V�[�h���w�肵�Ă���A���������@�\���g�p������@�ł��B<br>
 * �V�[�h�̓��v���C�̕ۑ��ȂǂɎg�p���邽�߁A���̂悤�ȃQ�[���f�U�C���̏ꍇ�ɂ́A �K�؂ȃ^�C�~���O�ŃV�[�h��������������擾����K�v������܂��B<br>
 * 2�߂̕��@�́A�V�[�h���w�肵�Ȃ��ŗ��������@�\���g�p���邱�Ƃł��B<br>
 * ���̏ꍇ�ɂ́A�ŏ��ɗ��������@�\(�V�[�h�̎擾(getSeed)���܂�)���g�p�������_�ŁA�K���ȃV�[�h���K�p����܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/11_17:26:09<br>
 * @author Dra0211<br>
 */
public final class Random implements Serializable {

	private static final long serialVersionUID = 4919268115768305696L;

	/**
	 * �C���X�^���X���ł��܂���.
	 */
	private Random() {
	}
	/**
	 * ���ݎg�p���̃V�[�h�ł�.
	 */
	private static long seed;
	/**
	 * �����W�F�l���[�^�ł�.
	 */
	private static java.util.Random random;

	/**
	 * �V�[�h��K���Ȑ��l�ŏ��������܂�.
	 *
	 * @return �K�p���ꂽ�V�[�h��Ԃ��܂��B<br>
	 */
	public static long initSeed() {
		return initSeed(System.nanoTime());
	}

	/**
	 * �w�肳�ꂽ�V�[�h���g�p���ăW�F�l���[�^�����������܂�.
	 *
	 * @param seed �ݒ肷��V�[�h���w�肵�܂��B<br>
	 *
	 * @return �K�p���ꂽ�V�[�h��Ԃ��܂��B<br>
	 */
	public static long initSeed(long seed) {
		random = new java.util.Random(Random.seed = seed);
		return seed;
	}

	/**
	 * �g�p���̃V�[�h��Ԃ��܂�.
	 *
	 * �W�F�l���[�^������������Ă��Ȃ��ꍇ�́A�K���ȃV�[�h���g�p���ď���������܂��B<br>
	 *
	 * @return �g�p���̃V�[�h��Ԃ��܂��B<br>
	 */
	public static long getSeed() {
		if (random == null) {
			initSeed();
		}
		return seed;
	}

	/**
	 * 0����1�܂ł̃����_����float�l��Ԃ��܂�.
	 *
	 * ���ʂɂ́A0�͊܂܂�܂����A1�͊܂܂�܂���B<br>
	 *
	 * @return 0����1�܂ł̃����_����float��Ԃ��܂��B<br>
	 */
	public static float randomFloat() {
		if (random == null) {
			initSeed();
		}
		return random.nextFloat();
	}

	/**
	 * 0����maxValue�܂ł̃����_����float�l��Ԃ��܂�.
	 *
	 * ���ʂɂ́A0�͊܂܂�܂����AmaxValue�͊܂܂�܂���B<br>
	 *
	 * @param maxValue �����̍ő�l���w�肵�܂��B<br>
	 *
	 * @return 0����maxValue�܂ł̃����_����float��Ԃ��܂��B<br>
	 */
	public static float randomFloat(float maxValue) {
		if (random == null) {
			initSeed();
		}
		return randomFloat() * maxValue;
	}

	/**
	 * �����_����int�l��Ԃ��܂��B
	 *
	 * @return Int�̑S�Ă͈̔͂̃����_���Ȓl��Ԃ��܂��B<br>
	 */
	public static int randomInt() {
		if (random == null) {
			initSeed();
		}
		return random.nextInt();
	}

	/**
	 * 0����maxValue�܂ł̃����_����int�l��Ԃ��܂�.
	 *
	 * ���ʂɂ́A0�͊܂܂�܂����AmaxValue�͊܂܂�܂���B<br>
	 *
	 * @param maxValue �����̍ő�l���w�肵�܂��B0�ȉ��̏ꍇ�ɂ�0�ɐ؂�l�߂��܂��B<br>
	 *
	 * @return 0����maxValue�܂ł̃����_����int��Ԃ��܂��B<br>
	 */
	public static int randomAbsInt(int maxValue) {
		if (random == null) {
			initSeed();
		}
		return random.nextInt(maxValue < 0 ? 0 : maxValue);
	}

	/**
	 * �����_���Ȑ�����int�l��Ԃ��܂�.
	 *
	 * @return ������Int�̃����_���Ȓl��Ԃ��܂��B0���܂݂܂��B<br>
	 */
	public static int randomAbsInt() {
		return Math.abs(randomInt());
	}

	/**
	 * FROM����TO�܂ł̃����_����int��Ԃ��܂�.
	 *
	 * @param from �J�n���l.
	 * @param to �I�����l.
	 * @return FROM?TO�܂ł̃����_���Ȓl�BTO�͊܂܂Ȃ��B
	 */
	public static int randomAbsInt(int from, int to) {
		return from + randomAbsInt(to - from);
	}

	/**
	 * �����_����boolean��Ԃ��܂�.
	 *
	 * @return true����false��Ԃ��܂��B<br>
	 */
	public static boolean randomBool() {
		if (random == null) {
			initSeed();
		}
		return random.nextBoolean();
	}

	/**
	 * �m��p�𔻒肵�܂�.
	 *
	 * @param p ��������m����0����1��float�Ƃ��Ďw�肵�܂��B<br>
	 *
	 * @return p*100(%)�̊m����true��Ԃ��܂��B ������p��0�ȉ��̏ꍇ��false�Ap��1�ȏ�̏ꍇ��true��Ԃ��܂��B<br>
	 */
	public static boolean percent(float p) {
		if (p <= 0f) {
			return false;
		}
		if (p >= 1f) {
			return true;
		}
		return randomFloat() < p;
	}

	// spread%�㉺�����l��Ԃ��܂��B
	public static float percent(float num, float spread) {
		if (spread >= 1) {
			return num * spread;
		}
		if (spread <= 0) {
			return 0;
		}
		float from = num * (1 - randomFloat(spread));
		float to = num * (1 + randomFloat(spread));
		if (randomBool()) {
			return from;
		}
		return to;
	}

	public static <T> T random(T... t) {
		int i = randomAbsInt(t.length);
		return t[i];
	}

	public static Point2D.Float randomLocation(Rectangle2D r) {
		return randomLocation(r, 1, 1);
	}

	public static Point2D.Float randomLocation(Rectangle2D r, float w, float h) {
		float x = (float) (r.getX() + randomAbsInt((int) (r.getWidth() - w)));
		float y = (float) (r.getY() + randomAbsInt((int) (r.getHeight() - h)));
		return new Point2D.Float(x, y);
	}

	/**
	 * ���K�V�[�ȃ_�C�X���[�����G�~�����[�g���郁�\�b�h�ł�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B���̈�����0�̏ꍇ�A0��Ԃ��܂��B<br>
	 * @param sided �_�C�X�̖ʐ����w�肵�܂��B<br>
	 *
	 * @return [num D sided] ��Ԃ��܂��B<br>
	 */
	public static int dice(int num, int sided) {
		return randomAbsInt(sided * num - num + 1) + (num);
	}

	/**
	 * 3�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 3]��Ԃ��܂��B<br>
	 */
	public static int d3(int num) {
		return dice(num, 3);
	}

	/**
	 * 4�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 4]��Ԃ��܂��B<br>
	 */
	public static int d4(int num) {
		return dice(num, 4);
	}

	/**
	 * 6�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 6]��Ԃ��܂��B<br>
	 */
	public static int d6(int num) {
		return dice(num, 6);
	}

	/**
	 * 8�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 8]��Ԃ��܂��B<br>
	 */
	public static int d8(int num) {
		return dice(num, 8);
	}

	/**
	 * 10�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 10]��Ԃ��܂��B<br>
	 */
	public static int d10(int num) {
		return dice(num, 10);
	}

	/**
	 * 12�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 12]��Ԃ��܂��B<br>
	 */
	public static int d12(int num) {
		return dice(num, 12);
	}

	/**
	 * 20�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 20]��Ԃ��܂��B<br>
	 */
	public static int d20(int num) {
		return dice(num, 20);
	}

	/**
	 * 24�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 24]��Ԃ��܂��B<br>
	 */
	public static int d24(int num) {
		return dice(num, 24);
	}

	/**
	 * 30�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 30]��Ԃ��܂��B<br>
	 */
	public static int d30(int num) {
		return dice(num, 30);
	}

	/**
	 * 50�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 50]��Ԃ��܂��B<br>
	 */
	public static int d50(int num) {
		return dice(num, 50);
	}

	/**
	 * 100�ʃ_�C�X��num��U�������v�l��Ԃ��܂�.
	 *
	 * @param num �_�C�X��U��񐔂��w�肵�܂��B<br>
	 *
	 * @return [num D 100]��Ԃ��܂��B<br>
	 */
	public static int d100(int num) {
		return dice(num, 100);
	}
}
