/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the user実行条件 of the GNU General Public License as published by
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kinugasa.game.I18N;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.util.Random;
import kinugasa.util.StringUtil;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_14:19:45<br>
 * @author Shinacho<br>
 */
public class ActionEvent implements Nameable, Comparable<ActionEvent> {

	public enum Event起動条件 {
		条件なしで必ず起動("条件なしで必ず起動"),
		前段がないか前段イベント全成功時のみ起動("前段がないか前段イベント全成功時のみ起動"),
		前段がないか前段イベント全失敗時のみ起動("前段がないか前段イベント全失敗時のみ起動"),
		前段がないか直前のイベント成功時のみ起動("前段がないか直前のイベント成功時のみ起動"),
		前段がないか直前のイベント失敗時のみ起動("前段がないか直前のイベント失敗時のみ起動"),
		前段がないか最初のイベントが成功時のみ起動("前段がないか最初のイベントが成功時のみ起動"),
		前段がないか最初のイベントが失敗時のみ起動("前段がないか最初のイベントが失敗時のみ起動"),;
		private String i18nKey;

		private Event起動条件(String i18nKey) {
			this.i18nKey = i18nKey;
		}

		public String getI18nKey() {
			return i18nKey;
		}

		public String getTextI18Nd(String idx) {
			return I18N.get(i18nKey, idx);
		}
	}

	public static class Actor起動条件 implements Nameable {

		public static enum Type {
			指定の状態異常を持っている,
			指定の状態異常を持っていない,
			指定の武器タイプの武器を装備している,
			武器を装備していない,
			指定のアイテムを持っている,
			指定のアイテムのいずれかを持っている,
			指定の名前のアイテムを持っている,
			指定のステータスの現在値が指定の割合以上,
			指定のステータスの現在値が指定の値以上,
			ACTORのIDが一致,
		}
		public final String id;
		public final Type type;
		public final float value;
		public final String tgtName;

		public Actor起動条件(String id, Type type, float value, String tgtName) {
			this.id = id;
			this.type = type;
			this.value = value;
			this.tgtName = tgtName;
		}

		public String getTextI18Nd() {
			switch (type) {
				case 指定の状態異常を持っていない, 指定の状態異常を持っている -> {
					return I18N.get(type.toString(), ConditionKey.valueOf(tgtName).getVisibleName());
				}
				case ACTORのIDが一致, 指定のアイテムのいずれかを持っている, 指定の名前のアイテムを持っている, 指定のアイテムを持っている, 武器を装備していない -> {
					return I18N.get(type.toString());
				}
				case 指定の武器タイプの武器を装備している -> {
					return I18N.get(type.toString(), WeaponType.valueOf(tgtName).getVisibleName());
				}
				case 指定のステータスの現在値が指定の値以上 -> {
					if (StatusKey.valueOf(tgtName) == StatusKey.魔術使用可否) {
						if ((int) value == 1) {
							return I18N.get(GameSystemI18NKeys.魔術利用可能);
						} else {
							return I18N.get(GameSystemI18NKeys.魔術利用不可);
						}
					}
					return I18N.get(type.toString(), StatusKey.valueOf(tgtName).getVisibleName(), (int) value);
				}
				case 指定のステータスの現在値が指定の割合以上 -> {
					return I18N.get(type.toString(), StatusKey.valueOf(tgtName).getVisibleName(), (int) (value * 100f));
				}
				default ->
					throw new AssertionError("undefined term type : " + type);
			}
		}

		public boolean canDo(Status a) {
			switch (type) {
				case 指定の状態異常を持っている -> {
					return a.getCurrentConditions().containsKey(ConditionKey.valueOf(tgtName));
				}
				case 指定の状態異常を持っていない -> {
					return !a.getCurrentConditions().containsKey(ConditionKey.valueOf(tgtName));
				}
				case 武器を装備していない -> {
					boolean res = true;
					if (a.getEqip().get(EqipSlot.右手) != null) {
						res &= a.getEqip().get(EqipSlot.右手) == null;
					}
					if (a.getEqip().get(EqipSlot.左手) != null) {
						res &= a.getEqip().get(EqipSlot.左手) == null;
					}
					return res;
				}
				case 指定の武器タイプの武器を装備している -> {
					if (a.getEqip().get(EqipSlot.右手) != null) {
						if (a.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.valueOf(tgtName)) {
							return true;
						}
					}
					if (a.getEqip().get(EqipSlot.左手) != null) {
						if (a.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.valueOf(tgtName)) {
							return true;
						}
					}
					return false;
				}
				case 指定のアイテムを持っている -> {
					return a.getItemBag().contains(tgtName);
				}
				case 指定のアイテムのいずれかを持っている -> {
					String[] ids = tgtName.contains(",") ? tgtName.split(",") : new String[]{tgtName};
					for (String id : ids) {
						if (a.getItemBag().contains(id)) {
							return true;
						}
					}
					return false;
				}
				case 指定の名前のアイテムを持っている -> {
					return a.getItemBag().getItems().stream().anyMatch(p -> p.getVisibleName().contains(tgtName));
				}
				case 指定のステータスの現在値が指定の割合以上 -> {
					return a.getEffectedStatus().get(StatusKey.valueOf(tgtName)).get割合() >= value;
				}
				case 指定のステータスの現在値が指定の値以上 -> {
					return a.getEffectedStatus().get(StatusKey.valueOf(tgtName)).getValue() >= value;
				}
				case ACTORのIDが一致 -> {
					return tgtName.equals(a.getId());
				}
				default ->
					throw new AssertionError("undefined term type : " + type);
			}
		}

