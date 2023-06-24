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
 * @vesion 1.0.0 - 2021/11/22_16:34:55<br>
 * @author Shinacho<br>
 */
public class GamePadTriggeres extends InputDeviceState {

	public final GamePadTrigger RIGHT;
	public final GamePadTrigger LEFT;

	public GamePadTriggeres(GamePadTrigger LEFT, GamePadTrigger RIGHT) {
		this.LEFT = LEFT;
		this.RIGHT = RIGHT;
	}

	@Override
	public boolean isAnyInput() {
		return RIGHT.isAnyInput() || LEFT.isAnyInput();
	}

	@Override
	public boolean isAnyButtonInput() {
		return RIGHT.isAnyInput() || LEFT.isAnyInput();
	}

	@Override
	public boolean isEmptyInput() {
		return !(RIGHT.isAnyInput() && LEFT.isAnyInput());
	}

	@Override
	public String toString() {
		return "GamePadTriggeres{" + "RIGHT=" + RIGHT + ", LEFT=" + LEFT + '}';
	}

}
