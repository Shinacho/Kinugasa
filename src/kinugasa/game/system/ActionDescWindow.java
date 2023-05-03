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

import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2022/12/24_11:50:01<br>
 * @author Dra211<br>
 */
public class ActionDescWindow extends PCStatusWindow {

	private MessageWindow mw;
	private List<Status> s;

	public ActionDescWindow(float x, float y, float w, float h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		pageIdx = 0;
		update();
	}
	private int pcIdx;
	private int pageIdx = 0;

	@Override
	public void nextPc() {
		pcIdx++;
		if (pcIdx >= s.size()) {
			pcIdx = 0;
		}
	}

	@Override
	public void prevPc() {
		pcIdx--;
		if (pcIdx < 0) {
			pcIdx = s.size() - 1;
		}
	}

	@Override
	public int getPcIdx() {
		return pcIdx;
	}

	private static final int MAX_LINE = StatusDescWindow.line - 1;

	@Override
	public void update() {
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.translate("ACTION")).append(pageIdx + 1).append(Text.getLineSep());

		int i = 0;//開始までのカウント
		int j = 0;//開始後のカウント
		int start = pageIdx == 0 ? ((MAX_LINE) * pageIdx) : ((MAX_LINE) * pageIdx) + 1;
		for (CmdAction a : s.get(pcIdx).getActions().stream().filter(p -> p.isBattleUse()).sorted().collect(Collectors.toList())) {
			if (a.getType() == ActionType.ITEM_USE) {
				continue;
			}
			if (a.getType() == ActionType.OTHER) {
				continue;
			}
			if (i < start) {
				i++;
				continue;
			}
			sb.append("  ")
					.append(a.getName());
			//魔法は魔法ウインドウで見れるので表示しない
			if (a.getType() != ActionType.MAGIC) {
				sb.append("／");
				sb.append(a.getDesc());
			}
			sb.append("(")
					.append(I18N.translate("AREA"))
					.append(":")
					.append(a.getAreaWithEqip(s.get(pcIdx)))
					.append("、")
					.append(I18N.translate("ACTION_ATTR"))
					.append(":");
			sb.append(a.getBattleEvent()
					.stream()
					.filter(p -> !AttrDescWindow.getUnvisibleAttrName().contains(p.getAttr().getName()))
					.map(p -> p.getAttr().getDesc())
					.distinct()
					.collect(Collectors.toList()));
			sb.append("、")
					.append(I18N.translate("ACTION_EFFECT"))
					.append(":");

			//ENEMYが入っている場合、minを、そうでない場合はMAXを取る
			if (!a.getBattleEvent().isEmpty()) {
				if (a.getBattleEvent().stream().anyMatch(p -> p.getTargetType().toString().contains("ENEMY"))) {
					sb.append(Math.abs(a.getBattleEvent()
							.stream()
							.mapToInt(p -> (int) (p.getValue()))
							.min()
							.getAsInt()));
				} else {
					sb.append(Math.abs(a.getBattleEvent()
							.stream()
							.mapToInt(p -> (int) (p.getValue()))
							.max()
							.getAsInt()));
				}
			}

			sb.append(")");

			sb.append(Text.getLineSep());
			j++;
			if (j > MAX_LINE) {
				break;
			}
			updateHasNext();
		}

		mw.setText(new Text(sb.toString()));
		mw.allText();
	}
	private boolean hasNext = false;

	private void updateHasNext() {
		int pages = (int) s.get(pcIdx).getActions().stream()
				.filter(p -> p.getType() != ActionType.ITEM_USE)
				.filter(p -> p.getType() != ActionType.OTHER)
				.filter(p -> p.isBattleUse())
				.count() / MAX_LINE;
		hasNext = pageIdx < pages;
	}

	@Override
	public void nextPage() {
		pageIdx++;
		updateHasNext();
	}

	@Override
	public boolean hasNextPage() {
		return hasNext;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}
}
