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
import java.awt.RenderingHints;
import kinugasa.game.GraphicsContext;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/06/18<br>
 * @author Shinacho<br>
 * <br>
 */
public class SimpleTextLabelModel extends TextLabelModel {

	private static int c = 0;

	public SimpleTextLabelModel(String name, FontModel font) {
		super(name, font);
	}

	public SimpleTextLabelModel(FontModel font) {
		super("LABELMODEL_" + c++, font);
	}
	private boolean textAntiAlias = true;

	@Override
	public void draw(GraphicsContext g, TextLabelSprite l) {
		Graphics2D g2 = g.create();
		setProperty(g2);
		if (textAntiAlias) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		g2.drawString(l.getText(), (int) l.getX(), (int) (l.getY() + getFontSize()));
		g2.dispose();
	}

	public boolean isTextAntiAlias() {
		return textAntiAlias;
	}

	public void setTextAntiAlias(boolean textAntiAlias) {
		this.textAntiAlias = textAntiAlias;
	}

}
