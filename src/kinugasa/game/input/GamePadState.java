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
