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
package kinugasa.game.field4;

import java.awt.image.BufferedImage;
import kinugasa.game.GraphicsContext;
import kinugasa.graphics.ImageEditor;
import kinugasa.object.ImageSprite;
import kinugasa.object.KVector;
import kinugasa.resource.Disposable;

/**
 * このクラスは、画面前面に雲のエフェクトを表示するための画像スプライトです。雲以外にも使えるかもしれません。
 * <br>
 * 雲画像はGIMPを使うと簡単に生成できます。<br>
 * このクラスに設定した画像はループされ、描画時に自動的に移動されます。また、フィールドマップへの速度や角度の変更の影響を受けません。
 *
 * @version 1.0.0 - 2015/06/16<br>
 * @author Dra<br>
 * <br>
 */
public class BeforeLayerSprite extends ImageSprite implements Disposable {


	public BeforeLayerSprite(BufferedImage image, float tp, float mg, KVector v) throws IllegalArgumentException {
		super(0, 0, image.getWidth() * mg, image.getHeight() * mg, ImageEditor.transparent(ImageEditor.resize(image, mg), tp, null));
		setVector(v);
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		int x = (int) getX();
		int y = (int) getY();
		int w = (int) getWidth();
		int h = (int) getHeight();

		while (x > 0) {
			x -= w;
		}
		while (y > 0) {
			y -= h;
		}

		BufferedImage image = getImage().get();
		g.drawImage(image, x, y);
		g.drawImage(image, x + w, y);
		g.drawImage(image, x, y + h);
		g.drawImage(image, x + w, y + h);
		

		super.move();
	}

	@Override
	public void dispose() {
		setImage((BufferedImage) null);
	}

}
