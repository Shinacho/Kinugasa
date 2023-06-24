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
