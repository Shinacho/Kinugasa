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
package kinugasa.object;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * ���̓f�o�C�X�̒l�ɂ���ăX�v���C�g���ړ�������@���`���܂�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_16:50:04<br>
 * @author Dra0211<br>
 */
public interface Controllable {

	/**
	 * ���̓f�o�C�X����̒l�ɂ���āA�I�u�W�F�N�g���ړ����܂�.
	 *
	 * @param xValue -1.0~1.0�̊Ԃ̓��͒l.�L�[����ł�-1.0�܂���1.0�A�Q�[���p�b�h���g�p����Ɖς̒l�����M�����.<br>
	 * @param yValue -1.0~1.0�̊Ԃ̓��͒l.�L�[����ł�-1.0�܂���1.0�A�Q�[���p�b�h���g�p����Ɖς̒l�����M�����.<br>
	 * @param s ���̃I�u�W�F�N�g���ړ��\�ȗ̈�.null�łȂ��ꍇ�͂��̗̈悩��o�Ă͂Ȃ�Ȃ�.<br>
	 *
	 * @return �ړ���̃I�u�W�F�N�g��s���ɑ��݂���ꍇ��true���As����o��(contains()��false)���߁A�ړ��ł��Ȃ������ꍇ��false��Ԃ�.<br>
	 */
	public boolean move(float xValue, float yValue, Shape s);

	/**
	 * ���̓f�o�C�X����̒l�ɂ���āA�I�u�W�F�N�g���ړ����܂�.
	 *
	 * @param p -1.0~1.0�̊Ԃ�X�����Y���̓��͒l.�L�[����ł�-1.0�܂���1.0�A�Q�[���p�b�h���g�p����Ɖς̒l�����M�����.<br>
	 * @param s ���̃I�u�W�F�N�g���ړ��\�ȗ̈�.null�łȂ��ꍇ�͂��̗̈悩��o�Ă͂Ȃ�Ȃ�.<br>
	 *
	 * @return �ړ���̃I�u�W�F�N�g��s���ɑ��݂���ꍇ��true���As����o��(contains()��false)���߁A�ړ��ł��Ȃ������ꍇ��false��Ԃ�.<br>
	 */
	public boolean move(Point2D.Float p, Shape s);
}
