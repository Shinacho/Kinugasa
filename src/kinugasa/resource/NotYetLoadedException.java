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
 * 開放可能なオブジェクトがロードされていない場合に 投げることができる例外です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/04/20_22:50:19<br>
 * @author Shinacho<br>
 */
public class NotYetLoadedException extends RuntimeException {

	/**
	 * 新しい NotYetLoadedException のインスタンスを作成.
	 */
	public NotYetLoadedException() {
	}

	/**
	 * 新しい NotYetLoadedException のインスタンスを作成.
	 *
	 * @param msg この例外のメッセージ.<br>
	 */
	public NotYetLoadedException(String msg) {
		super(msg);
	}
}
