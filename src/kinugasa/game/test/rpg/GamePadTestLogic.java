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
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.MusicRoom;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.game.input.GamePadStatusMonitor;
import kinugasa.object.FourDirection;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/13_12:17:23<br>
 * @author Dra211<br>
 */
public class GamePadTestLogic extends GameLogic {

	public GamePadTestLogic(GameManager gm) {
		super(Const.LogicName.GAMEPAD_TEST, gm);
	}
	private GamePadStatusMonitor gp;
	private TextLabelSprite operation;

	@Override
	public void load() {
		gp = new GamePadStatusMonitor();
		String operaionText = "(B)" + I18N.translate("RETURN");
		operation = new TextLabelSprite(operaionText, new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 420, 450);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void update(GameTimeManager gtm) {
		InputState is = InputState.getInstance();
		gp.update(is.getGamePadState());
		if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
			gls.changeTo(Const.LogicName.TITLE_LOGIC);
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		Graphics2D g2 = g.create();
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("MONOSPACED", Font.PLAIN, 24));
		g2.drawString("GAMEPAD TEST", 12, 32);
		g2.dispose();
		gp.draw(g);
		operation.draw(g);
	}

}
