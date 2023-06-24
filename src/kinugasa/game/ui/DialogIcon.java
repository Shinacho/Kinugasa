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

import javax.swing.JOptionPane;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:53:50<br>
 * @author Shinacho<br>
 */
public enum DialogIcon {
	QUESTION(JOptionPane.QUESTION_MESSAGE),
	INFORMATION(JOptionPane.INFORMATION_MESSAGE),;
	
	private int option;

	private DialogIcon(int option) {
		this.option = option;
	}

	public int getOption() {
		return option;
	}
	
	

}
