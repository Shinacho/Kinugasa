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
package kinugasa.graphics;

/**
 * 4�o�C�gARGB�`���̐F����ҏW���邽�߂̃��[�e�B���e�B�ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_1:37:59<br>
 * @author Shinacho<br>
 */
public final class ARGBColor {

	/**
	 * ARGB��̃A���t�@�����̃}�X�N�l�ł�.
	 */
	public static final int ARGB_ALPHA_MASK = 0xFF000000;
	/**
	 * ARGB���RED�����̃}�X�N�l�ł�.
	 */
	public static final int ARGB_RED_MASK = 0x00FF0000;
	/**
	 * ARGB���GREEN�����̃}�X�N�l�ł�.
	 */
	public static final int ARGB_GREEN_MASK = 0x0000FF00;
	/**
	 * ARGB���BLUE�����̃}�X�N�l�ł�.
	 */
	public static final int ARGB_BLUE_MASK = 0x000000FF;
	//
	/**
	 * �A���t�@�����̊��S�ɕs�����ł���l�ł�.
	 */
	public static final int ALPHA_OPAQUE = 255;
	/**
	 * �A���t�@�����̊��S�ɓ����ł���l�ł�.
	 */
	public static final int ALPHA_TRANSPARENT = 0;
	//
	/**
	 * �A���t�@������255�ɐݒ肵���ꍇ���ɂȂ銮�S�ɓ����ȐF�ł�.
	 */
	public static final int CLEAR_BLACK = 0x00000000;
	/**
	 * �A���t�@������255�ɐݒ肵���ꍇ���ɂȂ銮�S�ɓ����ȐF�ł�. ���̒萔�́AAPLHA�����ȊO�̗v�f�ւ̃}�X�N�Ƃ��Ă��g�p�ł��܂��B
	 */
	public static final int CLEAR_WHITE = 0x00FFFFFF;
	/**
	 * �s�����ȍ��ł�.
	 */
	public static final int BLACK = 0xFF000000;
	/**
	 * �s�����Ȕ��ł�.
	 */
	public static final int WHITE = 0xFFFFFFFF;
	/**
	 * �s�����ȐԂł�.
	 */
	public static final int RED = 0xFFFF0000;
	/**
	 * �s�����ȗ΂ł�.
	 */
	public static final int GREEN = 0xFF00FF00;
	/**
	 * �s�����Ȑł�.
	 */
	public static final int BLUE = 0xFF0000FF;
	/**
	 * �s�����Ȗ��邢�D�F�ł�.
	 */
	public static final int LIGHTGRAY = 0xFFC0C0C0;
	/**
	 * �s�����ȊD�F�ł�.
	 */
	public static final int GRAY = 0xFF808080;
	/**
	 * �s�����ȈÂ��D�F�ł�.
	 */
	public static final int DARKGRAY = 0xFF404040;
	/**
	 * �s�����ȃI�����W�ł�.
	 */
	public static final int ORANGE = 0xFFFFC800;
	/**
	 * �s�����ȉ��F�ł�.
	 */
	public static final int YELLOW = 0xFFFFFF00;
	/**
	 * �s�����ȃ}�[���^�ł�.
	 */
	public static final int MAGENTA = 0xFFFF00FF;
	/**
	 * �s�����ȃV�A���ł�.
	 */
	public static final int CYAN = 0xFF00FFFF;

	/**
	 * ���[�e�B���e�B�N���X�ł�.
	 */
	private ARGBColor() {
	}

	/**
	 * �F��񂪔͈͓����𒲂ׂ܂�.
	 *
	 * @param a �A���t�@����.<br>
	 * @param r RED����.<br>
	 * @param g GREEN����.<br>
	 * @param b BLUE����.<br>
	 *
	 * @throws IllegalArgumentException �͈͊O�̏ꍇ.<br>
	 */
	private static void checkColor(int a, int r, int g, int b) throws IllegalArgumentException {
		String badComp = "";
		if (a < 0 || a > 255) {
			badComp += " A";
		}
		if (r < 0 || r > 255) {
			badComp += " R";
		}
		if (g < 0 || g > 255) {
			badComp += " G";
		}
		if (b < 0 || b > 255) {
			badComp += " B";
		}
		if (!"".equals(badComp)) {
			throw new IllegalArgumentException("out of range : " + badComp);
		}
	}

