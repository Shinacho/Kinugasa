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
 * You should have received action copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.NewInstance;
import kinugasa.game.NotNewInstance;
import kinugasa.object.AnimationSprite;

/**
 * アクションに対する結果を定義するクラスです
 *
 * @vesion 1.0.0 - 2023/10/14_22:12:36<br>
 * @author Shinacho<br>
 */
public class ActionResult {

	private final Action action;
	private final ActionTarget tgt;
	private final Actor user;
	private final ActionResultSummary イベント未起動時の失敗理由;
	private final LinkedHashMap<ActionEvent, UserEventResult> userEventResult;
	private final LinkedHashMap<ActionEvent, PerEvent> mainEventResult;
	private AnimationSprite userAnimation;

	public static class EventActorResult {

		public final Actor tgt;
		public final ActionEvent event;
		public int tgtDamageHp, tgtDamageMp, tgtDamageSAN;
		public boolean is解脱 = false;
		public boolean is気絶 = false;
		public boolean is損壊 = false;
		public AnimationSprite tgtAnimation, otherAnimation;
		public String msgI18Nd;
		public final List<EventActorResult> 派生イベントの結果リスト = new ArrayList<>();

		public EventActorResult(Actor tgt, ActionEvent event) {
			this.tgt = tgt;
			this.event = event;
		}

		@Override
		public String toString() {
			return "SingleResult{" + "tgt=" + tgt + ", event=" + event + ", tgtDamageHp=" + tgtDamageHp + ", tgtDamageMp=" + tgtDamageMp + ", tgtDamageSAN=" + tgtDamageSAN + ", is\u89e3\u8131=" + is解脱 + ", is\u6c17\u7d76=" + is気絶 + ", is\u640d\u58ca=" + is損壊 + ", tgtAnimation=" + tgtAnimation + ", otherAnimation=" + otherAnimation + ", msgI18Nd=" + msgI18Nd + ", \u6d3e\u751f\u30a4\u30d9\u30f3\u30c8\u306e\u7d50\u679c\u30ea\u30b9\u30c8=" + 派生イベントの結果リスト + '}';
		}

		public EventActorResult setMsg(String v) {
			msgI18Nd = v;
			return this;
		}

		@NewInstance
		public Map<Actor, EventActorResult> asMap() {
			Map<Actor, EventActorResult> r = new HashMap<>();
			r.put(tgt, this);
			return r;
		}

	}

	public static class UserEventResult extends EventActorResult {

		public final ActionResultSummary summary;

		public UserEventResult(ActionEvent event, ActionResultSummary summary, Actor tgt) {
			super(tgt, event);
			this.summary = summary;
		}

	}

	public static class PerEvent {

		public final ActionEvent event;
		public ActionResultSummary summary;
		public final Map<Actor, EventActorResult> perActor;

		public PerEvent(ActionEvent event, ActionResultSummary summary, Map<Actor, EventActorResult> perActor) {
			this.event = event;
			this.summary = summary;
			this.perActor = perActor;
		}

		@Override
		public String toString() {
			return "PerEvent{" + "event=" + event + ", summary=" + summary + ", perActor=" + perActor + '}';
		}

	}

	public ActionResult(Action a, ActionTarget tgt) {
		this.action = a;
		this.tgt = tgt;
		this.user = tgt.getUser();
		this.userEventResult = new LinkedHashMap<>();
		this.mainEventResult = new LinkedHashMap<>();
		this.イベント未起動時の失敗理由 = null;
	}

	public ActionResult(Action a, ActionTarget tgt, ActionResultSummary イベント未起動時の失敗理由) {
		this.action = a;
		this.tgt = tgt;
		this.user = tgt.getUser();
		this.userEventResult = new LinkedHashMap<>();
		this.mainEventResult = new LinkedHashMap<>();
		this.イベント未起動時の失敗理由 = イベント未起動時の失敗理由;
	}

	public ActionResultSummary getイベント未起動時の失敗理由() {
		return イベント未起動時の失敗理由;
	}

	public boolean isイベント未起動() {
		return getイベント未起動時の失敗理由() != null;
	}

	@NotNewInstance
	public LinkedHashMap<ActionEvent, UserEventResult> getUserEventResult() {
		return userEventResult;
	}

	@NotNewInstance
	public LinkedHashMap<ActionEvent, PerEvent> getMainEventResult() {
		return mainEventResult;
	}

	public UserEventResult userEventResultOf(int i) {
		return userEventResult.get(new ArrayList<>((userEventResult.keySet())).get(i));
	}

	public PerEvent mainEventResultOf(int i) {
		return mainEventResult.get(new ArrayList<>(mainEventResult.keySet()).get(i));
	}

