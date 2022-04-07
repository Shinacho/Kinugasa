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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.resource.Storage;
import kinugasa.resource.TImage;

/**
 * .
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:32:04<br>
 * @author Dra0211<br>
 */
public final class ImagePainterStorage extends Storage<ImagePainter> {

	/**
	 * ���̃��f���͉����`�悵�܂���.
	 */
	public static final ImagePainter NOT_DRAW = new ImagePainter("NOT_DRAW") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			return;
		}
	};
	/**
	 * ���̃��f���̓X�v���C�g�̍��W�ɉ摜���摜�̃T�C�Y�ŕ`�悵�܂�. ���������āA�摜�̈ʒu�̓X�v���C�g�̗̈�́h����h�ɌŒ肳��܂��B<br>
	 * ���̃��f���͂����Ƃ������ɓ��삷�邽�߁A�X�v���C�g�Ɖ摜�̃T�C�Y����v����ꍇ�ɗL�p�ł��B<br>
	 */
	public static final ImagePainter IMAGE_BOUNDS_XY = new ImagePainter("IMAGE_BOUNDS_XY") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			g.drawImage(spr.getImage(), (int) spr.getX(), (int) spr.getY());
		}
	};
	/**
	 * ���̃��f���̓X�v���C�g�̒��S�Ɖ摜�̒��S���d�Ȃ�ʒu�ɉ摜�̃T�C�Y�ŕ`�悵�܂�.
	 */
	public static final ImagePainter IMAGE_BOUNDS_CENTER = new ImagePainter("IMAGE_BOUNDS_CENTER") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			g.drawImage(spr.getImage(),
					(int) (spr.getCenterX() - spr.getImageWidth() / 2),
					(int) (spr.getCenterY() - spr.getImageHeight() / 2));
		}
	};
	/**
	 * ���̃��f���͉摜�̃T�C�Y���X�v���C�g�̃T�C�Y�Ɋg�債�A�X�v���C�g�̗̈�𖄂߂�悤�ɕ`�悵�܂�.
	 */
	public static final ImagePainter SPRITE_BOUNDS = new ImagePainter("SPRITE_BOUNDS") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			g.drawImage(spr.getImage(), (int) spr.getX(), (int) spr.getY(),
					(int) spr.getWidth(), (int) spr.getHeight());
		}
	};
	/**
	 * ���̃��f���̓X�v���C�g�̈ړ��p�x�ɉ����ĉ摜����]���Ă���AIMAGE_BOUNDS_XY�ŕ`�悵�܂�.
	 */
	public static final ImagePainter IMAGE_BOUNDS_XY_ROTATE = new ImagePainter("IMAGE_BOUNDS_XY_ROTATE") {
		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			Graphics2D g2 = g.create();
			g2.setClip(spr.getBounds());
			g2.rotate(spr.getVector().getAngleAsRad(), spr.getCenterX(), spr.getCenterY());
			g2.drawImage(spr.getImage().asImage(), (int) spr.getX(), (int) spr.getY(), null);
			g2.dispose();
		}
	};
	/**
	 * ���̃��f���̓X�v���C�g�̈ړ��p�x�ɉ����ĉ摜����]���Ă���AIMAGE_BOUNDS_CENTER�ŕ`�悵�܂�.
	 */
	public static final ImagePainter IMAGE_BOUNDS_CENTER_ROTATE = new ImagePainter("IMAGE_BOUNDS_CENTER_ROTATE") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			Graphics2D g2 = g.create();
			g2.setClip(spr.getBounds());
			g2.rotate(spr.getVector().getAngleAsRad(), spr.getCenterX(), spr.getCenterY());
			g2.drawImage(spr.getImage().asImage(),
					(int) (spr.getCenterX() - spr.getImageWidth() / 2),
					(int) (spr.getCenterY() - spr.getImageHeight() / 2), null);
			g2.dispose();
		}
	};
	/**
	 * ���̃��f���̓X�v���C�g�̈ړ��p�x�ɉ����ĉ摜����]���Ă���ASPRITE_BOUNDS�ŕ`�悵�܂�.
	 */
	public static final ImagePainter SPRITE_BOUNDS_ROTATE = new ImagePainter("SPRITE_BOUNDS_ROTATE") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			Graphics2D g2 = g.create();
			g2.setClip(spr.getBounds());
			g2.rotate(spr.getVector().getAngleAsRad(), spr.getCenterX(), spr.getCenterY());
			g2.drawImage(spr.getImage().asImage(), (int) spr.getX(), (int) spr.getY(),
					(int) spr.getWidth(), (int) spr.getHeight(), null);
			g2.dispose();

		}
	};
	/**
	 * ���̃��f���́A�X�v���C�g�̗̈�Ɠ����蔻��̈�����ꂼ���`�ŕ`�悵�A�������܂�.
	 * �̈�̐F��Color.GREEN���A�����蔻��̗̈��Color.RED���g�p����܂��B<br>
	 */
	public static final ImagePainter DEBUG_SPRITE_BOUNDS = new ImagePainter("DEBUG_SPRITE_BOUNDS") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			g.setColor(Color.GREEN);
			g.draw((List<Drawable>) spr.getBounds());
			g.setColor(Color.RED);
			g.draw(spr.getHitBounds());
		}
	};
	/**
	 * �O���t�B�b�N�X�R���e�L�X�g�̃N���b�s���O�̈�� ���{�̉摜��񎟌��Ɍ��ԂȂ����ׂĕ`�悵�܂�. ���̃��f���ł̕`��͔��Ɍ������������߁A
	 * �^�C�����O�����摜�����炩���ߍ\�z���Ă����A�ʂ̃��f���ɂ���� �`�悷�邱�Ƃ𐄏����܂��B<br>
	 */
	public static final ImagePainter TITLING_CPLI_AREA = new ImagePainter("TILING_IMAGE_SIZE") {

		@Override
		public void draw(GraphicsContext g, ImageSprite sprite) {
			float minX = sprite.getX();
			float minY = sprite.getY();
			for (; minX >= 0; minX -= sprite.getImageWidth());
			for (; minY >= 0; minY -= sprite.getImageHeight());
			TImage image = sprite.getImage();
			float totalHeight = -minY + g.getClipBounds().height;
			float totalWidth = -minX + g.getClipBounds().width;
			for (float y = minY; y < totalHeight; y += image.getHeight()) {
				for (float x = minX; x < totalWidth; x += image.getWidth()) {
					g.drawImage(image.get(), (int) x, (int) y);
				}
			}
		}
	};
	/**
	 * �X�v���C�g�̗̈�ɓ��{�̉摜��񎟌��Ɍ��ԂȂ����ׂĕ`�悵�܂�. ���̃��f���ł̕`��͔��Ɍ������������߁A
	 * �^�C�����O�����摜�����炩���ߍ\�z���Ă����A�ʂ̃��f���ɂ���� �`�悷�邱�Ƃ𐄏����܂��B<br>
	 */
	public static final ImagePainter TILING_SPRITE_SIZE = new ImagePainter("TILING_SPRITE_SIZE") {

		@Override
		public void draw(GraphicsContext g, ImageSprite sprite) {
			float minX = sprite.getX();
			float minY = sprite.getY();
			for (; minX >= 0; minX -= sprite.getImageWidth());
			for (; minY >= 0; minY -= sprite.getImageHeight());
			TImage image = sprite.getImage();
			float totalHeight = -minY + sprite.getHeight();
			float totalWidth = -minX + sprite.getWidth();
			for (float y = minY; y < totalHeight; y += image.getHeight()) {
				for (float x = minX; x < totalWidth; x += image.getWidth()) {
					g.drawImage(image.get(), (int) x, (int) y);
				}
			}
		}
	};
	private static final long serialVersionUID = 2147787454213377482L;

	/**
	 * �C���X�^���X���擾���܂�.
	 *
	 * @return ���̃N���X�̗B��̃C���X�^���X��Ԃ��܂��B<br>
	 */
	public static ImagePainterStorage getInstance() {
		return INSTANCE;
	}

	/**
	 * �V���O���g���N���X�ł�.
	 */
	private ImagePainterStorage() {
		super();
		add(NOT_DRAW);
		add(IMAGE_BOUNDS_XY);
		add(IMAGE_BOUNDS_XY_ROTATE);
		add(IMAGE_BOUNDS_CENTER);
		add(IMAGE_BOUNDS_CENTER_ROTATE);
		add(SPRITE_BOUNDS);
		add(SPRITE_BOUNDS_ROTATE);
		add(DEBUG_SPRITE_BOUNDS);
		add(TILING_SPRITE_SIZE);
		add(TITLING_CPLI_AREA);
	}
	/**
	 * ���̃N���X�̗B��̃C���X�^���X�ł�.
	 */
	private static final ImagePainterStorage INSTANCE = new ImagePainterStorage();
}
