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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.I18N;
import kinugasa.game.LoopCall;
import kinugasa.game.NoLoopCall;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_15:37:41<br>
 * @author Shinacho<br>
 */
public class BattleCommandMessageWindow extends ScrollSelectableMessageWindow implements CommandWindow {

	private BattleCommand cmd;

	public BattleCommandMessageWindow(int x, int y, int w, int h) {
		super(x, y, w, h, 7, false);
		setLoop(true);
		updateText();
	}
	private int typeIdx = ActionType.values().length - 1;
	private ActionType type = ActionType.OTHER;
	private Action selected;

	public void resetSelect() {
		setType(ActionType.OTHER);
		reset();
	}

	public void setType(ActionType t) {
		typeIdx = t.ordinal();
		type = ActionType.values()[typeIdx];
		updateText();
	}

	public void nextType() {
		typeIdx++;
		if (typeIdx >= ActionType.values().length) {
			typeIdx = 0;
		}
		type = ActionType.values()[typeIdx];
		updateText();
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("BCMW :" + selected);
		}
		setCurrent();
	}

	public void prevType() {
		typeIdx--;
		if (typeIdx < 0) {
			typeIdx = ActionType.values().length - 1;
		}
		type = ActionType.values()[typeIdx];
		updateText();
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("BCMW :" + selected);
		}
		setCurrent();
	}

	private void setCurrent() {
		GameSystem.getInstance().getBattleSystem().getTargetSystem().setCurrent(this);
	}

	@Override
	public Action getSelectedCmd() {
		assert cmd != null : "BCMW cmd is null";
		return selected;
	}

	public boolean isSelected(String name) {
		return selected.getName().equals(name);
	}

	public BattleCommand getCmd() {
		return cmd;
	}

	public void setCmd(BattleCommand cmd) {
		this.cmd = cmd;
		updateText();
	}

	@LoopCall
	@Override
	public void update() {
		super.update();
	}

	@NoLoopCall
	private void updateText() {
		if (cmd == null) {
			if (GameSystem.isDebugMode()) {
				kinugasa.game.GameLog.print("BCMW : cmd is null");
			}
			return;
		}
		String text = cmd.getUser().getStatus().getName();
		text = I18N.get(GameSystemI18NKeys.どうする) + ", " + text + " ! ! ";
		text += "         <------------------[ " + type.displayName() + " ]------------------>";
		text += Text.getLineSep();
		int i = 0;
		cmd.getUser().getStatus().updateAction(true);
		List<Action> actionList = cmd.getBattleActionOf(type);
		//アイテム以外の場合はバトル利用可能なアクションにフィルター
		if (type != ActionType.ITEM) {
			actionList = actionList.stream().filter(p -> p.isBattleUse()).collect(Collectors.toList());
		}

		Collections.sort(actionList);

		if (actionList.isEmpty()) {
			switch (type) {
				case ATTACK:
				case OTHER:
					throw new GameSystemException("ATTACK,OTHER action is empty");
				case ITEM:
					text += I18N.get(GameSystemI18NKeys.何も持っていない) + Text.getLineSep();
					break;
				case MAGIC:
					text += I18N.get(GameSystemI18NKeys.使える魔術はない) + Text.getLineSep();
					break;
				default:
					throw new AssertionError("BattleCommandMessageWindow : undefined type");
			}
			List<Text> t = Text.split(new Text(text));
			setText(t);
			super.getWindow().allText();
			selected = null;
			setCurrent();
			return;
		}

		selected = actionList.get(0);
		for (Action b : actionList) {
			switch (type) {
				case ATTACK:
					text += b.getName() + ":" + b.getDesc();
					text += ("、")
							+ (I18N.get(GameSystemI18NKeys.属性))
							+ (":");
					text += (b.getBattleEvent()
							.stream()
							.filter(p -> !AttrDescWindow.getUnvisibleAttrName().contains(p.getAttr().getName()))
							.map(p -> p.getAttr().getDesc())
							.distinct()
							.collect(Collectors.toList()));
					text += ("、")
							+ (I18N.get(GameSystemI18NKeys.基礎威力))
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
					text += Text.getLineSep();
					break;
				case ITEM:
					Item item = (Item) b;
					String e = (item.getEqipmentSlot() != null && getCmd().getUser().getStatus().isEqip(item.getName()))
							? "(E)"
							: "";
					text += e + b.getName() + Text.getLineSep();
					break;
				case MAGIC:
					String status = "";
					for (String s : BattleConfig.getMagicVisibleStatusKey()) {
						Map<StatusKey, Integer> map = b.selfBattleDirectDamage();
						int val = map.containsKey(StatusKeyStorage.getInstance().get(s)) ? map.get(StatusKeyStorage.getInstance().get(s)) : 0;
						status += " " + StatusKeyStorage.getInstance().get(s).getDesc() + ":" + val;
					}
					status += " (" + I18N.get(GameSystemI18NKeys.詠唱時間) + ":" + b.getSpellTime() + I18N.get(GameSystemI18NKeys.ターン) + ")";
					text += b.getName() + " : " + status + Text.getLineSep();
					break;
				case OTHER:
					text += b.getName() + Text.getLineSep();
					break;
				default:
					throw new AssertionError("BCMW undefined type");
			}

			i++;
		}

		List<Text> t = Text.split(new Text(text));
		setText(t);
		super.getWindow().allText();
	}

	public void nextAction() {
		List<Action> actionList = cmd.getBattleActionOf(type);
		if (actionList.isEmpty()) {
			return;
		}
		super.nextSelect();
		Collections.sort(actionList);
		selected = actionList.get(getSelectedIdx() - 1);
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("MCMW :" + selected);
		}
		setCurrent();
	}

	public void prevAction() {
		super.prevSelect();
		List<Action> actionList = cmd.getBattleActionOf(type);
		if (actionList.isEmpty()) {
			return;
		}
		Collections.sort(actionList);
		selected = actionList.get(getSelectedIdx() - 1);
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("MCMW :" + selected);
		}
		setCurrent();
	}

}
