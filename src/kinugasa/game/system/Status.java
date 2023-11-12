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
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kinugasa.game.GameLog;
import kinugasa.game.NoLoopCall;
import kinugasa.game.Nullable;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.util.ManualTimeCounter;
import kinugasa.util.Random;
import kinugasa.game.NewInstance;
import kinugasa.game.NotNewInstance;
import kinugasa.object.Model;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_11:23:10<br>
 * @author Shinacho<br>
 */
public final class Status extends Model implements Nameable {

	//ID
	private String id;
	//ステータス本体
	private StatusValueSet status = new StatusValueSet();
	private StatusValueSet prevStatus;
	//ATTR
	private AttributeValueSet attrIn = new AttributeValueSet();
	private AttributeValueSet attrOut = new AttributeValueSet();
	//状態異常耐性
	private ConditionRegist conditionRegist = new ConditionRegist();
	//発生中の状態異常
	private EnumMap<ConditionKey, ManualTimeCounter> currentCondition = new EnumMap<>(ConditionKey.class);
	//人種
	private Race race;
	//アイテム
	private PersonalBag<Item> itemBag = new PersonalBag<>();
	//本
	private PersonalBag<Book> bookBag = new PersonalBag<>();
	//装備品
	private Map<EqipSlot, Item> eqip = new HashMap<>();
	//とれる行動
	private Storage<Action> actions = new Storage<>();
	//前列・後列
	private PartyLocation partyLocation = PartyLocation.FRONT;
	//状態異常による各種確率
	private ConditionFlags conditionFlags = new ConditionFlags();
	//キャラの特性
	private CharaAbility ability;

	@Override
	public Status clone() {
		Status r = (Status) super.clone();
		r.status = this.status.clone();
		r.attrIn = this.attrIn.clone();
		r.attrOut = this.attrOut.clone();
		r.conditionFlags = this.conditionFlags.clone();
		r.conditionRegist = this.conditionRegist.clone();
		r.currentCondition = this.currentCondition.clone();
		return r;
	}
	

	public Status(String id, Race r) {
		this.id = id;
		this.race = r;
		itemBag.setMax(race.getItemBagSize());
		bookBag.setMax(race.getBookBagSize());
		for (EqipSlot slot : race.getEqipSlots()) {
			eqip.put(slot, null);
		}
		this.status.init();
		this.attrIn.init();
		this.attrOut.init();
		this.conditionRegist.init();
	}

	public void setAbility(CharaAbility ability) {
		this.ability = ability;
	}

	public CharaAbility getAbility() {
		return ability;
	}

	private ConditionKey lastAddedConditin = null;

	public ConditionKey getLastAddedConditin() {
		ConditionKey r = lastAddedConditin;
		lastAddedConditin = null;
		return r;
	}

