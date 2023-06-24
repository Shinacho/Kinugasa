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
 * ゲームコンテンツのI/Oに関する例外です.
 * <br>
 * 通常は、java.io.IOExceptionをラップします。<br>
 *
 * <br>
 * @version 1.0.0 - 2013/04/20_18:58:59<br>
 * @author Shinacho<br>
 */
public class ContentsIOException extends RuntimeException {

	private static final long serialVersionUID = -8593840664351731828L;

	/**
	 * 新しい ContentsIOException のインスタンスを作成.
	 */
	public ContentsIOException() {
	}

	/**
	 * 新しい ContentsIOException のインスタンスを作成.
	 * @param string この例外のメッセージ.<br>
	 */
	public ContentsIOException(String string) {
		super(string);
	}

	/**
	 * Throwableをラップする例外を作成します.
	 * @param thrwbl 投げられた例外を指定します。<br>
	 */
	public ContentsIOException(Throwable thrwbl) {
		super(thrwbl);
	}
}
