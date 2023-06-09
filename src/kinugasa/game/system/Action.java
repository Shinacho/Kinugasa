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
import java.util.Objects;
import java.util.Set;
import kinugasa.object.Sprite;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_20:11:07<br>
 * @author Shinacho<br>
 */
public class Action implements Nameable, Comparable<Action>, Cloneable {

	public static int missWaitTime = 66;
	//
	private ActionType type;
	protected String id;
	private String visibleName;
	private String desc;
	private Sound sound;
	private int waitTime;
	private boolean stop = false;
	private final List<ActionEvent> battleEvent = new ArrayList<>();
	private final List<ActionEvent> fieldEvent = new ArrayList<>();
	private int area;
	private List<ActionTerm> terms = new ArrayList<>();
	private int sort;
	private int spellTime;
	private int actionCount = 1;
	private Set<StatusKey> damageCalcStatusKey = new HashSet<>();
	private TargetOption tgtOption;

	@Override
	public Action clone() {
		try {
			return (Action) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

	public String getVisibleName() {
		return visibleName;
	}

	public Action(ActionType type, String id, String visibleName, String desc) {
		this.type = type;
		this.id = id;
		this.visibleName = visibleName;
		this.desc = desc;
	}

	public Set<StatusKey> getDamageCalcStatusKey() {
		return damageCalcStatusKey;
	}

	public TargetOption getTargetOption() {
		return tgtOption;
	}

	public Action setTargetOption(TargetOption tgtOption) {
		this.tgtOption = tgtOption;
		return this;
	}

	public Action setDamageCalcStatusKey(Set<StatusKey> damageCalcStatusKey) {
		this.damageCalcStatusKey = damageCalcStatusKey;
		return this;
	}

	public void setActionCount(int actionCount) {
		this.actionCount = actionCount;
	}

	public int getActionCount() {
		return actionCount;
	}

	public void setName(String name) {
		this.id = name;
	}

	public int getSpellTime() {
		return spellTime;
	}

	public Action setSpellTime(int spellTime) {
		this.spellTime = spellTime;
		return this;
	}

	public int getSort() {
		return sort;
	}

	public Action setSort(int sort) {
		this.sort = sort;
		return this;
	}

	public Action setSound(Sound sound) {
		this.sound = sound;
		return this;
	}

	public Action setWaitTime(int waitTime) {
		this.waitTime = waitTime;
		return this;
	}

//	public Action setBattleEvent(List<ActionEvent> battleEvent) {
//		this.battleEvent = battleEvent;
//		return this;
//	}
	public Action addBattleEvent(ActionEvent... e) {
		this.battleEvent.addAll(Arrays.asList(e));
		return this;
	}

	public Action addBattleEvent(List<ActionEvent> e) {
		this.battleEvent.addAll(e);
		return this;
	}

//	public Action setFieldEvent(List<ActionEvent> fieldEvent) {
//		this.fieldEvent = fieldEvent;
//		return this;
//	}
	public Action addFieldEvent(ActionEvent... e) {
		this.fieldEvent.addAll(Arrays.asList(e));
		return this;
	}

	public Action addFieldEvent(List<ActionEvent> e) {
		this.fieldEvent.addAll(e);
		return this;
	}

	public Action addTerm(ActionTerm t) {
		this.terms.add(t);
		return this;
	}

	public Action setArea(int area) {
		this.area = area;
		return this;
	}

	public Action setTerms(List<ActionTerm> terms) {
		this.terms = terms;
		return this;
	}

	@Override
	public String getName() {
		return id;
	}

	public String getDesc() {
		return desc;
	}

	public Sound getSound() {
		return sound;
	}

	public int getWaitTime() {
		return waitTime;
	}

	private boolean battleActionSorted = false;

	public List<ActionEvent> getBattleEvent() {
		if (!battleActionSorted) {
			Collections.sort(battleEvent);
			battleActionSorted = true;
		}
		return new ArrayList<>(battleEvent);
	}

	private boolean fieldActionSorted = false;

	public List<ActionEvent> getFieldEvent() {
		if (!fieldActionSorted) {
			Collections.sort(fieldEvent);
			fieldActionSorted = true;
		}
		return new ArrayList<>(fieldEvent);
	}

	public int getArea() {
		return area;
	}

	public int getAreaWithEqip(BattleCharacter user) {
		return getAreaWithEqip(user.getStatus());
	}

	public int getAreaWithEqip(Status user) {
		if (type == ActionType.MAGIC) {
			return this.area;
		}
		if (type == ActionType.OTHER) {
			return this.area;
		}
		return user.getWeaponArea() + this.area;
	}

	@Deprecated
	public List<ActionTerm> getTerms() {
		return terms;
	}

	public boolean isStop() {
		return stop;
	}

	public Action setStop(boolean stop) {
		this.stop = stop;
		return this;
	}

	public ActionType getType() {
		return type;
	}

	public static Set<String> magicUserDmage = new HashSet<>();

	public boolean isFieldUse() {
		boolean f = (type == ActionType.MAGIC || type == ActionType.OTHER || type == ActionType.ITEM) && fieldEvent != null && !fieldEvent.isEmpty();
		if (!f) {
			return false;
		}
		f = fieldEvent.stream().filter(p -> p.getTargetType() != TargetType.SELF).count() == 0;
		if (!f) {
			return false;
		}
		f = fieldEvent.stream().filter((p) -> {
			return !(magicUserDmage.contains(p.getTgtName()) && p.getValue() < 0);
		}).count() != 0;
		return f;

	}

	public boolean isBattleUse() {
		boolean f = battleEvent != null && !battleEvent.isEmpty();
		if (!f) {
			return false;
		}
		f = battleEvent.stream().filter(p -> p.getTargetType() != TargetType.SELF).count() != 0;
		if (!f) {
			return false;
		}
		f = battleEvent.stream().filter((p) -> {
			return !(magicUserDmage.contains(p.getTgtName()) && p.getValue() < 0);
		}).count() != 0;
		return f;
	}

	public boolean battleEventIsOnly(ParameterType t) {
		return battleEvent != null && battleEvent.stream().allMatch(p -> p.getParameterType() == t);
	}

	public boolean fieldEventIsOnly(ParameterType t) {
		return fieldEvent != null && fieldEvent.stream().allMatch(p -> p.getParameterType() == t);
	}

	public boolean battleEventIsOnly(TargetType t) {
		return battleEvent != null && battleEvent.stream().allMatch(p -> p.getTargetType() == t);
	}

	public boolean fieldEventIsOnly(TargetType t) {
		return fieldEvent == null && fieldEvent.stream().allMatch(p -> p.getTargetType() == t);
	}

	public boolean hasBattleTT(TargetType t) {
		return getBattleEvent().stream().anyMatch(p -> p.getTargetType() == t);
	}

	public boolean hasBattlePT(ParameterType t) {
		return getBattleEvent().stream().anyMatch(p -> p.getParameterType() == t);
	}

	private boolean canDoThis(ActionTarget tgt) {
		return terms == null ? true : getTerms().stream().allMatch(p -> p.canExec(tgt));
	}

	public Map<StatusKey, Integer> selfFieldDirectDamage() {
		Map<StatusKey, Integer> result = new HashMap<>();

		for (ActionEvent a : fieldEvent) {
			if (a.getTargetType() == TargetType.SELF) {
				if (a.getParameterType() == ParameterType.STATUS) {
					StatusKey key = StatusKeyStorage.getInstance().get(a.getTgtName());
					int value = 0;
					switch (a.getDamageCalcType()) {
						case DIRECT:
							value += a.getValue();
							break;
						case PERCENT_OF_MAX:
							//計算不能
							break;
						case PERCENT_OF_NOW:
							//計算不能
							break;
						case USE_DAMAGE_CALC:
							//計算不能
							break;
					}
					if (value != 0) {
						if (result.containsKey(key)) {
							int v = result.get(key);
							v += value;
							result.put(key, v);
						} else {
							result.put(key, value);
						}
					}

				}
			}
		}

		return result;
	}

	public Map<StatusKey, Integer> selfBattleDirectDamage() {
		Map<StatusKey, Integer> result = new HashMap<>();

		for (ActionEvent a : battleEvent) {
			if (a.getTargetType() == TargetType.SELF) {
				if (a.getParameterType() == ParameterType.STATUS) {
					StatusKey key = StatusKeyStorage.getInstance().get(a.getTgtName());
					int value = 0;
					switch (a.getDamageCalcType()) {
						case DIRECT:
							value += a.getValue();
							break;
						case PERCENT_OF_MAX:
							//計算不能
							break;
						case PERCENT_OF_NOW:
							//計算不能
							break;
						case USE_DAMAGE_CALC:
							//計算不能
							break;
					}
					if (value != 0) {
						if (result.containsKey(key)) {
							int v = result.get(key);
							v += value;
							result.put(key, v);
						} else {
							result.put(key, value);
						}
					}

				}
			}
		}

		return result;
	}

	//effect ->targtet
	public ActionResult exec(ActionTarget tgt) {
		if (stop || !canDoThis(tgt)) {
			if (tgt.isInField()) {
				List<ActionResultType> list = Collections.nCopies(tgt.getTarget().size(), ActionResultType.MISS);
				List<List<ActionResultType>> res = Collections.nCopies(battleEvent.size(), list);
				return new ActionResult(tgt, res, new FrameTimeCounter(missWaitTime), null);
			} else {
				List<ActionResultType> list = Collections.nCopies(tgt.getTarget().size(), ActionResultType.MISS);
				List<List<ActionResultType>> res = Collections.nCopies(fieldEvent.size(), list);
				return new ActionResult(tgt, res, new FrameTimeCounter(missWaitTime), null);
			}
		}
		if (tgt.isInField()) {
			//フィールド
			if (fieldEvent == null || fieldEvent.isEmpty()) {
				throw new GameSystemException("this event is cant exec : " + this);
			}
			List<List<ActionResultType>> result = new ArrayList<>();
			for (ActionEvent e : fieldEvent) {
				int count = actionCount;
				if (tgt.getUser().getStatus().getEqipment().get(BattleConfig.weaponSlotName) != null) {
					count *= tgt.getUser().getStatus().getEqipment().get(BattleConfig.weaponSlotName).getActionCount();
				}
				for (int i = 0; i < count; i++) {
					ActionEventResult r = e.exec(tgt);
					result.add(r.getResultTypePerTgt());
				}
			}
			if (result.stream().flatMap(p -> p.stream()).anyMatch(p -> p == ActionResultType.SUCCESS)) {
				playSound();
			}
			return new ActionResult(tgt, result, createWaitTime(), new ArrayList<>());
		}

		//バトル
		if (battleEvent == null || battleEvent.isEmpty()) {
			throw new GameSystemException("this event is cant exec : " + this);
		}
		List<List<ActionResultType>> result = new ArrayList<>();
		List<Sprite> anime = new ArrayList<>();
		for (ActionEvent e : battleEvent) {
			int count = actionCount;
			if (tgt.getUser().getStatus().getEqipment().get(BattleConfig.weaponSlotName) != null) {
				count *= tgt.getUser().getStatus().getEqipment().get(BattleConfig.weaponSlotName).getActionCount();
			}
			for (int i = 0; i < count; i++) {
				ActionEventResult r = e.exec(tgt);
				result.add(r.getResultTypePerTgt());
				anime.addAll(r.getAnimation());
				if (result.stream().flatMap(p -> p.stream()).anyMatch(p -> p == ActionResultType.SUCCESS)) {
					playSound();
				}
				if (i > 0) {
					try {
						Thread.sleep(20);
					} catch (InterruptedException ex) {
					}
//					new Thread(() -> {
//						try {
//							Thread.sleep(20);
//						} catch (InterruptedException ex) {
//						}
//						playSound();
//					}).start();
				}
			}
		}
		return new ActionResult(tgt, result, createWaitTime(), anime);

	}

	private final void playSound() {
		if (sound != null) {
			sound.load().stopAndPlay();
		}
	}

	final FrameTimeCounter createWaitTime() {
		return new FrameTimeCounter(waitTime);
	}

	@Override
	public String toString() {
		return "CmdAction{" + type + ", " + id
				+ "(" + (battleEvent.isEmpty() ? "x" : "B")
				+ (fieldEvent.isEmpty() ? "x" : "F") + ")"
				+ ", " + visibleName
				+ '}';
	}

	@Override
	public int compareTo(Action o) {
		if (sort == o.sort) {
			if (id.length() != o.id.length()) {
				return id.length() - o.id.length();
			}
			return id.compareTo(o.getName());
		}
		return sort - o.sort;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this.id);
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
		final Action other = (Action) obj;
		return Objects.equals(this.id, other.id);
	}

}
