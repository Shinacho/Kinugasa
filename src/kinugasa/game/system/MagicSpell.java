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
 * @vesion 1.0.0 - 2022/12/02_15:05:29<br>
 * @author Shinacho<br>
 */
public class MagicSpell {

	private BattleCharacter user;
	private Action action;
	private boolean player;

	public MagicSpell(BattleCharacter user, Action action, boolean player) {
		this.user = user;
		this.action = action;
		this.player = player;
	}

	public boolean isPlayer() {
		return player;
	}

	public Action getAction() {
		return action;
	}

	public BattleCharacter getUser() {
		return user;
	}

}
