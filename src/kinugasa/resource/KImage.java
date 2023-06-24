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
