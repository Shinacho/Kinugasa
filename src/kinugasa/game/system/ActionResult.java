package kinugasa.game.system;

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
import java.util.List;
import kinugasa.game.system.ActionResultType;
import kinugasa.object.AnimationSprite;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_21:41:03<br>
 * @author Dra211<br>
 */
public class ActionResult {

	private BattleActionTarget target;
	private List<List<ActionResultType>> resultType;

	private FrameTimeCounter waitTime;
	private List<AnimationSprite> animation;

	public ActionResult(BattleActionTarget target, List<List<ActionResultType>> resultType, FrameTimeCounter waitTime, List<AnimationSprite> animation) {
		this.target = target;
		this.resultType = resultType;
		this.waitTime = waitTime;
		this.animation = animation;
	}

	public BattleActionTarget getTarget() {
		return target;
	}

	public List<List<ActionResultType>> getResultType() {
		return resultType;
	}

	public FrameTimeCounter getWaitTime() {
		return waitTime;
	}

	public List<AnimationSprite> getAnimation() {
		return animation;
	}

	public boolean hasAnimation() {
		return animation != null && !animation.isEmpty();
	}

	public boolean only(ActionResultType type) {
		return resultType.stream().flatMap(p -> p.stream()).allMatch(p -> p == type);
	}

	@Override
	public String toString() {
		return "ActionResult{" + "target=" + target + ", resultType=" + resultType + ", waitTime=" + waitTime + ", animation=" + animation + '}';
	}

}