	//優先度強の物は他のを消す
	//タイムが長ければ上書きする。
	//その際、もしくは初回登録時に初回ターンイベントを実行する。
	//戻り値は１状態異常に対応するので、貯めて出力すること
	//解脱または損壊の場合、時間は無視される。
	@Nullable
	public String addCondition(ConditionKey key, int time) {
		ManualTimeCounter t = time < 0 ? ManualTimeCounter.FALSE : new ManualTimeCounter(time);
		if (key == ConditionKey.解脱 && currentCondition.containsKey(ConditionKey.解脱)) {
			return getVisibleName() + ConditionKey.解脱.getExecMsgI18Nd();
		}
		//優先度順に処理
		if (key == ConditionKey.解脱) {
			currentCondition.clear();
			currentCondition.put(key, ManualTimeCounter.FALSE);
			lastAddedConditin = key;
			key.startEffect(getConditionFlags());
			return getVisibleName() + key.getStartMsgI18Nd();
		}
		if (hasCondition(ConditionKey.解脱)) {
			return getVisibleName() + ConditionKey.解脱.getExecMsgI18Nd();
		}
		if (key == ConditionKey.損壊) {
			currentCondition.clear();
			currentCondition.put(key, ManualTimeCounter.FALSE);
			lastAddedConditin = key;
			key.startEffect(this.conditionFlags);
			return getVisibleName() + key.getStartMsgI18Nd();
		}
		if (hasCondition(ConditionKey.損壊)) {
			return getVisibleName() + ConditionKey.損壊.getExecMsgI18Nd();
		}
		if (key == ConditionKey.気絶) {
			//気絶は再付与が行われる
			currentCondition.clear();
			currentCondition.put(key, t);
			lastAddedConditin = key;
			key.startEffect(this.conditionFlags);
			return getVisibleName() + key.getStartMsgI18Nd();
		}
		if (hasCondition(ConditionKey.気絶)) {
			return getVisibleName() + ConditionKey.気絶.getExecMsgI18Nd();
		}
		//3大状態異常以外の処理
		//寝た場合、ステータスを初期化する
		if (key == ConditionKey.眠り) {
			for (Actor a : Stream.of(GameSystem.getInstance().getParty(), BattleSystem.getInstance().getEnemies()).flatMap(p -> p.stream()).toList()) {
				if (a.getId().equals(id)) {
					a.退避＿ステータスの初期化されない項目();
					a.readFromXML();
					a.復元＿ステータスの初期化されない項目();
					a.getStatus().currentCondition.put(key, t);
					return getVisibleName() + key.getStartMsgI18Nd();
				}
			}
		}

		if (currentCondition.containsKey(key)) {
			if (currentCondition.get(key).getCurrentTime() < time) {
				currentCondition.put(key, t);
				lastAddedConditin = key;
				//再付与したときは初回イベントを起動しない
				return getVisibleName() + key.getStartMsgI18Nd();
			} else {
				//再付与しなかった=状態異常は追加されていない
				return null;
			}
		}
		currentCondition.put(key, t);
		lastAddedConditin = key;
		key.startEffect(conditionFlags);
		addWhen0Condition();
		return getName() + key.getStartMsgI18Nd();
	}

	@Nullable
	public String getVisibleName() {
		for (Actor a : Stream.of(GameSystem.getInstance().getParty(), BattleSystem.getInstance().getEnemies()).flatMap(p -> p.stream()).toList()) {
			if (a.getId().equals(id)) {
				return a.getVisibleName();
			}
		}
		return null;
	}

	@Nullable
	public String removeCondition(ConditionKey key) {
		if (currentCondition.containsKey(key)) {
			key.endEffect(conditionFlags);
			currentCondition.remove(key);
			return getVisibleName() + key.getEndMsgI18Nd();
		}
		return null;
	}

	@Nullable
	public String addWhen0Condition() {
		StatusValueSet vs = getEffectedStatus();
		//ステータスが0になったことによる付与
		if (vs.get(StatusKey.正気度).isZero()) {
			addCondition(ConditionKey.解脱, Integer.MAX_VALUE);
			ConditionKey.解脱.startEffect(conditionFlags);
			return getVisibleName() + ConditionKey.解脱.getStartMsgI18Nd();
		}
		if (vs.get(StatusKey.体力).isZero()) {
			addCondition(ConditionKey.損壊, Integer.MAX_VALUE);
			ConditionKey.損壊.startEffect(conditionFlags);
			return getVisibleName() + ConditionKey.損壊.getStartMsgI18Nd();
		}
		if (vs.get(StatusKey.魔力).isZero()) {
			addCondition(ConditionKey.気絶, Random.d10(1) + 2);
			ConditionKey.気絶.startEffect(conditionFlags);
			return getVisibleName() + ConditionKey.気絶.getStartMsgI18Nd();
		}
		return null;
	}

