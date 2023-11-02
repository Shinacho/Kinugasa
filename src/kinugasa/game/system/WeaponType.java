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
 * @vesion 1.0.0 - 2023/10/15_11:14:55<br>
 * @author Shinacho<br>
 */
public enum WeaponType {
	剣,
	盾,
	刀,
	短剣,
	細剣,
	大剣,
	魔法剣,
	槌,
	槍,
	薙刀,
	大杖,
	小杖,
	棒,
	棍,
	鞭,
	フレイル,
	弓,
	弩,
	銃,;

	public String getVisibleName() {
		return I18N.get(toString());
	}
}
