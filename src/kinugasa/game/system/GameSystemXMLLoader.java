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
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.resource.text.*;
import kinugasa.resource.FileNotFoundException;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_14:34:32<br>
 * @author Shinacho<br>
 */
public class GameSystemXMLLoader {

	public GameSystemXMLLoader() {
	}
	private List<String> statusKeyStorage = new ArrayList<>();
	private List<String> attrKeyStorage = new ArrayList<>();
	private List<String> battleField = new ArrayList<>();
	private List<String> enemyList = new ArrayList<>();
	private List<String> ess = new ArrayList<>();
	private String enemyProgressBarKey;

	public GameSystemXMLLoader addStatusKeyStorage(String fileName) {
		statusKeyStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addAttrKeyStorage(String fileName) {
		attrKeyStorage.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addBattleField(String fileName) {
		battleField.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addEnemyMaster(String fileName) {
		this.enemyList.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addEnemySetStorage(String fileName) {
		this.ess.add(fileName);
		return this;
	}

	public GameSystemXMLLoader setEnemyProgressBarKey(String key) {
		this.enemyProgressBarKey = key;
		return this;
	}

	public String getEnemyProgressBarKey() {
		return enemyProgressBarKey;
	}

	public List<String> getEnemySetList() {
		return ess;
	}

	public List<String> getStatusKeyStorage() {
		return statusKeyStorage;
	}

	public List<String> getAttrKeyStorage() {
		return attrKeyStorage;
	}

	public List<String> getBattleField() {
		return battleField;
	}

	public List<String> getEnemyList() {
		return enemyList;
	}

	public void load() throws IllegalStateException {
		if (statusKeyStorage.isEmpty()) {
			throw new IllegalStateException("statusMaster is empty");
		}
		statusKeyStorage.forEach(v -> readStatusMaster(v));

		if (attrKeyStorage.isEmpty()) {
			throw new IllegalStateException("attrMaster is empty");
		}
		attrKeyStorage.forEach(v -> readAttrMaster(v));

		if (battleField.isEmpty()) {
			throw new IllegalStateException("battleField is empty");
		}
		battleField.forEach(f -> BattleFieldSystem.getInstance().readFromXML(f));

		enemyList.forEach(f -> EnemyStorage.getInstance().readFromXML(f));

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
			int order = e.getAttributes().get("order").getIntValue();
			String desc = e.getAttributes().get("desc").getValue();
			String when0 = e.getAttributes().contains("when0") ? e.getAttributes().get("when0").getValue() : null;
			StatusKeyStorage.getInstance().add(new StatusKey(name, desc, order, min, max, when0));
		}
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
			int order = e.getAttributes().get("order").getIntValue();
			AttributeKeyStorage.getInstance().add(new AttributeKey(name, desc, order));
		}
		for (XMLElement e : root.getElement("dAttribute")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			int order = e.getAttributes().get("order").getIntValue();
			AttributeKeyStorage.getInstance().add(new AttributeKey(name, desc, order));
		}
		f.dispose();
	}
//
//	private void readConditionMaster(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
//		XMLFile f = new XMLFile(filePath);
//		if (!f.exists()) {
//			throw new FileNotFoundException(f.getFile());
//		}
//
//		XMLElement root = f.load().getFirst();
//
//		// キャラクタ状態異常マスタ
//		for (XMLElement e : root.getElement("condition")) {
//			String name = e.getAttributes().get("name").getValue();
//			String desc = e.getAttributes().get("desc").getValue();
//			int priority = e.getAttributes().get("pri").getIntValue();
//			ConditionKey key = new ConditionKey(name, desc, priority);
//			// エフェクトが入っていない場合エラー
//			if (!e.hasElement("effect")) {
//				throw new IllegalXMLFormatException("nCondition " + e + " is not have <effect>");
//			}
//			List<ConditionEffect> list = new ArrayList<>();
//			for (XMLElement ee : e.getElement("effect")) {
//				EffectContinueType ect = EffectContinueType.valueOf(ee.getAttributes().get("ect").getValue());
//				//NONEエフェクトの場合、追加して次
//				if (ect == EffectContinueType.NONE) {
//					list.add(new ConditionEffect(key));
//					continue;
//				}
//				EffectTargetType targetType = EffectTargetType.valueOf(ee.getAttributes().get("ett").getValue());
//				float p = ee.getAttributes().get("p").getFloatValue();
//				//ETTがSTOP、CONFU、ADD＿CONDITIONの場合、一部パラメタを省略して追加し、次
//				int min = 0, max = 0;
//				if (ee.hasAttribute("time")) {
//					String time = ee.getAttributes().get("time").getValue();
//					if (time.contains("/")) {
//						min = ee.getAttributes().get("time").parseInt("/")[0];
//						max = ee.getAttributes().get("time").parseInt("/")[1];
//					} else if (time.equals("INFINITY")) {
//						min = Integer.MAX_VALUE;
//						max = Integer.MAX_VALUE;
//					} else if (time.equals("1")) {
//						min = max = 1;
//					} else {
//						throw new IllegalXMLFormatException("nCondition " + e + " s time is error");
//					}
//				}
//				if (targetType == EffectTargetType.STOP) {
//					list.add(new ConditionEffect(key, ect, targetType, p, min, max));
//					continue;
//				}
//				if (targetType == EffectTargetType.ADD_CONDITION) {
//					String targetName = ee.getAttributes().get("tgt").getValue();
//					list.add(new ConditionEffect(key, ect, targetType, targetName, p));
//					continue;
//				}
//				// NONEでもSTOPでもない場合、各要素を取得
//
//				if (targetType == EffectTargetType.CONFU) {
//					list.add(new ConditionEffect(key, ect, targetType, min, max, p));
//					continue;
//				}
//				EffectSetType est = EffectSetType.valueOf(ee.getAttributes().get("est").getValue());
//				String targetName = ee.getAttributes().get("tgt").getValue();
//				float value = ee.getAttributes().get("value").getFloatValue();
//				list.add(new ConditionEffect(key, ect, targetType, est, targetName, value, p, min, max));
//			}
//			//エフェクトのタイム検査
//			List<ConditionEffect> continueEffect = list.stream().filter(a -> a.getContinueType() == EffectContinueType.CONTINUE).collect(Collectors.toList());
//			if (!continueEffect.isEmpty()) {
//				for (int i = 0, min = continueEffect.get(0).getMinTime(), max = continueEffect.get(0).getMaxTime(); i > continueEffect.size(); i++) {
//					if (min != continueEffect.get(i).getMinTime() || max != continueEffect.get(i).getMaxTime()) {
//						throw new IllegalXMLFormatException("nCondition " + e + " s time is error, missmatch");
//					}
//				}
//			}
//			ConditionValueStorage.getInstance().add(new ConditionValue(key, list));
//		}
//
//		f.dispose();
//	}

	/**
	 * ステータスの作成をテストします。これはテスト用メソッドです。
	 *
	 * @param raceName 人種の名称。
	 * @return 問題なく作成できた場合trueを返します。
	 */
	public boolean testStatus(String raceName) {
		try {
			Status s = new Status("name", RaceStorage.getInstance().get(raceName));
			kinugasa.game.GameLog.print(s);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
