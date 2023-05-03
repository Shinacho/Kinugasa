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
package kinugasa.game.input;

/**
 * キーボードのキーを指定するための列挙です.
 * <br>
 * Keysは対応するキーのAWTキーコードを持ちます。
 * AWTキーコードについては、java.awt.event.KeyEventクラスのリファレンスを参照してください。<br>
 * <br>
 *
 * @see java.awt.event.KeyEvent
 *
 * @version 1.0.0 - 2013/01/14_13:04:22<br>
 * @version 1.1.0 - 2015/06/19_22:49<br>
 * @author Dra0211<br>
 */
public enum Keys {

	/**
	 * 矢印キーの上です.
	 */
	UP(java.awt.event.KeyEvent.VK_UP),
	/**
	 * 矢印キーの下です.
	 */
	DOWN(java.awt.event.KeyEvent.VK_DOWN),
	/**
	 * 矢印キーの左です.
	 */
	LEFT(java.awt.event.KeyEvent.VK_LEFT),
	/**
	 * 矢印キーの右です.
	 */
	RIGHT(java.awt.event.KeyEvent.VK_RIGHT),
	/**
	 * Wキーと等価です.
	 */
	WASD_UP(java.awt.event.KeyEvent.VK_W),
	/**
	 * Sキーと等価です.
	 */
	WASD_DOWN(java.awt.event.KeyEvent.VK_S),
	/**
	 * Aキーと等価です.
	 */
	WASD_LEFT(java.awt.event.KeyEvent.VK_A),
	/**
	 * Dキーと等価です.
	 */
	WASD_RIGHT(java.awt.event.KeyEvent.VK_D),
	/**
	 * NUMPADの8と等価です.
	 */
	PAD_UP(java.awt.event.KeyEvent.VK_NUMPAD8),
	/**
	 * NUMPADの2と等価です.
	 */
	PAD_DOWN(java.awt.event.KeyEvent.VK_NUMPAD2),
	/**
	 * NUMPADの4と等価です.
	 */
	PAD_LEFT(java.awt.event.KeyEvent.VK_NUMPAD4),
	/**
	 * NUMPADの6と等価です.
	 */
	PAD_RIGHT(java.awt.event.KeyEvent.VK_NUMPAD6),
	/**
	 * NUMPADの7と等価です.
	 */
	PAD_LEFTUP(java.awt.event.KeyEvent.VK_NUMPAD7),
	/**
	 * NUMPADの9と等価です.
	 */
	PAD_RIGHTUP(java.awt.event.KeyEvent.VK_NUMPAD9),
	/**
	 * NUMPADの1と等価です.
	 */
	PAD_LEFTDOWN(java.awt.event.KeyEvent.VK_NUMPAD1),
	/**
	 * NUMPADの3と等価です.
	 */
	PAD_RIGHTDOWN(java.awt.event.KeyEvent.VK_NUMPAD3),
	/**
	 * NUMPADの5と等価です.
	 */
	PAD_CENTER(java.awt.event.KeyEvent.VK_NUMPAD5),
	/**
	 * スペースキーです.
	 */
	SPACE(java.awt.event.KeyEvent.VK_SPACE),
	/**
	 * CTRLキーです.
	 */
	CTRL(java.awt.event.KeyEvent.VK_CONTROL),
	/**
	 * シフトキーです.
	 */
	SHIFT(java.awt.event.KeyEvent.VK_SHIFT),
	/**
	 * エンターキーです.
	 */
	ENTER(java.awt.event.KeyEvent.VK_ENTER),
	/**
	 * エスケープキーです.
	 */
	ESCAPE(java.awt.event.KeyEvent.VK_ESCAPE),
	/**
	 * バックスペースキーです.
	 */
	BACK_SPACE(java.awt.event.KeyEvent.VK_BACK_SPACE),
	/**
	 * 対応するキーを表します.
	 */
	A(java.awt.event.KeyEvent.VK_A),
	/**
	 * 対応するキーを表します.
	 */
	B(java.awt.event.KeyEvent.VK_B),
	/**
	 * 対応するキーを表します.
	 */
	C(java.awt.event.KeyEvent.VK_C),
	/**
	 * 対応するキーを表します.
	 */
	D(java.awt.event.KeyEvent.VK_D),
	/**
	 * 対応するキーを表します.
	 */
	E(java.awt.event.KeyEvent.VK_E),
	/**
	 * 対応するキーを表します.
	 */
	F(java.awt.event.KeyEvent.VK_F),
	/**
	 * 対応するキーを表します.
	 */
	G(java.awt.event.KeyEvent.VK_G),
	/**
	 * 対応するキーを表します.
	 */
	H(java.awt.event.KeyEvent.VK_H),
	/**
	 * 対応するキーを表します.
	 */
	I(java.awt.event.KeyEvent.VK_I),
	/**
	 * 対応するキーを表します.
	 */
	J(java.awt.event.KeyEvent.VK_J),
	/**
	 * 対応するキーを表します.
	 */
	K(java.awt.event.KeyEvent.VK_K),
	/**
	 * 対応するキーを表します.
	 */
	L(java.awt.event.KeyEvent.VK_L),
	/**
	 * 対応するキーを表します.
	 */
	M(java.awt.event.KeyEvent.VK_M),
	/**
	 * 対応するキーを表します.
	 */
	N(java.awt.event.KeyEvent.VK_N),
	/**
	 * 対応するキーを表します.
	 */
	O(java.awt.event.KeyEvent.VK_O),
	/**
	 * 対応するキーを表します.
	 */
	P(java.awt.event.KeyEvent.VK_P),
	/**
	 * 対応するキーを表します.
	 */
	Q(java.awt.event.KeyEvent.VK_Q),
	/**
	 * 対応するキーを表します.
	 */
	R(java.awt.event.KeyEvent.VK_R),
	/**
	 * 対応するキーを表します.
	 */
	S(java.awt.event.KeyEvent.VK_S),
	/**
	 * 対応するキーを表します.
	 */
	T(java.awt.event.KeyEvent.VK_T),
	/**
	 * 対応するキーを表します.
	 */
	U(java.awt.event.KeyEvent.VK_U),
	/**
	 * 対応するキーを表します.
	 */
	V(java.awt.event.KeyEvent.VK_V),
	/**
	 * 対応するキーを表します.
	 */
	W(java.awt.event.KeyEvent.VK_W),
	/**
	 * 対応するキーを表します.
	 */
	X(java.awt.event.KeyEvent.VK_X),
	/**
	 * 対応するキーを表します.
	 */
	Y(java.awt.event.KeyEvent.VK_Y),
	/**
	 * 対応するキーを表します.
	 */
	Z(java.awt.event.KeyEvent.VK_Z),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_1(java.awt.event.KeyEvent.VK_NUMPAD1),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_2(java.awt.event.KeyEvent.VK_NUMPAD2),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_3(java.awt.event.KeyEvent.VK_NUMPAD3),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_4(java.awt.event.KeyEvent.VK_NUMPAD4),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_5(java.awt.event.KeyEvent.VK_NUMPAD6),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_6(java.awt.event.KeyEvent.VK_NUMPAD6),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_7(java.awt.event.KeyEvent.VK_NUMPAD7),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_8(java.awt.event.KeyEvent.VK_NUMPAD8),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_9(java.awt.event.KeyEvent.VK_NUMPAD9),
	/**
	 * NUM_PADの数字キーです.
	 */
	PAD_0(java.awt.event.KeyEvent.VK_NUMPAD0),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_1(java.awt.event.KeyEvent.VK_1),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_2(java.awt.event.KeyEvent.VK_2),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_3(java.awt.event.KeyEvent.VK_3),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_4(java.awt.event.KeyEvent.VK_4),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_5(java.awt.event.KeyEvent.VK_5),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_6(java.awt.event.KeyEvent.VK_6),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_7(java.awt.event.KeyEvent.VK_7),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_8(java.awt.event.KeyEvent.VK_8),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_9(java.awt.event.KeyEvent.VK_9),
	/**
	 * NUM_PADでないナンバーキーです.
	 */
	NUM_0(java.awt.event.KeyEvent.VK_0),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F1(java.awt.event.KeyEvent.VK_F1),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F2(java.awt.event.KeyEvent.VK_F2),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F3(java.awt.event.KeyEvent.VK_F3),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F4(java.awt.event.KeyEvent.VK_F4),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F5(java.awt.event.KeyEvent.VK_F5),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F6(java.awt.event.KeyEvent.VK_F6),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F7(java.awt.event.KeyEvent.VK_F7),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F8(java.awt.event.KeyEvent.VK_F8),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F9(java.awt.event.KeyEvent.VK_F9),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F10(java.awt.event.KeyEvent.VK_F10),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F11(java.awt.event.KeyEvent.VK_F11),
	/**
	 * これらのキーはファンクションキーを定義します.
	 */
	F12(java.awt.event.KeyEvent.VK_F12),
	/**
	 * INSERTキーです.
	 */
	INSERT(java.awt.event.KeyEvent.VK_INSERT),
	/**
	 * DELETEキーです.
	 */
	DELETE(java.awt.event.KeyEvent.VK_DELETE),
	/**
	 * HOMEキーです.
	 */
	HOME(java.awt.event.KeyEvent.VK_HOME),
	/**
	 * ENDキーです.
	 */
	END(java.awt.event.KeyEvent.VK_END),
	/**
	 * PAGEUPキーです.
	 */
	PAGE_UP(java.awt.event.KeyEvent.VK_PAGE_UP),
	/**
	 * PAGEDOWNキーです.
	 */
	PAGE_DOWN(java.awt.event.KeyEvent.VK_PAGE_DOWN),
	/**
	 * @です。
	 */
	AT(java.awt.event.KeyEvent.VK_AT),;
	/**
	 * キーのAWTキー番号.
	 */
	private final int keyCode;

	/**
	 * AWTのバーチャルキー番号を取得します.
	 *
	 * @return 対応するAWTキーコードを返します.<br>
	 */
	public int getKeyCode() {
		return keyCode;
	}

	private Keys(int keyCode) {
		this.keyCode = keyCode;
	}
}
