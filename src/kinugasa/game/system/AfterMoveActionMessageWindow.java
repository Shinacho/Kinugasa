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
	private List<Action> actions = new ArrayList();

	public List<Action> getActions() {
		return actions;
	}

	@Override
	public Action getSelectedCmd() {
		return actions.get(getSelectedIdx());
	}

	public void setActions(List<Action> actions) {
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
			Action b = actions.get(i);
			String text = "";
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
