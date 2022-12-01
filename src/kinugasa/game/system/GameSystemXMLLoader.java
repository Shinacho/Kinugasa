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
import java.util.stream.Collectors;
import kinugasa.resource.text.*;
import kinugasa.resource.FileNotFoundException;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_14:34:32<br>
 * @author Dra211<br>
 */
public class GameSystemXMLLoader {

	public GameSystemXMLLoader() {
	}
	private List<String> weaponMagicTypeStorage = new ArrayList<>();
	private List<String> statusKeyStorage = new ArrayList<>();
	private List<String> attrKeyStorage = new ArrayList<>();
	private List<String> conditionValueStorage = new ArrayList<>();
	private List<String> itemActionStorage = new ArrayList<>();
	private List<String> itemEqipmentSlotStorage = new ArrayList<>();
	private List<String> itemStorage = new ArrayList<>();
	private List<String> raceStorage = new ArrayList<>();
	private List<String> battleActionStorage = new ArrayList<>();
	private List<String> battleField = new ArrayList<>();
	private List<String> enemyList = new ArrayList<>();
	private List<String> ess = new ArrayList<>();
	private List<String> bookList = new ArrayList<>();
	private String enemyProgressBarKey;
	private boolean debugMode = false;

	public GameSystemXMLLoader addWeaponMagicTypeStorage(String fileName) {
		weaponMagicTypeStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addStatusKeyStorage(String fileName) {
		statusKeyStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addAttrKeyStorage(String fileName) {
		attrKeyStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addConditionValueStorage(String fileName) {
		conditionValueStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addFieldConditionValueStorage(String fileName) {
		conditionValueStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addItemActionStorage(String fileName) {
		itemActionStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addItemEqipmentSlotStorage(String fileName) {
		itemEqipmentSlotStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addItemStorage(String fileName) {
		itemStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addRaceStorage(String fileName) {
		raceStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addBattleActionStorage(String fileName) {
		battleActionStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader setDebugMode(boolean f) {
		debugMode = f;
		return this;
	}

	public GameSystemXMLLoader addBattleField(String fileName) {
		battleField.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addEnemyList(String fileName) {
		this.enemyList.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addEnemySet(String fileName) {
		this.ess.add(fileName);
		return this;
	}

	public GameSystemXMLLoader setEnemyProgressBarKey(String key) {
		this.enemyProgressBarKey = key;
		return this;
	}

	public GameSystemXMLLoader addBookList(String name) {
		bookList.add(name);
		return this;
	}

	public String getEnemyProgressBarKey() {
		return enemyProgressBarKey;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public List<String> getEnemySetList() {
		return ess;
	}

	public List<String> getWeaponMagicTypeStorage() {
		return weaponMagicTypeStorage;
	}

	public List<String> getStatusKeyStorage() {
		return statusKeyStorage;
	}

	public List<String> getAttrKeyStorage() {
		return attrKeyStorage;
	}

	public List<String> getConditionValueStorage() {
		return conditionValueStorage;
	}

	public List<String> getItemActionStorage() {
		return itemActionStorage;
	}

	public List<String> getItemEqipmentSlotStorage() {
		return itemEqipmentSlotStorage;
	}

	public List<String> getItemStorage() {
		return itemStorage;
	}

	public List<String> getRaceStorage() {
		return raceStorage;
	}

	public List<String> getBattleActionStorage() {
		return battleActionStorage;
	}

	public List<String> getBattleField() {
		return battleField;
	}

	public List<String> getEnemyList() {
		return enemyList;
	}

	public List<String> getBookList() {
		return bookList;
	}

	public void load() throws IllegalStateException {
		GameSystem.setDebugMode(debugMode);
		if (weaponMagicTypeStorage.isEmpty()) {
			throw new IllegalStateException("weaponMagicType is empty");
		}
		weaponMagicTypeStorage.forEach(v -> WeaponMagicTypeStorage.getInstance().readFromXML(v));

		if (statusKeyStorage.isEmpty()) {
			throw new IllegalStateException("statusMaster is empty");
		}
		statusKeyStorage.forEach(v -> readStatusMaster(v));

		if (attrKeyStorage.isEmpty()) {
			throw new IllegalStateException("attrMaster is empty");
		}
		attrKeyStorage.forEach(v -> readAttrMaster(v));

		if (conditionValueStorage.isEmpty()) {
			throw new IllegalStateException("conditionMaster is empty");
		}
		conditionValueStorage.forEach(v -> readConditionMaster(v));

		if (itemActionStorage.isEmpty()) {
			throw new IllegalStateException("itemActionStorage is empty");
		}
		itemActionStorage.forEach(v -> ItemActionStorage.getInstance().readFromXML(v));

		if (itemEqipmentSlotStorage.isEmpty()) {
			throw new IllegalStateException("itemEqipmentSlotStorage is empty");
		}
		itemEqipmentSlotStorage.forEach(v -> ItemEqipmentSlotStorage.getInstance().readFromXML(v));

		if (itemStorage.isEmpty()) {
			throw new IllegalStateException("itemStorage is empty");
		}
		itemStorage.forEach(v -> ItemStorage.getInstance().readFromXML(v));

		if (bookList.isEmpty()) {
			throw new IllegalStateException("bookList is empty");
		}
		bookList.forEach(v -> BookStorage.getInstance().readFromXML(v));
		
		if (raceStorage.isEmpty()) {
			throw new IllegalStateException("raceStorage is empty");
		}
		raceStorage.forEach(v -> RaceStorage.getInstance().readFromXML(v));

		if (battleActionStorage.isEmpty()) {
			throw new IllegalStateException("battleActionStorage is empty");
		}
		battleActionStorage.forEach(v -> BattleActionStorage.getInstance().readFromXML(v));

		if (battleField.isEmpty()) {
			throw new IllegalStateException("battleField is empty");
		}
		battleField.forEach(f -> BattleFieldSystem.getInstance().readFromXML(f));

		if (enemyList.isEmpty()) {
			throw new IllegalStateException("enemyList is empty");
		}
		enemyList.forEach(f -> EnemyStorage.getInstance().readFromXML(f));

		if (ess.isEmpty()) {
			throw new IllegalStateException("enemySet is empty");
		}
		ess.forEach(f -> EnemySetStorageStorage.getInstance().readFromXML(f));

		if (enemyProgressBarKey != null && !enemyProgressBarKey.isEmpty()) {
			Enemy.setProgressBarKey(enemyProgressBarKey);
		}
	}

	private void readStatusMaster(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile f = new XMLFile(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(f.getFile());
		}
		XMLElement root = f.load().getFirst();

		for (XMLElement e : root.getElement("status")) {
			String name = e.getAttributes().get("name").getValue();
			float min = e.getAttributes().get("min").getFloatValue();
			float max = e.getAttributes().get("max").getFloatValue();
			String desc = e.getAttributes().get("desc").getValue();
			String when0 = e.getAttributes().contains("when0") ? e.getAttributes().get("when0").getValue() : null;
			StatusKeyStorage.getInstance().add(new StatusKey(name, desc, min, max, when0));
		}
		StatusKeyStorage.getInstance().printAll(System.out);
		f.dispose();
	}

	private void readAttrMaster(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile f = new XMLFile(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(f.getFile());
		}

		XMLElement root = f.load().getFirst();

		for (XMLElement e : root.getElement("aAttribute")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			AttributeKeyStorage.getInstance().add(new AttributeKey(name, desc));
		}
		for (XMLElement e : root.getElement("dAttribute")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			AttributeKeyStorage.getInstance().add(new AttributeKey(name, desc));
		}
		AttributeKeyStorage.getInstance().printAll(System.out);
		f.dispose();
	}

	private void readConditionMaster(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile f = new XMLFile(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(f.getFile());
		}

		XMLElement root = f.load().getFirst();

		// キャラクタ状態異常マスタ
		for (XMLElement e : root.getElement("cCondition")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			int priority = e.getAttributes().get("pri").getIntValue();
			ConditionKey key = new ConditionKey(name, desc, priority);
			// エフェクトが入っていない場合エラー
			if (!e.hasElement("effect")) {
				throw new IllegalXMLFormatException("nCondition " + e + " is not have <effect>");
			}
			List<EffectMaster> list = new ArrayList<>();
			for (XMLElement ee : e.getElement("effect")) {
				EffectContinueType ect = EffectContinueType.valueOf(ee.getAttributes().get("ect").getValue());
				//NONEエフェクトの場合、追加して次
				if (ect == EffectContinueType.NONE) {
					list.add(new EffectMaster(key));
					continue;
				}
				EffectTargetType targetType = EffectTargetType.valueOf(ee.getAttributes().get("ett").getValue());
				float p = ee.getAttributes().get("p").getFloatValue();
				//ETTがSTOP、CONFU、ADD＿CONDITIONの場合、一部パラメタを省略して追加し、次
				int min = 0, max = 0;
				if (ee.hasAttribute("time")) {
					String time = ee.getAttributes().get("time").getValue();
					if (time.contains("/")) {
						min = ee.getAttributes().get("time").parseInt("/")[0];
						max = ee.getAttributes().get("time").parseInt("/")[1];
					} else if (time.equals("INFINITY")) {
						min = Integer.MAX_VALUE;
						max = Integer.MAX_VALUE;
					} else if (time.equals("1")) {
						min = max = 1;
					} else {
						throw new IllegalXMLFormatException("nCondition " + e + " s time is error");
					}
				}
				if (targetType == EffectTargetType.STOP) {
					list.add(new EffectMaster(key, ect, targetType, p, min, max));
					continue;
				}
				if (targetType == EffectTargetType.ADD_CONDITION) {
					String targetName = ee.getAttributes().get("tgt").getValue();
					list.add(new EffectMaster(key, ect, targetType, targetName, p));
					continue;
				}
				// NONEでもSTOPでもない場合、各要素を取得

				if (targetType == EffectTargetType.CONFU) {
					list.add(new EffectMaster(key, ect, targetType, min, max, p));
					continue;
				}
				EffectSetType est = EffectSetType.valueOf(ee.getAttributes().get("est").getValue());
				String targetName = ee.getAttributes().get("tgt").getValue();
				float value = ee.getAttributes().get("value").getFloatValue();
				list.add(new EffectMaster(key, ect, targetType, est, targetName, value, p, min, max));
			}
			//エフェクトのタイム検査
			List<EffectMaster> continueEffect = list.stream().filter(a -> a.getContinueType() == EffectContinueType.CONTINUE).collect(Collectors.toList());
			if (!continueEffect.isEmpty()) {
				for (int i = 0, min = continueEffect.get(0).getMinTime(), max = continueEffect.get(0).getMaxTime(); i > continueEffect.size(); i++) {
					if (min != continueEffect.get(i).getMinTime() || max != continueEffect.get(i).getMaxTime()) {
						throw new IllegalXMLFormatException("nCondition " + e + " s time is error, missmatch");
					}
				}
			}
			ConditionValueStorage.getInstance().add(new ConditionValue(key, list));
		}
		ConditionValueStorage.getInstance().printAll(System.out);

		//フィールド状態マスタ
		for (XMLElement e : root.getElement("fCondition")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			int priority = e.getAttributes().get("pri").getIntValue();
			ConditionKey key = new ConditionKey(name, desc, priority);
			List<EffectMaster> list = new ArrayList<>();
			for (XMLElement ee : e.getElement("effect")) {
				EffectContinueType ect = EffectContinueType.valueOf(ee.getAttributes().get("ect").getValue());
				//NONEエフェクトの場合、追加して次
				if (ect == EffectContinueType.NONE) {
					list.add(new EffectMaster(key));
					continue;
				}
				EffectTargetType targetType = EffectTargetType.valueOf(ee.getAttributes().get("ett").getValue());
				float p = ee.getAttributes().get("p").getFloatValue();
				//ETTがSTOP、CONFU、ADD＿CONDITIONの場合、一部パラメタを省略して追加し、次
				int min = 0, max = 0;
				if (ee.hasAttribute("time")) {
					String time = ee.getAttributes().get("time").getValue();
					if (time.contains("/")) {
						min = ee.getAttributes().get("time").parseInt("/")[0];
						max = ee.getAttributes().get("time").parseInt("/")[1];
					} else if (time.equals("INFINITY")) {
						min = Integer.MAX_VALUE;
						max = Integer.MAX_VALUE;
					} else if (time.equals("1")) {
						min = max = 1;
					} else {
						throw new IllegalXMLFormatException("nCondition " + e + " s time is error");
					}
				}
				if (targetType == EffectTargetType.STOP) {
					list.add(new EffectMaster(key, ect, targetType, p, min, max));
					continue;
				}
				if (targetType == EffectTargetType.ADD_CONDITION) {
					String targetName = ee.getAttributes().get("tgt").getValue();
					list.add(new EffectMaster(key, ect, targetType, targetName, p));
					continue;
				}
				// NONEでもSTOPでもない場合、各要素を取得
				if (targetType == EffectTargetType.CONFU) {
					list.add(new EffectMaster(key, ect, targetType, min, max, p));
					continue;
				}
				EffectSetType est = EffectSetType.valueOf(ee.getAttributes().get("est").getValue());
				String targetName = ee.getAttributes().get("tgt").getValue();
				float value = ee.getAttributes().get("value").getFloatValue();
				list.add(new EffectMaster(key, ect, targetType, est, targetName, value, p, min, max));
			}
			FieldConditionValueStorage.getInstance().add(new ConditionValue(key, list));
		}
		FieldConditionValueStorage.getInstance().printAll(System.out);

		f.dispose();
	}

	/**
	 * ステータスの作成をテストします。これはテスト用メソッドです。
	 *
	 * @param raceName 人種の名称。
	 * @return 問題なく作成できた場合trueを返します。
	 */
	public boolean testStatus(String raceName) {
		try {
			Status s = new Status("name", RaceStorage.getInstance().get(raceName));
			System.out.println(s);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
