/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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

import java.util.Arrays;

/**
 * 複数の、呼び出し回数ベースの待機時間を順番に評価するTimeCounterの実装です.
 * <br>
 * このクラスは、TimeCounterの基本の実装です。たとえば、STGにおける射撃間隔の制御などに使用します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/11_18:35:20<br>
 * @author Dra0211<br>
 */
public class FrameTimeCounter extends TimeCounter {

	private static final long serialVersionUID = 8128288858943550667L;
	/** 現在のインデックスの待ち時間のカウンタです.
	 * この値が実際に計算されます。 */
	private int timeCount;
	/** カウンタから引く値です. */
	private int speed;
	/** 遷移するインデックスのモデルです. */
	private ArrayIndexModel index;
	/** 最初に設定されていた状態のインデックスのモデルです. */
	private ArrayIndexModel initialIndex;
	/** 待ち時間を格納する配列です. */
	private int[] waitTime;
	/** 実行中であるかを判定するフラグです. */
	private boolean running;

	/**
	 * 待ち時間を指定して、新しいカウンタを作成します.
	 * このコンストラクタでは、速度は1、インデックスは通常のシーケンシャルなインデックスが設定されます。<br>
	 *
	 * @param waitTime 待機時間を指定します。0を指定すると、常にtrueを返すモデルが、1を指定すると、2回目の呼び出しから交互に
	 * trueを返すモデルが作成されます。何も指定しない場合は、0になります。<br>
	 */
	public FrameTimeCounter(int... waitTime) {
		this(1, (waitTime.length == 0 ? new int[]{0} : waitTime));
	}

	/**
	 * 速度と待ち時間を指定して、新しいカウンタを作成します.
	 * このコンストラクタでは、インデックスは通常のシーケンシャルなインデックスが設定されます。<br>
	 *
	 * @param speed 待ち時間に対する遷移速度を指定します。たとえば、2を指定すると待ち時間から検査のたびに2が引かれ、
	 * 0以下になった場合に「時間切れ」と判定されます。<br>
	 * @param waitTime 待機時間を指定します。0を指定すると、常にtrueを返すモデルが、1を指定すると、2回目の呼び出しから交互に
	 * trueを返すモデルが作成されます。何も指定しない場合は、0になります。<br>
	 */
	public FrameTimeCounter(int speed, int[] waitTime) {
		this(speed, new SimpleIndex(), waitTime);
	}

	/**
	 * インデックスモデルと待ち時間を指定して、新しいカウンタを作成します.
	 * このコンストラクタでは、速度は1が設定されます。<br>
	 *
	 * @param index 待ち時間の配列に対するインデックスの遷移モデルを指定します。<br>
	 * @param waitTime 待機時間を指定します。0を指定すると、常にtrueを返すモデルが、1を指定すると、2回目の呼び出しから交互に
	 * trueを返すモデルが作成されます。何も指定しない場合は、0になります。<br>
	 */
	public FrameTimeCounter(ArrayIndexModel index, int... waitTime) {
		this(1, index, waitTime);
	}

	/**
	 * 速度、インデックスモデル、待ち時間を指定して新しいカウンタを作成します.
	 *
	 * @param speed 待ち時間に対する遷移速度を指定します。たとえば、2を指定すると待ち時間から検査のたびに2が引かれ、
	 * 0以下になった場合に「時間切れ」と判定されます。<br>
	 * @param index 待ち時間の配列に対するインデックスの遷移モデルを指定します。<br>
	 * @param waitTime 待機時間を指定します。0を指定すると、常にtrueを返すモデルが、1を指定すると、2回目の呼び出しから交互に
	 * trueを返すモデルが作成されます。何も指定しない場合は、0になります。<br>
	 */
	public FrameTimeCounter(int speed, ArrayIndexModel index, int... waitTime) {
		if (waitTime.length == 0) {
			waitTime = new int[]{0};
		}
		this.speed = speed;
		this.index = index;
		this.waitTime = waitTime;
		this.timeCount = waitTime[index.index(waitTime.length)];
		this.initialIndex = index.clone();
	}

	@Override
	public FrameTimeCounter clone() {
		FrameTimeCounter result = (FrameTimeCounter) super.clone();
		result.index = this.index.clone();
		result.waitTime = this.waitTime.clone();
		return result;
	}

	@Override
	public boolean isReaching() {
		running = true;
		timeCount -= speed;
		if (timeCount < 0) {
			timeCount = waitTime[index.index(waitTime.length)];
			return true;
		}
		return false;
	}

	public void initCount() {
		timeCount = waitTime[index.getIndex()];
	}

	public void setIndex(ArrayIndexModel index) {
		this.index = index;
		initCount();
	}

	public void setIndex(int index) {
		timeCount = waitTime[index];
	}

	public ArrayIndexModel getIndex() {
		return index;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getSpeed() {
		return speed;
	}

	public int getTimeCount() {
		return timeCount;
	}

	public void setTimeCount(int timeCount) {
		this.timeCount = timeCount;
	}

	public int[] getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int... waitTime) {
		this.waitTime = waitTime;
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
		index = initialIndex.clone();
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + this.timeCount;
		hash = 67 * hash + this.speed;
		hash = 67 * hash + (this.index != null ? this.index.hashCode() : 0);
		hash = 67 * hash + Arrays.hashCode(this.waitTime);
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
		final FrameTimeCounter other = (FrameTimeCounter) obj;
		if (this.timeCount != other.timeCount) {
			return false;
		}
		if (this.speed != other.speed) {
			return false;
		}
		if (this.index != other.index && (this.index == null || !this.index.equals(other.index))) {
			return false;
		}
		if (!Arrays.equals(this.waitTime, other.waitTime)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "FrameTimeCounter{" + "timeCount=" + timeCount + ", speed=" + speed + ", index=" + index + ", waitTime=" + Arrays.toString(waitTime) + '}';
	}
}
