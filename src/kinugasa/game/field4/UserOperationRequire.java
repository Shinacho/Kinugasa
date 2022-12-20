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
package kinugasa.game.field4;

/**
 * �t�B�[���h�}�b�v�̏�Ԃɂ��A���[�U�����v�����܂��B
 *
 * @vesion 1.0.0 - 2022/12/12_18:04:27<br>
 * @author Dra211<br>
 */
public enum UserOperationRequire {

	/**
	 * �C�x���g�����s���܂��̂ŁA������󂯕t�����ɑҋ@���Ă��������BFMupdate�A move�����s�������Ă��������B
	 * FieldEventSystem������\�ɂȂ�܂őҋ@���Ă��������B
	 */
	WAIT_FOR_EVENT,
	/**
	 * �퓬�V�X�e���̋N�����v������܂����B�퓬���W�b�N�ɐ؂�ւ��Ă��������B �G���J�E���g���̓t�B�[���h�C�x���g�V�X�e������擾���Ă��������B
	 */
	TO_BATTLE,
	/**
	 * �Q�[���I�[�o�[���v������܂����B
	 */
	GAME_OVER,
	/**
	 * �}�b�v�ύX���v������܂����B
	 */
	CHANGE_MAP,
	/**
	 * ���b�Z�[�W�E�C���h�E�̕\�����v������܂����B�e�L�X�g��FieldEventSystem����擾���ĕ\�����Ă��������B
	 */
	SHOW_MESSAGE,
	CLOSE_MESSAGE,
	GET_ITEAM,
	/**
	 * �ʏ�̃t�B�[���h�}�b�v�ړ��𑱍s���Ă��������B
	 */
	CONTINUE,
	/**
	 * �t�F�[�h�A�E�g���v������܂����B�t�F�[�h�A�E�g�G�t�F�N�g���Đ����āA�I�������玟�̃C�x���g�����s���Ă��������B
	 */
	FADE_OUT,
	/**
	 * �t�F�[�h�C�����v������܂����B�t�F�[�h�C���G�t�F�N�g���Đ����āA�I�������玟�̃C�x���g�����s���Ă��������B
	 */
	FADE_IN,
	BLACKOUT,

}
