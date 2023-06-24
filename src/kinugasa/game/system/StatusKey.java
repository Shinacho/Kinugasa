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
 * ステータス1項目の名前と説明です
 *
 * @vesion 1.0.0 - 2022/11/15_11:11:32<br>
 * @author Shinacho<br>
 */
public class StatusKey implements Nameable, Comparable<StatusKey> {

	private String name;
	private String desc;
	private int order;
	private float min, max;
	private String when0ConditionName;

	public StatusKey(String name, String desc, int order, float min, float max, String when0ConditionName) {
		this.name = name;
		this.desc = desc;
		this.order = order;
		this.min = min;
		this.max = max;
		this.when0ConditionName = when0ConditionName;
	}

	public String getWhen0ConditionName() {
		return when0ConditionName;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

	@Override
	public String toString() {
		return "StatusKey{" + "name=" + name + ", desc=" + desc + ", order=" + order + ", min=" + min + ", max=" + max + ", when0ConditionName=" + when0ConditionName + '}';
	}

	@Override
	public int compareTo(StatusKey o) {
		return order - o.order;
	}

}
