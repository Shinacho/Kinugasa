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

import kinugasa.game.system.GameSystem;

/**
 * 数学ユーティリティです.
 * <br>
 * <br>
 * 三角関数の値は0.5度刻みで計算されます。この値は 最初の呼び出し時にキャッシュされます。<br>
 * 乱数ジェネレータはkinugasa.util.Randomクラスを使用してください。<br>
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
	 * インスタンス化できません.<br>
	 */
	private MathUtil() {
	}
	/**
	 * sinの値がキャッシュされる配列です.
	 */
	private static final float[] sinTable = new float[720];
	/**
	 * cosの値がキャッシュされる配列です.
	 */
	private static final float[] cosTable = new float[720];

	public static void init() {
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("kinugasa.util.MathUtil.init()");
		}
		for (int i = 0; i < sinTable.length; i++) {
			sinTable[i] = (float) Math.sin(Math.toRadians((float) i / 2f));
		}
		for (int i = 0; i < cosTable.length; i++) {
			cosTable[i] = (float) Math.cos(Math.toRadians((float) i / 2f));
		}
	}

	/**
	 * サイン波を格納した単精度配列を作成します.
	 *
	 * @param fq サイン波の周波数.<br>
	 * @param mag 拡大倍率.<br>
	 * @param max θの最大値.<br>
	 *
	 * @return 指定した領域のサイン波を構築できる値が格納された配列.<br>
	 */
	public static float[] createSinWaveArray(float fq, float mag, float max) {
		float[] reAry = new float[(int) (max / fq)];
		for (int i = 0; i < reAry.length; i++) {
			reAry[i] = (float) Math.sin(Math.toRadians(i * fq)) * mag;
		}
		return reAry;
	}

	/**
	 * サイン波を格納した単精度配列を作成します.
	 *
	 * @param fq サイン波の周波数.<br>
	 * @param mag 拡大倍率.<br>
	 * @param max θの最大値.<br>
	 * @param reverse trueを指定すると波形を逆順にソートします.<br>
	 *
	 * @return 指定した領域のサイン波を構築できる値が格納された配列.<br>
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
	 * サインを取得します.<br>
	 *
	 * @param angle 度数法の角度を送信します。<br>
	 *
	 * @return サインの値を返します。<br>
	 */
	public static float sin(float angle) {
		int index = (int) (angle + angle);
		if (index < sinTable.length && index >= 0) {
			return sinTable[index];
		}
		return sin(angle < 0 ? angle + 360 : angle - 360);
	}

	/**
	 * コサインを取得します.
	 *
	 * @param angle 度数法の角度を送信します。<br>
	 *
	 * @return コサインの値を返します。<br>
	 */
	public static float cos(float angle) {
		int index = (int) (angle + angle);
		if (index < cosTable.length && index >= 0) {
			return cosTable[index];
		}
		return cos(angle < 0 ? angle + 360 : angle - 360);
	}
}
