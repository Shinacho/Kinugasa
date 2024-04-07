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
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.ACTORのIDが一致;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.指定のアイテムのいずれかを持っている;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.指定のアイテムを持っている;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.指定のステータスの現在値が指定の値以上;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.指定のステータスの現在値が指定の割合以上;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.指定の名前のアイテムを持っている;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.指定の武器タイプの武器を装備している;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.指定の状態異常を持っていない;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.指定の状態異常を持っている;
import static kinugasa.game.system.ActionEvent.実行可否条件.Type.武器を装備していない;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2024/04/06_14:56:15<br>
 * @author Shinacho<br>
 */
public final class ActionUtil {

	private ActionUtil() {
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

	static String get起動条件(ActionEvent event) {
		List<String> res = new ArrayList<>();
		for (var v : event.getUser保有条件()) {
			switch (v.type) {
				case ACTORのIDが一致 -> {
					res.add(I18N.get(GameSystemI18NKeys.使用者が) + v.getVisibleTextI18Nd());
				}
				case 指定のアイテムを持っている, 指定のアイテムのいずれかを持っている, 指定の名前のアイテムを持っている -> {
					res.add(I18N.get(GameSystemI18NKeys.使用者が) + v.getVisibleTextI18Nd());
				}
				case 指定のステータスの現在値が指定の値以上 -> {
					if (StatusKey.valueOf(v.tgtName) == StatusKey.魔術使用可否) {
						if (v.value >= 1f) {
							res.add(I18N.get(GameSystemI18NKeys.使用者が)
									+ I18N.get(GameSystemI18NKeys.魔術利用可能));
						} else {
							res.add(I18N.get(GameSystemI18NKeys.使用者が)
									+ I18N.get(GameSystemI18NKeys.魔術利用不可));
						}
					} else {
						res.add(I18N.get(GameSystemI18NKeys.使用者が)
								+ v.getVisibleTextI18Nd(StatusKey.valueOf(v.tgtName).getVisibleName(), (int) v.value + ""));
					}
				}
				case 指定のステータスの現在値が指定の割合以上 -> {
					res.add(I18N.get(GameSystemI18NKeys.使用者の)
							+ v.getVisibleTextI18Nd(StatusKey.valueOf(v.tgtName).getVisibleName(), (int) (v.value * 100f) + ""));
				}
				case 武器を装備していない, 指定の武器タイプの武器を装備している -> {
					res.add(I18N.get(GameSystemI18NKeys.使用者が) + v.getVisibleTextI18Nd(WeaponType.valueOf(v.tgtName).getVisibleName()));
				}
				case 指定の状態異常を持っている, 指定の状態異常を持っていない -> {
					res.add(I18N.get(GameSystemI18NKeys.使用者が) + v.getVisibleTextI18Nd(ConditionKey.valueOf(v.tgtName).getVisibleName()));
				}
			}
		}
		for (var v : event.getTgt適用条件()) {
			switch (v.type) {
				case ACTORのIDが一致 -> {
					res.add(I18N.get(GameSystemI18NKeys.対象が) + v.getVisibleTextI18Nd());
				}
				case 指定のアイテムを持っている, 指定のアイテムのいずれかを持っている, 指定の名前のアイテムを持っている -> {
					res.add(I18N.get(GameSystemI18NKeys.対象が) + v.getVisibleTextI18Nd());
				}
				case 指定のステータスの現在値が指定の値以上 -> {
					if (StatusKey.valueOf(v.tgtName) == StatusKey.魔術使用可否) {
						if (v.value >= 1f) {
							res.add(I18N.get(GameSystemI18NKeys.対象が)
									+ I18N.get(GameSystemI18NKeys.魔術利用可能));
						} else {
							res.add(I18N.get(GameSystemI18NKeys.対象が)
									+ I18N.get(GameSystemI18NKeys.魔術利用不可));
						}
					} else {
						res.add(I18N.get(GameSystemI18NKeys.対象が)
								+ v.getVisibleTextI18Nd(StatusKey.valueOf(v.tgtName).getVisibleName(), (int) v.value + ""));
					}
				}
				case 指定のステータスの現在値が指定の割合以上 -> {
					res.add(I18N.get(GameSystemI18NKeys.対象の)
							+ v.getVisibleTextI18Nd(StatusKey.valueOf(v.tgtName).getVisibleName(), (int) (v.value * 100f) + ""));
				}
				case 武器を装備していない, 指定の武器タイプの武器を装備している -> {
					res.add(I18N.get(GameSystemI18NKeys.対象が) + v.getVisibleTextI18Nd(WeaponType.valueOf(v.tgtName).getVisibleName()));
				}
				case 指定の状態異常を持っている, 指定の状態異常を持っていない -> {
					res.add(I18N.get(GameSystemI18NKeys.対象が) + v.getVisibleTextI18Nd(ConditionKey.valueOf(v.tgtName).getVisibleName()));
				}
			}
		}
		if (res.isEmpty()) {
			return "";
		}
		res = res.stream().map(p -> "・" + p).toList();
		return String.join(Text.getLineSep(), res);
	}

	static String get計算方法(ActionEvent event) {
		StringBuilder sb = new StringBuilder();
		sb.append("    ");
		sb.append(I18N.get(GameSystemI18NKeys.計算方法));
		switch (event.getCalcMode()) {
			case DC: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.ダメージ計算));
				sb.append("、");
				sb.append(I18N.get(GameSystemI18NKeys.基礎値)).append(":").append(getVisible値(event));
				break;
			}
			case ADD: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.直接作用));
				sb.append("、");
				sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(getVisible値(event));
				break;
			}
			case MUL: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.乗算));
				sb.append("、");
				sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(getVisible値(event));
				break;
			}
			case TO: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.値になる));
				sb.append("、");
				sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(getVisible値(event));
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

	static String get計算方法Percent(ActionEvent event) {
		StringBuilder sb = new StringBuilder();
		sb.append("    ");
		sb.append(I18N.get(GameSystemI18NKeys.計算方法));
		switch (event.getCalcMode()) {
			case DC: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.ダメージ計算));
				sb.append("、");
				sb.append(I18N.get(GameSystemI18NKeys.基礎値)).append(":").append(getVisible値Percent(event));
				sb.append(get起動条件(event));
				break;
			}
			case ADD: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.直接作用));
				sb.append("、");
				sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(getVisible値Percent(event));
				sb.append(get起動条件(event));
				break;
			}
			case MUL: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.乗算));
				sb.append("、");
				sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(getVisible値Percent(event));
				sb.append(get起動条件(event));
				break;
			}
			case TO: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.値になる));
				sb.append("、");
				sb.append(I18N.get(GameSystemI18NKeys.値)).append(":").append(getVisible値Percent(event));
				sb.append(get起動条件(event));
				break;
			}
			case TO_MAX: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.最大値になる));
				sb.append(get起動条件(event));
				break;
			}
			case TO_ZERO: {
				sb.append(":").append(I18N.get(GameSystemI18NKeys.ゼロになる));
				sb.append(get起動条件(event));
				break;
			}
			default: {
				break;
			}
		}
		return sb.toString();
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
