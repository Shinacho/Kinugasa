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
import java.util.stream.Collectors;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_17:37:07<br>
 * @author Shinacho<br>
 */
public class BattleStatusWindows extends BasicSprite {

	private List<MessageWindow> mw = new ArrayList<>();
	private List<Status> status;

	BattleStatusWindows(List<Status> s) {
		status = s;
		init();
	}

	List<MessageWindow> getMw() {
		return mw;
	}

	public static float h = 65;

	public void init() {
		float x = 3;
		float y = 3;
		float w = (GameOption.getInstance().getWindowSize().width - 6) / status.size() / GameOption.getInstance().getDrawSize();
		for (Status s : status) {
			//表示文字列の生成
			String text = s.getName() + "   | ";
			StatusValueSet es = s.getEffectedStatus();
			int j = 0;
			for (String vs : BattleConfig.getVisibleStatus()) {
				if (j > 0) {
					text += "                   ";
				}
				text += "  " + vs + ":" + (int) (es.get(vs.trim()).getValue()) + Text.getLineSep();
			}
			//座標の計算
			Text t = new Text(text);
			t.allText();
			MessageWindow window = new MessageWindow(x, y, w, h, t);
			window.allText();
			window.setModel(new SimpleMessageWindowModel().setNextIcon(""));
			mw.add(window);
			x += w;
		}
	}

	@Override
	public void update() {
		int i = 0;
		for (Status s : status) {
			//表示文字列の生成
			String text = s.getName() + "   | ";
			StatusValueSet es = s.getEffectedStatus();
			int j = 0;
			for (String vs : BattleConfig.getVisibleStatus()) {
				if (j > 0) {
					text += "                    ";
				}
				text += "  " + vs + ":" + (int) (es.get(vs.trim()).getValue()) + Text.getLineSep();
				j++;
			}
			Text t = new Text(text);
			t.allText();
			mw.get(i).setText(t);
			i++;
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.forEach(v -> v.draw(g));
	}

}
