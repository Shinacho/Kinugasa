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
package kinugasa.game.input;

import kinugasa.object.Model;


/**
 * 入力デバイスの状態を格納するモデルのスーパークラスです.
 * <br>
 * 入力デバイスの状態は、クローニング可能です。
 * <br>
 * 状態は変更されないように、データをカプセル化する必要があります。<br>
 * <br>
 * @version 1.0.0 - 2013/04/20_21:27:02<br>
 * @author Shinacho<br>
 */
public abstract class InputDeviceState extends Model {

	/**
	 * サブクラスからのみインスタンス化できます.
	 */
	protected InputDeviceState() {
	}

	/**
	 * 検査時点で、何らかの入力があるかを調べます.
	 *
	 * このメソッドによって検査される、デバイスの要素は、実装によって異なります。<br>
	 *
	 * @return 何らかの入力がある場合にtrueを返します。<br>
	 */
	public abstract boolean isAnyInput();

	/**
	 * 検査時点で、何らかのボタンが押されているかを調べます.
	 *
	 * このメソッドによって検査される、デバイスの要素は、実装によって異なります。<br>
	 *
	 * @return 何らかのボタンが押されている場合はtrueを返します。<br>
	 */
	public abstract boolean isAnyButtonInput();

	/**
	 * 検査時点で、何も入力されていないかを調べます.
	 *
	 * このメソッドによって検査される、デバイスの要素は、実装によって異なります。<br>
	 *
	 * @return 何も入力されていない場合にtrueを返します。<br>
	 */
	public abstract boolean isEmptyInput();

	@Override
	public InputDeviceState clone() {
		return (InputDeviceState) super.clone();
	}
}
