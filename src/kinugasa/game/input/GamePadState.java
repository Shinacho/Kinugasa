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
 * @vesion 1.0.0 - 2021/11/22_16:23:40<br>
 * @author Shinacho<br>
 */
public class GamePadState extends InputDeviceState {

	public final GamePadButtons buttons;
	public final GamePadTriggeres triggeres;
	public final GamePadSticks sticks;

	GamePadState(GamePadButtons buttons, GamePadTriggeres triggeres, GamePadSticks sticks) {
		this.buttons = buttons;
		this.triggeres = triggeres;
		this.sticks = sticks;
	}

	GamePadState(float... nativeValues) {
		this.buttons = new GamePadButtons(
				nativeValues[GamePadConnection.BUTTON_A] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_B] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_X] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_Y] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_RB] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_LB] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_RIGHT_STICK] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_LEFT_STICK] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_POV_UP] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_POV_DOWN] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_POV_LEFT] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_POV_RIGHT] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_BACK] != GamePadConnection.NATIVE_FALSE,
				nativeValues[GamePadConnection.BUTTON_START] != GamePadConnection.NATIVE_FALSE
		);
		triggeres = new GamePadTriggeres(
				new GamePadTrigger(nativeValues[GamePadConnection.TRIGGER_LEFT]),
				new GamePadTrigger(nativeValues[GamePadConnection.TRIGGER_RIGHT])
		);
		sticks = new GamePadSticks(
				nativeValues[GamePadConnection.THUMB_STICK_LEFT_X],
				nativeValues[GamePadConnection.THUMB_STICK_LEFT_Y],
				nativeValues[GamePadConnection.THUMB_STICK_RIGHT_X],
				nativeValues[GamePadConnection.THUMB_STICK_RIGHT_Y]
		);

	}

	@Override
	public boolean isAnyInput() {
		return buttons.isAnyInput() || triggeres.isAnyInput();
	}

	@Override
	public boolean isAnyButtonInput() {
		return isAnyInput();
	}

	@Override
	public boolean isEmptyInput() {
		return !(buttons.isAnyInput() && triggeres.isAnyInput());
	}

	@Override
	public GamePadState clone() {
		return (GamePadState) super.clone();
	}

	@Override
	public String toString() {
		return "GamePadState{" + "buttons=" + buttons + ", triggeres=" + triggeres + ", sticks=" + sticks + '}';
	}

}
