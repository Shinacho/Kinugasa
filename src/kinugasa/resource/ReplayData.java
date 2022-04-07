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
import kinugasa.util.Random;

/**
 * ���̃N���X���g�p���āA�����_���V�[�h�Ɠ��͏�Ԃ𔭍s�^�������邱�Ƃ��o���܂�.
 * <br>
 * ���v���C�̃f�[�^�ʂ͓��͂̑����ɔ�Ⴕ�đ����Ȃ�܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/20_18:24:22<br>
 * @author Dra0211<br>
 */
public final class ReplayData extends Storage<ReplayInputState> implements Serializable {

	private static final long serialVersionUID = -7860006069807950463L;
	/** �g�p����Ă����V�[�h�ł�. */
	private long seed;

	/**
	 * �V�������v���C���쐬���܂�.
	 * FPS��60�A���Ԃ�5���ɂȂ�܂��B<br>
	 */
	public ReplayData() {
		this(60, 5);
	}

	/**
	 * �V�������v���C���쐬���܂�.
	 *
	 * @param fps    ���s���̃Q�[���̍ő�FSP���w�肵�܂��B�����f�[�^�ʂɉe�����܂��B<br>
	 * @param minute �Q�[���̎��s���Ԃ��w�肵�܂��B�����f�[�^�ʂɉe�����܂��B<br>
	 */
	public ReplayData(int fps, float minute) {
		super((int) (fps * minute));
	}

	/**
	 * �V�[�h�����������A���v���C�̕ۑ����J�n���܂�.
	 *
	 * @param seed �V�[�h���w�肵�܂��Bkinugasa Random�N���X�̃V�[�h������������܂��B<br>
	 *
	 * @return this�C���X�^���X��Ԃ��܂��B<br>
	 */
	public ReplayData recStart(long seed) {
		Random.initSeed(this.seed = seed);
		return this;
	}

	/**
	 * �K���ȃV�[�h���g�p���ă��v���C�̕ۑ����J�n���܂�.
	 *
	 * @return this�C���X�^���X��Ԃ��܂��B<br>
	 */
	public ReplayData recStart() {
		return recStart(System.nanoTime());
	}

	/**
	 * �ۑ����ꂽ�V�[�h���g�p���āA�����_���N���X�����������A���v���C�̍Đ����J�n���܂�.
	 */
	public void playStart() {
		Random.initSeed(seed);
	}

	/**
	 * �t���[�����Ɠ��͏�Ԃ��w�肵�āA���̓C�x���g��o�^���܂�.
	 * ���̃��\�b�h��add(new ReplayInputState(frame, inputState))�Ɠ�����������܂��B<br>
	 *
	 * @param frame      ���͂����m���ꂽ�t���[�����w�肵�܂��BGameTimeManager����擾�ł��܂��B<br>
	 * @param inputState ���͏�Ԃ𑗐M���܂��BReplayInputState�ŃN���[�j���O����܂��B<br>
	 */
	public void add(long frame, InputState inputState) {
		super.add(new ReplayInputState(frame, inputState));
	}

	/**
	 * ���݂̃t���[���œ��͂��ꂽ��Ԃ𕜌����܂�.
	 *
	 * @param frame ���͂��s���t���[�����w�肵�܂��BGameTimeManager����擾�ł��܂��B<br>
	 *
	 * @return �w�肳�ꂽ�t���[���̓��͏�Ԃ�Ԃ��܂��B���̃t���[���ɓ��͂����������ꍇ��null��Ԃ��܂��B<br>
	 */
	public InputState get(long frame) {
		ReplayInputState state = super.getIfContains(Long.toString(frame));
		return state == null ? null : state.getInputState();
	}
}
