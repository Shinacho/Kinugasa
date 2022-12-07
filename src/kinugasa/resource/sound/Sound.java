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
package kinugasa.resource.sound;

import kinugasa.resource.Input;
import kinugasa.resource.InputStatus;
import kinugasa.resource.Nameable;
import kinugasa.resource.NotYetLoadedException;

/**
 * �T�E���h�t�@�C���̍Đ����~�Ȃǂ̋@�\�𒊏ۉ����邽�߂̃C���^�[�t�F�[�X�ł�.
 * <br>
 * �S�ẴT�E���h�f�[�^�́A���̃C���^�[�t�F�[�X����������K�v������܂��B<br>
 * �ʏ�̎����ł́A���[�h����Ă��Ȃ��T�E���h�ɑ΂��鑀��́A�����s���܂���B<br>
 * �܂��A���łɃ��[�h����Ă���ꍇ�ɍēx���[�h���邱�Ƃ͂���܂���B<br>
 * <br>
 * �T�E���h�̖��O�Ƃ́A�ʏ�́A�p�X���������t�@�C�����ƂȂ�܂��B<br>
 * ���Ƃ��΁Ahoge/piyo/fuga.wav�̏ꍇ��fuga.wav�����O�ƂȂ�܂��B<br>
 * <br>
 *
 *
 * @version 1.0.0 - 2013/01/13_18:44:36<br>
 * @author Dra0211<br>
 */
public interface Sound extends Input<Sound>, Nameable {

	@Override
	public String getName();

	/**
	 * �T�E���h�̍Đ����J�n���܂�. �T�E���h�����[�v�@�\���T�|�[�g���Ă���ꍇ�́A�ݒ�ɂ���Ă̓��[�v�Đ����J�n���܂�.<Br>
	 * �T�E���h�����ɍĐ�����Ă���ꍇ�͉������܂���.<br>
	 *
	 * @throws NotYetLoadedException �T�E���h�����[�h����Ă��Ȃ��ꍇ�ɓ����邱�Ƃ��ł��܂��B<br>
	 */
	public void play() throws NotYetLoadedException;
	
	public void nonLoopPlay();

	public void stopAndPlay();

	/**
	 * �T�E���h�̌��݂̃t���[���ʒu��Ԃ��܂�.
	 *
	 * @return ���݂̃t���[���ʒu.<br>
	 */
	public long getFramePosition();

	/**
	 * ���̃T�E���h�̃t���[���̍ő咷��Ԃ��܂�.
	 *
	 * @return �t���[���̍ő咷.<br>
	 */
	public long getFrameLength();

	/**
	 * �T�E���h�̍Đ����~���čŏ��܂Ŋ����߂��܂�.
	 */
	public void stop();

	/**
	 * �T�E���h�̍Đ����ꎞ��~���܂�. ����play���Ăяo���ꂽ�Ƃ��Apause���Ă΂ꂽ�ʒu����Đ����܂�.<br>
	 */
	public void pause();

	public default void switchPause() {
		if (isPlaying()) {
			pause();
		} else {
			play();
		}
	}

	@Override
	public InputStatus getStatus();

	public boolean isPlaying();

	/**
	 * �T�E���h�̉��ʂ�ݒ肵�܂�.
	 *
	 * @param vol �V��������.0f�Ŗ����ɂȂ�.<br>
	 */
	public void setVolume(float vol);

	@Override
	public Sound load();

	@Override
	public void dispose();

}
