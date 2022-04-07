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
import java.util.Collections;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.StopWatch;

/**
 * �S�Ă̈ړ���i��ۊǂ���X�g���[�W�ł�.
 * <br>
 * �ړ���i��XML�́AkinugasaVehicle.dtd���g�p���܂��B<br>
 * �ړ���i��XML���烍�[�h����ۂ̃t�H�[�}�b�g��DTD���m�F���Ă��������B<br>
 *
 * <br>
 * ���̃N���X�́h���ݎg�p���̈ړ���i�h�̕ۊǏꏊ�����˂܂��B<br>
 * �������A���̕ۊǏꏊ���g�p���邩�ǂ����͊J���҂ɔC���܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_22:25:48<br>
 * @author Dra0211<br>
 */
public class VehicleStorage extends Storage<Vehicle>
		implements Serializable, XMLFileSupport {

	private static final long serialVersionUID = -8199049449459770547L;
	/**
	 * �B��̃C���X�^���X�ł�.
	 */
	private static final VehicleStorage INSTANCE = new VehicleStorage();

	/**
	 * �V���O���g���N���X�ł�. getInstance���g�p���Ă��������B<br>
	 */
	private VehicleStorage() {
	}

	/**
	 * �C���X�^���X���擾���܂�.
	 *
	 * @return VehicleStorage�̃C���X�^���X��Ԃ��܂��B<br>
	 */
	public static VehicleStorage getInstance() {
		return INSTANCE;
	}
	/**
	 * ���ݎg�p���̈ړ���i�ł�.
	 */
	private Vehicle currentVehicle;

	/**
	 * ���ݎg�p���Ă���ړ���i���擾���܂�. �ݒ肳��Ă��Ȃ�����́Anull��Ԃ��܂��B<br>
	 *
	 * @return ���ݎg�p���Ă���ړ���i�ł��B<br>
	 */
	public Vehicle getCurrentVehicle() {
		return currentVehicle;
	}

	/**
	 * ���ݎg�p���̈ړ���i��ύX���܂�.
	 *
	 * @param currentVehicle �V�����ړ���i�𑗐M���܂��B<br>
	 */
	public void setCurrentVehicle(Vehicle currentVehicle) {
		this.currentVehicle = currentVehicle;
	}

	/**
	 * {@inheritDoc }
	 *
	 * @throws NameNotFoundException �w�肳�ꂽ���O�����`�b�v������ ChipAttributeStorage�ɓo�^����Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	@Override
	public void readFromXML(String filePath)
			throws IllegalXMLFormatException, FileNotFoundException,
			ContentsIOException, NameNotFoundException {
		StopWatch watch = new StopWatch().start();
		XMLFile reader = new XMLFile(filePath).load();

		XMLElement root = reader.getFirst();
		if (!"kinugasaVehicle".equals(root.getName())) {
			throw new IllegalXMLFormatException("illegal root node name : " + root);
		}

		List<XMLElement> vehiclesElement = root.getElement("vehicle");

		for (int i = 0, size = vehiclesElement.size(); i < size; i++) {
			XMLElement vehicle = vehiclesElement.get(i);
			String name = vehicle.getAttributes().get("name").getValue();
			float speed = vehicle.getAttributes().get("speed").getFloatValue();
			List<XMLElement> chipAttribute = vehicle.getElement("stepOn");
			List<ChipAttribute> stepOnAttributes = new ArrayList<ChipAttribute>(chipAttribute.size());
			ChipAttributeStorage storage = ChipAttributeStorage.getInstance();
			for (int j = 0, attributeSize = chipAttribute.size(); j < attributeSize; j++) {
				stepOnAttributes.add(storage.get(chipAttribute.get(j).getAttributes().get("attribute").getValue()));
			}
			new Vehicle(name, speed, stepOnAttributes);//addThis
		}
		reader.dispose();
		watch.stop();
		GameLog.printInfo("�r�[�N��������ɓǂݍ��܂�܂���(" + watch.getTime() + " ms) : size=[" + size() + "]");
		List<Vehicle> list = new ArrayList<Vehicle>(getAll());
		Collections.sort(list);
		for (int i = 0, size = list.size(); i < size; i++) {
			GameLog.printInfo("- " + list.get(i).getName());
			List<ChipAttribute> st = list.get(i).getAttributeStorage().asList();
			for (int j = 0, vSize = st.size(); j < vSize; j++) {
				GameLog.printInfo("-- " + st.get(j));
			}
		}
	}
}
