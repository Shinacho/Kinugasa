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
