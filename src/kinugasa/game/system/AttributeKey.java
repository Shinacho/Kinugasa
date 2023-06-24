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
 * @vesion 1.0.0 - 2022/11/15_12:00:00<br>
 * @author Shinacho<br>
 */
public class AttributeKey implements Nameable, Comparable<AttributeKey> {

	private String name;
	private String desc;
	private int order;

	public AttributeKey(String name, String desc, int order) {
		this.name = name;
		this.desc = desc;
		this.order = order;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getOrder() {
		return order;
	}

	@Override
	public int compareTo(AttributeKey o) {
		return order - o.order;
	}

	@Override
	public String toString() {
		return "AttributeKey{" + "name=" + name + ", order=" + order + '}';
	}

}
