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
import static kinugasa.game.system.ActionEventType.ATTR_IN;
import static kinugasa.game.system.ActionEventType.ATTR_OUT;
import static kinugasa.game.system.ActionEventType.CND_REGIST;
import static kinugasa.game.system.ActionEventType.アイテムロスト;
import static kinugasa.game.system.ActionEventType.アイテム追加;
import static kinugasa.game.system.ActionEventType.ステータス回復;
import static kinugasa.game.system.ActionEventType.ステータス攻撃;
import static kinugasa.game.system.ActionEventType.状態異常付与;
import static kinugasa.game.system.ActionEventType.状態異常解除;
import static kinugasa.game.system.ActionEventType.独自効果;
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
		return event.getEventType().getPageDescI18Nd(event);
	}

	public int getPrice() {
		int val = 1000;
		switch (event.getEventType()) {
			case ATTR_IN:
			case ATTR_OUT: {
				val += Math.abs(event.getValue()) * 118 * 10 * event.getP();
				break;
			}
			case CND_REGIST: {
				val += Math.abs(event.getValue()) * 116 * 10 * event.getP();
				break;
			}
			case アイテムロスト:
			case アイテム追加: {
				break;
			}
			case ステータス回復:
			case ステータス攻撃: {
				val += Math.abs(event.getValue()) * 125 * event.getP();
				break;
			}
			case 状態異常付与:
			case 状態異常解除: {
				val += Math.abs(event.getValue()) * 112 * event.getP();
				break;
			}
			default: {
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
