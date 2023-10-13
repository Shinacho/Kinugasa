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

import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/12/26_16:44:54<br>
 * @author Shinacho<br>
 */
public class Material implements Nameable {

	private String id;
	private String visibleName;
	private int value;

	public Material(String id, String visibleName, int value) {
		this.id = id;
		this.visibleName = visibleName;
		this.value = value;
	}

	public void setVisibleName(String visibleName) {
		this.visibleName = visibleName;
	}

	public String getVisibleName() {
		return visibleName;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public String toString() {
		return "Material{" + "name=" + id + ", value=" + value + '}';
	}

}
