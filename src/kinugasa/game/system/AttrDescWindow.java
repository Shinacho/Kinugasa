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
 * @vesion 1.0.0 - 2022/12/21_16:24:36<br>
 * @author Shinacho<br>
 */
public class AttrDescWindow extends PCStatusWindow {

	private ScrollSelectableMessageWindow mw;
	private List<Status> s;

	public AttrDescWindow(int x, int y, int w, int h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new ScrollSelectableMessageWindow(x, y, w, h, 23, false);
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

	private void updateText() {

		Text midashi1 = Text.noI18N("<---" + I18N.get(GameSystemI18NKeys.Xの被属性,
				GameSystem.getInstance().getPCbyID(s.get(pcIdx).getId()).getVisibleName()
		) + "--->");

		List<Text> l = new ArrayList<>();
		//ATTR_IN
		l.add(midashi1);
		for (AttributeValue v : s.get(pcIdx).getEffectedAttrIn().stream().sorted().collect(Collectors.toList())) {
			l.add(Text.noI18N("  " + v.getKey().getVisibleName() + ":" + (v.getValue() * 100) + '%'));
		}
		//ATTR_OUT
		Text midashi2 = Text.noI18N("---" + I18N.get(GameSystemI18NKeys.Xの与属性, s.get(pcIdx).getVisibleName()) + "---");
		l.add(midashi2);
		for (AttributeValue v : s.get(pcIdx).getEffectedAttrOut().stream().sorted().collect(Collectors.toList())) {
			l.add(Text.noI18N("  " + v.getKey().getVisibleName() + ":" + (v.getValue() * 100) + '%'));
		}
		//CND_REGIST
		Text midashi3 = Text.noI18N("---" + I18N.get(GameSystemI18NKeys.Xの状態異常耐性, s.get(pcIdx).getVisibleName()) + "---");
		l.add(midashi3);
		for (ConditionKey k : s.get(pcIdx).getEffectedConditionRegist().keySet().stream().sorted().collect(Collectors.toList())) {
			l.add(Text.noI18N("  " + k.getVisibleName() + ":" + (s.get(pcIdx).getEffectedConditionRegist().get(k) * 100) + '%'));
		}

		mw.setText(l);
	}

	@Override
	public void update() {
		mw.update();
	}

	@Override
	public void prev() {
		mw.prevSelect();
	}

	@Override
	public void next() {
		mw.nextSelect();
	}

	@Override
	public ScrollSelectableMessageWindow getWindow() {
		return mw;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}
}
