/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
package kinugasa.game.field4;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import kinugasa.game.GameLog;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_7:10:13<br>
 * @author Dra211<br>
 */
public class VehicleStorage extends Storage<Vehicle> implements XMLFileSupport {

	private static final VehicleStorage INSTANCE = new VehicleStorage();

	public static VehicleStorage getInstance() {
		return INSTANCE;
	}

	private VehicleStorage() {
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.getFile().exists()) {
			throw new FileNotFoundException(file + " is not found");
		}
		XMLElement root = file.load().getFirst();

		for (XMLElement e : root.getElement("vehicle")) {
			String name = e.getAttributes().get("name").getValue();
			float speed = e.getAttributes().get("speed").getFloatValue();

			List<MapChipAttribute> list = new ArrayList<>();
			for (XMLElement ee : e.getElement("stepOn")) {
				list.add(MapChipAttributeStorage.getInstance().get(ee.getAttributes().get("attribute").getValue()));
			}
			add(new Vehicle(name, speed, list));
		}
		GameLog.printIfUsing(Level.ALL, getAll().toString());
		printAll(System.out);
	}

	private Vehicle currentVehicle;

	public Vehicle getCurrentVehicle() {
		return currentVehicle;
	}

	public void setCurrentVehicle(String name) {
		this.currentVehicle = get(name);
	}

}