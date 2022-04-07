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
package kinugasa.resource.text;

import java.io.Serializable;
import kinugasa.resource.Nameable;

/**
 * XMLにおける"属性"を表すクラスです.
 * <br>
 * 属性は要素に0個以上設定できるキーと値のペアです。<br>
 * たとえば、&lt;hoge fuga=piyo&gt;では、
 * fugaという名前でpiyoという値の属性が構築されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/03/15_7:10:56.<br>
 * @author dra0211 ( <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public class XMLAttribute implements Nameable, Serializable {

	private static final long serialVersionUID = -2192715007477514049L;
	/** この属性の名前です. */
	private String name;
	/** この属性の値です. */
	private String value;

	/**
	 * 名前と値が空文字の新しい属性を作成します.
	 */
	public XMLAttribute() {
		name = value = "";
	}

	/**
	 * 名前と値を指定して、新しい属性を作成します.
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
	 * @return 値をInteger.parseIntを使用して整数に変換して返します。<br>
	 * @throws NumberFormatException 値が変換できない場合に投げられます。<br>
	 */
	public int getIntValue() throws NumberFormatException {
		return Integer.parseInt(value);
	}

	/**
	 * 値が整数であるかを検査します.
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
	 * @return 値をFloat.parseFloatを使用して実数に変換して返します。<br>
	 * @throws NumberFormatException 値が変換できない場合に投げられます。<br>
	 */
	public float getFloatValue() throws NumberFormatException {
		return Float.parseFloat(value);
	}

	/**
	 * 値がfloatであるかを検査します.
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
	 * @return 値をBoolean.parseBooleanを使用して論理値に変換して返します。<br>
	 */
	public boolean getBool() {
		return Boolean.parseBoolean(value);
	}

	/**
	 * この属性の名前を変更します.
	 * 名前は、XMLAttriobuteStorageから参照されるため、
	 * 重複が許可されない場合があります。<br>
	 * @param name 新しい名前を指定します。<br>
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * この属性の値を変更します.
	 * @param value 新しい値を指定します。<br>
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * この属性の値を取得します.
	 * @return 値を返します。<br>
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}
}
