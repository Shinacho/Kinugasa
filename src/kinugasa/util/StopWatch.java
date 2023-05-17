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

import java.io.PrintStream;
import java.util.logging.Level;
import kinugasa.game.GameLog;

/**
 * デバッグ用の時間を計測するクラスです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/02/04_20:36:59<br>
 * @author Shinacho<br>
 */
public class StopWatch {

	/**
	 * 処理を開始した時刻です.
	 */
	private long startTime;
	/**
	 * 経過時間です.
	 */
	private float time;

	/**
	 * 新しいストップウォッチを作成します.
	 */
	public StopWatch() {
	}

	/**
	 * 現在の時刻を保存し、計測を開始します.
	 *
	 * @return このストップウォッチを返します。<br>
	 */
	public StopWatch start() {
		startTime = System.nanoTime();
		return this;
	}

	/**
	 * 計測を終了し、経過時間を保存します.
	 */
	public void stop() {
		time = (float) (System.nanoTime() - startTime) / 1000000.f;
	}

	/**
	 * 計測を終了し、経過時間を保存します.
	 *
	 * @param print trueを指定すると、標準出力に経過時間を表示します。<br>
	 */
	public void stop(boolean print) {
		stop();
		if (print) {
			GameLog.printIfUsing(Level.ALL, this.toString());
		}
	}

	/**
	 * 指定されたストリームにtoStringを発行します.
	 *
	 * @param stream 送信するストリームを指定します。<br>
	 */
	public void print(PrintStream stream) {
		stream.println(this);
	}

	public float getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "StopWatch{" + " time=" + time + "ms }";
	}

	@Override
	public StopWatch clone() {
		try {
			return (StopWatch) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}
}
