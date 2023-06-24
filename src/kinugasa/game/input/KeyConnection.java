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
