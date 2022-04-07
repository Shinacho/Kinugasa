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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.object.FourDirection;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.Storage;
import kinugasa.resource.sound.LoopPoint;
import kinugasa.resource.sound.SoundBuilder;
import kinugasa.resource.sound.SoundMap;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLAttributeStorage;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.StopWatch;
import kinugasa.util.StringUtil;

/**
 * �S�Ẵt�B�[���h�}�b�v�r���_���i�[����X�g���[�W�ł�.
 * <br>
 * �t�B�[���h�}�b�v�r���_��XML�́AkinugasaFieldMapBuilder.dtd���g�p���܂��B<br>
 * �t�B�[���h�}�b�v�r���_��XML���烍�[�h����ۂ̃t�H�[�}�b�g��DTD���m�F���Ă��������B<br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/04/29_15:52:24<br>
 * @author Dra0211<br>
 */
public final class FieldMapBuilderStorage extends Storage<FieldMapBuilder>
		implements XMLFileSupport {

	/**
	 * �B��̃C���X�^���X�ł�.
	 */
	private static final FieldMapBuilderStorage INSTANCE = new FieldMapBuilderStorage();
	//�}�b�v�z�u�ʒu�iMapData.xml->root.attr
	private int x;
	private int y;
	private int width;
	private int height;

	/**
	 * �V���O���g���N���X�ł�. getInstance���g�p���Ă��������B<br>
	 */
	private FieldMapBuilderStorage() {
	}

	/**
	 * �C���X�^���X���擾���܂�.
	 *
	 * @return FieldMapBuilderStorage�̃C���X�^���X��Ԃ��܂��B<br>
	 */
	public static FieldMapBuilderStorage getInstance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc }
	 *
	 * @param filePath
	 * @throws IllegalXMLFormatException
	 * @throws ContentsFileNotFoundException
	 * @throws ContentsIOException
	 * @throws DuplicateNameException
	 * @throws NumberFormatException
	 */
	@Override
	public void readFromXML(String filePath)
			throws IllegalXMLFormatException, FileNotFoundException,
			ContentsIOException, DuplicateNameException, NumberFormatException {
		StopWatch watch = new StopWatch().start();
		XMLFile reader = new XMLFile(filePath).load();
		XMLElement root = reader.getFirst();
		if (!"kinugasaFieldMapBuilder".equals(root.getName())) {
			throw new IllegalXMLFormatException("illegal root node name :" + root);
		}

		x = root.getAttributes().get("x").getIntValue();
		y = root.getAttributes().get("y").getIntValue();
		width = root.getAttributes().get("width").getIntValue();
		height = root.getAttributes().get("height").getIntValue();
		List<XMLElement> fieldMapList = root.getElement("fieldMap");
		for (int i = 0, size = fieldMapList.size(); i < size; i++) {
			XMLElement fieldMapElement = fieldMapList.get(i);
			String fieldMapName = fieldMapElement.getAttributes().get("name").getValue();
			XMLFile dataFileReader = new XMLFile(fieldMapElement.getAttributes().get("data").getValue());
			SoundMap bgmMap = createSoundMap(fieldMapName, fieldMapElement.getElement("bgm"));
			NodeMap nodeMap = createNodeMap(fieldMapElement.getElement("node"));
			String textDataPath = null;
			if (fieldMapElement.hasElement("textData")) {
				textDataPath = fieldMapElement.getElement("textData").get(0).getAttributes().get("data").getValue();
			}
			new XMLFieldMapBuilder(fieldMapName, bgmMap, nodeMap, dataFileReader, textDataPath, x, y);
		}
		reader.dispose();
		watch.stop();
		GameLog.printInfo("�t�B�[���h�}�b�v�r���_�[������ɓǂݍ��܂�܂���(" + watch.getTime() + " ms) : size=[" + size() + "]");
		List<FieldMapBuilder> list = new ArrayList<FieldMapBuilder>(getAll());
		Collections.sort(list);
		for (int i = 0, size = list.size(); i < size; i++) {
			GameLog.printInfo("- " + list.get(i).toString());
		}
	}

	/**
	 * SoundMap���쐬���܂�.
	 *
	 * @param fieldMapName �T�E���h�}�b�v�̖��O�ƂȂ�t�B�[���h�}�b�v�̖��O�𑗐M���܂��B<br>
	 * @param element BGM�G�������g�ł��B<br>
	 * @return BGM�G�������g����T�E���h�}�b�v���\�z���ĕԂ��܂��Belement��null�̏ꍇ�� ��̃T�E���h�}�b�v��Ԃ��܂��B<br>
	 */
	private SoundMap createSoundMap(String fieldMapName, List<XMLElement> element) {
		SoundMap soundMap = new SoundMap(fieldMapName);
		if (element == null) {
			return soundMap;
		}
		for (int i = 0, size = element.size(); i < size; i++) {
			XMLAttributeStorage attr = element.get(i).getAttributes();

			String fileName = attr.get("file").getValue();
			SoundBuilder builder = new SoundBuilder(fileName);
			builder.setName(attr.contains("name")
					? attr.get("name").getValue()
					: StringUtil.fileName(fileName));
			if (attr.contains("loopFrom") && attr.contains("loopTo")) {
				int from = LoopPoint.valueOf(attr.get("loopFrom").getValue());
				int to = LoopPoint.valueOf(attr.get("loopTo").getValue());
				builder.setLoopPoint(from, to);
			}
			if (attr.contains("volume")) {
				builder.setMasterGain(attr.get("volume").getFloatValue());
			}
			soundMap.add(builder.builde());
		}
		return soundMap;
	}

	/**
	 * NodeMap���쐬���܂�.
	 *
	 * @param element NODE�G�������g�ł��B<br>
	 * @return NODE�G�������g����m�[�h�}�b�v���\�z���ĕԂ��܂��Belement��null�̏ꍇ�� ��̃m�[�h�}�b�v��Ԃ��܂��B<br>
	 */
	private NodeMap createNodeMap(List<XMLElement> element) {
		NodeMap nodeMap = new NodeMap();
		if (element == null) {
			return nodeMap;
		}
		for (int i = 0, size = element.size(); i < size; i++) {
			XMLAttributeStorage attr = element.get(i).getAttributes();

			String nodeName = attr.get("name").getValue();
			int x = Integer.parseInt(attr.get("x").getValue());
			int y = Integer.parseInt(attr.get("y").getValue());
			String mapName = attr.get("map").getValue();
			String exitNode = attr.get("exitNode").getValue();
			FourDirection face = FourDirection.valueOf(attr.get("face").getValue());
			String tooltip = attr.getIfContains("tooltip").getValue();
			NodeAccepter accepter = attr.contains("accepter")
					? NodeAccepterStorage.getInstance().get(attr.get("accepter").getValue())
					: null;
			Node node = new Node(nodeName, new Point(x, y), mapName, exitNode, tooltip, face, accepter);
			nodeMap.put(node.getLocation(), node);
		}
		return nodeMap;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "FieldMapBuilderStorage{" + "x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + '}';
	}
}
