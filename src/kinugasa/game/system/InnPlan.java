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
 * 宿の宿泊プランです。
 *
 * @vesion 1.0.0 - 2023/12/01_20:18:28<br>
 * @author Shinacho<br>
 */
public class InnPlan implements Nameable {

	private String id;
	private InnClass grade;
	private int baseValue;

	public InnPlan(String id, InnClass grade, int baseValue) {
		this.id = id;
		this.grade = grade;
		this.baseValue = baseValue;
	}

	@Override
	public String getName() {
		return id;
	}

	public int getBaseValue() {
		return baseValue;
	}

	public InnClass getGrade() {
		return grade;
	}

	public int getSumPrice(int partyNum) {
		return baseValue * partyNum;
	}

	@Override
	public String toString() {
		return "InnPlan{" + "id=" + id + ", grade=" + grade + ", baseValue=" + baseValue + '}';
	}

}
