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

/**
 *
 * @vesion 1.0.0 - 2022/12/25_12:00:08<br>
 * @author Shinacho<br>
 */
public enum MagicCompositeType {
	SET_ATTR(4),
	HEAL_ATTRIN(6),
	ADD_ATTRIN(6),
	DAMAGE_STATUS_DIRECT(8),
	DAMAGE_STATUS_CALC(8),
	HEAL_STATUS(6),
	ADD_CONDITION(6),
	REMOVE_CONDITION(6),
	ADD_CONDITION_TIME(6),
	ADD_AREA(4),
	ADD_SPELL_TIME(8),
	P(2),
	TO_ALL(20),
	TO_ONE(2),
	TO_TEAM(10),
	ENEMY(6),
	FRIEND(6),
	CAST_COST(0),;

	private int point;

	private MagicCompositeType(int point) {
		this.point = point;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

}
