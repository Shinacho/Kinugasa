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

import kinugasa.game.GraphicsContext;
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
		FieldMapTile t = fm.getCurrentTile();
		if (t.getNode() == null) {
			label.setVisible(false);
			return;
		}
		if (t.getNode().getMode() == Node.Mode.OUT) {
			label.setVisible(false);
			return;
		}
		label.setText(t.getNode().getTooltip());
		FontModel f = label.getLabelModel().getFontConfig();
		float x = FieldMapStorage.getScreenWidth() / 2 - (f.getFont().getSize2D() * label.getText().length() / 2);
		float y = FieldMapStorage.getScreenHeight() / 2 - FieldMapStorage.getScreenHeight() / 4;
		label.setLocation(x, y);
		label.setVisible(true);
		g.draw(label);
	}

}
