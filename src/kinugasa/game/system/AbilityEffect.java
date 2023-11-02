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
 * @vesion 1.0.0 - 2023/10/29_19:49:48<br>
 * @author Shinacho<br>
 */
public interface AbilityEffect {

	public default StatusValueSet effectStatus(Status s, StatusValueSet v) {
		return v;
	}

	public default AttributeValueSet effectAttrIn(Status s, AttributeValueSet v) {
		return v;
	}

	public default AttributeValueSet effectAttrOut(Status s, AttributeValueSet v) {
		return v;
	}

	public default ConditionRegist effectCndRegist(Status s, ConditionRegist v) {
		return v;
	}

}
