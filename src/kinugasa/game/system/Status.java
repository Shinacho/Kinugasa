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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kinugasa.game.NoLoopCall;
import kinugasa.util.*;
import kinugasa.resource.*;

/**
 * �L�����N�^��G�̃X�e�[�^�X�A�������A�퓬������s�������N���X�ł��B
 *
 * @vesion 1.0.0 - 2022/11/15_11:57:27<br>
 * @author Dra211<br>
 */
public class Status implements Nameable {

	//���O
	private String name;
	//�X�e�[�^�X�{��
	private StatusValueSet status = new StatusValueSet();
	private StatusValueSet prevStatus;
	// �����Ə�Ԉُ�ɑ΂���ϐ�
	private AttributeValueSet attrIn = new AttributeValueSet();
	//�������̌���
	private final CharacterConditionValueSet condition = new CharacterConditionValueSet();
	// �G�t�F�N�g�̌��ʎ���
	private final HashMap<ConditionKey, TimeCounter> conditionTimes = new HashMap<>();
	// �l��
	private final Race race;
	//�����Ă���A�C�e��
	private ItemBag itemBag = new ItemBag();
	//�����Ă��閂�p��
	private BookBag bookBag = new BookBag();
	//�����i
	private final HashMap<ItemEqipmentSlot, Item> eqipment = new HashMap<>();
	//����s��
	private final List<CmdAction> actions = new ArrayList<>();
	//�O�q�E��q
	private PartyLocation partyLocation = PartyLocation.FRONT;
	//�������
	private boolean exists = true;

