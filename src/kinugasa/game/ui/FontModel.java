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
import java.awt.Font;
import kinugasa.graphics.ARGBColor;
import kinugasa.object.Model;
import kinugasa.resource.Nameable;
/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/03/29<br>
 * @author Shinacho<br>
 * <br>
 */
public class FontModel extends Model implements Nameable {

	public static final FontModel DEFAULT = new FontModel("DEFAULT", Color.WHITE, new Font("MONOSPACED", Font.PLAIN, 12));
	private Color color;
	private Font font;
	private String name;

	@Override
	public String getName() {
		return name;
	}

	public FontModel(String name, Color color, Font font) {
		this.name = name;
		this.color = color;
		this.font = font;
		addThis();
	}

	private void addThis() {
		FontModelStorage.getInstance().add(this);
	}

	public Color getColor() {
		return color;
	}

	public FontModel setColor(Color color) {
		this.color = color;
		return this;
	}

	public Font getFont() {
		return font;
	}

	public FontModel setFont(Font font) {
		this.font = font;
		return this;
	}

	public FontModel setFontSize(float size) {
		font = font.deriveFont(size);
		return this;
	}

	public FontModel setFontStyle(int style) {
		font = font.deriveFont(style);
		return this;
	}
	
	public void addAlpha(int val){
		int newVal = color.getAlpha()+val;
		newVal = newVal > 255 ? 255 : newVal < 0 ? 0 : newVal;
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(), newVal);
	}

	@Override
	public FontModel clone() {
		return (FontModel) super.clone();
	}

}
