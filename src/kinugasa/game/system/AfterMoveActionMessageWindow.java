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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;

/**
 *
 * @vesion 1.0.0 - 2022/11/27_21:08:16<br>
 * @author Dra211<br>
 */
public class AfterMoveActionMessageWindow extends MessageWindow implements CommandWindow {

	public AfterMoveActionMessageWindow(float x, float y, float w, float h) {
		super(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""), new TextStorage(), new Text());
	}
	private List<BattleAction> actions = new ArrayList();

	public List<BattleAction> getActions() {
		return actions;
	}

	private int selected = 0;

	@Override
	public BattleAction getSelected() {
		return actions.get(selected);
	}

	public void add(BattleAction... ba) {
		actions.addAll(Arrays.asList(ba));
		Collections.sort(actions);
		selected = 0;
		updateText();
	}

	public void add(List<BattleAction> ba) {
		actions.addAll(ba);
		Collections.sort(actions);
		selected = 0;
		updateText();
	}

	public void setActions(List<BattleAction> actions) {
		this.actions = actions;
		Collections.sort(actions);
		selected = 0;
		updateText();
	}

	public void clear() {
		actions.clear();
		selected = 0;
	}

	private static final int ACTION_LINE = 7;

	private void updateText() {
		if (actions.isEmpty()) {
			throw new GameSystemException("AMCMW :  + actions is empty");
		}
		StringBuilder s = new StringBuilder();
		for (int i = selected, c = 0; i < actions.size(); i++, c++) {
			if (i == selected) {
				s.append("  -> ");
			} else {
				s.append("     ");
			}
			s.append(actions.get(i).getName()).append(Text.getLineSep());
			if (c > ACTION_LINE) {
				break;
			}
		}
		setText(new Text(s.toString()));
		allText();
	}

	public void nextAction() {
		selected++;
		if (selected >= actions.size()) {
			selected = 0;
		}
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
		GameSystem.getInstance().getBattleSystem().setAfterMoveArea(getSelected());
	}

	public void prevAction() {
		selected--;
		if (selected < 0) {
			selected = actions.size() - 1;
		}
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
		GameSystem.getInstance().getBattleSystem().setAfterMoveArea(getSelected());
	}
}
