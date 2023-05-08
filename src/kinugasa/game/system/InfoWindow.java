/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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
		t.add(new Text("<---" + I18N.translate(mode.toString()) + "--->"));

		//data
		switch (mode) {
			case MONEY:
				for (Money m : GameSystem.getInstance().getMoneySystem()) {
					t.add(new Text(m.getVisibleText()));
				}
				break;
			case QUEST:
				//MAIN
				t.add(new Text("--" + I18N.translate("MAIN") + I18N.translate("QUEST")));
				QuestStage qs = QuestLineStorage.getInstance().get("MAIN").getStage();
				t.add(new Text("  " + qs.getTitle() + Text.getLineSep() + "   　　　 " + qs.getDesc().replaceAll("/", "/   　　　 ")));

				//SUB
				t.add(new Text("--" + I18N.translate("QSUB") + I18N.translate("QUEST")));
				for (QuestLine ql : QuestLineStorage.getInstance().filter(p -> !p.getName().equals("MAIN"))) {
					QuestStage qs2 = ql.getStage();
					t.add(new Text("  " + qs2.getTitle() + Text.getLineSep() + "  " + qs2.getDesc()));
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
