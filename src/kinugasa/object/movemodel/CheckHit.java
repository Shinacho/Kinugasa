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

import java.awt.Shape;
import kinugasa.object.BasicSprite;
import kinugasa.object.MovingModel;

/**
 * .
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/02/20_22:26:30<br>
 * @author Shinacho<br>
 */
public class CheckHit extends MovingModel {

	private Shape target;

	public CheckHit(Shape target) {
		this.target = target;
	}

	public Shape getTarget() {
		return target;
	}

	public void setTarget(Shape target) {
		this.target = target;
	}

	@Override
	public void move(BasicSprite s) {
		if (target.intersects(s.getHitBounds())) {
			s.setExist(false);
		}
	}

	@Override
	public CheckHit clone() {
		return (CheckHit) super.clone();
	}
}
