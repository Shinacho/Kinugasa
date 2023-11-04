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
import java.util.Map;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2022/12/23_19:50:14<br>
 * @author Shinacho<br>
 */
public class EqipItemWindow extends PCStatusWindow {

	private ScrollSelectableMessageWindow mw;
	private List<Status> s;

	public EqipItemWindow(int x, int y, int w, int h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new ScrollSelectableMessageWindow(x, y, w, h, 23);
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

	public void updateText() {
		StringBuilder sb = new StringBuilder();
		Actor a = GameSystem.getInstance().getPCbyID(s.get(pcIdx).getId());
		sb.append("<---").append(I18N.get(GameSystemI18NKeys.Xの装備,
				a.getVisibleName()))
				.append("--->").append(Text.getLineSep());

		for (EqipSlot s : EqipSlot.values()) {
			String key = s.getVisibleName();
			if (a.getStatus().getRace().getEqipSlots().contains(s)) {
				String value = a.getStatus().getEqip().get(s) == null ? I18N.get(GameSystemI18NKeys.なし) : a.getStatus().getEqip().get(s).getVisibleName();
				sb.append("  ").append(key).append(":").append(value).append(Text.getLineSep());
			}
		}

		mw.setText(Text.split(new Text(sb.toString())));
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
	public void update() {
		mw.update();
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
