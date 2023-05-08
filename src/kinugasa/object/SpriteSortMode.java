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
