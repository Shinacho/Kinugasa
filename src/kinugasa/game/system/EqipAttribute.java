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

import kinugasa.game.I18N;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_19:25:32<br>
 * @author Shinacho<br>
 */
public enum EqipAttribute {
	装備属性＿男(0),
	装備属性＿女(1),;

	private int value;

	private EqipAttribute(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public String i18n() {
		return I18N.get(toString());
	}
}
