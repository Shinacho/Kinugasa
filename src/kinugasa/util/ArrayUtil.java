/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

