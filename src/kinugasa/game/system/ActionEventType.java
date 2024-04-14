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
import java.util.Arrays;
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
import kinugasa.game.GameOption;
import kinugasa.game.I18N;
import kinugasa.game.PlayerConstants;
import kinugasa.game.field4.FieldEventParser;
import kinugasa.game.field4.FieldEventSystem;
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
import kinugasa.graphics.ImageUtil;
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える,
					event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.ON,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			String msg = I18N.get(GameSystemI18NKeys.Xダメージの術式,
					event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.ON);
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
				case ADD -> {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
				}
				case MUL -> {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
				}
				case TO -> {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
				}
				case TO_MAX -> {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
				}
				case TO_ZERO -> {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
				}
				case DC -> {
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
					StatusKey dcs = ActionUtil.getDCS(a, user, actionType);

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
					String msg = ActionUtil.createResultMsg(tgt, tgtStatusKey, r);
					addResult(ar, r.summary, user, tgt, e, msg, isUserEvent);
					return;
				}
				default ->
					throw new AssertionError("undefined calc mode");
			}//switch

			String msg = ActionUtil.createMsg(tgt, tgtStatusKey);
			StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
			addResult(ar,
					v != null ? ActionResultSummary.成功 : ActionResultSummary.失敗＿不発,
					user, tgt, e, msg, isUserEvent);
		}

	},
	ステータス回復(true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx) {
			String msg = I18N.get(GameSystemI18NKeys.Xを回復する,
					event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.ON,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			String msg = I18N.get(GameSystemI18NKeys.X回復の術式,
					event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.ON);
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
				case ADD -> {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
				}
				case MUL -> {
					if (e.isNoLimit()) {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
					}
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
				}
				case TO -> {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
				}
				case TO_MAX -> {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
				}
				case TO_ZERO -> {
					tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
				}
				case DC -> {
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
					StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
					String msg = ActionUtil.createResultMsg(tgt, tgtStatusKey, r);
					addResult(ar, r.summary, user, tgt, e, msg, isUserEvent);
					return;
				}
				default ->
					throw new AssertionError("undefined calc mode");
			}//switch
			String msg = ActionUtil.createMsg(tgt, tgtStatusKey);
			StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
			addResult(ar,
					v != null ? ActionResultSummary.成功 : ActionResultSummary.失敗＿不発,
					user, tgt, e, msg, isUserEvent);
		}

	},
	ステータス攻撃TGTID回実施(true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える,
					event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.ON,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.X回発動する, event.getTgtID()));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			String msg = I18N.get(GameSystemI18NKeys.XダメージX回の術式,
					event.getTgtStatusKey().getVisibleName(), event.getTgtID());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.ON,
					I18N.get(GameSystemI18NKeys.X回発動する, event.getTgtID()));
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
			try {
				int i = Integer.parseInt(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a, Actor tgt, ActionEvent e, ActionResult ar, boolean isUserEvent) {
			StatusKey tgtStatusKey = e.getTgtStatusKey();
			tgt.getStatus().saveBeforeDamageCalc();

			float value = e.getValue();
			switch (e.getCalcMode()) {
				case ADD -> {
					if (e.isNoLimit()) {
						{
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
						}
					} else {
						{
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
						}
					}
				}
				case MUL -> {
					if (e.isNoLimit()) {
						{
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
						}
					} else {
						{
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
						}
					}
				}
				case TO -> {
					{
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
					}
				}
				case TO_MAX -> {
					{
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
					}
				}
				case TO_ZERO -> {
					{
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
					}
				}
				case DC -> {
					{
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
						StatusKey dcs = ActionUtil.getDCS(a, user, actionType);

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
						String msg = ActionUtil.createResultMsg(tgt, tgtStatusKey, r);
						addResult(ar, r.summary, user, tgt, e, msg, isUserEvent);
					}
				}
				default ->
					throw new AssertionError("undefined calc mode");
			}//switch
			String msg = ActionUtil.createMsg(tgt, tgtStatusKey);
			StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
			addResult(ar,
					v != null ? ActionResultSummary.成功 : ActionResultSummary.失敗＿不発,
					user, tgt, e, msg, isUserEvent);

		}
	},
	ステータス回復TGTID回実施(true) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx) {
			String msg = I18N.get(GameSystemI18NKeys.Xを回復する,
					event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.ON,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.X回発動する, event.getTgtID()));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			String msg = I18N.get(GameSystemI18NKeys.X回復X回の術式,
					event.getTgtStatusKey().getVisibleName(), event.getTgtID());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.ON,
					I18N.get(GameSystemI18NKeys.X回発動する, event.getTgtID()));
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
			try {
				int i = Integer.parseInt(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが誤っています) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult ar, boolean isUserEvent) {
			StatusKey tgtStatusKey = e.getTgtStatusKey();
			tgt.getStatus().saveBeforeDamageCalc();

			float value = e.getValue();
			switch (e.getCalcMode()) {
				case ADD -> {
					if (e.isNoLimit()) {
						{
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
						}
					} else {
						{
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
						}
					}
				}
				case MUL -> {
					if (e.isNoLimit()) {
						{
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
						}
					} else {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
					}
				}
				case TO -> {
					{
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
					}
				}
				case TO_MAX -> {
					{
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
					}
				}
				case TO_ZERO -> {
					{
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
					}
				}
				case DC -> {
					{
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
						StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
						String msg = ActionUtil.createResultMsg(tgt, tgtStatusKey, r);
						addResult(ar, r.summary, user, tgt, e, msg, isUserEvent);
					}
				}
				default ->
					throw new AssertionError("undefined calc mode");
			}//switch
			String msg = ActionUtil.createMsg(tgt, tgtStatusKey);
			StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
			addResult(ar,
					v != null ? ActionResultSummary.成功 : ActionResultSummary.失敗＿不発,
					user, tgt, e, msg, isUserEvent);
		}

	},
	ATTR_IN(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.被耐性Xを変更する,
					event.getTgtAttrIn().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ATTRIN,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.被耐性変化の術式,
					event.getTgtAttrIn().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ATTRIN,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる));
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			tgt.getStatus().getAttrIn().get(e.getTgtAttrIn()).add(e.getValue());
			String msg = I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.被耐性Xが, e.getTgtAttrIn().getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xになった, (int) (tgt.getStatus().getAttrIn().get(e.getTgtAttrIn()).getValue() * 100f) + "%");
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	ATTR_OUT(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.与耐性Xを変更する,
					event.getTgtAttrOut().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ATTROUT,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.与耐性変化の術式,
					event.getTgtAttrOut().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ATTROUT,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる));
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			tgt.getStatus().getAttrIn().get(e.getTgtAttrOut()).add(e.getValue());
			String msg = I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.与耐性Xが, e.getTgtAttrIn().getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xになった, (int) (tgt.getStatus().getAttrIn().get(e.getTgtAttrIn()).getValue() * 100f) + "%");
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	CND_REGIST(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.X耐性を変更する,
					event.getTgtCndRegistKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CNDREG,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.状態耐性変化の術式,
					event.getTgtCndRegistKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CNDREG,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtCndRegistKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはCNDREGISTが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			float v = tgt.getStatus().getConditionRegist().get(e.getTgtCndRegistKey());
			tgt.getStatus().getConditionRegist().put(e.getTgtCndRegistKey(), v + e.getValue());
			String msg = I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.状態異常耐性Xが, e.getTgtCndRegistKey().getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xになった, (int) (tgt.getStatus().getAttrIn().get(e.getTgtAttrIn()).getValue() * 100f) + "%");
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	状態異常付与(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = event.getCndTime() == 0
					? I18N.get(GameSystemI18NKeys.Xを付与する, event.getTgtConditionKey().getVisibleName())
					: I18N.get(GameSystemI18NKeys.XをXターン付与する, event.getTgtConditionKey().getVisibleName(), event.getCndTime());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.状態付与の術式,
					event.getTgtConditionKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる));
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String msg = tgt.getStatus().addCondition(e.getTgtConditionKey(), e.getCndTime());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	耐性参照状態異常付与(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = event.getCndTime() == 0
					? I18N.get(GameSystemI18NKeys.Xを付与する, event.getTgtConditionKey().getVisibleName())
					: I18N.get(GameSystemI18NKeys.XをXターン付与する, event.getTgtConditionKey().getVisibleName(), event.getCndTime());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる),
					I18N.get(GameSystemI18NKeys.対象の耐性が参照される));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.状態付与の術式,
					event.getTgtConditionKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.意識を失うとこの効果はなくなる),
					I18N.get(GameSystemI18NKeys.対象の耐性が参照される));
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
			if (!e.getTgtConditionKey().isRegistOn()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはREGISTがあるTGTCNDKEYが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			//p判定は行われている。
			if (Random.percent(tgt.getStatus().getEffectedConditionRegist().get(e.getTgtConditionKey()))) {
				String msg = tgt.getStatus().addCondition(e.getTgtConditionKey(), e.getCndTime());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	状態異常解除(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xを解除する, event.getTgtConditionKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.状態解除の術式,
					event.getTgtConditionKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtConditionKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTCNDKEYが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String msg = tgt.getStatus().removeCondition(e.getTgtConditionKey());
			addResult(res, msg == null ? ActionResultSummary.失敗＿不発 : ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	アイテム追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xを入手する,
					event.getTgtAsItem().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ITEM,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.アイテム追加の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ITEM,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				if (ActionStorage.getInstance().getInstanceType(e.getTgtID()) != ActionStorage.InstanceType.ITEM) {
					throw new Exception();
				}
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			boolean suc = false;
			String msg;
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xを失う,
					event.getTgtAsItem().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ITEM,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.アイテムロストの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ITEM,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				if (ActionStorage.getInstance().getInstanceType(e.getTgtID()) != ActionStorage.InstanceType.ITEM) {
					throw new Exception();
				}
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			int prevSIze = tgt.getStatus().getItemBag().size();
			tgt.getStatus().getItemBag().drop(i);
			String msg = I18N.get(GameSystemI18NKeys.XはXを失った, tgt.getVisibleName(), i.getVisibleName());
			addResult(res, prevSIze != tgt.getStatus().getItemBag().size()
					? ActionResultSummary.成功
					: ActionResultSummary.失敗＿不発,
					user, tgt, e, msg, isUserEvent);
		}

	},
	ドロップアイテム追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.戦闘に勝利したときXを入手する,
					event.getTgtAsItem().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ITEM,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ドロップアイテム追加の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ITEM,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				if (ActionStorage.getInstance().getInstanceType(e.getTgtID()) != ActionStorage.InstanceType.ITEM) {
					throw new Exception();
				}
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Item i = ActionStorage.getInstance().itemOf(e.getTgtID());
			//ドロップアイテムに追加
			if (tgt instanceof Enemy enemy) {
				enemy.getDropItem().add(DropItem.itemOf(i, 1, 1f));//pは判定されているので、確率は１
				String msg = I18N.get(GameSystemI18NKeys.XはXを入手した, tgt.getVisibleName(), i.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	ドロップマテリアル追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.戦闘に勝利したときXを入手する,
					event.getTgtAsMaterial().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.MATERIAL,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ドロップマテリアル追加の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.MATERIAL,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Material m = MaterialStorage.getInstance().get(e.getTgtID());
			//ドロップマテリアルに追加
			if (tgt instanceof Enemy) {
				((Enemy) tgt).getDropItem().add(DropItem.materialOf(m, 1, 1f));//pは判定されているので、確率は１
				String msg = I18N.get(GameSystemI18NKeys.XはXを入手した, tgt.getVisibleName(), m.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	ユーザの武器をドロップしてTGTのドロップアイテムに追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.使用者のXを外し戦闘に勝利したとき入手する,
					event.getTgtAsSlot().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.SLOT,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.解除の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.SLOT,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				if (ActionStorage.getInstance().getInstanceType(e.getTgtID()) != ActionStorage.InstanceType.ITEM) {
					throw new Exception();
				}
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			//対象者のスロットのアイテムがnullの場合は投擲できないので呼び出しエラー
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
			if (tgt instanceof Enemy enemy) {
				enemy.getDropItem().add(DropItem.itemOf(tgtItem, 1, 1f));
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者は直ちにX回行動できる,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.即時追加行動の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			int i = 0;
			for (; i < (int) e.getValue(); i++) {
				BattleCommand cmd = new BattleCommand(tgt);
				if (tgt.isPlayer()) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者は直ちにX回行動できる,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.即時追加行動の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			int i = 0;
			for (; i < (int) e.getValue(); i++) {
				BattleCommand cmd = new BattleCommand(tgt);
				if (tgt.isPlayer()) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.ターゲットからXの距離内の同じチームの全員にも作用する,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.放射の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (a.getMainEvents().stream().filter(p -> p.getEventType().is連鎖イベント()).count() > 1) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.連鎖イベントは１種類しか設置できません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
				return;
			}
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream()
						.filter(p -> !p.getEventType().is連鎖イベント())
						.toList()) {
					if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
						for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
							ae.exec(user, a, newTgt, res, false);
						}
					} else {
						ae.exec(user, a, newTgt, res, false);
					}
				}
			}
			//このイベントの結果は入れなくていい。
		}

	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の全員にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.ターゲットからXの距離内の全員にも作用する,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.解放の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (a.getMainEvents().stream().filter(p -> p.getEventType().is連鎖イベント()).count() > 1) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.連鎖イベントは１種類しか設置できません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			for (Actor newTgt : newTgts) {
				for (ActionEvent ae : a.getMainEvents().stream()
						.filter(p -> !p.getEventType().is連鎖イベント())
						.toList()) {
					if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
						for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
							ae.exec(user, a, newTgt, res, false);
						}
					} else {
						ae.exec(user, a, newTgt, res, false);
					}
				}
			}
			//このイベントの結果は入れなくていい。
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内のランダムな同じチームの一人にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.ターゲットからXの距離内の同じチームのランダムな一人にも作用する,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.派生の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (a.getMainEvents().stream().filter(p -> p.getEventType().is連鎖イベント()).count() > 1) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.連鎖イベントは１種類しか設置できません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			for (ActionEvent ae : a.getMainEvents().stream()
					.filter(p -> !p.getEventType().is連鎖イベント())
					.toList()) {
				if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						ae.exec(user, a, newTgt, res, false);
					}
				} else {
					ae.exec(user, a, newTgt, res, false);
				}
			}
			//このイベントの結果は入れなくていい。
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内のランダムな一人にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.ターゲットからXの距離内のランダムな一人にも作用する,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.伝搬の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (a.getMainEvents().stream().filter(p -> p.getEventType().is連鎖イベント()).count() > 1) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.連鎖イベントは１種類しか設置できません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			Collections.shuffle(newTgts);
			if (newTgts.isEmpty()) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			Actor newTgt = newTgts.get(0);
			for (ActionEvent ae : a.getMainEvents().stream()
					.filter(p -> !p.getEventType().is連鎖イベント())
					.toList()) {
				if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						ae.exec(user, a, newTgt, res, false);
					}
				} else {
					ae.exec(user, a, newTgt, res, false);
				}
			}
			//このイベントの結果は入れなくていい。
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の最も近い同じチームの一人にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.ターゲットからXの距離内の同じチームの最も近い一人にも作用する,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.連鎖の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (a.getMainEvents().stream().filter(p -> p.getEventType().is連鎖イベント()).count() > 1) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.連鎖イベントは１種類しか設置できません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			List<Actor> newTgts = new ArrayList<>();
			if (tgt.isPlayer()) {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			} else {
				newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			}
			//newTgtsの最も近い対象を選出
			int len = Integer.MAX_VALUE;
			Actor newTgt = null;
			for (var ac : newTgts) {
				if (ac.getSprite().getCenter().distance(tgt.getSprite().getCenter()) < len) {
					newTgt = ac;
				}
			}

			if (newTgt == null) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			for (ActionEvent ae : a.getMainEvents().stream()
					.filter(p -> !p.getEventType().is連鎖イベント())
					.toList()) {
				if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						ae.exec(user, a, newTgt, res, false);
					}
				} else {
					ae.exec(user, a, newTgt, res, false);
				}
			}
			//このイベントの結果は入れなくていい。
		}
	},
	このアクションの他のイベントをこのイベントのTGTからVALUE内の最も近い一人にも適用(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.ターゲットからXの距離内の最も近い一人にも作用する,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.伝達の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (a.getMainEvents().stream().filter(p -> p.getEventType().is連鎖イベント()).count() > 1) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.連鎖イベントは１種類しか設置できません) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			List<Actor> newTgts = new ArrayList<>();
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allPartyOf(tgt.getSprite().getCenter(), (int) e.getValue()));
			newTgts.addAll(BattleSystem.getInstance().getTargetSystem().allEnemyOf(tgt.getSprite().getCenter(), (int) e.getValue()));

			//newTgtsの最も近い対象を選出
			int len = Integer.MAX_VALUE;
			Actor newTgt = null;
			for (var ac : newTgts) {
				if (ac.getSprite().getCenter().distance(tgt.getSprite().getCenter()) < len) {
					newTgt = ac;
				}
			}

			if (newTgt == null) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			}
			for (ActionEvent ae : a.getMainEvents().stream()
					.filter(p -> !p.getEventType().is連鎖イベント())
					.toList()) {
				if (ae.getEventType().isATKCOUNT回数実行するイベント()) {
					for (int i = 0; i < user.getStatus().getEffectedAtkCount(); i++) {
						ae.exec(user, a, newTgt, res, false);
					}
				} else {
					ae.exec(user, a, newTgt, res, false);
				}
			}
			//このイベントの結果は入れなくていい。
		}
	},
	このターンのTGTの行動を未行動ならこの直後に移動(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.このターン対象者が未行動なら対象者はこの行動のすぐあとに行動できる);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.即時行動の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.このターン対象者が未行動なら対象者はこのターンの最後に行動できる);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.遅延行動の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者は魔法詠唱を中断する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.詠唱中断の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者の詠唱完了イベントをXターン移動する,
					ActionUtil.getVisible値(event));
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.詠唱時間変更の術式,
					ActionUtil.getVisible値(event));
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			if (!tgt.getStatus().hasCondition(ConditionKey.詠唱中)) {
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
				if (t > 9999) {
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
			String tr = ActionUtil.getVisible値(e);
			String msg = I18N.get(GameSystemI18NKeys.Xの魔法詠唱はXターン移動した, tgt.getVisibleName(), tr);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	USERのクローンをパーティーまたはENEMYに追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象のクローンを召喚する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.クローニングの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String visibleName = "";
			if (tgt instanceof Enemy) {
				Enemy ee = Enemy.cloneOf(tgt);
				ee.setSummoned(true);
				BattleSystem.getInstance().getEnemies().add(ee);
				visibleName = ee.getVisibleName() + I18N.get(GameSystemI18NKeys.のクローン);
			} else {
				Actor newA = new Actor(tgt.getId() + "_CLONE", tgt);
				newA.setSummoned(true);
				GameSystem.getInstance().getParty().add(newA);
				visibleName = newA.getVisibleName() + I18N.get(GameSystemI18NKeys.のクローン);
			}
			String msg = I18N.get(GameSystemI18NKeys.分身Xが現れた, visibleName);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	このターンのTGTの行動を破棄(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者はそのターン行動できなくなる);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.行動阻止の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.そのターンの行動順を反転させる);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.トリックルームの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			LinkedList<BattleCommand> cmd = BattleSystem.getInstance().getCommandsOfThisTurn();
			Collections.reverse(cmd);
			BattleSystem.getInstance().setCommandsOfThisTurn(cmd);
			String msg = I18N.get(GameSystemI18NKeys.このターン行動順は反転した);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTを中心位置からVALUEの場所に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者は中心からXの範囲内に転送される, (int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.集結の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			//中心位置の取得
			Point2D.Float center = BattleSystem.getInstance().getBattleFieldSystem().getBattleFieldAllArea().getCenter();
			do {
				Point2D.Float p = Random.randomLocation(center, e.getValue());
				if (!BattleSystem.getInstance().getBattleFieldSystem().hitObstacle(p)) {
					if (BattleSystem.getInstance().getBattleFieldSystem().inArea(p)) {
						tgt.getSprite().setLocationByCenter(p);
						break;
					}
				}
			} while (true);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTを術者の近くに転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者は術者からXの範囲内に転送される, (int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.引き寄せの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Point2D.Float center = user.getSprite().getCenter();
			do {
				Point2D.Float p = Random.randomLocation(center, e.getValue());
				if (!BattleSystem.getInstance().getBattleFieldSystem().hitObstacle(p)) {
					if (BattleSystem.getInstance().getBattleFieldSystem().inArea(p)) {
						tgt.getSprite().setLocationByCenter(p);
						break;
					}
				}
			} while (true);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTを逃げられる位置に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者はすぐ逃げられる位置に転送される);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.退避の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者は一番近い敵対者のそばに転送される);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.接近の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			do {
				Point2D.Float center = tgt.getSprite().getCenter();
				Point2D.Float p = Random.randomLocation(center, tgt.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue() / 2);
				if (!BattleSystem.getInstance().getBattleFieldSystem().hitObstacle(p)) {
					if (BattleSystem.getInstance().getBattleFieldSystem().inArea(p)) {
						tgt.getSprite().setLocationByCenter(p);
						break;
					}
				}
			} while (true);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	USERをTGTの至近距離に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者は対象者のそばに転送される);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者転送の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			do {
				Point2D.Float center = tgt.getSprite().getCenter();
				Point2D.Float p = Random.randomLocation(center, tgt.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue() / 2);
				if (!BattleSystem.getInstance().getBattleFieldSystem().hitObstacle(p)) {
					if (BattleSystem.getInstance().getBattleFieldSystem().inArea(p)) {
						user.getSprite().setLocationByCenter(center);
						break;
					}
				}
			} while (true);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, user.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	USERとTGTの位置を交換(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者は対象者と位置が入れ替わる);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.位置交換の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Point2D.Float p = (Point2D.Float) tgt.getSprite().getCenter().clone();
			tgt.getSprite().setLocationByCenter(user.getSprite().getCenter());
			user.getSprite().setLocationByCenter(p);
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, user.getVisibleName());
			msg += Text.getLineSep();
			msg += I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	TGTIDのCSVにあるアイテムのいずれかをUSERに追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者は候補からいずれかのアイテムを手に入れる);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ランダムアイテムの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			String[] ids = StringUtil.safeSplit(e.getTgtID(), ",");
			if (ids.length == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			for (String id : ids) {
				try {
					if (ActionStorage.getInstance().getInstanceType(e.getTgtID()) != ActionStorage.InstanceType.ITEM) {
						throw new Exception();
					}
				} catch (Exception ex) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDがアイテムIDではありません) + " : " + this + " : " + e);
				}
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String id = Random.randomChoice(StringUtil.safeSplit(e.getTgtID(), ","));
			if (user.getStatus().getItemBag().canAdd() || e.isNoLimit()) {
				user.getStatus().getItemBag().add(ActionStorage.getInstance().itemOf(id));
				String msg = I18N.get(GameSystemI18NKeys.XはXを入手した);
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.Xはこれ以上物を持てない);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
			return;
		}
	},
	逃走で戦闘終了(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.戦闘が終了し逃走扱いになる);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.強制逃走の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			BattleSystem.getInstance().setEndStatus(BattleResult.勝利_こちらが全員逃げた);
			addResult(res, ActionResultSummary.成功, user, tgt, e, "", isUserEvent);
		}

	},
	TGTIDのマップIDの座標に転送(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにワープする, event.getTgtAsMsgI18Nd());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.MSG_I18Nd,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.マップ間ワープの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.MSG_I18Nd,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.現在のマップのランダムな出入り口に移動する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.テレポートの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Node n = FieldMap.getCurrentInstance().getNodeStorage().random();
			FieldMap.getCurrentInstance().setCurrentIdx(n.getIdx());
			FieldMap.getCurrentInstance().getCamera().updateToCenter();
			String msg = I18N.get(GameSystemI18NKeys.Xは転送された, user.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	友好的な存在の召喚(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xを召喚する, event.getTgtAsActor().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ACTOR,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.友好的存在召喚の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ACTOR,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String visibleName;
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xを召喚する, event.getTgtAsActor().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ACTOR,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.敵対的存在召喚の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ACTOR,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String visibleName;
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.現在のセーブデータを破壊しセーブせずにゲームを終了した場合はセーブデータをロストする);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.現在記録抹消の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			throw new UnsupportedOperationException("TODO:未実装");
		}

	},
	ゲームクラッシュ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.ゲームがセーブされずに終了する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.次元崩壊の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Object ぬるぽ = null;
			ぬるぽ.toString();
		}
	},
	カレント以外のセーブデータを１つロスト(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.他のセーブデータを破壊する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.別次元破壊の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			throw new UnsupportedOperationException("TODO:未実装");
		}
	},
	セーブデータ全ロスト(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.すべてのセーブデータを破壊する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.全空間破壊の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			throw new UnsupportedOperationException("TODO:未実装");
		}
	},
	独自効果(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.この効果は特殊なもので分析ができない);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.独自効果の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.独自効果イベントがオーバーライドされていません) + " : " + this + " : " + e);
		}

	},
	ビームエフェクト(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者から対象者へビームを照射する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.OFF,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.光線の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.OFF,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.独自効果イベントがオーバーライドされていません) + " : " + this + " : " + e);
		}
	},
	DC_ファイル選択からのハッシュ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える, event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは選択したファイルによって変動する));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.上位者の情報の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは選択したファイルによって変動する));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			if (value > 0) {
				value = -value;
			}
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
			//attr
			AttributeKey attr = AttributeKey.values()[file.getName().hashCode() % AttributeKey.values().length];
			//ダメージ計算実行
			DamageCalcSystem.Result dr
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									attr,
									actionType,
									value,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			String msg = ActionUtil.createResultMsg(tgt, e.getTgtStatusKey(), dr);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_ファイル選択からのサイズ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える, event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは選択したファイルのサイズによって変動する));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.上位者の巨大情報の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは選択したファイルのサイズによって変動する));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			AttributeKey attr = AttributeKey.神秘;
			try {
				value = Files.size(file.toPath()) / 1024f / 1024f;
				if (value > 0) {
					value = -value;
				}
				attr = AttributeKey.values()[(int) Files.size(file.toPath()) % AttributeKey.values().length];
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);

			//ダメージ計算実行
			DamageCalcSystem.Result dr
					= DamageCalcSystem.calcDamage(
							new DamageCalcSystem.Param(
									user,
									tgt,
									attr,
									actionType,
									value,
									e.getTgtStatusKey(),
									dcs)
					);

			//r評価
			String msg = ActionUtil.createResultMsg(tgt, e.getTgtStatusKey(), dr);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	DC_倒した敵の数が多い(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える, event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは倒した敵の数が多いほど大きくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.勇者の絶望の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは倒した敵の数が多いほど大きくなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Counts.Value v = Counts.getInstance().select(GameSystemI18NKeys.CountKey.倒した敵の数);
			if (v == null) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			float value = v.num / 100f;
			if (value > 0) {
				value = -value;
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	DC_倒した敵の数が少ない(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える, event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは倒した敵の数が少ないほど大きくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.慈悲深き聖者の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは倒した敵の数が少ないほど大きくなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Counts.Value v = Counts.getInstance().select(GameSystemI18NKeys.CountKey.倒した敵の数);
			if (v == null) {
				String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			float value = 1000 - (v.num / 100f);
			if (value > 0) {
				value = -value;
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	DC_ターン数が小さい(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える, event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージはターン数が小さいほど大きくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.速攻戦の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージはターン数が小さいほど大きくなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	DC_ターン数が大きい(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える, event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージはターン数が経過しているほど大きくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.速攻戦の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージはターン数が経過しているほど大きくなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	DC_CPUのコア数(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える, event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは使用しているコンピュータのコア数により変化する));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.上位者の脳の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは使用しているコンピュータのコア数により変化する));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			float value = Runtime.getRuntime().availableProcessors() * e.getValue();
			value = -value;
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_USERの持っているアイテムの重さ(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える, event.getTgtStatusKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージはアイテムをたくさん持っているほど大きくなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ヘビーボンバーの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージはアイテムをたくさん持っているほど大きくなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	詠唱完了イベントをVALUEターン内で反転(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xターン内の詠唱完了を反転させる, (int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.詠唱時間逆転の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.全員にXの正気度ダメージを与える, (int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.正気度ダメージの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max damage) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String msg = "";
			for (Actor ac : Stream.of(GameSystem.getInstance().getParty(), BattleSystem.getInstance().getEnemies()).flatMap(p -> p.stream()).toList()) {
				if (ac.equals(user)) {
					continue;
				}
				int v = (int) e.getValue();
				if (v < 0) {
					v = Math.abs(v);
				}
				int val = Random.randomAbsInt(v) + 1;
				if (v > 0) {
					v = -v;
				}
				ac.getStatus().getBaseStatus().get(StatusKey.正気度).add(val);
				msg += I18N.get(GameSystemI18NKeys.XはXの正気度ダメージを受けた, ac.getVisibleName(), Math.abs(val)) + Text.getLineSep();
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	WEBサイト起動(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.上位者の情報を閲覧する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.上位者の情報閲覧の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + "(n>1) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String tgtId = e.getTgtID();
			try {
				if (PlayerConstants.getInstance().OS_NAME.toLowerCase().contains("windows")) {
					Runtime.getRuntime().exec("cmd /q/c start " + tgtId);
				} else {
					Runtime.getRuntime().exec("open " + tgtId);
				}
				addResult(res, ActionResultSummary.成功, user, tgt, e, "", isUserEvent);
				return;
			} catch (IOException ex) {
			}
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, "", isUserEvent);
		}

	},
	DC_減っている体力(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは自身の体力が減っているほど高くなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.背水の陣の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは自身の体力が減っているほど高くなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_減っている魔力(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは自身の魔力が減っているほど高くなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.精神限界の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは自身の体力が減っているほど高くなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_減っている正気度(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは自身の正気度が減っているほど高くなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.狂気の笑みの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは自身の正気度が減っているほど高くなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_残っている体力(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは自身の体力が最大値に近いほど高くなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.体力の余裕の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは自身の体力が最大値に近いほど高くなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}

	},
	DC_残っている魔力(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは自身の魔力が最大値に近いほど高くなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.魔力の余裕の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは自身の魔力が最大値に近いほど高くなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	DC_残っている正気度(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このダメージは自身の正気度が最大値に近いほど高くなる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.精神的余裕の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.ON,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このダメージは自身の正気度が最大値に近いほど高くなる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
			if (e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(max power) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	USERによる指定IDの魔法の詠唱完了をこのターンの最後にVALUE回数追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xをこのターンの最後にX回発動する,
					event.getTgtAsAction().getVisibleName(), (int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ACTION,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.高速詠唱の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ACTION,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
	USERによる指定IDの魔法の詠唱完了をこのターンの最初にVALUE回数追加(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xをこのターンの最初にX回発動する,
					event.getTgtAsAction().getVisibleName(), (int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ACTION,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.多重発動の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ACTION,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.Xにダメージを与える,
					event.getTgtAsAction().getVisibleName(), (int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.このイベントはランダムな属性とダメージになる));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ランダムシードの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.このイベントはランダムな属性とダメージになる));
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtStatusKey() == null) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTgtStatusKeyが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			float val = Random.d100(1);
			AttributeKey attr = Random.randomChoice(AttributeKey.values());
			val = -val;
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
			StatusKey dcs = ActionUtil.getDCS(a, user, actionType);
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
			String msg = ActionUtil.createResultMsg(tgt, dcs, r);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);

		}
	},
	USERの指定スロットの装備品の攻撃回数をVALUE上げる(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者のX装備の攻撃回数をX増減する,
					event.getTgtAsSlot().getVisibleName(), ActionUtil.getVisible値(event));
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.攻撃回数変化の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
			if ((int) e.getValue() == 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + "(n>0) : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}
	},
	USERの指定スロットの装備品の価値をVALUE倍にする(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者のX装備の価値をXにする,
					event.getTgtAsSlot().getVisibleName(), ActionUtil.getVisible値Percent(event));
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.PERCENT,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.装備価値変更の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.PERCENT,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
				i.setPrice(v);
				String msg = I18N.get(GameSystemI18NKeys.XのXは価値が変動した, user.getVisibleName(), i.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	USERの指定スロットの装備品の価値にVALUEを追加する(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者のX装備の価値にXを加算する,
					event.getTgtAsSlot().getVisibleName(), ActionUtil.getVisible値(event));
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.装備価値加算の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (user.getStatus().getEqip().keySet().contains(slot)) {
				Item i = user.getStatus().getEqip().get(slot);
				if (i == null) {
					String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
					addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
					return;
				}
				int v = (int) (i.getPrice() + e.getValue());
				if (v < 0) {
					v = 0;
				}
				i.setPrice(v);
				String msg = I18N.get(GameSystemI18NKeys.XのXは価値が変動した, user.getVisibleName(), i.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTを即死させる(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象を即死Xさせる,
					event.getTgtConditionKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.即死の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
					tgt.getStatus().addCondition(e.getTgtConditionKey(), e.getCndTime());
					break;
				}
				default:
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントのTGTCNDKEYは解脱損壊気絶のいずれかである必要があります) + " : " + this + " : " + e);
			}
			String msg = tgt.getVisibleName() + e.getTgtConditionKey().getStartMsgI18Nd();
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTを即死させる_耐性参照(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象を即死Xさせる,
					event.getTgtConditionKey().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx,
					I18N.get(GameSystemI18NKeys.耐性が参照される));
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.即死の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					I18N.get(GameSystemI18NKeys.耐性が参照される));
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			switch (e.getTgtConditionKey()) {
				case 解脱: {
					if (!Random.percent(tgt.getStatus().getEffectedConditionRegist().get(ConditionKey.解脱))) {
						String msg = I18N.get(GameSystemI18NKeys.XはXしなかった, tgt.getVisibleName(), ConditionKey.解脱.getVisibleName());
						addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
						return;
					}
					tgt.getStatus().getBaseStatus().get(StatusKey.正気度).setValue(0);
					tgt.getStatus().addWhen0Condition();
					break;
				}
				case 損壊: {
					if (!Random.percent(tgt.getStatus().getEffectedConditionRegist().get(ConditionKey.損壊))) {
						String msg = I18N.get(GameSystemI18NKeys.XはXしなかった, tgt.getVisibleName(), ConditionKey.損壊.getVisibleName());
						addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
						return;
					}
					tgt.getStatus().getBaseStatus().get(StatusKey.体力).setValue(0);
					tgt.getStatus().addWhen0Condition();
					break;
				}
				case 気絶: {
					if (!Random.percent(tgt.getStatus().getEffectedConditionRegist().get(ConditionKey.気絶))) {
						String msg = I18N.get(GameSystemI18NKeys.XはXしなかった, tgt.getVisibleName(), ConditionKey.気絶.getVisibleName());
						addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
						return;
					}
					tgt.getStatus().getBaseStatus().get(StatusKey.魔力).setValue(0);
					tgt.getStatus().addCondition(e.getTgtConditionKey(), e.getCndTime());
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.術者のX装備を解除する,
					event.getTgtAsSlot().getVisibleName());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.SLOT,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.パージの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.SLOT,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			EqipSlot slot = EqipSlot.valueOf(e.getTgtID());
			if (tgt.getStatus().getEqip().keySet().contains(slot)) {
				tgt.getStatus().getEqip().put(slot, null);
				String msg = I18N.get(GameSystemI18NKeys.XはX装備を外した, user.getVisibleName(), slot.getVisibleName());
				addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
				return;
			}
			String msg = I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
		}

	},
	マップIDと座標を入力させて移動する(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.指定したマップの指定した座標にワープする);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.転送の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
				Map map = new Map(m.getName(), m.getName() + ":" + I18N.get(m.getName()));
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
				return;
			}
			FieldMap.getCurrentInstance().changeMap(Node.ofOutNode("AUTO_NODE_FROM_AE", tgtMap.id,
					(Integer) x.getValue(), (Integer) y.getValue(), FourDirection.NORTH));
			addResult(res, ActionResultSummary.成功, user, tgt, e, I18N.get(GameSystemI18NKeys.Xは転送された, tgt.getStatus().getVisibleName()), isUserEvent);
		}

	},
	ダミー＿成功(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.このイベントは処理の都合で入っているようだ);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.OFF,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ダミーの術式＿成功);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			addResult(res, ActionResultSummary.成功, user, tgt, e, "", isUserEvent);
		}

	},
	ダミー＿失敗(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.このイベントは処理の都合で入っているようだ);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.OFF,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ダミーの術式＿失敗);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, "", isUserEvent);
		}
	},
	メッセージ表示(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.このイベントは処理の都合で入っているようだ);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ダミーの術式＿メッセージ表示);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			addResult(res, ActionResultSummary.成功, user, tgt, e, I18N.get(e.getTgtID()), isUserEvent);
		}

	},
	フラグ参照メッセージ表示(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.このイベントは処理の都合で入っているようだ);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ダミーの術式＿メッセージ表示);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String[] val = StringUtil.safeSplit(e.getTgtID(), ",");
			String name = val[0];
			FlagStatus fs = FlagStatus.valueOf(val[1]);
			if (!FlagStorage.getInstance().get(name).get().is(fs)) {
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, "", isUserEvent);
			}
			addResult(res, ActionResultSummary.成功, user, tgt, e, I18N.get(val[2]), isUserEvent);
		}

	},
	指定IDのPCがいれば即死させる(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.特定のキャラを即死Xさせる);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.特定人物即死の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.CND,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
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
					ac.getStatus().addCondition(e.getTgtConditionKey(), e.getCndTime());
					ac.getStatus().getBaseStatus().get(StatusKey.魔力).setValue(0);
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
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.特定のキャラに正気度ダメージを与える);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.特定人物正気度ダメージの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
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
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Actor ac = GameSystem.getInstance().getPCbyID(e.getTgtID());
			int val = Random.randomAbsInt((int) e.getValue()) + 1;
			val = -val;
			ac.getStatus().getBaseStatus().get(StatusKey.正気度).add(val);
			String msg = I18N.get(GameSystemI18NKeys.XはXの正気度ダメージを受けた, ac.getVisibleName(), Math.abs(val));
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	TGTノックバック(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象者は最大Xノックバックする,
					(int) event.getValue() + "");
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.ノックバックの術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if ((int) e.getValue() <= 0) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはVALUEが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			KVector v = new KVector(user.getSprite().getCenter(), tgt.getSprite().getCenter());
			v.setSpeed(1f);
			KVector buf = tgt.getSprite().getVector();
			tgt.getSprite().setVector(v);
			for (int i = 0; i < (int) e.getValue(); i++) {
				Point2D.Float newC = tgt.getSprite().simulateMoveCenterLocation(v);
				if (BattleSystem.getInstance().getBattleFieldSystem().hitObstacle(newC)) {
					break;
				}
				if (!BattleSystem.getInstance().getBattleFieldSystem().inArea(newC)) {
					break;
				}
				tgt.getSprite().move();
			}
			tgt.getSprite().setVector(buf);
			String msg = I18N.get(GameSystemI18NKeys.Xはノックバックした, tgt.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	脚本の実行(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.脚本Xを実行する,
					event.getTgtID());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.脚本実行の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.ID_OR_TGTID,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			File file = new File(e.getTgtID());
			if (!file.exists()) {
				String msg = I18N.get(GameSystemI18NKeys.脚本Xが存在しない, e.getTgtID());
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			try {
				FieldEventSystem.checkAllScripts(file);
			} catch (Exception ex) {
				String msg = I18N.get(GameSystemI18NKeys.脚本Xは誤っている, e.getTgtID());
				addResult(res, ActionResultSummary.失敗＿不発, user, tgt, e, msg, isUserEvent);
				return;
			}
			FieldEventSystem.getInstance().setEvent(new LinkedList<>(FieldEventParser.parse(a.getId(), e.getTgtID())));
			String msg = I18N.get(GameSystemI18NKeys.脚本が実行された);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	統計情報変更(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.統計情報XにXを加算する,
					event.getTgtAsMsgI18Nd(), ActionUtil.getVisible値(event));
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.統計X改竄の術式, event.getTgtAsMsgI18Nd());
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.VALUE,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String id = e.getTgtID();
			long val = Counts.getInstance().select(id).num + (int) e.getValue();
			if (val < 0) {
				val = 0;
			}
			Counts.getInstance().updateOrInsert(id, val);
			String msg = I18N.get(GameSystemI18NKeys.統計情報XはXになった, I18N.get(id), val);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	統計情報完全リセット(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.統計情報を完全にリセットする);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.改竄の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			for (var v : Counts.getInstance().selectAll()) {
				String id = v.name;
				Counts.getInstance().updateOrInsert(id, 0);
			}
			String msg = I18N.get(GameSystemI18NKeys.統計情報がリセットされた);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}

	},
	現在のマップIDと座標表示(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.現在のフィールドマップ情報を閲覧する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.位置の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String text = "MAP ID : " + FieldMap.getCurrentInstance().getName();
			text += "\n";
			text += "LOCATION : x=" + FieldMap.getCurrentInstance().getCurrentIdx().x + ", y=" + FieldMap.getCurrentInstance().getCurrentIdx().y;
			Dialog.info("CURRENT MAP", text);
			addResult(res, ActionResultSummary.成功, user, tgt, e, "", isUserEvent);
		}

	},
	難易度の変更(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.難易度をXに変更する,
					event.getTgtAsDifficulty().getNameI18Nd());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.DIFFICULTY,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.世界設定変更の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.DIFFICULTY,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
			if (e.getTgtID() == null || e.getTgtID().isEmpty()) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.このイベントにはTGTIDが必要です) + " : " + this + " : " + e);
			}
			try {
				Difficulty d = Difficulty.valueOf(e.getTgtID());
			} catch (Exception ex) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.TGTIDが難易度ではありません) + " : " + this + " : " + e);
			}

		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Difficulty d = Difficulty.valueOf(e.getTgtID());
			GameSystem.setDifficulty(d);
			String msg = I18N.get(GameSystemI18NKeys.難易度がXになった, d.getNameI18Nd());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	難易度の選択(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.難易度を選択して変更する);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.世界設定再選択の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			JComboBox<String> cmb = new JComboBox<>();
			cmb.removeAllItems();
			Arrays.stream(Difficulty.values()).map(p -> p.getNameI18Nd()).forEach(p -> cmb.addItem(p));
			DialogOption r = Dialog.okOrCancel("SELECT DIFFICULTY", DialogIcon.QUESTION, cmb);
			if (r == DialogOption.OK) {
				String selected = cmb.getSelectedItem().toString();
				GameSystem.setDifficulty(Difficulty.valueOf(selected));
			}
			String msg = I18N.get(GameSystemI18NKeys.難易度がXになった, GameSystem.getDifficulty().getNameI18Nd());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	デバッグモードの変更(false) {
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.デバッグモードを切り替える);
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.世界の裏側の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.OFF,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			GameSystem.setDebugMode(!GameSystem.isDebugMode());
			String msg = I18N.get(GameSystemI18NKeys.デバッグモードがXになった, GameSystem.isDebugMode());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	キャラアビリティの変更(false) {
		//寝ると解除される
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象の特性をXに変える, event.getTgtAsMsgI18Nd());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.MSG_I18Nd,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.特性変更の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.MSG_I18Nd,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			Ability ab = Ability.valueOf(e.getTgtID());
			tgt.getStatus().setAbility(ab);
			String msg = I18N.get(GameSystemI18NKeys.XはXになった, tgt.getVisibleName(), ab.getVisibleName());
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	},
	異名の変更(false) {
		//寝ると解除される
		@Override
		public String getEventDescI18Nd(ActionEvent event, int thisIdx
		) {
			String msg = I18N.get(GameSystemI18NKeys.対象の異名をXに変える, event.getTgtAsMsgI18Nd());
			return ActionUtil.getVisibleDescI18Nd(event,
					msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.MSG_I18Nd,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF,
					ActionUtil.E起動条件Visibility.ON,
					thisIdx);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event
		) {
			String msg = I18N.get(GameSystemI18NKeys.異名変更の術式);
			return ActionUtil.getVisibleDescI18Nd(event, msg,
					ActionUtil.属性Visibility.OFF,
					ActionUtil.値Visibility.MSG_I18Nd,
					ActionUtil.確率Visibility.ON,
					ActionUtil.計算方法Visibility.OFF);
		}

		@Override
		public void pack(ActionEvent e, Action a) throws GameSystemException {
		}

		@Override
		public void exec(Actor user, Action a,
				Actor tgt, ActionEvent e,
				ActionResult res, boolean isUserEvent
		) {
			String v = e.getTgtAsMsgI18Nd();
			tgt.getStatus().set異名(v);
			String msg = I18N.get(GameSystemI18NKeys.XはXになった, tgt.getVisibleName(), v);
			addResult(res, ActionResultSummary.成功, user, tgt, e, msg, isUserEvent);
		}
	};

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public abstract String getEventDescI18Nd(ActionEvent e, int thisIdx);

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
		r.is損壊 = user.getStatus().hasCondition(損壊);
		r.is気絶 = user.getStatus().hasCondition(気絶);
		r.is解脱 = user.getStatus().hasCondition(解脱);
		r.msgI18Nd = msg;
		if (s.is成功()) {
			//ERへのアニメーションなどのセット
			if (e.getTgtAnimation() != null) {
				r.tgtAnimation = e.getTgtAnimation().clone();
				r.tgtAnimation.getAnimation().setImages(ImageUtil.resizeAll(r.tgtAnimation.getAnimation().getImages(), GameOption.getInstance().getDrawSize()));
				r.tgtAnimation.setLocationByCenter(user.getSprite().getCenter());
				r.tgtAnimation.getAnimation().setRepeat(false);
			}
			if (e.getOtherAnimation() != null) {
				r.otherAnimation = e.getOtherAnimation().clone();
				r.otherAnimation.getAnimation().setImages(ImageUtil.resizeAll(r.otherAnimation.getAnimation().getImages(), GameOption.getInstance().getDrawSize()));
				r.otherAnimation.setLocation(0, 0);
				r.otherAnimation.getAnimation().setRepeat(false);
			}
			if (e.getUserAnimation() != null) {
				r.userAnimation = e.getUserAnimation().clone();
				r.userAnimation.getAnimation().setImages(ImageUtil.resizeAll(r.userAnimation.getAnimation().getImages(), GameOption.getInstance().getDrawSize()));
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
		r.is損壊 = tgt.getStatus().hasCondition(損壊);
		r.is気絶 = tgt.getStatus().hasCondition(気絶);
		r.is解脱 = tgt.getStatus().hasCondition(解脱);
		r.msgI18Nd = msg;
		if (s.is成功()) {
			//ERへのアニメーションなどのセット
			if (e.getTgtAnimation() != null) {
				r.tgtAnimation = e.getTgtAnimation().clone();
				r.tgtAnimation.getAnimation().setImages(ImageUtil.resizeAll(r.tgtAnimation.getAnimation().getImages(), GameOption.getInstance().getDrawSize()));
				r.tgtAnimation.setLocationByCenter(tgt.getSprite().getCenter());
				r.tgtAnimation.getAnimation().setRepeat(false);
			}
			if (e.getOtherAnimation() != null) {
				r.otherAnimation = e.getOtherAnimation().clone();
				r.otherAnimation.getAnimation().setImages(ImageUtil.resizeAll(r.otherAnimation.getAnimation().getImages(), GameOption.getInstance().getDrawSize()));
				r.otherAnimation.setLocation(0, 0);
				r.otherAnimation.getAnimation().setRepeat(false);
			}
			if (e.getUserAnimation() != null) {
				r.userAnimation = e.getUserAnimation().clone();
				r.userAnimation.getAnimation().setImages(ImageUtil.resizeAll(r.userAnimation.getAnimation().getImages(), GameOption.getInstance().getDrawSize()));
				r.userAnimation.setLocationByCenter(user.getSprite().getCenter());
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
			if (e.getSuccessSound() != null) {
				e.getSuccessSound().load().stopAndPlay();
			}
		}
		return new ActionResult.PerEvent(e, s, Map.of(tgt, r));
	}

	public boolean is連鎖イベント() {

		return this == このアクションの他のイベントをこのイベントのTGTからVALUE内の同じチームの全員にも適用
				|| this == このアクションの他のイベントをこのイベントのTGTからVALUE内の全員にも適用
				|| this == このアクションの他のイベントをこのイベントのTGTからVALUE内のランダムな同じチームの一人にも適用
				|| this == このアクションの他のイベントをこのイベントのTGTからVALUE内のランダムな一人にも適用
				|| this == このアクションの他のイベントをこのイベントのTGTからVALUE内の最も近い同じチームの一人にも適用
				|| this == このアクションの他のイベントをこのイベントのTGTからVALUE内の最も近い一人にも適用;
	}

	public boolean isTgtID回実行イベント() {
		return this == ステータス回復TGTID回実施 || this == ステータス攻撃TGTID回実施;
	}
}
