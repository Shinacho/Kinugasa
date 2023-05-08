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
 * XML�̃^�O�⑮����W�J���郆�[�e�B���e�B�ł�.
 * <br>
 * ������API��DOM���x�[�X�ɐ݌v����Ă��܂��B���̃N���X�ł́ADOM�C���X�^���X��
 * �t���[�����[�N�Ŏg�p���邽�߂ɕϊ�����@�\��񋟂��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/03/15_13:01:50.<br>
 * @author Shinacho ( <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public final class XMLParserUtil {

	/**
	 * ���[�e�B���e�B���N���X�ł�.
	 */
	private XMLParserUtil() {
	}

	/**
	 * DOM�m�[�h�C���^�[�t�F�[�XXMLElement�N���X�ɕϊ����܂�.
	 * ���̃��\�b�h�͍ċA�I�ɏ�������܂��B����m�[�h�����q�m�[�h��
	 * �ȉ��̂悤�ɓW�J����܂��B<br>
	 * <br>
	 * �m�[�h�^�C�v��TEXT_NODE�̏ꍇ(NodeName��"#text"�̏ꍇ)�͂��̃m�[�h������TextContent�̒l��
	 * �߂�l�ƂȂ�G�������g��value�ɐݒ肳��܂��B<br>
	 * <br>
	 * ����ȊO�̃m�[�h�^�C�v�ł́A���̃m�[�h�����q�m�[�h�Ƒ�����
	 * �߂�l�ƂȂ�G�������g�ɐݒ肳��܂��B<br>
	 *
	 * @param node �ϊ�����m�[�h���w�肵�܂��B�ʏ�̓��[�g�m�[�h(DOM�h�L�������g��LastChild)�ł��B<br>
	 *
	 * @return DOM�m�[�h������\�z���ꂽXMLElemtn�N���X��Ԃ��܂��B<br>
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
	 * �m�[�h�̑����ƂȂ�NamedNodeMap��XMLAttribute�̃��X�g�ɕϊ����܂�.
	 *
	 * @param nodeMap �m�[�h���������𑗐M���܂��BNode�N���X��getAttribute�ł��B<br>
	 *
	 * @return �w�肳�ꂽNameNodeMap����������XMLAttribute�N���X�̃��X�g�Ƃ��ĕԂ��܂��B<br>
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
