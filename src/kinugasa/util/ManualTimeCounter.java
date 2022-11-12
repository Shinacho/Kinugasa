/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
 * �蓮���̃^�C���J�E���^�̎����ł��B update,add,sub�̊e���\�b�h�Œl��ύX���܂��A�l��0����������Ƃ��A�����ƂȂ�܂��B
 *
 * @vesion 1.0.0 - 2022/11/12_13:50:47<br>
 * @author Dra211<br>
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

}
