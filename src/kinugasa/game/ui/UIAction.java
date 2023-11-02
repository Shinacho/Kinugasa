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
package kinugasa.game.ui;

import kinugasa.object.Model;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/09/27<br>
 * @author Shinacho<br>
 * <br>
 */
public abstract class UIAction extends Model implements Comparable<UIAction> {

	private int priority;

	public UIAction() {
		this(1);
	}

	public UIAction(int priority) {
		this.priority = priority;
	}

	public abstract void exec();

	@Override
	public UIAction clone() {
		return (UIAction) super.clone();
	}

	@Override
	public int compareTo(UIAction o) {
		return Integer.compare(priority, o.priority);
	}

}
