/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANYE CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kinugasa.object;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import kinugasa.game.GraphicsContext;

/**
 * 空のスプライトです。何も表示しません。座標計算などで使うダミーのスプライトの実装です。
 *
 * @vesion 1.0.0 - 2022/11/25_20:18:38<br>
 * @author Shinacho<br>
 */
public class EmptySprite extends BasicSprite {

	public EmptySprite() {
		super(0, 0, 0, 0);
	}

	public EmptySprite(Point2D.Float p, Dimension d) {
		this(p.x, p.y, d.width, d.height);
	}

	public EmptySprite(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	public EmptySprite(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	@Override
	public void draw(GraphicsContext g) {
	}

	@Override
	public EmptySprite clone() {
		return (EmptySprite) super.clone();
	}

}
