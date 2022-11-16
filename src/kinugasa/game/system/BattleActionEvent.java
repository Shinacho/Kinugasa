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
 * バトルアクションによる消費されるステータス、耐性などを定義するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_8:22:38<br>
 * @author Dra211<br>
 */
public class BattleActionEvent {

	//対象者を特定するキー
	private BattleActionTargetType targetType;
	// 対象のパラメタを特定するキー
	private BattleActionTargetParameterType targetParameterType;
	// 対象のパラメタ名称（HP,ATK、アイテムロストの場合はアイテム名等）
	private String targetName;
	//基礎値・・・実際にはATTR＿OUTやSTATUSから計算される
	private float baseValue;
	// この攻撃の属性
	private AttributeKey atkAttr;
	//このアクションが成功する確率・・・実際にはATTR＿OUTやSTATUSからも計算される
	private float baseP;
	// ダメージ計算の方式（割合、固定、等）
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
				System.out.println(user.getName() + " による " + ba.getName() + " は発生しなかった。");
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
