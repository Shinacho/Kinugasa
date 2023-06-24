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
 * リフレクションを使用してインスタンスを作成する際に、
 * 名前解決できなかったときに投げられます.
 * <br>
 *
 * <br>
 * @version 1.0.0 - 2013/05/05_17:44:08<br>
 * @author Shinacho<br>
 */
public class ReflectionClassNotFoundException extends NotFoundException {

	private static final long serialVersionUID = -3047090270187251461L;

	/**
	 * 新しい ReflectionClassNotFoundException のインスタンスを作成します.
	 */
	public ReflectionClassNotFoundException() {
	}

	/**
	 * 新しい ReflectionClassNotFoundException のインスタンスを作成します.
	 * @param msg この例外のメッセージを指定します。<br>
	 */
	public ReflectionClassNotFoundException(String msg) {
		super(msg);
	}

	/**
	 * ThrowableをラップするClassNameNotFoundExceptionを作成します.
	 * @param t 投げられた例外を送信します。<br>
	 */
	public ReflectionClassNotFoundException(Throwable t) {
		super(t);
	}
}
