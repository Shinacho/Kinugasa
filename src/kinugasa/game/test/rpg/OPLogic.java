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

import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
/**
 *
 * @vesion 1.0.0 - 2022/11/13_14:58:58<br>
 * @author Dra211<br>
 */
public class OPLogic extends GameLogic {

	public OPLogic(GameManager gm) {
		super(Const.LogicName.OP, gm);
	}
	private TextLabelSprite operation;

	@Override
	public void load() {
		String operaionText = "(A)" + I18N.translate("SKIP");
		operation = new TextLabelSprite(operaionText, new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 420, 450);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void update(GameTimeManager gtm) {
		InputState is = InputState.getInstance();
		
		if(is.isPressed(GamePadButton.A, InputType.SINGLE)){
			gls.changeTo(Const.LogicName.E1);
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		new TextLabelSprite("‰¼", new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 43, 43).draw(g);
		operation.draw(g);
	}

}
