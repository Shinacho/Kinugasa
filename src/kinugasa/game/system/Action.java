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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kinugasa.game.GameLog;
import kinugasa.game.I18N;
import kinugasa.game.NoLoopCall;
import kinugasa.game.Nullable;
import static kinugasa.game.system.ActionEvent.CalcMode.ADD;
import static kinugasa.game.system.ActionEvent.CalcMode.MUL;
import static kinugasa.game.system.ActionEvent.CalcMode.TO;
import static kinugasa.game.system.ActionEvent.CalcMode.TO_MAX;
import static kinugasa.game.system.ActionEvent.CalcMode.TO_ZERO;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Nameable;

/**
 * イベントの数が攻撃回数です。
 *
 * @vesion 1.0.0 - 2023/10/14_11:41:31<br>
 * @author Shinacho<br>
 */
public class Action implements Nameable, Comparable<Action>, Cloneable {

	public enum ターゲットモード {
		自身のみ,
		単体_敵のみ,
		単体_味方のみ_自身含む,
		単体_味方のみ_自身含まない,
		単体_切替可能_自身含む_初期選択敵,
		単体_切替可能_自身含まない_初期選択敵,
		単体_切替可能_自身含む_初期選択味方,
		単体_切替可能_自身含まない_初期選択味方,
		グループ_味方全員,
		グループ_敵全員,
		グループ_切替可能_初期選択敵,
		グループ_切替可能_初期選択味方,
		全員,
		グループ_味方全員_自身除く,
		グループ_切替可能_初期選択敵_自身除く,
		グループ_切替可能_初期選択味方_自身除く,
		全員_自身除く,;

		public String getVisibleName() {
			return I18N.get(this.toString());
		}

		public boolean isチーム切替可能() {
			return this.toString().contains("切替可能");
		}

	}

	//死亡状態の対象を選択できるかどうか
	public enum 死亡者ターゲティング {
		気絶損壊解脱者を選択可能,
		気絶損壊解脱者は選択不可能,
		解脱者を選択可能,
		損壊者を選択可能,
		気絶者を選択可能,;

		public String getVisibleName() {
			return I18N.get(this.toString());
		}
	}
	private final String id;
	private final String visibleName;
	private String desc, summary;
	private ActionType type;
	private List<ActionEvent> mainEvents = new ArrayList<>();
	private List<ActionEvent> userEvents = new ArrayList<>();
	private boolean field, battle;
	private int area, castTime;
	private ターゲットモード tgtType;
	private 死亡者ターゲティング deadTgt;

	Action(String id, String visibleName, ActionType typ) {
		this.id = id;
		this.visibleName = visibleName;
		this.type = typ;
	}

	public List<ActionEvent.Actor保有条件> getAllTerms() {
		return Stream.of(mainEvents, userEvents)
				.flatMap(p -> p.stream())
				.map(p -> p.getTerms())
				.flatMap(p -> p.stream())
				.distinct()
				.collect(Collectors.toList());
	}