	/**
	 * 状態異常の効果時間を経過させ、それにともなうフラグの設定を行います。<br>
	 * またステータス起因による状態異常の追加も行います。 行動が停止していたり、混乱している場合はメッセージを返します。
	 */
	@Nullable
	@NoLoopCall("1-call/1-turn")
	public String updateCondition() {
		StatusValueSet vs = getEffectedStatus();
		if (currentCondition.containsKey(ConditionKey.解脱)) {
			if (!vs.get(StatusKey.正気度).isZero()) {
				ConditionKey.解脱.endEffect(conditionFlags);
				return removeCondition(ConditionKey.解脱);
			}
			return getVisibleName() + ConditionKey.解脱.getExecMsgI18Nd();
		}
		if (currentCondition.containsKey(ConditionKey.損壊)) {
			if (!vs.get(StatusKey.体力).isZero()) {
				ConditionKey.損壊.endEffect(conditionFlags);
				return removeCondition(ConditionKey.損壊);
			}
			return getVisibleName() + ConditionKey.損壊.getEndMsgI18Nd();
		}
		if (currentCondition.containsKey(ConditionKey.気絶)) {
			currentCondition.get(ConditionKey.気絶).add(-1);
			if (currentCondition.get(ConditionKey.気絶).isReaching()
					|| !vs.get(StatusKey.魔力).isZero()) {
				removeCondition(ConditionKey.気絶);
				ConditionKey.気絶.endEffect(conditionFlags);
				return removeCondition(ConditionKey.損壊);
			}
			return getVisibleName() + ConditionKey.気絶.getExecMsgI18Nd();
		}
		addWhen0Condition();
		//発生中効果の処理
		for (ConditionKey k : currentCondition.keySet()) {
			ConditionFlags.ConditionPercent p = k.getPercent();
			if (p.is停止()) {
				ManualTimeCounter tc = currentCondition.get(k);
				tc.add(-1);
				if (tc.isReaching()) {
					removeCondition(k);
				}
				return getVisibleName() + k.getExecMsgI18Nd();
			}
			if (p.is混乱()) {
				ManualTimeCounter tc = currentCondition.get(k);
				tc.add(-1);
				if (tc.isReaching()) {
					removeCondition(k);
				}
				return getVisibleName() + GameSystemI18NKeys.混乱している;
			}
			ManualTimeCounter tc = currentCondition.get(k);
			tc.add(-1);
			if (tc.isReaching()) {
				removeCondition(k);//解除時はMSG表示なし（暫定）
			}
		}
		return null;//特別な表示はない
	}

	public boolean hasCondition(ConditionKey key) {
		return currentCondition.containsKey(key);
	}

	@NewInstance
	public Map<ConditionKey, ManualTimeCounter> getCurrentConditions() {
		Map<ConditionKey, ManualTimeCounter> r = new HashMap<>();
		for (Map.Entry<ConditionKey, ManualTimeCounter> e : currentCondition.entrySet()) {
			r.put(e.getKey(), e.getValue());
		}
		return r;
	}

	//持っている本、装備品からアクションを導入する
	@NoLoopCall("event, this is hevy")
	public void updateAction() {
		actions.clear();
		//行動アクションの導入
		actions.addAll(ActionStorage.getInstance().allOf(ActionType.行動));
		if (GameSystem.getInstance().getPCbyID(id) == null) {
			actions.remove(BattleConfig.ActionID.状態);
		}
		//アイテム
		actions.addAll(getItemBag().getItems().stream().filter(p -> p.hasEvent()).collect(Collectors.toList()));
		//魔法（本から移入
		if (getEffectedStatus().get(StatusKey.魔術使用可否).getValue() == StatusKey.魔術使用可否＿使用可能) {
			actions.addAll(getBookBag().getItems().stream().map(p -> p.getAction()).collect(Collectors.toList()));
		}
		//攻撃
		for (Action a : ActionStorage.getInstance().allOf(ActionType.攻撃)) {
			if (a.canDo(this)) {//装備判定はここで行われる
				actions.add(a);
			}
		}
		//実施できないアクションを除外
		List<Action> remove = new ArrayList<>();
		for (Action a : actions) {
			if (!a.canDo(this)) {
				remove.add(a);
			}
		}
		actions.removeAll(remove);
	}

