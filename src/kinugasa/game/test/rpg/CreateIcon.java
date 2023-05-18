/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
package kinugasa.game.test.rpg;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import kinugasa.game.ui.FontModel;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;

/**
 *
 * @vesion 1.0.0 - 2022/12/08_16:24:43<br>
 * @author Shinacho<br>
 */
public class CreateIcon {

	public static void main(String[] args) {
		BufferedImage image = ImageUtil.newImage(48, 48);
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.SPEED);

		g.setColor(new Color(255, 96, 96));
		GradientPaint gp = new GradientPaint(0, 0, new Color(255,96,96), 48, 48, new Color(255, 48, 48));
		g.setPaint(gp);
		g.setFont(FontModel.DEFAULT.clone().setFontSize(48).setFontStyle(Font.BOLD).getFont());
		
		g.drawString("衣", 0, 42);

		g.dispose();
		ImageUtil.save("resource/test/icon.png", image);
	}
}
