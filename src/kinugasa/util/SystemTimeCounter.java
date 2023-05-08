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
