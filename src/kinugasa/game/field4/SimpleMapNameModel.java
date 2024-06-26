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

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.graphics.FadeCounter;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/11/11_12:30:00<br>
 * @author Shinacho<br>
 */
public class SimpleMapNameModel extends MapNameModel {

	private int stage = 0;
	private TextLabelSprite label;
	private FadeCounter labelFont = FadeCounter.fadeOut(-1);
	private FadeCounter back = FadeCounter.fadeOut(-1);
	private BasicSprite backSprite;
	private Color backColor = Color.BLACK;

	public SimpleMapNameModel() {
		label = new TextLabelSprite("", new SimpleTextLabelModel(FontModel.DEFAULT), 1, 64, 1, 16);
		backSprite = new BasicSprite() {
			@Override
			public void draw(GraphicsContext g) {
				Graphics2D g2 = g.create();
				g2.setColor(backColor);
				g2.fillRect((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
				g2.dispose();
			}
		};
	}

	@Override
	public void reset() {
		stage = 0;
	}

	@Override
	public void drawMapName(FieldMap fm, GraphicsContext g) {
		Graphics2D g2 = g.create();
		switch (stage) {
			case 0:
				String val = I18N.get(fm.getName());
				label.setText(val);
				float centerX = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 2;
				float width = (label.getText().length() * label.getLabelModel().getFontSize());
				float x = centerX - (width / 2);
				label.setX(x);
				label.setWidth(width);
				backSprite.setLocation(0, 64);
				backSprite.setSize(GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize(), 16);
				labelFont = FadeCounter.fadeOut(-1);
				back = FadeCounter.fadeOut(-1);
				backColor = backColor();
				FontModel f = label.getLabelModel().getFontConfig().clone();
				f.setColor(fontColor());
				TextLabelModel m = new SimpleTextLabelModel(f);
				label.setLabelModel(m);
				label.setVisible(true);
				backSprite.setVisible(true);
				stage = 1;
				break;
			case 1:
				backSprite.draw(g2);
				label.draw(g2);
				labelFont.update();
				back.update();
				FontModel ff = label.getLabelModel().getFontConfig().clone();
				ff.setColor(fontColor());
				backColor = backColor();
				TextLabelModel mm = new SimpleTextLabelModel(ff);
				label.setLabelModel(mm);
				if (labelFont.isEnded()) {
					stage = 2;
				}
				break;
			case 2:
				label.setVisible(false);
				backSprite.setVisible(false);
				break;
			default:
				throw new AssertionError();
		}
		g2.dispose();
	}

	private Color fontColor() {
		return new Color(255, 255, 255, labelFont.getValue());
	}

	private Color backColor() {
		return new Color(0, 0, 0, back.getValue());
	}

}
