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
 * ARGB�e�v�f��ColorTransitionModel���܂Ƃ߂�N���X�ł�.
 * <br>
 * ���̃N���X��RGB�ƃA���t�@������4��ColorTransitionModel�������A�����S�Ă�
 * �X�V������A���݂̒l���g�p���ĐF���\�z�ł��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_19:23:37<br>
 * @author Shinacho<br>
 */
public class ColorChanger extends Model implements Statable {

	private static final long serialVersionUID = 7119780668500645887L;
	/** �Ԃ̑J�ڃA���S���Y���ł�. */
	private ColorTransitionModel red;
	/** �΂̑J�ڃA���S���Y���ł�. */
	private ColorTransitionModel green;
	/** �̑J�ڃA���S���Y���ł�. */
	private ColorTransitionModel blue;
	/** �A���t�@�����̑J�ڃA���S���Y���ł�. */
	private ColorTransitionModel alpha;

	/**
	 * RGB�̃A���S���Y�����w�肵�āA�V����ColorChanger���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A�A���t�@������255�Œ�ƂȂ�܂��B<br>
	 * @param red �Ԃ̑J�ڃA���S���Y���ł��B<br>
	 * @param green �΂̑J�ڃA���S���Y���ł��B<br>
	 * @param blue �̑J�ڃA���S���Y���ł��B<br>
	 */
	public ColorChanger(ColorTransitionModel red,
			ColorTransitionModel green,
			ColorTransitionModel blue) {
		this(red, green, blue, ColorTransitionModel.valueOf(255));
	}

	/**
	 * RGBA�̃A���S���Y�����w�肵�āA�V����ColorChanger���쐬���܂�.
	 * @param red �Ԃ̑J�ڃA���S���Y���ł��B<br>
	 * @param green �΂̑J�ڃA���S���Y���ł��B<br>
	 * @param blue �̑J�ڃA���S���Y���ł��B<br>
	 * @param alpha �A���t�@�����̑J�ڃA���S���Y���ł��B<br>
	 */
	public ColorChanger(ColorTransitionModel red,
			ColorTransitionModel green,
			ColorTransitionModel blue,
			ColorTransitionModel alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	/**
	 * �A���t�@�����̃A���S���Y�������^���܂�.
	 * @return �A���t�@�����̑J�ڃA���S���Y����Ԃ��܂��B<br>
	 */
	public ColorTransitionModel getAlpha() {
		return alpha;
	}

	/**
	 * �̃A���S���Y�������^���܂�.
	 * @return �̑J�ڃA���S���Y����Ԃ��܂��B<br>
	 */
	public ColorTransitionModel getBlue() {
		return blue;
	}

	/**
	 * �΂̃A���S���Y�������^���܂�.
	 * @return �΂̑J�ڃA���S���Y����Ԃ��܂��B<br>
	 */
	public ColorTransitionModel getGreen() {
		return green;
	}

	/**
	 * �Ԃ̃A���S���Y�������^���܂�.
	 * @return �Ԃ̑J�ڃA���S���Y����Ԃ��܂��B<br>
	 */
	public ColorTransitionModel getRed() {
		return red;
	}

	/**
	 * �A���t�@�����̃A���S���Y����ύX���܂�.
	 * @param alpha �A���t�@�����̑J�ڃA���S���Y�����w�肵�܂��B<br>
	 */
	public void setAlpha(ColorTransitionModel alpha) {
		this.alpha = alpha;
	}

	/**
	 * �̃A���S���Y����ύX���܂�.
	 * @param blue �̑J�ڃA���S���Y�����w�肵�܂��B<br>
	 */
	public void setBlue(ColorTransitionModel blue) {
		this.blue = blue;
	}

	/**
	 * �΂̃A���S���Y����ύX���܂�.
	 * @param green �΂̑J�ڃA���S���Y�����w�肵�܂��B<br>
	 */
	public void setGreen(ColorTransitionModel green) {
		this.green = green;
	}

	/**
	 * �Ԃ̃A���S���Y����ύX���܂�.
	 * @param red �Ԃ̑J�ڃA���S���Y�����w�肵�܂��B<br>
	 */
	public void setRed(ColorTransitionModel red) {
		this.red = red;
	}

	@Override
	public ColorChanger clone() {
		ColorChanger result = (ColorChanger) super.clone();
		result.red = this.red.clone();
		result.green = this.green.clone();
		result.blue = this.blue.clone();
		result.alpha = this.alpha.clone();
		return result;
	}
	/** �I�����Ă��邩�𔻒肷�邽�߂̃t���O�ł�. */
	private boolean ended = false;

	/**
	 * �S�ẴA���S���Y�����X�V���܂�.
	 * �S�ẴA���S���Y�����u�I����ԁv�ɂȂ����Ƃ��A����ColorChanger���I����ԂƂȂ�܂��B<br>
	 */
	public void update() {
		red.update();
		green.update();
		blue.update();
		alpha.update();
		ended = red.isEnded() && green.isEnded() && blue.isEnded() && alpha.isEnded();
	}

	@Override
	public boolean isEnded() {
		return ended;
	}

	@Override
	public boolean isRunning() {
		return !ended;
	}

	/**
	 * RGBA�̌��݂̒l���g�p���āA�F���\�z���܂�.
	 * @return ���݂̒l����쐬���ꂽ�F��Ԃ��܂��B<br>
	 */
	public Color createColor() {
		return ColorTransitionModel.createColor(red, green, blue, alpha);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 71 * hash + (this.red != null ? this.red.hashCode() : 0);
		hash = 71 * hash + (this.green != null ? this.green.hashCode() : 0);
		hash = 71 * hash + (this.blue != null ? this.blue.hashCode() : 0);
		hash = 71 * hash + (this.alpha != null ? this.alpha.hashCode() : 0);
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
		final ColorChanger other = (ColorChanger) obj;
		if (this.red != other.red && (this.red == null || !this.red.equals(other.red))) {
			return false;
		}
		if (this.green != other.green && (this.green == null || !this.green.equals(other.green))) {
			return false;
		}
		if (this.blue != other.blue && (this.blue == null || !this.blue.equals(other.blue))) {
			return false;
		}
		if (this.alpha != other.alpha && (this.alpha == null || !this.alpha.equals(other.alpha))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ColorChanger{" + "red=" + red + ", green=" + green + ", blue=" + blue + ", alpha=" + alpha + '}';
	}
}
