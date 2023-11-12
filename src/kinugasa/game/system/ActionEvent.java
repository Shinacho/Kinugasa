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

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import kinugasa.game.I18N;
import kinugasa.game.PlayerConstants;
import kinugasa.game.field4.FieldMap;
import kinugasa.game.field4.FieldMapStorage;
import kinugasa.game.field4.Node;
import static kinugasa.game.system.ActionType.攻撃;
import static kinugasa.game.system.ActionType.魔法;
import kinugasa.game.ui.Dialog;
import kinugasa.game.ui.DialogIcon;
import kinugasa.game.ui.DialogOption;
import kinugasa.object.AnimationSprite;
import kinugasa.object.FourDirection;
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
	private AnimationSprite tgtAnimation, otherAnimation;

	public ActionEvent(String id) {
		this.id = id;
	}

	//Actorごとに呼び出される。このアクションイベントを実行してイベントリザルトを戻す
	public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt) {
		if (!Random.percent(p)) {
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, this);
		}
		tgt.getStatus().saveBeforeDamageCalc();

		//タイプが前のイベントの成功を参照かつ前のイベントが失敗している場合はこのイベントを失敗させる
		//このイベントが初回の場合は動く。
		if (!resOfThisTgt.isEmpty()) {
			if (type.is前のイベントが成功したときだけ実施するイベント() && resOfThisTgt.stream().map(p -> p.summary).anyMatch(p -> p.is失敗())) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, this);
			}
		}
		
		return type.exec(user, a, tgt, resOfThisTgt, this);

	}


	//このイベントの情報を返す
	public String getDescI18Nd() {
		StringBuilder sb = new StringBuilder();
		sb.append(getEventType().getVisibleName()).append(":").append(type.getVisibleName());
		sb.append(type.getEventDescI18Nd(this));

		return sb.toString();
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

}
