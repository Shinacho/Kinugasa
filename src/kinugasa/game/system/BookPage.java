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

import kinugasa.game.CopyCtor;
import static kinugasa.game.system.ActionEventType.ATTR_IN;
import static kinugasa.game.system.ActionEventType.ATTR_OUT;
import static kinugasa.game.system.ActionEventType.CND_REGIST;
import static kinugasa.game.system.ActionEventType.アイテムロスト;
import static kinugasa.game.system.ActionEventType.アイテム追加;
import static kinugasa.game.system.ActionEventType.ステータス回復;
import static kinugasa.game.system.ActionEventType.ステータス攻撃;
import static kinugasa.game.system.ActionEventType.状態異常付与;
import static kinugasa.game.system.ActionEventType.状態異常解除;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2023/10/25_19:29:23<br>
 * @author Shinacho<br>
 */
public class BookPage implements Nameable {

	private String eventID;

	@CopyCtor
	public BookPage(ActionEvent e) {
		this(e.getId());
	}

	public BookPage(String id) {
		this.eventID = id;
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	public ActionEvent getEvent() {
		return ActionStorage.getInstance().eventOf(eventID).setEvent起動条件(ActionEvent.Event起動条件.条件なしで必ず起動);
	}

	public String getDescI18Nd() {
		return getEvent().getPageDescI18Nd();
	}

	public int getPrice() {
		ActionEvent event = getEvent();
		int val = 1250;
		switch (event.getEventType()) {
			case ATTR_IN:
			case ATTR_OUT: {
				val += Math.abs(event.getValue()) * 103 * 10 * event.getP();
				break;
			}
			case CND_REGIST: {
				val += Math.abs(event.getValue()) * 107 * 10 * event.getP();
				break;
			}
			case アイテムロスト:
			case アイテム追加: {
				break;
			}
			case ステータス回復:
			case ステータス攻撃: {
				val += Math.abs(event.getValue()) * 127 * event.getP();
				break;
			}
			case 状態異常付与:
			case 状態異常解除: {
				val += Math.abs(event.getValue()) * 113 * event.getP();
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
		return "BookPage{" + "eventID=" + eventID + '}';
	}

}
