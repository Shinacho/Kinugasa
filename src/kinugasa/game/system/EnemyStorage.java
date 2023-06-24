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
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;
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
			String visibleName = e.getAttributes().get("name").getValue();
			BufferedImage image = ImageUtil.load(e.getAttributes().get("image").getValue());
			Race race = RaceStorage.getInstance().get(e.getAttributes().get("race").getValue());
			Vehicle vehicle = VehicleStorage.getInstance().get(e.getAttributes().get("vehicle").getValue());
			EnemyAI ai = EnemyAIStorage.getInstance().get(e.getAttributes().get("ai").getValue());
			Sound deadSound = null;
			if (e.getAttributes().contains("deadSound")) {
				String soundID = e.getAttributes().get("deadSound").getValue();
				Sound s = SoundStorage.getInstance().get(soundID);
				s.dispose();
				deadSound = s;
			}
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
			List<Action> actionList = new ArrayList<>();
			for (XMLElement ae : e.getElement("action")) {
				String name = ae.getAttributes().get("name").getValue();
				actionList.add(ActionStorage.getInstance().get(name));
			}
			List<ConditionKey> condition = new ArrayList<>();
			for (XMLElement ae : e.getElement("condition")) {
				String name = ae.getAttributes().get("name").getValue();
				condition.add(ConditionStorage.getInstance().get(name).getKey());
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
					ai,
					deadSound
			);
			add(b);
		}

		file.dispose();
	}

}
