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
