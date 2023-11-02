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

import java.util.List;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_22:12:30<br>
 * @author Shinacho<br>
 */
public class ActionTarget {

	private Actor user;
	private Action action;
	private List<Actor> tgt;
	private boolean inField;

	public ActionTarget(Actor user, Action action, List<Actor> tgt, boolean inField) {
		this.user = user;
		this.action = action;
		this.tgt = tgt;
		this.inField = inField;
	}

	public Actor getUser() {
		return user;
	}

	public Action getAction() {
		return action;
	}

	public List<Actor> getTgt() {
		return tgt;
	}

	public boolean isInField() {
		return inField;
	}

	@Override
	public String toString() {
		return "ActionTarget{" + "user=" + user + ", action=" + action + ", tgt=" + tgt + ", inField=" + inField + '}';
	}

}
