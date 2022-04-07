/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
package kinugasa.object;

import kinugasa.resource.Nameable;

/**
 * キャラクタなどのゲーム内オブジェクトの性質や能力を数値表現するための項目です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/02/10_2:19:42<br>
 * @author Dra0211<br>
 */
public class Parameter implements Nameable, Cloneable {

	private String name;
	private String description;
	private float value;
	private float maxValue;
	private float minValue = 0f;

	public Parameter(String name, float value) {
		this(name, value, Float.POSITIVE_INFINITY);
	}

	public Parameter(String name, float value, float maxValue) {
		this(name, value, 0, maxValue);
	}

	public Parameter(String name, float value, float minValue, float maxValue) {
		this.name = name;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public Parameter(String name, String discription, float value) {
		this(name, discription, value, Float.POSITIVE_INFINITY);
	}

	public Parameter(String name, String discription, float value, float maxValue) {
		this(name, discription, value, 0, maxValue);
	}

	public Parameter(String name, String discription, float value, float minValue, float maxValue) {
		this.name = name;
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.description = discription;
	}

	public void toMax() {
		value = maxValue;
	}

	public void toMin() {
		value = minValue;
	}

	public boolean isZero() {
		return value == 0f;
	}

	public boolean ltMin() {
		return value < minValue;
	}

	public boolean leMin() {
		return value <= minValue;
	}

	public boolean gtMax() {
		return value > maxValue;
	}

	public boolean geMax() {
		return value >= maxValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	public float getMinValue() {
		return minValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public float get() {
		return value;
	}

	public void set(float value) {
		this.value = value;
	}

	public int getInt() {
		return (int) value;
	}

	public void add(float value) {
		this.value += value;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 53 * hash + Float.floatToIntBits(this.value);
		hash = 53 * hash + Float.floatToIntBits(this.maxValue);
		hash = 53 * hash + Float.floatToIntBits(this.minValue);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Parameter other = (Parameter) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if (Float.floatToIntBits(this.value) != Float.floatToIntBits(other.value)) {
			return false;
		}
		if (Float.floatToIntBits(this.maxValue) != Float.floatToIntBits(other.maxValue)) {
			return false;
		}
		if (Float.floatToIntBits(this.minValue) != Float.floatToIntBits(other.minValue)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Parameter{" + "name=" + name + ", value=" + value + ", maxValue=" + maxValue + ", minValue=" + minValue + '}';
	}

	@Override
	public Parameter clone() {
		try {
			return (Parameter) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}
}
