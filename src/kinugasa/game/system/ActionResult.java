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
import java.util.List;
import java.util.Map;
import kinugasa.game.NewInstance;
import kinugasa.game.NotNull;
import kinugasa.object.AnimationSprite;

/**
 * アクションに対する結果を定義するクラスです
 * @vesion 1.0.0 - 2023/10/14_22:12:36<br>
 * @author Shinacho<br>
 */
public class ActionResult {

	private final Action action;
	private final ActionTarget tgt;
	private final Actor user;
	private final List<EventResult> userEventResult;
	private final Map<Actor, List<EventResult>> result;
	private AnimationSprite userAnimation;
	//
	private final ActionResultSummary oneSummary;

	public static class EventResult {

		public final Actor tgt;
		public final ActionResultSummary summary;
		public final ActionEvent event;
		public int tgtDamageHp, tgtDamageMp, tgtDamageSAN;
		public boolean tgtIsDead;
		public AnimationSprite tgtAnimation, otherAnimation;
		public String msgI18Nd;
		List<EventResult> list = new ArrayList<>();

		public EventResult(Actor tgt, ActionResultSummary summary, ActionEvent event) {
			this.tgt = tgt;
			this.summary = summary;
			this.event = event;
		}

		@Override
		public String toString() {
			return "EventResult{" + "tgt=" + tgt + ", summary=" + summary + ", event=" + event + ", tgtDamageHp=" + tgtDamageHp + ", tgtDamageMp=" + tgtDamageMp + ", tgtDamageSAN=" + tgtDamageSAN + ", tgtIsDead=" + tgtIsDead + ", msgI18Nd=" + msgI18Nd + '}';
		}

		public EventResult setMsg(String v) {
			msgI18Nd = v;
			return this;
		}

	}

	public ActionResult(Action a, ActionTarget tgt, ActionResultSummary s) {
		this.action = a;
		this.tgt = tgt;
		this.user = tgt.getUser();
		this.userEventResult = null;
		this.result = new HashMap<>();
		this.oneSummary = s;

	}

	public ActionResult(Action a, ActionTarget tgt, List<EventResult> userEventResult) {
		this.action = a;
		this.tgt = tgt;
		this.user = tgt.getUser();
		this.userEventResult = userEventResult;
		this.result = new HashMap<>();
		this.oneSummary = null;

	}

	public List<EventResult> getUserEventResult() {
		return userEventResult;
	}

	public void add(Actor a, List<ActionResult.EventResult> e) {
		if (result.containsKey(a)) {
			result.get(a).addAll(e);
		} else {
			result.put(a, new ArrayList<>(e));
		}
	}

	public Action getAction() {
		return action;
	}

	public Map<Actor, List<EventResult>> getResult() {
		if (result.isEmpty()) {
			throw new GameSystemException("action result is empty, no added");
		}
		return result;
	}

	@NewInstance
	@NotNull
	public List<EventResult> allResults() {
		List<EventResult> r = new ArrayList<>();
		r.addAll(userEventResult);
		for (Actor a : result.keySet()) {
			r.addAll(result.get(a));
		}
		return r;
	}

	public ActionTarget getTgt() {
		return tgt;
	}

	public Actor getUser() {
		return user;
	}

	public boolean is成功あり() {
		if (oneSummary != null) {
			return oneSummary.is成功();
		}
		boolean userIs失敗 = userEventResult.stream().map(p -> p.summary).allMatch(p -> p.is失敗());
		boolean mainIs失敗 = result.values().stream().flatMap(p -> p.stream()).map(p -> p.summary).allMatch(p -> p.is失敗());
		if (userIs失敗 && mainIs失敗) {
			return false;
		}
		return true;
	}

	public ActionResultSummary getOneSummary() {
		return oneSummary;
	}

	public AnimationSprite getUserAnimation() {
		return userAnimation;
	}

	public void setUserAnimation(AnimationSprite userAnimation) {
		this.userAnimation = userAnimation;
	}

	public static class DamageDesc {

		public final int hp, mp, san;
		public final boolean isDead;

		public DamageDesc(int hp, int mp, int san, boolean isDead) {
			this.hp = hp;
			this.mp = mp;
			this.san = san;
			this.isDead = isDead;
		}

		@Override
		public String toString() {
			return "DamageDesc{" + "hp=" + hp + ", mp=" + mp + ", san=" + san + ", isDead=" + isDead + '}';
		}

	}

	public DamageDesc getUserDamage() {
		int hp, mp, san;
		hp = mp = san = 0;
		boolean isDead = false;
		for (EventResult e : userEventResult) {
			hp += e.tgtDamageHp;
			mp += e.tgtDamageMp;
			san += e.tgtDamageSAN;
			isDead |= e.tgtIsDead;
		}
		return new DamageDesc(hp, mp, san, isDead);
	}

	public DamageDesc getDamage(Actor a) {
		if (!result.containsKey(a)) {
			throw new GameSystemException("action " + a + " s damage is not found");
		}
		int hp, mp, san;
		hp = mp = san = 0;
		boolean isDead = false;
		for (EventResult e : result.get(a)) {
			hp += e.tgtDamageHp;
			mp += e.tgtDamageMp;
			san += e.tgtDamageSAN;
			isDead |= e.tgtIsDead;
		}
		return new DamageDesc(hp, mp, san, isDead);

	}

	@Override
	public String toString() {
		return "ActionResult{" + "action=" + action
				+ ", tgt=" + tgt + ", user="
				+ user + ", userEventResult="
				+ userEventResult + ", result="
				+ result + ", oneSummary=" + oneSummary + '}';
	}
}
