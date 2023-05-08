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
