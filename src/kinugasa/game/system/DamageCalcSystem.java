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

import kinugasa.game.GameLog;
import kinugasa.game.I18N;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:31:55<br>
 * @author Shinacho<br>
 */
public class DamageCalcSystem {

	public enum ActionType {
		物理攻撃,
		物理回復,
		魔法攻撃,
		魔法回復,
	}

	public static class Param {

		public final Actor user;
		public final Actor tgt;
		public final AttributeKey attr;
		public final ActionType actionType;
		public final float baseValue;
		public final StatusKey tgtStatusKey;
		public final StatusKey dcs;

		public Param(Actor user, Actor tgt, AttributeKey attr, ActionType actionType, float baseValue, StatusKey tgtStatusKey, StatusKey dcs) {
			this.user = user;
			this.tgt = tgt;
			this.attr = attr;
			this.actionType = actionType;
			this.baseValue = baseValue;
			this.tgtStatusKey = tgtStatusKey;
			this.dcs = dcs;
			GameLog.print(this);
		}

		@Override
		public String toString() {
			return "Param{" + "user=" + user + ", tgt=" + tgt + ", attr=" + attr + ", actionType=" + actionType + ", baseValue=" + baseValue + ", tgtStatusKey=" + tgtStatusKey + ", dcs=" + dcs + '}';
		}

	}

	public static class Result {

		public final Param param;
		public final ActionResultSummary summary;
		public final boolean isクリティカル;
		public final boolean isTgtIs回避;
		public final boolean isブロックされた;
		public final boolean 反射された;
		public final boolean 吸収された;
		public final boolean 状態異常を付与した;
		public final boolean ゼロダメ補正結果＿０にした;
		public final boolean ゼロダメ補正結果＿１にした;
		public final float atkSubDef;
		public final float finalValue;
		public final boolean reverse調整;

		public Result(Param param, ActionResultSummary summary, boolean isクリティカル, boolean isTgtIs回避,
				boolean isブロックされた, boolean 反射された, boolean 吸収された, boolean 状態異常を付与した,
				boolean zeroTo0, boolean zeroTo1,
				float atkSubDef, float finalValue, boolean reverse調整) {
			this.param = param;
			this.summary = summary;
			this.isクリティカル = isクリティカル;
			this.isTgtIs回避 = isTgtIs回避;
			this.isブロックされた = isブロックされた;
			this.反射された = 反射された;
			this.吸収された = 吸収された;
			this.状態異常を付与した = 状態異常を付与した;
			this.ゼロダメ補正結果＿０にした = zeroTo0;
			this.ゼロダメ補正結果＿１にした = zeroTo1;
			this.atkSubDef = atkSubDef;
			this.finalValue = finalValue;
			this.reverse調整 = reverse調整;
			GameLog.print(this);
		}

		@Override
		public String toString() {
			return "Result{" + "param=" + param + ", summary=" + summary + ", is\u30af\u30ea\u30c6\u30a3\u30ab\u30eb=" + isクリティカル + ", isTgtIs\u56de\u907f=" + isTgtIs回避 + ", is\u30d6\u30ed\u30c3\u30af\u3055\u308c\u305f=" + isブロックされた + ", \u53cd\u5c04\u3055\u308c\u305f=" + 反射された + ", \u5438\u53ce\u3055\u308c\u305f=" + 吸収された + ", \u72b6\u614b\u7570\u5e38\u3092\u4ed8\u4e0e\u3057\u305f=" + 状態異常を付与した + ", atkSubDef=" + atkSubDef + ", finalValue=" + finalValue + ", reverse\u8abf\u6574=" + reverse調整 + '}';
		}

	}

	public static final float SPREAD = 0.05f;

