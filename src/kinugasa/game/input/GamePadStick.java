/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
