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
import java.util.stream.Collectors;
import kinugasa.game.I18N;
import static kinugasa.game.system.ActionType.攻撃;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.Text;

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
		getWindow().clearText();
		this.actions = actions;
		updateText();
		reset();
	}

	public void clear() {
		actions.clear();
	}

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
				case 攻撃:
					text += b.getVisibleName() + ":" + b.getSummary();
					if (b.getMainEvents().stream().anyMatch(p -> p.getAtkAttr() != null)) {
						text += ("、")
								+ (I18N.get(GameSystemI18NKeys.属性))
								+ (":");
						text += (b.getMainEvents()
								.stream()
								.filter(p -> p.getAtkAttr() != null)
								.map(p -> p.getAtkAttr().getVisibleName())
								.distinct()
								.collect(Collectors.toList()));
					}
					text += ("、")
							+ (I18N.get(GameSystemI18NKeys.基礎威力))
							+ (":");
					//ENEMYが入っている場合、minを、そうでない場合はMAXを取る
					text += (Math.abs(b.getMainEvents()
							.stream()
							.mapToInt(p -> (int) (p.getValue()))
							.map(p -> Math.abs(p))
							.sum()));
					break;
				case アイテム:
				case 魔法:
					//処理なし（入らない）
					break;
				case 行動:
					text += b.getVisibleName();
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
