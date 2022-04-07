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
package kinugasa.game.field;

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.FontModel;

/**
 *
 * @vesion 1.0.0 - 2021/11/29_7:32:25<br>
 * @author Dra211<br>
 */
public class SimpleToolTipModel extends TooltipModel {

	@Override
	public boolean accept(FieldMap map) {
		return map.getNodeMap().containsKey(map.getCurrentCharPoint());
	}

	@Override
	public void draw(GraphicsContext g, FieldMap map) {
		if (!accept(map)) {
			return;
		}
		Graphics2D g2 = g.create();
		g2.setColor(Color.WHITE);
		String msg = map.getNodeMap().get(map.getCurrentCharPoint()).getTooltip();
		if (msg != null) {
			g2.setFont(FontModel.DEFAULT.clone().getFont());
			g2.drawString(msg,
					map.getWidth() / 2 - msg.length() * 8,
					map.getHeight() / 2 - 32
			);
		}
		g2.dispose();
	}

}
