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
public class BattleActionEvent implements Comparable<BattleActionEvent> {

	//対象者を特定するキー
	private final BattleActionTargetType batt;
	// 対象のパラメタを特定するキー
	private final BattleActionTargetParameterType batpt;
	// 対象のパラメタ名称（HP,ATK、アイテムロストの場合はアイテム名等）
	private String targetName;
	//基礎値・・・実際にはATTR＿OUTやSTATUSから計算される
	private float baseValue = 0;
	// この攻撃の属性
	private AttributeKey atkAttr;
	//このアクションが成功する確率・・・実際にはATTR＿OUTやSTATUSからも計算される
	private float baseP = 1;
	// ダメージ計算の方式（割合、固定、等）
	private StatusDamageCalcType dct;
	//このイベントが成功したときのアニメーション
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
	 * このイベントのアニメーションを取得します。
	 *
	 * @return　アニメーション。
	 * @deprecated このメソッドから返されるアニメーションは、クローンされません。
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
