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

import kinugasa.game.I18N;
import kinugasa.resource.Nameable;

/**
 * ステータスのキーです.
 *
 * @vesion 1.0.0 - 2023/10/14_11:23:22<br>
 * @author Shinacho<br>
 */
public enum StatusKey implements Nameable {
	体力(true, 0, 9999, ConditionKey.損壊),
	魔力(true, 0, 9999, ConditionKey.気絶),
	正気度(true, 0, 99, ConditionKey.解脱),
	//
	筋力(true, 0, 99, null),//DCS
	器用さ(true, 0, 99, null),//DCS
	素早さ(true, 0, 99, null),//ターン内の行動順
	精神力(true, 0, 99, null),//DCS
	信仰(true, 0, 99, null),//DCS
	詠唱(true, 0, 99, null),//ターン内の行動順
	//
	攻撃力(true, 0, 999, null),
	魔法攻撃力(true, 0, 999, null),
	防御力(true, 0, 999, null),
	魔法防御力(true, 0, 999, null),
	//
	クリティカル率(true, 0, 1, null),
	クリティカルダメージ倍数(true, 0, 32f, null),
	魔法クリティカル率(true, 0, 1, null),
	魔法クリティカルダメージ倍数(true, 0, 32f, null),
	//
	命中率(true, 0, 1, null),
	回避率(true, 0, 1, null),
	ブロック率(true, 0, 1, null),
	ブロックダメージ倍率(true, 0, 1, null),
	魔法命中率(true, 0, 1, null),
	魔法回避率(true, 0, 1, null),
	魔法ブロック率(true, 0, 1, null),
	魔法ブロックダメージ倍率(true, 0, 1, null),
	//
	行動力(true, 0, 512, null),
	残り行動力(false, 0, 512, null),
	//
	魔術使用可否(true, 0, 7, null),
	装備属性(false, 0, 7, null),
	//
	レベル(true, 1, 99, null),
	保有経験値(true, 0, 99999999, null),
	次のレベルの経験値(true, 0, 99999999, null),
	レベルアップ未使用スキルポイント(false, 0, 99, null);

	public static final float 魔術使用可否＿使用可能 = 1f;

	private boolean visible;
	private float min, max;
	private boolean isPercent;
	private ConditionKey when0Condition;

	private StatusKey(boolean visible, float min, float max, ConditionKey when0Condition) {
		this.visible = visible;
		this.min = min;
		this.max = max;
		this.isPercent = max == 1f;
		this.when0Condition = when0Condition;
	}

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public boolean isVisible() {
		return visible;
	}

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

	public boolean isPercent() {
		return isPercent;
	}

	public ConditionKey getWhen0Condition() {
		return when0Condition;
	}

	@Override
	public String getName() {
		return toString();
	}

}
