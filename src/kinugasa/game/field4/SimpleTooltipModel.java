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
package kinugasa.game.field4;

import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;

/**
 *
 * @vesion 1.0.0 - 2022/11/11_12:27:18<br>
 * @author Dra211<br>
 */
public class SimpleTooltipModel extends TooltipModel {

	private TextLabelSprite label;

	public SimpleTooltipModel() {
		label = new TextLabelSprite("", new SimpleTextLabelModel(FontModel.DEFAULT), 0, 0, 0, 0);
	}

	@Override
	public void drawTooltip(FieldMap fm, GraphicsContext g) {
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
					s += I18N.translate("TALK");
					label.setText(s);
					label.setVisible(true);
				} else {
					label.setVisible(false);
				}
				break;
			case SEARCH:
				if (!t.getEvent().isEmpty()) {
					if (t.getEvent().stream().anyMatch(p -> p.getEventType() == FieldEventType.MANUAL_EVENT)) {
						s += I18N.translate("SURVEY");
						label.setText(s);
						label.setVisible(true);
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
