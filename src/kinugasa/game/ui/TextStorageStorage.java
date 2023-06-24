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
package kinugasa.game.ui;

import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 * テキストストレージのパスを保管する唯一の場所です。
 *
 * @vesion 1.0.0 - 2022/11/08_21:29:42<br>
 * @author Shinacho<br>
 */
public class TextStorageStorage extends Storage<TextStorage> implements XMLFileSupport {

	private static final TextStorageStorage INSTANCE = new TextStorageStorage();

	public static TextStorageStorage getInstance() {
		return INSTANCE;
	}

	private TextStorageStorage() {
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile data = new XMLFile(filePath);
		if (!data.exists()) {
			throw new FileNotFoundException(filePath + " is not found");
		}
		XMLElement root = data.load().getFirst();

		for (XMLElement e : root.getElement("textFile")) {
			super.add(new TextStorage(e.getAttributes().get("name").getValue(),
					new XMLFile(e.getAttributes().get("data").getValue())));
		}
		data.dispose();


	}
	
	public void dispose(){
		forEach(p->p.dispose());
	}

}
