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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kinugasa.game.NoLoopCall;
import kinugasa.util.*;
import kinugasa.resource.*;

/**
 * キャラクタや敵のステータス、持ち物、戦闘中取れる行動を持つクラスです。
 *
 * @vesion 1.0.0 - 2022/11/15_11:57:27<br>
 * @author Shinacho<br>
 */
public class Status implements Nameable {

	public static String canMagicStatusName = "CAN_MAGIC";
	public static String canMagicStatusValue = "1";
	//名前
	private String name;
	//ステータス本体
	private StatusValueSet status = new StatusValueSet();
	private StatusValueSet prevStatus;
	// 属性と状態異常に対する耐性
	private AttributeValueSet attrIn = new AttributeValueSet();
	//発生中の効果
	private final CharacterConditionValueSet condition = new CharacterConditionValueSet();
	// エフェクトの効果時間
	private final HashMap<ConditionKey, TimeCounter> conditionTimes = new HashMap<>();
	// 人種
	private final Race race;
	//持っているアイテム
	private ItemBag itemBag = new ItemBag();
	//持っている魔術書
	private BookBag bookBag = new BookBag();
	//装備品
	private final HashMap<ItemEqipmentSlot, Item> eqipment = new HashMap<>();
	//取れる行動
	private List<Action> actions = new ArrayList<>();
	//前衛・後衛
	private PartyLocation partyLocation = PartyLocation.FRONT;
	//生存状態
	private boolean exists = true;
	//実行中状態異常
	private Set<ConditionEffect> execEffect = new HashSet<>();
	//行動できない理由
	private Condition moveStopDesc;

