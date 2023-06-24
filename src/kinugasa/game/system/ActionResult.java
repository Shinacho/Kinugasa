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
