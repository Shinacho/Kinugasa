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
package kinugasa.game.test;

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameManager;
import kinugasa.game.GameOption;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LockUtil;
import static kinugasa.game.system.BattleConfig.messageWindowY;
import kinugasa.game.ui.MessageWindow;

/**
 *
 * @vesion 1.0.0 - 2022/11/22_5:31:16<br>
 * @author Dra211<br>
 */
public class Test2 extends GameManager {

	public static void main(String[] args) {
	
		LockUtil.deleteAllLockFile();
		new Test2().gameStart(args);
	}

	Test2() {
		super(GameOption.defaultOption());
	}
	MessageWindow mw;

	@Override
	protected void startUp() {
		float w = GameOption.getInstance().getWindowSize().width - 6;
		float h = (float) (GameOption.getInstance().getWindowSize().height / 3.66f);
		mw = new MessageWindow(3, messageWindowY, w, h);
		mw.setText("‚P‚Q‚R‚S‚T‚U‚V‚W‚X‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X‚O‚P‚Q‚R‚S‚T‚U‚V‚W‚X‚O");
		mw.allText();
	}

	@Override
	protected void dispose() {
	}

	@Override
	protected void update(GameTimeManager gtm) {
		mw.update();
	}

	@Override
	protected void draw(GraphicsContext gc) {
		mw.draw(gc);
	}

}
