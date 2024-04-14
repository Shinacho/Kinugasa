/*
 * Copyright (C) 2024 Shinacho
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

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.I18N;
import static kinugasa.game.system.ActionEvent.CalcMode.ADD;
import static kinugasa.game.system.ActionEvent.CalcMode.DC;
import static kinugasa.game.system.ActionEvent.CalcMode.MUL;
import static kinugasa.game.system.ActionEvent.CalcMode.TO;
import static kinugasa.game.system.ActionEvent.CalcMode.TO_MAX;
import static kinugasa.game.system.ActionEvent.CalcMode.TO_ZERO;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.ACTORのIDが一致;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.指定のアイテムのいずれかを持っている;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.指定のアイテムを持っている;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.指定のステータスの現在値が指定の値以上;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.指定のステータスの現在値が指定の割合以上;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.指定の名前のアイテムを持っている;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.指定の武器タイプの武器を装備している;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.指定の状態異常を持っていない;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.指定の状態異常を持っている;
import static kinugasa.game.system.ActionEvent.Actor起動条件.Type.武器を装備していない;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2024/04/06_14:56:15<br>
 * @author Shinacho<br>
 */
public final class ActionUtil {

	private ActionUtil() {
	}

	public enum 確率Visibility {
		ON,
		OFF
	}

	public enum 計算方法Visibility {
		ON,
		OFF
	}

	public enum 値Visibility {
		ON,
		ON_PERCENT,
		OFF,
		ON_ATTRIN,
		ON_ATTROUT,
		ON_CND,
		ON_CNDREG,
		ON_ITEM,
		ON_MATERIAL,
		ON_SLOT,
		ON_I18N,
		ON_ACTOR,
		ON_ID,
		ON_ACTION,
		ON_DIFF,
	}

	public enum 属性Visibility {
		ON,
		OFF
	}

	public enum E起動条件Visibility {
		ON,
		OFF
	}

	public static String getVisibleDescI18Nd(ActionEvent e, String mainMsg,
			属性Visibility zv, 値Visibility av, 確率Visibility pv, 計算方法Visibility kv, String... exMsg) {
		return getVisibleDescI18Nd(e, mainMsg, zv, av, pv, kv, E起動条件Visibility.OFF, 0, exMsg);
	}

	public static String getVisibleDescI18Nd(ActionEvent e, String mainMsg,
			属性Visibility zv, 値Visibility av, 確率Visibility pv, 計算方法Visibility kv,
			E起動条件Visibility ev, int thisIdx, String... exMsg) {
		StringBuilder m = new StringBuilder();
		for (var v : exMsg) {
			m.append("  ・").append(v).append(Text.getLineSep());
		}
		return getVisibleDescI18Nd(e, mainMsg, zv, av, pv, kv, ev, thisIdx) + m.toString();
	}

	public static String getVisibleDescI18Nd(ActionEvent e, String mainMsg,
			属性Visibility zv, 値Visibility av, 確率Visibility pv, 計算方法Visibility kv) {
		return getVisibleDescI18Nd(e, mainMsg, zv, av, pv, kv, E起動条件Visibility.OFF, 0);
	}

	public static String getVisibleDescI18Nd(ActionEvent e, String mainMsg,
			属性Visibility zv, 値Visibility av, 確率Visibility pv, 計算方法Visibility kv,
			E起動条件Visibility ev, int thisIdx) {
		/*　
		表示形式
		1)
		  mainMsg/
		  ・属性:神秘、基礎値:32、確率:xx%、計算方法:ダメージ計算/
		  ・条件:{(1)が成功時}/
		  ・条件:{魔術利用可能}/
		  ・条件:{対象が[木化]時}/
		^2 x space
		 */

		StringBuilder sb = new StringBuilder();
		sb.append("  ").append(mainMsg).append(Text.getLineSep());

		String nigyoume = "";
		if (zv == 属性Visibility.ON) {
			if (e.getAtkAttr() == null) {
				nigyoume += I18N.get(GameSystemI18NKeys.属性) + ":" + I18N.get(GameSystemI18NKeys.無) + ", ";
			} else {
				nigyoume += I18N.get(GameSystemI18NKeys.属性) + ":" + e.getAtkAttr().getVisibleName() + ", ";
			}
		}
		switch (av) {
			case OFF -> {
			}
			case ON ->
				nigyoume += I18N.get(GameSystemI18NKeys.値) + ":" + getVisible値(e) + ", ";
			case ON_PERCENT ->
				nigyoume += I18N.get(GameSystemI18NKeys.値) + ":" + getVisible値Percent(e) + ", ";
			case ON_ATTRIN ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":[" + e.getTgtAttrIn().getVisibleName() + "], ";
			case ON_ATTROUT ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":[" + e.getTgtAttrOut().getVisibleName() + "], ";
			case ON_CND ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":[" + e.getTgtConditionKey().getVisibleName() + "], ";
			case ON_CNDREG ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":[" + e.getTgtCndRegistKey().getVisibleName() + "], ";
			case ON_ITEM ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":" + e.getTgtAsItem().getVisibleName() + ", ";
			case ON_MATERIAL ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":" + e.getTgtAsMaterial().getVisibleName() + ", ";
			case ON_SLOT ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":" + e.getTgtAsSlot().getVisibleName() + ", ";
			case ON_I18N ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":" + I18N.get(e.getTgtID()) + ", ";
			case ON_ACTOR ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":" + e.getTgtAsActor().getVisibleName() + ", ";
			case ON_ID ->
				nigyoume += "ID" + ":" + e.getTgtID() + ", ";
			case ON_ACTION ->
				nigyoume += I18N.get(GameSystemI18NKeys.対象) + ":" + e.getTgtAsAction().getVisibleName() + ", ";
			case ON_DIFF ->
				nigyoume += I18N.get(GameSystemI18NKeys.値) + ":" + e.getTgtAsDifficulty().getNameI18Nd() + ", ";
			default ->
				throw new AssertionError("undefined value visibility : " + av);
		}
		if (pv == 確率Visibility.ON) {
			nigyoume += I18N.get(GameSystemI18NKeys.確率) + ":" + getVisible確率(e) + ", ";
		}
		if (kv == 計算方法Visibility.ON) {
			if (e.getCalcMode() != null) {
				nigyoume += I18N.get(GameSystemI18NKeys.計算方法) + ":" + e.getCalcMode().getVisibleName() + ", ";
			}
		}
		if (!nigyoume.isEmpty()) {
			nigyoume = nigyoume.substring(0, nigyoume.lastIndexOf(", "));
			sb.append("  ・").append(nigyoume).append(Text.getLineSep());
		}
		if (ev == E起動条件Visibility.ON) {
			if (e.getEvent起動条件() != null) {
				if (e.getEvent起動条件() != ActionEvent.Event起動条件.条件なしで必ず起動) {
					sb.append("  ・").append(I18N.get(GameSystemI18NKeys.条件)).append(":");
					sb.append("{").append(e.getEvent起動条件().getTextI18Nd(thisIdx + "")).append("}").append(Text.getLineSep());
				}
			}
		}
		for (var v : e.getUser起動条件()) {
			sb.append("  ・").append(I18N.get(GameSystemI18NKeys.条件)).append(":");
			sb.append("{").append(v.getTextI18Nd()).append("}").append(Text.getLineSep());
		}
		for (var v : e.getTgt起動条件()) {
			sb.append("  ・").append(I18N.get(GameSystemI18NKeys.条件)).append(":");
			sb.append("{").append(v.getTextI18Nd()).append("}").append(Text.getLineSep());
		}
		return sb.toString();

	}

