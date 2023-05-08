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
package kinugasa.object;


/**
 * このインターフェースを実装したオブジェクトに、「開始している」「終了した」などの状態を調べる機能を提供します.
 * <br>
 * 主に、エフェクトの開始／破棄を判定するために使用されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_19:11:08<br>
 * @author Shinacho<br>
 */
public interface Statable {

	/**
	 * このオブジェクトが「開始している」状態であるかを検査します.
	 * このオブジェクトの状態をリセットできる場合、リセット後も「開始されている」かどうかは
	 * 実装によって異なります。<br>
	 * @return 開始している場合はtrueを返します。<br>
	 */
	public boolean isRunning();

	/**
	 * このオブジェクトが「終了した」状態であるかを検査します.
	 * @return 終了している場合はtrueを返します。<br>
	 */
	public boolean isEnded();
}
