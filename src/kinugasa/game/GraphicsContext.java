/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import kinugasa.object.Drawable;
import kinugasa.resource.KImage;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_5:43:54<br>
 * @author Dra211<br>
 */
public class GraphicsContext {

	private Graphics2D g;

	public GraphicsContext(Graphics2D g) {
		this.g = g;
	}

	public Graphics2D create() {
		return (Graphics2D) g.create();
	}

	public void draw(Drawable... d) {
		for (int i = 0; i < d.length; i++) {
			d[i].draw(this);
		}
	}

	public void draw(List<Drawable> d) {
		d.forEach((v) -> v.draw(this));
	}

	public Rectangle getClipBounds() {
		return g.getClipBounds();
	}

	public void drawImage(KImage image, int i, int i0) {
		g.drawImage(image.asImage(), i, i0, null);
	}

	public void drawImage(Image image, int i, int i0) {
		g.drawImage(image, i, i0, null);
	}

	public void drawImage(KImage image, int i, int i0, int i1, int i2) {
		g.drawImage(image.asImage(), i, i0, i1, i2, null);
	}

	public void drawImage(Image image, int i, int i0, int i1, int i2) {
		g.drawImage(image, i, i0, i1, i2, null);
	}

	public void setColor(Color c) {
		g.setColor(c);
	}

	public void fillOval(float i, float i0, float i1, float i2) {
		g.fillOval((int) i, (int) i0, (int) i1, (int) i2);
	}

	public void drawOval(float i, float i0, float i1, float i2) {
		g.drawOval((int) i, (int) i0, (int) i1, (int) i2);
	}

	public void drawImageF(BufferedImage i, float x, float y) {
		drawImage(i, (int) x, (int) y);
	}

	public void fillRect(int i, int i0, int i1, int i2) {
		g.fillRect(i, i0, i1, i2);
	}

	public void drawRect(int i, int i0, int i1, int i2) {
		g.drawRect(i, i0, i1, i2);
	}

	public void clearRect(int i, int i0, int i1, int i2) {
		g.clearRect(i, i0, i1, i2);
	}

	public void dispose() {
		g.dispose();
	}

}
