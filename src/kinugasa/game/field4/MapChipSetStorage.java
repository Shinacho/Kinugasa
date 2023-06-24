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

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.logging.Level;
import kinugasa.game.GameLog;
import kinugasa.graphics.ImageUtil;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.KImage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_7:13:52<br>
 * @author Shinacho<br>
 */
public class MapChipSetStorage extends Storage<MapChipSet> implements XMLFileSupport {

	private static final MapChipSetStorage INSTANCE = new MapChipSetStorage();

	private MapChipSetStorage() {
	}

	public static MapChipSetStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.getFile().exists()) {
			throw new FileNotFoundException(file + " is not found");
		}

		XMLElement root = file.load().getFirst();

		for (XMLElement e : root.getElement("chipSet")) {
			String name = e.getAttributes().get("name").getValue();
			String image = e.getAttributes().get("image").getValue();
			int w = e.getAttributes().get("cutWidth").getIntValue();
			int h = e.getAttributes().get("cutHeight").getIntValue();

			Map<String, BufferedImage> map = ImageUtil.splitAsMapN(ImageUtil.load(image), w, h, 3);

			MapChipSet chipSet = new MapChipSet(name);
			for (XMLElement c : e.getElement("mapChip")) {
				String chipName = c.getAttributes().get("name").getValue();
				String attr = c.getAttributes().get("attribute").getValue();
				BufferedImage image2 = map.get(chipName);
				if (image2 == null) {
					image2 = ImageUtil.newImage(w, h);
				}
				chipSet.add(new MapChip(chipName, MapChipAttributeStorage.getInstance().get(attr), new KImage(image2)));

			}
			add(chipSet);
		}
		GameLog.print(getAll());

	}

}
