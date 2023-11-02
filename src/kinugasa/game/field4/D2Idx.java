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
	public D2Idx clone() {
		try {
			return (D2Idx) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
