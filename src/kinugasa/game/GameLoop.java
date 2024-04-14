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

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import kinugasa.game.input.InputState;
import kinugasa.game.system.GameSystem;
import kinugasa.game.ui.Dialog;

/**
 * ゲームの進行を行うスレッドの実装です.
 * <br>
 * このクラスをゲーム作成者が使用することはありません。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_12:52:13<br>
 * @version 1.0.1 - 2013/04/27_21:11<br>
 * @author Shinacho<br>
 */
public final class GameLoop implements Runnable {

	/**
	 * このスレッドが更新する対象のゲーム.
	 */
	private GameManager game;
	/**
	 * FPSおよび待機時間の管理.
	 */
	private GameTimeManager gtm;
	/**
	 * 起動するメインのスレッド.
	 */
	private Thread thread;
	/**
	 * 起動状態.
	 */
	private volatile boolean exec = false;
	private boolean updateIfNotActive;

	/**
	 * 新しいゲーム用スレッドを作成します. 作成しただけでは、起動されません.<br>
	 *
	 * @param game 対象のゲームインスタンス.<br>
	 * @param gtm FPSおよび待機時間を管理するGameTimeManager.<br>
	 */
	GameLoop(GameManager game, GameTimeManager gtm) {
		this.game = game;
		this.gtm = gtm;
		this.updateIfNotActive = false;
	}

	/**
	 * 新しいゲーム用スレッドを作成します. 作成しただけでは、起動されません.<br>
	 *
	 * @param game 対象のゲームインスタンス.<br>
	 * @param gtm FPSおよび待機時間を管理するGameTimeManager.<br>
	 */
	GameLoop(GameManager game, GameTimeManager gtm, boolean updateIfNotActive) {
		this.game = game;
		this.gtm = gtm;
		this.updateIfNotActive = updateIfNotActive;
	}

	/**
	 * メインループの実装です.
	 */
	@Override
	public void run() {
		try {
			long startTime;
			if (updateIfNotActive) {
				while (exec) {
					startTime = System.nanoTime();
					game.update(gtm, InputState.getInstance());
					game.repaint();
					gtm.sleep(startTime);
				}
			} else {
				while (exec) {
					if (game.getWindow().isActive() || GameSystem.isDebugMode()) {
						startTime = System.nanoTime();
						game.update(gtm, InputState.getInstance());
						game.repaint();
						game.clearEndedEffects();
						gtm.sleep(startTime);
					} else {
						startTime = System.nanoTime();
						try {//ダミー処理
							Thread.sleep(1000 / gtm.getIdealFPS());
						} catch (InterruptedException ex) {
							Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
						}
						gtm.sleep(startTime);
					}
				}
			}
		} catch (Throwable ex) {
			Toolkit.getDefaultToolkit().beep();
			ex.printStackTrace();
			Throwable t = ex;
			String s = t.toString() + "\n";
			while (t.getCause() != null) {
				kinugasa.game.GameLog.print(t.getCause());
				t = t.getCause();
				s += t.toString() + "\n";
			}
			GameLog.print(ex);
			Dialog.error("!", s);
			LockUtil.deleteAllLockFile();
			System.exit(1);
		}
	}

	/**
	 * メインループを終了します.
	 */
	void end() {
		exec = false;
	}

	/**
	 * スレッドを起動し、再描画や更新を開始します.
	 *
	 * @throws IllegalStateException すでに起動している場合に投げられる.<br>
	 */
	void start() throws IllegalStateException {
		if (isStarted()) {
			throw new IllegalStateException("");
		}
		exec = true;
		thread = new Thread(this, game.getOption().getTitle() + ":1");
		thread.start();
	}

	/**
	 * スレッドが起動しているかどうかを検査します.
	 *
	 * @return startが呼び出され、起動している場合はtrueを返します。<br>
	 */
	boolean isStarted() {
		return thread != null;
	}
}