	public static Result calcDamage(Param p) {
		GameLog.print("CALC DAMAGE ^-PARAM / v-RESULT");
		if (p.baseValue == 0) {
			return new Result(p, ActionResultSummary.失敗＿基礎威力０,
					false, false, false, false, false, false, false, false, 0, 0, false);
		}
		boolean クリティカル = false;
		boolean 回避 = false;
		boolean ブロック = false;
		boolean reverse調整 = false;
		boolean 反射 = false;
		boolean 吸収 = false;
		boolean 状態異常付与 = false;
		boolean ゼロダメ＿０にした = false;
		boolean ゼロダメ＿１にした = false;

		StatusValueSet userVS = p.user.getStatus().getEffectedStatus();
		StatusValueSet tgtVS = p.tgt.getStatus().getEffectedStatus();

		switch (p.actionType) {
			case 物理攻撃: {
				float value = p.baseValue;

				//DCS計算
				if (p.dcs != null) {
					value *= (1 + userVS.get(p.dcs).get割合());
				}

				//ATTR計算
				//OUT
				if (p.attr != null) {
					value *= p.user.getStatus().getEffectedAttrOut().get(p.attr).getValue();
				}
				//IN
				if (p.attr != null) {
					value *= p.tgt.getStatus().getEffectedAttrIn().get(p.attr).getValue();
				}

				if (value == 0) {
					return new Result(p,
							ActionResultSummary.失敗＿計算結果０,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							0, value, reverse調整);
				}

				//攻撃-防御判定
				float atk = userVS.get(StatusKey.攻撃力).getValue();
				float def = tgtVS.get(StatusKey.防御力).getValue();
				float atkSubDef = atk - def * BattleConfig.ATK_DEF_PERCENT;
				if (atkSubDef < 0) {
					atkSubDef = 0;
				}
				value *= atkSubDef;

				//調整
				value *= BattleConfig.DAMAGE_MUL;

				//乱数
				float spread = value * Random.randomFloat(SPREAD);
				if (Random.randomBool()) {
					value += spread;
				} else {
					value -= spread;
				}

				//攻撃-回復調整
				if (value > 0) {
					reverse調整 = true;
					value = -value;
				}

				//0ダメ1補正
				if ((-1 < value && value <= 0) || (0 <= value && value < 1)) {
					if (Random.percent(def / (atk == 0 ? 1 : atk))) {
						value = -1;
						ゼロダメ＿１にした = true;
					} else {
						value = 0;
						ゼロダメ＿０にした = true;
					}
				}

				if (value == 0) {
					return new Result(p,
							ActionResultSummary.失敗＿計算結果０,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							atkSubDef, value, reverse調整);
				}

				//クリティカル
				if (Random.percent(userVS.get(StatusKey.クリティカル率).getValue())) {
					クリティカル = true;
					value *= (1f + userVS.get(StatusKey.クリティカルダメージ倍数).getValue());
					if (BattleConfig.Sounds.物理クリティカル != null) {
						BattleConfig.Sounds.物理クリティカル.load().stopAndPlay();
					}
				}

				//命中＊回避判定
				//クリティカルの場合は回避できない
				if (!クリティカル) {
					if (Random.percent(tgtVS.get(StatusKey.回避率).getValue() * userVS.get(StatusKey.命中率).getValue())) {
						//回避成功
						回避 = true;
						if (BattleConfig.Sounds.物理回避 != null) {
							BattleConfig.Sounds.物理回避.load().stopAndPlay();
						}
						return new Result(p, ActionResultSummary.失敗＿実行したがミス,
								false, true, false, false, false, false, false, false,
								0, 0, false);
					}
				}
				//ブロック判定
				//クリティカルの場合はブロックできない
				if (!クリティカル) {
					if (Random.percent(tgtVS.get(StatusKey.ブロック率).getValue())) {
						//ブロック成功
						ブロック = true;
						if (BattleConfig.Sounds.物理ブロック != null) {
							BattleConfig.Sounds.物理ブロック.load().stopAndPlay();
						}
						value *= tgtVS.get(StatusKey.ブロックダメージ倍率).getValue();
					}
				}

				//反射判定
				if (Random.percent(p.tgt.getStatus().getConditionFlags().get物理反射確率())) {
					if (BattleConfig.Sounds.物理反射 != null) {
						BattleConfig.Sounds.物理反射.load().stopAndPlay();
					}
					反射 = true;
					p.user.getStatus().getBaseStatus().get(p.tgtStatusKey).add(value);
					return new Result(p,
							ActionResultSummary.失敗＿反射された,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							atkSubDef, value, reverse調整);
				}

				//吸収判定
				if (Random.percent(p.tgt.getStatus().getConditionFlags().get物理吸収確率())) {
					if (BattleConfig.Sounds.物理吸収 != null) {
						BattleConfig.Sounds.物理吸収.load().stopAndPlay();
					}
					吸収 = true;
					value = -value;
					p.tgt.getStatus().getBaseStatus().get(p.tgtStatusKey).add(value);
					return new Result(p,
							ActionResultSummary.失敗＿吸収された,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							atkSubDef, value, reverse調整);
				}

				//状態異常付与判定
				for (Item i : p.user.getStatus().getEqip().values()) {
					if (i == null) {
						continue;
					}
					if (i.isAddConditionP()) {
						if (p.tgt.getStatus().getEffectedConditionRegist().containsKey(i.getAddCondition())) {
							if (Random.percent(p.tgt.getStatus().getEffectedConditionRegist().get(i.getAddCondition()))) {
								p.tgt.getStatus().addCondition(i.getAddCondition(), i.getCnditionTime());
								状態異常付与 = true;
							}
						} else {
							p.tgt.getStatus().addCondition(i.getAddCondition(), i.getCnditionTime());
							状態異常付与 = true;
						}
					}
				}

				//ダメージコミット
				p.tgt.getStatus().getBaseStatus().get(p.tgtStatusKey).add(value);
				return new Result(p,
						クリティカル
								? ActionResultSummary.成功＿クリティカル
								: ブロック
										? ActionResultSummary.成功＿ブロックされたが１以上
										: ActionResultSummary.成功,
						クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
						ゼロダメ＿０にした, ゼロダメ＿１にした,
						atkSubDef, value, reverse調整);
			}
			case 魔法攻撃: {
				float value = p.baseValue;

				//DCS計算
				if (p.dcs != null) {
					value *= (1 + userVS.get(p.dcs).get割合());
				}

				//ATTR計算
				//OUT
				if (p.attr != null) {
					value *= p.user.getStatus().getEffectedAttrOut().get(p.attr).getValue();
				}
				//IN
				if (p.attr != null) {
					value *= p.tgt.getStatus().getEffectedAttrIn().get(p.attr).getValue();
				}

				if (value == 0) {
					return new Result(p,
							ActionResultSummary.失敗＿計算結果０,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							0, value, reverse調整);
				}

				//攻撃-防御判定
				float atk = userVS.get(StatusKey.魔法攻撃力).getValue();
				float def = tgtVS.get(StatusKey.魔法防御力).getValue();
				float atkSubDef = atk - def * BattleConfig.ATK_DEF_PERCENT;
				if (atkSubDef < 0) {
					atkSubDef = 0;
				}
				value *= atkSubDef;

				//調整
				value *= BattleConfig.DAMAGE_MUL;

				//乱数
				float spread = value * Random.randomFloat(SPREAD);
				if (Random.randomBool()) {
					value += spread;
				} else {
					value -= spread;
				}

				//攻撃-回復調整
				if (value > 0) {
					reverse調整 = true;
					value = -value;
				}

				//0ダメ1補正
				if ((-1 < value && value <= 0) || (0 <= value && value < 1)) {
					if (Random.percent(def / (atk == 0 ? 1 : atk))) {
						value = -1;
						ゼロダメ＿１にした = true;
					} else {
						value = 0;
						ゼロダメ＿０にした = true;
					}
				}

				if (value == 0) {
					return new Result(p,
							ActionResultSummary.失敗＿計算結果０,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							atkSubDef, value, reverse調整);
				}

				//クリティカル
				if (Random.percent(userVS.get(StatusKey.魔法クリティカル率).getValue())) {
					クリティカル = true;
					value *= (1f + userVS.get(StatusKey.魔法クリティカルダメージ倍数).getValue());
					if (BattleConfig.Sounds.魔法クリティカル != null) {
						BattleConfig.Sounds.魔法クリティカル.load().stopAndPlay();
					}
				}

				//回避判定
				//クリティカルの場合は回避できない
				if (!クリティカル) {
					if (Random.percent(tgtVS.get(StatusKey.魔法回避率).getValue() * userVS.get(StatusKey.魔法命中率).getValue())) {
						//回避成功
						回避 = true;
						if (BattleConfig.Sounds.魔法回避 != null) {
							BattleConfig.Sounds.魔法回避.load().stopAndPlay();
						}
						return new Result(p, ActionResultSummary.失敗＿実行したがミス,
								false, true, false, false, false, false, false, false,
								0, 0, false);
					}
				}
				//ブロック判定
				//クリティカルの場合はブロックできない
				if (!クリティカル) {
					if (Random.percent(tgtVS.get(StatusKey.魔法ブロック率).getValue())) {
						//ブロック成功
						ブロック = true;
						if (BattleConfig.Sounds.魔法ブロック != null) {
							BattleConfig.Sounds.魔法ブロック.load().stopAndPlay();
						}
						value *= tgtVS.get(StatusKey.魔法ブロックダメージ倍率).getValue();
					}
				}

				//反射判定
				if (Random.percent(p.tgt.getStatus().getConditionFlags().get物理反射確率())) {
					if (BattleConfig.Sounds.魔法反射 != null) {
						BattleConfig.Sounds.魔法反射.load().stopAndPlay();
					}
					反射 = true;
					p.user.getStatus().getBaseStatus().get(p.tgtStatusKey).add(value);
					return new Result(p,
							ActionResultSummary.失敗＿反射された,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							atkSubDef, value, reverse調整);
				}

				//吸収判定
				if (Random.percent(p.tgt.getStatus().getConditionFlags().get物理吸収確率())) {
					if (BattleConfig.Sounds.魔法吸収 != null) {
						BattleConfig.Sounds.魔法吸収.load().stopAndPlay();
					}
					吸収 = true;
					value = -value;
					p.tgt.getStatus().getBaseStatus().get(p.tgtStatusKey).add(value);
					return new Result(p,
							ActionResultSummary.失敗＿吸収された,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							atkSubDef, value, reverse調整);
				}

				//状態異常付与判定
				for (Item i : p.user.getStatus().getEqip().values()) {
					if (i == null) {
						continue;
					}
					if (i.isAddConditionP()) {
						if (p.tgt.getStatus().getEffectedConditionRegist().containsKey(i.getAddCondition())) {
							if (Random.percent(p.tgt.getStatus().getEffectedConditionRegist().get(i.getAddCondition()))) {
								p.tgt.getStatus().addCondition(i.getAddCondition(), i.getCnditionTime());
								状態異常付与 = true;
							}
						} else {
							p.tgt.getStatus().addCondition(i.getAddCondition(), i.getCnditionTime());
							状態異常付与 = true;
						}
					}

				}

				//ダメージコミット
				p.tgt.getStatus().getBaseStatus().get(p.tgtStatusKey).add(value);
				return new Result(p,
						クリティカル
								? ActionResultSummary.成功＿クリティカル
								: ブロック
										? ActionResultSummary.成功＿ブロックされたが１以上
										: ActionResultSummary.成功,
						クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
						ゼロダメ＿０にした, ゼロダメ＿１にした,
						atkSubDef, value, reverse調整);
			}
			case 物理回復: {
				float value = p.baseValue;

				//DCS計算
				if (p.dcs != null) {
					value *= (1 + userVS.get(p.dcs).get割合());
				}

				//ATTR計算
				//OUT
				if (p.attr != null) {
					value *= p.user.getStatus().getEffectedAttrOut().get(p.attr).getValue();
				}
				//IN
				if (p.attr != null) {
					value *= p.tgt.getStatus().getEffectedAttrIn().get(p.attr).getValue();
				}

				if (value == 0) {
					return new Result(p,
							ActionResultSummary.失敗＿計算結果０,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							0, value, reverse調整);
				}

				//調整
				value *= BattleConfig.DAMAGE_MUL;

				//乱数
				float spread = value * Random.randomFloat(SPREAD);
				if (Random.randomBool()) {
					value += spread;
				} else {
					value -= spread;
				}

				//攻撃-回復調整
				if (value < 0) {
					reverse調整 = true;
					value = -value;
				}

				float atkSubDef = 0f;

				if (value == 0) {
					return new Result(p,
							ActionResultSummary.失敗＿計算結果０,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							atkSubDef, value, reverse調整);
				}

				//クリティカル
				if (Random.percent(userVS.get(StatusKey.クリティカル率).getValue())) {
					クリティカル = true;
					value *= (1f + userVS.get(StatusKey.クリティカルダメージ倍数).getValue());
					if (BattleConfig.Sounds.物理クリティカル != null) {
						BattleConfig.Sounds.物理クリティカル.load().stopAndPlay();
					}
				}

				//ダメージコミット
				p.tgt.getStatus().getBaseStatus().get(p.tgtStatusKey).add(value);
				return new Result(p,
						クリティカル
								? ActionResultSummary.成功＿クリティカル
								: ブロック
										? ActionResultSummary.成功＿ブロックされたが１以上
										: ActionResultSummary.成功,
						クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
						ゼロダメ＿０にした, ゼロダメ＿１にした,
						atkSubDef, value, reverse調整);
			}
			case 魔法回復: {
				float value = p.baseValue;

				//DCS計算
				if (p.dcs != null) {
					value *= (1 + userVS.get(p.dcs).get割合());
				}

				//ATTR計算
				//OUT
				if (p.attr != null) {
					value *= p.user.getStatus().getEffectedAttrOut().get(p.attr).getValue();
				}
				//IN
				if (p.attr != null) {
					value *= p.tgt.getStatus().getEffectedAttrIn().get(p.attr).getValue();
				}

				if (value == 0) {
					return new Result(p,
							ActionResultSummary.失敗＿計算結果０,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							0, value, reverse調整);
				}

				//調整
				value *= BattleConfig.DAMAGE_MUL;

				//乱数
				float spread = value * Random.randomFloat(SPREAD);
				if (Random.randomBool()) {
					value += spread;
				} else {
					value -= spread;
				}

				//攻撃-回復調整
				if (value < 0) {
					reverse調整 = true;
					value = -value;
				}

				float atkSubDef = 0f;

				if (value == 0) {
					return new Result(p,
							ActionResultSummary.失敗＿計算結果０,
							クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
							ゼロダメ＿０にした, ゼロダメ＿１にした,
							atkSubDef, value, reverse調整);
				}

				//クリティカル
				if (Random.percent(userVS.get(StatusKey.魔法クリティカル率).getValue())) {
					クリティカル = true;
					value *= (1f + userVS.get(StatusKey.魔法クリティカルダメージ倍数).getValue());
					if (BattleConfig.Sounds.魔法クリティカル != null) {
						BattleConfig.Sounds.魔法クリティカル.load().stopAndPlay();
					}
				}

				//ダメージコミット
				p.tgt.getStatus().getBaseStatus().get(p.tgtStatusKey).add(value);
				return new Result(p,
						クリティカル
								? ActionResultSummary.成功＿クリティカル
								: ブロック
										? ActionResultSummary.成功＿ブロックされたが１以上
										: ActionResultSummary.成功,
						クリティカル, 回避, ブロック, 反射, 吸収, 状態異常付与,
						ゼロダメ＿０にした, ゼロダメ＿１にした,
						atkSubDef, value, reverse調整);
			}
			default:
				throw new AssertionError("cannot damage calc : " + p);
		}
	}

}
