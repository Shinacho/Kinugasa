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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * �z��̑����v�f�̌������[�e�B���e�B�ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/04/27_17:38:11<br>
 * @author Dra0211<br>
 */
public final class ArrayUtil {

	/**
	 * �C���X�^���X���ł��܂���.
	 */
	private ArrayUtil() {
	}

	/**
	 * �z��̍ŏ���NULL�v�f�̃C���f�b�N�X��Ԃ��܂�.
	 *
	 * @param <T> �z��̌^�ł��B<br>
	 * @param array �z��𑗐M���܂��B<br>
	 * @return �ŏ��ɔ������ꂽnull�v�f�̈ʒu��Ԃ��܂��B null�v�f�������ł��Ȃ������ꍇ��-1��Ԃ��܂��B<br>
	 */
	public static <T> int firstNull(T[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * �v�f���z��Ɋ܂܂�Ă��邩���������܂�.
	 *
	 * @param <T> �z��̌^�ł��B<br>
	 * @param array �z��𑗐M���܂��B<br>
	 * @param obj ��������I�u�W�F�N�g�ł��B<br>
	 * @return obj��array���ɑ��݂���ꍇ��true�A�����łȂ��ꍇ��false��Ԃ��܂��B<br>
	 */
	public static <T> boolean contains(T[] array, T obj) {
		for (T array1 : array) {
			if (array1.equals(obj)) {
				return true;
			}
		}
		return false;
	}
}

