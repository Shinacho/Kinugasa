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
 * XML�ɂ�����"����"��\���N���X�ł�.
 * <br>
 * �����͗v�f��0�ȏ�ݒ�ł���L�[�ƒl�̃y�A�ł��B<br>
 * ���Ƃ��΁A&lt;hoge fuga=piyo&gt;�ł́A
 * fuga�Ƃ������O��piyo�Ƃ����l�̑������\�z����܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/03/15_7:10:56.<br>
 * @author dra0211 ( <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public class XMLAttribute implements Nameable, Serializable {

	private static final long serialVersionUID = -2192715007477514049L;
	/** ���̑����̖��O�ł�. */
	private String name;
	/** ���̑����̒l�ł�. */
	private String value;

	/**
	 * ���O�ƒl���󕶎��̐V�����������쐬���܂�.
	 */
	public XMLAttribute() {
		name = value = "";
	}

	/**
	 * ���O�ƒl���w�肵�āA�V�����������쐬���܂�.
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
	 * �l�𐮐��Ƃ��Ď擾���܂�.
	 * @return �l��Integer.parseInt���g�p���Đ����ɕϊ����ĕԂ��܂��B<br>
	 * @throws NumberFormatException �l���ϊ��ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public int getIntValue() throws NumberFormatException {
		return Integer.parseInt(value);
	}

	/**
	 * �l�������ł��邩���������܂�.
	 * @return getIntValue���g�p�ł���ꍇtrue��Ԃ��܂��B<br>
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
	 * �l�������Ƃ��Ď擾���܂�.
	 * @return �l��Float.parseFloat���g�p���Ď����ɕϊ����ĕԂ��܂��B<br>
	 * @throws NumberFormatException �l���ϊ��ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public float getFloatValue() throws NumberFormatException {
		return Float.parseFloat(value);
	}

	/**
	 * �l��float�ł��邩���������܂�.
	 * @return getFloatValue���g�p�ł���ꍇ��true��Ԃ��܂��B<br>
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
	 * �l��_���l�Ƃ��Ď擾���܂�.
	 * @return �l��Boolean.parseBoolean���g�p���Ę_���l�ɕϊ����ĕԂ��܂��B<br>
	 */
	public boolean getBool() {
		return Boolean.parseBoolean(value);
	}

	/**
	 * ���̑����̖��O��ύX���܂�.
	 * ���O�́AXMLAttriobuteStorage����Q�Ƃ���邽�߁A
	 * �d����������Ȃ��ꍇ������܂��B<br>
	 * @param name �V�������O���w�肵�܂��B<br>
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ���̑����̒l��ύX���܂�.
	 * @param value �V�����l���w�肵�܂��B<br>
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * ���̑����̒l���擾���܂�.
	 * @return �l��Ԃ��܂��B<br>
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return name + "=" + value;
	}
}
