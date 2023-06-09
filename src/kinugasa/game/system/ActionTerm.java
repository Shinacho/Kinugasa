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

import java.util.Objects;
import kinugasa.resource.Nameable;
import kinugasa.resource.db.DBRecord;

/**
 * アクションの発動条件を定義するクラスです。
 *
 * @vesion 1.0.0 - 2022/12/01_21:52:13<br>
 * @author Shinacho<br>
 */
@DBRecord
public class ActionTerm implements Nameable {

	private String name;
	private TermType type;
	private String value;

	public ActionTerm(String name, TermType type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	public TermType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	//アクションが発動できるかを返す。true：可能
	public boolean canExec(ActionTarget tgt) {
		switch (type) {
			case NONE:
				return true;
			case EQ_ITEM:
				return tgt.getUser().getStatus().isEqip(value);
			case EQ_WEAPON_TYPE:
				return tgt.getUser().getStatus().isEqipWMType(value);
			case HAS_BOOK:
				return tgt.getUser().getStatus().getBookBag().contains(value);
			case HAS_ITEM:
				return tgt.getUser().getStatus().getItemBag().contains(value);
			case NO_EQ_ITEM:
				return !tgt.getUser().getStatus().isEqip(value);
			case NO_EQ_WEAPON_TYPE:
				return !tgt.getUser().getStatus().isEqipWMType(value);
			case HAS_CONDITION:
				return tgt.getUser().getStatus().hasCondition(value);
			case NO_CONDITION:
				return !tgt.getUser().getStatus().hasCondition(value);
			case HAS_ITEM_NAME_CONTAINS:
				return tgt.getUser().getStatus().getItemBag().getItems().stream().anyMatch(p -> p.getVisibleName().contains(value));
			case EQ_ANY_ITEM:
				return tgt.getUser().getStatus().isEqip(ItemEqipmentSlotStorage.getInstance().get(value));
			case NO_EQ_ANY_ITEM:
				return !tgt.getUser().getStatus().isEqip(ItemEqipmentSlotStorage.getInstance().get(value));
			case EQ_ITEM_DESC_CONTAINS:
				for (Item i : tgt.getUser().getStatus().getEqipment().values()) {
					if (i == null) {
						continue;
					}
					if (i.getDesc().contains(value)) {
						return true;
					}
				}
				return false;
			case EQ_ITEM_NAME_CONTAINS:
				for (Item i : tgt.getUser().getStatus().getEqipment().values()) {
					if (i == null) {
						continue;
					}
					if (i.getVisibleName().contains(value)) {
						return true;
					}
				}
				return false;
			case STATUS_IS:
				String key = value.split(",")[0];
				String val = value.split(",")[1];
				return (tgt.getUser().getStatus().getEffectedStatus().get(key).getValue() + "").replaceAll(".0", "").equals(val);
			default:
				throw new GameSystemException("undefined termtype : " + type);
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ActionTerm other = (ActionTerm) obj;
		return Objects.equals(this.name, other.name);
	}

	@Override
	public String toString() {
		return "ActionTerm{" + "name=" + name + ", type=" + type + ", value=" + value + '}';
	}

}
