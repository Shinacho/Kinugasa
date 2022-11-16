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

import java.util.List;
import kinugasa.game.I18N;
import kinugasa.game.field4.*;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.resource.sound.SoundLoader;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_15:45:53<br>
 * @author Dra211<br>
 */
public class GameSystem {

	private static boolean debugMode = false;

	public static void setDebugMode(boolean debugMode) {
		GameSystem.debugMode = debugMode;
		FieldMap.setDebugMode(debugMode);
	}

	public static boolean isDebugMode() {
		return debugMode;
	}
	private static final GameSystem INSTANCE = new GameSystem();

	private GameSystem() {
	}

	public static GameSystem getInstance() {
		return INSTANCE;
	}

	private List<Status> party;
	private List<Status> enemy;
	private BattleField bf = new BattleField();

	public BattleField getBf() {
		return bf;
	}

	public List<Status> getEnemy() {
		return enemy;
	}

	public List<Status> getParty() {
		return party;
	}

	public void setParty(List<Status> party) {
		this.party = party;
	}

	public void setEnemy(List<Status> enemy) {
		this.enemy = enemy;
	}

	public void setBf(BattleField bf) {
		this.bf = bf;
	}

	public static void main(String[] args) {
		I18N.init("ja");

		//--------------------------------------
		new GameSystemXMLLoader()
				.addWeaponMagicTypeStorage("resource/field/data/item/weaponMagicType.xml")
				.addStatusKeyStorage("resource/field/data/battle/status.xml")
				.addAttrKeyStorage("resource/field/data/battle/attribute.xml")
				.addConditionValueStorage("resource/field/data/battle/condition.xml")
				.addItemActionStorage("resource/field/data/item/itemAction.xml")
				.addItemEqipmentSlotStorage("resource/field/data/item/itemSlotList.xml")
				.addItemStorage("resource/field/data/item/itemList.xml")
				.addRaceStorage("resource/field/data/race/raceList.xml")
				.addBattleActionStorage("resource/field/data/battle/battleAction.xml")
				.load();

		new GameSystemXMLLoader().testStatus("êlä‘");

		//--------------------------------------
		new FieldMapXMLLoader()
				.addSound("resource/bgm/BGM.csv")
				.addSound("resource/se/SE.csv")
				.addTextStorage("resource/field/data/text/000.xml")
				.addMapChipAttr("resource/field/data/attr/ChipAttributes.xml")
				.addVehicle("resource/field/data/vehicle/01.xml")
				.setInitialVehicleName("WALK")
				.addMapChipSet("resource/field/data/chipSet/01.xml")
				.addMapChipSet("resource/field/data/chipSet/02.xml")
				.addFieldMapStorage("resource/field/data/mapBuilder/builder.xml")
				.setInitialFieldMapName("ÉYÉV")
				.setInitialLocation(new D2Idx(9, 9))
				.load();
	}

}
