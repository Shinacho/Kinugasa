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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.util.*;
import kinugasa.resource.*;

/**
 * キャラクタや敵のステータス、持ち物、戦闘中取れる行動を持つクラスです。
 *
 * @vesion 1.0.0 - 2022/11/15_11:57:27<br>
 * @author Dra211<br>
 */
public class Status {

	//名前
	private String name;
	//ステータス本体
	private final StatusValueSet status = new StatusValueSet();
	// 属性と状態異常に対する耐性
	private final AttributeValueSet attrIn = new AttributeValueSet();
	//発生中の効果
	private final CharacterConditionValueSet condition = new CharacterConditionValueSet();
	// エフェクトの効果時間
	private final HashMap<ConditionKey, TimeCounter> effectTimes = new HashMap<>();
	// 人種
	private final Race race;
	//持っているアイテム
	private final ItemBag itemBag = new ItemBag();
	//装備品
	private final HashMap<ItemEqipmentSlot, Item> eqipment = new HashMap<>();
	//取れる行動
	private final Storage<BattleAction> battleActions = new Storage<>();

	public Status(String name, Race race) {
		this.name = name;
		this.race = race;
		itemBag.setMax(race.getItemBagSize());
	}

	public String getName() {
		return name;
	}

	public Storage<BattleAction> getBattleActions() {
		return battleActions;
	}

	//基礎ステータスを取得します。通常、レベルアップ等以外ではこの値は変わりません。
	public StatusValueSet getBaseStatus() {
		return status;
	}

	public void addEqip(Item i) {
		if (!itemBag.contains(i)) {
			throw new GameSystemException(name + " is not have " + i);
		}
		ItemEqipmentSlot slot = i.getEqipmentSlot();
		if (eqipment.containsKey(slot)) {
			eqipment.remove(slot);
		}
		eqipment.put(slot, i);
	}

	public void clearEqip() {
		eqipment.clear();
	}

	public void removeEqip(Item i) {
		ItemEqipmentSlot slot = i.getEqipmentSlot();
		if (eqipment.containsKey(slot)) {
			eqipment.remove(slot);
		}
	}

