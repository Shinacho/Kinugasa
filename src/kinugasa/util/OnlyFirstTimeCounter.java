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

/**
 * 最初の検査だけ指定された値を返し、それ以降は 最初の検査とは逆の値を返すタイムカウンタの実装です.
 * <br>
 * resetをコールすると、「最初の呼び出し」前の状態に戻すことが出来ます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/02/20_22:16:22<br>
 * @author Shinacho<br>
 */
public class OnlyFirstTimeCounter extends TimeCounter {

	private static final long serialVersionUID = -1074690399355994082L;
	/**
	 * 最初に返す値です.
	 */
	private boolean firstValue;
	/**
	 * 最初の呼び出しを検知するためのフラグです.
	 */
	private boolean running = false;

	/**
	 * 最初に返す値を指定した、タイムカウンタを作成します.
	 *
	 * @param firstValue 最初に返す値です.2回目の呼び出しからは、resetされるまで!firstValueを返します。<br>
	 */
	public OnlyFirstTimeCounter(boolean firstValue) {
		this.firstValue = firstValue;
	}

	@Override
	public boolean isReaching() {
		if (running) {
			return !firstValue;
		}
		running = true;
		return firstValue;
	}

	@Override
	public int getCurrentTime() {
		return running ? 0 : 1;
	}

	@Override
	public boolean isEnded() {
		return false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void reset() {
		running = false;
	}

	@Override
	public OnlyFirstTimeCounter clone() {
		return (OnlyFirstTimeCounter) super.clone();
	}

	@Override
	public String toString() {
		return "OnlyFirstTimeCounter{" + "firstValue=" + firstValue + ", running=" + running + '}';
	}
}
