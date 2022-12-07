/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.system;

/**
 *
 * @vesion 1.0.0 - 2022/11/29_21:31:59<br>
 * @author Dra211<br>
 */
public enum OperationResult {
	/**
	 * �s�������s����A����I���������Ƃ�\���܂��B���̎�Ԃɑ���܂����AINFO�܂���ACTION���b�Z�[�W���\������Ă���Ԃ͑ҋ@���Ă��������B
	 */
	SUCCESS,
	/**
	 * �s���̓L�����Z������܂����B�ēx�R�}���h��I���ł��܂��B
	 */
	CANCEL,
	/**
	 * �ړ����v������܂����B���̃��W�b�N�ɐ؂�ւ��Ă��������B
	 * after�E�C���h�E���\������Ă���B
	 * �ړ����ɍU������ꍇ�́AexecPCAction�����s����B
	 */
	MOVE,
	/**
	 * �X�e�[�^�X��ʂ̕\�����v������܂����B���̃��W�b�N�ɐ؂�ւ��Ă��������B
	 */
	SHOW_STATUS,
	/**
	 * �s���͎��s����܂��������s���܂����B���̎�Ԃɑ���܂����AINFO�܂���ACTION���b�Z�[�W���\������Ă���Ԃ͑ҋ@���Ă��������B
	 */
	MISS,
	/**
	 * �^�[�Q�b�g�I�����v������܂����B���̃��W�b�N�ɐ؂�ւ��Ă��������B
	 */
	TO_TARGET_SELECT,;

}
