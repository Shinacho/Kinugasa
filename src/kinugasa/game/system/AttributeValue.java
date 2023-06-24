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
 * @vesion 1.0.0 - 2022/11/15_12:01:53<br>
 * @author Shinacho<br>
 */
public class AttributeValue implements Nameable, Cloneable, Comparable<AttributeValue> {

	private AttributeKey key;
	private float value, initial, min, max;

	public AttributeValue(AttributeKey key, float value, float initial, float min, float max) {
		this.key = key;
		this.value = value;
		this.initial = initial;
		this.min = min;
		this.max = max;
	}

	public boolean isZero() {
		return value == 0;
	}

	@Override
	public String getName() {
		return key.getName();
	}

	public AttributeKey getKey() {
		return key;
	}

	public void setKey(AttributeKey key) {
		this.key = key;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public void set(float value) {
		this.value = value;
	}

	public void add(float value) {
		this.value += value;
	}

	public float getInitial() {
		return initial;
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

	public void setInitial(float initial) {
		this.initial = initial;
	}

	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	@Override
	public AttributeValue clone() {
		try {
			return (AttributeValue) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

	@Override
	public int compareTo(AttributeValue o) {
		return key.compareTo(o.key);
	}

	@Override
	public String toString() {
		return "AttributeValue{" + "key=" + key + ", value=" + value + ", min=" + min + '}';
	}

}
