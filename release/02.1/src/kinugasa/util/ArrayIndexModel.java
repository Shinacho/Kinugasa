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

import kinugasa.object.Model;

/**
 * �z��̃C���f�b�N�X�̑J�ڏ������`���܂�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_15:42:02<br>
 * @author Dra0211<br>
 */
public abstract class ArrayIndexModel extends Model {

	private static final long serialVersionUID = -6398853184708789620L;

	/**
	 * ����̃A���S���Y���ɂ��������āA�z��̃C���f�b�N�X��Ԃ��܂�.
	 *
	 * ���̃��\�b�h�ł́A�C���f�b�N�X�̑J�ڂ������ōs���܂��B<br>
	 * ���������āA���̌Ăяo���ł̓A���S���Y���ɉ�����
	 * �u���̒l�v��Ԃ��܂��B<br>
	 *
	 * @param arrayLength �z��̒���(length)�𑗐M���܂��B<br>
	 *
	 * @return �z��̃C���f�b�N�X��Ԃ��܂��B�C���f�b�N�X��0����arrayLength-1�ł���K�v������܂��B<br>
	 */
	public abstract int index(int arrayLength);

	/**
	 * ���݂̃C���f�b�N�X��Ԃ��܂�.
	 *
	 * ���̃��\�b�h�ł́A�C���f�b�N�X��ύX�����ɁA���ݒl��Ԃ��܂��B<br>
	 *
	 * @return �C���f�b�N�X�̌��ݒl��Ԃ��܂��B<br>
	 */
	public abstract int getIndex();

	@Override
	public ArrayIndexModel clone() {
		return (ArrayIndexModel) super.clone();
	}
}
