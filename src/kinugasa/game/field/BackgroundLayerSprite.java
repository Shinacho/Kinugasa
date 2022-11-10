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
package kinugasa.game.field;

import java.awt.image.BufferedImage;
import kinugasa.game.GraphicsContext;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Disposable;
import kinugasa.util.TimeCounter;

/**
 * 背景用アニメーションを表示するレイヤです.
 * <br>
 * 背景レイヤは移動判定に使用されません。<br>
 * <br>
 * 背景は1つ以上の画像をタイリングして構築されます。ロードすると、あらかじめ設定されている 画像をタイリングした描画用アニメーションが設定されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/05/02_23:03:55<br>
 * @author Dra0211<br>
 */
public class BackgroundLayerSprite extends AnimationSprite implements Disposable {

	public BackgroundLayerSprite(float w, float h) {
		super(0, 0, w, h);
	}
	private boolean n = false;
	private boolean s = false;
	private boolean e = false;
	private boolean w = false;

	public BackgroundLayerSprite toNorth() {
		n = true;
		return this;
	}

	public BackgroundLayerSprite toSouth() {
		s = true;
		return this;
	}

	public BackgroundLayerSprite toEast() {
		e = true;
		return this;
	}

	public BackgroundLayerSprite toWest() {
		w = true;
		return this;
	}

	public void build(int chipDrawSize, TimeCounter tc, BufferedImage... images) {
		BufferedImage[] buildeImage = new BufferedImage[images.length];
		for (int i = 0; i < images.length; i++) {
			buildeImage[i] = ImageUtil.tiling(images[i], null,
					e ? (int) (getWidth() / images[i].getWidth()) : 1,
					s ? (int) (getHeight() / images[i].getHeight()) : 1,
					chipDrawSize,
					chipDrawSize);
			setAnimation(new Animation(tc, buildeImage));
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		float x = getX();
		float y = getY();
		for (; w && x >= 0; x -= getWidth());
		for (; n && y >= 0; y -= getHeight());

		float ix = x;
		BufferedImage image = getAnimation().getCurrentBImage();
		float totalWidth = e ? getWidth() : 0;
		float totalHeight = s ? getHeight() : 0;

		for (; y < totalHeight; y += image.getHeight()) {
			for (; x < totalWidth; x += image.getWidth()) {
				g.drawImage(image, (int) x, (int) y);
			}
			x = ix;
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
