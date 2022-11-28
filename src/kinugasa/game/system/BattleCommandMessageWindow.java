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
import java.util.Collections;
import java.util.List;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_15:37:41<br>
 * @author Dra211<br>
 */
public class BattleCommandMessageWindow extends MessageWindow {

	private BattleCommand cmd;

	public BattleCommandMessageWindow(float x, float y, float w, float h) {
		super(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""), new TextStorage(), new Text());
		updateText();
	}
	private BattleActionType type = BattleActionType.OTHER;
	private int typeIdx = BattleActionType.values().length - 1;
	private int actionIdx;
	private BattleAction selected;
	private static List<String> statusKey = new ArrayList<>();
	private static String moveActionName = I18N.translate("MOVE");

	public static String getMoveActionName() {
		return moveActionName;
	}

	public static void setMoveActionName(String moveActionName) {
		BattleCommandMessageWindow.moveActionName = moveActionName;
	}

	public static List<String> getStatusKey() {
		return statusKey;
	}

	public static void setStatusKey(List<String> statusKey) {
		BattleCommandMessageWindow.statusKey = statusKey;
	}

	public void resetSelect() {
		setType(BattleActionType.OTHER);
	}

	public void setType(BattleActionType t) {
		typeIdx = t.ordinal();
		type = BattleActionType.values()[typeIdx];
		actionIdx = 0;
		updateText();
	}

	public void nextType() {
		typeIdx++;
		if (typeIdx >= BattleActionType.values().length) {
			typeIdx = 0;
		}
		type = BattleActionType.values()[typeIdx];
		actionIdx = 0;
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
	}

	public void prevType() {
		typeIdx--;
		if (typeIdx < 0) {
			typeIdx = BattleActionType.values().length - 1;
		}
		type = BattleActionType.values()[typeIdx];
		actionIdx = 0;
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
	}

	public BattleAction getSelected() {
		assert cmd != null : "BAMWs CMD is null";
		return selected;
	}
	
	public boolean isSelected(String name){
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
		List<BattleAction> actionList = cmd.getAll(type, cmd.getUser().getStatus());
		Collections.sort(actionList);
		for (BattleAction b : actionList) {
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
			if (type == BattleActionType.MAGIC || type == BattleActionType.SPECIAL_ATTACK) {
				String status = "";
				for (String s : statusKey) {
					int val = cmd.getUser().getStatus().calcSelfStatusDamage(b.getName(), s);
					status += " " + s + ":" + val;
				}
				if (type == BattleActionType.MAGIC) {
					status += " (" + I18N.translate("SPELLTIME") + ":" + b.getSpellTime() + I18N.translate("TURN") + ")";
				}
				text += b.getName() + " : " + b.getDesc() + status + Text.getLineSep();
			} else if (type == BattleActionType.OTHER) {
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
		if (actionIdx >= cmd.getAll(type, cmd.getUser().getStatus()).size()) {
			actionIdx = 0;
		}
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
	}

	public void prevAction() {
		actionIdx--;
		if (actionIdx < 0) {
			actionIdx = cmd.getAll(type, cmd.getUser().getStatus()).size() - 1;
		}
		updateText();
		if (GameSystem.isDebugMode()) {
			System.out.println("SELECT:" + selected);
		}
	}

}
