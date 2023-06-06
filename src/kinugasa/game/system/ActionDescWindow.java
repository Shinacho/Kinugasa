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
					.append(a.getName());
			//魔法は魔法ウインドウで見れるので表示しない
			if (a.getType() != ActionType.MAGIC) {
				sb.append("／");
				sb.append(a.getVisibleName());
			}
			sb.append("(")
					.append(I18N.get(GameSystemI18NKeys.範囲))
					.append(":")
					.append(a.getAreaWithEqip(s.get(pcIdx)))
					.append("、")
					.append(I18N.get(GameSystemI18NKeys.属性))
					.append(":");
			sb.append(a.getBattleEvent()
					.stream()
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
