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
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_8:09:13<br>
 * @author Shinacho<br>
 */
public class OrderSelectWindow extends BasicSprite {

	public OrderSelectWindow(float x, float y, float w, float h) {
		super(x, y, w, h);
		window = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		init();
	}

	private void init() {
		List<Text> options = new ArrayList<>();
		for (Status s : GameSystem.getInstance().getPartyStatus()) {
			options.add(Text.of(
					GameSystem.getInstance().getPCbyID(s.getId()).getVisibleName()
					+ " : "
					+ I18N.get(s.getPartyLocation()
							== PartyLocation.BACK
									? GameSystemI18NKeys.後列
									: GameSystemI18NKeys.前列)));
		}
		window.setText(Choice.of(options, "ORDER_SELECT_WINDOW", I18N.get(GameSystemI18NKeys.前列後列設定)));
		window.showAllNow();
	}
	private MessageWindow window;

	public void nextPC() {
		window.nextSelect();
	}

	public void prevPC() {
		window.prevSelect();
	}

	public void changeOrder() {
		int idx = window.getSelect();
		PartyLocation pl = GameSystem.getInstance().getPartyStatus().get(idx).getPartyLocation();
		switch (pl) {
			case BACK:
				GameSystem.getInstance().getPartyStatus().get(idx).setPartyLocation(PartyLocation.FRONT);
				break;
			case FRONT:
				GameSystem.getInstance().getPartyStatus().get(idx).setPartyLocation(PartyLocation.BACK);
				break;
		}
		updateText();
	}

	private void updateText() {
		int selectId = window.getSelect();
		List<Text> options = new ArrayList<>();
		for (Status s : GameSystem.getInstance().getPartyStatus()) {
			options.add(Text.of(
					GameSystem.getInstance().getPCbyID(s.getId()).getVisibleName()
					+ " : "
					+ I18N.get(s.getPartyLocation() == PartyLocation.BACK
							? GameSystemI18NKeys.後列
							: GameSystemI18NKeys.前列)));
		}
		window.setText(Choice.of(options, "ORDER_SELECT_WINDOW", I18N.get(GameSystemI18NKeys.前列後列設定)));
		window.showAllNow();
		window.setSelect(selectId);
	}

	@Override
	public void update() {
		window.update();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		window.draw(g);
	}

}
