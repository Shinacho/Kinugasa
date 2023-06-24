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
package kinugasa.game;

/**
 * ゲームの進行時間を管理し、FPSを一定に保つための機能を提供します.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_12:33:16<br>
 * @author Shinacho<br>
 */
public final class GameTimeManager {

	/**
	 * 単位時間当たりの更新回数.
	 */
	private int updateNum;
	/**
	 * 前回検査時の時刻.
	 */
	private long prevTime;
	/**
	 * 現在の時刻.
	 */
	private long nowTime;
	/**
	 * 現在のFPS.
	 */
	private float fps;
	/**
	 * スリープでの待ち時間.
	 */
	private long waitTime;
	/**
	 * 開始からの経過フレーム.
	 */
	private long totalFrame = 0L;
	/**
	 * スリープの終了時刻.
	 */
	private long endTime;
	private long startTime;

	/**
	 * FPSの最大値が60の新しいTimeManagerを作成します.
	 */
	GameTimeManager() {
		this(60);
	}

	/**
	 * 新しいTimeManagerを作成します.
	 *
	 * @param idealFPS FPSの最大値.<Br>
	 */
	GameTimeManager(int idealFPS) {
		waitTime = 1000 / idealFPS * 1000000;
		updateNum = idealFPS;
		prevTime = System.nanoTime() - 1000000000;
	}

	void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * ゲーム開始時刻を取得します。このゲームが開始された時刻です。
	 * @return ゲーム開始時刻。System.currentTimeMillisです。
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * FPSの最大値を取得します. このメソッドの戻り値はミリ秒制度に丸められる.
	 *
	 * @return FPSの最大値.<br>
	 */
	public long getIdealFPS() {
		return 1000000000 / waitTime;
	}

	/**
	 * FPSの最大値を設定します.
	 *
	 * @param idealFPS FPSの最大値.<br>
	 */
	public void setIdealFPS(int idealFPS) {
		waitTime = 1000 / idealFPS * 1000000;
		updateNum = idealFPS;
	}

	/**
	 * 現在のFPSを取得します.
	 *
	 * @return 現在のFPS.<br>
	 */
	public float getFPS() {
		return fps;
	}

	/**
	 * FPSのみ時列表記を取得します. このメソッドの戻り値は単精度です.<Br>
	 *
	 * @return FPSの文字列表記.通常は少数以下は6桁程度になる.<br>
	 */
	public String getFPSStr() {
		return Float.toString(getFPS());
	}

	/**
	 * 指定した桁数でFPSの文字列表記を取得します.
	 *
	 * @param d 小数点以下の桁数.<Br>
	 *
	 * @return 指定した桁数の切り捨てられたFPS表記.<Br>
	 *
	 * @throws IllegalArgumentException dが負数の場合に投げられる.<Br>
	 */
	public String getFPSStr(int d) throws IllegalArgumentException {
		if (d < 0) {
			throw new IllegalArgumentException("disit is minus");
		}
		String s = getFPSStr();
		int dotPosition = s.indexOf('.');
		if (dotPosition <= 0) {
			return s;
		}
		if (d == 0) {
			return s.substring(0, dotPosition);
		}
		dotPosition += d;
		if (s.length() <= dotPosition) {
			return s;
		}
		return s.substring(0, dotPosition + 1);
	}

	/**
	 * 開始からの経過フレームを取得します.
	 *
	 * @return 開始からの経過フレーム数.<br>
	 */
	public long getTotalFrame() {
		return totalFrame;
	}

	/**
	 * 開始からの経過フレームを初期化します.
	 */
	public void resetTotalFrame() {
		totalFrame = 0L;
	}

	/**
	 * このメソッドを呼ぶと、あらかじめ設定されたFPSを維持できる時間だけ実行したスレッドをsleepします.
	 *
	 * @param startTime 処理開始前の時刻をnano秒精度で送信します。<br>
	 */
	void sleep(long startTime) {
		nowTime = System.nanoTime();
		endTime = nowTime + (waitTime - (nowTime - startTime));//終了時刻(ns
		while (System.nanoTime() < endTime) {//System.nanoTimeがインクリメントされるのを利用している
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		nowTime = System.nanoTime();//スリープしているので現在時刻を更新
		totalFrame++;
		updateNum++;
		if (nowTime - prevTime > 1000000000) {//1s
			fps = (float) updateNum / ((nowTime - prevTime) / 1000000000f);
			updateNum = 0;
			prevTime = nowTime;
		}
	}
}
