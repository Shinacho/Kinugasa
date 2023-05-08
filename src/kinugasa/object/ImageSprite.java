/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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

import java.awt.Image;
import java.awt.image.BufferedImage;
import kinugasa.game.GraphicsContext;
import kinugasa.resource.KImage;

/**
 * �P��̉摜��\�������{�X�v���C�g�̎����ł�.
 * <br>
 * �摜�X�v���C�g�ɐݒ肳���摜�́ASerializableImage�Ƀ��b�v����܂��B<br>
 * ���̉摜�^�̓X�v���C�g���ƃV���A���C�Y�ł��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:21:03<br>
 * @version 1.4.0 - 2013/05/05_19:25<br>
 * @author Shinacho<br>
 */
public class ImageSprite extends BasicSprite {

	private KImage image;
	private ImagePainter painter;

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�. ���̃R���X�g���N�^�ł́A�摜��null�ɁA �`�惂�f����IMAGE_BOUNDS_XY�ɐݒ肳��܂��B<br>
	 */
	public ImageSprite() {
		super();
		this.image = null;
		this.painter = ImagePainterStorage.IMAGE_BOUNDS_XY;
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�. ���̃R���X�g���N�^�ł́A�摜��null�ɁA �`�惂�f����IMAGE_BOUNDS_XY�ɐݒ肳��܂��B<br>
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 */
	public ImageSprite(float x, float y, float w, float h) {
		super(x, y, w, h);
		this.image = null;
		this.painter = ImagePainterStorage.IMAGE_BOUNDS_XY;
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�. ���̃R���X�g���N�^�ł́A �`�惂�f����IMAGE_BOUNDS_XY�ɐݒ肳��܂��B<br>
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param image �\������摜���w�肵�܂��B<br>
	 */
	public ImageSprite(float x, float y, float w, float h, KImage image) {
		super(x, y, w, h);
		this.image = image;
		this.painter = ImagePainterStorage.IMAGE_BOUNDS_XY;
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�.
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param image �\������摜���w�肵�܂��B<br>
	 * @param model �`����@���w�肵�܂��B<br>
	 */
	public ImageSprite(float x, float y, float w, float h, KImage image, ImagePainter model) {
		super(x, y, w, h);
		this.image = image;
		this.painter = model;
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�.
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param vector �ړ��x�N�g�����w�肵�܂��B<br>
	 * @param mm �ړ����f�����w�肵�܂��B<br>
	 * @param image �\������摜���w�肵�܂��B<br>
	 * @param dm �`�惂�f�����w�肵�܂��B<br>
	 */
	public ImageSprite(float x, float y, float w, float h, KVector vector,
			MovingModel mm, KImage image, ImagePainter dm) {
		super(x, y, w, h, vector, mm);
		this.image = image;
		this.painter = dm;
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�. ���̃R���X�g���N�^�̓N���[�j���O�p�̃}�X�^�f�[�^���쐬����ꍇ�ɗL�p�ł��B<br>
	 *
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param vector �ړ��x�N�g�����w�肵�܂��B<br>
	 * @param mm �ړ����f�����w�肵�܂��B<br>
	 * @param image �\������摜���w�肵�܂��B<br>
	 * @param dm �`�惂�f�����w�肵�܂��B<br>
	 */
	public ImageSprite(float w, float h, KVector vector,
			MovingModel mm, KImage image, ImagePainter dm) {
		super(w, h, vector, mm);
		this.image = image;
		this.painter = dm;
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�. ���̃R���X�g���N�^�ł́A �`�惂�f����IMAGE_BOUNDS_XY�ɐݒ肳��܂��B<br>
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param image �\������摜���w�肵�܂��B<br>
	 */
	public ImageSprite(float x, float y, float w, float h, BufferedImage image) {
		super(x, y, w, h);
		this.image = new KImage(image);
		this.painter = ImagePainterStorage.IMAGE_BOUNDS_XY;
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�.
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param image �\������摜���w�肵�܂��B<br>
	 * @param model �`����@���w�肵�܂��B<br>
	 */
	public ImageSprite(float x, float y, float w, float h, BufferedImage image, ImagePainter model) {
		super(x, y, w, h);
		this.image = new KImage(image);
		this.painter = model;
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�.
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param vector �ړ��x�N�g�����w�肵�܂��B<br>
	 * @param mm �ړ����f�����w�肵�܂��B<br>
	 * @param image �\������摜���w�肵�܂��B<br>
	 * @param dm �`�惂�f�����w�肵�܂��B<br>
	 */
	public ImageSprite(float x, float y, float w, float h, KVector vector,
			MovingModel mm, BufferedImage image, ImagePainter dm) {
		super(x, y, w, h, vector, mm);
		this.image = new KImage(image);
		this.painter = dm;
	}

	public ImageSprite(float w, float h, BufferedImage image) {
		this(0, 0, w, h, image);
	}

	/**
	 * �V�����摜�X�v���C�g���쐬���܂�. ���̃R���X�g���N�^�̓N���[�j���O�p�̃}�X�^�f�[�^���쐬����ꍇ�ɗL�p�ł��B<br>
	 *
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param vector �ړ��x�N�g�����w�肵�܂��B<br>
	 * @param mm �ړ����f�����w�肵�܂��B<br>
	 * @param image �\������摜���w�肵�܂��B<br>
	 * @param dm �`�惂�f�����w�肵�܂��B<br>
	 */
	public ImageSprite(float w, float h, KVector vector,
			MovingModel mm, BufferedImage image, ImagePainter dm) {
		super(w, h, vector, mm);
		this.image = new KImage(image);
		this.painter = dm;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		if (image == null) {
			return;
		}
		painter.draw(g, this);
	}

	public KImage getImage() {
		return image;
	}

	public Image getAWTImage() {
		return image.get();
	}

	/**
	 * �摜�̕����擾���܂�. �摜�̃T�C�Y�͉摜�C���X�^���X�ɐݒ肳��Ă���A�s�N�Z���P�ʂ̃T�C�Y�ł��B<br>
	 * ���̒l�͕`�惂�f���ɂ���Ă͖�������A���ۂ̃X�v���C�g�̃T�C�Y�Ƃ͈Ⴄ�ꍇ������܂��B<br>
	 *
	 * @return �摜�̕���Ԃ��܂��B<br>
	 */
	public int getImageWidth() {
		return image.getWidth();
	}

	/**
	 * �摜�̍������擾���܂�. �摜�̃T�C�Y�͉摜�C���X�^���X�ɐݒ肳��Ă���A�s�N�Z���P�ʂ̃T�C�Y�ł��B<br>
	 * ���̒l�͕`�惂�f���ɂ���Ă͖�������A���ۂ̃X�v���C�g�̃T�C�Y�Ƃ͈Ⴄ�ꍇ������܂��B<br>
	 *
	 * @return �摜�̍�����Ԃ��܂��B<br>
	 */
	public int getImageHeight() {
		return image.getHeight();
	}

	/**
	 * �X�v���C�g�ɕ\������摜��ݒ肵�܂�.
	 *
	 * @param image
	 */
	public void setImage(BufferedImage image) {
		this.image = new KImage(image);
	}

	public void setImage(KImage image) {
		this.image = image;
	}

	/**
	 * �`�惂�f�����擾���܂�.
	 *
	 * @return �ݒ蒆�̕`�惂�f����Ԃ��܂��B<br>
	 */
	public ImagePainter getPainter() {
		return painter;
	}

	/**
	 * �`�惂�f����ݒ肵�܂�.
	 *
	 * @param painter �ݒ肷��`�惂�f���B<br>
	 */
	public void setPainter(ImagePainter painter) {
		this.painter = painter;
	}

	//Painter�̃N���[���͂��Ȃ��Ă��悢
	@Override
	public ImageSprite clone() {
		ImageSprite sprite = (ImageSprite) super.clone();
		//sprite.painter = this.painter.clone();
		return sprite;
	}

	@Override
	public String toString() {
		return "ImageSprite location=[" + getX() + "," + getY() + "] size=["
				+ getWidth() + "," + getHeight() + "] " + "center=["
				+ getCenterX() + "," + getCenterY() + "] personalCenter=["
				+ getPersonalCenterX() + "," + getPersonalCenterY() + "] visible=["
				+ isVisible() + "] exist=[" + isExist() + "] speed=[" + getSpeed() + "] vector=["
				+ getVector() + "] z=[" + getZ() + "] image=[" + getImage() + "] painter=["
				+ getPainter().getName() + "]";
	}
}