	//混乱の状態異常が付与されているかを検査します
	public boolean isConfu() {
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.CONFU) {
					return true;
				}
			}
		}
		return false;
	}

	// 発生中の効果に基づいて、このターン行動できるかを判定します
	public boolean canMoveThiTurn() {
		if (condition.isEmpty()) {
			assert effectTimes.isEmpty() : "conditionとeffectTimesの同期が取れていません";
			return true;
		}
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.STOP) {
					if (Random.percent(e.getP())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	//状態異常を追加します
	public void addCondition(String name) {
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		// すでに発生している効果の場合、何もしない
		if (condition.contains(name)) {
			assert effectTimes.containsKey(v.getKey()) : "conditionとeffectTimesの同期が取れていません";
			return;
		}
		//優先度計算
		//優先度が同一の状態異常がある場合、後勝ちで削除
		int pri = v.getKey().getPriority();
		if (!condition.asList().stream().filter(s -> s.getKey().getPriority() == pri).collect(Collectors.toList()).isEmpty()) {
			condition.remove(name);
			effectTimes.remove(new ConditionKey(name, "", 0));
		}
		List<EffectMaster> effects = v.getEffects();
		//タイム算出
		List<EffectMaster> continueEffect = effects.stream().filter(a -> a.getContinueType() == EffectContinueType.CONTINUE).collect(Collectors.toList());
		TimeCounter tc = continueEffect.isEmpty() ? TimeCounter.oneCounter() : continueEffect.get(0).createTimeCounter();
		//発生中の効果とエフェクト効果時間に追加
		condition.add(v);
		effectTimes.put(v.getKey(), tc);
	}

	//状態異常を追加します
	public void addCondition(ConditionKey k) {
		addCondition(k.getName());
	}

	//エフェクトの効果時間を引く
	//終了したエフェクトは、エフェクトタイムとコンディションから取り除く。
	public void update() {
		List<ConditionKey> deleteList = new ArrayList<>();
		for (ConditionKey key : effectTimes.keySet()) {
			if (effectTimes.get(key).isReaching()) {
				deleteList.add(key);
			}
		}
		for (ConditionKey k : deleteList) {
			effectTimes.remove(k);
			condition.remove(k.getName());
		}
	}

	// コンディションによるコンディション発生を設定する
	//Pの判定を行っているので、毎回違う結果になる可能性がある。
	// すでに発生している状態異常は付与しない。効果時間のリセットは別途作成すること
	public void updateCondition() {
		List<ConditionValue> addList = new ArrayList<>();
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.ADD_CONDITION) {
					if (Random.percent(e.getP())) {
						if (!condition.contains(e.getTargetName())) {
							addList.add(ConditionValueStorage.getInstance().get(e.getTargetName()));
							effectTimes.put(e.getKey(), e.createTimeCounter());
						}
					}
				}
			}
		}
		condition.addAll(addList);
	}

	// conditionValueSetによる効果を適用させた値を返却
	//注意：ベースをうわがかないように参照を別にすること。
	//Pの判定を行っているので、毎回違う結果になる可能性がある。
	public StatusValueSet getEffectedStatus() {
		StatusValueSet r = status.clone();

		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (r.contains(e.getTargetName())) {
					StatusValue tgtVal = r.get(e.getTargetName());
					if (Random.percent(e.getP())) {
						switch (e.getSetType()) {
							case ADD_PERCENT_OF_MAX:
								float val = tgtVal.getValue();
								val += (e.getValue() * tgtVal.getKey().getMax());
								tgtVal.set(val);
								break;
							case ADD_VALUE:
								tgtVal.add(e.getValue());
								break;
							case TO:
								tgtVal.set(e.getValue());
								break;
							default:
								throw new AssertionError();
						}
					}
				}
			}
		}

		return r;
	}

	// conditionValueSetによる効果を適用させた値を返却
	//注意：ベースをうわがかないように参照を別にすること。
	//Pの判定を行っているので、毎回違う結果になる可能性がある。
	public AttributeValueSet getEffectedAttrIn() {
		AttributeValueSet r = attrIn.clone();

		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (r.contains(e.getTargetName())) {
					AttributeValue tgtVal = r.get(e.getTargetName());
					if (Random.percent(e.getP())) {
						switch (e.getSetType()) {
							case ADD_PERCENT_OF_MAX:
								float val = tgtVal.getValue();
								val += (e.getValue() * tgtVal.getMax());
								tgtVal.set(val);
								break;
							case ADD_VALUE:
								tgtVal.add(e.getValue());
								break;
							case TO:
								tgtVal.set(e.getValue());
								break;
							default:
								throw new AssertionError();
						}
					}
				}
			}
		}
		return r;
	}

	public AttributeValueSet getBaseAttrIn() {
		return attrIn;
	}

	public CharacterConditionValueSet getCondition() {
		return condition;
	}

	public ItemBag getItemBag() {
		return itemBag;
	}

	public HashMap<ItemEqipmentSlot, Item> getEqipment() {
		return eqipment;
	}

	public Race getRace() {
		return race;
	}

	// 指定のバトルアクションを実行した際の、指定のステータス項目の増減を計算します。
	public int calcSelfStatusDamage(String battleActionName, String statusName) {
		if (!battleActions.contains(battleActionName)) {
			throw new NameNotFoundException(battleActionName + " is not fond");
		}
		return battleActions.get(battleActionName).calcSelfStatusDamage(this, statusName);
	}

	@Override
	public String toString() {
		return "Status{" + "name=" + name + ", status=" + status + ", attrIn=" + attrIn + ", condition=" + condition + ", effectTimes=" + effectTimes + ", race=" + race + ", itemBag=" + itemBag + ", eqipment=" + eqipment + ", battleActions=" + battleActions + '}';
	}

}
