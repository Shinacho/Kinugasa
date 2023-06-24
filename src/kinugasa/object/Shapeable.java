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

import java.awt.geom.Point2D;

/**
 * このインターフェースを実装したクラスは、マウスによる入力判定などの座標の内包が判定できます.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:45:24<br>
 * @author Shinacho<br>
 */
public interface Shapeable {

	public boolean contains(Point2D point);
}
