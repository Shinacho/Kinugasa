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
package kinugasa.game.input;

import kinugasa.object.Model;


/**
 * ���̓f�o�C�X�̏�Ԃ��i�[���郂�f���̃X�[�p�[�N���X�ł�.
 * <br>
 * ���̓f�o�C�X�̏�Ԃ́A�N���[�j���O�\�ł��B
 * <br>
 * ��Ԃ͕ύX����Ȃ��悤�ɁA�f�[�^���J�v�Z��������K�v������܂��B<br>
 * <br>
 * @version 1.0.0 - 2013/04/20_21:27:02<br>
 * @author Dra0211<br>
 */
public abstract class InputDeviceState extends Model {

	/**
	 * �T�u�N���X����̂݃C���X�^���X���ł��܂�.
	 */
	protected InputDeviceState() {
	}

	/**
	 * �������_�ŁA���炩�̓��͂����邩�𒲂ׂ܂�.
	 *
	 * ���̃��\�b�h�ɂ���Č��������A�f�o�C�X�̗v�f�́A�����ɂ���ĈقȂ�܂��B<br>
	 *
	 * @return ���炩�̓��͂�����ꍇ��true��Ԃ��܂��B<br>
	 */
	public abstract boolean isAnyInput();

	/**
	 * �������_�ŁA���炩�̃{�^����������Ă��邩�𒲂ׂ܂�.
	 *
	 * ���̃��\�b�h�ɂ���Č��������A�f�o�C�X�̗v�f�́A�����ɂ���ĈقȂ�܂��B<br>
	 *
	 * @return ���炩�̃{�^����������Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	public abstract boolean isAnyButtonInput();

	/**
	 * �������_�ŁA�������͂���Ă��Ȃ����𒲂ׂ܂�.
	 *
	 * ���̃��\�b�h�ɂ���Č��������A�f�o�C�X�̗v�f�́A�����ɂ���ĈقȂ�܂��B<br>
	 *
	 * @return �������͂���Ă��Ȃ��ꍇ��true��Ԃ��܂��B<br>
	 */
	public abstract boolean isEmptyInput();

	@Override
	public InputDeviceState clone() {
		return (InputDeviceState) super.clone();
	}
}