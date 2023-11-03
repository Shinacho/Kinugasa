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
import static kinugasa.game.system.ActionEvent.EventType.ATTR_IN;
import static kinugasa.game.system.ActionEvent.EventType.ATTR_OUT;
import static kinugasa.game.system.ActionEvent.EventType.CND_REGIST;
import static kinugasa.game.system.ActionEvent.EventType.アイテムロスト;
import static kinugasa.game.system.ActionEvent.EventType.アイテム追加;
import static kinugasa.game.system.ActionEvent.EventType.ステータス回復;
import static kinugasa.game.system.ActionEvent.EventType.ステータス攻撃;
import static kinugasa.game.system.ActionEvent.EventType.状態異常付与;
import static kinugasa.game.system.ActionEvent.EventType.状態異常解除;
import static kinugasa.game.system.ActionEvent.EventType.独自効果;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2023/10/25_19:29:23<br>
 * @author Shinacho<br>
 */
public class BookPage implements Nameable {

	private ActionEvent event;

	public BookPage(ActionEvent e) {
		this.event = e;
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	public ActionEvent getEvent() {
		return event;
	}

	public String getVisibleName() {
		StringBuilder sb = new StringBuilder();
		switch (event.getEventType()) {
			case ATTR_IN: {
				sb.append(I18N.get(GameSystemI18NKeys.被属性変化の術式));
				sb.append(":");
				sb.append(event.getTgtAttrIn().getVisibleName());
				sb.append(event.getValue() < 0 ? "-" : "+");
				sb.append((int) (event.getValue() * 100));
				sb.append("%(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				break;
			}
			case ATTR_OUT: {
				sb.append(I18N.get(GameSystemI18NKeys.与属性変化の術式));
				sb.append(":");
				sb.append(event.getTgtAttrOut().getVisibleName());
				sb.append(event.getValue() < 0 ? "-" : "+");
				sb.append((int) (event.getValue() * 100));
				sb.append("%(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				break;
			}
			case CND_REGIST: {
				sb.append(I18N.get(GameSystemI18NKeys.状態異常耐性変化の術式));
				sb.append(":");
				sb.append(event.getTgtCndRegist().getVisibleName());
				sb.append(event.getValue() < 0 ? "-" : "+");
				sb.append((int) (event.getValue() * 100));
				sb.append("%(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				break;
			}
			case アイテムロスト: {
				sb.append(I18N.get(GameSystemI18NKeys.アイテムロストの術式));
				sb.append(":");
				sb.append(ActionStorage.getInstance().itemOf(event.getTgtItemID()).getVisibleName());
				sb.append("(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				break;
			}
			case アイテム追加: {
				sb.append(I18N.get(GameSystemI18NKeys.アイテム追加の術式));
				sb.append(":");
				sb.append(ActionStorage.getInstance().itemOf(event.getTgtItemID()).getVisibleName());
				sb.append("(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				break;
			}
			case ステータス回復: {
				sb.append(I18N.get(GameSystemI18NKeys.X回復の術式, event.getTgtStatusKey().getVisibleName()));
				sb.append(":");
				sb.append(event.getAtkAttr().getVisibleName());
				sb.append(event.getValue() < 0 ? "-" : "+");
				sb.append((int) (event.getValue()));
				sb.append("%(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				if (event.getCalcMode() == ActionEvent.CalcMode.DC) {
					sb.append("[");
					sb.append(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる);
					sb.append("]");
				}
				break;
			}
			case ステータス攻撃: {
				sb.append(I18N.get(GameSystemI18NKeys.Xダメージの術式, event.getTgtStatusKey().getVisibleName()));
				sb.append(":");
				sb.append(event.getAtkAttr().getVisibleName());
				sb.append(event.getValue() < 0 ? "-" : "+");
				sb.append((int) (event.getValue()));
				sb.append("%(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				if (event.getCalcMode() == ActionEvent.CalcMode.DC) {
					sb.append("[");
					sb.append(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる);
					sb.append("]");
				}
				break;
			}
			case 状態異常付与: {
				sb.append(I18N.get(GameSystemI18NKeys.状態異常付与の術式));
				sb.append(":");
				sb.append(event.getTgtConditionKey().getVisibleName());
				sb.append("(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				break;
			}
			case 状態異常解除: {
				sb.append(I18N.get(GameSystemI18NKeys.状態異常解除の術式));
				sb.append(":");
				sb.append(event.getTgtConditionKey().getVisibleName());
				sb.append("(");
				sb.append(GameSystemI18NKeys.確率);
				sb.append(((int) (event.getP() * 100))).append("%");
				sb.append(")");
				break;
			}
			case 独自効果: {
				sb.append(I18N.get(GameSystemI18NKeys.不明な効果));
				break;
			}
		}
		return sb.toString();
	}

	public int getPrice() {
		int val = 1000;
		switch (event.getEventType()) {
			case ATTR_IN:
			case ATTR_OUT: {
				val += Math.abs(event.getValue()) * 100 * 10 * event.getP();
				break;
			}
			case CND_REGIST: {
				val += Math.abs(event.getValue()) * 100 * 10 * event.getP();
				break;
			}
			case アイテムロスト:
			case アイテム追加: {
				break;
			}
			case ステータス回復:
			case ステータス攻撃: {
				val += Math.abs(event.getValue()) * 100 * event.getP();
				break;
			}
			case 状態異常付与:
			case 状態異常解除: {
				val += Math.abs(event.getValue()) * 100 * event.getP();
				break;
			}
			case ユーザの武器を装備解除してドロップアイテムに追加:
			case 独自効果: {
				break;
			}
		}
		return val;
	}

	@Override
	public String toString() {
		return "BookPage{" + "event=" + event + '}';
	}

}
