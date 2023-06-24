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
package kinugasa.object.movemodel;

import kinugasa.object.BasicSprite;
import kinugasa.object.MovingModel;


/**
 * スプライトに設定されているベクトルを使用して直線的に移動する基本の移動パターンです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_18:26:22<br>
 * @author Shinacho<br>
 */
public class BasicMoving extends MovingModel {

	private BasicMoving() {
	}
	private static final BasicMoving INSTANCE = new BasicMoving();

	@Override
	public void move(BasicSprite s) {
		s.setX(s.getX() + s.getVector().getX());
		s.setY(s.getY() + s.getVector().getY());
	}

	public static BasicMoving getInstance() {
		return INSTANCE;
	}

	@Override
	public BasicMoving clone() {
		return INSTANCE;
	}
}
