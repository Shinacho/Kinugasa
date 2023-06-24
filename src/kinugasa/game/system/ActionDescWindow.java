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
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2022/12/24_11:50:01<br>
 * @author Shinacho<br>
 */
public class ActionDescWindow extends PCStatusWindow {

	private ScrollSelectableMessageWindow mw;
	private List<Status> s;

	public ActionDescWindow(int x, int y, int w, int h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new ScrollSelectableMessageWindow(x, y, w, h, SimpleMessageWindowModel.maxLine, false);
		mw.setLoop(true);
		updateText();
	}
	private int pcIdx;

	@Override
	public void setPcIdx(int pcIdx) {
		this.pcIdx = pcIdx;
		updateText();
	}

	@Override
	public void nextPc() {
		pcIdx++;
		if (pcIdx >= s.size()) {
			pcIdx = 0;
		}
		mw.reset();
		updateText();
	}

	@Override
	public void prevPc() {
		pcIdx--;
		if (pcIdx < 0) {
			pcIdx = s.size() - 1;
		}
		mw.reset();
		updateText();
	}

	@Override
	public int getPcIdx() {
		return pcIdx;
	}

	private static final int MAX_LINE = SimpleMessageWindowModel.maxLine - 1;

	private void updateText() {
		//SSMWに文字列を設定

		List<Text> t = new ArrayList<>();
		t.add(new Text("<---" + I18N.get(GameSystemI18NKeys.Xの行動, s.get(pcIdx).getName()) + "--->"));

		for (Action a : s.get(pcIdx).getActions()
				.stream()
				.filter(p -> p.isBattleUse())
				.sorted()
				.collect(Collectors.toList())) {
			StringBuilder sb = new StringBuilder();
			if (a.getType() == ActionType.ITEM) {
				//表示しない
				continue;
			}
			if (a.getType() == ActionType.OTHER) {
				//表示しない
				continue;
			}
			sb.append("  ")
					.append(a.getVisibleName());
			//魔法は魔法ウインドウで見れるので表示しない
			if (a.getType() != ActionType.MAGIC) {
				sb.append("／");
				sb.append(a.getDesc());
			}
			sb.append("(")
					.append(I18N.get(GameSystemI18NKeys.範囲))
					.append(":")
					.append(a.getAreaWithEqip(s.get(pcIdx)));

			sb.append("、")
					.append(I18N.get(GameSystemI18NKeys.属性))
					.append(":");
			sb.append(a.getBattleEvent()
					.stream()
					.filter(p -> p.getAttr() != null)
					.filter(p -> !AttrDescWindow.getUnvisibleAttrName().contains(p.getAttr().getName()))
					.map(p -> p.getAttr().getDesc())
					.distinct()
					.collect(Collectors.toList()));

			sb.append("、")
					.append(I18N.get(GameSystemI18NKeys.基礎威力))
					.append(":");

			//ENEMYが入っている場合、minを、そうでない場合はMAXを取る
			if (!a.getBattleEvent().isEmpty()) {
				if (a.getTargetOption().getDefaultTarget() == TargetOption.DefaultTarget.ENEMY) {
					sb.append(Math.abs(
							a.getBattleEvent()
									.stream()
									.mapToInt(p -> (int) (p.getValue()))
									.min()
									.getAsInt()));
				} else {
					sb.append(Math.abs(
							a.getBattleEvent()
									.stream()
									.mapToInt(p -> (int) (p.getValue()))
									.max()
									.getAsInt()));
				}
			}
			sb.append(")");

			t.add(new Text(sb.toString()));
		}
		mw.setText(t);
	}

	@Override
	public void update() {
		mw.update();
	}

	@Override
	public MessageWindow getWindow() {
		return mw.getWindow();
	}

	@Override
	public void next() {
		mw.nextSelect();
	}

	@Override
	public void prev() {
		mw.prevSelect();
	}

	@Override
	public void draw(GraphicsContext g
	) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}
}
