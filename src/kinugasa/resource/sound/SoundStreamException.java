/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
