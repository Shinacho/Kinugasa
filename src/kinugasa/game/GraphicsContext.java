/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import kinugasa.object.Drawable;
import kinugasa.resource.KImage;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_5:43:54<br>
 * @author Shinacho<br>
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

	public void drawString(String v, int x, int y) {
		g.drawString(v, x, y);
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
