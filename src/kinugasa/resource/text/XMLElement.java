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

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import kinugasa.game.GameLog;
import kinugasa.game.system.GameSystem;
import kinugasa.resource.Nameable;

/**
 * "�v�f"�N���X�́AXML��1�̃G�������g�ɑΉ����܂�.
 * <br>
 * �v�f�Ƃ͎��̂悤�ȍ\���ł��B<br>
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
	 * ���̗v�f�̖��O�ł�.
	 */
	private String name;
	/**
	 * ���̗v�f�̓����ɐݒu���ꂽ�v�f�̃}�b�v�ł�,
	 */
	private Map<String, List<XMLElement>> content;
	/**
	 * ���̗v�f���������ł�.
	 */
	private XMLAttributeStorage attributes;
	/**
	 * ���̗v�f�̓����ɒ��ڏ����ꂽ�e�L�X�g�ł�.
	 */
	private String value;

	/**
	 * �q�̗v�f�������Ȃ��v�f���쐬���܂�.
	 *
	 * @param name ���̗v�f�̖��O�ł��B<br>
	 * @param attributes ���̗v�f���������ł��B<br>
	 * @param value ���̗v�f�̃e�L�X�g�ł��B<br>
	 */
	public XMLElement(String name, XMLAttributeStorage attributes, String value) {
		this.name = name;
		this.attributes = attributes;
		this.value = value;
	}

	/**
	 * �q�v�f�����v�f���쐬���܂�.
	 *
	 * @param name ���̗v�f�̖��O�ł��B<br>
	 * @param content ���̃G�������g�����q�̃G�������g�𑗐M���܂��B<br>
	 * @param attributes ���̗v�f���������ł��B<br>
	 * @param value ���̗v�f�̃e�L�X�g�ł��B<br>
	 */
	public XMLElement(String name, Map<String, List<XMLElement>> content, XMLAttributeStorage attributes, String value) {
		this.name = name;
		this.content = content;
		this.attributes = attributes;
		this.value = value;
	}

	/**
	 * ���̗v�f�̃e�L�X�g���擾���܂�.
	 *
	 * @return ���̗v�f�����e�L�X�g��Ԃ��܂��B�e�L�X�g���Ȃ��ꍇ�́ANULL���Ԃ���܂��B<br>
	 */
	public String getValue() {
		return value;
	}

	/**
	 * ���̗v�f���e�L�X�g�������Ă��邩���������܂�.
	 *
	 * @return �e�L�X�g�����ꍇ��true��Ԃ��܂��B���̖߂�l��getValue()!=null���Ɠ����ł��B<br>
	 */
	public boolean hasValue() {
		return value != null;
	}

	/**
	 * ���̗v�f�ɐݒ肳�ꂽ�������擾���܂�.
	 *
	 * @return �������i�[�����X�g���[�W��Ԃ��܂��B<br>
	 */
	public XMLAttributeStorage getAttributes() {
		return attributes;
	}

	public boolean hasAttribute(String key) {
		return attributes.contains(key);
	}

	/**
	 * ���̗v�f�����q�̗v�f���擾���܂�.
	 *
	 * @return �q�̗v�f��Ԃ��܂��B�q�̗v�f���ݒ肳��Ă��Ȃ��ꍇ��null��Ԃ��܂��B<br>
	 */
	public Map<String, List<XMLElement>> getContent() {
		return content;
	}

	/**
	 * �w�肵�����O�����q�̗v�f��Ԃ��܂�.
	 *
	 * @param name �v�f�̖��O���w�肵�܂��B<br>
	 * @return �w�肵�����O�̗v�f�̃��X�g��Ԃ��܂��B���݂��Ȃ����O���w�肵���ꍇ�� null��Ԃ��܂��B<br>
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
	 * �q�̗v�f�����݂��邩���������܂�.
	 *
	 * @param name �v�f�̖��O.<br>
	 * @return ���݂���ꍇ��true��Ԃ��܂�.<br>
	 */
	public boolean hasElement(String name) {
		return content.containsKey(name);
	}

	/**
	 * ���̗v�f���X�g���[���ɏo�͂��܂�. ���̃��\�b�h�̓f�o�b�O�p�ł��B<br>
	 *
	 * @param stream �o�͂���X�g���[���𑗐M���܂��B<br>
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