	public void addPerEvent(PerEvent e) {
		if (hasMainEventResult(e.event)) {
			mainEventResult.get(e.event).summary = mainEventResult.get(e.event).summary.or(e.summary);
			mainEventResult.get(e.event).perActor.putAll(e.perActor);//1人しか入っていないと思う
		} else {
			mainEventResult.put(e.event, e);
		}
	}

	public boolean hasMainEventResult(ActionEvent e) {
		return mainEventResult.containsKey(e);
	}

	public void addUserEventResult(ActionResult.UserEventResult e) {
		userEventResult.put(e.event, e);
	}

	public Action getAction() {
		return action;
	}

	public ActionTarget getTgt() {
		return tgt;
	}

	public Actor getUser() {
		return user;
	}

	public AnimationSprite getUserAnimation() {
		return userAnimation;
	}

	public void setUserAnimation(AnimationSprite userAnimation) {
		this.userAnimation = userAnimation;
	}

	public List<UserEventResult> getUserEventResultAsList() {
		return new ArrayList<>(userEventResult.values());
	}

	public List<PerEvent> getMainEventResultAsList() {
		return new ArrayList<>(mainEventResult.values());
	}

	public UserEventResult getLastUserEventResult() {
		List<UserEventResult> l = getUserEventResultAsList();
		return l.get(l.size() - 1);
	}

	public PerEvent getLastMainEventResult() {
		List<PerEvent> l = getMainEventResultAsList();
		return l.get(l.size() - 1);
	}

	public UserEventResult getFirstUserEventResult() {
		List<UserEventResult> l = getUserEventResultAsList();
		return l.get(0);
	}

	public PerEvent getFirstMainEventResult() {
		List<PerEvent> l = getMainEventResultAsList();
		return l.get(0);
	}

	public boolean is成功あり() {
		for (var v : userEventResult.values()) {
			if (v.summary.is成功()) {
				return true;
			}
		}
		for (var v : mainEventResult.values()) {
			if (v.summary.is成功()) {
				return true;
			}
		}
		return false;
	}

	public static class DamageDesc {

		public final Actor actor;
		public final int hp, mp, san;
		public final boolean is解脱;
		public final boolean is気絶;
		public final boolean is損壊;

		public DamageDesc(Actor actor, int hp, int mp, int san, boolean is解脱, boolean is気絶, boolean is損壊) {
			this.actor = actor;
			this.hp = hp;
			this.mp = mp;
			this.san = san;
			this.is解脱 = is解脱;
			this.is気絶 = is気絶;
			this.is損壊 = is損壊;
		}

		@Override
		public String toString() {
			return "DamageDesc{" + "actor=" + actor + ", hp=" + hp + ", mp=" + mp + ", san=" + san + ", is\u89e3\u8131=" + is解脱 + ", is\u6c17\u7d76=" + is気絶 + ", is\u640d\u58ca=" + is損壊 + '}';
		}

	}

	public DamageDesc getUserDamage() {
		int hp, mp, san;
		hp = mp = san = 0;
		boolean is解脱 = false;
		boolean is気絶 = false;
		boolean is損壊 = false;
		Actor user = this.user;
		for (EventActorResult e : userEventResult.values()) {
			hp += e.tgtDamageHp;
			mp += e.tgtDamageMp;
			san += e.tgtDamageSAN;
			is解脱 |= e.is解脱;
			is気絶 |= e.is気絶;
			is損壊 |= e.is損壊;
		}
		return new DamageDesc(user, hp, mp, san, is解脱, is気絶, is損壊);
	}

	public DamageDesc getDamage(Actor a) {
		if (!mainEventResult.values().stream().map(p -> p.perActor.keySet()).flatMap(p -> p.stream()).anyMatch(p -> p.equals(a))) {
			throw new GameSystemException("action " + a + " s damage is not found");
		}
		int hp, mp, san;
		hp = mp = san = 0;
		boolean is解脱 = false;
		boolean is気絶 = false;
		boolean is損壊 = false;
		for (PerEvent p : mainEventResult.values()) {
			EventActorResult e = p.perActor.get(a);
			if (e == null) {
				continue;
			}
			hp += e.tgtDamageHp;
			mp += e.tgtDamageMp;
			san += e.tgtDamageSAN;
			is解脱 |= e.is解脱;
			is気絶 |= e.is気絶;
			is損壊 |= e.is損壊;
		}
		return new DamageDesc(a, hp, mp, san, is解脱, is気絶, is損壊);

	}

	@Override
	public String toString() {
		return "ActionResult{" + "action=" + action + ", tgt=" + tgt + ", user=" + user + ", userEventResult=" + userEventResult + ", mainEventResult=" + mainEventResult + ", userAnimation=" + userAnimation + '}';
	}

}
