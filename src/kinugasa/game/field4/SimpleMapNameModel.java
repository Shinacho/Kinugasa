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

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.graphics.FadeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/11_12:30:00<br>
 * @author Dra211<br>
 */
public class SimpleMapNameModel extends MapNameModel {

	private int stage = 0;
	private TextLabelSprite label;
	private FadeCounter labelFont = FadeCounter.fadeOut(-1);
	private FadeCounter back = FadeCounter.fadeOut(-1);

	public SimpleMapNameModel() {
		label = new TextLabelSprite("", new SimpleTextLabelModel(FontModel.DEFAULT), 0, 24, 1000, 20) {
			@Override
			public void draw(GraphicsContext g) {
				Graphics2D g2 = g.create();
				g2.setColor(backColor());
				g2.fillRect((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
				g2.dispose();
				super.draw(g); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
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
				label.setText(fm.getName());
				float x = FieldMapStorage.getScreenWidth() - 24 - label.getText().length() * label.getLabelModel().getFontSize();
				label.setX(x);
				labelFont = FadeCounter.fadeOut(-1);
				back = FadeCounter.fadeOut(-1);
				FontModel f = label.getLabelModel().getFontConfig().clone();
				f.setColor(fontColor());
				TextLabelModel m = new SimpleTextLabelModel(f);
				label.setLabelModel(m);
				label.setVisible(true);
				stage = 1;
				break;
			case 1:
				label.draw(g2);
				labelFont.update();
				back.update();
				FontModel ff = label.getLabelModel().getFontConfig().clone();
				ff.setColor(fontColor());
				TextLabelModel mm = new SimpleTextLabelModel(ff);
				label.setLabelModel(mm);
				if (labelFont.isEnded()) {
					stage = 2;
				}
				break;
			case 2:
				label.setVisible(false);
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
