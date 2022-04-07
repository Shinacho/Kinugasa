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

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import kinugasa.game.GameLog;
import kinugasa.graphics.ImageUtil;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Storage;
import kinugasa.resource.TImage;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.StopWatch;

/**
 * �S�Ẵ`�b�v�Z�b�g��o�^����X�g���[�W�ł�.
 * <br>
 * �`�b�v�Z�b�g��XML�́AkinugasaChipSet.dtd���g�p���܂��B<br>
 * �`�b�v�Z�b�g��XML���烍�[�h����ۂ̃t�H�[�}�b�g��DTD���m�F���Ă��������B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_23:30:28<br>
 * @author Dra0211<br>
 */
public class ChipSetStorage extends Storage<ChipSet>
		implements XMLFileSupport {

	/**
	 * �B��̃C���X�^���X�ł�.
	 */
	private static final ChipSetStorage INSTANCE = new ChipSetStorage();

	/**
	 * �V���O���g���N���X�ł�. getInstance���g�p���Ă��������B<br>
	 */
	private ChipSetStorage() {
	}

	/**
	 * �C���X�^���X���擾���܂�.
	 *
	 * @return ChipSetStorage�̃C���X�^���X��Ԃ��܂��B<br>
	 */
	public static ChipSetStorage getInstance() {
		return INSTANCE;
	}

	/**
	 * �`�b�v�Z�b�g�̍\�z���s���܂�.
	 * <br> {@inheritDoc }
	 *
	 * @throws NumberFormatException �؂�o���T�C�Y�̐��l��int�^�ɕϊ��ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws NameNotFoundException �w�肳�ꂽ���O�����`�b�v������ ChipAttributeStorage�ɓo�^����Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	@Override
	public void readFromXML(String filePath)
			throws IllegalXMLFormatException, FileNotFoundException,
			ContentsIOException, NumberFormatException,
			NameNotFoundException {

		StopWatch watch = new StopWatch().start();
		XMLFile reader = new XMLFile(filePath).load();
		XMLElement root = reader.getFirst();

		if (!"kinugasaChipSet".equals(root.getName())) {
			throw new IllegalXMLFormatException("illegal root node name :" + root);
		}

		List<XMLElement> chipSetElementList = root.getElement("chipSet");
		for (int i = 0, chipSetSize = chipSetElementList.size(); i < chipSetSize; i++) {
			XMLElement chipSetElement = chipSetElementList.get(i);
			int cutWidth = chipSetElement.getAttributes().get("cutWidth").getIntValue();
			int cutHeight = chipSetElement.getAttributes().get("cutHeight").getIntValue();
			ChipSet chipSet = new ChipSet(chipSetElement.getAttributes().get("name").getValue(),
					cutWidth, cutHeight);
			Map<String, BufferedImage> imageMap = ImageUtil.splitAsMapN(
					ImageUtil.load(chipSetElement.getAttributes().get("image").getValue()),
					cutWidth, cutHeight, 3);
			List<XMLElement> mapChipElementList = chipSetElement.getElement("mapChip");
			for (int j = 0, mapChipSize = mapChipElementList.size(); j < mapChipSize; j++) {
				XMLElement mapChipElement = mapChipElementList.get(j);
				String mapChipName = mapChipElement.getAttributes().get("name").getValue();
				ChipAttribute attribute = ChipAttributeStorage.getInstance().get(mapChipElement.getAttributes().get("attribute").getValue());
				chipSet.add(new MapChip(
						mapChipName,
						imageMap.get(mapChipName) == null ? new TImage(ImageUtil.newImage(1, 1)) : new TImage(imageMap.get(mapChipName)),
						attribute));
			}
		}
		reader.dispose();
		watch.stop();
		GameLog.printInfo("�`�b�v�Z�b�g������ɓǂݍ��܂�܂���(" + watch.getTime() + " ms) : size=[" + size() + "]");
//
//		List<ChipSet> chipSetList = new ArrayList<ChipSet>(getAll());
//		Collections.sort(chipSetList);
//
//		for (int i = 0, chipSetSize = chipSetList.size(); i < chipSetSize; i++) {
//			ChipSet chipSet = chipSetList.get(i);
//			GameLog.printInfo("- name=[" + chipSet.getName() + "] size=[" + chipSet.size() + "]");
//			List<MapChip> mapChipList = new ArrayList<MapChip>(chipSet.getAll());
//			Collections.sort(mapChipList);
//			for (int j = 0, mapChipSize = chipSet.size(); j < mapChipSize; j++) {
//				GameLog.printInfo("-- " + mapChipList.get(j));
//			}
//		}

	}
}
