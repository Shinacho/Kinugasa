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
package kinugasa.object;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import kinugasa.game.GraphicsContext;

/**
 * 空のスプライトです。何も表示しません。座標計算などで使うダミーのスプライトの実装です。
 *
 * @vesion 1.0.0 - 2022/11/25_20:18:38<br>
 * @author Shinacho<br>
 */
public class EmptySprite extends BasicSprite {

	public EmptySprite() {
		super(0, 0, 0, 0);
	}

	public EmptySprite(Point2D.Float p, Dimension d) {
		this(p.x, p.y, d.width, d.height);
	}

	public EmptySprite(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	public EmptySprite(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	@Override
	public void draw(GraphicsContext g) {
	}

	@Override
	public EmptySprite clone() {
		return (EmptySprite) super.clone();
	}

}
