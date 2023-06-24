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

import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_11:57:35<br>
 * @author Shinacho<br>
 */
public class StatusValue implements Nameable, Cloneable, Comparable<StatusValue> {

	private StatusKey key;
	private float value, initial, min, max;

	public StatusValue(StatusKey key, float value, float initial, float min, float max) {
		this.key = key;
		this.value = value;
		this.initial = initial;
		this.min = min;
		this.max = max;
	}

	public StatusValue(StatusValue v) {
		this(v.getKey(), v.value, v.initial, v.min, v.max);
	}

	public StatusKey getKey() {
		return key;
	}

	public void setKey(StatusKey key) {
		this.key = key;
	}

	public boolean isZero() {
		return value <= 0;
	}

	public float getValue() {
		return value;
	}

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public void set(float value) {
		this.value = value;
	}

	public void addNoLimit(float value) {
		this.value += value;
	}

	public void add(float value) {
		this.value += value;
		if (getMax() < this.value) {
			this.value = getMax();
		}
		if (getMin() > this.value) {
			this.value = getMin();
		}
		if (key.getMax() < this.value) {
			this.value = key.getMax();
		}
		if (key.getMin() > this.value) {
			this.value = key.getMin();
		}
	}

	public float getInitial() {
		return initial;
	}

	public void setInitial(float initial) {
		this.initial = initial;
	}

	public void toInitial() {
		setValue(initial);
	}

	public void saveInitial() {
		initial = value;
	}

	public void setInitialIfChange() {
		if (value != initial) {
			toInitial();
		}
	}

	public void setMaxIfOverMax() {
		if (value > max) {
			value = max;
		}
	}

	public void toMax() {
		value = key.getMax();
	}

	public void toMin() {
		value = key.getMin();
	}

	@Override
	public String getName() {
		return key.getName();
	}

	@Override
	public StatusValue clone() {
		try {
			StatusValue r = (StatusValue) super.clone();
			return r;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

	@Override
	public String toString() {
		return "StatusValue{" + "key=" + key + ", value=" + value + ", initial=" + initial + '}';
	}

	@Override
	public int compareTo(StatusValue o) {
		return key.compareTo(o.key);
	}

}
