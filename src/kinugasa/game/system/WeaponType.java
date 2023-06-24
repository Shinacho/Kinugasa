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
import kinugasa.resource.db.DBRecord;
import kinugasa.resource.*;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_16:55:03<br>
 * @author Shinacho<br>
 */
@DBRecord
public class WeaponType implements Nameable {

	private String id;
	private String visibleName;
	private String desc;

	public WeaponType(String id, String name, String desc) {
		this.id = id;
		this.visibleName = name;
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String getName() {
		return id;
	}

	public String getVisibleName() {
		return visibleName;
	}

	@Override
	public String toString() {
		return "WeaponMagicType{" + "id=" + id + ", visibleName=" + visibleName + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.id);
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
		final WeaponType other = (WeaponType) obj;
		return Objects.equals(this.id, other.id);
	}

}
