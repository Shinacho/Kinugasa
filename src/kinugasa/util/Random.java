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
 * 乱数とダイスのエミュレートを提供します.
 * <br>
 * 乱数ジェネレータを利用する方法は2つあります。<br>
 * 1つは、シードを指定してから、乱数生成機能を使用する方法です。<br>
 * シードはリプレイの保存などに使用するため、そのようなゲームデザインの場合には、 適切なタイミングでシードを初期化したり取得する必要があります。<br>
 * 2つめの方法は、シードを指定しないで乱数生成機能を使用することです。<br>
 * この場合には、最初に乱数生成機能(シードの取得(getSeed)を含む)を使用した時点で、適当なシードが適用されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/11_17:26:09<br>
 * @author Dra0211<br>
 */
public final class Random implements Serializable {

	private static final long serialVersionUID = 4919268115768305696L;

	/**
	 * インスタンス化できません.
	 */
	private Random() {
	}
	/**
	 * 現在使用中のシードです.
	 */
	private static long seed;
	/**
	 * 乱数ジェネレータです.
	 */
	private static java.util.Random random;

	/**
	 * シードを適当な数値で初期化します.
	 *
	 * @return 適用されたシードを返します。<br>
	 */
	public static long initSeed() {
		return initSeed(System.nanoTime());
	}

	/**
	 * 指定されたシードを使用してジェネレータを初期化します.
	 *
	 * @param seed 設定するシードを指定します。<br>
	 *
	 * @return 適用されたシードを返します。<br>
	 */
	public static long initSeed(long seed) {
		random = new java.util.Random(Random.seed = seed);
		return seed;
	}

	/**
	 * 使用中のシードを返します.
	 *
	 * ジェネレータが初期化されていない場合は、適当なシードを使用して初期化されます。<br>
	 *
	 * @return 使用中のシードを返します。<br>
	 */
	public static long getSeed() {
		if (random == null) {
			initSeed();
		}
		return seed;
	}

	/**
	 * 0から1までのランダムなfloat値を返します.
	 *
	 * 結果には、0は含まれますが、1は含まれません。<br>
	 *
	 * @return 0から1までのランダムなfloatを返します。<br>
	 */
	public static float randomFloat() {
		if (random == null) {
			initSeed();
		}
		return random.nextFloat();
	}

	/**
	 * 0からmaxValueまでのランダムなfloat値を返します.
	 *
	 * 結果には、0は含まれますが、maxValueは含まれません。<br>
	 *
	 * @param maxValue 乱数の最大値を指定します。<br>
	 *
	 * @return 0からmaxValueまでのランダムなfloatを返します。<br>
	 */
	public static float randomFloat(float maxValue) {
		if (random == null) {
			initSeed();
		}
		return randomFloat() * maxValue;
	}

	/**
	 * ランダムなint値を返します。
	 *
	 * @return Intの全ての範囲のランダムな値を返します。<br>
	 */
	public static int randomInt() {
		if (random == null) {
			initSeed();
		}
		return random.nextInt();
	}

	/**
	 * 0からmaxValueまでのランダムなint値を返します.
	 *
	 * 結果には、0は含まれますが、maxValueは含まれません。<br>
	 *
	 * @param maxValue 乱数の最大値を指定します。0以下の場合には0に切り詰められます。<br>
	 *
	 * @return 0からmaxValueまでのランダムなintを返します。<br>
	 */
	public static int randomAbsInt(int maxValue) {
		if (random == null) {
			initSeed();
		}
		return random.nextInt(maxValue < 0 ? 0 : maxValue);
	}

	/**
	 * ランダムな整数のint値を返します.
	 *
	 * @return 整数のIntのランダムな値を返します。0を含みます。<br>
	 */
	public static int randomAbsInt() {
		return Math.abs(randomInt());
	}

	/**
	 * FROMからTOまでのランダムなintを返します.
	 *
	 * @param from 開始数値.
	 * @param to 終了数値.
	 * @return FROM?TOまでのランダムな値。TOは含まない。
	 */
	public static int randomAbsInt(int from, int to) {
		return from + randomAbsInt(to - from);
	}

	/**
	 * ランダムなbooleanを返します.
	 *
	 * @return true又はfalseを返します。<br>
	 */
	public static boolean randomBool() {
		if (random == null) {
			initSeed();
		}
		return random.nextBoolean();
	}

	/**
	 * 確率pを判定します.
	 *
	 * @param p 発生する確率を0から1のfloatとして指定します。<br>
	 *
	 * @return p*100(%)の確率でtrueを返します。 ただしpが0以下の場合はfalse、pが1以上の場合はtrueを返します。<br>
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

	// spread%上下した値を返します。
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
	 * レガシーなダイスロールをエミュレートするメソッドです.
	 *
	 * @param num ダイスを振る回数を指定します。この引数が0の場合、0を返します。<br>
	 * @param sided ダイスの面数を指定します。<br>
	 *
	 * @return [num D sided] を返します。<br>
	 */
	public static int dice(int num, int sided) {
		return randomAbsInt(sided * num - num + 1) + (num);
	}

	/**
	 * 3面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 3]を返します。<br>
	 */
	public static int d3(int num) {
		return dice(num, 3);
	}

	/**
	 * 4面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 4]を返します。<br>
	 */
	public static int d4(int num) {
		return dice(num, 4);
	}

	/**
	 * 6面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 6]を返します。<br>
	 */
	public static int d6(int num) {
		return dice(num, 6);
	}

	/**
	 * 8面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 8]を返します。<br>
	 */
	public static int d8(int num) {
		return dice(num, 8);
	}

	/**
	 * 10面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 10]を返します。<br>
	 */
	public static int d10(int num) {
		return dice(num, 10);
	}

	/**
	 * 12面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 12]を返します。<br>
	 */
	public static int d12(int num) {
		return dice(num, 12);
	}

	/**
	 * 20面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 20]を返します。<br>
	 */
	public static int d20(int num) {
		return dice(num, 20);
	}

	/**
	 * 24面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 24]を返します。<br>
	 */
	public static int d24(int num) {
		return dice(num, 24);
	}

	/**
	 * 30面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 30]を返します。<br>
	 */
	public static int d30(int num) {
		return dice(num, 30);
	}

	/**
	 * 50面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 50]を返します。<br>
	 */
	public static int d50(int num) {
		return dice(num, 50);
	}

	/**
	 * 100面ダイスをnum回振った合計値を返します.
	 *
	 * @param num ダイスを振る回数を指定します。<br>
	 *
	 * @return [num D 100]を返します。<br>
	 */
	public static int d100(int num) {
		return dice(num, 100);
	}
}
