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
public class BattleActionEvent {

	//�Ώێ҂���肷��L�[
	private BattleActionTargetType targetType;
	// �Ώۂ̃p�����^����肷��L�[
	private BattleActionTargetParameterType targetParameterType;
	// �Ώۂ̃p�����^���́iHP,ATK�A�A�C�e�����X�g�̏ꍇ�̓A�C�e�������j
	private String targetName;
	//��b�l�E�E�E���ۂɂ�ATTR�QOUT��STATUS����v�Z�����
	private float baseValue;
	// ���̍U���̑���
	private AttributeKey atkAttr;
	//���̃A�N�V��������������m���E�E�E���ۂɂ�ATTR�QOUT��STATUS������v�Z�����
	private float baseP;
	// �_���[�W�v�Z�̕����i�����A�Œ�A���j
	private DamageCalcType dct;

	public BattleActionEvent(BattleActionTargetType targetType, BattleActionTargetParameterType targetParameterType, String targetName, float baseValue, AttributeKey atkAttr, float baseP, DamageCalcType dct) {
		this.targetType = targetType;
		this.targetParameterType = targetParameterType;
		this.targetName = targetName;
		this.baseValue = baseValue;
		this.atkAttr = atkAttr;
		this.baseP = baseP;
		this.dct = dct;
	}

	public void exec(GameSystem gs, BattleAction ba, Status user) {
		if (!Random.percent(baseP)) {
			if (GameSystem.isDebugMode()) {
				System.out.println(user.getName() + " �ɂ�� " + ba.getName() + " �͔������Ȃ������B");
			}
			return;
		}
		DamageCalcModel dcm = DamageCalcModelStorage.getInstance().getCurrent();
		dcm.exec(gs, user, ba, this);
	}

	public DamageCalcType getDamageCalcType() {
		return dct;
	}

	public BattleActionTargetParameterType getTargetParameterType() {
		return targetParameterType;
	}

	public BattleActionTargetType getTargetType() {
		return targetType;
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

}