	static String getVisible確率(ActionEvent event) {
		return (int) (event.getP() * 100f) + "%";
	}

	static String getVisible値(ActionEvent event) {
		String r = event.getValue() < 0 ? "-" : "+";
		return r + Math.abs((int) (event.getValue()));
	}

	static String getVisible値Percent(ActionEvent event) {
		String r = event.getValue() < 0 ? "-" : "+";
		return r + Math.abs((int) (event.getValue() * 100f));
	}

	static StatusKey getDCS(Action a, Actor user, DamageCalcSystem.ActionType actionType) {
		//このアクションが武器に紐づく場合、その武器のDCSを採用する
		WeaponType wt = a.getWeaponType();
		if (wt == null) {
			return actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神;
		}

		if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
			Item i = user.getStatus().getEqip().get(EqipSlot.右手);
			if (i != null && i.getDcs() != null && i.getWeaponType() == wt) {
				return i.getDcs();
			} else if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
				i = user.getStatus().getEqip().get(EqipSlot.左手);
				if (i != null && i.getDcs() != null && i.getWeaponType() == wt) {
					return i.getDcs();
				}
			}
		} else if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
			Item i = user.getStatus().getEqip().get(EqipSlot.左手);
			if (i != null && i.getDcs() != null && i.getWeaponType() == wt) {
				return i.getDcs();
			}
		}

		return actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神;
	}

	static String createResultMsg(Actor tgt, StatusKey tgtStatusKey, DamageCalcSystem.Result r) {

		//r評価
		String msg = "";
		if (r.isTgtIs回避) {
			msg += I18N.get(GameSystemI18NKeys.Xは回避した, tgt.getVisibleName());
		}
		if (r.isブロックされた) {
			msg += I18N.get(GameSystemI18NKeys.Xはブロックした, tgt.getVisibleName());
		}
		if (r.反射された) {
			msg += I18N.get(GameSystemI18NKeys.Xは反射した, tgt.getVisibleName());
		}
		if (r.吸収された) {
			msg += I18N.get(GameSystemI18NKeys.Xは吸収した, tgt.getVisibleName());
		}
		if (r.isクリティカル) {
			msg += I18N.get(GameSystemI18NKeys.クリティカルヒットした);
		}
		StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
		if (v == null) {
			msg = I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
		} else if (v.getValue() < 0) {
			if (!msg.isEmpty()) {
				msg += "  ";
			}
			msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xに, tgtStatusKey.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs((int) v.getValue()));
		} else {
			if (!msg.isEmpty()) {
				msg += "  ";
			}
			msg += I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.Xは, tgtStatusKey.getVisibleName())
					+ I18N.get(GameSystemI18NKeys.X回復した, Math.abs((int) v.getValue()));
		}

		return msg;
	}

	static String createMsg(Actor tgt, StatusKey tgtStatusKey) {
		StatusValue v = tgt.getStatus().getDamageFromSavePoint().get(tgtStatusKey);
		if (v != null) {
			if (v.getValue() < 0) {
				return I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xに, tgtStatusKey.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xのダメージ, (int) v.getValue());
			} else {
				return I18N.get(GameSystemI18NKeys.Xの, tgt.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xは, tgtStatusKey.getVisibleName())
						+ I18N.get(GameSystemI18NKeys.X回復した, (int) v.getValue());
			}
		}
		return I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, tgt.getVisibleName());
	}
}