	//アイテムの効果も載せる。その時に左手が漁手持ちだったら右手の効果を2倍にする
	@NewInstance
	public StatusValueSet getEffectedStatus() {
		StatusValueSet s = this.status.clone();

		//アイテム
		for (Map.Entry<EqipSlot, Item> e : eqip.entrySet()) {
			if (e.getValue() == null) {
				continue;
			}
			switch (e.getKey()) {
				case 右手: {
					if (eqip.containsKey(EqipSlot.左手)
							&& eqip.get(EqipSlot.左手) != null
							&& eqip.get(EqipSlot.左手).equals(ActionStorage.getInstance().両手持ち)) {
						if (!Set.of(WeaponType.大剣, WeaponType.大杖, WeaponType.弓, WeaponType.銃, WeaponType.弩, WeaponType.薙刀)
								.contains(e.getValue().getWeaponType())) {
							s = s.composite(e.getValue().getEffectedStatus());
							s = s.composite(e.getValue().getEffectedStatus());
						} else {
							s = s.composite(e.getValue().getEffectedStatus());
						}
					} else {
						s = s.composite(e.getValue().getEffectedStatus());
					}
					break;
				}
				case 左手: {
					if (!e.getValue().getId().equals("TWO_HAND")) {
						s = s.composite(e.getValue().getEffectedStatus());
					}
					break;
				}
				case 胴体:
				case 装飾品:
				case 足:
				case 頭: {
					s = s.composite(e.getValue().getEffectedStatus());
					break;
				}
				default:
					throw new AssertionError("undefined eqip slot");
			}
		}
		//バフデバフ
		StatusValueSet vs = s.clone();
		List<ConditionKey> list = new ArrayList<>(getCurrentConditions().keySet());
		Collections.sort(list);
		for (ConditionKey k : list) {
			s = s.composite(k.getStatusValue(vs));
		}
		//アビリティ
		if (this.ability != null) {
			s = ability.effectStatus(this, s);
		}
		return s;
	}

	@NewInstance
	public AttributeValueSet getEffectedAttrIn() {
		AttributeValueSet r = this.attrIn.clone();
		//アイテム
		for (Item i : getEqip().values()) {
			if (i == null) {
				continue;
			}
			r = r.composite(i.getEffectedAttrIn());
		}
		AttributeValueSet rr = r.clone();
		//バフデバフ
		for (ConditionKey k : getCurrentConditions().keySet()) {
			r = r.composite(k.getAttrIn(rr));
		}
		//アビリティ
		if (this.ability != null) {
			r = ability.effectAttrIn(this, r);
		}
		return r;
	}

	@NewInstance
	public AttributeValueSet getEffectedAttrOut() {
		AttributeValueSet r = this.attrOut.clone();
		//アイテム
		for (Item i : getEqip().values()) {
			if (i == null) {
				continue;
			}
			r = r.composite(i.getEffectedAttrOut());
		}
		AttributeValueSet rr = r.clone();
		//バフデバフ
		for (ConditionKey k : getCurrentConditions().keySet()) {
			r = r.composite(k.getAttrOut(rr));
		}
		//アビリティ
		if (this.ability != null) {
			r = ability.effectAttrOut(this, r);
		}
		return r;
	}

	@NewInstance
	public ConditionRegist getEffectedConditionRegist() {
		ConditionRegist r = this.conditionRegist.clone();
		//アイテム
		for (Item i : getEqip().values()) {
			if (i == null) {
				continue;
			}
			r = r.add(i.getEffectedConditionRegist());
		}
		ConditionRegist rr = r.clone();
		//バフデバフ
		for (ConditionKey k : getCurrentConditions().keySet()) {
			r = r.add(k.getCndRegist(rr));
		}
		//アビリティ
		if (this.ability != null) {
			r = ability.effectCndRegist(this, r);
		}
		return r;
	}

	public int getEffectedArea(Action a) {
		if (!actions.contains(a)) {
			throw new GameSystemException("this action is not have me : " + a);
		}
		if (a.getType() == ActionType.行動) {
			if (a.getId().equals(BattleConfig.ActionID.逃走)) {
				return (int) (getEffectedStatus().get(StatusKey.行動力).getValue());
			}
			return 0;
		}
		if (a.getType() == ActionType.アイテム) {
			return (int) (getEffectedStatus().get(StatusKey.行動力).getValue() / 2);
		}
		int r = a.getArea();
		for (Item i : eqip.values()) {
			if (i == null) {
				continue;
			}
			r += i.getEffectedArea();
		}
		return r;
	}

