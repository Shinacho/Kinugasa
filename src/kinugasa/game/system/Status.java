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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.util.*;
import kinugasa.resource.*;

/**
 * �L�����N�^��G�̃X�e�[�^�X�A�������A�퓬������s�������N���X�ł��B
 *
 * @vesion 1.0.0 - 2022/11/15_11:57:27<br>
 * @author Dra211<br>
 */
public class Status {

	//���O
	private String name;
	//�X�e�[�^�X�{��
	private final StatusValueSet status = new StatusValueSet();
	// �����Ə�Ԉُ�ɑ΂���ϐ�
	private final AttributeValueSet attrIn = new AttributeValueSet();
	//�������̌���
	private final CharacterConditionValueSet condition = new CharacterConditionValueSet();
	// �G�t�F�N�g�̌��ʎ���
	private final HashMap<ConditionKey, TimeCounter> effectTimes = new HashMap<>();
	// �l��
	private final Race race;
	//�����Ă���A�C�e��
	private final ItemBag itemBag = new ItemBag();
	//�����i
	private final HashMap<ItemEqipmentSlot, Item> eqipment = new HashMap<>();
	//����s��
	private final Storage<BattleAction> battleActions = new Storage<>();

	public Status(String name, Race race) {
		this.name = name;
		this.race = race;
		itemBag.setMax(race.getItemBagSize());
	}

	public String getName() {
		return name;
	}

	public Storage<BattleAction> getBattleActions() {
		return battleActions;
	}

	//��b�X�e�[�^�X���擾���܂��B�ʏ�A���x���A�b�v���ȊO�ł͂��̒l�͕ς��܂���B
	public StatusValueSet getBaseStatus() {
		return status;
	}

	public void addEqip(Item i) {
		if (!itemBag.contains(i)) {
			throw new GameSystemException(name + " is not have " + i);
		}
		ItemEqipmentSlot slot = i.getEqipmentSlot();
		if (eqipment.containsKey(slot)) {
			eqipment.remove(slot);
		}
		eqipment.put(slot, i);
	}

	public void clearEqip() {
		eqipment.clear();
	}

	public void removeEqip(Item i) {
		ItemEqipmentSlot slot = i.getEqipmentSlot();
		if (eqipment.containsKey(slot)) {
			eqipment.remove(slot);
		}
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

	// �������̌��ʂɊ�Â��āA���̃^�[���s���ł��邩�𔻒肵�܂�
	public boolean canMoveThiTurn() {
		if (condition.isEmpty()) {
			assert effectTimes.isEmpty() : "condition��effectTimes�̓��������Ă��܂���";
			return true;
		}
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.STOP) {
					if (Random.percent(e.getP())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	//��Ԉُ��ǉ����܂�
	public void addCondition(String name) {
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		// ���łɔ������Ă�����ʂ̏ꍇ�A�������Ȃ�
		if (condition.contains(name)) {
			assert effectTimes.containsKey(v.getKey()) : "condition��effectTimes�̓��������Ă��܂���";
			return;
		}
		//�D��x�v�Z
		//�D��x������̏�Ԉُ킪����ꍇ�A�㏟���ō폜
		int pri = v.getKey().getPriority();
		if (!condition.asList().stream().filter(s -> s.getKey().getPriority() == pri).collect(Collectors.toList()).isEmpty()) {
			condition.remove(name);
			effectTimes.remove(new ConditionKey(name, "", 0));
		}
		List<EffectMaster> effects = v.getEffects();
		//�^�C���Z�o
		List<EffectMaster> continueEffect = effects.stream().filter(a -> a.getContinueType() == EffectContinueType.CONTINUE).collect(Collectors.toList());
		TimeCounter tc = continueEffect.isEmpty() ? TimeCounter.oneCounter() : continueEffect.get(0).createTimeCounter();
		//�������̌��ʂƃG�t�F�N�g���ʎ��Ԃɒǉ�
		condition.add(v);
		effectTimes.put(v.getKey(), tc);
	}

	//��Ԉُ��ǉ����܂�
	public void addCondition(ConditionKey k) {
		addCondition(k.getName());
	}

	//�G�t�F�N�g�̌��ʎ��Ԃ�����
	//�I�������G�t�F�N�g�́A�G�t�F�N�g�^�C���ƃR���f�B�V���������菜���B
	public void update() {
		List<ConditionKey> deleteList = new ArrayList<>();
		for (ConditionKey key : effectTimes.keySet()) {
			if (effectTimes.get(key).isReaching()) {
				deleteList.add(key);
			}
		}
		for (ConditionKey k : deleteList) {
			effectTimes.remove(k);
			condition.remove(k.getName());
		}
	}

	// �R���f�B�V�����ɂ��R���f�B�V����������ݒ肷��
	//P�̔�����s���Ă���̂ŁA����Ⴄ���ʂɂȂ�\��������B
	// ���łɔ������Ă����Ԉُ�͕t�^���Ȃ��B���ʎ��Ԃ̃��Z�b�g�͕ʓr�쐬���邱��
	public void updateCondition() {
		List<ConditionValue> addList = new ArrayList<>();
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.ADD_CONDITION) {
					if (Random.percent(e.getP())) {
						if (!condition.contains(e.getTargetName())) {
							addList.add(ConditionValueStorage.getInstance().get(e.getTargetName()));
							effectTimes.put(e.getKey(), e.createTimeCounter());
						}
					}
				}
			}
		}
		condition.addAll(addList);
	}

	// conditionValueSet�ɂ����ʂ�K�p�������l��ԋp
	//���ӁF�x�[�X�����킪���Ȃ��悤�ɎQ�Ƃ�ʂɂ��邱�ƁB
	//P�̔�����s���Ă���̂ŁA����Ⴄ���ʂɂȂ�\��������B
	public StatusValueSet getEffectedStatus() {
		StatusValueSet r = status.clone();

		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
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
		return r;
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

	// �w��̃o�g���A�N�V���������s�����ۂ́A�w��̃X�e�[�^�X���ڂ̑������v�Z���܂��B
	public int calcSelfStatusDamage(String battleActionName, String statusName) {
		if (!battleActions.contains(battleActionName)) {
			throw new NameNotFoundException(battleActionName + " is not fond");
		}
		return battleActions.get(battleActionName).calcSelfStatusDamage(this, statusName);
	}

	@Override
	public String toString() {
		return "Status{" + "name=" + name + ", status=" + status + ", attrIn=" + attrIn + ", condition=" + condition + ", effectTimes=" + effectTimes + ", race=" + race + ", itemBag=" + itemBag + ", eqipment=" + eqipment + ", battleActions=" + battleActions + '}';
	}

}
