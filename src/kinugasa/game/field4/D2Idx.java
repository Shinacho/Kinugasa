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
package kinugasa.game.field4;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.resource.Nameable;

/**
 * 2次元配列の座標をカプセル化するクラスです. これは、表示座標と区別するために使用されます。
 *
 * @vesion 1.0.0 - 2022/11/09_18:18:35<br>
 * @author Shinacho<br>
 */
public class D2Idx implements Nameable, Cloneable {

	public int x;
	public int y;

	public D2Idx(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public D2Idx(D2Idx idx) {
		this(idx.x, idx.y);
	}

	@Override
	public final String getName() {
		return x + "," + y;
	}

	public Point2D.Float asPoint2D() {
		return new Point2D.Float(x, y);
	}

	@Override
	public String toString() {
		return "D2Idx{" + "x=" + x + ", y=" + y + '}';
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + this.x;
		hash = 19 * hash + this.y;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final D2Idx other = (D2Idx) obj;
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		return true;
	}

	@Override
	protected D2Idx clone() {
		try {
			return (D2Idx) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