	public int getEffectedAtkCount() {
		int r = 0;
		for (Item i : eqip.values()) {
			if (i == null) {
				continue;
			}
			r += i.getEffectedATKCount();
		}
		return r;
	}

	public void saveBeforeDamageCalc() {
		prevStatus = getEffectedStatus().clone();
		GameLog.print(getName() + " saved");
	}

	public boolean has武器() {
		if (this.eqip.containsKey(EqipSlot.右手)) {
			if (eqip.get(EqipSlot.右手) != null) {
				return true;
			}
		}
		if (this.eqip.containsKey(EqipSlot.左手)) {
			if (eqip.get(EqipSlot.左手) != null) {
				return true;
			}
		}
		return false;
	}

	@NewInstance
	public StatusValueSet getDamageFromSavePoint() {
		if (prevStatus == null) {
			return new StatusValueSet();
		}
		StatusValueSet r = new StatusValueSet();
		StatusValueSet now = getEffectedStatus();
		//this - prev
		for (StatusKey k : StatusKey.values()) {
			float v = now.get(k).getValue() - prevStatus.get(k).getValue();
			if (v == 0) {
				continue;
			}
			r.put(new StatusValue(k, v));
		}
		GameLog.print(getName() + " from save / " + r);
		return r;
	}

	@NotNewInstance
	public Race getRace() {
		return race;
	}

	public int getAreaWithEqip(Action a) {
		if (!hasAction(a)) {
			throw new GameSystemException("im not have this action : " + this + " / " + a);
		}
		int area = a.getArea();
		//eqipAreaの加算
		for (Item i : eqip.values()) {
			if (i != null) {
				area += i.getEffectedArea();
			}
		}
		return area;
	}

	public void eqip(EqipSlot s, Item i) {
		if (i.getId().equals("TWO_HAND")) {
			eqip.put(EqipSlot.左手, i);
			return;
		}
		if (!getItemBag().has(i)) {
			throw new GameSystemException("add eqip, but im not have :" + id + " / " + i);
		}
		EqipSlot slot = s;
		if (slot == null) {
			throw new GameSystemException("this item is cant eqip : " + i);
		}
		if (!getRace().getEqipSlots().contains(slot)) {
			throw new GameSystemException("this item is cant eqip : " + i);
		}
		if (eqip.get(slot) != null) {
			eqip.get(slot).unEqip(conditionFlags);
		}
		eqip.put(slot, i);
		i.eqip(conditionFlags);
		updateAction();

	}

	public void eqip(Item i) {
		eqip(i.getSlot(), i);
	}

	//アイテムが武器であることを確認すること！
	public void eqipLeftHand(Item i) {
		if (!getItemBag().has(i)) {
			throw new GameSystemException("add eqip, but im not have :" + id + " / " + i);
		}
		EqipSlot slot = EqipSlot.左手;
		if (!getRace().getEqipSlots().contains(slot)) {
			throw new GameSystemException("this item is cant eqip : " + i);
		}
		if (eqip.get(slot) != null) {
			eqip.get(slot).unEqip(conditionFlags);
		}
		eqip.put(slot, i);
		i.eqip(conditionFlags);
		updateAction();
	}

	public void unEqip(EqipSlot s) {
		if (eqip.containsKey(s)) {
			if (eqip.get(s) != null) {
				eqip.get(s).unEqip(conditionFlags);
				updateAction();
			}
			eqip.put(s, null);
		}
	}

	@NotNewInstance
	public Map<EqipSlot, Item> getEqip() {
		return eqip;
	}

	@NotNewInstance
	public PersonalBag<Item> getItemBag() {
		return itemBag;
	}

	@NotNewInstance
	public PersonalBag<Book> getBookBag() {
		return bookBag;
	}

	public boolean canDoThis(Action a) {
		if (a.getType() == ActionType.行動) {
			return true;
		}
		if (a.getType() == ActionType.魔法
				&& getEffectedStatus().get(StatusKey.魔術使用可否).getValue() != StatusKey.魔術使用可否＿使用可能) {
			return false;
		}
		if (!hasAction(a)) {
			throw new GameSystemException("i am not have this action : " + a);
		}
		return a.canDo(this);
	}

