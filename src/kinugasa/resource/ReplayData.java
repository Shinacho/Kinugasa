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
import kinugasa.util.Random;

/**
 * このクラスを使用して、ランダムシードと入力状態を発行／復元することが出来ます.
 * <br>
 * リプレイのデータ量は入力の多さに比例して多くなります。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/20_18:24:22<br>
 * @author Shinacho<br>
 */
public final class ReplayData extends Storage<ReplayInputState> implements Serializable {

	private static final long serialVersionUID = -7860006069807950463L;
	/** 使用されていたシードです. */
	private long seed;

	/**
	 * 新しいリプレイを作成します.
	 * FPSは60、時間は5分になります。<br>
	 */
	public ReplayData() {
		this(60, 5);
	}

	/**
	 * 新しいリプレイを作成します.
	 *
	 * @param fps    実行中のゲームの最大FSPを指定します。初期データ量に影響します。<br>
	 * @param minute ゲームの実行時間を指定します。初期データ量に影響します。<br>
	 */
	public ReplayData(int fps, float minute) {
		super((int) (fps * minute));
	}

	/**
	 * シードを初期化し、リプレイの保存を開始します.
	 *
	 * @param seed シードを指定します。kinugasa Randomクラスのシードが初期化されます。<br>
	 *
	 * @return thisインスタンスを返します。<br>
	 */
	public ReplayData recStart(long seed) {
		Random.initSeed(this.seed = seed);
		return this;
	}

	/**
	 * 適当なシードを使用してリプレイの保存を開始します.
	 *
	 * @return thisインスタンスを返します。<br>
	 */
	public ReplayData recStart() {
		return recStart(System.nanoTime());
	}

	/**
	 * 保存されたシードを使用して、ランダムクラスを初期化し、リプレイの再生を開始します.
	 */
	public void playStart() {
		Random.initSeed(seed);
	}

	/**
	 * フレーム数と入力状態を指定して、入力イベントを登録します.
	 * このメソッドはadd(new ReplayInputState(frame, inputState))と同じ動作をします。<br>
	 *
	 * @param frame      入力が検知されたフレームを指定します。GameTimeManagerから取得できます。<br>
	 * @param inputState 入力状態を送信します。ReplayInputStateでクローニングされます。<br>
	 */
	public void add(long frame, InputState inputState) {
		super.add(new ReplayInputState(frame, inputState));
	}

	/**
	 * 現在のフレームで入力された状態を復元します.
	 *
	 * @param frame 入力を行うフレームを指定します。GameTimeManagerから取得できます。<br>
	 *
	 * @return 指定されたフレームの入力状態を返します。このフレームに入力が無かった場合はnullを返します。<br>
	 */
	public InputState get(long frame) {
		ReplayInputState state = super.getOrNull(Long.toString(frame));
		return state == null ? null : state.getInputState();
	}
}
