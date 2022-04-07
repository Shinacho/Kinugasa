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

import kinugasa.game.GraphicsContext;
import kinugasa.resource.Nameable;

/**
 * 画像を描画する方法をカプセル化します.
 * <br>
 * このモデルは、通常クローニングされないため、ImageSpriteでのクローンでは複製されません。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:30:09<br>
 * @author Dra0211<br>
 */
public abstract class ImagePainter extends Model implements Nameable {

	private final String name;

	public ImagePainter(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public abstract void draw(GraphicsContext g, ImageSprite sprite);

	@Override
	public ImagePainter clone() {
		return (ImagePainter) super.clone();
	}
}