	/**
	 * �F��񂪔͈͓����𒲂ׂ܂�.
	 *
	 * @param a �A���t�@����.<br>
	 * @param r RED����.<br>
	 * @param g GREEN����.<br>
	 * @param b BLUE����.<br>
	 *
	 * @return �͈͓��̂Ƃ�true��Ԃ�.<br>
	 */
	public static boolean checkRange(int a, int r, int g, int b) {
		try {
			checkColor(a, r, g, b);
			return true;
		} catch (IllegalArgumentException iae) {
			return false;
		}
	}

	/**
	 * 0����255�Ŏw�肳�ꂽ�F����ARGB�ɕϊ�����.
	 *
	 * @param r RED����.<br>
	 * @param g GREEN����.<br>
	 * @param b BLUE����.<br>
	 *
	 * @return �w�肳�ꂽ�F���̕s������ARGB.<br>
	 *
	 * @throws IllegalArgumentException �F��񂪔͈͊O�̂Ƃ�.<br>
	 */
	public static int toARGB(int r, int g, int b) throws IllegalArgumentException {
		checkColor(255, r, g, b);
		return ARGB_ALPHA_MASK | r << 16 | g << 8 | b;
	}

	/**
	 * 0����255�Ŏw�肳�ꂽ�F����ARGB�ɕϊ�����.
	 *
	 * @param a �A���t�@����.<br>
	 * @param r RED����.<br>
	 * @param g GREEN����.<br>
	 * @param b BLUE����.<br>
	 *
	 * @return �w�肳�ꂽ�F����ARGB.<br>
	 *
	 * @throws IllegalArgumentException �F��񂪔͈͊O�̂Ƃ�.<br>
	 */
	public static int toARGB(int a, int r, int g, int b) {
		checkColor(a, r, g, b);
		return a << 24 | r << 16 | g << 8 | b;
	}

	/**
	 * AWT�J���[��ARGB�ɕϊ����܂�.<br>
	 *
	 * @param c �F.<br>
	 *
	 * @return ARGB.<br>
	 */
	public static int toARGB(java.awt.Color c) {
		return c.getRGB();
	}

	/**
	 * ARGB��AWT�J���[�ɕύX���܂�.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return AWT�J���[.<br>
	 */
	public static java.awt.Color toAWTColor(int argb) {
		return new java.awt.Color(getRed(argb), getGreen(argb), getBlue(argb), getAlpha(argb));
	}

	/**
	 * ARGB�񂩂�A���t�@�����𒊏o���܂�.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return �A���t�@������0����255�̒l�Ƃ��ĕԂ��܂�.<br>
	 */
	public static int getAlpha(int argb) {
		return argb >> 24 & 0xFF;
	}

	public static boolean isTransparent(int argb) {
		return getAlpha(argb) == 0;
	}

	/**
	 * ARGB�񂩂�RED�����𒊏o���܂�.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return RED������0����255�̒l�Ƃ��ĕԂ��܂�.<br>
	 */
	public static int getRed(int argb) {
		return argb >> 16 & 0xFF;
	}

	/**
	 * ARGB�񂩂�GREEN�����𒊏o���܂�.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return GREEN������0����255�̒l�Ƃ��ĕԂ��܂�.<br>
	 */
	public static int getGreen(int argb) {
		return argb >> 8 & 0xFF;
	}

	/**
	 * ARGB�񂩂�BLUE�����𒊏o���܂�.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return BLUE������0����255�̒l�Ƃ��ĕԂ��܂�.<br>
	 */
	public static int getBlue(int argb) {
		return argb & 0xFF;
	}

	/**
	 * RGB�̕��ϒl�i���x�j���Z�o���܂�.
	 *
	 * @param argb RGB.<br>
	 *
	 * @return ���x.<br>
	 */
	public static int getRGBAverage(int argb) {
		return (getRed(argb) + getGreen(argb) + getBlue(argb)) / 3;
	}

	/**
	 * 4�o�C�g�J���[�𐮌`�����������Ԃ��܂�. ���̃��\�b�h�́A"ARGB:[getAlpha(argb)], [getRed(argb)], [getGreen(argb)], [getBlue(argb)]"�̌`���̕������ �Ԃ��܂�.<br>
	 *
	 * @param argb ARGB�`����4�o�C�g�J���[.<br>
	 *
	 * @return ���`�����������Ԃ�.<br>
	 */
	public static String toString(int argb) {
		return "ARGB:[" + getAlpha(argb) + ", " + getRed(argb) + ", " + getGreen(argb) + ", " + getBlue(argb) + "]";
	}
}
