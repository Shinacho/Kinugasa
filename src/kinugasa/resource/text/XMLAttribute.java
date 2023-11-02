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
package kinugasa.resource.text;

import java.io.Serializable;
import java.util.Arrays;
import kinugasa.resource.Nameable;

/**
 * XMLにおける"属性"を表すクラスです.
 * <br>
 * 属性は要素に0個以上設定できるキーと値のペアです。<br>
 * たとえば、&lt;hoge fuga=piyo&gt;では、 fugaという名前でpiyoという値の属性が構築されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/03/15_7:10:56.<br>
 * @author Shinacho (
 * <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public class XMLAttribute implements Nameable, Serializable {

	private static final long serialVersionUID = -2192715007477514049L;
	/**
	 * この属性の名前です.
	 */
	private String name;
	/**
	 * この属性の値です.
	 */
	private String value;

	/**
	 * 名前と値が空文字の新しい属性を作成します.
	 */
	public XMLAttribute() {
		name = value = "";
	}

	/**
	 * 名前と値を指定して、新しい属性を作成します.
	 *
	 * @param name
	 * @param value
	 */
	public XMLAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * 値を整数として取得します.
	 *
	 * @return 値をInteger.parseIntを使用して整数に変換して返します。<br>
	 * @throws NumberFormatException 値が変換できない場合に投げられます。<br>
	 */
	public int getIntValue() throws NumberFormatException {
		return Integer.parseInt(value);
	}

	/**
	 * 値が整数であるかを検査します.
	 *
	 * @return getIntValueが使用できる場合trueを返します。<br>
	 */
	public boolean isIntValue() {
		try {
			getIntValue();
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * 値を実数として取得します.
	 *
	 * @return 値をFloat.parseFloatを使用して実数に変換して返します。<br>
	 * @throws NumberFormatException 値が変換できない場合に投げられます。<br>
	 */
	public float getFloatValue() throws NumberFormatException {
		return Float.parseFloat(value);
	}

	/**
	 * 値がfloatであるかを検査します.
	 *
	 * @return getFloatValueが使用できる場合にtrueを返します。<br>
	 */
	public boolean isFloatValue() {
		try {
			getFloatValue();
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * 値を論理値として取得します.
	 *
	 * @return 値をBoolean.parseBooleanを使用して論理値に変換して返します。<br>
	 */
	public boolean getBool() {
		return Boolean.parseBoolean(value);
	}

	public int[] safeParseInt(String separator) {
		String[] data = safeSplit(separator);
		return Arrays.stream(data).mapToInt(s -> Integer.parseInt(s)).toArray();
	}

	/**
	 * この属性の名前を変更します. 名前は、XMLAttriobuteStorageから参照されるため、 重複が許可されない場合があります。<br>
	 *
	 * @param name 新しい名前を指定します。<br>
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * この属性の値を変更します.
	 *
	 * @param value 新しい値を指定します。<br>
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * この属性の値を取得します.
	 *
	 * @return 値を返します。<br>
	 */
	public String getValue() {
		return value;
	}

	public <T extends Enum<T>> T of(Class<T> c) {
		T[] values = c.getEnumConstants();
		for (T t : values) {
			if (t.toString().equals(value)) {
				return t;
			}
		}
		throw new AssertionError("not found " + value);
	}

	public String[] safeSplit(String split) {
		if (value.contains(split)) {
			return value.split(split);
		}
		return new String[]{value};
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}
}
