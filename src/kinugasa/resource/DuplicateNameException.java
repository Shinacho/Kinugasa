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
package kinugasa.resource;


/**
 * 指定された名前が、すでに使用されている場合に投げられる例外です.
 * <br>
 *
 * <br>
 * @version 1.0.0 - 2013/04/20_17:59:34<br>
 * @author Shinacho<br>
 */
public class DuplicateNameException extends RuntimeException {


	/**
	 * 新しい DuplicateNameException のインスタンスを作成.
	 */
	public DuplicateNameException() {
	}

	/**
	 * 新しい DuplicateNameException のインスタンスを作成.
	 * @param msg この例外のメッセージ.<br>
	 */
	public DuplicateNameException(String msg) {
		super(msg);
	}
}
