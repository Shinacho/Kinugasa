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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kinugasa.game.I18N;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_14:19:45<br>
 * @author Shinacho<br>
 */
public class ActionEvent implements Nameable, Comparable<ActionEvent> {

	public enum 起動条件 {
		条件なしで必ず起動,
		前段がないか前段イベント全成功時のみ起動,
		前段がないか前段イベント全失敗時のみ起動,
		前段がないか直前のイベント成功時のみ起動,
		前段がないか直前のイベント失敗時のみ起動,
		前段がないか最初のイベントが成功時のみ起動,
		前段がないか最初のイベントが失敗時のみ起動,
	}

	public static class Term implements Nameable {

		public static enum Type {
			指定の状態異常を持っている,
			指定の状態異常を持っていない,
			指定の武器タイプの武器を装備している,
			指定のアイテムを持っている,
			指定のアイテムのいずれかを持っている,
			指定の名前のアイテムを持っている,
			指定のステータスの現在値が指定の割合以上,
			ACTORのIDが一致,
		}
		public final String id;
		public final Type type;
		public final float value;
		public final String tgtName;

		public Term(String id, Type type, float value, String tgtName) {
			this.id = id;
			this.type = type;
			this.value = value;
			this.tgtName = tgtName;
		}

		public boolean canDo(Status a) {
			switch (type) {
				case 指定の状態異常を持っている: {
					return a.getCurrentConditions().containsKey(ConditionKey.valueOf(tgtName));
				}
				case 指定の状態異常を持っていない: {
					return !a.getCurrentConditions().containsKey(ConditionKey.valueOf(tgtName));
				}
				case 指定の武器タイプの武器を装備している: {
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
				case 指定のアイテムを持っている: {
					return a.getItemBag().contains(tgtName);
				}
				case 指定のアイテムのいずれかを持っている: {
					String[] ids = tgtName.contains(",") ? tgtName.split(",") : new String[]{tgtName};
					for (String id : ids) {
						if (a.getItemBag().contains(id)) {
							return true;
						}
					}
					return false;
				}
				case 指定の名前のアイテムを持っている: {
					return a.getItemBag().getItems().stream().anyMatch(p -> p.getVisibleName().contains(tgtName));
				}
				case 指定のステータスの現在値が指定の割合以上: {
					return a.getEffectedStatus().get(StatusKey.valueOf(tgtName)).get割合() >= value;
				}
				case ACTORのIDが一致: {
					return tgtName.equals(a.getId());
				}
				default:
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

	private String id;
	private int sort;
	private ActionEventType type;
	private List<Term> terms = new ArrayList<>();
	private StatusKey tgtStatusKey;
	private float p;
	private ConditionKey tgtConditionKey;
	private int cndTime;
	private AttributeKey atkAttr;
	private AttributeKey tgtAttrKeyOut;
	private AttributeKey tgtAttrKeyIn;
	private ConditionKey tgtCndRegist;
	private String tgtId;
	private boolean noLimit;
	private float value;
	private CalcMode calcMode;
	private Sound successSound;
	private AnimationSprite tgtAnimation, otherAnimation, userAnimation;
	private 起動条件 j;
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
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
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
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
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
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
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
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
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
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
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
						ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
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
		if (!Random.percent(p)) {
			if (isUserEvent) {
				ar.addUserEventResult(new ActionResult.UserEventResult(this, ActionResultSummary.失敗＿不発, tgt));
			} else {
				ar.setPerEvent(new ActionResult.PerEvent(this, ActionResultSummary.失敗＿不発, Map.of()));
			}
			return;
		}
		tgt.getStatus().saveBeforeDamageCalc();//2回実行しても別に問題はないのでここでかけておく

		type.exec(user, a, isUserEvent ? user : tgt, this, ar, isUserEvent);

	}

	public void set起動条件(起動条件 j) {
		this.j = j;
	}

	public 起動条件 get起動条件() {
		return j;
	}

	//このイベントの情報を返す
	public String getDescI18Nd() {
		StringBuilder sb = new StringBuilder();
		sb.append(getEventType().getVisibleName()).append(":").append(type.getVisibleName());
		sb.append(type.getEventDescI18Nd(this));

		return sb.toString();
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

	ActionEvent setTerms(List<Term> terms) {
		this.terms = terms;
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

	public ConditionKey getTgtCndRegist() {
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

	public List<Term> getTerms() {
		return terms;
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
