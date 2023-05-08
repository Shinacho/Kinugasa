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
 * 1��XML�v�f���������̃}�b�v�ł�.
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
	 * �V����XMLAttributeStorage���쐬���܂�.
	 */
	public XMLAttributeStorage() {
	}

	/**
	 * �e�ʂ̏����l���w�肵�āA�V����XMLAttributeStorage���쐬���܂�.
	 *
	 * @param initialSize Strage�̏����e�ʂ��w�肵�܂��B<br>
	 */
	public XMLAttributeStorage(int initialSize) {
		super(initialSize);
	}

	/**
	 * XMLAttribute�̃R���N�V������S�Ēǉ�����Strage���쐬���܂�.
	 *
	 * @param attributes
	 */
	public XMLAttributeStorage(Collection<XMLAttribute> attributes) {
		this(attributes.size());
		addAll(attributes);
	}

	/**
	 * �S�Ă̑������X�y�[�X�ŋ�؂��ĕ��ׂ��������Ԃ��܂�.
	 *
	 * @return ������key=value�̌`���ŁA�X�y�[�X�ŋ�؂��ĕ��ׂ��������Ԃ��܂��B<br>
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
