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

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * 入力デバイスの値によってスプライトを移動する方法を定義します.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_16:50:04<br>
 * @author Shinacho<br>
 */
public interface Controllable {

	/**
	 * 入力デバイスからの値によって、オブジェクトを移動します.
	 *
	 * @param xValue -1.0~1.0の間の入力値.キー操作では-1.0または1.0、ゲームパッドを使用すると可変の値が送信される.<br>
	 * @param yValue -1.0~1.0の間の入力値.キー操作では-1.0または1.0、ゲームパッドを使用すると可変の値が送信される.<br>
	 * @param s このオブジェクトが移動可能な領域.nullでない場合はこの領域から出てはならない.<br>
	 *
	 * @return 移動後のオブジェクトがs内に存在する場合はtrueを、sから出る(contains()がfalse)ため、移動できなかった場合はfalseを返す.<br>
	 */
	public boolean move(float xValue, float yValue, Shape s);

	/**
	 * 入力デバイスからの値によって、オブジェクトを移動します.
	 *
	 * @param p -1.0~1.0の間のXおよびY軸の入力値.キー操作では-1.0または1.0、ゲームパッドを使用すると可変の値が送信される.<br>
	 * @param s このオブジェクトが移動可能な領域.nullでない場合はこの領域から出てはならない.<br>
	 *
	 * @return 移動後のオブジェクトがs内に存在する場合はtrueを、sから出る(contains()がfalse)ため、移動できなかった場合はfalseを返す.<br>
	 */
	public boolean move(Point2D.Float p, Shape s);
}
