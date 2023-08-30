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
package kinugasa.graphics;

/**
 * 4バイトARGB形式の色情報を編集するためのユーティリティです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_1:37:59<br>
 * @author Shinacho<br>
 */
public final class ARGBColor {

	/**
	 * ARGB列のアルファ成分のマスク値です.
	 */
	public static final int ARGB_ALPHA_MASK = 0xFF000000;
	/**
	 * ARGB列のRED成分のマスク値です.
	 */
	public static final int ARGB_RED_MASK = 0x00FF0000;
	/**
	 * ARGB列のGREEN成分のマスク値です.
	 */
	public static final int ARGB_GREEN_MASK = 0x0000FF00;
	/**
	 * ARGB列のBLUE成分のマスク値です.
	 */
	public static final int ARGB_BLUE_MASK = 0x000000FF;
	//
	/**
	 * アルファ成分の完全に不透明である値です.
	 */
	public static final int ALPHA_OPAQUE = 255;
	/**
	 * アルファ成分の完全に透明である値です.
	 */
	public static final int ALPHA_TRANSPARENT = 0;
	//
	/**
	 * アルファ成分を255に設定した場合黒になる完全に透明な色です.
	 */
	public static final int CLEAR_BLACK = 0x00000000;
	/**
	 * アルファ成分を255に設定した場合白になる完全に透明な色です. この定数は、APLHA成分以外の要素へのマスクとしても使用できます。
	 */
	public static final int CLEAR_WHITE = 0x00FFFFFF;
	/**
	 * 不透明な黒です.
	 */
	public static final int BLACK = 0xFF000000;
	/**
	 * 不透明な白です.
	 */
	public static final int WHITE = 0xFFFFFFFF;
	/**
	 * 不透明な赤です.
	 */
	public static final int RED = 0xFFFF0000;
	/**
	 * 不透明な緑です.
	 */
	public static final int GREEN = 0xFF00FF00;
	/**
	 * 不透明な青です.
	 */
	public static final int BLUE = 0xFF0000FF;
	/**
	 * 不透明な明るい灰色です.
	 */
	public static final int LIGHTGRAY = 0xFFC0C0C0;
	/**
	 * 不透明な灰色です.
	 */
	public static final int GRAY = 0xFF808080;
	/**
	 * 不透明な暗い灰色です.
	 */
	public static final int DARKGRAY = 0xFF404040;
	/**
	 * 不透明なオレンジです.
	 */
	public static final int ORANGE = 0xFFFFC800;
	/**
	 * 不透明な黄色です.
	 */
	public static final int YELLOW = 0xFFFFFF00;
	/**
	 * 不透明なマゼンタです.
	 */
	public static final int MAGENTA = 0xFFFF00FF;
	/**
	 * 不透明なシアンです.
	 */
	public static final int CYAN = 0xFF00FFFF;

	/**
	 * ユーティリティクラスです.
	 */
	private ARGBColor() {
	}

	/**
	 * 色情報が範囲内かを調べます.
	 *
	 * @param a アルファ成分.<br>
	 * @param r RED成分.<br>
	 * @param g GREEN成分.<br>
	 * @param b BLUE成分.<br>
	 *
	 * @throws IllegalArgumentException 範囲外の場合.<br>
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
	 * 色情報が範囲内かを調べます.
	 *
	 * @param a アルファ成分.<br>
	 * @param r RED成分.<br>
	 * @param g GREEN成分.<br>
	 * @param b BLUE成分.<br>
	 *
	 * @return 範囲内のときtrueを返す.<br>
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
	 * 0から255で指定された色情報をARGBに変換する.
	 *
	 * @param r RED成分.<br>
	 * @param g GREEN成分.<br>
	 * @param b BLUE成分.<br>
	 *
	 * @return 指定された色情報の不透明なARGB.<br>
	 *
	 * @throws IllegalArgumentException 色情報が範囲外のとき.<br>
	 */
	public static int toARGB(int r, int g, int b) throws IllegalArgumentException {
		checkColor(255, r, g, b);
		return ARGB_ALPHA_MASK | r << 16 | g << 8 | b;
	}

	/**
	 * 0から255で指定された色情報をARGBに変換する.
	 *
	 * @param a アルファ成分.<br>
	 * @param r RED成分.<br>
	 * @param g GREEN成分.<br>
	 * @param b BLUE成分.<br>
	 *
	 * @return 指定された色情報のARGB.<br>
	 *
	 * @throws IllegalArgumentException 色情報が範囲外のとき.<br>
	 */
	public static int toARGB(int a, int r, int g, int b) {
		checkColor(a, r, g, b);
		return a << 24 | r << 16 | g << 8 | b;
	}

	/**
	 * AWTカラーをARGBに変換します.<br>
	 *
	 * @param c 色.<br>
	 *
	 * @return ARGB.<br>
	 */
	public static int toARGB(java.awt.Color c) {
		return c.getRGB();
	}

	/**
	 * ARGBをAWTカラーに変更します.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return AWTカラー.<br>
	 */
	public static java.awt.Color toAWTColor(int argb) {
		return new java.awt.Color(getRed(argb), getGreen(argb), getBlue(argb), getAlpha(argb));
	}

	/**
	 * ARGB列からアルファ成分を抽出します.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return アルファ成分を0から255の値として返します.<br>
	 */
	public static int getAlpha(int argb) {
		return argb >> 24 & 0xFF;
	}

	public static boolean isTransparent(int argb) {
		return getAlpha(argb) == 0;
	}

	public static int reverse(int argb) {
		return toARGB(getAlpha(argb),
				255 - getRed(argb),
				255 - getGreen(argb),
				255 - getBlue(argb));
	}

	/**
	 * ARGB列からRED成分を抽出します.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return RED成分を0から255の値として返します.<br>
	 */
	public static int getRed(int argb) {
		return argb >> 16 & 0xFF;
	}

	/**
	 * ARGB列からGREEN成分を抽出します.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return GREEN成分を0から255の値として返します.<br>
	 */
	public static int getGreen(int argb) {
		return argb >> 8 & 0xFF;
	}

	/**
	 * ARGB列からBLUE成分を抽出します.
	 *
	 * @param argb ARGB.<br>
	 *
	 * @return BLUE成分を0から255の値として返します.<br>
	 */
	public static int getBlue(int argb) {
		return argb & 0xFF;
	}

	/**
	 * RGBの平均値（明度）を算出します.
	 *
	 * @param argb RGB.<br>
	 *
	 * @return 明度.<br>
	 */
	public static int getRGBAverage(int argb) {
		return (getRed(argb) + getGreen(argb) + getBlue(argb)) / 3;
	}

	/**
	 * 4バイトカラーを整形した文字列を返します. このメソッドは、"ARGB:[getAlpha(argb)], [getRed(argb)],
	 * [getGreen(argb)], [getBlue(argb)]"の形式の文字列を 返します.<br>
	 *
	 * @param argb ARGB形式の4バイトカラー.<br>
	 *
	 * @return 整形した文字列を返す.<br>
	 */
	public static String toString(int argb) {
		return "ARGB:[" + getAlpha(argb) + ", " + getRed(argb) + ", " + getGreen(argb) + ", " + getBlue(argb) + "]";
	}
}
