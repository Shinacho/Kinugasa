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
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2023/05/06_7:56:27<br>
 * @author Shinacho<br>
 */
public class InfoWindow extends BasicSprite {

	public InfoWindow(int x, int y, int w, int h) {
		super(x, y, w, h);
		main = new ScrollSelectableMessageWindow(x, y, w, h, 20, false);
		main.setLoop(true);
		updateText();
	}

	public enum Mode {
		MONEY,
		QUEST
	}
	private Mode mode = Mode.MONEY;
	private ScrollSelectableMessageWindow main;

	public void switchMode() {
		mode = mode == Mode.MONEY ? Mode.QUEST : Mode.MONEY;
		main.reset();
		updateText();
	}

	public void nextSelect() {
		main.nextSelect();
	}

	public void prevSelect() {
		main.prevSelect();
	}

	private void updateText() {
		List<Text> t = new ArrayList<>();

		//line1
		t.add(new Text("<---" + I18N.get(mode == Mode.MONEY ? GameSystemI18NKeys.お金 : GameSystemI18NKeys.クエスト) + "--->"));

		//data
		switch (mode) {
			case MONEY:
				for (Money m : GameSystem.getInstance().getMoneySystem()) {
					t.add(new Text(m.getVisibleText()));
				}
				break;
			case QUEST:
				//MAIN
				t.add(new Text("--" + I18N.get(GameSystemI18NKeys.メインクエスト)));
				Quest qs = QuestStorage.getInstance().get("MAIN");
				t.add(new Text("  " + qs.getVisibleName() + Text.getLineSep() + "   　　　 " + qs.getDesc().replaceAll("/", "/   　　　 ")));

				//SUB
				t.add(new Text("--" + I18N.get(GameSystemI18NKeys.サブクエスト)));
				for (Quest ql : QuestStorage.getInstance().filter(p -> !p.getName().equals("MAIN"))) {
					t.add(new Text("  " + ql.getVisibleName() + Text.getLineSep() + "  " + ql.getDesc()));
				}

				break;
			default:
				throw new AssertionError();
		}

		main.setText(t);
	}

	@Override
	public void update() {
		main.update();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		main.draw(g);
	}

}
