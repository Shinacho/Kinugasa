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
package kinugasa.game.test.stg;

import java.awt.Color;
import kinugasa.game.GameLogicStorage;
import kinugasa.game.GameManager;
import kinugasa.game.GameOption;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LockUtil;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_4:46:06<br>
 * @author Dra211<br>
 */
public class STG_GM extends GameManager {

	public static void main(String[] args) {
		LockUtil.deleteAllLockFile();
		new STG_GM().gameStart();
	}

	private STG_GM() {
		super(GameOption.defaultOption().setCenterOfScreen().setTitle("STG TEST").setBackColor(Color.BLACK).setUseGamePad(true));
	}

	@Override
	protected void startUp() {
		GameLogicStorage.getInstance().add(new TitleLogic("TITLE", this));
		GameLogicStorage.getInstance().add(new Stage1Logic("STAGE1", this));
		GameLogicStorage.getInstance().add(new Stage2Logic("STAGE2", this));
		GameLogicStorage.getInstance().add(new GameoverLogic("GAMEOVER", this));
		
		GameLogicStorage.getInstance().changeTo("TITLE");
	}

	@Override
	protected void dispose() {
		GameLogicStorage.getInstance().getCurrent().dispose();
	}

	@Override
	protected void update(GameTimeManager gtm) {
		GameLogicStorage.getInstance().getCurrent().update(gtm);
	}

	@Override
	protected void draw(GraphicsContext gc) {
		GameLogicStorage.getInstance().getCurrent().draw(gc);
	}

}
