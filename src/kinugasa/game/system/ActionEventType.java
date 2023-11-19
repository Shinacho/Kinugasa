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

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import kinugasa.game.I18N;
import kinugasa.game.PlayerConstants;
import kinugasa.game.field4.FieldMap;
import kinugasa.game.field4.FieldMapStorage;
import kinugasa.game.field4.Node;
import static kinugasa.game.system.ActionEvent.CalcMode.ADD;
import static kinugasa.game.system.ActionEvent.CalcMode.DC;
import static kinugasa.game.system.ActionEvent.CalcMode.MUL;
import static kinugasa.game.system.ActionEvent.CalcMode.TO;
import static kinugasa.game.system.ActionEvent.CalcMode.TO_MAX;
import static kinugasa.game.system.ActionEvent.CalcMode.TO_ZERO;
import static kinugasa.game.system.ActionType.攻撃;
import static kinugasa.game.system.ActionType.魔法;
import static kinugasa.game.system.ConditionKey.損壊;
import static kinugasa.game.system.ConditionKey.気絶;
import static kinugasa.game.system.ConditionKey.解脱;
import kinugasa.game.ui.Dialog;
import kinugasa.game.ui.DialogIcon;
import kinugasa.game.ui.DialogOption;
import kinugasa.game.ui.Text;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.util.Random;
import kinugasa.util.StringUtil;

/**
 *
 * @vesion 1.0.0 - 2023/11/04_12:34:27<br>
 * @author Shinacho<br>
 */
