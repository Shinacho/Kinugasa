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
package kinugasa.game.test.stg;

import kinugasa.game.GameLogic;
import kinugasa.game.GameLogicStorage;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_4:34:15<br>
 * @author Shinacho<br>
 */
public class GameoverLogic extends GameLogic {

	public GameoverLogic(String name, GameManager gm) {
		super(name, gm);
	}

	@Override
	public void load() {
		text1 = new TextLabelSprite("GAME OVER", new SimpleTextLabelModel("DEFAULT", FontModel.DEFAULT.clone().setFontSize(48)), 24, 24);
		text2 = new TextLabelSprite("retry...A", new SimpleTextLabelModel("DEFAULT", FontModel.DEFAULT.clone().setFontSize(12)), 280, 440);
		tc = new FrameTimeCounter(30);
	}
	private TextLabelSprite text1;
	private TextLabelSprite text2;
	private TimeCounter tc;

	@Override
	public void dispose() {
	}

	@Override
	public void update(GameTimeManager gtm, InputState is) {
		if(is.isPressed(GamePadButton.A, InputType.SINGLE)){
			GameLogicStorage.getInstance().changeTo(gls.getPrev().getName());
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		if(tc.isReaching()){
			text2.setVisible(!text2.isVisible());
			tc.reset();
		}
		text1.draw(g);
		text2.draw(g);
		
	}
}
