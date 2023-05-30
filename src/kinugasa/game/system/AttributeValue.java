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
