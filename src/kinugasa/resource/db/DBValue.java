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

	public long asLong() {
		if (value == null || value.isEmpty()) {
			return 0L;
		}
		return Long.parseLong(value);
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
		if (value.trim().isEmpty()) {
			return 0;
		}
		return Float.parseFloat(value);
	}

	public boolean asBoolean() {
		if (value.trim().isEmpty()) {
			return false;
		}
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