		@Deprecated
		@Override
		public String getName() {
			return id;
		}

		public String getId() {
			return id;
		}

	}

	public enum CalcMode {
		MUL,
		ADD,
		TO,
		TO_ZERO,
		TO_MAX,
		DC;

		public String getVisibleName() {
			return I18N.get(toString());
		}
	}

	// base
	private String id;
	private int sort;
	private String tgtId;
	private float value;
	//条件
	private float p;
	private ActionEventType type;
	private List<Actor起動条件> user保有条件 = new ArrayList<>();
	private List<Actor起動条件> tgt適用条件 = new ArrayList<>();
	private Event起動条件 j;
	//攻撃処理
	private StatusKey tgtStatusKey;
	private AttributeKey atkAttr;
	private CalcMode calcMode;
	//状態異常
	private ConditionKey tgtConditionKey;
	private int cndTime;
	//ATTR操作
	private AttributeKey tgtAttrKeyOut;
	private AttributeKey tgtAttrKeyIn;
	//CND_REGIST
	private ConditionKey tgtCndRegist;
	//アイテム
	private boolean noLimit;
	//Effect
	private Sound successSound;
	private AnimationSprite tgtAnimation, otherAnimation, userAnimation;
	private int waitTime = 1;

	public ActionEvent(String id) {
		this.id = id;
	}

