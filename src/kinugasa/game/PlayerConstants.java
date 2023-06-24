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

/**
 * プレイヤーの実行環境の定数フィールドにアクセスするためのクラスです.
 * <br>
 * このクラスの情報は、System.getPropertyおよびToolkitから取得されます。<br>
 * また、このクラスは、プレイヤーの入力デバイスの使用状況を格納する唯一の場所ともなります。 その値はゲームの開始時のみ指定できます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/02/11_0:12:16<br>
 * @author Shinacho<br>
 */
public final class PlayerConstants {

	private PlayerConstants() {
		SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
		OS_NAME = System.getProperty("os.name");
		HOME_PATH = System.getProperty("user.home");
		USER_NAME = System.getProperty("user.name");
		DESKTOP_PATH = HOME_PATH + "/Desktop/";
		USER_COUNTRY = System.getProperty("user.country");
	}
	/**
	 * 唯一のインスタンスです.
	 */
	private static final PlayerConstants INSTANCE = new PlayerConstants();

	/**
	 * インスタンスを取得します.
	 *
	 * @return 唯一のインスタンスを取得します.<br>
	 */
	public static PlayerConstants getInstance() {
		return INSTANCE;
	}
	/**
	 * プレイヤーが使用している画面の幅です.
	 */
	public final int SCREEN_WIDTH;
	/**
	 * プレイヤーが使用している画面の高さです.
	 */
	public final int SCREEN_HEIGHT;
	/**
	 * OSの名前です.
	 */
	public final String OS_NAME;
	/**
	 * プレイヤーのホームディレクトリです.
	 */
	public final String HOME_PATH;
	/**
	 * プレイヤーのOSアカウント名です.
	 */
	public final String USER_NAME;
	/**
	 * プレイヤーのデスクトップのパスです.
	 */
	public final String DESKTOP_PATH;
	/**
	 * プレイヤーの国籍です.
	 */
	public final String USER_COUNTRY;
	/**
	 * マウスの使用状況です.
	 */
	private boolean usingMouse;
	/**
	 * キーボードの使用状況です.
	 */
	private boolean usingKeyboard;

	private boolean usingGamePad;

	private int width, height;

	/**
	 * ゲーム開始時の設定によってキーボードが 使用できる状態であるかを調べます.
	 *
	 * @return キーボードを使用する設定でゲームを開始した場合はtrueを返します。<br>
	 */
	public boolean isUsingKeyboard() {
		return usingKeyboard;
	}

	/**
	 * ゲーム開始時の設定によってマウスが 使用できる状態であるかを調べます.
	 *
	 * @return マウスを使用する設定でゲームを開始した場合はtrueを返します。<br>
	 */
	public boolean isUsingMouse() {
		return usingMouse;
	}

	/**
	 * キーボードの使用可否を設定します.
	 *
	 * @param usingKeyboard 使用可能な場合はtrueを送信します。<br>
	 */
	void setUsingKeyboard(boolean usingKeyboard) {
		this.usingKeyboard = usingKeyboard;
	}

	/**
	 * マウスの使用可否を設定します.
	 *
	 * @param usingMouse 使用可能な場合はtrueを送信します。<br>
	 */
	void setUsingMouse(boolean usingMouse) {
		this.usingMouse = usingMouse;
	}

	void setUsingGamePad(boolean usingGamePad) {
		this.usingGamePad = usingGamePad;
	}

	public boolean isUsingGamePad() {
		return usingGamePad;
	}

	void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