	public Action pack() throws GameSystemException {
		//イベントソート
		Collections.sort(mainEvents);
		Collections.sort(userEvents);

		if (id == null || id.isEmpty()) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.IDが入ってません) + " : " + this);
		}
		if (visibleName == null || visibleName.isEmpty()) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.名前が入ってません) + " : " + this);
		}
		if (type == null) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.アクションタイプが入ってません) + " : " + this);
		}
		if ((type == ActionType.魔法 || type == ActionType.攻撃) && (mainEvents.isEmpty() && userEvents.isEmpty())) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.アクションタイプが攻撃または魔法ですがイベントが入ってません) + " : " + this);
		}
		//アクションの状態チェック
		if (tgtType == null && (!mainEvents.isEmpty() || !userEvents.isEmpty())) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.イベントがありますがターゲット選択情報が空です) + " : " + this);
		}
		if (desc == null || desc.isEmpty()) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.DESCが入っていません) + " : " + this);
		}
		if (area == 0 && (!mainEvents.isEmpty() || !userEvents.isEmpty()) && type != ActionType.アイテム) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.イベントがありますがAREAが０です) + " : " + this);
		}
		if (deadTgt == null && (!mainEvents.isEmpty() || !userEvents.isEmpty())) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.イベントがありますが死亡者ターゲット可否が空です) + " : " + this);
		}
		if ((!field && !battle) && (!mainEvents.isEmpty() || !userEvents.isEmpty())) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.イベントがありますがFIELDとBATTLEがOFFです) + " : " + this);
		}

		//イベントの必須項目チェック
		for (ActionEvent e : userEvents) {
			checkEvent(e);
		}
		for (ActionEvent e : mainEvents) {
			checkEvent(e);
		}
		if (GameSystem.isDebugMode()) {
			GameLog.print("action : " + this + " is loaded");
		}

		return this;
	}

	private void checkEvent(ActionEvent e) throws GameSystemException {

		if (e.getEventType() == null) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.イベントのタイプが空です) + " : " + this + " : " + e);
		}
		if (e.getP() <= 0) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.イベントの発生確率が０です) + " : " + this + " : " + e);
		}
		if (!e.getTerms().isEmpty()) {
			int size = e.getTerms().size();
			if (size != e.getTerms().stream().distinct().count()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.イベントTermが重複しています) + " : " + this + " : " + e);
			}
		}
		e.getEventType().pack(e, this);
	}

	@Nullable
	@NoLoopCall("its heavy")
	public WeaponType getWeaponType() {
		for (ActionEvent e : getAllEvents()) {
			for (ActionEvent.Actor保有条件 t : e.getTerms()) {
				if (t.type == ActionEvent.Actor保有条件.Type.指定の武器タイプの武器を装備している) {
					return WeaponType.valueOf(t.tgtName);
				}
			}
		}
		return null;
	}

	public int getArea() {
		return area;
	}

	Action setArea(int area) {
		this.area = area;
		return this;
	}

	Action setDesc(String desc) {
		this.desc = desc;
		return this;
	}

	Action setMainEvents(List<ActionEvent> mainEvents) {
		this.mainEvents = mainEvents;
		return this;
	}

	Action setUserEvents(List<ActionEvent> userEvents) {
		this.userEvents = userEvents;
		return this;
	}

	Action setField(boolean field) {
		this.field = field;
		return this;
	}

	Action setBattle(boolean battle) {
		this.battle = battle;
		return this;
	}

	Action set死亡者ターゲティング(死亡者ターゲティング a) {
		this.deadTgt = a;
		return this;
	}

	Action setCastTime(int castTime) {
		this.castTime = castTime;
		return this;
	}

	Action setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	@Deprecated
	@Override
	public final String getName() {
		return id;
	}

	public final String getId() {
		return id;
	}

	public String getSummary() {
		return I18N.get(summary);
	}

	public String getVisibleName() {
		return I18N.get(visibleName);
	}

	public final String getDesc() {
		return I18N.get(desc);
	}

	public int getCastTime() {
		return castTime;
	}

	public final ActionType getType() {
		return type;
	}

	public List<ActionEvent> getUserEvents() {
		return userEvents;
	}

	public List<ActionEvent> getMainEvents() {
		return mainEvents;
	}

	public boolean hasMainEvent() {
		return mainEvents != null && !mainEvents.isEmpty();
	}

	public boolean hasUserEvent() {
		return userEvents != null && !userEvents.isEmpty();
	}

	public 死亡者ターゲティング getDeadTgt() {
		return deadTgt;
	}

	public boolean isField() {
		return field;
	}

	public boolean isBattle() {
		return battle;
	}

	public boolean hasEvent() {
		if (mainEvents != null) {
			if (!mainEvents.isEmpty()) {
				return true;
			}
		}
		if (userEvents != null) {
			if (!userEvents.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public StatusValueSet simuleteSelfStatusDamage(Actor user) {
		StatusValueSet res = user.getStatus().getEffectedStatus().clone();
		if (userEvents == null || userEvents.isEmpty()) {
			return res;
		}
		for (ActionEvent e : userEvents) {
			if (e.getTgtStatusKey() == null) {
				continue;
			}
			StatusKey key = e.getTgtStatusKey();
			switch (e.getCalcMode()) {
				case ADD:
					res.get(key).add(e.getValue());
					break;
				case MUL:
					res.get(key).mul(e.getValue());
					break;
				case TO_MAX:
					res.get(key).toMax();
					break;
				case TO_ZERO:
					res.get(key).toZero();
					break;
				case TO:
					res.get(key).setValue(e.getValue());
					break;
				default:
					throw new AssertionError("undefined calc mode : Action.java");
			}
		}
		return res;
	}

	public static class ResourceShortage {

		public Set<StatusKey> keys;

		public ResourceShortage(Set<StatusKey> keys) {
			this.keys = keys;
		}

		public boolean is足りないステータスあり() {
			return !keys.isEmpty();
		}

		@Override
		public String toString() {
			return "ResourceShortage{" + "keys=" + keys + '}';
		}

	}

	Action setTgtType(ターゲットモード tgtType) {
		this.tgtType = tgtType;
		return this;
	}

	public ターゲットモード getTgtType() {
		return tgtType;
	}

	public List<ActionEvent> getAllEvents() {
		return Stream.of(mainEvents, userEvents)
				.flatMap(p -> p.stream())
				.collect(Collectors.toList());
	}

	@Nullable
	public ResourceShortage checkResource(Status user) {
		if (userEvents == null || userEvents.isEmpty()) {
			return new ResourceShortage(Set.of());
		}
		StatusValueSet res = user.getEffectedStatus().clone();
		Set<StatusKey> keys = new HashSet<>();
		for (ActionEvent e : userEvents) {
			if (e.getTgtStatusKey() == null) {
				continue;
			}
			StatusKey key = e.getTgtStatusKey();
			switch (e.getCalcMode()) {
				case ADD:
					res.get(key).add(e.getValue(), false);
					if (res.get(key).isMinus()) {
						keys.add(key);
					}
					break;
				case MUL:
					res.get(key).mul(e.getValue());
					if (res.get(key).isMinus()) {
						keys.add(key);
					}
					break;
				case TO_MAX:
					res.get(key).toMax();
					break;
				case TO_ZERO:
					res.get(key).toZero();
					break;
				case TO:
					res.get(key).setValue(e.getValue());
					if (res.get(key).isMinus()) {
						keys.add(key);
					}
					break;
				default:
					throw new AssertionError("undefined calc mode : Action.java");
			}
		}
		return new ResourceShortage(keys);
	}

	public boolean canDo(Status a) {
		if (!userEvents.isEmpty()) {
			if (!checkResource(a).keys.isEmpty()) {
				return false;
			}
		}
		if (userEvents.isEmpty()) {
			return true;
		}
		return userEvents.stream()
				.map(p -> p.getTerms())
				.flatMap(p -> p.stream())
				.allMatch(p -> p.canDo(a));
	}

	public ActionResult exec(ActionTarget tgt) {
		//使えないアイテムの場合例外とするので、使う側で制御のこと
		if (!hasMainEvent() && !hasUserEvent()) {
			ActionResult r = new ActionResult(this, tgt, ActionResultSummary.失敗＿このアクションにはイベントがない);
			GameLog.print(r);
			return r;
		}
		if (tgt.isInField() && !field) {
			throw new GameSystemException("tgt is in field, but this ac tion is not field :  " + this);
		}
		if (tgt.isInField() && GameSystem.getInstance().getMode() == GameMode.BATTLE) {
			throw new GameSystemException("field-battle mode missmatch :  " + this);
		}
		if (!tgt.isInField() && GameSystem.getInstance().getMode() == GameMode.FIELD) {
			throw new GameSystemException("field-battle mode missmatch :  " + this);
		}

		if (!canDo(tgt.getUser().getStatus())) {
			ActionResult r = new ActionResult(this, tgt, ActionResultSummary.失敗＿リソースが足りない);
			GameLog.print(r);
			return r;
		}

		ActionResult r = new ActionResult(this, tgt);
		for (ActionEvent e : userEvents) {
			e.exec(tgt.getUser(), this, tgt.getUser(), r, true);
		}
		ActionResult.DamageDesc userDamage = r.getUserDamage();
		if (userDamage.is損壊 || userDamage.is気絶 || userDamage.is解脱) {
			r = new ActionResult(this, tgt, ActionResultSummary.失敗＿術者死亡);
			GameLog.print(r);
			return r;
		}

		for (ActionEvent e : mainEvents) {
			for (Actor a : tgt.getTgt()) {
				//eが攻撃の場合はATKカウント回実施、そうでなければ1回実施
				if (e.getEventType().isATKCOUNT回数実行するイベント()) {
					for (int i = 0; i < tgt.getUser().getStatus().getEffectedAtkCount(); i++) {
						e.exec(tgt.getUser(), this, a, r, false);
					}
				} else {
					e.exec(tgt.getUser(), this, a, r, false);
				}
			}
		}
		GameLog.print(r);
		return r;
	}

	@Override
	public int compareTo(Action o) {
		return id.compareTo(o.id);
	}

	@Override
	public String toString() {
		return "Action{" + "id=" + id + ", visibleName=" + visibleName + '}';
	}

	@Override
	protected Action clone() {
		try {
			return (Action) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
