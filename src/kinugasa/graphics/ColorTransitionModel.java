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

import java.awt.Color;
import kinugasa.object.Model;
import kinugasa.object.Statable;

/**
 * 色のARGBの各要素の遷移アルゴリズムを定義します.
 * <br>
 * 色要素は0から255の整数として表されます。
 * 1つの色は、RGB又はRGBAのColorTransitionModelから構成されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_18:59:50<br>
 * @author Shinacho<br>
 */
public abstract class ColorTransitionModel extends Model implements Statable {

	private static final long serialVersionUID = -5301732826729025440L;
	/** カウンタとして使用可能な最小の値(0)です. */
	public static final int MIN = 0;
	/** カウンタとして使用可能な最大の値(255)です. */
	public static final int MAX = 255;
	/** 現在の値です. */
	protected int value;

	/**
	 * 初期値を指定して、遷移アルゴリズムを定義します.
	 * @param value このモデルが表す色要素の初期値を指定します。<br>
	 * @throws IllegalArgumentException 初期値が色要素として有効な値でないときに投げられます。<br>
	 */
	public ColorTransitionModel(int value) throws IllegalArgumentException {
		if (value < 0 || value > 255) {
			throw new IllegalArgumentException("illegal color value : value=[" + value + "]");
		}
		this.value = value;
	}

	//カウントしない固定カウンタを返す。この戻り値はStaableとしては終わらない
	/**
	 * 値が変更されないアルゴリズムを作成します.
	 * @param value 色要素の値を指定します。<br>
	 * @return 指定した色要素の値が変更されないモデルを返します。<br>
	 * @throws IllegalArgumentException 初期値が色要素として有効な値でないときに投げられます。<br>
	 */
	public static ColorTransitionModel valueOf(int value) throws IllegalArgumentException {
		return new ColorTransitionModel(value) {
			private static final long serialVersionUID = 8593451345811759766L;

			@Override
			public void update() {
			}

			@Override
			public boolean isEnded() {
				return true;
			}

			@Override
			public boolean isRunning() {
				return true;
			}
		};
	}

	@Override
	public abstract boolean isEnded();

	@Override
	public abstract boolean isRunning();

	/**
	 * 特定のアルゴリズムにしたがって、値(value)を変更します.
	 * 値が、有効範囲(0-255)から出る場合、有効範囲内に切り詰める必要があります。<br>
	 * また、開始および終了状態の変更も行う必要があります。<br>
	 */
	public abstract void update();

	/**
	 * 値の現在値を取得します.
	 * このメソッドによって返される値は、次にupdateが呼ばれるまで
	 * 変更されてはなりません。<br>
	 * @return 現在値を返します。<br>
	 */
	public int getValue() {
		return value;
	}

	/**
	 * 値を設定す.
	 * @param value 新しい値を送信します。0以上255以下を指定します。<br
	 * @throws IllegalArgumentException 初期値が色要素として有効な値でないときに投げられます。<br>
	 */
	public void setValue(int value) throws IllegalArgumentException {
		if (value < 0 || value > 255) {
			throw new IllegalArgumentException("illegal color value : value=[" + value + "]");
		}
		this.value = value;
	}

	/**
	 * RGBの遷移アルゴリズムの現在の値を使用して、色を構築します.
	 * @param r 赤要素のアルゴリズムです。<br>
	 * @param g 緑のアルゴリズムです。<br>
	 * @param b 青素のアルゴリズムです。<br>
	 * @return 指定されたアルゴリズムの現在の値から構築された色を返します。アルファ成分は255となります。<br>
	 */
	public static Color createColor(ColorTransitionModel r,
			ColorTransitionModel g,
			ColorTransitionModel b) {
		return new Color(r.value, g.value, b.value);
	}

	/**
	 * RGBA遷移アルゴリズムの現在の値を使用して、色を構築します.
	 * @param r 赤要素のアルゴリズムです。<br>
	 * @param g 緑のアルゴリズムです。<br>
	 * @param b 青素のアルゴリズムです。<br>
	 * @param a アルファ成分のアルゴリズムです。<br>
	 * @return 指定されたアルゴリズムの現在の値から構築された色を返します。<br>
	 */
	public static Color createColor(ColorTransitionModel r,
			ColorTransitionModel g,
			ColorTransitionModel b,
			ColorTransitionModel a) {
		return new Color(r.value, g.value, b.value, a.value);
	}

	@Override
	public ColorTransitionModel clone() {
		return (ColorTransitionModel) super.clone();
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + this.value;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ColorTransitionModel other = (ColorTransitionModel) obj;
		if (this.value != other.value) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ColorCounter{" + "value=" + value + '}';
	}
}