	public boolean hasAction(Action a) {
		return actions.contains(a);
	}

	public boolean hasAction(String a) {
		return actions.contains(a);
	}

	public boolean hasAnyCondition(ConditionKey... k) {
		for (ConditionKey key : k) {
			if (hasCondition(key)) {
				return true;
			}
		}
		return false;
	}

	@NotNewInstance
	public StatusValueSet getBaseStatus() {
		return status;
	}

	@NotNewInstance
	public ConditionFlags getConditionFlags() {
		return conditionFlags;
	}

	@Deprecated
	@Override
	public String getName() {
		return id;
	}

	public String getId() {
		return id;
	}

	@Deprecated
	public void addAction(Action a) {
		actions.add(a);
	}

	public void addDamage(StatusKey sk, float damage) {
		getBaseStatus().get(sk).add(damage);
	}

	@NotNewInstance
	public AttributeValueSet getAttrOut() {
		return attrOut;
	}

	@NotNewInstance
	public AttributeValueSet getAttrIn() {
		return attrIn;
	}

	@NotNewInstance
	public ConditionRegist getConditionRegist() {
		return conditionRegist;
	}

	@NotNewInstance
	public Storage<Action> getActions() {
		return actions;
	}

	@NewInstance
	public List<Action> get現状実行可能なアクション() {
		return actions.stream().filter(p -> p.canDo(this)).toList();
	}

	public void clearCondition() {
		conditionFlags = new ConditionFlags();
		currentCondition.clear();
	}

	public void pass(Item i, Status tgt) {
		if (!getItemBag().contains(i)) {
			throw new GameSystemException("i(" + id + ") dont have this item : " + i);
		}
		if (!tgt.getItemBag().canAdd()) {
			throw new GameSystemException("tgt(" + id + ") cant have this item : " + i);
		}
		getItemBag().drop(i);
		tgt.getItemBag().add(i);
	}

	public void pass(Book b, Status tgt) {
		if (!getBookBag().contains(b)) {
			throw new GameSystemException("i(" + id + ") dont have this book : " + b);
		}
		if (!tgt.getBookBag().canAdd()) {
			throw new GameSystemException("tgt(" + id + ") cant have this book : " + b);
		}
		getBookBag().drop(b);
		tgt.getBookBag().add(b);
	}

	public void updateBagSize() {
		//もともとのサイズに加えて、ActionStorageに置いてあるバッグアイテムを装備していたら容量追加を行う。
		int itemBagSize = race.getItemBagSize();
		for (Item i : eqip.values()) {
			if (i == null) {
				continue;
			}
			if (ActionStorage.isItemBagItem(i.getId())) {
				itemBagSize += ActionStorage.getItemBagAddSize(i.getId());
			}
		}
		itemBag.setMax(itemBagSize);

		int bookBagSize = race.getBookBagSize();
		for (Item i : eqip.values()) {
			if (i == null) {
				continue;
			}
			if (ActionStorage.isBookBagItem(i.getId())) {
				itemBagSize += ActionStorage.getBookBagAddSize(i.getId());
			}
		}
		bookBag.setMax(bookBagSize);

	}

	@NotNewInstance
	@Deprecated
	public EnumMap<ConditionKey, ManualTimeCounter> getCurrentCondition() {
		return currentCondition;
	}

	public PartyLocation getPartyLocation() {
		return partyLocation;
	}

	public void setPartyLocation(PartyLocation partyLocation) {
		this.partyLocation = partyLocation;
	}

	public int mulExp(int exp) {
		int res = exp;
		for (Item i : eqip.values()) {
			if (i == null) {
				continue;
			}
			res = i.getEffectedExp(res);
		}
		for (ConditionKey k : currentCondition.keySet()) {
			res = k.mulExp(res);
		}
		return res;
	}

	@Override
	public String toString() {
		return "Status{" + "name=" + id + ", status=" + status + '}';
	}
}
