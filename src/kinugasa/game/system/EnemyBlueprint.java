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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.field4.Vehicle;
import kinugasa.object.ImageSprite;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_21:02:30<br>
 * @author Shinacho<br>
 */
public class EnemyBlueprint implements Nameable {

	private static Map<String, Character> enemyNo = new HashMap<>();

	public static String getEnemyNo(String name) {
		if (enemyNo.containsKey(name)) {
			char c = enemyNo.get(name);
			c++;
			if (c > 'Z') {
				c = 'A';
			}
			enemyNo.put(name, c);
			return c + "";
		} else {
			enemyNo.put(name, (char) ('A' - 1));
			return getEnemyNo(name);
		}
	}

	public static void initEnemyNoMap() {
		enemyNo.clear();
	}

	@Override
	public String getName() {
		return visibleName;
	}

	public String getVisibleName() {
		return visibleName;
	}


	private String visibleName;
	private StatusValueSet statusValueSet;
	private AttributeValueSet attrValueSet;
	private List<ConditionKey> conditionList;
	private Race race;
	private ItemBag itemBag;
	private BookBag bookBag;
	private Map<ItemEqipmentSlot, Item> equipment;
	private ArrayList<DropItem> dropItems;
	private ImageSprite sprite;
	private List<Action> actions;
	private Vehicle vehicle;
	private EnemyAI ai;
	private Sound deadSound;

	public EnemyBlueprint(
			String visibleName,
			StatusValueSet statusValueSet,
			AttributeValueSet attrValueSet,
			List<ConditionKey> conditionList,
			Race race,
			ItemBag itemBag,
			BookBag bookBag,
			Map<ItemEqipmentSlot, Item> equipment,
			ArrayList<DropItem> dropItems,
			ImageSprite sprite,
			List<Action> actions,
			Vehicle vehicle, EnemyAI ai, Sound deadSound) {
		this.visibleName = visibleName;
		this.statusValueSet = statusValueSet;
		this.attrValueSet = attrValueSet;
		this.conditionList = conditionList;
		this.race = race;
		this.itemBag = itemBag;
		this.bookBag = bookBag;
		this.equipment = equipment;
		this.dropItems = dropItems;
		this.sprite = sprite;
		this.actions = actions;
		this.vehicle = vehicle;
		this.ai = ai;
		this.deadSound = deadSound;
	}

	public Enemy create() {
		String enemyName = visibleName + getEnemyNo(visibleName);
		Status s = new Status(enemyName, race);
		s.setBaseAttrIn(attrValueSet.clone());
		s.setBaseStatus(statusValueSet.clone());
		for (ConditionKey k : conditionList) {
			s.addCondition(k);
		}
		s.setItemBag(itemBag.clone());
		for (Item i : equipment.values()) {
			s.addEqip(i);
		}
		s.setBookBag(bookBag);
		if (actions.isEmpty()) {
			throw new GameSystemException(enemyName + " s action is empty");
		}
		actions.forEach(v -> s.getActions().add(v));

		return new Enemy(visibleName, s, (ArrayList<DropItem>) dropItems.clone(), sprite.clone(), vehicle, ai, deadSound);
	}
}
