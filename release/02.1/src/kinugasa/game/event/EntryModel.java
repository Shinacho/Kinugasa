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
package kinugasa.game.event;

import java.io.Serializable;

/**
 * �G���g�����f���͂���C�x���g�����̎��_�ŋN�����邩�ǂ����𔻒肷��@�\�ł�.
 * <br>
 * <br>
 *
 * @version 1.0.0 - 2012/10/19_18:14:04.<br>
 * @author Dra0211<br>
 */
public interface EntryModel extends Serializable {

	/**
	 * �w�肵���C�x���g���A�����_�Ŏ��s�\�ł��邩�𔻒肵�܂�.
	 * �ʏ�̓C�x���g�}�l�[�W���̋@�\���g�p�ł���悤�ɁA�C�x���g�}�l�[�W���̎�����private final�ȃt�B�[���h�Ƃ���
	 * ��`���Ă��������B<br>
	 *
	 * @param evt ���肷��C�x���g�B<br>
	 *
	 * @return ���̃C�x���g�����s�ł���ꍇ��true�A�����łȂ��ꍇ��false��Ԃ��B<br>
	 */
	public abstract boolean isReaching(TimeEvent<?> evt);
}
