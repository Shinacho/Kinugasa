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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;

/**
 *
 * @vesion 1.0.0 - 2022/12/16_9:07:07<br>
 * @author Shinacho<br>
 */
public class BeforeClearBlackImage {

	public static void main(String[] args) {
		Color c = new Color(0, 0, 0, 72);
		BufferedImage image = ImageUtil.newImage(1440 / 2, 960 / 2);
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.SPEED);
		g.setColor(c);
		g.fillRect(0, 0, 1440 / 2, 960 / 2);
		g.dispose();
		ImageUtil.save(new File("resource/test/before1.png"), image);
		
	}
}
