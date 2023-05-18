/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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
