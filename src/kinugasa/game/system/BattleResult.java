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
 * @vesion 1.0.0 - 2022/11/26_17:22:59<br>
 * @author Shinacho<br>
 */
public enum BattleResult {
	勝利_敵全滅,
	勝利_敵が全員逃げた,
	敗北_味方全滅,
	敗北_こちらが全員逃げた,;

	public String getVisibleName() {
		return I18N.get(toString());
	}
}
