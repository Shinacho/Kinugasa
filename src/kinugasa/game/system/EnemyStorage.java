/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.field4.Vehicle;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.ImageSprite;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 * 敵のマスタです。
 *
 * @vesion 1.0.0 - 2022/11/16_20:54:18<br>
 * @author Shinacho<br>
 */
public class EnemyStorage extends Storage<EnemyBlueprint> implements XMLFileSupport {

	private static final EnemyStorage INSTANCE = new EnemyStorage();

	private EnemyStorage() {
	}

	public static EnemyStorage getInstance() {
		return INSTANCE;
	}

	public EnemyBlueprint getByVisibleName(String name) {
		for (EnemyBlueprint e : this) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		throw new NameNotFoundException(name + " is not found");
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}

		XMLElement root = file.load().getFirst();

		for (XMLElement e : root.getElement("enemy")) {
			String id = e.getAttributes().get("name").getValue();
			String visibleName = e.getAttributes().get("visibleName").getValue();
			BufferedImage image = ImageUtil.load(e.getAttributes().get("image").getValue());
			Race race = RaceStorage.getInstance().get(e.getAttributes().get("race").getValue());
			Vehicle vehicle = VehicleStorage.getInstance().get(e.getAttributes().get("vehicle").getValue());
			EnemyAI ai = EnemyAIStorage.getInstance().get(e.getAttributes().get("ai").getValue());
			StatusValueSet statusValueSet = new StatusValueSet();
			statusValueSet.setAll(1);
			for (XMLElement se : e.getElement("status")) {
				String statusKey = se.getAttributes().get("name").getValue();
				float value = se.getAttributes().get("value").getFloatValue();
				float min = se.getAttributes().get("min").getFloatValue();
				float max = se.getAttributes().get("max").getFloatValue();
				statusValueSet.get(statusKey).setMin(min);
				statusValueSet.get(statusKey).setMax(max);
				statusValueSet.get(statusKey).setValue(value);
				statusValueSet.get(statusKey).setInitial(value);
			}
			AttributeValueSet attrValueSet = new AttributeValueSet();
			attrValueSet.setAll(1);
			for (XMLElement ae : e.getElement("attrIn")) {
				String attrKey = ae.getAttributes().get("name").getValue();
				float value = ae.getAttributes().get("value").getFloatValue();
				attrValueSet.get(attrKey).setValue(value);
				attrValueSet.get(attrKey).setInitial(value);
			}
			ArrayList<DropItem> dropItems = new ArrayList<>();
			for (XMLElement ie : e.getElement("dropItem")) {
				String itemKey = ie.getAttributes().get("name").getValue();
				int n = ie.getAttributes().get("n").getIntValue();
				float p = ie.getAttributes().get("p").getFloatValue();
				dropItems.add(new DropItem(ItemStorage.getInstance().get(itemKey), n, p));
			}
			List<CmdAction> actionList = new ArrayList<>();
			for (XMLElement ae : e.getElement("action")) {
				String name = ae.getAttributes().get("name").getValue();
				actionList.add(ActionStorage.getInstance().get(name));
			}
			List<ConditionKey> condition = new ArrayList<>();
			for (XMLElement ae : e.getElement("condition")) {
				String name = ae.getAttributes().get("name").getValue();
				condition.add(ConditionValueStorage.getInstance().get(name).getKey());
			}
			Map<ItemEqipmentSlot, Item> eqip = new HashMap<>();
			ItemBag itemBag = new ItemBag(race.getItemBagSize());
			for (XMLElement ie : e.getElement("eqip")) {
				String name = ie.getAttributes().get("name").getValue();
				Item i = ItemStorage.getInstance().get(name);
				eqip.put(i.getEqipmentSlot(), i);
				itemBag.add(i);
			}
			for (XMLElement ie : e.getElement("item")) {
				String name = ie.getAttributes().get("name").getValue();
				Item i = ItemStorage.getInstance().get(name);
				itemBag.add(i);
			}
			//
			BookBag books = new BookBag();
			for (XMLElement be : e.getElement("book")) {
				String name = be.getAttributes().get("name").getValue();
				Book b = BookStorage.getInstance().get(name);
				books.add(b);
			}

			EnemyBlueprint b = new EnemyBlueprint(
					id,
					visibleName,
					statusValueSet,
					attrValueSet,
					condition,
					race,
					itemBag,
					books,
					eqip,
					dropItems,
					new ImageSprite(0, 0, image.getWidth(), image.getHeight(), image),
					actionList,
					vehicle,
					ai
			);
			add(b);
		}

		file.dispose();
	}

}
