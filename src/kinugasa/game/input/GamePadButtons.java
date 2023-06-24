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

/**
 *
 * @vesion 1.0.0 - 2021/11/22_16:23:59<br>
 * @author Shinacho<br>
 */
public final class GamePadButtons extends InputDeviceState{

	public static final boolean ON = true;
	public static final boolean OFF = false;

	public final boolean A;
	public final boolean B;
	public final boolean X;
	public final boolean Y;
	public final boolean RB;
	public final boolean LB;
	public final boolean LEFT_STICK;
	public final boolean RIGHT_STICK;
	public final boolean POV_UP;
	public final boolean POV_DOWN;
	public final boolean POV_LEFT;
	public final boolean POV_RIGHT;
	public final boolean BACK;
	public final boolean START;

	protected GamePadButtons(boolean A, boolean B, boolean X, boolean Y, boolean RB, boolean LB,
			boolean LEFT_STICK, boolean RIGHT_STICK, boolean POV_UP, boolean POV_DOWN, boolean POV_LEFT, boolean POV_RIGHT,
			boolean BACK, boolean START) {
		this.A = A;
		this.B = B;
		this.X = X;
		this.Y = Y;
		this.RB = RB;
		this.LB = LB;
		this.LEFT_STICK = LEFT_STICK;
		this.RIGHT_STICK = RIGHT_STICK;
		this.POV_UP = POV_UP;
		this.POV_DOWN = POV_DOWN;
		this.POV_LEFT = POV_LEFT;
		this.POV_RIGHT = POV_RIGHT;
		this.BACK = BACK;
		this.START = START;
	}

	@Override
	public boolean isAnyInput() {
		return A || B || X || Y || RB || LB || LEFT_STICK || RIGHT_STICK || POV_UP || POV_DOWN || POV_LEFT || POV_RIGHT || BACK || START;
	}

	@Override
	public boolean isAnyButtonInput() {
		return isAnyInput();
	}

	@Override
	public boolean isEmptyInput() {
		return !(A && B && X && Y && RB && LB && LEFT_STICK && RIGHT_STICK && POV_UP && POV_DOWN && POV_LEFT && POV_RIGHT && BACK && START);
	}

	@Override
	public String toString() {
		return "GamePadButton{" + "A=" + A + ", B=" + B + ", X=" + X + ", Y=" + Y + ", RB=" + RB + ", LB=" + LB
				+ ", LEFT_STICK=" + LEFT_STICK + ", RIGHT_STICK=" + RIGHT_STICK
				+ ", POV_UP=" + POV_UP + ", POV_DOWN=" + POV_DOWN + ", POV_LEFT=" + POV_LEFT + ", POV_RIGHT=" + POV_RIGHT
				+ ", BACK=" + BACK + ", START=" + START + '}';
	}

	public boolean isPressed(GamePadButton b) {
		switch (b) {
			case A:
				return this.A;
			case B:
				return this.B;
			case X:
				return this.X;
			case Y:
				return this.Y;
			case RB:
				return this.RB;
			case LB:
				return this.LB;
			case LEFT_STICK:
				return this.LEFT_STICK;
			case RIGHT_STICK:
				return this.RIGHT_STICK;
			case POV_UP:
				return this.POV_UP;
			case POV_DOWN:
				return this.POV_DOWN;
			case POV_LEFT:
				return this.POV_LEFT;
			case POV_RIGHT:
				return this.POV_RIGHT;
			case BACK:
				return this.BACK;
			case START:
				return this.START;
		}

		return false;
	}
	
}
