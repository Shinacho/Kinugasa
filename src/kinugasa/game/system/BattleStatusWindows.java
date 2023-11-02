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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_17:37:07<br>
 * @author Shinacho<br>
 */
public class BattleStatusWindows extends BasicSprite {

	private List<MessageWindow> mw = new ArrayList<>();
	private List<Actor> status;

	BattleStatusWindows(List<Actor> s) {
		status = s;
		init();
	}

	List<MessageWindow> getMw() {
		return mw;
	}

	public static float h = 68;

	public void init() {
		float x = 3;
		float y = 3;
		float w = (GameOption.getInstance().getWindowSize().width - 6) / status.size() / GameOption.getInstance().getDrawSize();
		for (Actor s : status) {
			//表示文字列の生成
			String text = s + s.getVisibleName() + " | ";
			StatusValueSet es = s.getStatus().getEffectedStatus();
			int j = 0;
			for (StatusValue vs : es) {
				if (j != 0) {
					if (s.getVisibleName().substring(0, 1).getBytes().length == 1) {
						text += " ".repeat(s.getVisibleName().length());
					} else {
						text += "　".repeat(s.getVisibleName().length());
					}
					text += " | ";
				}
				text += vs.getKey().getVisibleName()
						+ ":"
						+ (int) (vs.getValue()) + Text.getLineSep();
				j++;
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
		for (Actor s : status) {
			//表示文字列の生成
			String text = s.getVisibleName() + " | ";
			StatusValueSet es = s.getStatus().getEffectedStatus();
			int j = 0;
			for (StatusValue vs : es) {
				if (j != 0) {
					if (s.getVisibleName().substring(0, 1).getBytes().length == 1) {
						text += " ".repeat(s.getVisibleName().length());
					} else {
						text += "　".repeat(s.getVisibleName().length());
					}
					text += " | ";
				}
				text += vs.getKey().getVisibleName()
						+ ":"
						+ (int) (vs.getValue()) + Text.getLineSep();
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
