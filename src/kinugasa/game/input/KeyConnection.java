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

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;

/**
 * キーボードの入力状態を検査するためのKeｙListenerの実装です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:49:55<br>
 * @author Shinacho<br>
 */
public class KeyConnection implements KeyListener {

	/**
	 * キー状態が格納されるビットセットです.
	 */
	private static final BitSet keySet = new BitSet(512);
	/**
	 * 唯一のインスタンスです.
	 */
	private static final KeyConnection INSTANCE = new KeyConnection();
	/**
	 * 何も入力されていない状態のKeyStateです.
	 */
	private static final KeyState CLEAR_STATE = new KeyState(new BitSet(512));

	/**
	 * シングルトンクラスです.インスタンス化できません.<br>
	 */
	private KeyConnection() {
	}

	static KeyConnection getInstance() {
		return INSTANCE;
	}

	/**
	 * コンポーネントにこのリスナを追加します.
	 *
	 * @param component リスナを登録するコンポーネントを送信します。<br>
	 */
	public static void setListener(Component component) {
		component.addKeyListener(INSTANCE);
	}

	/**
	 * 空のキーステートを発行します. このメソッドは常に同じインスタンスを返します。<br>
	 *
	 * @return 空のキーステートを返します。何も押されていない状態です。<br>
	 */
	public static KeyState createClearState() {
		return CLEAR_STATE;
	}

	/**
	 * 現在の入力状態を取得します.
	 *
	 * @return 現在のキー入力状態です。<br>
	 */
	public static KeyState getState() {
		return new KeyState(KeyConnection.keySet);
	}

	/**
	 * キーリスナの実装です.
	 *
	 * @param ke キーイベントが送信されます。<br>
	 */
	@Override
	public void keyTyped(KeyEvent ke) {
	}

	/**
	 * キーリスナの実装です.
	 *
	 * @param ke キーイベントが送信されます。<br>
	 */
	@Override
	public void keyPressed(KeyEvent ke) {
		keySet.set(ke.getKeyCode());
	}

	/**
	 * キーリスナの実装です.
	 *
	 * @param ke キーイベントが送信されます。<br>
	 */
	@Override
	public void keyReleased(KeyEvent ke) {
		keySet.clear(ke.getKeyCode());
	}
}
