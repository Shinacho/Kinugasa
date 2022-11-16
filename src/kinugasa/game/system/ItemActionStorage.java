/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.system;

import kinugasa.resource.*;
import kinugasa.resource.text.*;
import kinugasa.resource.FileNotFoundException;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_12:06:31<br>
 * @author Dra211<br>
 */
public class ItemActionStorage extends Storage<ItemAction> implements XMLFileSupport {

	private static final ItemActionStorage INSTANCE = new ItemActionStorage();

	private ItemActionStorage() {
	}

	public static ItemActionStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}
		XMLElement root = file.load().getFirst();

		for (XMLElement e : root.getElement("itemAction")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			ItemActionTargetType iatt = ItemActionTargetType.valueOf(e.getAttributes().get("iatt").getValue());
			ItemActionTargetStatusType iatst = ItemActionTargetStatusType.valueOf(e.getAttributes().get("iatst").getValue());
			float p = e.getAttributes().get("p").getFloatValue();
			if (iatst == ItemActionTargetStatusType.DROP_THIS_ITEM) {
				getInstance().add(new ItemAction(name, desc, iatt, iatst, p));
				continue;
			}
			String targetName = e.getAttributes().get("tgtName").getValue();
			ItemValueCalcType ivct = ItemValueCalcType.valueOf(e.getAttributes().get("ivct").getValue());
			float value = e.getAttributes().get("value").getFloatValue();
			float spread = e.getAttributes().get("spread").getFloatValue();
			getInstance().add(new ItemAction(name, desc, iatt, iatst, targetName, ivct, value, spread, p));
		}

		file.dispose();
		printAll(System.out);
	}

}
