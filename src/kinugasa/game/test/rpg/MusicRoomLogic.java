/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.test.rpg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.ui.Dialog;
import kinugasa.game.ui.DialogIcon;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.MusicRoom;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.object.FourDirection;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/13_10:34:45<br>
 * @author Dra211<br>
 */
public class MusicRoomLogic extends GameLogic {

	public MusicRoomLogic(GameManager gm) {
		super(Const.LogicName.MUSIC_ROOM, gm);
	}

	private MusicRoom mr;
	private FrameTimeCounter gamepadSticksDelay = new FrameTimeCounter(10);
	private TextLabelSprite operation;

	@Override
	public void load() {
		mr = new MusicRoom("BGM", Const.Screen.WIDTH, Const.Screen.HEIGHT);
		String operaionText = "(A)" + I18N.translate("SUBMIT") + " / " + "Å™Å´Å®Å©" + I18N.translate("MOVE") + " / " + "(X) " + I18N.translate("OPEN") + " / " + "(B) " + I18N.translate("RETURN");
		operation = new TextLabelSprite(operaionText, new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 280, 450);
	}

	@Override
	public void dispose() {
		SoundStorage.getInstance().get("BGM").stopAll();
	}

	@Override
	public void update(GameTimeManager gtm) {
		InputState is = InputState.getInstance();
		if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE) || is.getGamePadState().sticks.LEFT.is(FourDirection.SOUTH) && gamepadSticksDelay.isReaching()) {
			mr.next();
			gamepadSticksDelay.reset();
		}
		if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE) || is.getGamePadState().sticks.LEFT.is(FourDirection.NORTH) && gamepadSticksDelay.isReaching()) {
			mr.prev();
			gamepadSticksDelay.reset();
		}
		if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
			mr.nextColumn();
			gamepadSticksDelay.reset();
		}
		if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
			mr.prevColumn();
			gamepadSticksDelay.reset();
		}

		if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
			mr.exec();
		}
		if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
			gls.changeTo(Const.LogicName.TITLE_LOGIC);
		}
		if (is.isPressed(GamePadButton.X, InputType.SINGLE)) {
			try {
				Runtime.getRuntime().exec("cmd /C start .\\resource\\bgm");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	@Override
	public void draw(GraphicsContext g) {
		Graphics2D g2 = g.create();
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("MONOSPACED", Font.PLAIN, 24));
		g2.drawString("MUSIC ROOM", 12, 32);
		g2.dispose();
		mr.draw(g);
		operation.draw(g);
	}

}
