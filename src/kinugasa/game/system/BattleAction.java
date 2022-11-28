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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.object.Drawable;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:32:30<br>
 * @author Dra211<br>
 */
public class BattleAction implements Nameable, Comparable<BattleAction> {

	/**
	 * 行動種類
	 */
	private BattleActionType type;
	// このアクションのID
	private String name;
	private String desc;
	//操作の一覧
	private List<BattleActionEvent> events = new ArrayList<>();
	// このアクションが実施できるか判断するロジック
	private List<BattleActionEventTerm> term = new ArrayList<>();
	//利用停止フラグ・・・アクションを削除しない一時的な封印に使う
	private boolean stop = false;
	//フィールド利用可能フラグ
	private boolean fieldUse = false;
	//魔法詠唱時間
	private int spellTime = 0;
	//効果範囲
	private int area = 0;
	//アクションの効果音
	private Sound sound;
	//ソートキー
	private int sort;
	//アクション待ち時間
	private FrameTimeCounter waitTime = new FrameTimeCounter(1);

	public BattleAction(BattleAction a) {
		this(a.type, a.name, a.desc);
		setEvent(a.events);
		setTerm(a.term);
		setFieldUse(a.fieldUse);
		setSpellTime(a.spellTime);
		setArea(a.area);
		setSound(a.sound);
		setWaitTime(a.waitTime.clone());
	}

	public BattleAction(BattleActionType type, String name, String desc) {
		this.type = type;
		this.name = name;
		this.desc = desc;
	}

	public BattleAction setEvent(List<BattleActionEvent> events) {
		this.events = events;
		return this;
	}

	public BattleAction addEvent(BattleActionEvent e) {
		events.add(e);
		return this;
	}

	public BattleAction setTerm(List<BattleActionEventTerm> term) {
		this.term = term;
		return this;
	}

	public BattleAction addTerm(BattleActionEventTerm t) {
		term.add(t);
		return this;
	}

	public BattleAction setSound(Sound sound) {
		this.sound = sound;
		return this;
	}

	public BattleAction setFieldUse(boolean f) {
		this.fieldUse = f;
		return this;
	}

	public BattleAction setSpellTime(int t) {
		this.spellTime = t;
		return this;
	}

	public BattleAction setArea(int a) {
		this.area = a;
		return this;
	}

	public BattleAction setSortKey(int s) {
		this.sort = s;
		return this;
	}

	public BattleAction setWaitTime(FrameTimeCounter tc) {
		this.waitTime = tc;
		return this;
	}

	public BattleActionType getBattleActionType() {
		return type;
	}

	public List<BattleActionTargetType> getTargetTypeList() {
		return getEvents().stream().map(v -> v.getBatt()).collect(Collectors.toList());
	}

	public FrameTimeCounter createWaitTime() {
		return waitTime.clone();
	}

	public int getArea() {
		return area;
	}

	public String getDesc() {
		return desc;
	}

	private boolean sorted = false;

	public List<BattleActionEvent> getEvents() {
		if (!sorted) {
			Collections.sort(events);
			sorted = true;
		}
		return events;
	}

