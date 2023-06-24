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
package kinugasa.game.system;

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
import java.util.List;
import kinugasa.object.Sprite;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_21:41:03<br>
 * @author Shinacho<br>
 */
public class ActionResult {

	private ActionTarget target;
	private List<List<ActionResultType>> resultType;

	private FrameTimeCounter waitTime;
	private List<Sprite> animation;

	public ActionResult(ActionTarget target, List<List<ActionResultType>> resultType, FrameTimeCounter waitTime, List<Sprite> animation) {
		this.target = target;
		this.resultType = resultType;
		this.waitTime = waitTime;
		this.animation = animation;
	}

	public ActionTarget getTarget() {
		return target;
	}

	public List<List<ActionResultType>> getResultType() {
		return resultType;
	}

	public FrameTimeCounter getWaitTime() {
		return waitTime;
	}

	public List<Sprite> getAnimation() {
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
