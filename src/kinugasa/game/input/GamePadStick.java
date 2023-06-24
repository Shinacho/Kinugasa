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
package kinugasa.game.input;

import java.awt.geom.Point2D;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.util.ManualTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/22_16:26:23<br>
 * @author Shinacho<br>
 */
public class GamePadStick extends InputDeviceState {

	public static final Point2D.Float NOTHING = new Point2D.Float(0f, 0f);
	public final float x, y;

	protected GamePadStick(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Point2D.Float getLocation() {
		return new Point2D.Float(x, y);
	}

	public Point2D.Float getLocation(float mag) {
		return new Point2D.Float(x * mag, y * mag);
	}

	public Point2D.Float getRoughLocation(float mag, float deadZone) {
		Point2D.Float result = getLocation(mag);

		if (Math.abs(result.x) < deadZone) {
			result.x = 0;
		}
		if (Math.abs(result.y) < deadZone) {
			result.y = 0;
		}

		return result;
	}

	@Override
	public boolean isAnyInput() {
		return !NOTHING.equals(new Point2D.Float(x, y));
	}

	@Override
	public String toString() {
		return "ThumbStick{" + "value=" + new Point2D.Float(x, y) + '}';
	}

	@Override
	public boolean isAnyButtonInput() {
		return isAnyInput();
	}

	@Override
	public boolean isEmptyInput() {
		return NOTHING.equals(new Point2D.Float(x, y));
	}

	public boolean is(FourDirection dir) {
		KVector v = new KVector(new Point2D.Float(x, y));
		switch (dir) {
			case NORTH:
				return v.checkRange(FourDirection.NORTH.getAngle(), 90);
			case EAST:
				return v.checkRange(FourDirection.EAST.getAngle(), 90);
			case WEST:
				return v.checkRange(FourDirection.WEST.getAngle(), 90);
			case SOUTH:
				return v.checkRange(FourDirection.SOUTH.getAngle(), 90);
		}
		throw new InternalError(dir + " is not found");
	}

}
