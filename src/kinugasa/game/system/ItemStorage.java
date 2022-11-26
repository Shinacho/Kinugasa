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

import java.util.ArrayList;
import java.util.List;
import kinugasa.resource.text.*;
import kinugasa.resource.*;
import kinugasa.util.*;

/**
 * アイテムの一覧を定義するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:58:41<br>
 * @author Dra211<br>
 */
public class ItemStorage extends Storage<Item> implements XMLFileSupport {

	private static final ItemStorage INSTANCE = new ItemStorage();

	private ItemStorage() {
	}

	public static ItemStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}
		XMLElement root = file.load().getFirst();

		for (XMLElement e : root.getElement("item")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();

			List<ItemAction> fieldModeActions = new ArrayList<>();
			if (e.getAttributes().contains("fieldAction")) {
				String actionName = e.getAttributes().get("fieldAction").getValue();
				if (actionName.contains(",")) {
					for (String v : actionName.split(",")) {
						fieldModeActions.add(ItemActionStorage.getInstance().get(v));
					}
				} else {
					fieldModeActions.add(ItemActionStorage.getInstance().get(actionName));
				}
			}
			List<ItemAction> battleModeActions = new ArrayList<>();
			if (e.getAttributes().contains("battleAction")) {
				String actionName = e.getAttributes().get("battleAction").getValue();
				if (actionName.contains(",")) {
					for (String v : actionName.split(",")) {
						battleModeActions.add(ItemActionStorage.getInstance().get(v));
					}
				} else {
					battleModeActions.add(ItemActionStorage.getInstance().get(actionName));
				}
			}
			if (e.getAttributes().contains("slot")) {
				//装備スロットありのアイテム
				ItemEqipmentSlot slot = ItemEqipmentSlotStorage.getInstance().get(e.getAttributes().get("slot").getValue());
				AttributeValueSet attr = new AttributeValueSet();
				attr.setAll(0);
				for (XMLElement ee : e.getElement("eqAttr")) {
					String tgtName = ee.getAttributes().get("tgt").getValue();
					float value = ee.getAttributes().get("value").getFloatValue();
					attr.put(new AttributeValue(AttributeKeyStorage.getInstance().get(tgtName), value, value, value, value));
				}
				StatusValueSet status = new StatusValueSet();
				status.setAll(0);
				for (XMLElement ee : e.getElement("eqStatus")) {
					String tgtName = ee.getAttributes().get("tgt").getValue();
					float value = ee.getAttributes().get("value").getFloatValue();
					status.put(new StatusValue(StatusKeyStorage.getInstance().get(tgtName), value, value, 0, value));
				}
				int area = 0;
				if (e.getAttributes().contains("area")) {
					area = e.getAttributes().get("area").getIntValue();
				}
				WeaponMagicType wmt = WeaponMagicTypeStorage.getInstance().get(e.getAttributes().get("wmt").getValue());
				getInstance().add(new Item(name, desc, fieldModeActions, battleModeActions, attr, status, slot, wmt, area));
			} else {
				//装備スロットなしのアイテム
				getInstance().add(new Item(name, desc, fieldModeActions, battleModeActions, null, null, null, null, 0));
			}
		}

		file.dispose();
		printAll(System.out);

	}

}
