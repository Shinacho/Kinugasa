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
			kinugasa.game.GameLog.print("XML ATTR KEY NOT FOUND");
			throw ex;
		}
	}

}
