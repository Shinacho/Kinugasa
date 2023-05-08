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
package kinugasa.graphics;

import java.awt.Color;
import kinugasa.object.Model;
import kinugasa.object.Statable;

/**
 * �F��ARGB�̊e�v�f�̑J�ڃA���S���Y�����`���܂�.
 * <br>
 * �F�v�f��0����255�̐����Ƃ��ĕ\����܂��B
 * 1�̐F�́ARGB����RGBA��ColorTransitionModel����\������܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_18:59:50<br>
 * @author Shinacho<br>
 */
public abstract class ColorTransitionModel extends Model implements Statable {

	private static final long serialVersionUID = -5301732826729025440L;
	/** �J�E���^�Ƃ��Ďg�p�\�ȍŏ��̒l(0)�ł�. */
	public static final int MIN = 0;
	/** �J�E���^�Ƃ��Ďg�p�\�ȍő�̒l(255)�ł�. */
	public static final int MAX = 255;
	/** ���݂̒l�ł�. */
	protected int value;

	/**
	 * �����l���w�肵�āA�J�ڃA���S���Y�����`���܂�.
	 * @param value ���̃��f�����\���F�v�f�̏����l���w�肵�܂��B<br>
	 * @throws IllegalArgumentException �����l���F�v�f�Ƃ��ėL���Ȓl�łȂ��Ƃ��ɓ������܂��B<br>
	 */
	public ColorTransitionModel(int value) throws IllegalArgumentException {
		if (value < 0 || value > 255) {
			throw new IllegalArgumentException("illegal color value : value=[" + value + "]");
		}
		this.value = value;
	}

	//�J�E���g���Ȃ��Œ�J�E���^��Ԃ��B���̖߂�l��Staable�Ƃ��Ă͏I���Ȃ�
	/**
	 * �l���ύX����Ȃ��A���S���Y�����쐬���܂�.
	 * @param value �F�v�f�̒l���w�肵�܂��B<br>
	 * @return �w�肵���F�v�f�̒l���ύX����Ȃ����f����Ԃ��܂��B<br>
	 * @throws IllegalArgumentException �����l���F�v�f�Ƃ��ėL���Ȓl�łȂ��Ƃ��ɓ������܂��B<br>
	 */
	public static ColorTransitionModel valueOf(int value) throws IllegalArgumentException {
		return new ColorTransitionModel(value) {
			private static final long serialVersionUID = 8593451345811759766L;

			@Override
			public void update() {
			}

			@Override
			public boolean isEnded() {
				return true;
			}

			@Override
			public boolean isRunning() {
				return true;
			}
		};
	}

	@Override
	public abstract boolean isEnded();

	@Override
	public abstract boolean isRunning();

	/**
	 * ����̃A���S���Y���ɂ��������āA�l(value)��ύX���܂�.
	 * �l���A�L���͈�(0-255)����o��ꍇ�A�L���͈͓��ɐ؂�l�߂�K�v������܂��B<br>
	 * �܂��A�J�n����яI����Ԃ̕ύX���s���K�v������܂��B<br>
	 */
	public abstract void update();

	/**
	 * �l�̌��ݒl���擾���܂�.
	 * ���̃��\�b�h�ɂ���ĕԂ����l�́A����update���Ă΂��܂�
	 * �ύX����Ă͂Ȃ�܂���B<br>
	 * @return ���ݒl��Ԃ��܂��B<br>
	 */
	public int getValue() {
		return value;
	}

	/**
	 * �l��ݒ肷.
	 * @param value �V�����l�𑗐M���܂��B0�ȏ�255�ȉ����w�肵�܂��B<br
	 * @throws IllegalArgumentException �����l���F�v�f�Ƃ��ėL���Ȓl�łȂ��Ƃ��ɓ������܂��B<br>
	 */
	public void setValue(int value) throws IllegalArgumentException {
		if (value < 0 || value > 255) {
			throw new IllegalArgumentException("illegal color value : value=[" + value + "]");
		}
		this.value = value;
	}

	/**
	 * RGB�̑J�ڃA���S���Y���̌��݂̒l���g�p���āA�F���\�z���܂�.
	 * @param r �ԗv�f�̃A���S���Y���ł��B<br>
	 * @param g �΂̃A���S���Y���ł��B<br>
	 * @param b �f�̃A���S���Y���ł��B<br>
	 * @return �w�肳�ꂽ�A���S���Y���̌��݂̒l����\�z���ꂽ�F��Ԃ��܂��B�A���t�@������255�ƂȂ�܂��B<br>
	 */
	public static Color createColor(ColorTransitionModel r,
			ColorTransitionModel g,
			ColorTransitionModel b) {
		return new Color(r.value, g.value, b.value);
	}

	/**
	 * RGBA�J�ڃA���S���Y���̌��݂̒l���g�p���āA�F���\�z���܂�.
	 * @param r �ԗv�f�̃A���S���Y���ł��B<br>
	 * @param g �΂̃A���S���Y���ł��B<br>
	 * @param b �f�̃A���S���Y���ł��B<br>
	 * @param a �A���t�@�����̃A���S���Y���ł��B<br>
	 * @return �w�肳�ꂽ�A���S���Y���̌��݂̒l����\�z���ꂽ�F��Ԃ��܂��B<br>
	 */
	public static Color createColor(ColorTransitionModel r,
			ColorTransitionModel g,
			ColorTransitionModel b,
			ColorTransitionModel a) {
		return new Color(r.value, g.value, b.value, a.value);
	}

	@Override
	public ColorTransitionModel clone() {
		return (ColorTransitionModel) super.clone();
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + this.value;
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
		final ColorTransitionModel other = (ColorTransitionModel) obj;
		if (this.value != other.value) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ColorCounter{" + "value=" + value + '}';
	}
}
