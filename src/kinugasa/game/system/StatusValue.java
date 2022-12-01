/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
 * @vesion 1.0.0 - 2022/11/15_11:57:35<br>
 * @author Dra211<br>
 */
public class StatusValue implements Nameable, Cloneable {

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
		if (key.getMin() > value) {
			this.value = key.getMin();
		}
	}

	public void add(float value) {
		this.value += value;
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

}
