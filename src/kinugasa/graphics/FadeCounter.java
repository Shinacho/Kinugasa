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
 * 色要素をフェードイン／フェードアウトするためのColorCounterの実装です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_19:07:48<br>
 * @author Shinacho<br>
 */
public class FadeCounter extends ColorTransitionModel {

	private static final long serialVersionUID = -1609450992263652227L;
	/** フェードの速度です。カウンタの現在値に足されます. */
	private int speed;

	/**
	 * 初期値と遷移速度を指定して、フェードカウンタを作成します.
	 * @param value カウンタの初期値です。0から255の間で指定します。<br>
	 * @param speed カウンタの遷移速度です。負数を指定できます。<br>
	 * @throws IllegalArgumentException 初期値が0未満か、255を超える場合に投げられます。<br>
	 */
	public FadeCounter(int value, int speed) throws IllegalArgumentException {
		super(value);
		this.speed = speed;
	}

	/**
	 * 0から開始するカウンタを作成します.
	 * @param speed カウンタの遷移速度を指定します。
	 * このメソッドでは、通常は正数を指定します。<br>
	 * @return フェードイン用のカウンタを作成します。<br>
	 */
	public static FadeCounter fadeIn(int speed) {
		return new FadeCounter(MIN, speed);
	}

	/**
	 * 255から開始するカウンタを作成します.
	 * @param speed カウンタの遷移速度を指定します。
	 * このメソッドでは、通常は負数を指定します。<br>
	 * @return フェードアウト用のカウンタを作成します。<br>
	 */
	public static FadeCounter fadeOut(int speed) {
		return new FadeCounter(MAX, speed);
	}

	@Override
	public void update() {
		if (ended) {
			return;
		}
		started = true;
		value += speed;
		if (value < MIN) {
			value = MIN;
			ended = true;
		} else if (value > MAX) {
			value = MAX;
			ended = true;
		}
	}
	/** 「開始している」状態フラグです.
	 * 最初にupdateを呼び出すと、trueになります。
	 * それ以降は、常にtrueを返します。 */
	private boolean started = false;
	/** 「終了している」状態フラグです.
	 * カウンタの値が有効範囲から出ると
	 * trueになります。それ以降は常にtrueを返します。 */
	private boolean ended = false;

	/**
	 * {@inheritDoc }
	 * <br>
	 * この実装では最初にupdateを呼び出すと、それ以降は常に「開始している状態」となります。<br>
	 */
	@Override
	public boolean isRunning() {
		return started;
	}

	/**
	 * {@inheritDoc }
	 * <br>
	 * この実装では、updateメソッドによってカウンタの値が有効範囲から出ると
	 * それ以降は常に「終了している状態」となります。<br>
	 */
	@Override
	public boolean isEnded() {
		return ended;
	}

	@Override
	public String toString() {
		return "ColorFadeCounter{" + "value=" + value + ", " + "speed=" + speed + '}';
	}

	@Override
	public FadeCounter clone() {
		return (FadeCounter) super.clone();
	}
}
