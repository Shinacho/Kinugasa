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
 * �f�o�b�O�p�̎��Ԃ��v������N���X�ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/02/04_20:36:59<br>
 * @author Shinacho<br>
 */
public class StopWatch {

	/**
	 * �������J�n���������ł�.
	 */
	private long startTime;
	/**
	 * �o�ߎ��Ԃł�.
	 */
	private float time;

	/**
	 * �V�����X�g�b�v�E�H�b�`���쐬���܂�.
	 */
	public StopWatch() {
	}

	/**
	 * ���݂̎�����ۑ����A�v�����J�n���܂�.
	 *
	 * @return ���̃X�g�b�v�E�H�b�`��Ԃ��܂��B<br>
	 */
	public StopWatch start() {
		startTime = System.nanoTime();
		return this;
	}

	/**
	 * �v�����I�����A�o�ߎ��Ԃ�ۑ����܂�.
	 */
	public void stop() {
		time = (float) (System.nanoTime() - startTime) / 1000000.f;
	}

	/**
	 * �v�����I�����A�o�ߎ��Ԃ�ۑ����܂�.
	 *
	 * @param print true���w�肷��ƁA�W���o�͂Ɍo�ߎ��Ԃ�\�����܂��B<br>
	 */
	public void stop(boolean print) {
		stop();
		if (print) {
			GameLog.printIfUsing(Level.ALL, this.toString());
		}
	}

	/**
	 * �w�肳�ꂽ�X�g���[����toString�𔭍s���܂�.
	 *
	 * @param stream ���M����X�g���[�����w�肵�܂��B<br>
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