	public Status(String name, Race race) {
		this.name = name;
		this.race = race;
		itemBag.setMax(race.getItemBagSize());
		bookBag.setMax(race.getBookBagSize());
		actions.addAll(itemBag.getItems());
		for (ItemEqipmentSlot slot : race.getEqipSlot()) {
			eqipment.put(slot, null);
		}
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public void setPartyLocation(PartyLocation partyLocation) {
		this.partyLocation = partyLocation;
	}

	public PartyLocation getPartyLocation() {
		return partyLocation;
	}

	public void setBaseAttrIn(AttributeValueSet attrIn) {
		this.attrIn = attrIn;
	}

	public void setBaseStatus(StatusValueSet status) {
		prevStatus = this.status;
		this.status = status;
	}

	public void setItemBag(ItemBag itemBag) {
		actions.removeAll(this.itemBag.getItems());
		this.itemBag = itemBag;
		actions.addAll(this.itemBag.getItems());
	}

	public void setBookBag(BookBag bookBag) {
		this.bookBag = bookBag;
	}

	public BookBag getBookBag() {
		return bookBag;
	}

	public boolean hasCondition(String name) {
		return conditionTimes.containsKey(ConditionValueStorage.getInstance().get(name).getKey());
	}

	public boolean hasConditions(boolean all, List<String> name) {
		boolean result = all;
		for (String n : name) {
			if (all) {
				result &= conditionTimes.containsKey(ConditionValueStorage.getInstance().get(n).getKey());
			} else {
				result |= conditionTimes.containsKey(ConditionValueStorage.getInstance().get(n).getKey());
				if (result) {
					return true;
				}
			}
		}
		return result;
	}

	public boolean hasConditions(boolean all, String... name) {
		return hasConditions(all, Arrays.asList(name));
	}

	public boolean canEqip(Item i) {
		if (!itemBag.contains(i)) {
			throw new GameSystemException(this + " not have item:" + i);
		}
		return i.canEqip(this) && eqipment.keySet().contains(i.getEqipmentSlot());
	}

	public List<CmdAction> getActions(ActionType type) {
		return actions.stream().filter(p -> p.getType() == type).collect(Collectors.toList());
	}

	@Override
	public String getName() {
		return name;
	}

	public int getWeaponArea() {
		int r = 0;
		for (Item i : eqipment.values()) {
			if (i != null) {
				r += i.getArea();
			}
		}
		return r;
	}

	public boolean hasAction(ParameterType batpt) {
		for (CmdAction ba : actions) {
			for (ActionEvent e : ba.getBattleEvent()) {
				if (e.getParameterType() == batpt) {
					return true;
				}
			}
		}
		return false;
	}

	public List<CmdAction> getActions() {
		return actions;
	}

	public boolean hasAction(String name) {
		return actions.stream().anyMatch(p -> p.getName().equals(name));
	}

	//��b�X�e�[�^�X���擾���܂��B�ʏ�A���x���A�b�v���ȊO�ł͂��̒l�͕ς��܂���B
	public StatusValueSet getBaseStatus() {
		return status;
	}

	public void addEqip(Item i) {
		if (!itemBag.contains(i)) {
			throw new GameSystemException(name + " is not have " + i);
		}
		if (!i.canEqip(this)) {
			throw new GameSystemException(i + " is can not eqip");
		}
		ItemEqipmentSlot slot = i.getEqipmentSlot();
		if (eqipment.containsKey(slot)) {
			eqipment.remove(slot);
		}
		eqipment.put(slot, i);
	}

	public void passItem(Status tgt, Item i) {
		if (!itemBag.contains(i)) {
			throw new GameSystemException(name + " is not have " + i);
		}
		itemBag.drop(i);
		actions.remove(i);
		tgt.itemBag.add(i);
		tgt.actions.add(i);
	}

	public void passBook(Status tgt, Book b) {
		if (!bookBag.contains(b)) {
			throw new GameSystemException(name + " is not have " + b);
		}
		bookBag.drop(b);
		tgt.bookBag.add(b);
	}

	void updateItemAction() {
		//�����Ă���A�C�e���g�p�A�N�V�������������񂷂ׂď����āAITEMBAG����ē�������
		List<CmdAction> removeList = getActions().stream().filter(p -> p.getType() == ActionType.ITEM_USE).collect(Collectors.toList());
		getActions().removeAll(removeList);
		getActions().addAll(getItemBag().getItems());
	}

	public void updateAction() {
		actions.clear();
		for (CmdAction a : ActionStorage.getInstance()) {
			if (a.getType() == ActionType.OTHER) {
				actions.add(a);
				continue;
			}
			if (a.getType() == ActionType.ITEM_USE) {
				if (itemBag.contains(a.getName())) {
					actions.add(a);
					continue;
				}
			}
			if (a.getTerms() != null && a.getTerms().stream().allMatch(p -> p.canExec(ActionTarget.instantTarget(this, a)))) {
				actions.add(a);
			}
		}

	}

	public void clearEqip() {
		eqipment.clear();
		for (ItemEqipmentSlot slot : race.getEqipSlot()) {
			eqipment.put(slot, null);
		}
	}

	public void removeEqip(Item i) {
		ItemEqipmentSlot slot = i.getEqipmentSlot();
		if (eqipment.containsKey(slot)) {
			eqipment.put(slot, null);
		}
	}

	public void removeEqip(ItemEqipmentSlot slot) {
		if (eqipment.containsKey(slot)) {
			eqipment.put(slot, null);
		}
	}

	public boolean isEqip(String itemName) {
		if (eqipment.values() == null) {
			return false;
		}
		for (Item i : eqipment.values()) {
			if (i == null) {
				continue;
			}
			if (i.getName().equals(itemName)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEqip(ItemEqipmentSlot slot) {
		return eqipment.get(slot) != null;
	}

	public boolean isEqipWMType(String typeName) {
		if (eqipment.values() == null) {
			return false;
		}
		for (Item i : eqipment.values()) {
			if (i == null) {
				continue;
			}
			if (i.getWeaponMagicType() == WeaponMagicTypeStorage.getInstance().get(typeName)) {
				return true;
			}
		}
		return false;
	}

	//�����̏�Ԉُ킪�t�^����Ă��邩���������܂�
	public boolean isConfu() {
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.CONFU) {
					return true;
				}
			}
		}
		return false;
	}

	private EffectMaster moveStopDesc;

	public EffectMaster moveStopDesc() {
		if (moveStopDesc == null) {
			return null;
		}
		return moveStopDesc;
	}

	// �������̌��ʂɊ�Â��āA���̃^�[���s���ł��邩�𔻒肵�܂�
	public boolean canMoveThisTurn() {
		if (condition.isEmpty()) {
			assert conditionTimes.isEmpty() : "condition��effectTimes�̓��������Ă��܂���";
			return true;
		}
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.STOP) {
					if (Random.percent(e.getP())) {
						moveStopDesc = e;
						return false;
					}
				}
			}
		}
		moveStopDesc = null;
		return true;
	}

	//��Ԉُ��ǉ����܂�
	public void addCondition(String name) {
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		// ���łɔ������Ă�����ʂ̏ꍇ�A�������Ȃ�
		if (condition.contains(name)) {
			assert conditionTimes.containsKey(v.getKey()) : "condition��effectTimes�̓��������Ă��܂���";
			return;
		}
		//�D��x�v�Z
		//�D��x������̏�Ԉُ킪����ꍇ�A�㏟���ō폜
		int pri = v.getKey().getPriority();
		if (!condition.asList().stream().filter(s -> s.getKey().getPriority() == pri).collect(Collectors.toList()).isEmpty()) {
			condition.remove(name);
			conditionTimes.remove(new ConditionKey(name, "", 0));
		}
		List<EffectMaster> effects = v.getEffects();
		//�^�C���Z�o
		List<EffectMaster> continueEffect = effects.stream().filter(a -> a.getContinueType() == EffectContinueType.CONTINUE).collect(Collectors.toList());
		TimeCounter tc = continueEffect.isEmpty() ? TimeCounter.oneCounter() : continueEffect.get(0).createTimeCounter();
		//�������̌��ʂƃG�t�F�N�g���ʎ��Ԃɒǉ�
		condition.add(v);
		conditionTimes.put(v.getKey(), tc);
	}

	//��Ԉُ��ǉ����܂�
	public void addCondition(ConditionKey k) {
		addCondition(k.getName());
	}

	//�G�t�F�N�g�̌��ʎ��Ԃ�����
	//�I�������G�t�F�N�g�́A�G�t�F�N�g�^�C���ƃR���f�B�V���������菜���B
	private Set<EffectMaster> execEffect = new HashSet<>();

	@NoLoopCall
	public void update() {
		//��Ԉُ�ɂ����ʂ̎��s
		List<EffectMaster> addList = new ArrayList<>();
		for (int i = 0; i < condition.size(); i++) {
			ConditionValue v = condition.asList().get(i);
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.ADD_CONDITION) {
					e.exec(this);
					if (e.getContinueType() == EffectContinueType.ONECE) {
						addList.add(e);
					}
				}
			}
		}
		execEffect.addAll(addList);

		List<ConditionKey> deleteList = new ArrayList<>();
		for (ConditionKey key : conditionTimes.keySet()) {
			if (conditionTimes.get(key).isReaching()) {
				deleteList.add(key);
			}
		}
		for (ConditionKey k : deleteList) {
			conditionTimes.remove(k);
			condition.remove(k.getName());
			//���ʂ��I�������G�t�F�N�g��ONCE���s�ς݃t���O����������
			ConditionValue v = ConditionValueStorage.getInstance().get(k.getName());
			for (EffectMaster e : v.getEffects()) {
				if (execEffect.contains(e)) {
					execEffect.remove(e);
				}
			}
		}

	}

	// ���ׂĂ̏�Ԉُ����菜���܂�
	public void clearCondition() {
		condition.clear();
		conditionTimes.clear();
	}

	// ��Ԉُ�������I�Ɏ�菜���܂�
	public void removeCondition(String name) {
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		condition.remove(v);
		conditionTimes.remove(v.getKey());
	}

	// ��Ԉُ�̌��ʎ��Ԃ��㏑�����܂��B��Ԉُ킪�t�^����Ă��Ȃ��ꍇ�̓Z�b�g���܂��B
	public void setConditionTime(String name, int time) {
		ConditionKey key = ConditionValueStorage.getInstance().get(name).getKey();
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		if (condition.contains(v)) {
			removeCondition(name);
		}
		condition.put(v);
		conditionTimes.put(key, new FrameTimeCounter(time));
	}

	// ��Ԉُ�̌��ʎ��Ԃ�ǉ����܂��B��Ԉُ킪�t�^����Ă��Ȃ��ꍇ�̓Z�b�g���܂��B
	public void addConditionTime(String name, int time) {
		ConditionKey key = ConditionValueStorage.getInstance().get(name).getKey();
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		if (condition.contains(v)) {
			removeCondition(name);
		}
		time += conditionTimes.get(key).getCurrentTime();
		condition.put(v);
		conditionTimes.put(key, new FrameTimeCounter(time));
	}

	// conditionValueSet�ɂ����ʂ�K�p�������l��ԋp
	//���ӁF�x�[�X�����킪���Ȃ��悤�ɎQ�Ƃ�ʂɂ��邱�ƁB
	//P�̔�����s���Ă���̂ŁA����Ⴄ���ʂɂȂ�\��������B
	public StatusValueSet getEffectedStatus() {
		StatusValueSet r = status.clone();

		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.STATUS) {
					if (r.contains(e.getTargetName())) {
						StatusValue tgtVal = r.get(e.getTargetName());
						if (Random.percent(e.getP())) {
							switch (e.getSetType()) {
								case ADD_PERCENT_OF_MAX:
									float val = tgtVal.getValue();
									val += (e.getValue() * tgtVal.getKey().getMax());
									tgtVal.set(val);
									break;
								case ADD_VALUE:
									tgtVal.addNoLimit(e.getValue());
									break;
								case TO:
									tgtVal.set(e.getValue());
									break;
								default:
									throw new AssertionError();
							}
						}
					}
				}
			}
		}
		for (ItemEqipmentSlot slot : eqipment.keySet()) {
			Item eqipItem = eqipment.get(slot);
			if (eqipItem != null) {
				for (StatusValue v : eqipItem.getEqStatus()) {
					r.get(v.getName()).addNoLimit(v.getValue());
				}
			}
		}

		return r;
	}

	// conditionValueSet�ɂ����ʂ�K�p�������l��ԋp
	//���ӁF�x�[�X�����킪���Ȃ��悤�ɎQ�Ƃ�ʂɂ��邱�ƁB
	//P�̔�����s���Ă���̂ŁA����Ⴄ���ʂɂȂ�\��������B
	public AttributeValueSet getEffectedAttrIn() {
		AttributeValueSet r = attrIn.clone();

		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (r.contains(e.getTargetName())) {
					AttributeValue tgtVal = r.get(e.getTargetName());
					if (Random.percent(e.getP())) {
						switch (e.getSetType()) {
							case ADD_PERCENT_OF_MAX:
								float val = tgtVal.getValue();
								val += (e.getValue() * tgtVal.getMax());
								tgtVal.set(val);
								break;
							case ADD_VALUE:
								tgtVal.add(e.getValue());
								break;
							case TO:
								tgtVal.set(e.getValue());
								break;
							default:
								throw new AssertionError();
						}
					}
				}
			}
		}
		for (ItemEqipmentSlot slot : eqipment.keySet()) {
			Item eqipItem = eqipment.get(slot);
			if (eqipItem != null) {
				for (AttributeValue v : eqipItem.getEqAttr()) {
					r.get(v.getName()).add(v.getValue());
				}
			}
		}

		return r;
	}

	//calcDamage��PREV���X�V����
	public void setDamageCalcPoint() {
		prevStatus = status.clone();
		if (GameSystem.isDebugMode()) {
			System.out.println(getName() + " / SAVE DCP : " + prevStatus);
		}
	}

	//�O�񌟍�������̍����������Z�o����
	public Map<StatusKey, Float> calcDamage() {
		if (prevStatus == null) {
			return Collections.emptyMap();
		}

		Map<StatusKey, Float> result = new HashMap<>();

		for (StatusValue v : prevStatus) {
			float val = v.getValue() - status.get(v.getKey().getName()).getValue();
			System.out.println(v.getKey().getName() + " : " + val);
			if (val != 0) {
				result.put(v.getKey(), val);
			}
		}
		if (GameSystem.isDebugMode()) {
			System.out.println("DCP<>DC[" + getName() + "] : " + result);
		}

//		prevStatus = this.status;
		return result;
	}

	public HashMap<ConditionKey, TimeCounter> getConditionTimes() {
		return conditionTimes;
	}

	public AttributeValueSet getBaseAttrIn() {
		return attrIn;
	}

	public CharacterConditionValueSet getCondition() {
		return condition;
	}

	public ItemBag getItemBag() {
		return itemBag;
	}

	public HashMap<ItemEqipmentSlot, Item> getEqipment() {
		return eqipment;
	}

	public Race getRace() {
		return race;
	}

	@Override
	public String toString() {
		return "Status{" + "name=" + name + '}';
	}

	public StatusValueSet simulateDamage(Map<StatusKey, Integer> damage) {
		StatusValueSet result = getEffectedStatus();

		for (Map.Entry<StatusKey, Integer> e : damage.entrySet()) {
			result.get(e.getKey().getName()).addNoLimit(e.getValue());
		}

		return result;
	}

}
