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
import java.util.Set;
import kinugasa.resource.Nameable;
import kinugasa.resource.db.DBRecord;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_13:27:22<br>
 * @author Shinacho<br>
 */
@DBRecord
public class Race implements Nameable {

	private String name;
	private int itemBagSize, bookBagSize;
	private Set<ItemEqipmentSlot> eqipSlot;

	public Race(String name, int itemBagSize, int bookBagSize, Set<ItemEqipmentSlot> eqipSlot) {
		this.name = name;
		this.itemBagSize = itemBagSize;
		this.bookBagSize = bookBagSize;
		this.eqipSlot = eqipSlot;
	}

	public int getBookBagSize() {
		return bookBagSize;
	}

	public int getItemBagSize() {
		return itemBagSize;
	}

	public Set<ItemEqipmentSlot> getEqipSlot() {
		return eqipSlot;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Race{" + "name=" + name + ", eqipSlot=" + eqipSlot + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.name);
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
		final Race other = (Race) obj;
		return Objects.equals(this.name, other.name);
	}

}
