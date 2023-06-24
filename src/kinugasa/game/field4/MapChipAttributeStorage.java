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
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 * マップチップの属性の唯一の保管庫です。このクラスはシングルトンです。
 *
 * @vesion 1.0.0 - 2022/11/08_16:12:11<br>
 * @author Shinacho<br>
 */
public class MapChipAttributeStorage extends Storage<MapChipAttribute> implements XMLFileSupport {

	private static final MapChipAttributeStorage INSTANCE = new MapChipAttributeStorage();

	public static MapChipAttributeStorage getInstance() {
		return INSTANCE;
	}
	public static final MapChipAttribute VOID = new MapChipAttribute("VOID");
	public static final MapChipAttribute CLOSE = new MapChipAttribute("CLOSE");

	static {
		getInstance().add(VOID);
		getInstance().add(CLOSE);
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.getFile().exists()) {
			throw new FileNotFoundException(file + " is not found");
		}

		XMLElement root = file.load().getFirst();
		for (XMLElement e : root.getElement("attribute")) {
			String name = e.getAttributes().get("name").getValue();
			int encountBaseValue = e.getAttributes().contains("encountBaseValue") ? e.getAttributes().get("encountBaseValue").getIntValue() : 0;
			getInstance().add(new MapChipAttribute(name, encountBaseValue));
		}
		GameLog.print(getAll());

	}

}
