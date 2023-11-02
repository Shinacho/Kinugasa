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
 * @vesion 1.0.0 - 2022/12/21_12:42:59<br>
 * @author Shinacho<br>
 */
public class ConditionDescWindow extends PCStatusWindow {

	private ScrollSelectableMessageWindow mw;
	private List<Status> s;

	public ConditionDescWindow(int x, int y, int w, int h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new ScrollSelectableMessageWindow(x, y, w, h, SimpleMessageWindowModel.maxLine, false);
		mw.setLoop(true);
		updateText();
	}

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

	private int pcIdx;

	private void updateText() {
		StringBuilder sb = new StringBuilder();
		sb.append("<---");
		sb.append(I18N.get(GameSystemI18NKeys.Xの発生中の効果,
				GameSystem.getInstance().getPCbyID(s.get(pcIdx).getId()).getVisibleName())).append("--->");
		List<Text> l = new ArrayList<>();
		l.add(new Text(sb.toString()));
		l.addAll(s.get(pcIdx).getCurrentConditions().keySet().stream().map(p -> new Text(p.getVisibleName())).collect(Collectors.toList()));
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
	public MessageWindow getWindow() {
		return mw.getWindow();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}

}
