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
package kinugasa.util;

/**
 * 
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/04/20_17:18:27<br>
 * @author Shinacho<br>
 */
public enum Versions {

	VERSION_01("0.1.000");

	/**
	 * 私です.
	 */
	public static final String AUTHOR = "Shinacho";
	/**
	 * 私のメールアドレスです.
	 */
	public static final String MAIL_TO = "d0211@live.jp";
	/**
	 * 著作権の表記です.
	 */
	public static final String COPY_RIGHT = "Copyright (c) 2011- " + AUTHOR;

	private final String value;

	private Versions(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Versions getLatest() {
		return VERSION_01;
	}
}
