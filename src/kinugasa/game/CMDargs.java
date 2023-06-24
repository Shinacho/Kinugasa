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
package kinugasa.game;

/**
 *
 * @vesion 1.0.0 - 2022/12/06_5:08:26<br>
 * @author Shinacho<br>
 */
public class CMDargs {

	private String[] args;

	static void init(String[] args) {
		getInstance().args = args;
	}
	private static final CMDargs INSTANCE = new CMDargs();

	private CMDargs() {
	}

	public static CMDargs getInstance() {
		return INSTANCE;
	}

	public String[] getArgs() {
		return args;
	}

}
