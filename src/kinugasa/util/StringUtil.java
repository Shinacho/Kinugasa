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

/**
 * �����񑀍샆�[�e�B���e�B�ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2012/06/16_15:23:39.<br>
 * @author Shinacho<br>
 */
public final class StringUtil {

	/**
	 * ���[�e�B���e�B�N���X�ł�.
	 */
	private StringUtil() {
	}

	/**
	 * �������length�̒����ɂȂ�悤�E�l���A�󂢂��X�y�[�X��" "��}�����܂�.
	 *
	 * @param msg �Ώە�����.<br>
	 * @param length �����̑S�̂̒���.<br>
	 *
	 * @return �E�l���ꂽ������.<br>
	 */
	public static String toRight(String msg, int length) {
		String res = "";
		for (int i = 0; i < length - msg.length(); i++) {
			res += " ";
		}
		res += msg;
		return res;
	}

	/**
	 * �w�肳�ꂽ�����񂩂�t�@�C�����𒊏o���܂�. ���Ƃ���/hoge/piyo/fuga/a.c�̂Ƃ�a.c��Ԃ��܂��B<br>
	 * ������̏I�[��"/"�ł���ꍇ�͂��̕����񎩑̂�Ԃ��܂��B<br>
	 * ���������"/"�����݂��Ȃ��ꍇ�����̕����񎩑̂�Ԃ��܂��B<br>
	 *
	 * @param path �t�@�C�����𒊏o����p�X�𑗐M���܂��B<br>
	 *
	 * @return �p�X������t�@�C�����𒊏o���ĕԂ��܂��B<br>
	 */
	public static String fileName(String path) {
		return path.endsWith("/") ? path : path.substring(path.lastIndexOf('/') + 1, path.length());
	}

	public static int[] parseIntCSV(String value)
			throws NumberFormatException {
		String[] values = value.split(",");
		int[] result = new int[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(values[i]);
		}
		return result;
	}

	public static int[] parseIntCSV(String value, String separator)
			throws NumberFormatException {
		if (!value.contains(separator)) {
			return new int[]{Integer.parseInt(value)};
		}
		String[] values = value.split(separator);
		int[] result = new int[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(values[i]);
		}
		return result;
	}

	public static boolean isDigit(String val) {
		boolean dg = true;
		for (char ch : val.toCharArray()) {
			dg &= (ch <= '9' & ch >= '0');
		}
		return dg;
	}
}
