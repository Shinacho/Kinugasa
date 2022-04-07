/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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

/**
 *
 * @vesion 1.0.0 - 2021/11/22_16:33:56<br>
 * @author Dra211<br>
 */
public class GamePadSticks extends InputDeviceState{
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
		this.LEFT = new GamePadStick(Math.abs(leftStickDeadZone.x) > Math.abs(leftX) ? 0f : leftX, Math.abs(leftStickDeadZone.y) > -Math.abs(leftY) ? -leftY : 0f );
		this.RIGHT = new GamePadStick(Math.abs(rightStickDeadZone.x) > Math.abs(rightX) ? 0f : rightX, Math.abs(rightStickDeadZone.y) > -Math.abs(rightY) ? -rightY : 0f );
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
