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
package kinugasa.resource.text;

/**
 * XMFファイルのフォーマットがDTDに沿っていない場合に投げられる例外です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/03/14_15:11:06.<br>
 * @author Shinacho ( <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public class IllegalXMLFormatException extends FileFormatException {

	private static final long serialVersionUID = -8305393008312141380L;

	/**
	 * 新しい IllegalXMLFormatException のインスタンスを作成.
	 *
	 * @param msg この例外のメッセージ.<br>
	 */
	public IllegalXMLFormatException(String msg) {
		super(msg);
	}

	/**
	 * Throwableから例外を作成します.
	 * @param ex 投げられた例外を送信します。<br>
	 */
	public IllegalXMLFormatException(Exception ex) {
		super(ex);
	}
}
