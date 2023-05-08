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

import java.util.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLのタグや属性を展開するユーティリティです.
 * <br>
 * これらのAPIはDOMをベースに設計されています。このクラスでは、DOMインスタンスを
 * フレームワークで使用するために変換する機能を提供します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/03/15_13:01:50.<br>
 * @author Shinacho ( <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public final class XMLParserUtil {

	/**
	 * ユーティリティうクラスです.
	 */
	private XMLParserUtil() {
	}

	/**
	 * DOMノードインターフェースXMLElementクラスに変換します.
	 * このメソッドは再帰的に処理されます。あるノードが持つ子ノードは
	 * 以下のように展開されます。<br>
	 * <br>
	 * ノードタイプがTEXT_NODEの場合(NodeNameが"#text"の場合)はそのノードが持つTextContentの値が
	 * 戻り値となるエレメントのvalueに設定されます。<br>
	 * <br>
	 * それ以外のノードタイプでは、そのノードが持つ子ノードと属性が
	 * 戻り値となるエレメントに設定されます。<br>
	 *
	 * @param node 変換するノードを指定します。通常はルートノード(DOMドキュメントのLastChild)です。<br>
	 *
	 * @return DOMノードがから構築されたXMLElemtnクラスを返します。<br>
	 */
	public static XMLElement createElement(Node node) {
		String nodeName = node.getNodeName();
		List<XMLAttribute> attributes = XMLParserUtil.getAttributes(node.getAttributes());
		NodeList nodeList = node.getChildNodes();

		Map<String, List<XMLElement>> childElements = new HashMap<String, List<XMLElement>>(nodeList.getLength());
		String textValue = node.getNodeValue();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node element = nodeList.item(i);
			if ("#text".equals(element.getNodeName())) {
				textValue = element.getNodeValue();
				continue;
			}
			if (!childElements.containsKey(element.getNodeName())) {
				childElements.put(element.getNodeName(), new ArrayList<XMLElement>());
			}
			childElements.get(element.getNodeName()).add(createElement(element));
		}
		return new XMLElement(nodeName,
				childElements,
				new XMLAttributeStorage(attributes),
				textValue);
	}

	/**
	 * ノードの属性となるNamedNodeMapをXMLAttributeのリストに変換します.
	 *
	 * @param nodeMap ノードが持つ属性を送信します。NodeクラスのgetAttributeです。<br>
	 *
	 * @return 指定されたNameNodeMapが持つ属性をXMLAttributeクラスのリストとして返します。<br>
	 */
	public static List<XMLAttribute> getAttributes(NamedNodeMap nodeMap) {
		if (nodeMap == null) {
			return Collections.<XMLAttribute>emptyList();
		}
		List<XMLAttribute> attributes = new ArrayList<XMLAttribute>(nodeMap.getLength());
		for (int i = 0; i < nodeMap.getLength(); i++) {
			attributes.add(new XMLAttribute(nodeMap.item(i).getNodeName(), nodeMap.item(i).getNodeValue()));
		}
		return attributes;
	}
}
