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

import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.Model;
import kinugasa.resource.Nameable;

/**
 * このモデルは、テキストラベルのフォントや表示位置を決定します.
 * <br>
 *
 * @version 1.0.0 - 2015/03/24<br>
 * @author Shinacho<br>
 * <br>
 */
public abstract class TextLabelModel extends Model implements Nameable {

	private final String name;
	private FontModel font;

	public TextLabelModel(String name, FontModel font) {
		this.name = name;
		this.font = font;
		addThis();
	}

	private void addThis() {
		if (!TextLabelModelStorage.getInstance().contains(name)) {
			TextLabelModelStorage.getInstance().add(this);
		}
	}

	@Override
	public final String getName() {
		return name;
	}

	protected final void setProperty(Graphics2D g) {
		g.setFont(font.getFont());
		g.setColor(font.getColor());
	}

	public abstract void draw(GraphicsContext g, TextLabelSprite l);

	public int getFontSize() {
		return font.getFont().getSize();
	}

	public FontModel getFontConfig() {
		return font;
	}

	@Override
	public TextLabelModel clone() {
		return (TextLabelModel) super.clone();
	}

}
