/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_8:09:13<br>
 * @author Shinacho<br>
 */
public class OrderSelectWindow extends BasicSprite {

	public OrderSelectWindow(float x, float y, float w, float h) {
		super(x, y, w, h);
		window = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		init();
	}

	private void init() {
		List<Text> options = new ArrayList<>();
		for (Status s : GameSystem.getInstance().getPartyStatus()) {
			options.add(new Text(s.getName() + " : " + I18N.get(s.getPartyLocation() == PartyLocation.BACK ? GameSystemI18NKeys.後列 : GameSystemI18NKeys.前列)));
		}
		window.setText(new Choice(options, "ORDER_SELECT_WINDOW", I18N.get(GameSystemI18NKeys.前列後列設定)));
		window.allText();
	}
	private MessageWindow window;

	public void nextPC() {
		window.nextSelect();
	}

	public void prevPC() {
		window.prevSelect();
	}

	public void changeOrder() {
		int idx = window.getSelect();
		PartyLocation pl = GameSystem.getInstance().getPartyStatus().get(idx).getPartyLocation();
		switch (pl) {
			case BACK:
				GameSystem.getInstance().getPartyStatus().get(idx).setPartyLocation(PartyLocation.FRONT);
				break;
			case FRONT:
				GameSystem.getInstance().getPartyStatus().get(idx).setPartyLocation(PartyLocation.BACK);
				break;
		}
		updateText();
	}

	private void updateText() {
		int selectId = window.getSelect();
		List<Text> options = new ArrayList<>();
		for (Status s : GameSystem.getInstance().getPartyStatus()) {
			options.add(new Text(s.getName() + " : " + I18N.get(s.getPartyLocation() == PartyLocation.BACK ? GameSystemI18NKeys.後列 : GameSystemI18NKeys.前列)));
		}
		window.setText(new Choice(options, "ORDER_SELECT_WINDOW", I18N.get(GameSystemI18NKeys.前列後列設定)));
		window.allText();
		window.setSelect(selectId);
	}

	@Override
	public void update() {
		window.update();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		window.draw(g);
	}

}
