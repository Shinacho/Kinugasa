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
package kinugasa.object;

import kinugasa.game.GraphicsContext;
import kinugasa.resource.Nameable;

/**
 * 画像を描画する方法をカプセル化します.
 * <br>
 * このモデルは、通常クローニングされないため、ImageSpriteでのクローンでは複製されません。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:30:09<br>
 * @author Shinacho<br>
 */
public abstract class ImagePainter extends Model implements Nameable {

	private final String name;

	public ImagePainter(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public abstract void draw(GraphicsContext g, ImageSprite sprite);

	@Override
	public ImagePainter clone() {
		return (ImagePainter) super.clone();
	}
}
