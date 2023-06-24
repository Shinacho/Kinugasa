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
			GameLog.print(this.toString());
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
