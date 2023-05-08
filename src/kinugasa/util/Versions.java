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
package kinugasa.util;


/**
 * フレームワークのバージョンと開発開始日のメモです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/04/20_17:18:27<br>
 * @author Shinacho<br>
 */
public enum Versions {

	VERSION_01("0.1.000");

	/**
	 * 私です.
	 */
	public static final String AUTHOR = "Shinacho";
	/**
	 * 私のメールアドレスです.
	 */
	public static final String MAIL_TO = "d0211@live.jp";
	/**
	 * 著作権の表記です.
	 */
	public static final String COPY_RIGHT = "Copyright (c) 2011- " + AUTHOR + " All rights reserved.";

	private final String value;

	private Versions(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static Versions getLatest() {
		return VERSION_01;
	}
}
