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
 * �R���e���c��j������@�\���`���܂�.
 * <br>
 * ���̃C���^�[�t�F�[�X�́u�ă��[�h�v���邱�Ƃ��Ȃ��N���X�Ɏ�������܂��B<Br>
 * �����̃N���X�͕K�v�ɂȂ����Ƃ������C���X�^���X������A�s�v�ɂȂ����Ƃ���
 * dispose���\�b�h���g�p���čċA�I�ɊJ������܂��B<br>
 * �u�ꎞ�I�Ƀ��������J�����A�ă��[�h����Ƃ��ɔ�����vFreeable�Ƃ�
 * �ʂ̎g�p���@�ł���_�ɒ��ӂ��Ă��������B<br>
 * <br>
 * @version 1.0.0 - 2013/05/05_17:54:26<br>
 * @author Dra0211<br>
 */
public interface Disposable {

	/**
	 * �R���e���c������������j�����܂�.
	 */
	public void dispose();
}
