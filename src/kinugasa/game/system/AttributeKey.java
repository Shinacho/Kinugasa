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
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_14:41:12<br>
 * @author Shinacho<br>
 */
public enum AttributeKey implements Nameable {
	斬撃,
	刺突,
	衝撃,
	炎,
	氷,
	水,
	雷,
	風,
	土,
	光,
	闇,
	神秘,
	精神,
	錬金,
	時空,;

	public boolean is物理() {
		return this == 斬撃 || this == 刺突 || this == 衝撃;
	}

	public boolean is魔法() {
		return this != 斬撃 && this != 刺突 && this != 衝撃;
	}

	public String getVisibleName() {
		return I18N.get(toString());
	}

	@Override
	public String getName() {
		return toString();
	}
}
