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

import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.game.system.GameSystemException;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_11:23:15<br>
 * @author Shinacho<br>
 */
public class StatusValue implements Nameable, Cloneable, Comparable<StatusValue> {

	private StatusKey key;
	private float value;
	private boolean useMax = false;
	private float min, max;

	public StatusValue(StatusKey key, float value) {
		this(key, value, 0, 0);
		useMax = false;
	}

	public StatusValue(StatusKey key, float value, float min, float max) {
		this.key = key;
		this.value = value;
		this.min = min;
		this.max = max;
		useMax = true;
		if (!key.isPercent()) {
			this.value = (int) value;
		}
	}

	public StatusKey getKey() {
		return key;
	}

	@Override
	public String getName() {
		return key.toString();
	}

	public void toKeyMax() {
		value = key.getMax();
	}

	public void toKeyMin() {
		value = key.getMin();
	}

	public void toZero() {
		value = 0;
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

	public void add(float v) {
		value += v;
		if (value > max && useMax) {
			value = max;
		}
		if (value < min) {
			value = min;
		}
		if (!key.isPercent()) {
			value = (int) value;
		}
	}

	public void addMax(float v) {
		max += v;
	}

	public void addValueAndMax(float v) {
		max += v;
		value += v;
		if (!key.isPercent()) {
			value = (int) value;
		}
	}

	public void mulValueAndMax(float v) {
		max *= v;
		value *= v;
		if (!key.isPercent()) {
			value = (int) value;
		}
	}

	public float get割合() {
		if (!useMax) {
			return 1;
		}
		assert max > 0 : "status max is 0";
		return value / max;
	}

	public void setBy割合(float v) {
		if (v < 0) {
			throw new GameSystemException("percentage is under 0");
		}
		value = max * v;
		if (!key.isPercent()) {
			value = (int) value;
		}
	}

	public void add(float v, boolean limit) {
		value += v;
		if (limit && value > max && useMax) {
			value = max;
		}
		if (limit && value < min) {
			value = min;
		}
		if (!key.isPercent()) {
			value = (int) value;
		}
	}

	public void mul(float v) {
		value *= v;
		if (value > max && useMax) {
			value = max;
		}
		if (value < min) {
			value = min;
		}
		if (!key.isPercent()) {
			value = (int) value;
		}
	}

	public void mulMax(float v) {
		if (useMax) {
			max *= v;
		}
	}

	public void toMax() {
		value = max;
	}

	public void toMin() {
		value = min;
	}

	public boolean isZero() {
		return value == 0;
	}
	
	public boolean isZeroOrMinus(){
		return value <= 0;
	}

	public boolean isMinus() {
		return value < 0;
	}

	public boolean isMax() {
		return value == max;
	}

	public boolean isMin() {
		return value == min;
	}

	public void setValue(float value) {
		this.value = value;
		if (!key.isPercent()) {
			this.value = (int) value;
		}
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public float getValue() {
		return value;
	}

	@Override
	public StatusValue clone() {
		try {
			return (StatusValue) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	public boolean isUseMax() {
		return useMax;
	}

	@Override
	public String toString() {
		if (useMax) {
			return "StatusValue{" + "key=" + key + ", value=" + value + ", max=" + max + '}';
		}
		return "StatusValue{" + "key=" + key + ", value=" + value + '}';
	}

	@Override
	public int compareTo(StatusValue o) {
		return key.compareTo(o.key);
	}

}
