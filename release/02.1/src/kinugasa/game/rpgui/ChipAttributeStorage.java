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
package kinugasa.game.rpgui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.StopWatch;

/**
 * �`�b�v�������i�[����X�g���[�W�ł�.
 * <br>
 * �`�b�v������XML�́AkinugasaChipAttribute.dtd���g�p���܂��B<br>
 * �`�b�v������XML���烍�[�h����ۂ̃t�H�[�}�b�g��DTD���m�F���Ă��������B<br>
 * <br>
 * ���̃X�g���[�W�ɂ́A2�̃`�b�v�����������Œǉ�����܂��B<br>
 * <li>"VOID"</li>
 * <br>
 * ���̑����́A������ړ���i���ړ��\�ł��B
 * <br>
 * <li>"CLOSE"</li>
 * <br>
 * ���̑����́A������ړ���i�ňړ��ł��܂���B
 * <br>
 * <br>
 * �����̑����͍폜�ł��܂���B(�폜��Ɏ����ǉ�����܂��j<br>
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_21:59:44<br>
 * @author Dra0211<br>
 */
public final class ChipAttributeStorage extends Storage<ChipAttribute>
		implements Serializable, XMLFileSupport {

	private static final long serialVersionUID = -3407377075571254018L;
	/**
	 * �B��̃C���X�^���X�ł�.
	 */
	private static final ChipAttributeStorage INSTANCE = new ChipAttributeStorage();

	/**
	 * �V���O���g���N���X�ł�. getInstance���g�p���Ă��������B<br>
	 */
	private ChipAttributeStorage() {
		putDefaultAttribute();
	}

	/**
	 * {@inheritDoc }
	 *
	 * @throws DuplicateNameException �`�b�v�����̖��O���d������ꍇ�ɓ������܂��B<br>
	 */
	@Override
	public void readFromXML(String filePath)
			throws IllegalXMLFormatException, FileNotFoundException,
			ContentsIOException, DuplicateNameException {
		StopWatch watch = new StopWatch().start();
		XMLFile reader = new XMLFile(filePath).load();
		XMLElement root = reader.getFirst();
		if (!"kinugasaChipAttribute".equals(root.getName())) {
			throw new IllegalXMLFormatException("illegal root node name : " + root);
		}
		List<XMLElement> attributeElements = root.getElement("attribute");
		for (int i = 0, size = attributeElements.size(); i < size; i++) {
			add(new ChipAttribute(attributeElements.get(i).getAttributes().get("name").getValue()));
		}
		reader.dispose();
		watch.stop();
		GameLog.printInfo("�`�b�v����������ɓǂݍ��܂�܂���(" + watch.getTime() + " ms) : size=[" + size() + "]");
		List<ChipAttribute> list = new ArrayList<ChipAttribute>(getAll());
		Collections.sort(list);
		for (int i = 0, size = list.size(); i < size; i++) {
			GameLog.printInfo("- " + list.get(i).toString());
		}
	}

	/**
	 * �폜����уN���A�����ۂɁA�f�t�H���g�Œǉ�����Ă��� �`�b�v������ݒ肵�Ȃ����܂�.
	 */
	private void putDefaultAttribute() {
		put(new ChipAttribute("VOID"));
		put(new ChipAttribute("CLOSE"));
	}

	/**
	 * �C�e���[�^��Ԃ��܂�. ���̎����ł́A�C�e���[�^����̍폜�����s����ƁA ����ɓ��삵�Ȃ��Ȃ�\��������܂��B<br>
	 *
	 * @return �C�e���[�^��Ԃ��܂��B<br>
	 */
	@Override
	public Iterator<ChipAttribute> iterator() {
		return super.iterator();
	}

	/**
	 * �C���X�^���X���擾���܂�.
	 *
	 * @return ChipAttributeStorage�̃C���X�^���X��Ԃ��܂��B<br>
	 */
	public static ChipAttributeStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public void remove(ChipAttribute val) {
		super.remove(val);
		putDefaultAttribute();
	}

	@Override
	public void remove(String key) {
		super.remove(key);
		putDefaultAttribute();
	}

	@Override
	public void removeAll(ChipAttribute... values) {
		super.removeAll(values);
		putDefaultAttribute();
	}

	@Override
	public void removeAll(Collection<? extends ChipAttribute> values) {
		super.removeAll(values);
		putDefaultAttribute();
	}

	@Override
	public void removeAll(String... keys) {
		super.removeAll(keys);
		putDefaultAttribute();
	}

	@Override
	public void clear() {
		super.clear();
		putDefaultAttribute();
	}
}
