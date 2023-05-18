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
