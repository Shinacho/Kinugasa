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
package kinugasa.game.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/11/22_5:25:16<br>
 * @author Shinacho<br>
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
