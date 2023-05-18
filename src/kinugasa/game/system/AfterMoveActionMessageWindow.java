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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;

/**
 *
 * @vesion 1.0.0 - 2022/11/27_21:08:16<br>
 * @author Shinacho<br>
 */
public class AfterMoveActionMessageWindow extends ScrollSelectableMessageWindow implements CommandWindow {

	public AfterMoveActionMessageWindow(int x, int y, int w, int h) {
		super(x, y, w, h, 7, true);
	}
	private List<CmdAction> actions = new ArrayList();

	public List<CmdAction> getActions() {
		return actions;
	}

	@Override
	public CmdAction getSelectedCmd() {
		return actions.get(getSelectedIdx());
	}

	public void setActions(List<CmdAction> actions) {
		this.actions = actions;
		updateText();
		reset();
	}

	public void clear() {
		actions.clear();
	}

	private static final int ACTION_LINE = 7;

	private void updateText() {
		if (actions.isEmpty()) {
			throw new GameSystemException("AMCMW :  + actions is empty");
		}
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < actions.size(); i++) {
			ActionType type = actions.get(i).getType();
			CmdAction b = actions.get(i);
			String text = "";
			switch (type) {
				case ATTACK:
					text += b.getName() + ":" + b.getDesc();
					text += ("、")
							+ (I18N.translate("ACTION_ATTR"))
							+ (":");
					text += (b.getBattleEvent()
							.stream()
							.filter(p -> !AttrDescWindow.getUnvisibleAttrName().contains(p.getAttr().getName()))
							.map(p -> p.getAttr().getDesc())
							.distinct()
							.collect(Collectors.toList()));
					text += ("、")
							+ (I18N.translate("ACTION_EFFECT"))
							+ (":");
					//ENEMYが入っている場合、minを、そうでない場合はMAXを取る
					if (b.getBattleEvent().stream().anyMatch(p -> p.getTargetType().toString().contains("ENEMY"))) {
						text += (Math.abs(b.getBattleEvent()
								.stream()
								.mapToInt(p -> (int) (p.getValue()))
								.min()
								.getAsInt()));
					} else {
						text += (Math.abs(b.getBattleEvent()
								.stream()
								.mapToInt(p -> (int) (p.getValue()))
								.max()
								.getAsInt()));
					}
					break;
				case ITEM:
				case MAGIC:
					//処理なし（入らない）
					break;
				case OTHER:
					text += b.getName();
					break;
				default:
					throw new AssertionError("BCMW undefined type");
			}
			s.append(text + Text.getLineSep());
		}
		setText(Text.split(new Text(s.toString())));
		getWindow().allText();
	}

	public void nextAction() {
		nextSelect();
		GameSystem.getInstance().getBattleSystem().getTargetSystem().setCurrent(this);
	}

	public void prevAction() {
		prevSelect();
		GameSystem.getInstance().getBattleSystem().getTargetSystem().setCurrent(this);
	}
}
