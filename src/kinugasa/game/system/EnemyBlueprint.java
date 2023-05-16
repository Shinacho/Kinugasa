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
		return id;
	}

	public String getVisibleName() {
		return visibleName;
	}

	public String getId() {
		return id;
	}

	private String id;
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
	private List<CmdAction> actions;
	private Vehicle vehicle;
	private EnemyAI ai;
	private Sound deadSound;

	public EnemyBlueprint(String id,
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
			List<CmdAction> actions,
			Vehicle vehicle, EnemyAI ai,Sound deadSound) {
		this.id = id;
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
		String enemyName = visibleName + getEnemyNo(id);
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

		return new Enemy(id, s, (ArrayList<DropItem>) dropItems.clone(), sprite.clone(), vehicle, ai, deadSound);
	}
}
