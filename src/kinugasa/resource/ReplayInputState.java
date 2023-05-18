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

import java.io.Serializable;
import kinugasa.game.input.InputState;


/**
 * このクラスはInputStateのクローンをラップし、リプレイとして保存できるようにします.
 * <br>
 * このクラスでは、InputStateにNameableの機能として、入力されたフレーム数を保存する機能が追加されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/20_19:14:08<br>
 * @author Shinacho<br>
 */
public final class ReplayInputState implements Serializable, Nameable {

	/** 入力を検証したタイミングで、通常は起動からの経過フレーム数です. */
	private long time;
	/** この時点での入力状態です. */
	private InputState inputState;

	/**
	 * 新しいリプレイ用入力状態を作成します.
	 *
	 * @param time
	 * @param inputState この時点での入力状態を送信します。
	 */
	public ReplayInputState(long time, InputState inputState) {
		this.time = time;
		this.inputState = inputState.clone();
	}

	/**
	 * 入力状態を取得します.
	 *
	 * @return 入力状態を返します。<br>
	 */
	public InputState getInputState() {
		return inputState;
	}

	/**
	 * フレーム数を取得します.
	 *
	 * @return フレームを返します。<br>
	 */
	public long getTime() {
		return time;
	}

	@Override
	public String getName() {
		return Long.toString(time);
	}

	@Override
	public String toString() {
		return "ReplayInputState{" + "time=" + time + ", inputState=" + inputState + '}';
	}
}
