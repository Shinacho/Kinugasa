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
