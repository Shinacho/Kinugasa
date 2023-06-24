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

/**
 *
 * @vesion 1.0.0 - 2021/11/22_16:33:56<br>
 * @author Shinacho<br>
 */
public class GamePadSticks extends InputDeviceState {

	private static Point2D.Float leftStickDeadZone = new Point2D.Float(0.18921477f, 0.18921477f);
	private static Point2D.Float rightStickDeadZone = new Point2D.Float(0.18921477f, 0.18921477f);
	private static final long serialVersionUID = 1L;

	public static Point2D.Float getLeftStickDeadZone() {
		return leftStickDeadZone;
	}

	public static void setLeftStickDeadZone(Point2D.Float leftStickDeadZone) {
		GamePadSticks.leftStickDeadZone = leftStickDeadZone;
	}

	public static Point2D.Float getRightStickDeadZone() {
		return rightStickDeadZone;
	}

	public static void setRightStickDeadZone(Point2D.Float rightStickDeadZone) {
		GamePadSticks.rightStickDeadZone = rightStickDeadZone;
	}

	public static void noDeadZone() {
		leftStickDeadZone = new Point2D.Float(0f, 0f);
		rightStickDeadZone = (Point2D.Float) leftStickDeadZone.clone();
	}

	public static void resetDeadZone() {
		leftStickDeadZone = new Point2D.Float(0.18921477f, 0.18921477f);
		rightStickDeadZone = (Point2D.Float) leftStickDeadZone.clone();
	}

	public final GamePadStick LEFT;
	public final GamePadStick RIGHT;

	protected GamePadSticks(float leftX, float leftY, float rightX, float rightY) {
		this.LEFT = new GamePadStick(
				Math.abs(leftStickDeadZone.x) > Math.abs(leftX) ? 0f : leftX,
				-Math.abs(leftStickDeadZone.y) > -Math.abs(leftY) ? -leftY : 0f);
		this.RIGHT = new GamePadStick(
				Math.abs(rightStickDeadZone.x) > Math.abs(rightX) ? 0f : rightX,
				-Math.abs(rightStickDeadZone.y) > -Math.abs(rightY) ? -rightY : 0f);
	}

	@Override
	public String toString() {
		return "ThumbSticks{" + "left=" + LEFT + ", right=" + RIGHT + '}';
	}

	@Override
	public boolean isAnyInput() {
		return LEFT.isAnyInput() || RIGHT.isAnyInput();
	}

	@Override
	public boolean isAnyButtonInput() {
		return isAnyInput();
	}

	@Override
	public boolean isEmptyInput() {
		return !(LEFT.isAnyInput() && RIGHT.isAnyInput());
	}
}
