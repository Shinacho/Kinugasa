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
 * @vesion 1.0.0 - 2022/11/20_15:14:04<br>
 * @author Shinacho<br>
 */
public enum ActionType {
	ATTACK,
	MAGIC,
	ITEM,
	OTHER,;

	public String displayName() {
		switch (this) {
			case ATTACK:
				return I18N.get(GameSystemI18NKeys.攻撃);
			case ITEM:
				return I18N.get(GameSystemI18NKeys.道具);
			case MAGIC:
				return I18N.get(GameSystemI18NKeys.魔術);
			case OTHER:
				return I18N.get(GameSystemI18NKeys.行動);
			default:
				throw new AssertionError("undefined ActionType");
		}
	}

}
