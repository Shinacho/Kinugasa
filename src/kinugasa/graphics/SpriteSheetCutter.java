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
package kinugasa.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import kinugasa.object.Model;

/**
 * スプライトシートの切り出しアルゴリズムをカプセル化します.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_13:00:58<br>
 * @author Shinacho<br>
 */
public abstract class SpriteSheetCutter extends Model {

	private static final long serialVersionUID = -1630361870659622865L;

	/**
	 * 画像を特定のアルゴリズムに従って切り出します.
	 *
	 * @param base この画像をもとに、画像を切り出します。この画像は変更されてはなりません。<br>
	 *
	 * @return 切り出された画像を、リスト形式として返します。<br>
	 *
	 * @throws RasterFormatException ベース画像のサイズが、このアルゴリズムに適切でない場合に投げることができます。<br>
	 */
	public abstract BufferedImage[] cut(BufferedImage base) throws RasterFormatException;

	@Override
	public SpriteSheetCutter clone() {
		return (SpriteSheetCutter) super.clone();
	}

}
