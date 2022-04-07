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
package kinugasa.graphics;

/**
 * �F�v�f���t�F�[�h�C���^�t�F�[�h�A�E�g���邽�߂�ColorCounter�̎����ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_19:07:48<br>
 * @author Dra0211<br>
 */
public class FadeCounter extends ColorTransitionModel {

	private static final long serialVersionUID = -1609450992263652227L;
	/** �t�F�[�h�̑��x�ł��B�J�E���^�̌��ݒl�ɑ�����܂�. */
	private int speed;

	/**
	 * �����l�ƑJ�ڑ��x���w�肵�āA�t�F�[�h�J�E���^���쐬���܂�.
	 * @param value �J�E���^�̏����l�ł��B0����255�̊ԂŎw�肵�܂��B<br>
	 * @param speed �J�E���^�̑J�ڑ��x�ł��B�������w��ł��܂��B<br>
	 * @throws IllegalArgumentException �����l��0�������A255�𒴂���ꍇ�ɓ������܂��B<br>
	 */
	public FadeCounter(int value, int speed) throws IllegalArgumentException {
		super(value);
		this.speed = speed;
	}

	/**
	 * 0����J�n����J�E���^���쐬���܂�.
	 * @param speed �J�E���^�̑J�ڑ��x���w�肵�܂��B
	 * ���̃��\�b�h�ł́A�ʏ�͐������w�肵�܂��B<br>
	 * @return �t�F�[�h�C���p�̃J�E���^���쐬���܂��B<br>
	 */
	public static FadeCounter fadeIn(int speed) {
		return new FadeCounter(MIN, speed);
	}

	/**
	 * 255����J�n����J�E���^���쐬���܂�.
	 * @param speed �J�E���^�̑J�ڑ��x���w�肵�܂��B
	 * ���̃��\�b�h�ł́A�ʏ�͕������w�肵�܂��B<br>
	 * @return �t�F�[�h�A�E�g�p�̃J�E���^���쐬���܂��B<br>
	 */
	public static FadeCounter fadeOut(int speed) {
		return new FadeCounter(MAX, speed);
	}

	@Override
	public void update() {
		if (ended) {
			return;
		}
		started = true;
		value += speed;
		if (value < MIN) {
			value = MIN;
			ended = true;
		} else if (value > MAX) {
			value = MAX;
			ended = true;
		}
	}
	/** �u�J�n���Ă���v��ԃt���O�ł�.
	 * �ŏ���update���Ăяo���ƁAtrue�ɂȂ�܂��B
	 * ����ȍ~�́A���true��Ԃ��܂��B */
	private boolean started = false;
	/** �u�I�����Ă���v��ԃt���O�ł�.
	 * �J�E���^�̒l���L���͈͂���o���
	 * true�ɂȂ�܂��B����ȍ~�͏��true��Ԃ��܂��B */
	private boolean ended = false;

	/**
	 * {@inheritDoc }
	 * <br>
	 * ���̎����ł͍ŏ���update���Ăяo���ƁA����ȍ~�͏�Ɂu�J�n���Ă����ԁv�ƂȂ�܂��B<br>
	 */
	@Override
	public boolean isRunning() {
		return started;
	}

	/**
	 * {@inheritDoc }
	 * <br>
	 * ���̎����ł́Aupdate���\�b�h�ɂ���ăJ�E���^�̒l���L���͈͂���o���
	 * ����ȍ~�͏�Ɂu�I�����Ă����ԁv�ƂȂ�܂��B<br>
	 */
	@Override
	public boolean isEnded() {
		return ended;
	}

	@Override
	public String toString() {
		return "ColorFadeCounter{" + "value=" + value + ", " + "speed=" + speed + '}';
	}

	@Override
	public FadeCounter clone() {
		return (FadeCounter) super.clone();
	}
}
