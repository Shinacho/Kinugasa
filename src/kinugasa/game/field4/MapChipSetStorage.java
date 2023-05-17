/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
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
		GameLog.printIfUsing(Level.ALL, getAll().toString());

	}

}
