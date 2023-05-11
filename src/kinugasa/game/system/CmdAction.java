package kinugasa.game.system;

/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_20:11:07<br>
 * @author Shinacho<br>
 */
public class CmdAction implements Nameable, Comparable<CmdAction> {

	public static int missWaitTime = 66;
	//
	private ActionType type;
	private String name;
	private String desc;
	private Sound sound;
	private int waitTime;
	private boolean stop = false;
	private List<ActionEvent> battleEvent = new ArrayList<>();
	private List<ActionEvent> fieldEvent = new ArrayList<>();
	private int area;
	private List<ActionTerm> terms = new ArrayList<>();
	private int sort;
	private int spellTime;
	private int actionCount = 1;

	public CmdAction(ActionType type, String name, String desc) {
		this.type = type;
		this.name = name;
		this.desc = desc;
	}

	public void setActionCount(int actionCount) {
		this.actionCount = actionCount;
	}

	public int getActionCount() {
		return actionCount;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSpellTime() {
		return spellTime;
	}

	public CmdAction setSpellTime(int spellTime) {
		this.spellTime = spellTime;
		return this;
	}

	public int getSort() {
		return sort;
	}

	public CmdAction setSort(int sort) {
		this.sort = sort;
		return this;
	}

	public CmdAction setSound(Sound sound) {
		this.sound = sound;
		return this;
	}

	public CmdAction setWaitTime(int waitTime) {
		this.waitTime = waitTime;
		return this;
	}

	public CmdAction setBattleEvent(List<ActionEvent> battleEvent) {
		this.battleEvent = battleEvent;
		return this;
	}

	public CmdAction addBattleEvent(ActionEvent e) {
		this.battleEvent.add(e);
		return this;
	}

	public CmdAction setFieldEvent(List<ActionEvent> fieldEvent) {
		this.fieldEvent = fieldEvent;
		return this;
	}

	public CmdAction addFieldEvent(ActionEvent e) {
		this.fieldEvent.add(e);
		return this;
	}

	public CmdAction addTerm(ActionTerm t) {
		this.terms.add(t);
		return this;
	}

	public CmdAction setArea(int area) {
		this.area = area;
		return this;
	}

	public CmdAction setTerms(List<ActionTerm> terms) {
		this.terms = terms;
		return this;
	}

	@Override
	public String getName() {
		return name;
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
		return battleEvent;
	}

	private boolean fieldActionSorted = false;

	public List<ActionEvent> getFieldEvent() {
		if (!fieldActionSorted) {
			Collections.sort(fieldEvent);
			fieldActionSorted = true;
		}
		return fieldEvent;
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

	public CmdAction setStop(boolean stop) {
		this.stop = stop;
		return this;
	}

	public ActionType getType() {
		return type;
	}

	public boolean isFieldUse() {
		return (type == ActionType.MAGIC || type == ActionType.OTHER || type == ActionType.ITEM) && fieldEvent != null && !fieldEvent.isEmpty();
	}

	public boolean isBattleUse() {
		return battleEvent != null && !battleEvent.isEmpty();
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
		return terms == null ? true : terms.stream().allMatch(p -> p.canExec(tgt));
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
	public final ActionResult exec(ActionTarget tgt) {
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
		playSound();
		if (tgt.isInField()) {
			//フィールド
			if (fieldEvent == null || fieldEvent.isEmpty()) {
				throw new GameSystemException("this event is cant exec : " + this);
			}
			List<List<ActionResultType>> result = new ArrayList<>();
			for (ActionEvent e : fieldEvent) {
				for (int i = 0; i < actionCount; i++) {
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
		List<AnimationSprite> anime = new ArrayList<>();
		for (ActionEvent e : battleEvent) {
			for (int i = 0; i < actionCount; i++) {
				ActionEventResult r = e.exec(tgt);
				result.add(r.getResultTypePerTgt());
				anime.addAll(r.getAnimation());
			}
		}
		if (result.stream().flatMap(p -> p.stream()).anyMatch(p -> p == ActionResultType.SUCCESS)) {
			playSound();
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
		return "CmdAction{" + "type=" + type + ", name=" + name + '}';
	}

	@Override
	public int compareTo(CmdAction o) {
		if (sort == o.sort) {
			if (name.length() != o.name.length()) {
				return name.length() - o.name.length();
			}
			return name.compareTo(o.getName());
		}
		return sort - o.sort;
	}

}
