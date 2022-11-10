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
package kinugasa.resource;

import java.io.Serializable;
import kinugasa.game.input.InputState;


/**
 * ���̃N���X��InputState�̃N���[�������b�v���A���v���C�Ƃ��ĕۑ��ł���悤�ɂ��܂�.
 * <br>
 * ���̃N���X�ł́AInputState��Nameable�̋@�\�Ƃ��āA���͂��ꂽ�t���[������ۑ�����@�\���ǉ�����܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/20_19:14:08<br>
 * @author Dra0211<br>
 */
public final class ReplayInputState implements Serializable, Nameable {

	/** ���͂����؂����^�C�~���O�ŁA�ʏ�͋N������̌o�߃t���[�����ł�. */
	private long time;
	/** ���̎��_�ł̓��͏�Ԃł�. */
	private InputState inputState;

	/**
	 * �V�������v���C�p���͏�Ԃ��쐬���܂�.
	 *
	 * @param time
	 * @param inputState ���̎��_�ł̓��͏�Ԃ𑗐M���܂��B
	 */
	public ReplayInputState(long time, InputState inputState) {
		this.time = time;
		this.inputState = inputState.clone();
	}

	/**
	 * ���͏�Ԃ��擾���܂�.
	 *
	 * @return ���͏�Ԃ�Ԃ��܂��B<br>
	 */
	public InputState getInputState() {
		return inputState;
	}

	/**
	 * �t���[�������擾���܂�.
	 *
	 * @return �t���[����Ԃ��܂��B<br>
	 */
	public long getTime() {
		return time;
	}

	@Override
	public String getName() {
		return Long.toString(time);
	}

	@Override
	public String toString() {
		return "ReplayInputState{" + "time=" + time + ", inputState=" + inputState + '}';
	}
}