/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
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
package kinugasa.game;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:48:38<br>
 * @author Shinacho<br>
 */
public interface GameWindow {

	/**
	 * �E�C���h�E��X���W��ݒ肵�܂�.
	 *
	 * @param x X���W.<br>
	 */
	public void setX(int x);

	/**
	 * �E�C���h�E��X���W��ݒ肵�܂�.
	 *
	 * @param y Y���W.<br>
	 */
	public void setY(int y);

	/**
	 * �E�C���h�E��X���W���擾���܂�.
	 *
	 * @return �E�C���h�E��X���W.<br>
	 */
	public int getX();

	/**
	 * �E�C���h�E��Y���W���擾���܂�.
	 *
	 * @return �E�C���h�E��Y���W.<br>
	 */
	public int getY();

	/**
	 * �E�C���h�E�̕����擾���܂�.
	 *
	 * @return �E�C���h�E�̕�.<Br>
	 */
	public int getWidth();

	/**
	 * �E�C���h�E�̍������擾���܂�.
	 *
	 * @return �E�C���h�E�̍���.<Br>
	 */
	public int getHeight();

	/**
	 * �E�C���h�E�̈ʒu��ݒ肵�܂�.
	 *
	 * @param x �E�C���h�E��X���W.<br>
	 * @param y �E�C���h�E��Y���W.<br>
	 */
	public void setLocation(int x, int y);

	/**
	 * �E�C���h�E�̈ʒu��ݒ肵�܂�.
	 *
	 * @param p �E�C���h�E�̍��W.<br>
	 */
	public void setLocation(Point p);

	/**
	 * �E�C���h�E�̈ʒu���擾���܂�.
	 *
	 * @return �E�C���h�E�̈ʒu.<br>
	 */
	public Point getLocation();

	/**
	 * �E�C���h�E�̗̈���擾���܂�. ���̃��\�b�h�ł́A�E�C���h�E�̃{�[�_���܂񂾃E�C���h�E�S�̗̂̈��Ԃ��܂��B<br>
	 *
	 * @return �E�C���h�E�̈ʒu����уT�C�Y��Ԃ��܂��B<br>
	 */
	public Rectangle getBounds();

	/**
	 * �E�C���h�E�̎g�p�\�ȓ����̈���擾���܂�. ���̃��\�b�h�ł́A�E�C���h�E�̃{�[�_���܂܂�Ȃ����ۂɕ`��\�ȗ̈悪�Ԃ���܂��B<br>
	 * �ʏ�A���̗̈�̈ʒu��0,0�ɒ�������Ԃ���܂��B<br>
	 *
	 * @return �E�C���h�E���̎g�p�\�ȗ̈��Ԃ��܂��B<br>
	 */
	public Rectangle getInternalBounds();

	/**
	 * �E�C���h�E�̘g�̕���Ԃ��܂�.
	 *
	 * @return �E�C���h�E�̃{�[�_�̕���Ԃ��܂��B<Br>
	 */
	public Insets getInsets();

	/**
	 * �E�C���h�E�̔w�i�F��ݒ肵�܂�.
	 *
	 * @param color �w�i�F�B<br>
	 */
	public void setBackground(Color color);

	/**
	 * �E�C���h�E�̔w�i�F���擾���܂��B<br>
	 *
	 * @return �E�C���h�E�̔w�i�F�B<br>
	 */
	public Color getBackground();

	/**
	 * �E�C���h�E�ɐݒ肳��Ă���A�C�R�����擾���܂�.
	 *
	 * @return �E�C���h�E�̃A�C�R���B<br>
	 */
	public Image getIconImage();

	/**
	 * �E�C���h�E�̃A�C�R����ύX���܂�.
	 *
	 * @param image �E�C���h�E�̃A�C�R���B<br>
	 */
	public void setIconImage(Image image);

	/**
	 * �E�C���h�E�̃^�C�g���o�[�̕�������擾���܂�.
	 *
	 * @return �E�C���h�E�̃^�C�g���o�[�ɕ\������Ă��镶�����Ԃ��܂��B<br>
	 */
	public String getTitle();

	/**
	 * �E�C���h�E�̃^�C�g���o�[��ݒ肵�܂�.
	 *
	 * @param title �E�C���h�E�̃^�C�g���o�[�ɕ\�����镶������w�肵�܂��B<br>
	 */
	public void setTitle(String title);

	/**
	 * �E�C���h�E���I������Ă���A�\�����ł��邩���������܂�. �ʏ�A�E�C���h�E���A�N�e�B�u�łȂ��ꍇ�̓Q�[���̐i�s�͒�~���܂��B<br>
	 *
	 * @return �E�C���h�E���A�N�e�B�u�ł���ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean isActive();

	/**
	 * �}�E�X�J�[�\����ݒ肵�܂�.
	 *
	 * @param cursor �V�����J�[�\�����w�肵�܂��B<br>
	 */
	public void setCursor(Cursor cursor);

	/**
	 * ���݂̃}�E�X�J�[�\�����擾���܂�.
	 *
	 * @return �g�p���̃}�E�X�J�[�\�����Ԃ���܂��B<br>
	 */
	public Cursor getCursor();

}
