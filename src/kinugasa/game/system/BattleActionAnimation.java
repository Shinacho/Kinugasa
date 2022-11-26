/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.system;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.game.GraphicsContext;
import kinugasa.object.AnimationSprite;
import kinugasa.object.CompositeSprite;
import kinugasa.object.Drawable;

/**
 *
 * @vesion 1.0.0 - 2022/11/22_20:05:55<br>
 * @author Dra211<br>
 */
public class BattleActionAnimation implements Drawable, Cloneable {

	private AnimationSprite animationSprite;
	//アニメーションターゲット
	private BattleActionAnimationTargetType animationTargetType;

	public BattleActionAnimation(AnimationSprite animationSprite, BattleActionAnimationTargetType animationTargetType) {
		this.animationSprite = animationSprite;
		this.animationTargetType = animationTargetType;
		assert animationSprite != null;
	}

	public AnimationSprite getAnimationSprite() {
		return animationSprite;
	}

	public BattleActionAnimationTargetType getAnimationTargetType() {
		return animationTargetType;
	}

	public boolean isEnded() {
		return animationSprite.getAnimation().isEnded();
	}

	@Override
	public void draw(GraphicsContext g) {
		animationSprite.draw(g);
	}

	public BattleActionAnimation clone() {
		BattleActionAnimation a;
		try {
			a = (BattleActionAnimation) super.clone();
			a.animationSprite = this.animationSprite.clone();
			return a;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}
}
