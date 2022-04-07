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
 * 全ての移動手段を保管するストレージです.
 * <br>
 * 移動手段のXMLは、kinugasaVehicle.dtdを使用します。<br>
 * 移動手段をXMLからロードする際のフォーマットはDTDを確認してください。<br>
 *
 * <br>
 * このクラスは”現在使用中の移動手段”の保管場所をかねます。<br>
 * ただし、この保管場所を使用するかどうかは開発者に任せます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_22:25:48<br>
 * @author Dra0211<br>
 */
public class VehicleStorage extends Storage<Vehicle>
		implements Serializable, XMLFileSupport {

	private static final long serialVersionUID = -8199049449459770547L;
	/**
	 * 唯一のインスタンスです.
	 */
	private static final VehicleStorage INSTANCE = new VehicleStorage();

	/**
	 * シングルトンクラスです. getInstanceを使用してください。<br>
	 */
	private VehicleStorage() {
	}

	/**
	 * インスタンスを取得します.
	 *
	 * @return VehicleStorageのインスタンスを返します。<br>
	 */
	public static VehicleStorage getInstance() {
		return INSTANCE;
	}
	/**
	 * 現在使用中の移動手段です.
	 */
	private Vehicle currentVehicle;

	/**
	 * 現在使用している移動手段を取得します. 設定されていない限りは、nullを返します。<br>
	 *
	 * @return 現在使用している移動手段です。<br>
	 */
	public Vehicle getCurrentVehicle() {
		return currentVehicle;
	}

	/**
	 * 現在使用中の移動手段を変更します.
	 *
	 * @param currentVehicle 新しい移動手段を送信します。<br>
	 */
	public void setCurrentVehicle(Vehicle currentVehicle) {
		this.currentVehicle = currentVehicle;
	}

	/**
	 * {@inheritDoc }
	 *
	 * @throws NameNotFoundException 指定された名前を持つチップ属性が ChipAttributeStorageに登録されていない場合に投げられます。<br>
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
		GameLog.printInfo("ビークルが正常に読み込まれました(" + watch.getTime() + " ms) : size=[" + size() + "]");
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
