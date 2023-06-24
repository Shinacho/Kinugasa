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
 * 指定されたオブジェクトが存在しなかったことを通知する例外です.
 * <br>
 *
 * <br>
 * @version 1.0.0 - 2013/04/20_17:58:50<br>
 * @author Shinacho<br>
 */
public abstract class NotFoundException extends ContentsIOException {

	private static final long serialVersionUID = -951498720683908364L;

	/**
	 * 新しい NotFoundException のインスタンスを作成.
	 */
	public NotFoundException() {
	}

	/**
	 * 新しい NotFoundException のインスタンスを作成.
	 * @param msg この例外のメッセージ.<br>
	 */
	public NotFoundException(String msg) {
		super(msg);
	}

	/**
	 * Throwableをラップする例外を作成します.
	 * @param thrwbl 投げられた例外を送信します。<br>
	 */
	public NotFoundException(Throwable thrwbl) {
		super(thrwbl);
	}
}