	protected final boolean 起動条件判定＿起動OK(Actor tgt, ActionResult ar, boolean isUserEvent) {
		//起動条件判定
		switch (j) {
			case 条件なしで必ず起動: {
				return true;
			}
			case 前段がないか前段イベント全成功時のみ起動: {
				if (isUserEvent) {
					if (ar.getUserEventResultAsList().isEmpty()
							|| ar.getUserEventResultAsList().stream().allMatch(p -> p.summary.is成功())) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿起動条件未達, tgt));
						return false;
					}
				} else {
					if (ar.getMainEventResult().isEmpty()
							|| ar.getMainEventResultAsList().stream().allMatch(p -> p.summary.is成功())) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿起動条件未達, Map.of()));
						return false;
					}
				}
			}
			case 前段がないか前段イベント全失敗時のみ起動: {
				if (isUserEvent) {
					if (ar.getUserEventResultAsList().isEmpty()
							|| ar.getUserEventResultAsList().stream().allMatch(p -> p.summary.is失敗())) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿起動条件未達, tgt));
						return false;

					}
				} else {
					if (ar.getMainEventResult().isEmpty()
							|| ar.getMainEventResultAsList().stream().allMatch(p -> p.summary.is失敗())) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿起動条件未達, Map.of()));
						return false;

					}
				}
			}
			case 前段がないか直前のイベント成功時のみ起動: {
				if (isUserEvent) {
					if (ar.getUserEventResultAsList().isEmpty()
							|| ar.getLastUserEventResult().summary.is成功()) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿起動条件未達, tgt));
						return false;

					}
				} else {
					if (ar.getMainEventResult().isEmpty()
							|| ar.getLastMainEventResult().summary.is成功()) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿起動条件未達, Map.of()));
						return false;

					}
				}
			}
			case 前段がないか直前のイベント失敗時のみ起動: {
				if (isUserEvent) {
					if (ar.getUserEventResultAsList().isEmpty()
							|| ar.getLastUserEventResult().summary.is失敗()) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿起動条件未達, tgt));
						return false;

					}
				} else {
					if (ar.getMainEventResult().isEmpty()
							|| ar.getLastMainEventResult().summary.is失敗()) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿起動条件未達, Map.of()));
						return false;

					}
				}
			}
			case 前段がないか最初のイベントが成功時のみ起動: {
				if (isUserEvent) {
					if (ar.getUserEventResultAsList().isEmpty()
							|| ar.getFirstUserEventResult().summary.is成功()) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿起動条件未達, tgt));
						return false;

					}
				} else {
					if (ar.getMainEventResult().isEmpty()
							|| ar.getFirstMainEventResult().summary.is成功()) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿起動条件未達, Map.of()));
						return false;

					}
				}
			}
			case 前段がないか最初のイベントが失敗時のみ起動: {
				if (isUserEvent) {
					if (ar.getUserEventResultAsList().isEmpty()
							|| ar.getFirstUserEventResult().summary.is失敗()) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿起動条件未達, tgt));
						return false;

					}
				} else {
					if (ar.getMainEventResult().isEmpty()
							|| ar.getFirstMainEventResult().summary.is失敗()) {
						//起動可能
						return true;
					} else {
						//起動不可
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿起動条件未達, Map.of()));
						return false;

					}
				}
			}
			default:
				throw new AssertionError("undefined trigger option");
		}
	}

	public void exec(ActionTarget tgt, final ActionResult ar, boolean isUserEvent) {
		if (isUserEvent) {
			this.exec(tgt.getUser(), tgt.getAction(), tgt.getUser(), ar, true);
		} else {
			for (var v : tgt.getTgt()) {
				this.exec(tgt.getUser(), tgt.getAction(), v, ar, false);
			}
		}
	}

	//Actorごとに呼び出される。このアクションイベントを実行してイベントリザルトを戻す
	public void exec(Actor user, Action a, Actor tgt, final ActionResult ar, boolean isUserEvent) {
		if (!起動条件判定＿起動OK(tgt, ar, isUserEvent)) {
			if (isUserEvent) {
				ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿起動条件未達, tgt));
			} else {
				ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿起動条件未達, Map.of()));
			}
			return;
		}

		if (tgt.getStatus().hasCondition(ConditionKey.解脱)) {
			if (a.getDeadTgt() != Action.死亡者ターゲティング.気絶損壊解脱者を選択可能
					&& a.getDeadTgt() != Action.死亡者ターゲティング.解脱者を選択可能) {
				ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿不発, tgt));
				return;
			}
		}
		if (tgt.getStatus().hasCondition(ConditionKey.損壊)) {
			if (a.getDeadTgt() != Action.死亡者ターゲティング.気絶損壊解脱者を選択可能
					&& a.getDeadTgt() != Action.死亡者ターゲティング.損壊者を選択可能) {
				ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿不発, tgt));
				return;
			}
		}
		if (tgt.getStatus().hasCondition(ConditionKey.気絶)) {
			if (a.getDeadTgt() != Action.死亡者ターゲティング.気絶損壊解脱者を選択可能
					&& a.getDeadTgt() != Action.死亡者ターゲティング.気絶者を選択可能) {
				ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿不発, tgt));
				return;
			}
		}
		if (!user保有条件.stream().allMatch(p -> p.canDo(user.getStatus()))) {
			ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
			return;
		}
		if (!isUserEvent && !tgt適用条件.stream().allMatch(p -> p.canDo(tgt.getStatus()))) {
			ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
			return;
		}
		if (!Random.percent(p)) {
			if (isUserEvent) {
				ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿不発, tgt));
			} else {
				ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
			}
			return;
		}
		type.exec(user, a, isUserEvent ? user : tgt, this, ar, isUserEvent);
		return;
	}

	ActionEvent setEvent起動条件(Event起動条件 j) {
		this.j = j;
		return this;
	}

	public Event起動条件 getEvent起動条件() {
		return j;
	}

	//このイベントの情報を返す
	public String getPageDescI18Nd() {
		return type.getPageDescI18Nd(this);
	}

	public String getEventDescI18Nd(int thisIdx) {
		return type.getEventDescI18Nd(this, thisIdx);
	}

	ActionEvent setWaitTime(int waitTime) {
		this.waitTime = waitTime;
		return this;
	}

	public int getWaitTime() {
		return waitTime;
	}

	ActionEvent setUserAnimation(AnimationSprite userAnimation) {
		this.userAnimation = userAnimation;
		return this;
	}

	public AnimationSprite getUserAnimation() {
		return userAnimation;
	}

	ActionEvent setEventType(ActionEventType type) {
		this.type = type;
		return this;
	}

	ActionEvent setAtkAttr(AttributeKey atkAttr) {
		this.atkAttr = atkAttr;
		return this;
	}

	ActionEvent setTgtStatusKey(StatusKey tgtStatusKey) {
		this.tgtStatusKey = tgtStatusKey;
		return this;
	}

	ActionEvent setP(float p) {
		this.p = p;
		return this;
	}

	ActionEvent setTgtID(String tgtID) {
		this.tgtId = tgtID;
		return this;
	}

	ActionEvent setTgtConditionKey(ConditionKey tgtConditionKey) {
		this.tgtConditionKey = tgtConditionKey;
		return this;
	}

	ActionEvent setTgtAttrKeyOut(AttributeKey tgtAttrKeyOut) {
		this.tgtAttrKeyOut = tgtAttrKeyOut;
		return this;
	}

	ActionEvent setTgtAttrKeyin(AttributeKey tgtAttrKeyin) {
		this.tgtAttrKeyIn = tgtAttrKeyin;
		return this;
	}

	ActionEvent setValue(float value) {
		this.value = value;
		return this;
	}

	ActionEvent setCalcMode(CalcMode calcMode) {
		this.calcMode = calcMode;
		return this;
	}

	ActionEvent setCndTime(int time) {
		this.cndTime = time;
		return this;
	}

	ActionEvent setSuccessSound(Sound successSound) {
		this.successSound = successSound;
		return this;
	}

	ActionEvent setTgtAnimation(AnimationSprite tgtAnimation) {
		this.tgtAnimation = tgtAnimation;
		return this;
	}

	ActionEvent setOtherAnimation(AnimationSprite otherAnimation) {
		this.otherAnimation = otherAnimation;
		return this;
	}

	ActionEvent setNoLimit(boolean s) {
		this.noLimit = s;
		return this;
	}

	ActionEvent setSort(int sort) {
		this.sort = sort;
		return this;
	}

	ActionEvent setCndRegist(ConditionKey r) {
		this.tgtCndRegist = r;
		return this;
	}

	public String getId() {
		return id;
	}

	public int getCndTime() {
		return cndTime;
	}

	public ConditionKey getTgtCndRegistKey() {
		return tgtCndRegist;
	}

	public ActionEventType getEventType() {
		return type;
	}

	public boolean isNoLimit() {
		return noLimit;
	}

	public AttributeKey getAtkAttr() {
		return atkAttr;
	}

	public String getTgtID() {
		return tgtId;
	}

	public Item getTgtAsItem() {
		return ActionStorage.getInstance().itemOf(tgtId);
	}

	public Material getTgtAsMaterial() {
		return MaterialStorage.getInstance().select(tgtId);
	}

	public String getTgtAsMsgI18Nd() {
		return I18N.get(tgtId);
	}

	public EqipSlot getTgtAsSlot() {
		return EqipSlot.valueOf(tgtId);
	}

	public List<Item> getTgtAsItemList() {
		return ActionStorage.getInstance().itemOf(StringUtil.safeSplit(tgtId, ","));
	}

	public Actor getTgtAsActor() {
		return new Actor(tgtId);
	}

	public Action getTgtAsAction() {
		return ActionStorage.getInstance().get(tgtId);
	}

	public Difficulty getTgtAsDifficulty() {
		return Difficulty.valueOf(tgtId);
	}

	public List<Actor起動条件> getUser起動条件() {
		return user保有条件;
	}

	ActionEvent setUser起動条件(List<Actor起動条件> user保有条件) {
		this.user保有条件 = user保有条件;
		return this;
	}

	public List<Actor起動条件> getTgt起動条件() {
		return tgt適用条件;
	}

	ActionEvent setTgt起動条件(List<Actor起動条件> tgt適用条件) {
		this.tgt適用条件 = tgt適用条件;
		return this;
	}

	public StatusKey getTgtStatusKey() {
		return tgtStatusKey;
	}

	public float getP() {
		return p;
	}

	public ConditionKey getTgtConditionKey() {
		return tgtConditionKey;
	}

	public AttributeKey getTgtAttrOut() {
		return tgtAttrKeyOut;
	}

	public AttributeKey getTgtAttrIn() {
		return tgtAttrKeyIn;
	}

	public float getValue() {
		return value;
	}

	public CalcMode getCalcMode() {
		return calcMode;
	}

	public Sound getSuccessSound() {
		return successSound;
	}

	public AnimationSprite getTgtAnimation() {
		return tgtAnimation;
	}

	public AnimationSprite getOtherAnimation() {
		return otherAnimation;
	}

	public int getSort() {
		return sort;
	}

	@Deprecated
	@Override
	public String getName() {
		return id;
	}

	@Override
	public String toString() {
		return "ActionEvent{" + "id=" + id + ", type=" + type + '}';
	}

	@Override
	public int compareTo(ActionEvent o) {
		return sort - o.sort;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ActionEvent other = (ActionEvent) obj;
		return Objects.equals(this.id, other.id);
	}

}
