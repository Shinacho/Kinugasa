/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
 * コンテンツを破棄する機能を定義します.
 * <br>
 * このインターフェースは「再ロード」することがないクラスに実装されます。<Br>
 * それらのクラスは必要になったときだけインスタンス化され、不要になったときに
 * disposeメソッドを使用して再帰的に開放されます。<br>
 * 「一時的にメモリを開放し、再ロードするときに備える」Freeableとは
 * 別の使用方法である点に注意してください。<br>
 * <br>
 * @version 1.0.0 - 2013/05/05_17:54:26<br>
 * @author Dra0211<br>
 */
public interface Disposable {

	/**
	 * コンテンツをメモリから破棄します.
	 */
	public void dispose();
}
