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
package kinugasa.object.movemodel;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;
import kinugasa.object.BasicSprite;
import kinugasa.object.MovingModel;
import kinugasa.object.Sprite;
import kinugasa.util.TimeCounter;


/**
 * 単純追尾アルゴリズムです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/02/20_21:59:06<br>
 * @author Shinacho<br>
 */
public class Track extends MovingModel {

	private List<Sprite> targetList;
	private Sprite currentTarget;
	private TimeCounter updateTargetDelay;

	public Track(List<Sprite> targetList, TimeCounter updateTargetDelay) {
		this.targetList = targetList;
		this.updateTargetDelay = updateTargetDelay;
	}

	public Track(Sprite target, TimeCounter updateTargetDelay) {
		this(Arrays.asList(target), updateTargetDelay);
	}

	public Track(Sprite target) {
		this(Arrays.asList(target), TimeCounter.FALSE);
		currentTarget = target;
	}

	public List<Sprite> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<Sprite> targetList) {
		this.targetList = targetList;
	}

	public Sprite getCurrentTarget() {
		return currentTarget;
	}

	public void setCurrentTarget(Sprite currentTarget) {
		this.currentTarget = currentTarget;
	}

	public TimeCounter getUpdateTargetDelay() {
		return updateTargetDelay;
	}

	public void setUpdateTargetDelay(TimeCounter updateTargetDelay) {
		this.updateTargetDelay = updateTargetDelay;
	}

	public void serachMostNearTarget(BasicSprite sprite) {
		Point2D.Float spriteCenter = sprite.getCenter();
		double distance = currentTarget == null
				? Double.POSITIVE_INFINITY
				: spriteCenter.distance(currentTarget.getCenter());
		for (int i = 0, size = targetList.size(); i < size; i++) {
			double candidateDistance =
					spriteCenter.distance(targetList.get(i).getCenter());
			if (candidateDistance < distance) {
				distance = candidateDistance;
				currentTarget = targetList.get(i);
			}
		}
	}

	@Override
	public void move(BasicSprite s) {
		if (updateTargetDelay.isReaching()) {
			serachMostNearTarget(s);
		}
		if (currentTarget == null) {
			return;
		}
		s.getVector().setAngle(s.getCenter(), currentTarget.getCenter());
	}

	@Override
	public Track clone() {
		Track clone = (Track) super.clone();
		clone.updateTargetDelay = this.updateTargetDelay.clone();
		return clone;
	}
}