	public int getSpellTime() {
		return spellTime;
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean isFieldUse() {
		return fieldUse;
	}

	public Sound getSound() {
		return sound;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public boolean isStop() {
		return stop;
	}

	// userがこのアクションを実行できるか判定します
	public boolean canDoThis(Status user) {
		if (stop) {
			return false;
		}
		if (term.isEmpty()) {
			return true;
		}
		return term.stream().anyMatch(p -> p.canDoThis(user));
	}

	public int getAreaWithEqip(Status s) {
		int a = area;
		for (Item i : s.getEqipment().values()) {
			if (i != null) {
				a += i.getArea();
			}
		}
		return a;
	}

	public boolean isOnlyBatt(BattleActionTargetType t) {
		boolean result = true;
		for (BattleActionEvent e : getEvents()) {
			result &= e.getBatt() == t;
		}
		return result;
	}

	public boolean isOnlyBatpt(BattleActionTargetParameterType t) {
		boolean result = true;
		for (BattleActionEvent e : getEvents()) {
			result &= e.getBatpt() == t;
		}
		return result;
	}

	public boolean isMoveOnly() {
		boolean result = true;
		for (BattleActionEvent e : getEvents()) {
			result &= e.getBatpt() == BattleActionTargetParameterType.MOVE;
		}
		return result;
	}

	public List<BattleActionResult> exec(GameSystem gs, BattleCharacter user) {
		return exec(gs, user, Collections.emptyList());
	}

	public List<BattleActionResult> exec(GameSystem gs, BattleCharacter user, List<BattleCharacter> selectedTarget) {
		List<BattleActionResult> result = new ArrayList<>();
		if (stop) {
			if (GameSystem.isDebugMode()) {
				System.out.println("行動は停止されているため実行されなかった：" + user.getStatus().getName() + " / " + name);
			}
			result.add(BattleActionResult.STOPED);
			return result;
		}
		if (sound != null) {
			sound.load().stopAndPlay();
		}
		//SELFでターゲットが入っている場合、例外
		if (isOnlyBatt(BattleActionTargetType.SELF) && (selectedTarget != null || selectedTarget.size() != 0)) {
			throw new GameSystemException("this actin is SELF, but target is exsist:" + getName());
		}
		for (BattleActionEvent e : getEvents()) {
			if (!Random.percent(e.getBaseP())) {
				if (GameSystem.isDebugMode()) {
					System.out.println("行動は失敗した(P)：" + user.getStatus().getName() + " / " + name);
				}
				result.add(BattleActionResult.MISS);
				continue;
			}
			List<BattleCharacter> tgt = new ArrayList<>();
			BattleActionTargetType batt;
			switch (batt = e.getBatt()) {
				case FIELD:
					BattleActionTargetParameterType parameterType = e.getBatpt();
					if (parameterType != BattleActionTargetParameterType.ADD_CONDITION && parameterType != BattleActionTargetParameterType.REMOVE_CONDITION) {
						throw new GameSystemException("batt=FIELD, but batpt is not ADD_CONDITION or REMOVE_CONDITION");
					}
					String tgtName = e.getTargetName();
					if (tgtName == null) {
						throw new GameSystemException("batt=FIELD batpt=ADD_CONDITION or REMOVE_CONDITION, but tgtName is null");
					}
					if (parameterType == BattleActionTargetParameterType.ADD_CONDITION) {
						gs.getBattleSystem().getBattleFieldSystem().addCondition(ConditionValueStorage.getInstance().get(tgtName).getKey());
					} else {
						gs.getBattleSystem().getBattleFieldSystem().removeCondition(tgtName);
					}
					result.add(BattleActionResult.ADD_CONDITION_FIELD);
					break;
				case SELF:
					tgt.add(user);
					break;
				case ALL:
				case TEAM_PARTY:
				case TEAM_ENEMY:
					tgt.addAll(selectedTarget);
					break;
				case ONE_ENEMY:
				case ONE_PARTY:
				case RANDOM_ONE:
				case RANDOM_ONE_ENEMY:
				case RANDOM_ONE_PARTY:
					if (selectedTarget.size() != 1) {
						throw new GameSystemException("this event target is 1, but target size is not 1:" + name + " / " + selectedTarget);
					}
					tgt.addAll(selectedTarget);
					break;
				default:
					throw new AssertionError();
			}
			BattleActionTargetParameterType batpt;
			switch (batpt = e.getBatpt()) {
				case ADD_CONDITION:
					String tgtName = e.getTargetName();
					if (tgtName == null) {
						throw new GameSystemException("batt=" + batt + " batpt=ADD_CONDITION, but tgtName is null：" + user.getStatus().getName() + " / " + name);
					}
					gs.getBattleSystem().getBattleFieldSystem().addCondition(ConditionValueStorage.getInstance().get(tgtName).getKey());
					result.add(BattleActionResult.ADD_CONDITION_TGT);
					break;
				case REMOVE_CONDITION:
					String tgtName2 = e.getTargetName();
					if (tgtName2 == null) {
						throw new GameSystemException("batt=" + batt + " batpt=REMOVE_CONDITION, but tgtName is null：" + user.getStatus().getName() + " / " + name);
					}
					gs.getBattleSystem().getBattleFieldSystem().removeCondition(tgtName2);
					result.add(BattleActionResult.REMOVE_CONDITION_TGT);
					break;
				case ATTR_IN:
					float value = e.getValue();
					String tgtAttrName = e.getTargetName();
					if (tgtAttrName == null) {
						throw new GameSystemException("ATTR_IN, but tgtName is null：" + user.getStatus().getName() + " / " + name);
					}
					switch (e.getDamageCalcType()) {
						case DIRECT:
							for (BattleCharacter t : tgt) {
								t.getStatus().getBaseAttrIn().get(tgtAttrName).set(value);
							}
							break;
						case PERCENT_OF_MAX:
							for (BattleCharacter t : tgt) {
								value *= t.getStatus().getBaseAttrIn().get(tgtAttrName).getMax();
								t.getStatus().getBaseAttrIn().get(tgtAttrName).set(value);
							}
							break;
						case PERCENT_OF_NOW:
							for (BattleCharacter t : tgt) {
								value *= t.getStatus().getBaseAttrIn().get(tgtAttrName).getValue();
								t.getStatus().getBaseAttrIn().get(tgtAttrName).set(value);
							}
							break;
						default:
							throw new AssertionError();
					}
					result.add(BattleActionResult.ATTR_IN);
					break;
				case ITEM_LOST:
					String tgtItemName = e.getTargetName();
					if (tgtItemName == null) {
						throw new GameSystemException("ITEM_LOST, but tgtName is null：" + user.getStatus().getName() + " / " + name);
					}
					for (BattleCharacter s : tgt) {
						s.getStatus().getItemBag().drop(tgtItemName);
					}
					result.add(BattleActionResult.ITEM_LOST);
					break;
				case MOVE:
					//MOVEイベントは特に何もしない（移動はプレイヤーの入力によるため
					result.add(BattleActionResult.MOVE);
					break;
				case NONE:
					//NONEイベントは特に何もしない
					result.add(BattleActionResult.NONE);
					break;
				case USE_ITEM:
					String tgtItemName2 = e.getTargetName();
					Item item = ItemStorage.getInstance().get(tgtItemName2);
					if (!user.getStatus().getItemBag().contains(item)) {
						throw new GameSystemException(name + " use " + item + " but dont have it.");
					}
					for (ItemAction a : item.getBattleAction()) {
						a.exec(user, selectedTarget);
					}
					result.add(BattleActionResult.USE_ITEM);
					break;
				case STATUS:
					String tgtStatusName = e.getTargetName();
					if (tgtStatusName == null) {
						throw new GameSystemException("STATUS, but tgtName is null：" + user.getStatus().getName() + " / " + name);
					}
					StatusDamageCalcType calcType = e.getDamageCalcType();
					if (calcType == null) {
						throw new GameSystemException("STATUS, but StatusDamageCalcType(dct) is null：" + user.getStatus().getName() + " / " + name);
					}
					AttributeKey attrKey = e.getAtkAttr();
					if (attrKey == null) {
						throw new GameSystemException("STATUS, but AttributeKey is null：" + user.getStatus().getName() + " / " + name);
					}
					StatusDamageCalcModelStorage.getInstance().getCurrent().exec(gs, user, this, e, calcType, attrKey, tgtStatusName, tgt);
					result.add(BattleActionResult.SUCCESS);
					break;
				default:
					throw new AssertionError();
			}
		}
		assert getEvents().size() == result.size() : "battle action event result size is mismatch";
		return result;
	}

	public List<BattleActionEventTerm> getTerm() {
		return term;
	}

	// このアクションを発動した際の、指定のキーのステータスのSELF増減を計算します。＋または-です。SELFのみ対象です。
	public int calcSelfStatusDamage(Status self, String name) {
		int r = 0;
		for (BattleActionEvent e : getEvents()) {
			if (e.getBatt() == BattleActionTargetType.SELF) {
				if (e.getBatpt() == BattleActionTargetParameterType.STATUS) {
					if (name.equals(e.getTargetName())) {
						switch (e.getDamageCalcType()) {
							case DIRECT:
								r += e.getValue();
								break;
							case PERCENT_OF_MAX:
								float max = StatusKeyStorage.getInstance().get(name).getMax();
								r += (max * e.getValue());
								break;
							case PERCENT_OF_NOW:
								float current = self.getEffectedStatus().get(name).getValue();
								r += (current * e.getValue());
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

	@Override
	public String toString() {
		return "BattleAction{" + "type=" + type + ", name=" + name + '}';
	}

	@Override
	public int compareTo(BattleAction o) {
		return sort - o.sort;
	}

}
