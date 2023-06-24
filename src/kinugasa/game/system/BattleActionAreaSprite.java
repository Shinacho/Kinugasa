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
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;
import kinugasa.object.Sprite;
import kinugasa.util.TimeCounter;

/**
 * バトルアクションエリアスプライトは、ターゲットシステムで使用される、中心点から距離分離れたサークルを定義するクラスです。
 * このスプライトは、スプライトの機能をつかうためだけにSpriteのサブクラスになっています。座標及びサイズは無視されます。
 *
 * @vesion 1.0.0 - 2022/11/28_21:36:31<br>
 * @author Shinacho<br>
 */
public class BattleActionAreaSprite extends BasicSprite {

	private int area = 0;
	private Color color;

	public BattleActionAreaSprite(Color color) {
		this.color = color;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getArea() {
		return area;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		if (area == 0) {
			return;
		}
		Graphics2D g2 = g.create();
		g2.setColor(color);
		int x = (int) (getCenterX() - area);
		int y = (int) (getCenterY() - area);
		g2.drawOval(x, y, area * 2, area * 2);
		g2.dispose();
	}

}
