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
 * @vesion 1.0.0 - 2022/11/12_20:19:10<br>
 * @author Shinacho<br>
 */
public class Flag implements Nameable {

	private String name;
	private FlagStatus status;

	public Flag(String name) {
		this.name = name;
		status = FlagStatus.OFF;
	}

	public Flag(String name, FlagStatus status) {
		this.name = name;
		this.status = status;
	}

	@Override
	public String getName() {
		return name;
	}

	public FlagStatus get() {
		return status;
	}

	public void set(FlagStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return name + "=" + status;
	}

}
