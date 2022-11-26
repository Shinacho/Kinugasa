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

import java.util.List;
import kinugasa.util.*;

/**
 * �o�g���A�N�V�����ɂ�������X�e�[�^�X�A�ϐ��Ȃǂ��`����N���X�ł��B
 *
 * @vesion 1.0.0 - 2022/11/16_8:22:38<br>
 * @author Dra211<br>
 */
public class BattleActionEvent implements Comparable<BattleActionEvent> {

	//�Ώێ҂���肷��L�[
	private final BattleActionTargetType batt;
	// �Ώۂ̃p�����^����肷��L�[
	private final BattleActionTargetParameterType batpt;
	// �Ώۂ̃p�����^���́iHP,ATK�A�A�C�e�����X�g�̏ꍇ�̓A�C�e�������j
	private String targetName;
	//��b�l�E�E�E���ۂɂ�ATTR�QOUT��STATUS����v�Z�����
	private float baseValue = 0;
	// ���̍U���̑���
	private AttributeKey atkAttr;
	//���̃A�N�V��������������m���E�E�E���ۂɂ�ATTR�QOUT��STATUS������v�Z�����
	private float baseP = 1;
	// �_���[�W�v�Z�̕����i�����A�Œ�A���j
	private StatusDamageCalcType dct;
	//���̃C�x���g�����������Ƃ��̃A�j���[�V����
	private BattleActionAnimation animation;

	public BattleActionEvent(BattleActionTargetType batt, BattleActionTargetParameterType batpt) {
		this.batt = batt;
		this.batpt = batpt;
	}

	public BattleActionEvent setTargetName(String name) {
		this.targetName = name;
		return this;
	}

	public BattleActionEvent setBaseValue(float v) {
		this.baseValue = v;
		return this;
	}

	public BattleActionEvent setAttribute(AttributeKey k) {
		this.atkAttr = k;
		return this;
	}

	public BattleActionEvent setBaseP(float p) {
		this.baseP = p;
		return this;
	}

	public BattleActionEvent setDamageCalcType(StatusDamageCalcType t) {
		this.dct = t;
		return this;
	}

	public BattleActionEvent setAnimation(BattleActionAnimation a) {
		this.animation = a.clone();
		return this;
	}

	public StatusDamageCalcType getDamageCalcType() {
		return dct;
	}

	public BattleActionTargetType getBatt() {
		return batt;
	}

	public BattleActionTargetParameterType getBatpt() {
		return batpt;
	}

	/**
	 * ���̃C�x���g�̃A�j���[�V�������擾���܂��B
	 *
	 * @return�@�A�j���[�V�����B
	 * @deprecated ���̃��\�b�h����Ԃ����A�j���[�V�����́A�N���[������܂���B
	 */
	@Deprecated
	public BattleActionAnimation getAnimation() {
		return animation;
	}

	public BattleActionAnimation getAnimationClone() {
		return animation.clone();
	}

	public float getBaseP() {
		return baseP;
	}

	public String getTargetName() {
		return targetName;
	}

	public float getValue() {
		return baseValue;
	}

	public AttributeKey getAtkAttr() {
		return atkAttr;
	}

	@Override
	public int compareTo(BattleActionEvent o) {
		return batpt.getValue() - o.getBatpt().getValue();
	}

}
