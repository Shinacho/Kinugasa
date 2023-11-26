/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.game.field4;

import java.util.logging.Level;
import kinugasa.game.GameLog;
import kinugasa.game.GameOption;
import kinugasa.game.system.GameSystem;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.IniFile;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_18:32:21<br>
 * @author Shinacho<br>
 */
public class FieldMapStorage extends Storage<FieldMap> implements XMLFileSupport {

	private static final FieldMapStorage INSTANCE = new FieldMapStorage();

	public static FieldMapStorage getInstance() {
		return INSTANCE;
	}

	private FieldMapStorage() {
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {

		XMLFile file = new XMLFile(filePath);
		if (!file.getFile().exists()) {
			throw new FileNotFoundException(file + " is not found");
		}

		XMLElement root = file.load().getFirst();

		for (XMLElement e : root.getElement("fieldMap")) {
			String name = e.getAttributes().get("name").getValue();
			XMLFile fieldMapDataFile = new XMLFile(e.getAttributes().get("data").getValue());
			if (!fieldMapDataFile.exists()) {
				throw new IllegalXMLFormatException("data file is not found " + fieldMapDataFile);
			}
			add(new FieldMap(name, fieldMapDataFile));
		}
		if (GameSystem.isDebugMode()) {
			GameLog.print(getAll());
		}
//		printAll(System.out);
	}

}
