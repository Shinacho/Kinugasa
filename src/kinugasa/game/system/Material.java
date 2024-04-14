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

/**
 *
 * @vesion 1.0.0 - 2023/10/14_19:29:23<br>
 * @author Shinacho<br>
 */
public class Material implements Nameable {

	private String id;
	private String visibleName;
	private int price;

	Material(String id, String visibleName, int price) {
		this.id = id;
		this.visibleName = visibleName;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public String getVisibleName() {
		if (GameSystem.isDebugMode()) {
			return visibleName + " *" + id;
		}
		return visibleName;
	}

	@Override
	public String getName() {
		return id;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Material{" + "id=" + id + ", visibleName=" + visibleName + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.id);
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
		final Material other = (Material) obj;
		return Objects.equals(this.id, other.id);
	}

}
