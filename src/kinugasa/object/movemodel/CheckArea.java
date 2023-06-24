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
 * @version 1.0.0 - 2013/02/10_2:57:47<br>
 * @author Shinacho<br>
 */
public class CheckArea extends MovingModel {

	private Shape area;

	public CheckArea(Shape area) {
		this.area = area;
	}

	public Shape getArea() {
		return area;
	}

	public void setArea(Shape area) {
		this.area = area;
	}

	@Override
	public void move(BasicSprite s) {
		if (!area.contains(s.getCenter())) {
			s.setExist(false);
		}
	}

	@Override
	public CheckArea clone() {
		return (CheckArea) super.clone();
	}
}
