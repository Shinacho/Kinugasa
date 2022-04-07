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
 * @author Dra0211<br>
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

}
