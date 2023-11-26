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

/**
 * 文字列操作ユーティリティです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2012/06/16_15:23:39.<br>
 * @author Shinacho<br>
 */
public final class StringUtil {

	/**
	 * ユーティリティクラスです.
	 */
	private StringUtil() {
	}

	/**
	 * 文字列をlengthの長さになるよう右詰し、空いたスペースに" "を挿入します.
	 *
	 * @param msg 対象文字列.<br>
	 * @param length 操作後の全体の長さ.<br>
	 *
	 * @return 右詰された文字列.<br>
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
	 * 文字列をlengthの長さになるよう右詰し、空いたスペースに"0"を挿入します.
	 *
	 * @param msg 対象文字列.<br>
	 * @param length 操作後の全体の長さ.<br>
	 *
	 * @return 右詰された文字列.<br>
	 */
	public static String zeroUme(String msg, int length) {
		String res = "";
		for (int i = 0; i < length - msg.length(); i++) {
			res += "0";
		}
		res += msg;
		return res;
	}

	/**
	 * 指定された文字列からファイル名を抽出します. たとえば/hoge/piyo/fuga/a.cのときa.cを返します。<br>
	 * 文字列の終端が"/"である場合はその文字列自体を返します。<br>
	 * 文字列内に"/"が存在しない場合もその文字列自体を返します。<br>
	 *
	 * @param path ファイル名を抽出するパスを送信します。<br>
	 *
	 * @return パス中からファイル名を抽出して返します。<br>
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

	public static String[] safeSplit(String val, String sep) {
		if (val == null || val.isEmpty()) {
			return new String[]{};
		}
		return val.contains(sep) ? val.split(sep) : new String[]{val};
	}
}
