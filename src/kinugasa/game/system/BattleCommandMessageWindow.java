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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_15:37:41<br>
 * @author Shinacho<br>
 */
public class BattleCommandMessageWindow extends MessageWindow implements CommandWindow {

	private BattleCommand cmd;

	public BattleCommandMessageWindow(float x, float y, float w, float h) {
		super(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""), new TextStorage(), new Text());
		updateText();
	}
	private ActionType type = ActionType.OTHER;
	private int typeIdx = ActionType.values().length - 1;
	private int actionIdx;
	private CmdAction selected;

	public void resetSelect() {
		setType(ActionType.OTHER);
	}

	public void setType(ActionType t) {
		typeIdx = t.ordinal();
		type = ActionType.values()[typeIdx];
		actionIdx = 0;
		updateText();
	}

	public void nextType() {
		typeIdx++;
		if (typeIdx >= ActionType.values().length) {
			typeIdx = 0;
		}
		type = ActionType.values()[typeIdx];
		actionIdx = 0;
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
		setSelected();
	}

	public void prevType() {
		typeIdx--;
		if (typeIdx < 0) {
			typeIdx = ActionType.values().length - 1;
		}
		type = ActionType.values()[typeIdx];
		actionIdx = 0;
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
		setSelected();
	}

	private void setSelected() {
		GameSystem.getInstance().getBattleSystem().getTargetSystem().setCurrent(this);
	}

	@Override
	public CmdAction getSelected() {
		assert cmd != null : "BAMWs CMD is null";
		return selected;
	}

	public boolean isSelected(String name) {
		return selected.getName().equals(name);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	public BattleCommand getCmd() {
		return cmd;
	}

	public void setCmd(BattleCommand cmd) {
		this.cmd = cmd;
		updateText();
	}

	private static final int ACTION_LINE = 5;

	private void updateText() {
		if (cmd == null) {
			if (GameSystem.isDebugMode()) {
				System.out.println("BattleCommandMessageWindow : cmd is null");
			}
			return;
		}
		String text = cmd.getUser().getStatus().getName();
		text = "  " + I18N.translate("WHATDO") + ", " + text + " ! ! ";
		text += "                 <--------------------------------------[ " + type.displayName() + " ]------------------------------------->";
		text += Text.getLineSep();
		int i = 0;
		int c = 0;
		List<CmdAction> actionList = cmd.getBattleActionOf(type);
		Collections.sort(actionList);
		for (CmdAction b : actionList) {
			if (c > ACTION_LINE - 1) {
				break;
			}
			if (i < actionIdx) {
				i++;
				continue;
			}
			if (i == actionIdx) {
				text += "  -> ";
				selected = b;
			} else {
				text += "     ";
			}
			if (type == ActionType.MAGIC || type == ActionType.SPECIAL_ATTACK) {
				String status = "";
				for (String s : BattleConfig.getMagicVisibleStatusKey()) {
					Map<StatusKey, Integer> map = b.selfBattleDirectDamage();
					int val = map.containsKey(StatusKeyStorage.getInstance().get(s)) ? map.get(StatusKeyStorage.getInstance().get(s)) : 0;
					status += " " + s + ":" + val;
				}
				if (type == ActionType.MAGIC) {
					status += " (" + I18N.translate("SPELLTIME") + ":" + b.getSpellTime() + I18N.translate("TURN") + ")";
				}
				text += b.getName() + " : " + b.getDesc() + status + Text.getLineSep();
			} else if (type == ActionType.OTHER || type == ActionType.ITEM_USE) {
				text += b.getName() + Text.getLineSep();
			} else {
				text += b.getName() + " : " + b.getDesc() + Text.getLineSep();
			}

			c++;
			i++;
		}

		Text t = new Text(text);
		t.allText();
		setText(t);
	}

	public void nextAction() {
		actionIdx++;
		if (actionIdx >= cmd.getBattleActionOf(type).size()) {
			actionIdx = 0;
		}
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
		setSelected();
	}

	public void prevAction() {
		actionIdx--;
		if (actionIdx < 0) {
			actionIdx = cmd.getBattleActionOf(type).size() - 1;
		}
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
		setSelected();
	}

}
