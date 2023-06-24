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
 * 指定された名前を持つオブジェクトが見つからない場合に
 * 投げられる例外です.
 * <br>
 *
 * <br>
 * @version 1.0.0 - 2013/04/21_13:16:00<br>
 * @author Shinacho<br>
 */
public class NameNotFoundException extends NotFoundException {


	/**
	 * 新しい NameNotFoundException のインスタンスを作成します.
	 */
	public NameNotFoundException() {
	}

	/**
	 * 新しい NameNotFoundException のインスタンスを作成します.
	 * @param msg この例外のメッセージを指定します。<br>
	 */
	public NameNotFoundException(String msg) {
		super(msg);
	}

	/**
	 * ThrowableをラップするNameNotFoundExceptionを作成します.
	 * @param t 投げられた例外を送信します。<br>
	 */
	public NameNotFoundException(Throwable t) {
		super(t);
	}
}
