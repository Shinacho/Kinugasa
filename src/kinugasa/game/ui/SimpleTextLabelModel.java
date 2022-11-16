/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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
package kinugasa.game.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import kinugasa.game.GraphicsContext;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/06/18<br>
 * @author Dra<br>
 * <br>
 */
public class SimpleTextLabelModel extends TextLabelModel {

	private static int c = 0;

	public SimpleTextLabelModel(String name, FontModel font) {
		super(name, font);
	}

	public SimpleTextLabelModel(FontModel font) {
		super("LABELMODEL_" + c++, font);
	}
	private boolean textAntiAlias = true;

	@Override
	public void draw(GraphicsContext g, TextLabelSprite l) {
		Graphics2D g2 = g.create();
		setProperty(g2);
		if (textAntiAlias) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		g2.drawString(l.getText(), (int) l.getX(), (int) (l.getY() + getFontSize()));
		g2.dispose();
	}

	public boolean isTextAntiAlias() {
		return textAntiAlias;
	}

	public void setTextAntiAlias(boolean textAntiAlias) {
		this.textAntiAlias = textAntiAlias;
	}

}
