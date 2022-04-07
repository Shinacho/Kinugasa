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


/**
 * �w�肳�ꂽ���O�����I�u�W�F�N�g��������Ȃ��ꍇ��
 * ���������O�ł�.
 * <br>
 *
 * <br>
 * @version 1.0.0 - 2013/04/21_13:16:00<br>
 * @author Dra0211<br>
 */
public class NameNotFoundException extends NotFoundException {


	/**
	 * �V���� NameNotFoundException �̃C���X�^���X���쐬���܂�.
	 */
	public NameNotFoundException() {
	}

	/**
	 * �V���� NameNotFoundException �̃C���X�^���X���쐬���܂�.
	 * @param msg ���̗�O�̃��b�Z�[�W���w�肵�܂��B<br>
	 */
	public NameNotFoundException(String msg) {
		super(msg);
	}

	/**
	 * Throwable�����b�v����NameNotFoundException���쐬���܂�.
	 * @param t ������ꂽ��O�𑗐M���܂��B<br>
	 */
	public NameNotFoundException(Throwable t) {
		super(t);
	}
}
