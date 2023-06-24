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

import java.util.Comparator;
import kinugasa.util.Random;

/**
 * CompositeSpriteのソート方向を指定するコンパレータです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_16:20:44<br>
 * @author Shinacho<br>
 */
public enum SpriteSortMode implements Comparator<Sprite> {

	/** この列挙を指定すると、描画順はZ軸座標の大きい順になります.
	 * したがって、Z軸座標の小さなスプライトが手前に表示されます. */
	FRONT_TO_BACK {
		@Override
		public int compare(Sprite s1, Sprite s2) {
			return java.lang.Float.compare(s2.getZ(), s1.getZ());
		}
	},
	/** この列挙を指定すると、描画順はZ軸座標の小さい順になります.
	 * したがって、Z軸座標の大きなスプライトが手前に表示されます. */
	BACK_TO_FRONT {
		@Override
		public int compare(Sprite s1, Sprite s2) {
			return java.lang.Float.compare(s1.getZ(), s2.getZ());
		}
	},
	/** この列挙を指定すると、使用するたびに異なる滅茶苦茶な順番に並び替えます.
	 * この列挙は21乱数ジェネレータ（twoone.util.Random）を使用します. */
	RANDOM {
		@Override
		public int compare(Sprite s1, Sprite s2) {
			return Random.randomAbsInt() % 3 - 1;
		}
	};

	@Override
	public abstract int compare(Sprite s1, Sprite s2);
}
