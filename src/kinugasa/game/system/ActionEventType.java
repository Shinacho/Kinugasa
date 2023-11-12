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
import kinugasa.util.Random;
import kinugasa.util.StringUtil;

/**
 *
 * @vesion 1.0.0 - 2023/11/04_12:34:27<br>
 * @author Shinacho<br>
 */
public enum ActionEventType {
	ステータス攻撃(false, true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			switch (event.getCalcMode()) {
				case DC: {
					sb.append("  ").append(I18N.get(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる));
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
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			if (event.getCalcMode() == ActionEvent.CalcMode.DC) {
				sb.append("[");
				sb.append(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる);
				sb.append("]");
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
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			StatusKey tgtStatusKey = e.getTgtStatusKey();
			StatusValueSet tgtStatus = tgt.getStatus().getEffectedStatus();
			tgt.getStatus().saveBeforeDamageCalc();

			float value = e.getValue();
			float prev = tgtStatus.get(tgtStatusKey).getValue();
			switch (e.getCalcMode()) {
				case ADD: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case MUL: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO_MAX: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO_ZERO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
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
					return convert(r, e);
				}
				default:
					throw new AssertionError("undefined calc mode");
			}//switch
		}

	},
	ステータス回復(false, true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXを回復する, (int) (event.getP() * 100) + "%", event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			switch (event.getCalcMode()) {
				case DC: {
					sb.append("  ").append(I18N.get(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる));
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
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			if (event.getCalcMode() == ActionEvent.CalcMode.DC) {
				sb.append("[");
				sb.append(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる);
				sb.append("]");
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
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			StatusKey tgtStatusKey = e.getTgtStatusKey();
			StatusValueSet tgtStatus = tgt.getStatus().getEffectedStatus();
			tgt.getStatus().saveBeforeDamageCalc();

			float value = e.getValue();
			float prev = tgtStatus.get(tgtStatusKey).getValue();
			switch (e.getCalcMode()) {
				case ADD: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case MUL: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO_MAX: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO_ZERO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
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
						dcs = actionType == DamageCalcSystem.ActionType.物理回復 ? StatusKey.器用さ : StatusKey.信仰;
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
					return convert(r, e);
				}
				default:
					throw new AssertionError("undefined calc mode");
			}//switch
		}
	},
	前イベ成功時_ステータス攻撃(true, true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			switch (event.getCalcMode()) {
				case DC: {
					sb.append("  ").append(I18N.get(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる));
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
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			if (event.getCalcMode() == ActionEvent.CalcMode.DC) {
				sb.append("[");
				sb.append(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる);
				sb.append("]");
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
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			StatusKey tgtStatusKey = e.getTgtStatusKey();
			StatusValueSet tgtStatus = tgt.getStatus().getEffectedStatus();
			tgt.getStatus().saveBeforeDamageCalc();

			float value = e.getValue();
			float prev = tgtStatus.get(tgtStatusKey).getValue();
			switch (e.getCalcMode()) {
				case ADD: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case MUL: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO_MAX: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO_ZERO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
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
					return convert(r, e);
				}
				default:
					throw new AssertionError("undefined calc mode");
			}//switch
		}

	},
	前イベ成功時_ステータス回復(true, true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXを回復する, (int) (event.getP() * 100) + "%", event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			switch (event.getCalcMode()) {
				case DC: {
					sb.append("  ").append(I18N.get(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる));
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
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			if (event.getCalcMode() == ActionEvent.CalcMode.DC) {
				sb.append("[");
				sb.append(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる);
				sb.append("]");
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
			if (e.getAtkAttr() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATK_ATTRが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			StatusKey tgtStatusKey = e.getTgtStatusKey();
			StatusValueSet tgtStatus = tgt.getStatus().getEffectedStatus();
			tgt.getStatus().saveBeforeDamageCalc();

			float value = e.getValue();
			float prev = tgtStatus.get(tgtStatusKey).getValue();
			switch (e.getCalcMode()) {
				case ADD: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case MUL: {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO_MAX: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
				}
				case TO_ZERO: {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
					return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt, e);
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
						dcs = actionType == DamageCalcSystem.ActionType.物理回復 ? StatusKey.器用さ : StatusKey.信仰;
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
					return convert(r, e);
				}
				default:
					throw new AssertionError("undefined calc mode");
			}//switch
		}
	},
	ATTR_IN(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			tgt.getStatus().getAttrIn().get(e.getTgtAttrIn()).add(e.getValue());
			return getResult(true, tgt, e);
		}

	},
	ATTR_OUT(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			tgt.getStatus().getAttrOut().get(e.getTgtAttrOut()).add(e.getValue());
			return getResult(true, tgt, e);
		}
	},
	前イベ成功時_ATTR_IN(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			tgt.getStatus().getAttrIn().get(e.getTgtAttrIn()).add(e.getValue());
			return getResult(true, tgt, e);
		}

	},
	前イベ成功時_ATTR_OUT(true, false) {
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
			if (e.getTgtAttrOut() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはATTROUTが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			tgt.getStatus().getAttrOut().get(e.getTgtAttrOut()).add(e.getValue());
			return getResult(true, tgt, e);
		}
	},
	CND_REGIST(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			float v = tgt.getStatus().getConditionRegist().get(e.getTgtCndRegist());
			tgt.getStatus().getConditionRegist().put(e.getTgtCndRegist(), v + e.getValue());
			return getResult(true, tgt, e);
		}
	},
	前イベ成功時_CND_REGIST(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			float v = tgt.getStatus().getConditionRegist().get(e.getTgtCndRegist());
			tgt.getStatus().getConditionRegist().put(e.getTgtCndRegist(), v + e.getValue());
			return getResult(true, tgt, e);
		}
	},
	状態異常付与(false, false) {
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
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String msg = tgt.getStatus().addCondition(e.getTgtConditionKey(), e.getCndTime());
			ActionResult.EventResult r = getResult(msg != null, tgt, e);
			r.msgI18Nd = msg;
			return r;
		}

	},
	状態異常解除(false, false) {
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
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String msg = tgt.getStatus().removeCondition(e.getTgtConditionKey());
			ActionResult.EventResult r = getResult(msg != null, tgt, e);
			r.msgI18Nd = msg;
			return r;
		}
	},
	前イベ成功時_状態異常付与(true, false) {
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
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String msg = tgt.getStatus().addCondition(e.getTgtConditionKey(), e.getCndTime());
			ActionResult.EventResult r = getResult(msg != null, tgt, e);
			r.msgI18Nd = msg;
			return r;
		}

	},
	前イベ成功時_状態異常解除(true, false) {
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
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String msg = tgt.getStatus().removeCondition(e.getTgtConditionKey());
			ActionResult.EventResult r = getResult(msg != null, tgt, e);
			r.msgI18Nd = msg;
			return r;
		}
	},
	アイテム追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			if (e.isNoLimit() || tgt.getStatus().getItemBag().canAdd()) {
				tgt.getStatus().getItemBag().add(i);
			}
			ActionResult.EventResult ae = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			if (e.getOtherAnimation() != null) {
				ae.otherAnimation = e.getOtherAnimation().clone();
			}
			if (e.getTgtAnimation() != null) {
				ae.tgtAnimation = e.getTgtAnimation().clone();
			}
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
			return ae;
		}
	},
	アイテムロスト(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			tgt.getStatus().getItemBag().drop(i);
			ActionResult.EventResult ae = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			if (e.getOtherAnimation() != null) {
				ae.otherAnimation = e.getOtherAnimation().clone();
			}
			if (e.getTgtAnimation() != null) {
				ae.tgtAnimation = e.getTgtAnimation().clone();
			}
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
			return ae;
		}
	},
	前イベ成功時_アイテム追加(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			if (e.isNoLimit() || tgt.getStatus().getItemBag().canAdd()) {
				tgt.getStatus().getItemBag().add(i);
			}
			ActionResult.EventResult ae = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			if (e.getOtherAnimation() != null) {
				ae.otherAnimation = e.getOtherAnimation().clone();
			}
			if (e.getTgtAnimation() != null) {
				ae.tgtAnimation = e.getTgtAnimation().clone();
			}
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
			return ae;
		}
	},
	前イベ成功時_アイテムロスト(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			tgt.getStatus().getItemBag().drop(i);
			ActionResult.EventResult ae = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			if (e.getOtherAnimation() != null) {
				ae.otherAnimation = e.getOtherAnimation().clone();
			}
			if (e.getTgtAnimation() != null) {
				ae.tgtAnimation = e.getTgtAnimation().clone();
			}
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
			return ae;
		}
	},
	ドロップアイテム追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			//ドロップアイテムに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.itemOf(i, 1, 1f));
				return getResult(true, tgt, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	ドロップマテリアル追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Material m = MaterialStorage.getInstance().get(e.getTgtID());
			//ドロップアイテムに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.materialOf(m, 1, 1f));
				return getResult(true, tgt, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	前イベ成功時_ドロップアイテム追加(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			//ドロップアイテムに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.itemOf(i, 1, 1f));
				return getResult(true, tgt, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	前イベ成功時_ドロップマテリアル追加(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Material m = MaterialStorage.getInstance().get(e.getTgtID());
			//ドロップアイテムに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.materialOf(m, 1, 1f));
				return getResult(true, tgt, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	ユーザの武器をドロップしてドロップアイテムに追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
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

			//ドロップアイテムに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.itemOf(tgtItem, 1, 1f));
			} else {
				//PCの場合アイテムバッグに追加、追加できなかったらロストする
				if (tgt.getStatus().getItemBag().canAdd()) {
					tgt.getStatus().getItemBag().add(tgtItem);
				}
			}
			return getResult(true, tgt, e);
		}
	},
	前イベ成功時_ユーザの武器をドロップしてドロップアイテムに追加(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
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

			//ドロップアイテムに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.itemOf(tgtItem, 1, 1f));
			} else {
				//PCの場合アイテムバッグに追加、追加できなかったらロストする
				if (tgt.getStatus().getItemBag().canAdd()) {
					tgt.getStatus().getItemBag().add(tgtItem);
				}
			}
			return getResult(true, tgt, e);
		}
	},
	TGTの行動をVALUE回数この直後に追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			for (int i = 0; i < (int) e.getValue(); i++) {
				BattleCommand cmd = new BattleCommand(user);
				if (user.isPlayer()) {
					cmd.setUserOperation(true);
				}
				BattleSystem.getInstance().addCmdFirst(cmd);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);

		}

	},
	前イベ成功時_TGTの行動をVALUE回数この直後に追加(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			for (int i = 0; i < (int) e.getValue(); i++) {
				BattleCommand cmd = new BattleCommand(user);
				if (user.isPlayer()) {
					cmd.setUserOperation(true);
				}
				BattleSystem.getInstance().addCmdFirst(cmd);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	TGTの行動をVALUE回数ターン最後に追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			for (int i = 0; i < (int) e.getValue(); i++) {
				BattleCommand cmd = new BattleCommand(user);
				if (user.isPlayer()) {
					cmd.setUserOperation(true);
				}
				BattleSystem.getInstance().addCmdLast(cmd);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_TGTの行動をVALUE回数ターン最後に追加(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			for (int i = 0; i < (int) e.getValue(); i++) {
				BattleCommand cmd = new BattleCommand(user);
				if (user.isPlayer()) {
					cmd.setUserOperation(true);
				}
				BattleSystem.getInstance().addCmdLast(cmd);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の同じチームの全員にも適用(false, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でこのアクションはターゲットからXの距離内の同じチームの全員にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.放射の術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Actor> newTgts = new ArrayList<>();
			if (tgt.isPlayer()) {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			} else {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			}
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			ActionResult.EventResult res = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !this.equals(p)).toList()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						res.list.add(ae.exec(user, a, newTgt, resOfThisTgt));
					}
				}
			}
			return res;
		}
	},
	前イベ成功時_このアクションの他のイベントをこのイベントのTGTからVALUE内の同じチームの全員にも適用(true, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でこのアクションはターゲットからXの距離内の同じチームの全員にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.放射の術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Actor> newTgts = new ArrayList<>();
			if (tgt.isPlayer()) {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			} else {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			}
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			ActionResult.EventResult res = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !this.equals(p)).toList()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						res.list.add(ae.exec(user, a, newTgt, resOfThisTgt));
					}
				}
			}
			return res;
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の全員にも適用(false, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でこのアクションはターゲットからXの距離内の全員にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.全体放射の術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			ActionResult.EventResult res = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !this.equals(p)).toList()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						res.list.add(ae.exec(user, a, newTgt, resOfThisTgt));
					}
				}
			}
			return res;
		}
	},
	前イベ成功時_このアクションの他のイベントをこのイベントのTGTからVALUE内の全員にも適用(true, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でこのアクションはターゲットからXの距離内の全員にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.全体放射の術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			ActionResult.EventResult res = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !this.equals(p)).toList()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						res.list.add(ae.exec(user, a, newTgt, resOfThisTgt));
					}
				}
			}
			return res;
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の同じチームの一人にも適用(false, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でこのアクションはターゲットからXの距離内の同じチームの一人にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.派生の術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Actor> newTgts = new ArrayList<>();
			if (tgt.isPlayer()) {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			} else {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			}
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			newTgts = new ArrayList<>(List.of(newTgts.get(0)));
			ActionResult.EventResult res = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !this.equals(p)).toList()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						res.list.add(ae.exec(user, a, newTgt, resOfThisTgt));
					}
				}
			}
			return res;
		}
	},
	前イベ成功時_このアクションの他のイベントをこのイベントのTGTからVALUE内の同じチームの一人にも適用(true, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でこのアクションはターゲットからXの距離内の同じチームの一人にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.派生の術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Actor> newTgts = new ArrayList<>();
			if (tgt.isPlayer()) {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			} else {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			}
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			newTgts = new ArrayList<>(List.of(newTgts.get(0)));
			ActionResult.EventResult res = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !this.equals(p)).toList()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						res.list.add(ae.exec(user, a, newTgt, resOfThisTgt));
					}
				}
			}
			return res;
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の一人にも適用(false, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でこのアクションはターゲットからXの距離内の一人にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.伝搬の術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			newTgts = new ArrayList<>(List.of(newTgts.get(0)));
			ActionResult.EventResult res = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !this.equals(p)).toList()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						res.list.add(ae.exec(user, a, newTgt, resOfThisTgt));
					}
				}
			}
			return res;
		}
	},
	前イベ成功時_このアクションの他のイベントをこのイベントのTGTからVALUE内の一人にも適用(true, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でこのアクションはターゲットからXの距離内の一人にも作用する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.伝搬の術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			newTgts = new ArrayList<>(List.of(newTgts.get(0)));
			ActionResult.EventResult res = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream().filter(p -> !this.equals(p)).toList()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						res.list.add(ae.exec(user, a, newTgt, resOfThisTgt));
					}
				}
			}
			return res;
		}
	},
	TGTの行動を未行動ならこの直後に移動(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = null;
			for (BattleCommand c : BattleSystem.getInstance().getCommandsOfThisTurn()) {
				if (tgt.equals(c.getUser())) {
					cmd = c;
					break;
				}
			}
			if (cmd != null) {
				BattleSystem.getInstance().moveToFirst(cmd);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}

	},
	前イベ成功時_TGTの行動を未行動ならこの直後に移動(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = null;
			for (BattleCommand c : BattleSystem.getInstance().getCommandsOfThisTurn()) {
				if (tgt.equals(c.getUser())) {
					cmd = c;
					break;
				}
			}
			if (cmd != null) {
				BattleSystem.getInstance().moveToFirst(cmd);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}

	},
	TGTの行動を未行動ならターン最後に移動(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = null;
			for (BattleCommand c : BattleSystem.getInstance().getCommandsOfThisTurn()) {
				if (tgt.equals(c.getUser())) {
					cmd = c;
					break;
				}
			}
			if (cmd != null) {
				BattleSystem.getInstance().moveToLast(cmd);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	前イベ成功時_TGTの行動を未行動ならターン最後に移動(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = null;
			for (BattleCommand c : BattleSystem.getInstance().getCommandsOfThisTurn()) {
				if (tgt.equals(c.getUser())) {
					cmd = c;
					break;
				}
			}
			if (cmd != null) {
				BattleSystem.getInstance().moveToLast(cmd);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	TGTの魔法詠唱を中断(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			boolean f = BattleSystem.getInstance().魔法詠唱を破棄(tgt);
			return f
					? new ActionResult.EventResult(tgt, ActionResultSummary.成功, e)
					: new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	前イベ成功時_TGTの魔法詠唱を中断(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			boolean f = BattleSystem.getInstance().魔法詠唱を破棄(tgt);
			return f
					? new ActionResult.EventResult(tgt, ActionResultSummary.成功, e)
					: new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	TGTの魔法詠唱完了をVALUEターン分ずらす(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (tgt.getStatus().hasCondition(ConditionKey.詠唱中)) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
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
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_TGTの魔法詠唱完了をVALUEターン分ずらす(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (tgt.getStatus().hasCondition(ConditionKey.詠唱中)) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
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
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	USERのクローンをパーティーまたはENEMYに追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (tgt instanceof Enemy) {
				Enemy ee = Enemy.cloneOf(tgt);
				BattleSystem.getInstance().getEnemies().add(ee);
			} else {
				Actor newA = new Actor(tgt.getId() + "_CLONE", tgt);
				newA.setSummoned(true);
				GameSystem.getInstance().getParty().add(newA);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	前イベ成功時_USERのクローンをパーティーまたはENEMYに追加(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (tgt instanceof Enemy) {
				Enemy ee = Enemy.cloneOf(tgt);
				BattleSystem.getInstance().getEnemies().add(ee);
			} else {
				Actor newA = new Actor(tgt.getId() + "_CLONE", tgt);
				newA.setSummoned(true);
				GameSystem.getInstance().getParty().add(newA);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	このターンのTGTの行動をこの次にする(false, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者の行動順を早める, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ヘイストの術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = BattleSystem.getInstance().getCommandsOfThisTurn().stream().filter(p -> p.getUser().equals(tgt)).findFirst().orElse(null);
			if (cmd == null) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			BattleSystem.getInstance().getCommandsOfThisTurn().remove(cmd);
			BattleSystem.getInstance().addCmdFirst(cmd);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	前イベ成功時_このターンのTGTの行動をこの次にする(true, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者の行動順を早める, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ヘイストの術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = BattleSystem.getInstance().getCommandsOfThisTurn().stream().filter(p -> p.getUser().equals(tgt)).findFirst().orElse(null);
			if (cmd == null) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			BattleSystem.getInstance().getCommandsOfThisTurn().remove(cmd);
			BattleSystem.getInstance().addCmdFirst(cmd);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	このターンのTGTの行動を破棄(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = BattleSystem.getInstance().getCommandsOfThisTurn().stream().filter(p -> p.getUser().equals(tgt)).findFirst().orElse(null);
			if (cmd == null) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			BattleSystem.getInstance().getCommandsOfThisTurn().remove(cmd);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_このターンのTGTの行動を破棄(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = BattleSystem.getInstance().getCommandsOfThisTurn().stream().filter(p -> p.getUser().equals(tgt)).findFirst().orElse(null);
			if (cmd == null) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			BattleSystem.getInstance().getCommandsOfThisTurn().remove(cmd);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	このターンのTGTの行動を最後にする(false, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は行動がそのターンの最後になる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.スローの術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = BattleSystem.getInstance().getCommandsOfThisTurn().stream().filter(p -> p.getUser().equals(tgt)).findFirst().orElse(null);
			if (cmd == null) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			BattleSystem.getInstance().getCommandsOfThisTurn().remove(cmd);
			BattleSystem.getInstance().addCmdLast(cmd);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_このターンのTGTの行動を最後にする(true, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は行動がそのターンの最後になる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.スローの術式));
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleCommand cmd = BattleSystem.getInstance().getCommandsOfThisTurn().stream().filter(p -> p.getUser().equals(tgt)).findFirst().orElse(null);
			if (cmd == null) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			BattleSystem.getInstance().getCommandsOfThisTurn().remove(cmd);
			BattleSystem.getInstance().addCmdLast(cmd);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	このターンの行動順を反転させる(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			LinkedList<BattleCommand> cmd = BattleSystem.getInstance().getCommandsOfThisTurn();
			Collections.reverse(cmd);
			BattleSystem.getInstance().setCommandsOfThisTurn(cmd);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_このターンの行動順を反転させる(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			LinkedList<BattleCommand> cmd = BattleSystem.getInstance().getCommandsOfThisTurn();
			Collections.reverse(cmd);
			BattleSystem.getInstance().setCommandsOfThisTurn(cmd);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	TGTを中心位置からVALUEの場所に転送(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			//中心位置の取得
			Point2D.Float center = BattleSystem.getInstance().getBattleFieldSystem().getBattleFieldAllArea().getCenter();
			Point2D.Float p = Random.randomLocation(center, e.getValue());
			tgt.getSprite().setLocationByCenter(p);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_TGTを中心位置からVALUEの場所に転送(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			//中心位置の取得
			Point2D.Float center = BattleSystem.getInstance().getBattleFieldSystem().getBattleFieldAllArea().getCenter();
			Point2D.Float p = Random.randomLocation(center, e.getValue());
			tgt.getSprite().setLocationByCenter(p);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	TGTを術者の近くに転送(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			//位置の取得
			Point2D.Float center = user.getSprite().getCenter();
			Point2D.Float p = Random.randomLocation(center, 64f);
			tgt.getSprite().setLocationByCenter(p);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_TGTを術者の近くに転送(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			//位置の取得
			Point2D.Float center = user.getSprite().getCenter();
			Point2D.Float p = Random.randomLocation(center, 64f);
			tgt.getSprite().setLocationByCenter(p);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	TGTを逃げられる位置に転送(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			//現在位置の取得
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
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	前イベ成功時_TGTを逃げられる位置に転送(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			//現在位置の取得
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
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	TGTを一番近い敵対者の至近距離に転送(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_TGTを一番近い敵対者の至近距離に転送(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	USERをTGTの至近距離に転送(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Point2D.Float p = Random.randomLocation(tgt.getSprite().getCenter(), 64);
			user.getSprite().setLocationByCenter(p);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_USERをTGTの至近距離に転送(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Point2D.Float p = Random.randomLocation(tgt.getSprite().getCenter(), 64);
			user.getSprite().setLocationByCenter(p);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	USERとTGTの位置を交換(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Point2D.Float p = (Point2D.Float) tgt.getSprite().getCenter().clone();
			tgt.getSprite().setLocationByCenter(user.getSprite().getCenter());
			user.getSprite().setLocationByCenter(p);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_USERとTGTの位置を交換(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Point2D.Float p = (Point2D.Float) tgt.getSprite().getCenter().clone();
			tgt.getSprite().setLocationByCenter(user.getSprite().getCenter());
			user.getSprite().setLocationByCenter(p);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	TGTIDのCSVにあるアイテムのいずれかをUSERに追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String id = Random.randomChoice(StringUtil.safeSplit(e.getTgtID(), ","));
			if (user.getStatus().getItemBag().canAdd()) {
				//追加
				user.getStatus().getItemBag().add(ActionStorage.getInstance().itemOf(id));
			} else if (e.isNoLimit()) {
				//追加
				user.getStatus().getItemBag().add(ActionStorage.getInstance().itemOf(id));
			} else {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_TGTIDのCSVにあるアイテムのいずれかをUSERに追加(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String id = Random.randomChoice(StringUtil.safeSplit(e.getTgtID(), ","));
			if (user.getStatus().getItemBag().canAdd()) {
				//追加
				user.getStatus().getItemBag().add(ActionStorage.getInstance().itemOf(id));
			} else if (e.isNoLimit()) {
				//追加
				user.getStatus().getItemBag().add(ActionStorage.getInstance().itemOf(id));
			} else {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	逃走で戦闘終了(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			BattleSystem.getInstance().setEndStatus(BattleResult.敗北_こちらが全員逃げた);
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	前イベ成功時_逃走で戦闘終了(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (GameSystem.getInstance().getMode() == GameMode.BATTLE) {
				BattleSystem.getInstance().setEndStatus(BattleResult.敗北_こちらが全員逃げた);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}

	},
	TGTIDのマップIDの座標に転送(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String[] val = StringUtil.safeSplit(e.getTgtID(), ",");
			String tgtMapId = val[0];
			int x = Integer.parseInt(val[1]);
			int y = Integer.parseInt(val[2]);
			FieldMap.getCurrentInstance().changeMap(Node.ofOutNode("AUTO_AE_NODE", tgtMapId, x, y, FourDirection.SOUTH));
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	カレントマップのランダムな出口ノードに転送(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Node n = FieldMap.getCurrentInstance().getNodeStorage().random();
			FieldMap.getCurrentInstance().setCurrentIdx(n.getIdx());
			FieldMap.getCurrentInstance().getCamera().updateToCenter();
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	友好的な存在の召喚(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (user.isPlayer()) {
				Actor ac = new Actor(e.getTgtID());
				ac.setSummoned(true);
				GameSystem.getInstance().getParty().add(ac);
			} else {
				Enemy ac = new Enemy(e.getTgtID());
				ac.setSummoned(true);
				BattleSystem.getInstance().getEnemies().add(ac);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	前イベ成功時_友好的な存在の召喚(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (user.isPlayer()) {
				Actor ac = new Actor(e.getTgtID());
				ac.setSummoned(true);
				GameSystem.getInstance().getParty().add(ac);
			} else {
				Enemy ac = new Enemy(e.getTgtID());
				ac.setSummoned(true);
				BattleSystem.getInstance().getEnemies().add(ac);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	敵対的な存在の召喚(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (user.isPlayer()) {
				Enemy ac = new Enemy(e.getTgtID());
				ac.setSummoned(true);
				BattleSystem.getInstance().getEnemies().add(ac);
			} else {
				Actor ac = new Actor(e.getTgtID());
				ac.setSummoned(true);
				GameSystem.getInstance().getParty().add(ac);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	前イベ成功時_敵対的な存在の召喚(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			if (user.isPlayer()) {
				Enemy ac = new Enemy(e.getTgtID());
				ac.setSummoned(true);
				BattleSystem.getInstance().getEnemies().add(ac);
			} else {
				Actor ac = new Actor(e.getTgtID());
				ac.setSummoned(true);
				GameSystem.getInstance().getParty().add(ac);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	カレントセーブデータロスト(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new UnsupportedOperationException("TODO:未実装");
		}

	},
	前イベ成功時_カレントセーブデータロスト(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new UnsupportedOperationException("TODO:未実装");
		}

	},
	ゲームクラッシュ(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Object ぬるぽ = null;
			ぬるぽ.toString();
			throw new InternalError();
		}

	},
	前イベ成功時_ゲームクラッシュ(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Object ぬるぽ = null;
			ぬるぽ.toString();
			throw new InternalError();
		}

	},
	カレント以外のセーブデータを１つロスト(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new UnsupportedOperationException("TODO:未実装");
		}
	},
	前イベ成功時_カレント以外のセーブデータを１つロスト(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new UnsupportedOperationException("TODO:未実装");
		}
	},
	セーブデータ全ロスト(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new UnsupportedOperationException("TODO:未実装");
		}
	},
	前イベ成功時_セーブデータ全ロスト(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new UnsupportedOperationException("TODO:未実装");
		}
	},
	独自効果(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.独自効果イベントがオーバーライドされていません) + " : " + this + " : " + e);
		}
	},
	前イベ成功時_独自効果(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.独自効果イベントがオーバーライドされていません) + " : " + this + " : " + e);
		}
	},
	前イベ成功時_ビームエフェクト(true, false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.このイベントがあると術者から対象者へビームを発射するアニメーションが追加される);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.このイベントがあると術者から対象者へビームを発射するアニメーションが追加される);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.独自効果イベントがオーバーライドされていません) + " : " + this + " : " + e);
		}
	},
	DC_ファイル選択からのハッシュ(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			JFileChooser c = new JFileChooser(new File(PlayerConstants.getInstance().DESKTOP_PATH));
			c.setMultiSelectionEnabled(false);
			int res = c.showOpenDialog(null);
			if (res != JFileChooser.APPROVE_OPTION) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
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
			return convert(r, e);

		}

	},
	DC_ファイル選択からのサイズ(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			JFileChooser c = new JFileChooser(new File(PlayerConstants.getInstance().DESKTOP_PATH));
			c.setMultiSelectionEnabled(false);
			int res = c.showOpenDialog(null);
			if (res != JFileChooser.APPROVE_OPTION) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
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
			return convert(r, e);

		}
	},
	DC_倒した敵の数(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Counts.Value v = Counts.getInstance().select(GameSystemI18NKeys.CountKey.倒した敵の数);
			if (v == null) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
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
			return convert(r, e);

		}

	},
	DC_ターン数が小さい(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);

		}
	},
	DC_ターン数が大きい(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);

		}
	},
	DC_CPUのコア数(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);

		}

	},
	DC_USERの持っているアイテムの重さ(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			List<Item> items = user.getStatus().getItemBag().getItems();
			float val = 0;
			for (Item i : items) {
				if (i.getDesc().contains(I18N.get(GameSystemI18NKeys.軽い))) {
					val -= 0.5;
				} else if (i.getDesc().contains(I18N.get(GameSystemI18NKeys.とてつもなく重い))) {
					val -= 32;
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
			return convert(r, e);
		}

	},
	詠唱完了イベントをVALUEターン内で反転(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	前イベ成功時_詠唱完了イベントをVALUEターン内で反転(true, false) {
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
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(n>1) : " + this + " : " + e);
			}
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	自身以外の全員の正気度にダメージ(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			for (Actor ac : Stream.of(GameSystem.getInstance().getParty(), BattleSystem.getInstance().getEnemies()).flatMap(p -> p.stream()).toList()) {
				if (!ac.equals(user)) {
					int val = Random.randomAbsInt((int) e.getValue()) + 1;
					ac.getStatus().getBaseStatus().get(StatusKey.正気度).add(val);
				}
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	前イベ成功時_自身以外の全員の正気度にダメージ(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			for (Actor ac : Stream.of(GameSystem.getInstance().getParty(), BattleSystem.getInstance().getEnemies()).flatMap(p -> p.stream()).toList()) {
				if (!ac.equals(user)) {
					int val = Random.randomAbsInt((int) e.getValue()) + 1;
					ac.getStatus().getBaseStatus().get(StatusKey.正気度).add(val);
				}
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	WEBサイト起動(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String tgtId = e.getTgtID();
			try {
				if (PlayerConstants.getInstance().OS_NAME.toLowerCase().contains("windows")) {
					Runtime.getRuntime().exec("cmd /q/c start " + tgtId);
				} else {
					Runtime.getRuntime().exec("open " + tgtId);
				}
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			} catch (IOException ex) {
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);

		}

	},
	DC_減っている体力(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);
		}
	},
	DC_減っている魔力(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);
		}
	},
	DC_減っている正気度(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);
		}
	},
	DC_残っている体力(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);
		}
	},
	DC_残っている魔力(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);
		}
	},
	DC_残っている正気度(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);
		}
	},
	USERによる指定IDの魔法の詠唱完了をこのターンの最後にVALUE回数追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			Action tgtA = ActionStorage.getInstance().actionOf(e.getTgtID());
			for (int i = 0; i < (int) e.getValue(); i++) {
				MagicSpell ms = new MagicSpell(user, tgtA, user.isPlayer());
				BattleSystem.getInstance().addCmdLast(ms);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	DC_ランダム属性のランダムダメージ(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return convert(r, e);
		}
	},
	USERの指定スロットの装備品の攻撃回数をVALUE上げる(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (user.getStatus().getEqip().keySet().contains(slot)) {
				Item i = user.getStatus().getEqip().get(slot);
				if (i == null) {
					return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
				}
				i.setAtkCount(i.getAtkCount() + (int) e.getValue());
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	前イベ成功時_USERの指定スロットの装備品の攻撃回数にVALUE追加(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (user.getStatus().getEqip().keySet().contains(slot)) {
				Item i = user.getStatus().getEqip().get(slot);
				if (i == null) {
					return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
				}
				int v = i.getAtkCount() + (int) e.getValue();
				if (v <= 0) {
					v = 1;
				}
				i.setAtkCount(v);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	USERの指定スロットの装備品の価値をVALUE倍にする(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (user.getStatus().getEqip().keySet().contains(slot)) {
				Item i = user.getStatus().getEqip().get(slot);
				if (i == null) {
					return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
				}
				int v = (int) (i.getPrice() * e.getValue());
				if (v < 0) {
					v = 0;
				}
				i.setPrice(v);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);

		}
	},
	前イベ成功時_USERの指定スロットの装備品の価値をVALUE倍にする(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (user.getStatus().getEqip().keySet().contains(slot)) {
				Item i = user.getStatus().getEqip().get(slot);
				if (i == null) {
					return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
				}
				i.setPrice((int) (i.getPrice() * e.getValue()));
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);

		}
	},
	TGTを即死させる(false, false) {
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
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return getResult(true, tgt, e);
		}

	},
	前イベ成功時_TGTを即死させる(true, false) {
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
		}

		@Override
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
			return getResult(true, tgt, e);
		}

	},
	パージ(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (tgt.getStatus().getEqip().keySet().contains(slot)) {
				tgt.getStatus().getEqip().put(slot, null);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}

	},
	前イベ成功時_パージ(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (tgt.getStatus().getEqip().keySet().contains(slot)) {
				tgt.getStatus().getEqip().put(slot, null);
				return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			}
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}

	},
	マップIDと座標を入力させて移動する(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
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
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
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
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			FieldMap.getCurrentInstance().changeMap(Node.ofOutNode("AUTO_NODE_FROM_AE", tgtMap.id,
					(Integer) x.getValue(), (Integer) y.getValue(), FourDirection.NORTH));
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}

	},
	ダミー＿成功(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			return new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
		}
	},
	ダミー＿失敗(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	ダミー＿前イベ成功時失敗(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
		}
	},
	メッセージ表示(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			ActionResult.EventResult r = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			r.msgI18Nd = e.getTgtID();
			return r;
		}

	},
	前イベ成功時_メッセージ表示(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			ActionResult.EventResult r = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			r.msgI18Nd = e.getTgtID();
			return r;
		}
	},
	フラグ参照メッセージ表示(false, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String[] val = StringUtil.safeSplit(e.getTgtID(), ",");
			String name = val[0];
			FlagStatus fs = FlagStatus.valueOf(val[1]);
			if (!FlagStorage.getInstance().get(name).get().is(fs)) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			ActionResult.EventResult r = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			r.msgI18Nd = val[2];
			return r;
		}

	},
	前イベ成功時_フラグ参照メッセージ表示(true, false) {
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
		public ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e) {
			String[] val = StringUtil.safeSplit(e.getTgtID(), ",");
			String name = val[0];
			FlagStatus fs = FlagStatus.valueOf(val[1]);
			if (!FlagStorage.getInstance().get(name).get().is(fs)) {
				return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, e);
			}
			ActionResult.EventResult r = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			r.msgI18Nd = val[2];
			return r;
		}
	};

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public abstract String getEventDescI18Nd(ActionEvent e);

	public abstract String getPageDescI18Nd(ActionEvent e);

	private boolean 前のイベントが成功したときだけ実施するイベント = false;
	private boolean ATKCOUNT回数実行するイベント = false;

	private ActionEventType(boolean 前のイベントが成功したときだけ実施するイベント, boolean ATKCOUNT回数実行するイベント) {
		this.前のイベントが成功したときだけ実施するイベント = 前のイベントが成功したときだけ実施するイベント;
		this.ATKCOUNT回数実行するイベント = ATKCOUNT回数実行するイベント;
	}

	public boolean is前のイベントが成功したときだけ実施するイベント() {
		return 前のイベントが成功したときだけ実施するイベント;
	}

	public boolean isATKCOUNT回数実行するイベント() {
		return ATKCOUNT回数実行するイベント;
	}

	public abstract void pack(ActionEvent e, Action a) throws GameSystemException;

	public abstract ActionResult.EventResult exec(Actor user, Action a, Actor tgt, List<ActionResult.EventResult> resOfThisTgt, ActionEvent e);

	private static ActionResult.EventResult getResult(boolean is成功, Actor tgt, ActionEvent e) {
		if (is成功) {
			tgt.getStatus().addWhen0Condition();
			ActionResult.EventResult er = new ActionResult.EventResult(tgt, ActionResultSummary.成功, e);
			//ERへのアニメーションなどのセット
			if (e.getTgtAnimation() != null) {
				er.tgtAnimation = e.getTgtAnimation().clone();
				er.tgtAnimation.setLocationByCenter(tgt.getSprite().getCenter());
				er.tgtAnimation.getAnimation().setRepeat(false);
			}
			if (e.getOtherAnimation() != null) {
				er.otherAnimation = e.getOtherAnimation().clone();
				er.otherAnimation.setLocation(0, 0);
				er.otherAnimation.getAnimation().setRepeat(false);
			}
			StatusValueSet tgtVs = tgt.getStatus().getDamageFromSavePoint();
			if (tgtVs.contains(StatusKey.体力)) {
				er.tgtDamageHp = (int) tgtVs.get(StatusKey.体力).getValue();
			}
			if (tgtVs.contains(StatusKey.魔力)) {
				er.tgtDamageMp = (int) tgtVs.get(StatusKey.魔力).getValue();
			}
			if (tgtVs.contains(StatusKey.正気度)) {
				er.tgtDamageSAN = (int) tgtVs.get(StatusKey.正気度).getValue();
			}
			er.tgtIsDead = tgt.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.損壊);
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
			return er;
		}
		return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿計算結果０, e);
	}

	private static ActionResult.EventResult convert(DamageCalcSystem.Result r, ActionEvent e) {
		r.param.tgt.getStatus().addWhen0Condition();
		ActionResult.EventResult er = new ActionResult.EventResult(
				r.param.tgt,
				r.summary,
				e);
		//ERへのアニメーションなどのセット
		if (r.summary == ActionResultSummary.成功
				|| r.summary == ActionResultSummary.成功＿クリティカル
				|| r.summary == ActionResultSummary.成功＿ブロックされたが１以上) {
			if (e.getTgtAnimation() != null) {
				er.tgtAnimation = e.getTgtAnimation().clone();
				er.tgtAnimation.setLocationByCenter(r.param.tgt.getSprite().getCenter());
				er.tgtAnimation.getAnimation().setRepeat(false);
			}
			if (e.getOtherAnimation() != null) {
				er.otherAnimation = e.getOtherAnimation().clone();
				er.otherAnimation.setLocation(0, 0);
				er.otherAnimation.getAnimation().setRepeat(false);
			}
			StatusValueSet tgtVs = r.param.tgt.getStatus().getDamageFromSavePoint();
			if (tgtVs.contains(StatusKey.体力)) {
				er.tgtDamageHp = (int) tgtVs.get(StatusKey.体力).getValue();
			}
			if (tgtVs.contains(StatusKey.魔力)) {
				er.tgtDamageMp = (int) tgtVs.get(StatusKey.魔力).getValue();
			}
			if (tgtVs.contains(StatusKey.正気度)) {
				er.tgtDamageSAN = (int) tgtVs.get(StatusKey.正気度).getValue();
			}
			er.tgtIsDead = r.param.tgt.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.損壊);
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
		}
		return er;
	}
}