public enum ActionEventType {
	ステータス攻撃(true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える,
					(int) (event.getP() * 100) + "%",
					event.getAtkAttr().getVisibleName(),
					event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("    ");
			sb.append(I18N.get(GameSystemI18NKeys.計算方法));
			switch (event.getCalcMode()) {
				case DC: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.ダメージ計算));
					sb.append("、");
					sb.append(I18N.get(GameSystemI18NKeys.基礎値)).append(":").append(Math.abs((int) event.getValue()));
					break;
				}
				case ADD: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.直接作用));
					sb.append("、");
					sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(Math.abs((int) event.getValue()));
					break;
				}
				case MUL: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.乗算));
					sb.append("、");
					sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(Math.abs((int) event.getValue()));
					break;
				}
				case TO: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.値になる));
					sb.append("、");
					sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(Math.abs((int) event.getValue()));
					break;
				}
				case TO_MAX: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.最大値になる));
					break;
				}
				case TO_ZERO: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.ゼロになる));
					break;
				}
				default: {
					break;
				}
			}
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xダメージの術式, event.getTgtStatusKey().getVisibleName()));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append(Math.abs((int) (event.getValue())));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(I18N.get(GameSystemI18NKeys.計算方法));
			switch (event.getCalcMode()) {
				case DC: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.ダメージ計算));
					break;
				}
				case ADD: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.直接作用));
					break;
				}
				case MUL: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.乗算));
					break;
				}
				case TO: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.値になる));
					break;
				}
				case TO_MAX: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.最大値になる));
					break;
				}
				case TO_ZERO: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.ゼロになる));
					break;
				}
				default: {
					break;
				}
			}
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getCalcMode() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはCALC_MODEが必要です) + " : " + this + " : " + e);
			}
			if (e.getCalcMode() == DC && e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult ar, boolean isUserEvent) {
			StatusKey tgtStatusKey = e.getTgtStatusKey();
			tgt.getStatus().saveBeforeDamageCalc();

			float value = e.getValue();
			switch (e.getCalcMode()) {
				case ADD: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
					break;
				}
				case MUL: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
					break;
				}
				case TO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
					break;
				}
				case TO_MAX: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
					break;
				}
				case TO_ZERO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
					break;
				}
				case DC: {
					if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
						throw new GameSystemException("damage calc is cant exec in field : " + this);
					}
					//攻撃タイプ調整
					DamageCalcSystem.ActionType actionType
							= switch (a.getType()) {
						case 攻撃 ->
							DamageCalcSystem.ActionType.物理攻撃;
						case 魔法 ->
							DamageCalcSystem.ActionType.魔法攻撃;
						default ->
							throw new AssertionError("damage calc cant exec : " + this);
					};

					//アイテムからDCS取得
					StatusKey dcs = null;
					if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
						Item i = user.getStatus().getEqip().get(EqipSlot.右手);
						if (i != null && i.getDcs() != null) {
							dcs = i.getDcs();
						}
					}
					if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
						Item i = user.getStatus().getEqip().get(EqipSlot.左手);
						if (i != null && i.getDcs() != null) {
							dcs = i.getDcs();
						}
					}
					if (dcs == null) {
						dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
					}
					//ダメージ計算実行
					DamageCalcSystem.Result r
							= DamageCalcSystem.calcDamage(
									new DamageCalcSystem.Param(
											user,
											tgt,
											e.getAtkAttr(),
											actionType,
											value,
											tgtStatusKey,
											dcs)
							);

					//r評価
					StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
					String msg = "";
					if (v == null) {
						I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
					} else if (v.getValue() < 0) {
						msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.Xに, tgtStatusKey.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) v.getValue());
					} else {
						msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.Xは, tgtStatusKey.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.X回復した, (int) v.getValue());
					}
					addResult(ar, r.summary, user, tgt, e, msg, isUserEvent);
					return;
				}
				default:
					throw new AssertionError("undefined calc mode");
			}//switch
			StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
			String msg = "";
			if (v.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, tgtStatusKey.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs((int) v.getValue()));
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, tgtStatusKey.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, Math.abs((int) v.getValue()));
			}
			addResult(ar, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	ステータス回復(true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性の回復ダメージをXに与える,
					(int) (event.getP() * 100) + "%",
					event.getAtkAttr().getVisibleName(),
					event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("    ");
			sb.append(I18N.get(GameSystemI18NKeys.計算方法));
			switch (event.getCalcMode()) {
				case DC: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.ダメージ計算));
					sb.append("、");
					sb.append(I18N.get(GameSystemI18NKeys.基礎値)).append(":").append(Math.abs((int) event.getValue()));
					break;
				}
				case ADD: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.直接作用));
					sb.append("、");
					sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(Math.abs((int) event.getValue()));
					break;
				}
				case MUL: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.乗算));
					sb.append("、");
					sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(Math.abs((int) event.getValue()));
					break;
				}
				case TO: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.値になる));
					sb.append("、");
					sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(Math.abs((int) event.getValue()));
					break;
				}
				case TO_MAX: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.最大値になる));
					break;
				}
				case TO_ZERO: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.ゼロになる));
					break;
				}
				default: {
					break;
				}
			}
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.X回復の術式, event.getTgtStatusKey().getVisibleName()));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append(Math.abs((int) (event.getValue())));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(I18N.get(GameSystemI18NKeys.計算方法));
			switch (event.getCalcMode()) {
				case DC: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.ダメージ計算));
					break;
				}
				case ADD: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.直接作用));
					break;
				}
				case MUL: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.乗算));
					break;
				}
				case TO: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.値になる));
					break;
				}
				case TO_MAX: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.最大値になる));
					break;
				}
				case TO_ZERO: {
					sb.append(":").append(I18N.get(GameSystemI18NKeys.ゼロになる));
					break;
				}
				default: {
					break;
				}
			}
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getCalcMode() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはCALC_MODEが必要です) + " : " + this + " : " + e);
			}
			if (e.getCalcMode() == DC && e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult ar, boolean isUserEvent) {
			StatusKey tgtStatusKey = e.getTgtStatusKey();
			tgt.getStatus().saveBeforeDamageCalc();

			float value = e.getValue();
			switch (e.getCalcMode()) {
				case ADD: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
					break;
				}
				case MUL: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
					break;
				}
				case TO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
					break;
				}
				case TO_MAX: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
					break;
				}
				case TO_ZERO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
					break;
				}
				case DC: {
					if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
						throw new GameSystemException("damage calc is cant exec in field : " + this);
					}
					//攻撃タイプ調整
					DamageCalcSystem.ActionType actionType
							= switch (a.getType()) {
						case 攻撃 ->
							DamageCalcSystem.ActionType.物理回復;
						case 魔法 ->
							DamageCalcSystem.ActionType.魔法回復;
						default ->
							throw new AssertionError("damage calc cant exec : " + this);
					};

					//アイテムからDCS取得
					StatusKey dcs = null;
					if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
						Item i = user.getStatus().getEqip().get(EqipSlot.右手);
						if (i != null && i.getDcs() != null) {
							dcs = i.getDcs();
						}
					}
					if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
						Item i = user.getStatus().getEqip().get(EqipSlot.左手);
						if (i != null && i.getDcs() != null) {
							dcs = i.getDcs();
						}
					}
					if (dcs == null) {
						dcs = actionType == DamageCalcSystem.ActionType.物理回復 ? StatusKey.筋力 : StatusKey.精神力;
					}
					//ダメージ計算実行
					DamageCalcSystem.Result r
							= DamageCalcSystem.calcDamage(
									new DamageCalcSystem.Param(
											user,
											tgt,
											e.getAtkAttr(),
											actionType,
											value,
											tgtStatusKey,
											dcs)
							);

					//r評価
					StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
					String msg = "";
					if (v == null) {
						I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
					} else if (v.getValue() < 0) {
						msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.Xに, tgtStatusKey.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs((int) v.getValue()));
					} else {
						msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.Xは, tgtStatusKey.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.X回復した, Math.abs((int) v.getValue()));
					}
					addResult(ar, r.summary, user, tgt, e, msg, isUserEvent);
					return;
				}
				default:
					throw new AssertionError("undefined calc mode");
			}//switch
			StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
			String msg = "";
			if (v.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, tgtStatusKey.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) v.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, tgtStatusKey.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) v.getValue());
			}
			addResult(ar, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	ATTR_IN(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.被耐性XをXの確率でX変更する, event.getTgtAttrIn().getVisibleName(), (int) (event.getP() * 100) + "%", event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.被属性変化の術式));
			sb.append(":");
			sb.append(event.getTgtAttrIn().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue() * 100));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtAttrIn() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATTRINが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			tgt.getStatus().getAttrIn().get(e.getTgtAttrIn()).add(e.getValue());
			String msg = I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.被属性Xが, e.getTgtAttrIn().getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xになった, (tgt.getStatus().getAttrIn().get(e.getTgtAttrIn()).getValue() * 100) + "%");
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	ATTR_OUT(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.与耐性XをXの確率でX変更する, event.getTgtAttrOut().getVisibleName(), (int) (event.getP() * 100) + "%", event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.与属性変化の術式));
			sb.append(":");
			sb.append(event.getTgtAttrOut().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue() * 100));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtAttrIn() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATTROUTが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			tgt.getStatus().getAttrIn().get(e.getTgtAttrOut()).add(e.getValue());
			String msg = I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.与属性Xが, e.getTgtAttrIn().getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xになった, (tgt.getStatus().getAttrOut().get(e.getTgtAttrOut()).getValue() * 100) + "%");
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	CND_REGIST(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常Xの耐性をXの確率でX変更する, event.getTgtCndRegist().getVisibleName(), (int) (event.getP() * 100) + "%", event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常耐性変化の術式));
			sb.append(":");
			sb.append(event.getTgtCndRegist().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue() * 100));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtCndRegist() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはCNDREGISTが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float v = tgt.getStatus().getConditionRegist().get(e.getTgtCndRegist());
			tgt.getStatus().getConditionRegist().put(e.getTgtCndRegist(), v + e.getValue());
			String msg = I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.状態異常耐性Xが, e.getTgtCndRegist().getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xになった, (tgt.getStatus().getConditionRegist().get(e.getTgtCndRegist()) * 100) + "%");
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	状態異常付与(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常XをXの確率で追加する, event.getTgtConditionKey().getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常付与の術式));
			sb.append(":");
			sb.append(event.getTgtConditionKey().getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtConditionKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTCNDKEYが必要です) + " : " + this + " : " + e);
			}
			if (e.getCndTime() == 0) {
				if (e.getTgtConditionKey() != ConditionKey.解脱 && e.getTgtConditionKey() != ConditionKey.損壊) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはCNDTIMEが必要です) + " : " + this + " : " + e);
				}
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String msg = tgt.getStatus().addCondition(e.getTgtConditionKey(), e.getCndTime());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	状態異常解除(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常XをXの確率で解除する, event.getTgtConditionKey().getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常解除の術式));
			sb.append(":");
			sb.append(event.getTgtConditionKey().getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtConditionKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTCNDKEYが必要です) + " : " + this + " : " + e);
			}
			if (e.getCndTime() == 0) {
				if (e.getTgtConditionKey() != ConditionKey.解脱 && e.getTgtConditionKey() != ConditionKey.損壊) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはCNDTIMEが必要です) + " : " + this + " : " + e);
				}
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String msg = tgt.getStatus().removeCondition(e.getTgtConditionKey());
			addResult(res, msg == null ? ActionResultSummary.失敗＿不発 : ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	アイテム追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.XをXの確率で入手する, i.getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.アイテム追加の術式));
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(":").append(i.getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				ActionStorage.getInstance().itemOf(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			boolean suc = false;
			String msg = "";
			if (e.isNoLimit() || tgt.getStatus().getItemBag().canAdd()) {
				tgt.getStatus().getItemBag().add(i);
				suc = true;
				msg = I18N.get(GameSystemI18NKeys.Xの持ち物にXが追加された, tgt.getVisibleName(), i.getVisibleName());
			} else {
				msg = I18N.get(GameSystemI18NKeys.Xはこれ以上物を持てない);
			}
			addResult(res, suc ? ActionResultSummary.成功 : ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	アイテムロスト(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.XをXの確率で失う, i.getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.アイテムロストの術式));
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(":").append(i.getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				ActionStorage.getInstance().itemOf(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			int prevSIze = tgt.getStatus().getItemBag().size();
			tgt.getStatus().getItemBag().add(i);
			String msg = I18N.get(GameSystemI18NKeys.XはXを失った, tgt.getVisibleName(), i.getVisibleName());
			addResult(res, prevSIze != tgt.getStatus().getItemBag().size()
					? ActionResultSummary.成功
					: ActionResultSummary.失敗＿不発,
					user, tgt, e, msg, isUserEvent);
		}

	},
	ドロップアイテム追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.戦闘に勝利したときXをXの確率で入手する, i.getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ドロップアイテム追加の術式));
			sb.append(":");
			sb.append(ActionStorage.getInstance().itemOf(event.getTgtID()).getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				ActionStorage.getInstance().itemOf(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			//ドロップアイテムに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.itemOf(i, 1, 1f));
				String msg = I18N.get(GameSystemI18NKeys.XはXを入手した, tgt.getVisibleName(), i.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	ドロップマテリアル追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Material m = MaterialStorage.getInstance().get(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.戦闘に勝利したときXをXの確率で入手する, m.getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ドロップマテリアル追加の術式));
			sb.append(":");
			sb.append(MaterialStorage.getInstance().get(event.getTgtID()).getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				MaterialStorage.getInstance().get(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがマテリアルIDではありません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Material m = MaterialStorage.getInstance().get(e.getTgtID());
			//ドロップマテリアルに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.materialOf(m, 1, 1f));
				String msg = I18N.get(GameSystemI18NKeys.XはXを入手した, tgt.getVisibleName(), m.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	ユーザの武器をドロップしてドロップアイテムに追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で自身の武器装備を解除して敵のドロップアイテムに追加する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.武器投擲の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				ActionStorage.getInstance().itemOf(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
			}
			//このアクションが紐づく武器タイプを逆引き検索
			WeaponType t = a.getWeaponType();
			if (t == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントが武器に紐づいていないため装備解除できません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			//このアクションが紐づく武器タイプを逆引き検索
			WeaponType t = a.getWeaponType();
			//武器タイプが紐づくスロットを取得
			EqipSlot slot = null;
			Map<EqipSlot, Item> eqip = user.getStatus().getEqip();
			for (EqipSlot es : EqipSlot.values()) {
				if (!eqip.containsKey(es)) {
					continue;
				}
				if (eqip.get(es) == null) {
					continue;
				}
				if (eqip.get(es).getWeaponType() == t) {
					slot = es;
					break;
				}
			}
			//対象者がスロットを持っていない場合は不発
			if (slot == null) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			//対象者のスロットのアイテムがnullの場合は呼び出しエラー
			Item tgtItem = eqip.get(slot);
			if (tgtItem == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.装備解除しようとしましたがユーザはすでに装備していませんTERMとの整合性を要確認) + " : " + this + " : " + e);
			}
			user.getStatus().unEqip(slot);
			//手を外した場合、もう片方の手が両手なら外す
			if (slot == EqipSlot.右手) {
				if (eqip.containsKey(EqipSlot.左手)) {
					if (eqip.get(EqipSlot.左手).equals(ActionStorage.getInstance().両手持ち)) {
						user.getStatus().unEqip(EqipSlot.左手);
					}
				}
			}
			if (slot == EqipSlot.左手) {
				if (eqip.containsKey(EqipSlot.右手)) {
					if (eqip.get(EqipSlot.右手).equals(ActionStorage.getInstance().両手持ち_弓)) {
						user.getStatus().unEqip(EqipSlot.右手);
					}
				}
			}

			//アイテムドロップ
			user.getStatus().getItemBag().drop(tgtItem);
			String msg = I18N.get(GameSystemI18NKeys.XはXを失った, user.getVisibleName(), tgtItem.getVisibleName());
			msg += Text.getLineSep();
			//ドロップアイテムに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.itemOf(tgtItem, 1, 1f));
				msg += I18N.get(GameSystemI18NKeys.XはXを入手した, tgt.getVisibleName(), tgtItem.getVisibleName());
			} else {
				//PCの場合アイテムバッグに追加、追加できなかったらロストする
				if (tgt.getStatus().getItemBag().canAdd()) {
					tgt.getStatus().getItemBag().add(tgtItem);
					msg += I18N.get(GameSystemI18NKeys.XはXを入手した, tgt.getVisibleName(), tgtItem.getVisibleName());
				}
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTの行動をVALUE回数この直後に追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は直ちにX回行動できる, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.即時追加行動の術式));
			sb.append(":").append(I18N.get(GameSystemI18NKeys.X回, ((int) event.getValue()) + ""));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			int i = 0;
			for (; i < (int) e.getValue(); i++) {
				BattleCommand cmd = new BattleCommand(user);
				if (user.isPlayer()) {
					cmd.setUserOperation(true);
				}
				BattleSystem.getInstance().addCmdFirst(cmd);
			}
			String msg = I18N.get(GameSystemI18NKeys.Xは追加で行動できるようになった, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTの行動をVALUE回数ターン最後に追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者はこのターンの最後にX回行動できる, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.遅延追加行動の術式));
			sb.append(":").append(I18N.get(GameSystemI18NKeys.X回, ((int) event.getValue()) + ""));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			int i = 0;
			for (; i < (int) e.getValue(); i++) {
				BattleCommand cmd = new BattleCommand(user);
				if (user.isPlayer()) {
					cmd.setUserOperation(true);
				}
				BattleSystem.getInstance().addCmdLast(cmd);
			}
			String msg = I18N.get(GameSystemI18NKeys.Xは追加で行動できるようになった, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の同じチームの全員にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でターゲットからXの距離内の同じチームの全員にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.放射の術式));
			sb.append(":");
			sb.append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			List<Actor> newTgts = new ArrayList<>();
			if (tgt.isPlayer()) {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			} else {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			}
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !e.equals(p)).toList()) {
					if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
						for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
							ae.exec(user, a, newTgt, res, isUserEvent);
						}
					} else {
						ae.exec(user, a, newTgt, res, isUserEvent);
					}
				}
			}
			//このイベントの結果は入れなくていい。
		}

	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の全員にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でターゲットからXの距離内の全員にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.全体放射の術式));
			sb.append(":");
			sb.append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !e.equals(p)).toList()) {
					if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
						for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
							ae.exec(user, a, newTgt, res, isUserEvent);
						}
					} else {
						ae.exec(user, a, newTgt, res, isUserEvent);
					}
				}
			}
			//このイベントの結果は入れなくていい。
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の同じチームの一人にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でターゲットからXの距離内の同じチームの一人にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.派生の術式));
			sb.append(":");
			sb.append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			List<Actor> newTgts = new ArrayList<>();
			if (tgt.isPlayer()) {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			} else {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			}
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			Actor newTgt = newTgts.get(0);
			for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !e.equals(p)).toList()) {
				if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						ae.exec(user, a, newTgt, res, isUserEvent);
					}
				} else {
					ae.exec(user, a, newTgt, res, isUserEvent);
				}
			}
			//このイベントの結果は入れなくていい。
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の一人にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でターゲットからXの距離内の一人にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.伝搬の術式));
			sb.append(":");
			sb.append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			Actor newTgt = newTgts.get(0);
			for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !e.equals(p)).toList()) {
				if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						ae.exec(user, a, newTgt, res, isUserEvent);
					}
				} else {
					ae.exec(user, a, newTgt, res, isUserEvent);
				}
			}
			//このイベントの結果は入れなくていい。
		}
	},
	このターンのTGTの行動を未行動ならこの直後に移動(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.このターン対象者が未行動ならXの確率で対象者はこの行動のすぐあとに行動できる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.即時行動の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			BattleCommand cmd = null;
			for (BattleCommand c : BattleSystem.getInstance().getCommandsOfThisTurn()) {
				if (tgt.equals(c.getUser())) {
					cmd = c;
					break;
				}
			}
			if (cmd != null) {
				BattleSystem.getInstance().moveToFirst(cmd);
				String msg = I18N.get(GameSystemI18NKeys.Xは次に行動できるようになった, tgt.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	このターンのTGTの行動を未行動ならターン最後に移動(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.このターン対象者が未行動ならXの確率で対象者はこのターンの最後に行動できる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.遅延行動の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			BattleCommand cmd = null;
			for (BattleCommand c : BattleSystem.getInstance().getCommandsOfThisTurn()) {
				if (tgt.equals(c.getUser())) {
					cmd = c;
					break;
				}
			}
			if (cmd != null) {
				BattleSystem.getInstance().moveToLast(cmd);
				String msg = I18N.get(GameSystemI18NKeys.Xは最後に行動できるようになった, tgt.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTの魔法詠唱を中断(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は魔法詠唱を中断する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.詠唱中断の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			boolean f = BattleSystem.getInstance().魔法詠唱を破棄(tgt);
			if (f) {
				String msg = I18N.get(GameSystemI18NKeys.Xは詠唱が中断された, tgt.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTの魔法詠唱完了をVALUEターン分ずらす(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			String v = event.getValue() < 0 ? "-" : "+";
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者の詠唱完了イベントをXターン移動する, (int) (event.getP() * 100) + "%", v + (int) event.getValue()));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.詠唱時間変更の術式));
			sb.append(":").append((int) event.getValue()).append(I18N.get(GameSystemI18NKeys.ターン));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			if (tgt.getStatus().hasCondition(ConditionKey.詠唱中)) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			//魔法詠唱完了イベントを取得
			int t = 0, i = 0;
			LinkedHashMap<Integer, List<MagicSpell>> ms = BattleSystem.getInstance().getMagics();
			L1:
			while (true) {
				i = 0;
				for (MagicSpell sp : ms.get(t)) {
					if (sp.getUser().equals(tgt)) {
						break L1;
					}
					i++;
				}
				t++;
				if (t > 999) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.BSの魔法詠唱中リストとACの詠唱中状態の整合性が取れていない) + " : " + this + " : " + e);
				}
			}
			MagicSpell s = ms.get(t).get(i);
			ms.get(t).remove(i);
			//削除完了したので、sをVALUE前後したターンに置く
			int tgtTurn = t + (int) e.getValue();
			if (tgtTurn <= t) {
				tgtTurn = t;
			}
			ms.get(tgtTurn).add(s);
			BattleSystem.getInstance().setMagics(ms);
			String tr = ((int) e.getValue() > 0 ? "+" : "-") + (int) e.getValue();
			String msg = I18N.get(GameSystemI18NKeys.Xの魔法詠唱はXターン移動した, tgt.getVisibleName(), tr);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	USERのクローンをパーティーまたはENEMYに追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で自身のクローンを召喚する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.術者クローニングの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String visibleName = "";
			if (tgt instanceof Enemy) {
				Enemy ee = Enemy.cloneOf(tgt);
				BattleSystem.getInstance().getEnemies().add(ee);
				visibleName = ee.getVisibleName();
			} else {
				Actor newA = new Actor(tgt.getId() + "_CLONE", tgt);
				newA.setSummoned(true);
				GameSystem.getInstance().getParty().add(newA);
				visibleName = newA.getVisibleName();
			}
			String msg = I18N.get(GameSystemI18NKeys.分身Xが現れた, visibleName);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	このターンのTGTの行動を破棄(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者はそのターン行動できなくなる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.行動阻止の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			BattleCommand cmd = BattleSystem.getInstance().getCommandsOfThisTurn().stream().filter(p -> p.getUser().equals(tgt)).findFirst().orElse(null);
			if (cmd == null) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			BattleSystem.getInstance().getCommandsOfThisTurn().remove(cmd);
			String msg = I18N.get(GameSystemI18NKeys.Xの行動は中断された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	このターンの行動順を反転させる(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でそのターンの行動順を反転させる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.トリックルームの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			LinkedList<BattleCommand> cmd = BattleSystem.getInstance().getCommandsOfThisTurn();
			Collections.reverse(cmd);
			BattleSystem.getInstance().setCommandsOfThisTurn(cmd);
			String msg = I18N.get(GameSystemI18NKeys.このターン行動順は反転した);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTを中心位置からVALUEの場所に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は中心からXの範囲内に転送される, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.集結の術式));
			sb.append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			//中心位置の取得
			Point2D.Float center = BattleSystem.getInstance().getBattleFieldSystem().getBattleFieldAllArea().getCenter();
			Point2D.Float p = Random.randomLocation(center, e.getValue());
			tgt.getSprite().setLocationByCenter(p);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTを術者の近くに転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は術者からXの範囲内に転送される, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.引き寄せの術式));
			sb.append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Point2D.Float center = user.getSprite().getCenter();
			Point2D.Float p = Random.randomLocation(center, 64f);
			tgt.getSprite().setLocationByCenter(p);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTを逃げられる位置に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者はすぐ逃げられる位置に転送される, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.退避の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Point2D.Float p = (Point2D.Float) tgt.getSprite().getCenter().clone();
			if (tgt instanceof Enemy) {
				//X = 行動力
				p.x = Random.randomFloat(tgt.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue());
			} else {
				//X = width - 行動力
				p.x = BattleSystem.getInstance().getBattleFieldSystem().getBattleFieldAllArea().getWidth()
						- Random.randomFloat(tgt.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue());
			}
			tgt.getSprite().setLocationByCenter(p);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTを一番近い敵対者の至近距離に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は一番近い敵対者のそばに転送される, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.接近の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			List<Actor> list = (tgt instanceof Enemy)
					? GameSystem.getInstance().getParty()
					: BattleSystem.getInstance().getEnemies().stream().collect(Collectors.toList());

			float dist = Float.MAX_VALUE;
			Actor newTgt = null;
			for (Actor ac : list) {
				float d = (float) tgt.getSprite().getCenter().distance(ac.getSprite().getCenter());
				if (d < dist) {
					dist = d;
					newTgt = ac;
				}
			}
			assert newTgt != null : "tgt is null : " + this;
			Point2D.Float p = Random.randomLocation(newTgt.getSprite().getCenter(), 64);
			tgt.getSprite().setLocationByCenter(p);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	USERをTGTの至近距離に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者は対象者のそばに転送される, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.術者転送の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Point2D.Float p = Random.randomLocation(tgt.getSprite().getCenter(), 64);
			user.getSprite().setLocationByCenter(p);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, user.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	USERとTGTの位置を交換(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者は対象者と位置が入れ替わる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.位置交換の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Point2D.Float p = (Point2D.Float) tgt.getSprite().getCenter().clone();
			tgt.getSprite().setLocationByCenter(user.getSprite().getCenter());
			user.getSprite().setLocationByCenter(p);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, user.getVisibleName());
			msg += Text.getLineSep();
			msg = I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTIDのCSVにあるアイテムのいずれかをUSERに追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者は特定のアイテムを手に入れる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ランダムアイテムの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			String[] ids = StringUtil.safeSplit(e.getTgtID(), ",");
			if (ids.length == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			for (String id : ids) {
				try {
					ActionStorage.getInstance().itemOf(id);
				} catch (Exception ex) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
				}
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String id = Random.randomChoice(StringUtil.safeSplit(e.getTgtID(), ","));
			if (user.getStatus().getItemBag().canAdd()) {
				//追加
				user.getStatus().getItemBag().add(ActionStorage.getInstance().itemOf(id));
			} else if (e.isNoLimit()) {
				//追加
				user.getStatus().getItemBag().add(ActionStorage.getInstance().itemOf(id));
			} else {
				String msg = I18N.get(GameSystemI18NKeys.Xはこれ以上物を持てない);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.XはXを入手した);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	逃走で戦闘終了(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で戦闘が終了し逃走扱いになる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.強制逃走の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			BattleSystem.getInstance().setEndStatus(BattleResult.敗北_こちらが全員逃げた);
			addResult(res, ActionResultSummary.成功, user, tgt, e, "", isUserEvent);
		}

	},
	TGTIDのマップIDの座標に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXにワープする, (int) (event.getP() * 100) + "%", I18N.get(event.getTgtID())));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.マップ間ワープの術式));
			sb.append(":").append(I18N.get(event.getTgtID()));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			String[] val = StringUtil.safeSplit(e.getTgtID(), ",");
			if (val.length != 3) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
			if (val[0].isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
			if (!I18N.contains(val[0])) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがI18NにありませんおそらくマップIDではありません) + " : " + this + " : " + e);
			}
			try {
				int x = Integer.parseInt(val[1]);
				int y = Integer.parseInt(val[2]);
				if (x < 0 || y < 0) {
					throw new Exception();
				}
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String[] val = StringUtil.safeSplit(e.getTgtID(), ",");
			String tgtMapId = val[0];
			int x = Integer.parseInt(val[1]);
			int y = Integer.parseInt(val[2]);
			FieldMap.getCurrentInstance().changeMap(Node.ofOutNode("AUTO_AE_NODE", tgtMapId, x, y, FourDirection.SOUTH));
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, user.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	カレントマップのランダムな出口ノードに転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で現在のマップのランダムな出入り口に移動する, (int) (event.getP() * 100) + "%", I18N.get(event.getTgtID())));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.テレポートの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Node n = FieldMap.getCurrentInstance().getNodeStorage().random();
			FieldMap.getCurrentInstance().setCurrentIdx(n.getIdx());
			FieldMap.getCurrentInstance().getCamera().updateToCenter();
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, user.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	友好的な存在の召喚(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Actor ac = new Actor(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXを召喚する, (int) (event.getP() * 100) + "%", ac.getVisibleName()));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.友好的存在召喚の術式));
			Actor ac = new Actor(event.getTgtID());
			sb.append(":").append(ac.getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			try {
				Actor ac = new Actor(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String visibleName = "";
			if (user.isPlayer()) {
				Actor ac = new Actor(e.getTgtID());
				visibleName = ac.getVisibleName();
				ac.setSummoned(true);
				GameSystem.getInstance().getParty().add(ac);
			} else {
				Enemy ac = new Enemy(e.getTgtID());
				visibleName = ac.getVisibleName();
				ac.setSummoned(true);
				BattleSystem.getInstance().getEnemies().add(ac);
			}
			String msg = I18N.get(GameSystemI18NKeys.Xが召喚された, visibleName);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	敵対的な存在の召喚(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Actor ac = new Actor(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXを召喚する, (int) (event.getP() * 100) + "%", ac.getVisibleName()));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.敵対的存在召喚の術式));
			Actor ac = new Actor(event.getTgtID());
			sb.append(":").append(ac.getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			try {
				Actor ac = new Actor(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String visibleName = "";
			if (user.isPlayer()) {
				Enemy ac = new Enemy(e.getTgtID());
				visibleName = ac.getVisibleName();
				ac.setSummoned(true);
				BattleSystem.getInstance().getEnemies().add(ac);
			} else {
				Actor ac = new Actor(e.getTgtID());
				visibleName = ac.getVisibleName();
				ac.setSummoned(true);
				GameSystem.getInstance().getParty().add(ac);
			}
			String msg = I18N.get(GameSystemI18NKeys.Xが召喚された, visibleName);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	カレントセーブデータロスト(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で現在のセーブデータを破壊しセーブせずにゲームを終了した場合はセーブデータをロストする, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.現在記録抹消の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			throw new UnsupportedOperationException("TODO:未実装");
		}

	},
	ゲームクラッシュ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でゲームがセーブされずに終了する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.次元崩壊の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Object ぬるぽ = null;
			ぬるぽ.toString();
		}
	},
	カレント以外のセーブデータを１つロスト(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で他のセーブデータを破壊する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.別次元破壊の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			throw new UnsupportedOperationException("TODO:未実装");
		}
	},
	セーブデータ全ロスト(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率ですべてのセーブデータを破壊する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.全空間破壊の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			throw new UnsupportedOperationException("TODO:未実装");
		}
	},
	独自効果(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.この効果は特殊なもので分析ができない);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.この効果は特殊なもので分析ができない);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.独自効果イベントがオーバーライドされていません) + " : " + this + " : " + e);
		}

	},
	ビームエフェクト(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.術者から対象者へビームを発射する);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.術者から対象者へビームを発射する);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.独自効果イベントがオーバーライドされていません) + " : " + this + " : " + e);
		}
	},
	DC_ファイル選択からのハッシュ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.起動するとファイル選択が開き選んだファイルに応じて属性とダメージが決まる);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.上位者の情報の術式);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			JFileChooser c = new JFileChooser(new File(PlayerConstants.getInstance().DESKTOP_PATH));
			c.setMultiSelectionEnabled(false);
			int r = c.showOpenDialog(null);
			if (r != JFileChooser.APPROVE_OPTION) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			File file = c.getSelectedFile();

			float value = file.getName().hashCode() % 128;
			value = file.getName().hashCode() % 2 == 0 ? -value : +value;
			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result dr
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									value,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (v == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (v.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) v.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) v.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_ファイル選択からのサイズ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.起動するとファイル選択が開き選んだファイルのサイズに応じて属性とダメージが決まる);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.上位者の巨大情報の術式);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			JFileChooser c = new JFileChooser(new File(PlayerConstants.getInstance().DESKTOP_PATH));
			c.setMultiSelectionEnabled(false);
			int r = c.showOpenDialog(null);
			if (r != JFileChooser.APPROVE_OPTION) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			File file = c.getSelectedFile();

			float value = 0;
			try {
				value = Files.size(file.toPath()) / 1024f / 1024f;
			} catch (IOException ex) {
				value = -1;
			}
			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result dr
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									value,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (v == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (v.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) v.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) v.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	DC_倒した敵の数(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは倒した敵の数が多いほど大きくなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.勇者の絶望の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは倒した敵の数が多いほど大きくなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Counts.Value v = Counts.getInstance().select(GameSystemI18NKeys.CountKey.倒した敵の数);
			if (v == null) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			float value = v.num / 100f;
			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									value,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_ターン数が小さい(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージはターン数が小さいほど大きくなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.速攻戦の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージはターン数が小さいほど大きくなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float value = -(128 - BattleSystem.getInstance().getTurn());

			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									value,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	DC_ターン数が大きい(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージはターン数が経過しているほど大きくなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.遅滞戦術の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージはターン数が経過しているほど大きくなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float value = -BattleSystem.getInstance().getTurn();
			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									value,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	DC_CPUのコア数(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは使用しているコンピュータのコア数により変化する));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.上位者の脳の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは使用しているコンピュータのコア数により変化する);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float value = Runtime.getRuntime().availableProcessors() * e.getValue();
			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									value,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_USERの持っているアイテムの重さ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージはアイテムをたくさん持っているほど大きくなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ヘビーボンバーの術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージはアイテムをたくさん持っているほど大きくなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			List<Item> items = user.getStatus().getItemBag().getItems();
			float val = 0;
			for (Item i : items) {
				if (i.getDesc().contains(I18N.get(GameSystemI18NKeys.軽い))) {
					val -= 0.25;
				} else if (i.getDesc().contains(I18N.get(GameSystemI18NKeys.とてつもなく重い))) {
					val -= 64;
				} else if (i.getDesc().contains(I18N.get(GameSystemI18NKeys.非常に重い))) {
					val -= 16;
				} else if (i.getDesc().contains(I18N.get(GameSystemI18NKeys.かなり重い))) {
					val -= 8;
				} else if (i.getDesc().contains(I18N.get(GameSystemI18NKeys.重い))) {
					val -= 4;
				} else {
					val -= 1;
				}
			}
			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									val,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	詠唱完了イベントをVALUEターン内で反転(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXターン内の詠唱完了を反転させる, (int) (event.getP() * 100) + "%", ((int) event.getValue()) + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.詠唱時間逆転の術式));
			sb.append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
			if ((int) e.getValue() <= 1) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			LinkedHashMap<Integer, List<MagicSpell>> ms = BattleSystem.getInstance().getMagics();
			List<Integer> turnList = ms.keySet().stream().sorted().limit((int) e.getValue()).toList();
			for (int i = 0, j = turnList.size() - 1; i < turnList.size(); i++, j--) {
				int tgtT1 = turnList.get(i);
				int tgtT2 = turnList.get(j);
				List<MagicSpell> list1 = ms.get(tgtT1);
				List<MagicSpell> list2 = ms.get(tgtT2);
				ms.put(tgtT1, list2);
				ms.put(tgtT2, list1);
			}

			BattleSystem.getInstance().setMagics(ms);
			String msg = I18N.get(GameSystemI18NKeys.Xターン内の魔法詠唱は反転された, (int) e.getValue());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	自身以外の全員の正気度にダメージ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で全員にXの正気度ダメージを与える, (int) (event.getP() * 100) + "%", ((int) event.getValue()) + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.正気度ダメージの術式));
			sb.append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max damage) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String msg = "";
			for (Actor ac : Stream.of(GameSystem.getInstance().getParty(), BattleSystem.getInstance().getEnemies()).flatMap(p -> p.stream()).toList()) {
				if (ac.equals(user)) {
					continue;
				}
				int v = (int) e.getValue();
				if (v > 0) {
					v = -v;
				}
				int val = Random.randomAbsInt(v) + 1;
				ac.getStatus().getBaseStatus().get(StatusKey.正気度).add(val);
				msg += I18N.get(GameSystemI18NKeys.XはXの正気度ダメージを受けた, ac.getVisibleName(), val) + Text.getLineSep();
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	WEBサイト起動(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.上位者の情報を閲覧する);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.上位者の情報閲覧の術式);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + "(n>1) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String tgtId = e.getTgtID();
			try {
				if (PlayerConstants.getInstance().OS_NAME.toLowerCase().contains("windows")) {
					Runtime.getRuntime().exec("cmd /q/c start " + tgtId);
				} else {
					Runtime.getRuntime().exec("open " + tgtId);
				}
				addResult(res, ActionResultSummary.成功, user, tgt, e, "", isUserEvent);
			} catch (IOException ex) {
			}
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, "", isUserEvent);
		}

	},
	DC_減っている体力(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の体力が減っているほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.背水の陣の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の体力が減っているほど高くなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float val = e.getValue() * (1 - user.getStatus().getEffectedStatus().get(StatusKey.体力).get割合());

			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									val,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_減っている魔力(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の魔力が減っているほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.精神限界の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の魔力が減っているほど高くなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float val = e.getValue() * (1 - user.getStatus().getEffectedStatus().get(StatusKey.魔力).get割合());

			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									val,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_減っている正気度(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の正気度が減っているほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.狂気の笑みの術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の正気度が減っているほど高くなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float val = e.getValue() * (1 - user.getStatus().getEffectedStatus().get(StatusKey.正気度).get割合());

			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									val,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_残っている体力(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の体力が最大値に近いほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.体力の余裕の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の体力が最大値に近いほど高くなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float val = e.getValue() * (user.getStatus().getEffectedStatus().get(StatusKey.体力).get割合());

			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									val,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_残っている魔力(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の魔力が最大値に近いほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.魔力の余裕の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の魔力が最大値に近いほど高くなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float val = e.getValue() * (user.getStatus().getEffectedStatus().get(StatusKey.魔力).get割合());

			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									val,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	DC_残っている正気度(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の正気度が最大値に近いほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.精神的余裕の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の正気度が最大値に近いほど高くなる);
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float val = e.getValue() * (user.getStatus().getEffectedStatus().get(StatusKey.正気度).get割合());

			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									e.getAtkAttr(),
									actionType,
									val,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	USERによる指定IDの魔法の詠唱完了をこのターンの最後にVALUE回数追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Action a = ActionStorage.getInstance().actionOf(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXをこのターンの最後にX回発動する, (int) (event.getP() * 100) + "%", a.getVisibleName(), ((int) event.getValue()) + ""));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.高速詠唱の術式));
			sb.append(":");
			Action a = ActionStorage.getInstance().actionOf(event.getTgtID());
			sb.append(a.getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				ActionStorage.getInstance().actionOf(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + "(id=actionID) : " + this + " : " + e);
			}
			if (e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Action tgtA = ActionStorage.getInstance().actionOf(e.getTgtID());
			for (int i = 0; i < (int) e.getValue(); i++) {
				MagicSpell ms = new MagicSpell(user, tgtA, user.isPlayer());
				BattleSystem.getInstance().addCmdLast(ms);
			}
			String msg = I18N.get(GameSystemI18NKeys.XはXをX回発動する準備をした,
					user.getVisibleName(),
					tgtA.getVisibleName(),
					(int) e.getValue());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	DC_ランダム属性のランダムダメージ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でランダムな属性のランダムなダメージをXに与える, (int) (event.getP() * 100) + "%", event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ランダムシードの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			float val = Random.d100(1);
			AttributeKey attr = Random.randomChoice(AttributeKey.values());

			if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
				throw new GameSystemException("damage calc is cant exec in field : " + this);
			}
			//攻撃タイプ調整
			DamageCalcSystem.ActionType actionType
					= switch (a.getType()) {
				case 攻撃 ->
					DamageCalcSystem.ActionType.物理攻撃;
				case 魔法 ->
					DamageCalcSystem.ActionType.魔法攻撃;
				default ->
					throw new AssertionError("damage calc cant exec : " + this);
			};

			//アイテムからDCS取得
			StatusKey dcs = null;
			if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.右手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				Item i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null) {
					dcs = i.getDcs();
				}
			}
			if (dcs == null) {
				dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
			}
			//ダメージ計算実行
			DamageCalcSystem.Result r
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									attr,
									actionType,
									val,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			StatusValue vv = tgt.getStatus().getDamageFromSavePoint().get(e.getTgtStatusKey());
			String msg = "";
			if (vv == null) {
				I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
			} else if (vv.getValue() < 0) {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) vv.getValue());
			} else {
				msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, e.getTgtStatusKey().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) vv.getValue());
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	USERの指定スロットの装備品の攻撃回数をVALUE上げる(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者のX装備の攻撃回数をX上げる, (int) (event.getP() * 100) + "%", EqipSlot.valueOf(event.getTgtID()).getVisibleName(), ((int) event.getValue()) + ""));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.攻撃回数増加の術式));
			sb.append(":").append(EqipSlot.valueOf(event.getTgtID()).getVisibleName()).append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
				if (slot == null) {
					throw new Exception();
				}
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがSLOTではありません) + " : " + this + " : " + e);
			}
			if (e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(n>0) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (user.getStatus().getEqip().keySet().contains(slot)) {
				Item i = user.getStatus().getEqip().get(slot);
				if (i == null) {
					String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
					addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
					return;
				}
				i.setAtkCount(i.getAtkCount() + (int) e.getValue());
				String msg = I18N.get(GameSystemI18NKeys.XのXは攻撃回数が変動した, user.getVisibleName(), i.getVisibleName());
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}
	},
	USERの指定スロットの装備品の価値をVALUE倍にする(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者のX装備の価値をX倍にする, (int) (event.getP() * 100) + "%", EqipSlot.valueOf(event.getTgtID()).getVisibleName(), (event.getValue()) + ""));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.装備価値変更の術式));
			sb.append(":").append(EqipSlot.valueOf(event.getTgtID()).getVisibleName()).append(":").append(event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
				if (slot == null) {
					throw new Exception();
				}
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがSLOTではありません) + " : " + this + " : " + e);
			}
			if (e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(n>0) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (user.getStatus().getEqip().keySet().contains(slot)) {
				Item i = user.getStatus().getEqip().get(slot);
				if (i == null) {
					String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
					addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
					return;
				}
				int v = (int) (i.getPrice() * e.getValue());
				if (v < 0) {
					v = 0;
				}
				String msg = I18N.get(GameSystemI18NKeys.XのXは価値が変動した, user.getVisibleName(), i.getVisibleName());
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTを即死させる(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象を即死させる, (int) (event.getP() * 100) + "%"));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.即死の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtConditionKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTCNDKEYが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtConditionKey() != ConditionKey.解脱 && e.getTgtConditionKey() != ConditionKey.気絶 && e.getTgtConditionKey() != ConditionKey.損壊) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントのTGTCNDKEYは解脱損壊気絶のいずれかである必要があります) + " : " + this + " : " + e);
			}
			if (e.getTgtConditionKey() == 気絶) {
				if (e.getCndTime() == 0) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはCNDTIMEが必要です) + " : " + this + " : " + e);
				}
			}

		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			switch (e.getTgtConditionKey()) {
				case 解脱: {
					tgt.getStatus().getBaseStatus().get(StatusKey.正気度).setValue(0);
					tgt.getStatus().addWhen0Condition();
					break;
				}
				case 損壊: {
					tgt.getStatus().getBaseStatus().get(StatusKey.体力).setValue(0);
					tgt.getStatus().addWhen0Condition();
					break;
				}
				case 気絶: {
					tgt.getStatus().getBaseStatus().get(StatusKey.魔力).setValue(0);
					tgt.getStatus().addWhen0Condition();
					break;
				}
				default:
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントのTGTCNDKEYは解脱損壊気絶のいずれかである必要があります) + " : " + this + " : " + e);
			}
			String msg = tgt.getVisibleName() + e.getTgtConditionKey().getStartMsgI18Nd();
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	指定スロットの装備解除(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者のX装備を解除する, (int) (event.getP() * 100) + "%", EqipSlot.valueOf(event.getTgtID()).getVisibleName()));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.パージの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
				if (slot == null) {
					throw new Exception();
				}
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがSLOTではありません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (tgt.getStatus().getEqip().keySet().contains(slot)) {
				tgt.getStatus().getEqip().put(slot, null);
				String msg = I18N.get(GameSystemI18NKeys.XはX装備を外した, user.getVisibleName(), slot.getVisibleName());
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	マップIDと座標を入力させて移動する(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.指定したマップの指定した座標にワープする));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.転送の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			class Map {

				final String id;
				final String visibleName;

				public Map(String id, String visibleName) {
					this.id = id;
					this.visibleName = visibleName;
				}

				@Override
				public String toString() {
					return visibleName;
				}

			}
			List<Map> mapList = new ArrayList<>();
			JComboBox comboBox = new JComboBox();
			for (FieldMap m : FieldMapStorage.getInstance()) {
				Map map = new Map(m.getName(), I18N.get(m.getName()));
				mapList.add(map);
				comboBox.addItem(map);
			}
			if (DialogOption.OK != Dialog.okOrCancel("teleport mapName", DialogIcon.QUESTION, comboBox)) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			Map tgtMap = mapList.get(comboBox.getSelectedIndex());
			JSpinner x = new JSpinner();
			x.setModel(new SpinnerNumberModel(0, 0, 999, 1));
			JSpinner y = new JSpinner();
			y.setModel(new SpinnerNumberModel(0, 0, 999, 1));
			JPanel p = new JPanel();
			p.add(x);
			p.add(y);
			if (DialogOption.OK != Dialog.okOrCancel("teleport x,y", DialogIcon.QUESTION, p)) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			FieldMap.getCurrentInstance().changeMap(Node.ofOutNode("AUTO_NODE_FROM_AE", tgtMap.id,
					(Integer) x.getValue(), (Integer) y.getValue(), FourDirection.NORTH));
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, "", isUserEvent);
		}

	},
	ダミー＿成功(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.このイベントは処理の都合で入っているようだ));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ダミーの術式＿成功));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			addResult(res, ActionResultSummary.成功, user, tgt, e, "", isUserEvent);
		}

	},
	ダミー＿失敗(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.このイベントは処理の都合で入っているようだ));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ダミーの術式＿失敗));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, "", isUserEvent);
		}
	},
	メッセージ表示(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.このイベントは処理の都合で入っているようだ));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ダミーの術式＿メッセージ表示));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			addResult(res, ActionResultSummary.成功, user, tgt, e, e.getTgtID(), isUserEvent);
		}

	},
	フラグ参照メッセージ表示(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.このイベントは処理の都合で入っているようだ));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ダミーの術式＿メッセージ表示));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			//フラグ情報
			String[] val = StringUtil.safeSplit(e.getTgtID(), ",");
			if (val.length != 3) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
			String name = val[0];
			FlagStatus fs = FlagStatus.valueOf(val[1]);
			if (!FlagStorage.getInstance().contains(name)) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			String[] val = StringUtil.safeSplit(e.getTgtID(), ",");
			String name = val[0];
			FlagStatus fs = FlagStatus.valueOf(val[1]);
			if (!FlagStorage.getInstance().get(name).get().is(fs)) {
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, "", isUserEvent);
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, val[2], isUserEvent);
		}

	},
	指定IDのPCがいれば即死させる(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象を即死させる, (int) (event.getP() * 100) + "%"));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.即死の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtConditionKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTCNDKEYが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtConditionKey() != ConditionKey.解脱 && e.getTgtConditionKey() != ConditionKey.気絶 && e.getTgtConditionKey() != ConditionKey.損壊) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントのTGTCNDKEYは解脱損壊気絶のいずれかである必要があります) + " : " + this + " : " + e);
			}
			if (e.getTgtConditionKey() == 気絶) {
				if (e.getCndTime() == 0) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはCNDTIMEが必要です) + " : " + this + " : " + e);
				}
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Actor ac = GameSystem.getInstance().getPCbyID(e.getTgtID());
			switch (e.getTgtConditionKey()) {
				case 解脱: {
					ac.getStatus().getBaseStatus().get(StatusKey.正気度).setValue(0);
					ac.getStatus().addWhen0Condition();
					break;
				}
				case 損壊: {
					ac.getStatus().getBaseStatus().get(StatusKey.体力).setValue(0);
					ac.getStatus().addWhen0Condition();
					break;
				}
				case 気絶: {
					ac.getStatus().getBaseStatus().get(StatusKey.魔力).setValue(0);
					ac.getStatus().addWhen0Condition();
					break;
				}
				default:
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントのTGTCNDKEYは解脱損壊気絶のいずれかである必要があります) + " : " + this + " : " + e);
			}
			String msg = ac.getVisibleName() + e.getTgtConditionKey().getStartMsgI18Nd();
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	指定IDのPCがいれば正気度ダメージ(false) {

		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で最大Xの正気度ダメージを与える, (int) (event.getP() * 100) + "%", ((int) event.getValue()) + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.正気度ダメージの術式));
			sb.append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			if ((int) e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max damage) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			Actor ac = GameSystem.getInstance().getPCbyID(e.getTgtID());
			int val = Random.randomAbsInt((int) e.getValue()) + 1;
			ac.getStatus().getBaseStatus().get(StatusKey.正気度).add(val);
			String msg = I18N.get(GameSystemI18NKeys.XはXの正気度ダメージを受けた, ac.getVisibleName(), (int) e.getValue());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTノックバック_弱(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は最大48ノックバックする, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.弱ノックバックの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			KVector buf = tgt.getSprite().getVector();

			tgt.getSprite().setVector(new KVector(user.getSprite().getCenter(), tgt.getSprite().getCenter()));
			tgt.getSprite().getVector().speed = 1;

			for (int i = 0; i < 48; i++) {
				Point2D.Float newC = tgt.getSprite().simulateMove();
				if (BattleSystem.getInstance().getBattleFieldSystem().hitObstacle(newC)) {
					break;
				}
				if (!BattleSystem.getInstance().getBattleFieldSystem().getBattleFieldAllArea().contains(newC)) {
					break;
				}
				tgt.getSprite().move();
			}
			tgt.getSprite().setVector(buf);
			String msg = I18N.get(GameSystemI18NKeys.Xはノックバックした, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTノックバック_中(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は最大116ノックバックする, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.中ノックバックの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			KVector buf = tgt.getSprite().getVector();

			tgt.getSprite().setVector(new KVector(user.getSprite().getCenter(), tgt.getSprite().getCenter()));
			tgt.getSprite().getVector().speed = 1;

			for (int i = 0; i < 116; i++) {
				Point2D.Float newC = tgt.getSprite().simulateMove();
				if (BattleSystem.getInstance().getBattleFieldSystem().hitObstacle(newC)) {
					break;
				}
				if (!BattleSystem.getInstance().getBattleFieldSystem().getBattleFieldAllArea().contains(newC)) {
					break;
				}
				tgt.getSprite().move();
			}
			tgt.getSprite().setVector(buf);
			String msg = I18N.get(GameSystemI18NKeys.Xはノックバックした, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTノックバック_強(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は最大255ノックバックする, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.強ノックバックの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent) {
			KVector buf = tgt.getSprite().getVector();

			tgt.getSprite().setVector(new KVector(user.getSprite().getCenter(), tgt.getSprite().getCenter()));
			tgt.getSprite().getVector().speed = 1;

			for (int i = 0; i < 255; i++) {
				Point2D.Float newC = tgt.getSprite().simulateMove();
				if (BattleSystem.getInstance().getBattleFieldSystem().hitObstacle(newC)) {
					break;
				}
				if (!BattleSystem.getInstance().getBattleFieldSystem().getBattleFieldAllArea().contains(newC)) {
					break;
				}
				tgt.getSprite().move();
			}
			tgt.getSprite().setVector(buf);
			String msg = I18N.get(GameSystemI18NKeys.Xはノックバックした, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},;

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public abstract String getEventDescI18Nd(ActionEvent e);

	public abstract String getPageDescI18Nd(ActionEvent e);

	private boolean ATKCOUNT回数実行するイベント = false;

	private ActionEventType(boolean ATKCOUNT回数実行するイベント) {
		this.ATKCOUNT回数実行するイベント = ATKCOUNT回数実行するイベント;
	}

	public boolean isATKCOUNT回数実行するイベント() {
		return ATKCOUNT回数実行するイベント;
	}

	public abstract void pack(ActionEvent e, Action a) throws GameSystemException;

	public abstract void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult res, boolean isUserEvent);

	private static void addResult(ActionResult res, ActionResultSummary s, Actor user, Actor tgt, ActionEvent e, String msg, boolean isUserEvent) {
		if (isUserEvent) {
			res.addUserEventResult(getUserEventResult(s, user, e, msg));
		} else {
			res.setPerEvent(getPerEvent(s, user, tgt, e, msg));
		}
	}

	//これの戻り値をarに入れる。派生イベント結果は独自にいれよ。
	private static ActionResult.UserEventResult getUserEventResult(ActionResultSummary s, Actor user, ActionEvent e, String msg) {
		ActionResult.UserEventResult r = new ActionResult.UserEventResult(e, s, user);
		user.getStatus().addWhen0Condition();
		if (s.is成功()) {
			//ERへのアニメーションなどのセット
			if (e.getTgtAnimation() != null) {
				r.tgtAnimation = e.getTgtAnimation().clone();
				r.tgtAnimation.setLocationByCenter(user.getSprite().getCenter());
				r.tgtAnimation.getAnimation().setRepeat(false);
			}
			if (e.getOtherAnimation() != null) {
				r.otherAnimation = e.getOtherAnimation().clone();
				r.otherAnimation.setLocation(0, 0);
				r.otherAnimation.getAnimation().setRepeat(false);
			}
			if (e.getUserAnimation() != null) {
				r.userAnimation = e.getUserAnimation().clone();
				r.userAnimation.setLocation(user.getSprite().getCenter());
				r.userAnimation.getAnimation().setRepeat(false);
			}
			StatusValueSet tgtVs = user.getStatus().getDamageFromSavePoint();
			if (tgtVs.contains(StatusKey.体力)) {
				r.tgtDamageHp = (int) tgtVs.get(StatusKey.体力).getValue();
			}
			if (tgtVs.contains(StatusKey.魔力)) {
				r.tgtDamageMp = (int) tgtVs.get(StatusKey.魔力).getValue();
			}
			if (tgtVs.contains(StatusKey.正気度)) {
				r.tgtDamageSAN = (int) tgtVs.get(StatusKey.正気度).getValue();
			}
			r.is損壊 = user.getStatus().hasCondition(損壊);
			r.is気絶 = user.getStatus().hasCondition(気絶);
			r.is解脱 = user.getStatus().hasCondition(解脱);
			r.msgI18Nd = msg;
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
		}
		return r;
	}

	//これの戻り値をarに入れる。派生イベント結果は独自にいれよ。
	private static ActionResult.PerEvent getPerEvent(ActionResultSummary s, Actor user, Actor tgt, ActionEvent e, String msg) {
		ActionResult.EventActorResult r = new ActionResult.EventActorResult(tgt, e);
		tgt.getStatus().addWhen0Condition();
		if (s.is成功()) {
			//ERへのアニメーションなどのセット
			if (e.getTgtAnimation() != null) {
				r.tgtAnimation = e.getTgtAnimation().clone();
				r.tgtAnimation.setLocationByCenter(tgt.getSprite().getCenter());
				r.tgtAnimation.getAnimation().setRepeat(false);
			}
			if (e.getOtherAnimation() != null) {
				r.otherAnimation = e.getOtherAnimation().clone();
				r.otherAnimation.setLocation(0, 0);
				r.otherAnimation.getAnimation().setRepeat(false);
			}
			if (e.getUserAnimation() != null) {
				r.userAnimation = e.getUserAnimation().clone();
				r.userAnimation.setLocation(user.getSprite().getCenter());
				r.userAnimation.getAnimation().setRepeat(false);
			}
			StatusValueSet tgtVs = tgt.getStatus().getDamageFromSavePoint();
			if (tgtVs.contains(StatusKey.体力)) {
				r.tgtDamageHp = (int) tgtVs.get(StatusKey.体力).getValue();
			}
			if (tgtVs.contains(StatusKey.魔力)) {
				r.tgtDamageMp = (int) tgtVs.get(StatusKey.魔力).getValue();
			}
			if (tgtVs.contains(StatusKey.正気度)) {
				r.tgtDamageSAN = (int) tgtVs.get(StatusKey.正気度).getValue();
			}
			r.is損壊 = tgt.getStatus().hasCondition(損壊);
			r.is気絶 = tgt.getStatus().hasCondition(気絶);
			r.is解脱 = tgt.getStatus().hasCondition(解脱);
			r.msgI18Nd = msg;
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
		}
		return new ActionResult.PerEvent(e, s, Map.of(tgt, r));
	}

}
