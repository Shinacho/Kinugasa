/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2021/11/29_16:52:17<br>
 * @author Dra211<br>
 */
public class FPSLabel extends BasicSprite {

	private GameTimeManager gtm;

	public FPSLabel(int x, int y) {
		super(x, y, 48, 12);
	}

	public void setGtm(GameTimeManager gtm) {
		this.gtm = gtm;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist() || gtm == null) {
			return;
		}
		Graphics2D g2 = g.create();
		g2.setColor(Color.CYAN);
		g2.setFont(FontModel.DEFAULT.clone().getFont());
		g2.drawString(gtm.getFPSStr(), (int) getX(), (int) getY());
		g2.dispose();
	}

}
