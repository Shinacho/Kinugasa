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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 配列の操作や要素の検査ユーティリティです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/04/27_17:38:11<br>
 * @author Shinacho<br>
 */
public final class ArrayUtil {

	/**
	 * インスタンス化できません.
	 */
	private ArrayUtil() {
	}

	/**
	 * 配列の最初のNULL要素のインデックスを返します.
	 *
	 * @param <T> 配列の型です。<br>
	 * @param array 配列を送信します。<br>
	 * @return 最初に発見されたnull要素の位置を返します。 null要素が発見できなかった場合は-1を返します。<br>
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
	 * 要素が配列に含まれているかを検査します.
	 *
	 * @param <T> 配列の型です。<br>
	 * @param array 配列を送信します。<br>
	 * @param obj 検索するオブジェクトです。<br>
	 * @return objがarray内に存在する場合はtrue、そうでない場合はfalseを返します。<br>
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

