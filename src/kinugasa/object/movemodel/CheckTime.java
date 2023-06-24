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

import kinugasa.object.BasicSprite;
import kinugasa.object.MovingModel;
import kinugasa.util.TimeCounter;

/**
 * .
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/02/10_3:00:25<br>
 * @author Shinacho<br>
 */
public class CheckTime extends MovingModel {

	private TimeCounter timeCounter;

	public CheckTime(TimeCounter timeCounter) {
		this.timeCounter = timeCounter;
	}

	public void setTimeCounter(TimeCounter timeCounter) {
		this.timeCounter = timeCounter;
	}

	public TimeCounter getTimeCounter() {
		return timeCounter;
	}

	@Override
	public void move(BasicSprite s) {
		if (timeCounter.isReaching()) {
			s.setExist(false);
		}
	}

	@Override
	public CheckTime clone() {
		CheckTime result = (CheckTime) super.clone();
		result.timeCounter = this.timeCounter.clone();
		return result;
	}
}
