/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/11/22_5:25:16<br>
 * @author Dra211<br>
 */
public class ProgressBarSprite extends BasicSprite {

	private int val, initial, max;
	private Color color = Color.GREEN;
	private boolean colorChange = true;

	public ProgressBarSprite(float x, float y, float w, float h, int val, int initial, int max) {
		super(x, y, w, h);
		this.initial = initial;
		this.max = max;
		setVal(val);
	}

	public void reset() {
		val = initial;
	}

	public void setColorChange(boolean colorChange) {
		this.colorChange = colorChange;
	}

	public boolean isColorChange() {
		return colorChange;
	}

	public void setVal(int val) {
		this.val = val;
		setColor();
	}

	public void setInitial(int initial) {
		this.initial = initial;
	}

	public void setMax(int max) {
		this.max = max;
		setColor();
	}

	public int getVal() {
		return val;
	}

	public int getInitial() {
		return initial;
	}

	public int getMax() {
		return max;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public boolean is1of4() {
		return max * 0.25 > val;
	}

	public boolean is2of4() {
		return max * 0.5 >= val;
	}

	public boolean is3of4() {
		return max * 0.75 >= val;
	}

	public void add() {
		val++;
		if (val > max) {
			val = max;
		}
		setColor();
	}

	public void sub() {
		val--;
		if (val < 0) {
			val = 0;
		}
		setColor();
	}

	private void setColor() {
		if (!isColorChange()) {
			return;
		}
		if (is1of4()) {
			color = Color.RED;
		} else if (is2of4()) {
			color = Color.ORANGE;
		} else if (is3of4()) {
			color = Color.YELLOW;
		} else {
			color = Color.GREEN;
		}
	}
	private boolean visibleValue = true;

	public void setVisibleValue(boolean visibleValue) {
		this.visibleValue = visibleValue;
	}

	public boolean isVisibleValue() {
		return visibleValue;
	}

	@Override
	public void draw(GraphicsContext g) {
		int x = (int) getX();
		int y = (int) getY();
		int h = (int) getHeight();
		int w = (int) ((getWidth() / max) * val);
		Graphics2D g2 = g.create();
		g2.setColor(color);
		g2.fillRect(x, y, w, h);
		g2.setColor(Color.BLACK);
		g2.drawRect((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
		if (visibleValue) {
			g2.setColor(new Color(44, 44, 196));
			g2.setFont(FontModel.DEFAULT.clone().setFontSize(8).getFont());
			g2.drawString(val + "", x + 4, y + h);
		}
		g2.dispose();
	}

}
