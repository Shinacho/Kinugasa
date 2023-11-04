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

import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_11:23:39<br>
 * @author Shinacho<br>
 */
public class AttributeValue implements Nameable, Cloneable, Comparable<AttributeValue> {

	private AttributeKey key;
	private float value;

	public AttributeValue(AttributeKey key, float value) {
		this.key = key;
		this.value = value;
	}

	public AttributeKey getKey() {
		return key;
	}

	@Override
	public String getName() {
		return key.toString();
	}

	public void to1() {
		value = 1;
	}

	public void toZero() {
		value = 0;
	}

	public void add(float v) {
		value += v;
		if (value < 0) {
			value = 0;
		}
	}

	public void mul(float v) {
		value *= v;
		if (value < 0) {
			value = 0;
		}
	}

	public boolean isZero() {
		return value == 0;
	}

	public boolean isMinus() {
		return value < 0;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	@Override
	public AttributeValue clone() {
		try {
			return (AttributeValue) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public String toString() {
		return "AttributeValue{" + "key=" + key + ", value=" + value + '}';
	}

	@Override
	public int compareTo(AttributeValue o) {
		return key.compareTo(o.key);
	}

}