	public Status(String name, Race race) {
		this.name = name;
		this.race = race;
		itemBag.setMax(race.getItemBagSize());
		bookBag.setMax(race.getBookBagSize());
		actions.addAll(itemBag.getItems());
		for (ItemEqipmentSlot slot : race.getEqipSlot()) {
			eqipment.put(slot, null);
		}
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public void setPartyLocation(PartyLocation partyLocation) {
		this.partyLocation = partyLocation;
	}

	public PartyLocation getPartyLocation() {
		return partyLocation;
	}

	public void setBaseAttrIn(AttributeValueSet attrIn) {
		this.attrIn = attrIn;
	}

	public void setBaseStatus(StatusValueSet status) {
		prevStatus = this.status;
		this.status = status;
	}

	public void setItemBag(ItemBag itemBag) {
		actions.removeAll(this.itemBag.getItems());
		this.itemBag = itemBag;
		actions.addAll(this.itemBag.getItems());
	}

	public void setBookBag(BookBag bookBag) {
		this.bookBag = bookBag;
	}

	public BookBag getBookBag() {
		return bookBag;
	}

	public boolean hasCondition(String name) {
		return conditionTimes.containsKey(ConditionStorage.getInstance().get(name).getKey());
	}

	public boolean hasConditions(boolean all, List<String> name) {
		boolean result = all;
		for (String n : name) {
			if (all) {
				result &= conditionTimes.containsKey(ConditionStorage.getInstance().get(n).getKey());
			} else {
				result |= conditionTimes.containsKey(ConditionStorage.getInstance().get(n).getKey());
				if (result) {
					return true;
				}
			}
		}
		return result;
	}

	public boolean hasConditions(boolean all, String... name) {
		return hasConditions(all, Arrays.asList(name));
	}

	public boolean canEqip(Item i) {
		if (!itemBag.contains(i)) {
			throw new GameSystemException(this + " not have item:" + i);
		}
		if (!i.getEqipTerm().stream().allMatch(p -> p.canEqip(this, i))) {
			return false;
		}
		return i.canEqip(this) && eqipment.keySet().contains(i.getEqipmentSlot());
	}

	public List<Action> getActions(ActionType type) {
		return actions.stream().filter(p -> p.getType() == type).collect(Collectors.toList());
	}

	@Override
	public String getName() {
		return name;
	}

	public int getWeaponArea() {
		int r = 0;
		for (Item i : eqipment.values()) {
			if (i != null) {
				r += i.getArea();
			}
		}
		return r;
	}

	public boolean hasAction(ParameterType batpt) {
		for (Action ba : actions) {
			for (ActionEvent e : ba.getBattleEvent()) {
				if (e.getParameterType() == batpt) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Action> getActions() {
		return actions;
	}

	public boolean hasAction(String name) {
		return actions.stream().anyMatch(p -> p.getName().equals(name));
	}

	//基礎ステータスを取得します。通常、レベルアップ等以外ではこの値は変わりません。
	public StatusValueSet getBaseStatus() {
		return status;
	}

	public void addEqip(Item i) {
		if (!itemBag.contains(i)) {
			throw new GameSystemException(name + " is not have " + i);
		}
		if (!i.canEqip(this)) {
			throw new GameSystemException(i + " is can not eqip");
		}
		ItemEqipmentSlot slot = i.getEqipmentSlot();
		if (eqipment.containsKey(slot)) {
			eqipment.remove(slot);
		}
		eqipment.put(slot, i);
	}

	public void passItem(Status tgt, Item i) {
		if (!itemBag.contains(i)) {
			throw new GameSystemException(name + " is not have " + i);
		}
		itemBag.drop(i);
		actions.remove(i);
		tgt.itemBag.add(i);
		tgt.actions.add(i);
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("STATUS item pass [" + getName() + "]s[" + i.getVisibleName() + "] to [" + tgt.getName() + "]");
		}
	}

	public void passBook(Status tgt, Book b) {
		if (!bookBag.contains(b)) {
			throw new GameSystemException(name + " is not have " + b);
		}
		bookBag.drop(b);
		tgt.bookBag.add(b);
	}

	public void updateAction() {
		updateAction(false);
	}

	public void updateAction(boolean itemAdd) {
		actions.clear();
		for (Action a : ActionStorage.getInstance()) {
			if (a.getType() == ActionType.ITEM) {
				continue;
			}
			if (a.getType() == ActionType.OTHER) {
				actions.add(a);
				continue;
			}
			//その他(MAGIC,ATTACK
			if (a.getTerms() != null && a.getTerms().stream().allMatch(p -> p.canExec(ActionTarget.instantTarget(this, a)))) {
				actions.add(a);
			}
		}
		if (itemAdd) {
			for (Item i : itemBag) {
				actions.add(i);
			}
		}
		//本から逆移入の処理
		if (getEffectedStatus().get(Status.canMagicStatusName).getValue() == (Float.parseFloat(Status.canMagicStatusValue))) {
			for (Book b : getBookBag()) {
				for (Action a : b.getBookAction()) {
					actions.add(a);
				}
			}
		}

		actions = actions.stream().distinct().collect(Collectors.toList());

//		if (GameSystem.isDebugMode()) {
//			kinugasa.game.GameLog.print("STATUS [" + getName() + "]s action update : ");
//			for (Action a : actions) {
//				kinugasa.game.GameLog.print(" " + a);
//			}
//			kinugasa.game.GameLog.print("---");
//		}
	}

	public void clearEqip() {
		eqipment.clear();
		for (ItemEqipmentSlot slot : race.getEqipSlot()) {
			eqipment.put(slot, null);
		}
	}

	public void removeEqip(Item i) {
		ItemEqipmentSlot slot = i.getEqipmentSlot();
		if (eqipment.containsKey(slot)) {
			eqipment.put(slot, null);
		}
	}

	public void removeEqip(ItemEqipmentSlot slot) {
		if (eqipment.containsKey(slot)) {
			eqipment.put(slot, null);
		}
	}

	public boolean isEqip(String id) {
		if (eqipment.values() == null) {
			return false;
		}
		for (Item i : eqipment.values()) {
			if (i == null) {
				continue;
			}
			if (i.getName().equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public boolean isEqip(ItemEqipmentSlot slot) {
		return eqipment.get(slot) != null;
	}

	public boolean isEqipWMType(String typeName) {
		if (eqipment.values() == null) {
			return false;
		}
		for (Item i : eqipment.values()) {
			if (i == null) {
				continue;
			}
			//防具等
			if (i.getWeaponMagicType() == null) {
				continue;
			}
			if (i.getWeaponMagicType().equals(WeaponTypeStorage.getInstance().get(typeName))) {
				return true;
			}
		}
		return false;
	}

	//混乱の状態異常が付与されているかを検査します
	public boolean isConfu() {
		for (Condition v : condition) {
			for (ConditionEffect e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.CONFU) {
					return true;
				}
			}
		}
		return false;
	}

	public Condition moveStopDesc() {
		return moveStopDesc;
	}

	// 発生中の効果に基づいて、このターン行動できるかを判定します
	public boolean canMoveThisTurn() {
		if (condition.isEmpty()) {
			assert conditionTimes.isEmpty() : "Condition and effectTimes are out of sync.";
			return true;
		}
		for (Condition v : condition) {
			for (ConditionEffect e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.STOP) {
					if (Random.percent(e.getP())) {
						moveStopDesc = v;
						return false;
					}
				}
			}
		}
		moveStopDesc = null;
		return true;
	}

	public void addCondition(Condition v) {
		// すでに発生している効果の場合、何もしない
		if (condition.contains(name)) {
			assert conditionTimes.containsKey(v.getKey()) : "Condition and effectTimes are out of sync.";
			return;
		}
		//耐性チェック
		List<AttributeKey> set = AttributeKeyStorage.getInstance().stream().filter(p -> p.getName().contains("C_" + name)).collect(Collectors.toList());
		if (!set.isEmpty()) {
			assert set.size() == 1 : "condition name is duplicated : " + set;
			if (!Random.percent(getEffectedAttrIn().get(set.get(0).getName()).getValue())) {
				//設定しない
				return;
			}
		}

		//優先度計算
		//新しい状態異常より優先度が低い状態異常を破棄
		int pri = v.getKey().getPriority();
		List<String> removeList = new ArrayList<>();
		for (Condition cv : condition) {
			if (cv.getKey().getPriority() < pri) {
				removeList.add(cv.getKey().getName());
			}
		}
		removeList.forEach(p -> removeCondition(p));

		//優先度が同一の状態異常がある場合、後勝ちで削除
		if (!condition.asList().stream().filter(s -> s.getKey().getPriority() == pri).collect(Collectors.toList()).isEmpty()) {
			condition.remove(name);
			conditionTimes.remove(new ConditionKey(name, "", 0));
		}
		List<ConditionEffect> effects = v.getEffects();
		//タイム算出
		List<ConditionEffect> continueEffect = effects.stream().filter(a -> a.getContinueType() == EffectContinueType.CONTINUE).collect(Collectors.toList());
		TimeCounter tc = continueEffect.isEmpty() ? TimeCounter.oneCounter() : continueEffect.get(0).createTimeCounter();
		//発生中の効果とエフェクト効果時間に追加
		condition.add(v);
		conditionTimes.put(v.getKey(), tc);

	}

	//状態異常を追加します
	public void addCondition(String name) {
		addCondition(ConditionStorage.getInstance().get(name));
	}

	//状態異常を追加します
	public void addCondition(ConditionKey k) {
		addCondition(k.getName());
	}

	@NoLoopCall
	public void update() {
		//状態異常による効果の実行
		List<ConditionEffect> addList = new ArrayList<>();
		for (int i = 0; i < condition.size(); i++) {
			Condition v = condition.asList().get(i);
			for (ConditionEffect e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.ADD_CONDITION) {
					if (Random.percent(e.getP()
							* (getEffectedAttrIn().contains(e.getTargetName())
							? getEffectedAttrIn().get(e.getTargetName()).getValue()
							: 1))) {

						e.exec(this);
						if (e.getContinueType() == EffectContinueType.ONECE) {
							addList.add(e);
						}
					}
				}
			}
		}
		execEffect.addAll(addList);

		List<ConditionKey> deleteList = new ArrayList<>();
		for (ConditionKey key : conditionTimes.keySet()) {
			if (conditionTimes.get(key).isReaching()) {
				deleteList.add(key);
			}
		}
		for (ConditionKey k : deleteList) {
			conditionTimes.remove(k);
			condition.remove(k.getName());
			//効果が終了したエフェクトのONCE実行済みフラグを除去する
			Condition v = ConditionStorage.getInstance().get(k.getName());
			for (ConditionEffect e : v.getEffects()) {
				if (execEffect.contains(e)) {
					execEffect.remove(e);
				}
			}
		}

	}

	// すべての状態異常を取り除きます
	public void clearCondition() {
		condition.clear();
		conditionTimes.clear();
	}

	// 状態異常を強制的に取り除きます
	public void removeCondition(String name) {
		Condition v = ConditionStorage.getInstance().get(name);
		condition.remove(v);
		conditionTimes.remove(v.getKey());
	}

	// 状態異常の効果時間を上書きします。状態異常が付与されていない場合はセットします。
	public void setConditionTime(String name, int time) {
		ConditionKey key = ConditionStorage.getInstance().get(name).getKey();
		Condition v = ConditionStorage.getInstance().get(name);
		if (condition.contains(v)) {
			removeCondition(name);
		}
		condition.put(v);
		conditionTimes.put(key, new FrameTimeCounter(time));
	}

	// 状態異常の効果時間を追加します。状態異常が付与されていない場合はセットします。
	public void addConditionTime(String name, int time) {
		ConditionKey key = ConditionStorage.getInstance().get(name).getKey();
		Condition v = ConditionStorage.getInstance().get(name);
		if (condition.contains(v)) {
			removeCondition(name);
		}
		time += conditionTimes.get(key).getCurrentTime();
		condition.put(v);
		conditionTimes.put(key, new FrameTimeCounter(time));
	}

	// conditionValueSetによる効果を適用させた値を返却
	//注意：ベースをうわがかないように参照を別にすること。
	//Pの判定を行っているので、毎回違う結果になる可能性がある。
	public StatusValueSet getEffectedStatus() {
		StatusValueSet r = status.clone();

		for (Condition v : condition) {
			for (ConditionEffect e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.STATUS) {
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
									tgtVal.addNoLimit(e.getValue());
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
		}
		for (ItemEqipmentSlot slot : eqipment.keySet()) {
			Item eqipItem = eqipment.get(slot);
			if (eqipItem != null) {
				for (StatusValue v : eqipItem.getEqStatus()) {
					r.get(v.getName()).addNoLimit(v.getValue());
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

		for (Condition v : condition) {
			for (ConditionEffect e : v.getEffects()) {
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
		for (ItemEqipmentSlot slot : eqipment.keySet()) {
			Item eqipItem = eqipment.get(slot);
			if (eqipItem != null) {
				for (AttributeValue v : eqipItem.getEqAttr()) {
					r.get(v.getName()).add(v.getValue());
				}
			}
		}

		return r;
	}

	//calcDamageのPREVを更新する
	public void setDamageCalcPoint() {
		prevStatus = status.clone();
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("STATUS save DCP:" + getName());
		}
	}

	//前回検査時からの差分を自動算出する
	/**
	 * ダメージ計算ポイントからのダメージを自動計算して返します。
	 *
	 * @return 前回のダメージ計算ポイントからの差分。0でない場合だけキーが含まれる.
	 */
	public Map<StatusKey, Float> calcDamage() {
		if (prevStatus == null) {
			return Collections.emptyMap();
		}

		Map<StatusKey, Float> result = new HashMap<>();

		for (StatusValue v : prevStatus) {
			float val = v.getValue() - status.get(v.getKey().getName()).getValue();
			if (val != 0) {
				result.put(v.getKey(), val);
			}
		}
//		if (GameSystem.isDebugMode()) {
//			kinugasa.game.GameLog.print("DCP<>DC[" + getName() + "] : " + result);
//		}

//		prevStatus = this.status;
		return result;
	}

	public HashMap<ConditionKey, TimeCounter> getConditionTimes() {
		return conditionTimes;
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

	@Override
	public String toString() {
		return "Status{" + "name=" + name + '}';
	}

	public StatusValueSet simulateDamage(Map<StatusKey, Integer> damage) {
		StatusValueSet result = getEffectedStatus();

		for (Map.Entry<StatusKey, Integer> e : damage.entrySet()) {
			result.get(e.getKey().getName()).addNoLimit(e.getValue());
		}

		return result;
	}

	public void updateItemBagSize() {
		int n = getRace().getItemBagSize();
		for (Map.Entry<String, Integer> m : ItemStorage.bagItems.entrySet()) {
			if (isEqip(m.getKey())) {
				n += m.getValue();
				break;
			}
		}
		getItemBag().setMax(n);
	}

}
