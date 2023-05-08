/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
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
package kinugasa.resource;

import java.awt.Image;
import java.awt.image.BufferedImage;
import kinugasa.graphics.ImageUtil;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:22:24<br>
 * @author Shinacho<br>
 */
public class KImage {
	
	private BufferedImage image;

	public KImage(Image image) {
	}

	public KImage(BufferedImage bimage) {
		this.image = bimage;
	}

	public int[][] getPixcels() {
		return ImageUtil.getPixel2D(image);
	}

	public int getpixcel(int x, int y) {
		return getPixcels()[y][x];
	}

	public int getWidth() {
		return getPixcels()[0].length;
	}

	;
	public int getHeight() {
		return getPixcels().length;
	}

	public Image asImage() {
		return image;
	}

	public BufferedImage get() {
		return image;
	}
	
	
	
	
}
