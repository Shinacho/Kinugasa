/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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
import kinugasa.graphics.ImageEditor;
import kinugasa.object.ImagePainter;
import kinugasa.object.ImageSprite;
import kinugasa.object.MovingModel;
import kinugasa.object.TVector;
import kinugasa.resource.TImage;

/**
 * 雲エフェクトクラスは、画面前面に雲のエフェクトを表示するための画像スプライトです.
 * <br>
 * 雲画像はGIMPを使うと簡単に生成できます。<br>
 *
 * @version 1.0.0 - 2015/06/16<br>
 * @author Dra<br>
 * <br>
 */
public class CloudEffect extends ImageSprite {

	public CloudEffect(BufferedImage image, float x, float y, float tp) throws IllegalArgumentException {
		super(x, y, image.getWidth(), image.getHeight(), ImageEditor.transparent(image, tp, image));
	}

	public CloudEffect(BufferedImage image, float x, float y, TVector v, MovingModel model, ImagePainter p, float tp) throws IllegalArgumentException {
		super(x, y, image.getWidth(), image.getHeight(), v, model, ImageEditor.transparent(image, tp, image), p);

	}

	public CloudEffect(BufferedImage image, float x, float y, float tp, float en) throws IllegalArgumentException {
		super(x, y, image.getWidth() * en, image.getHeight() * en, ImageEditor.transparent(ImageEditor.resize(image, en), tp, null));
	}

	public CloudEffect(BufferedImage image, float x, float y, TVector v, MovingModel model, ImagePainter p, float tp, float en) throws IllegalArgumentException {
		super(x, y, image.getWidth() * en, image.getHeight() * en, v, model, ImageEditor.transparent(ImageEditor.resize(image, en), tp, null), p);
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
		TImage image = getImage();
		float totalHeight = -dy + getHeight();
		float totalWidth = -dx + getWidth();
		for (; dy < totalHeight; dy += image.getHeight()) {
			for (; dx < totalWidth; dx += image.getWidth()) {
				g.drawImage(image, (int) dx, (int) dy);
			}
			dx = idx;
		}
	}

}
