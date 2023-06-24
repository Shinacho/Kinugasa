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
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2021/11/29_16:52:17<br>
 * @author Shinacho<br>
 */
public class FPSLabel extends BasicSprite {

	private GameTimeManager gtm;

	public FPSLabel(int x, int y) {
		super(x, y, 48, 12);
	}

	public void setGtm(GameTimeManager gtm) {
		this.gtm = gtm;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist() || gtm == null) {
			return;
		}
		Graphics2D g2 = g.create();
		g2.setColor(Color.CYAN);
		g2.setFont(FontModel.DEFAULT.clone().setFontSize(12).getFont());
		g2.drawString(gtm.getFPSStr(), (int) getX(), (int) getY());
		g2.dispose();
	}

}
