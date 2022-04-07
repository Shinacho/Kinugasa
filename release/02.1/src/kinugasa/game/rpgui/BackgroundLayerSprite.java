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
package kinugasa.game.rpgui;

import java.awt.image.BufferedImage;
import kinugasa.game.GraphicsContext;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.AnimationSprite;
import kinugasa.object.TVector;
import kinugasa.resource.Disposable;
import kinugasa.util.TimeCounter;

/**
 * �w�i�p�A�j���[�V������\�����郌�C���ł�.
 * <br>
 * �w�i���C���͈ړ�����Ɏg�p����܂���B�S�Ẵ`�b�v������"VOID"��Ԃ��܂��B<br>
 * <br>
 * �w�i��1�ȏ�̉摜���^�C�����O���č\�z����܂��B���[�h����ƁA���炩���ߐݒ肳��Ă���
 * �摜���^�C�����O�����`��p�A�j���[�V�������ݒ肳��܂��B<br>
 * <br>
 * @version 1.0.0 - 2013/05/02_23:03:55<br>
 * @author Dra0211<br>
 */
public class BackgroundLayerSprite extends AnimationSprite implements Disposable {

	private static final long serialVersionUID = -7754463251969957979L;

	public BackgroundLayerSprite(float speed, int x, int y, int width, int height,
			int drawWidth, int drawHeight,
			TimeCounter tc, BufferedImage... images) {
		super(x, y, width, height);
		setVector(new TVector(speed));
		BufferedImage[] buildeImage = new BufferedImage[images.length];
		for (int i = 0; i < images.length; i++) {
			buildeImage[i] = ImageUtil.tiling(images[i], null,
					(int) (getWidth() / images[i].getWidth()),
					(int) (getHeight() / images[i].getHeight()),
					drawWidth, drawHeight);
		}
		setAnimation(new Animation(tc, buildeImage));
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		float dx = getX();
		float dy = getY();
		for (; dx >= 0; dx -= getWidth());
		for (; dy >= 0; dy -= getHeight());
		float idx = dx;
		BufferedImage image = getAnimation().getCurrentBImage();
		float totalHeight = -dy + getHeight();
		float totalWidth = -dx + getWidth();
		for (; dy < totalHeight; dy += image.getHeight(null)) {
			for (; dx < totalWidth; dx += image.getWidth(null)) {
				g.drawImage(image, (int) dx, (int) dy);
			}
			dx = idx;
		}
		if (super.isImageUpdate()) {
			super.update();
		}
	}

	@Override
	public void dispose() {
		setAnimation(null);
	}
}
