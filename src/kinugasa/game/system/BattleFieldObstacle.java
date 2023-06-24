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
package kinugasa.game.system;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import kinugasa.game.GraphicsContext;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.object.ImageSprite;
import kinugasa.resource.Nameable;

/**
 * バトルフィールドに配置される障害物のスプライトです。
 *
 * @vesion 1.0.0 - 2022/11/23_11:42:08<br>
 * @author Shinacho<br>
 */
public class BattleFieldObstacle extends ImageSprite implements Nameable {

	private String name;

	public BattleFieldObstacle(String name, float w, float h, BufferedImage image) {
		super(w, h, image);
		this.name = name;
	}

	public BattleFieldObstacle(String name, float x, float y, float w, float h, BufferedImage image) {
		super(x, y, w, h, image);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public BattleFieldObstacle clone() {
		return (BattleFieldObstacle) super.clone();
	}

	@Override
	public void draw(GraphicsContext g) {
		super.draw(g); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
		if (GameSystem.isDebugMode()) {
			Graphics2D g2 = g.create();
			g2.setColor(Color.YELLOW);
			GraphicsUtil.drawRect(g2, getBounds());
			g2.dispose();
		}
	}

}
