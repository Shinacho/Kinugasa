/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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
package kinugasa.resource.db;

/**
 *
 * @vesion 1.0.0 - May 26, 2023_12:27:19 PM<br>
 * @author Shinacho<br>
 */
public class DBValue {

	private String columnName;
	private String value;

	DBValue(String columnName, String value) {
		this.columnName = columnName;
		this.value = value;
	}

	DBValue(String value) {
		this.value = value;
	}

	public String getColumnName() {
		return columnName;
	}

	public String get() {
		return value;
	}

	public int asInt() {
		if (value == null || value.isEmpty()) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	public int[] asIntArray(String sep) {
		String[] v = safeSplit(sep);
		int[] res = new int[v.length];
		for (int i = 0; i < v.length; i++) {
			res[i] = Integer.parseInt(v[i]);
		}
		return res;
	}

	public float[] asFloatArray(String sep) {
		String[] v = safeSplit(sep);
		float[] res = new float[v.length];
		for (int i = 0; i < v.length; i++) {
			res[i] = Float.parseFloat(v[i]);
		}
		return res;
	}

	public float asFloat() {
		return Float.parseFloat(value);
	}

	public boolean asBoolean() {
		return Boolean.parseBoolean(value);
	}

	public String[] safeSplit(String sep) {
		if (value == null) {
			return new String[]{};
		}
		if (value.contains(sep)) {
			return value.split(sep);
		}
		if (value.isEmpty()) {
			return new String[]{};
		}
		return new String[]{value};
	}

	public <T extends Enum<T>> T of(Class<T> c) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		T[] values = c.getEnumConstants();
		for (T t : values) {
			if (t.toString().equals(value.toUpperCase())) {
				return t;
			}
		}
		throw new AssertionError("DBValue : not found " + value);
	}

	@Override
	public String toString() {
		return get();
	}

}
