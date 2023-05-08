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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.util.Versions;

/**
 *
 * @vesion 1.0.0 - 2022/11/12_21:21:47<br>
 * @author Shinacho<br>
 */
public class TitleImage {

	public static void main(String[] args) {
		BufferedImage image = ImageUtil.newImage(720, 480);
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.QUALITY);
		g.setColor(new Color(0, 32, 66));
		g.fillRect(0, 0, 720, 480);
		g.setColor(Color.WHITE);
		Font f = new Font(Font.SERIF, Font.PLAIN, 40);
		g.setFont(f);
		g.drawString("Fuzzy World", 24, 70);
		
		f = new Font(Font.SERIF, Font.PLAIN, 32);
		g.setFont(f);
		g.drawString("-ñÇñ@égÇ¢Ç∆ïséÄÇÃîÈèp-", 38, 120);
		//16,85,240,85
		g.drawLine(16, 85, 240, 85);

		f = new Font(Font.SERIF, Font.PLAIN, 16);
		g.setFont(f);
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(Versions.COPY_RIGHT, 24, 470);

		g.dispose();
		ImageUtil.save("resource/test/title.png", image);
	}
}
