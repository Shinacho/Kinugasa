/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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
import java.util.Collection;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Storage;

/**
 * 1つのXML要素が持つ属性のマップです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/03/15_17:53:23.<br>
 * @author Shinacho (
 * <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public class XMLAttributeStorage extends Storage<XMLAttribute> implements Serializable {

	private static final long serialVersionUID = -4953330104834129110L;

	/**
	 * 新しいXMLAttributeStorageを作成します.
	 */
	public XMLAttributeStorage() {
	}

	/**
	 * 容量の初期値を指定して、新しいXMLAttributeStorageを作成します.
	 *
	 * @param initialSize Strageの初期容量を指定します。<br>
	 */
	public XMLAttributeStorage(int initialSize) {
		super(initialSize);
	}

	/**
	 * XMLAttributeのコレクションを全て追加したStrageを作成します.
	 *
	 * @param attributes
	 */
	public XMLAttributeStorage(Collection<XMLAttribute> attributes) {
		this(attributes.size());
		addAll(attributes);
	}

	/**
	 * 全ての属性をスペースで区切って並べた文字列を返します.
	 *
	 * @return 属性をkey=valueの形式で、スペースで区切って並べた文字列を返します。<br>
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (XMLAttribute attr : this) {
			result.append(attr);
			result.append(' ');
		}
		return result.toString();
	}

	@Override
	public XMLAttribute get(String key) throws NameNotFoundException {
		try {
			return super.get(key); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
		} catch (NameNotFoundException ex) {
			System.out.println("XML ATTR KEY NOT FOUND");
			printAll(System.out);
			throw ex;
		}
	}

}
