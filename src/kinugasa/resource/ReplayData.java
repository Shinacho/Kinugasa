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
		ReplayInputState state = super.getIfContains(Long.toString(frame));
		return state == null ? null : state.getInputState();
	}
}
