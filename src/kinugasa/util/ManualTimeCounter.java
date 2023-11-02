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
 * 手動式のタイムカウンタの実装です。 update,add,subの各メソッドで値を変更します、値が0を下回ったとき、完了となります。
 *
 * @vesion 1.0.0 - 2022/11/12_13:50:47<br>
 * @author Shinacho<br>
 */
public class ManualTimeCounter extends TimeCounter {

	private int currentTime;
	private int initialTime;

	public static final ManualTimeCounter FALSE = new ManualTimeCounter(0) {
		@Override
		public boolean isReaching() {
			return false;
		}

		@Override
		public boolean isEnded() {
			return false;
		}

		@Override
		public boolean isRunning() {
			return false;
		}

	};

	public ManualTimeCounter(int time) {
		currentTime = initialTime = time;
	}

	public void update(int x) {
		add(x);
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public int getInitialTime() {
		return initialTime;
	}

	public void add(int x) {
		currentTime += x;
	}

	public void sub(int x) {
		currentTime -= x;
	}

	@Override
	public boolean isReaching() {
		return currentTime <= 0;
	}

	@Override
	public boolean isEnded() {
		return currentTime <= 0;
	}

	@Override
	public boolean isRunning() {
		return currentTime != initialTime;
	}

	@Override
	public void reset() {
		currentTime = initialTime;
	}

	public void setCurrentTime(int r) {
		this.currentTime = r;
	}

	@Override
	public String toString() {
		return "ManualTimeCounter{" + "currentTime=" + currentTime + '}';
	}

}
