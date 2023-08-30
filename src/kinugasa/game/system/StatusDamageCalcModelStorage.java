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
import static kinugasa.game.system.AnimationMoveType.BEAM_BLACK;
import static kinugasa.game.system.AnimationMoveType.BEAM_BLACK_THICK;
import static kinugasa.game.system.AnimationMoveType.BEAM_BLUE;
import static kinugasa.game.system.AnimationMoveType.BEAM_BLUE_THICK;
import static kinugasa.game.system.AnimationMoveType.BEAM_GREN;
import static kinugasa.game.system.AnimationMoveType.BEAM_GREN_THICK;
import static kinugasa.game.system.AnimationMoveType.BEAM_RED;
import static kinugasa.game.system.AnimationMoveType.BEAM_RED_THICK;
import static kinugasa.game.system.AnimationMoveType.BEAM_WHITE;
import static kinugasa.game.system.AnimationMoveType.BEAM_WHITE_THICK;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Storage;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:31:55<br>
 * @author Shinacho<br>
 */
public class StatusDamageCalcModelStorage extends Storage<StatusDamageCalcModel> {

	private static final StatusDamageCalcModelStorage INSTANCE = new StatusDamageCalcModelStorage();

	private StatusDamageCalcModelStorage() {
		add(new StatusDamageCalcModel("DEFAULT") {
			@Override
			public ActionEventResult exec(Actor user, ActionEvent be, Actor tgt) {

				switch (be.getParameterType()) {
					case ADD_CONDITION:
					case ATTR_IN:
					case ITEM_ADD:
					case ITEM_LOST:
					case NONE:
					case REMOVE_CONDITION:
						throw new GameSystemException("damage calculation: Invalid damage calc type:" + be);
					case STATUS:
						switch (be.getDamageCalcType()) {
							case DIRECT:
							case PERCENT_OF_MAX:
							case PERCENT_OF_NOW:
								throw new GameSystemException("damage calculation: Invalid damage calc type:" + be);
							case USE_DAMAGE_CALC:
								//OK
								break;
							default:
								throw new AssertionError("undefined damage calc type");
						}
						break;
					default:
						throw new AssertionError("undefined parameter type");
				}
				assert be.getParameterType() == ParameterType.STATUS : "damage calculation: Invalid damage calc type:" + be;
				assert be.getDamageCalcType() == DamageCalcType.USE_DAMAGE_CALC : "damage calculation: Invalid damage calc type:" + be;

				//割合へのダメージの場合エラーとする
				if (StatusKeyStorage.getInstance().get(be.getTgtName()).getMax() == 1f) {
					throw new GameSystemException("only non-float status can be used to calc damage.");
				}

//				//P判定はActionEventで実施済み
//				if (!Random.percent(be.getP())) {
//					if (GameSystem.isDebugMode()) {
//						kinugasa.game.GameLog.print("damage calculation, calceled by P.");
//					}
//					return new ActionEventResult(ActionResultType.MISS, null);
//				}
				//魔法or攻撃
				boolean isAtk = be.getAttr().getOrder() < 10;
				StringBuilder desc = new StringBuilder();
				desc.append("SDCM : ").append(user.getName()).append("->").append(tgt.getName()).append(":");

				//攻撃------------------------------------------------------------------------------------------------------------------------
				if (isAtk) {
					desc.append("ATK,");
					//baのvalue * spread
					float spread = (be.getValue() * be.getSpread());
					float value = be.getValue();
					if (Random.percent(0.5f)) {
						value += spread;
					} else {
						value -= spread;
					}

					//アクションクリティカル判定
					boolean critical = Random.percent(user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.critAtk).getValue());
					if (critical) {
						desc.append("CRITICAL,");
						value *= (1 + user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.critAtkVal).getValue());
					}

					//回避判定
					//クリティカルの場合は回避できない
					if (!critical) {
						if (Random.percent(tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.avoAtk).getValue())) {
							desc.append("AVO");
							//回避成功
							if (GameSystem.isDebugMode()) {
								kinugasa.game.GameLog.print("damage calculation(ATK), calceled by AVO.");
							}
							//サウンド再生
							if (BattleConfig.Sound.avoidance != null) {
								BattleConfig.Sound.avoidance.load().stopAndPlay();
							}
							return new ActionEventResult(ActionResultType.MISS, new AnimationSprite());
						}
					}

					//ATTR計算
					value *= tgt.getStatus().getEffectedAttrIn().get(be.getAttr().getName()).getValue();
					//DCS
					//ユーザの装備品に計算ステータスキーがあるか検査
					Item weapon = user.getStatus().getEqipment().get(ItemEqipmentSlotStorage.getInstance().get(BattleConfig.weaponSlotName));
					if (weapon != null) {
						float ave = (float) user.getStatus()
								.getEffectedStatus()
								.stream()
								.filter(p -> weapon.getDamageCalcStatusKey().contains(p.getKey()))
								.mapToDouble(p -> p.getValue() / p.getKey().getMax())
								.average()
								.getAsDouble();
						//ダメージ計算ステータス%分威力を上げる
						value *= (1 + ave);
						desc.append("DCS,");
					}

					//攻撃力：防御力判定
					float atk = user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.atk).getValue()
							/ user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.atk).getKey().getMax();
					float def = tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.defAtk).getValue()
							/ tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.defAtk).getKey().getMax();
					//攻撃の割合-防御の割合
					float atkSubDef = atk - def * BattleConfig.atkDefPercent;
					if (atkSubDef < 0) {
						atkSubDef = 0;
					}
					desc.append("atk-def[" + atkSubDef + "],");
					value *= atkSubDef;

					//攻撃カット判定
					//クリティカルの場合はカットできない
					if (!critical) {
						if (Random.percent(tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.cutAtk).getValue())) {
							//カット成功
							desc.append("CUT,");
							value *= tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.cutAtkVal).getValue();
							//サウンド再生
							if (BattleConfig.Sound.block != null) {
								BattleConfig.Sound.block.load().stopAndPlay();
							}
						}
					}

					//ターゲットステータス名をdescに追加
					desc.append(be.getTgtName() + ",");

					//value設定
					value *= BattleConfig.damageMul;
					value = (int) value;
					if (value == 0) {
						desc.append("VALUE is 0");
						if (GameSystem.isDebugMode()) {
							kinugasa.game.GameLog.print(desc.toString());
						}
						return new ActionEventResult(ActionResultType.MISS, new AnimationSprite());
					}
					desc.append("result[" + value + "]");
					tgt.getStatus().getBaseStatus().get(be.getTgtName()).add(value);

					//アニメーション処理
					AnimationSprite sprite = null;
					if (be.hasAnimation()) {
						//アニメーションスプライト
						sprite = be.createAnimationSprite(user.getCenter(), tgt.getCenter());
						sprite.update();
						sprite.setSizeByImage();
						switch (be.getAnimationMoveType()) {
							case NONE:
								sprite.setVisible(false);
								sprite.setExist(false);
								break;
							case TGT:
								sprite.setLocationByCenter(tgt.getSprite().getCenter());
								break;
							case USER:
								sprite.setLocationByCenter(user.getSprite().getCenter());
								break;
							case BEAM_BLACK:
							case BEAM_BLACK_THICK:
							case BEAM_BLUE:
							case BEAM_BLUE_THICK:
							case BEAM_GREN:
							case BEAM_GREN_THICK:
							case BEAM_RED:
							case BEAM_RED_THICK:
							case BEAM_WHITE:
							case BEAM_WHITE_THICK:
								break;
							default:
								sprite.setVector(be.getAnimationMoveType().createVector(user.getCenter(), tgt.getCenter()));
								break;
						}
					}

					//リザルト返却
					if (GameSystem.isDebugMode()) {
						GameLog.print("DamageCalcResult:" + desc.toString());
					}
					return new ActionEventResult(ActionResultType.SUCCESS, sprite);

				}
				//魔法----------------------------------------------------------------------------------------------
				desc.append("MGK,");
				//baのvalue * spread
				float spread = (be.getValue() * be.getSpread());
				float value = be.getValue();
				if (Random.percent(0.5f)) {
					value += spread;
				} else {
					value -= spread;
				}

				//アクションクリティカル判定
				boolean critical = Random.percent(user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.critMgk).getValue());
				if (critical) {
					desc.append("CRITICAL,");
					value *= (1 + user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.critMgkVal).getValue());
				}

				//回避判定
				//クリティカルの場合は回避できない
				if (!critical) {
					if (Random.percent(tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.avoMgk).getValue())) {
						desc.append("AVO");
						//回避成功
						if (GameSystem.isDebugMode()) {
							kinugasa.game.GameLog.print("damage calculation(MGK), calceled by AVO.");
						}
						//サウンド再生
						if (BattleConfig.Sound.avoidance != null) {
							BattleConfig.Sound.avoidance.load().stopAndPlay();
						}
						return new ActionEventResult(ActionResultType.MISS, new AnimationSprite());
					}
				}

				//ATTR計算
				value *= tgt.getStatus().getEffectedAttrIn().get(be.getAttr().getName()).getValue();

				//DCS
				//ユーザの装備品に計算ステータスキーがあるか検査
				Item weapon = user.getStatus().getEqipment().get(ItemEqipmentSlotStorage.getInstance().get(BattleConfig.weaponSlotName));
				if (weapon != null) {
					float ave = (float) user.getStatus()
							.getEffectedStatus()
							.stream()
							.filter(p -> weapon.getDamageCalcStatusKey().contains(p.getKey()))
							.mapToDouble(p -> p.getValue() / p.getKey().getMax())
							.average()
							.getAsDouble();
					//ダメージ計算ステータス%分威力を上げる
					value *= (1 + ave);
					desc.append("DCS,");
				}

				//攻撃力：防御力判定
				float atk = user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.mgk).getValue()
						/ user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.mgk).getKey().getMax();
				float def = tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.defMgk).getValue()
						/ tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.defMgk).getKey().getMax();
				//攻撃の割合-防御の割合
				float atkSubDef = atk - def * BattleConfig.atkDefPercent;
				if (atkSubDef < 0) {
					atkSubDef = 0;
				}
				desc.append("atk-def[" + atkSubDef + "],");
				value *= atkSubDef;

				//攻撃カット判定
				//クリティカルの場合はカットできない
				if (!critical) {
					if (Random.percent(tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.cutMgk).getValue())) {
						//カット成功
						desc.append("CUT,");
						value *= tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.cutMgkVal).getValue();
						//サウンド再生
						if (BattleConfig.Sound.block != null) {
							BattleConfig.Sound.block.load().stopAndPlay();
						}
					}
				}

				//ターゲットステータス名をdescに追加
				desc.append(be.getTgtName() + ",");

				//value設定
				value *= BattleConfig.damageMul;
				value = (int) value;
				if (value == 0) {
					desc.append("VALUE is 0");
					if (GameSystem.isDebugMode()) {
						kinugasa.game.GameLog.print(desc.toString());
					}
					return new ActionEventResult(ActionResultType.MISS, new AnimationSprite());
				}
				desc.append("result[" + value + "]");
				tgt.getStatus().getBaseStatus().get(be.getTgtName()).add(value);

				//アニメーション処理
				AnimationSprite sprite = null;
				if (be.hasAnimation()) {
					//アニメーションスプライト
					sprite = be.createAnimationSprite(user.getCenter(), tgt.getCenter());
					sprite.update();
					sprite.setSizeByImage();
					switch (be.getAnimationMoveType()) {
						case NONE:
							sprite.setVisible(false);
							sprite.setExist(false);
							break;
						case TGT:
							sprite.setLocationByCenter(tgt.getSprite().getCenter());
							break;
						case USER:
							sprite.setLocationByCenter(user.getSprite().getCenter());
							break;
						case BEAM_BLACK:
						case BEAM_BLACK_THICK:
						case BEAM_BLUE:
						case BEAM_BLUE_THICK:
						case BEAM_GREN:
						case BEAM_GREN_THICK:
						case BEAM_RED:
						case BEAM_RED_THICK:
						case BEAM_WHITE:
						case BEAM_WHITE_THICK:
							break;
						default:
							sprite.setVector(be.getAnimationMoveType().createVector(user.getCenter(), tgt.getCenter()));
							break;
					}
				}

				//リザルト返却
				if (GameSystem.isDebugMode()) {
					GameLog.print("DamageCalcResult:" + desc.toString());
				}
				return new ActionEventResult(ActionResultType.SUCCESS, sprite);

			}
		});
		setCurrent("DEFAULT");
	}

	public static StatusDamageCalcModelStorage getInstance() {
		return INSTANCE;
	}
	private StatusDamageCalcModel current;

	public void setCurrent(String name) {
		this.current = get(name);
	}

	public StatusDamageCalcModel getCurrent() {
		return current;
	}

	private static String noneAttrKey = "NONE";

	public static String getNoneAttrKey() {
		return noneAttrKey;
	}

	public static void setNoneAttrKey(String noneAttrKey) {
		StatusDamageCalcModelStorage.noneAttrKey = noneAttrKey;
	}

}
