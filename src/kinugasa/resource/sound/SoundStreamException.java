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
package kinugasa.resource.sound;


/**
 * サウンドの再生やロードに関する例外です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_18:49:52<br>
 * @author Shinacho<br>
 */
public class SoundStreamException extends RuntimeException {


	/**
	 * 新しい SoundStreamException のインスタンスを作成.
	 */
	public SoundStreamException() {
	}

	/**
	 * 新しい SoundStreamException のインスタンスを作成.
	 *
	 * @param msg この例外のメッセージ.<br>
	 */
	public SoundStreamException(String msg) {
		super(msg);
	}

	/**
	 * ThrowableをラップするSoundStreamExceptionを作成します.
	 * @param thrwbl  投げられた例外を送信します。<br>
	 */
	public SoundStreamException(Throwable thrwbl) {
		super(thrwbl);
	}
}
