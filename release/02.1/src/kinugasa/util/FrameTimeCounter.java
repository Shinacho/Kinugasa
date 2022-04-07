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
 * �����́A�Ăяo���񐔃x�[�X�̑ҋ@���Ԃ����Ԃɕ]������TimeCounter�̎����ł�.
 * <br>
 * ���̃N���X�́ATimeCounter�̊�{�̎����ł��B���Ƃ��΁ASTG�ɂ�����ˌ��Ԋu�̐���ȂǂɎg�p���܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/11_18:35:20<br>
 * @author Dra0211<br>
 */
public class FrameTimeCounter extends TimeCounter {

	private static final long serialVersionUID = 8128288858943550667L;
	/** ���݂̃C���f�b�N�X�̑҂����Ԃ̃J�E���^�ł�.
	 * ���̒l�����ۂɌv�Z����܂��B */
	private int timeCount;
	/** �J�E���^��������l�ł�. */
	private int speed;
	/** �J�ڂ���C���f�b�N�X�̃��f���ł�. */
	private ArrayIndexModel index;
	/** �ŏ��ɐݒ肳��Ă�����Ԃ̃C���f�b�N�X�̃��f���ł�. */
	private ArrayIndexModel initialIndex;
	/** �҂����Ԃ��i�[����z��ł�. */
	private int[] waitTime;
	/** ���s���ł��邩�𔻒肷��t���O�ł�. */
	private boolean running;

	/**
	 * �҂����Ԃ��w�肵�āA�V�����J�E���^���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A���x��1�A�C���f�b�N�X�͒ʏ�̃V�[�P���V�����ȃC���f�b�N�X���ݒ肳��܂��B<br>
	 *
	 * @param waitTime �ҋ@���Ԃ��w�肵�܂��B0���w�肷��ƁA���true��Ԃ����f�����A1���w�肷��ƁA2��ڂ̌Ăяo��������݂�
	 * true��Ԃ����f�����쐬����܂��B�����w�肵�Ȃ��ꍇ�́A0�ɂȂ�܂��B<br>
	 */
	public FrameTimeCounter(int... waitTime) {
		this(1, (waitTime.length == 0 ? new int[]{0} : waitTime));
	}

	/**
	 * ���x�Ƒ҂����Ԃ��w�肵�āA�V�����J�E���^���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A�C���f�b�N�X�͒ʏ�̃V�[�P���V�����ȃC���f�b�N�X���ݒ肳��܂��B<br>
	 *
	 * @param speed �҂����Ԃɑ΂���J�ڑ��x���w�肵�܂��B���Ƃ��΁A2���w�肷��Ƒ҂����Ԃ��猟���̂��т�2��������A
	 * 0�ȉ��ɂȂ����ꍇ�Ɂu���Ԑ؂�v�Ɣ��肳��܂��B<br>
	 * @param waitTime �ҋ@���Ԃ��w�肵�܂��B0���w�肷��ƁA���true��Ԃ����f�����A1���w�肷��ƁA2��ڂ̌Ăяo��������݂�
	 * true��Ԃ����f�����쐬����܂��B�����w�肵�Ȃ��ꍇ�́A0�ɂȂ�܂��B<br>
	 */
	public FrameTimeCounter(int speed, int[] waitTime) {
		this(speed, new SimpleIndex(), waitTime);
	}

	/**
	 * �C���f�b�N�X���f���Ƒ҂����Ԃ��w�肵�āA�V�����J�E���^���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A���x��1���ݒ肳��܂��B<br>
	 *
	 * @param index �҂����Ԃ̔z��ɑ΂���C���f�b�N�X�̑J�ڃ��f�����w�肵�܂��B<br>
	 * @param waitTime �ҋ@���Ԃ��w�肵�܂��B0���w�肷��ƁA���true��Ԃ����f�����A1���w�肷��ƁA2��ڂ̌Ăяo��������݂�
	 * true��Ԃ����f�����쐬����܂��B�����w�肵�Ȃ��ꍇ�́A0�ɂȂ�܂��B<br>
	 */
	public FrameTimeCounter(ArrayIndexModel index, int... waitTime) {
		this(1, index, waitTime);
	}

	/**
	 * ���x�A�C���f�b�N�X���f���A�҂����Ԃ��w�肵�ĐV�����J�E���^���쐬���܂�.
	 *
	 * @param speed �҂����Ԃɑ΂���J�ڑ��x���w�肵�܂��B���Ƃ��΁A2���w�肷��Ƒ҂����Ԃ��猟���̂��т�2��������A
	 * 0�ȉ��ɂȂ����ꍇ�Ɂu���Ԑ؂�v�Ɣ��肳��܂��B<br>
	 * @param index �҂����Ԃ̔z��ɑ΂���C���f�b�N�X�̑J�ڃ��f�����w�肵�܂��B<br>
	 * @param waitTime �ҋ@���Ԃ��w�肵�܂��B0���w�肷��ƁA���true��Ԃ����f�����A1���w�肷��ƁA2��ڂ̌Ăяo��������݂�
	 * true��Ԃ����f�����쐬����܂��B�����w�肵�Ȃ��ꍇ�́A0�ɂȂ�܂��B<br>
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
