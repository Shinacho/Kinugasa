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
package kinugasa.game.field4;

import kinugasa.game.system.GameSystemI18NKeys;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;

/**
 *
 * @vesion 1.0.0 - 2022/11/11_12:27:18<br>
 * @author Shinacho<br>
 */
public class SimpleTooltipModel extends TooltipModel {

	private TextLabelSprite label;

	public SimpleTooltipModel() {
		label = new TextLabelSprite("", new SimpleTextLabelModel(FontModel.DEFAULT), 0, 0, 0, 0);
		setVisible(true);
	}

	@Override
	public void drawTooltip(FieldMap fm, GraphicsContext g) {
		if (!visible) {
			label.setVisible(false);
			return;
		}
		String s = FieldMap.getEnterOperation();
		FieldMapTile t = fm.getCurrentTile();
		mode = Mode.NONE;
		if (t.getEvent() != null
				&& !t.getEvent().isEmpty()
				&& t.getEvent().stream().anyMatch(p -> p.getEventType() == FieldEventType.MANUAL_EVENT)) {
			mode = Mode.SEARCH;
		}
		if (!FieldMap.getPlayerCharacter().isEmpty() && fm.canTalk()) {
			mode = Mode.TALK;
		}
		if (t.getNode() != null) {
			if (t.getNode().getMode() == Node.Mode.INOUT) {
				mode = Mode.NODE;
			}
		}
		if (fm.getMessageWindow() != null && fm.getMessageWindow().isVisible()) {
			mode = Mode.NONE;
		}
		if (FieldEventSystem.getInstance().isExecuting() || !FieldEventSystem.getInstance().isUserOperation()) {
			mode = Mode.NONE;
		}

		switch (mode) {
			case NODE:
				if (t.getNode() != null) {
					if (t.getNode().getMode() != Node.Mode.OUT) {
						s += t.getNode().getTooltip();
						label.setText(s);
						label.setVisible(true);
					} else {
						label.setVisible(false);
					}
				} else {
					label.setVisible(false);
				}
				break;
			case NONE:
				label.setVisible(false);
				break;
			case TALK:
				if (fm.canTalk()) {
					s += I18N.get(GameSystemI18NKeys.話す);
					label.setText(s);
					label.setVisible(true);
				} else {
					label.setVisible(false);
				}
				break;
			case SEARCH:
				if (!t.getEvent().isEmpty()) {
					if (t.getEvent().stream().anyMatch(p -> p.getEventType() == FieldEventType.MANUAL_EVENT)) {
						if (fm.getMessageWindow() != null && fm.getMessageWindow().isVisible()) {
							label.setVisible(false);
						} else {
							s += I18N.get(GameSystemI18NKeys.調べる);
							label.setText(s);
							label.setVisible(true);
						}
					} else {
						label.setVisible(false);
					}
				} else {
					label.setVisible(false);
				}
				break;
		}
		if (label.isVisible()) {
			FontModel f = label.getLabelModel().getFontConfig();
			float x = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 2 - (f.getFont().getSize2D() * label.getText().length() / 2);
			float y = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 2 - GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 4;
			label.setLocation(x, y);
			g.draw(label);
		}
	}

}
