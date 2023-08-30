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

import java.awt.geom.Point2D;
import kinugasa.object.BasicSprite;
import kinugasa.object.FourDirection;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_13:08:50<br>
 * @author Shinacho<br>
 */
public interface Actor {

	public BasicSprite getSprite();

	public Status getStatus();

	public void setTargetLocation(Point2D.Float p, int area);

	public void unsetTarget();

	public boolean isMoving();

	public void moveToTgt();

	public void move();

	public void to(FourDirection dir);

	public boolean isPlayer();

	public String getId();

	public default Point2D.Float getCenter() {
		return getSprite().getCenter();
	}

	public default String getName() {
		return getStatus().getName();
	}

}
