/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
package kinugasa.game.field;

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.ImageSprite;
import kinugasa.object.KVector;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/26_17:44:20<br>
 * @author Dra211<br>
 */
public class PlayerCharacter extends ImageSprite {

	private List<Animation> anime;
	private FourDirection dir = FourDirection.NORTH;
	private int currentAnimationIndex = dir.getIdx();

	public PlayerCharacter(int x, int y, int w, int h, String fileName) {
		super(x, y, w, h);
		List<Animation> anime = new ArrayList<>();
		anime.add(new Animation(new FrameTimeCounter(7), ImageUtil.rows(ImageUtil.load(fileName), 0, 32, 32)));
		anime.add(new Animation(new FrameTimeCounter(7), ImageUtil.rows(ImageUtil.load(fileName), 32, 32, 32)));
		anime.add(new Animation(new FrameTimeCounter(7), ImageUtil.rows(ImageUtil.load(fileName), 64, 32, 32)));
		anime.add(new Animation(new FrameTimeCounter(7), ImageUtil.rows(ImageUtil.load(fileName), 96, 32, 32)));
		this.anime = anime;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		g.drawImageF(anime.get(currentAnimationIndex).getCurrentImage().get(), getX(), getY());
	}

	@Override
	public void update() {
		super.update();
		KVector v = getVector();

		if (v.checkRange(KVector.NORTH, 90)) {
			dir = FourDirection.NORTH;
		} else if (v.checkRange(KVector.SOUTH, 90)) {
			dir = FourDirection.SOUTH;
		} else if (v.checkRange(KVector.WEST, 90)) {
			dir = FourDirection.WEST;
		} else if (v.checkRange(KVector.EAST, 90)) {
			dir = FourDirection.EAST;
		}
		currentAnimationIndex = dir.getIdx();
		anime.get(currentAnimationIndex).update();
	}

}
