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

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import kinugasa.game.GameLog;
import kinugasa.game.system.GameSystem;
import kinugasa.resource.Nameable;

/**
 * "要素"クラスは、XMLの1つのエレメントに対応します.
 * <br>
 * 要素とは次のような構成です。<br>
 * &lt;elementName attributeName=attributeValue&gt;<br>
 * &lt;contentElement&gt;<br>
 * &lt;/contentElement&gt;<br>
 * value<br>
 * &lt;/elementName attributeName=attributeValue&gt;<br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/03/15_7:07:44.<br>
 * @author Shinacho (
 * <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public class XMLElement implements Nameable {

	/**
	 * この要素の名前です.
	 */
	private String name;
	/**
	 * この要素の内部に設置された要素のマップです,
	 */
	private Map<String, List<XMLElement>> content;
	/**
	 * この要素が持つ属性です.
	 */
	private XMLAttributeStorage attributes;
	/**
	 * この要素の内部に直接書かれたテキストです.
	 */
	private String value;

	/**
	 * 子の要素を持たない要素を作成します.
	 *
	 * @param name この要素の名前です。<br>
	 * @param attributes この要素が持つ属性です。<br>
	 * @param value この要素のテキストです。<br>
	 */
	public XMLElement(String name, XMLAttributeStorage attributes, String value) {
		this.name = name;
		this.attributes = attributes;
		this.value = value;
	}

	/**
	 * 子要素を持つ要素を作成します.
	 *
	 * @param name この要素の名前です。<br>
	 * @param content このエレメントが持つ子のエレメントを送信します。<br>
	 * @param attributes この要素が持つ属性です。<br>
	 * @param value この要素のテキストです。<br>
	 */
	public XMLElement(String name, Map<String, List<XMLElement>> content, XMLAttributeStorage attributes, String value) {
		this.name = name;
		this.content = content;
		this.attributes = attributes;
		this.value = value;
	}

	/**
	 * この要素のテキストを取得します.
	 *
	 * @return この要素が持つテキストを返します。テキストがない場合は、NULLが返されます。<br>
	 */
	public String getValue() {
		return value;
	}

	/**
	 * この要素がテキストを持っているかを検査します.
	 *
	 * @return テキストを持つ場合はtrueを返します。この戻り値はgetValue()!=nullをと同じです。<br>
	 */
	public boolean hasValue() {
		return value != null;
	}

	/**
	 * この要素に設定された属性を取得します.
	 *
	 * @return 属性を格納したストレージを返します。<br>
	 */
	public XMLAttributeStorage getAttributes() {
		return attributes;
	}

	public boolean hasAttribute(String key) {
		return attributes.contains(key);
	}

	/**
	 * この要素が持つ子の要素を取得します.
	 *
	 * @return 子の要素を返します。子の要素が設定されていない場合はnullを返します。<br>
	 */
	public Map<String, List<XMLElement>> getContent() {
		return content;
	}

	/**
	 * 指定した名前を持つ子の要素を返します.
	 *
	 * @param name 要素の名前を指定します。<br>
	 * @return 指定した名前の要素のリストを返します。存在しない名前を指定した場合は nullを返します。<br>
	 */
	public List<XMLElement> getElement(String name) {
		if (content.get(name) == null) {
//			if (GameSystem.isDebugMode()) {
//				kinugasa.game.GameLog.printInfo("! > XMLElement[" + name + "] is nothing : " + getName());
//			}
			return Collections.emptyList();
		}
		return content.get(name);
	}

	/**
	 * 子の要素が存在するかを検査します.
	 *
	 * @param name 要素の名前.<br>
	 * @return 存在する場合はtrueを返します.<br>
	 */
	public boolean hasElement(String name) {
		return content.containsKey(name);
	}

	/**
	 * この要素をストリームに出力します. このメソッドはデバッグ用です。<br>
	 *
	 * @param stream 出力するストリームを送信します。<br>
	 */
	public void printAll(PrintStream stream) {
		stream.println(name + " " + attributes);
		for (List<XMLElement> elements : content.values()) {
			for (XMLElement element : elements) {
				stream.println(element);
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "XMLElement{" + name + " : " + attributes + " : " + value + '}';
	}
}
