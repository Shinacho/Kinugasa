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
 * システム時計の経過時間によって待機時間を評価するTimeCounterの実装です.
 * <br>
 * 全ての時間はミリ秒単位です。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_14:46:33<br>
 * @author Shinacho<br>
 */
public class SystemTimeCounter extends TimeCounter {

	private static final long serialVersionUID = -4734476685297706601L;
	/**
	 * 待機が終了する時刻です.
	 */
	private long endTime;
	/**
	 * 待機する時間（ミリ秒）です.
	 */
	private long waitTime;
	/**
	 * 最初の呼び出しを検知するフラグです.
	 */
	private boolean running = false;

	public SystemTimeCounter(long milliSec) {
		this.endTime = System.currentTimeMillis() + milliSec;
		this.waitTime = milliSec;
	}

	@Override
	public boolean isReaching() {
		running = true;
		if (System.currentTimeMillis() > endTime) {
			endTime = System.currentTimeMillis() + waitTime;
			return true;
		}
		return false;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
		this.endTime = System.currentTimeMillis() + waitTime;
	}

	public long getWaitTime() {
		return waitTime;
	}

	@Override
	public boolean isEnded() {
		return isReaching();
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void reset() {
		this.endTime = System.currentTimeMillis() + waitTime;
	}

	@Override
	public int getCurrentTime() {
		return (int) (System.currentTimeMillis() - endTime);
	}

	@Override
	public SystemTimeCounter clone() {
		return (SystemTimeCounter) super.clone();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 47 * hash + (int) (this.endTime ^ (this.endTime >>> 32));
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
		final SystemTimeCounter other = (SystemTimeCounter) obj;
		if (this.endTime != other.endTime) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SystemTimeCounter{" + "endTime=" + endTime + '}';
	}
}
