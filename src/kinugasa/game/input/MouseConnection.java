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
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * マウスカーソルの位置や入力状態を検査するためのマウスアダプタの実装です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:09:50<br>
 * @author Shinacho<br>
 */
public class MouseConnection extends MouseAdapter {

	/**
	 * 唯一のインスタンスです.
	 */
	private static final MouseConnection INSTANCE = new MouseConnection();
	/**
	 * 何も入力されていない状態のMouseStateです.
	 */
	private static final MouseState CLEAR_STATE = new MouseState(false, false, false, false, false, 0, new Point(), new Point(), null);

	static MouseConnection getInstance() {
		return INSTANCE;
	}

	/**
	 * コンポーネントにこのリスナを追加します.
	 *
	 * @param component リスナを登録するコンポーネントを送信します。<br>
	 */
	public static void setListener(Component component) {
		component.addMouseListener(INSTANCE);
		component.addMouseMotionListener(INSTANCE);
		component.addMouseWheelListener(INSTANCE);
	}

	/**
	 * 空のマウスステートを発行します. このメソッドは常に同じインスタンスを返します。<br>
	 *
	 * @return 空のマウスステートを返します。何も押されていない状態です。座標は0,0です。<br>
	 */
	public static MouseState createClearState() {
		return CLEAR_STATE;
	}

	/**
	 * シングルトンクラスです.
	 */
	private MouseConnection() {
		location = new Point();
		locationOnScreen = new Point();
	}

	/**
	 * 現在のマウスの状態を取得します.
	 *
	 * @return マウスの入力状態を返します。<br>
	 */
	public static MouseState getState() {
		return new MouseState(
				INSTANCE.buttonState[MouseEvent.BUTTON1 - 1],
				INSTANCE.buttonState[MouseEvent.BUTTON3 - 1],
				INSTANCE.buttonState[MouseEvent.BUTTON2 - 1],
				INSTANCE.entry,
				INSTANCE.drag,
				INSTANCE.wheelValue,
				INSTANCE.location,
				INSTANCE.locationOnScreen,
				INSTANCE.draggedLocation);
	}
	/**
	 * マウスのボタンの入力状態です.
	 */
	private boolean[] buttonState = {false, false, false, false, false, false};
	/**
	 * マウスがウインドウ内にあるかを判定します.
	 */
	private boolean entry;
	/**
	 * ドラッグ中であるかを判定します.
	 */
	private boolean drag;
	/**
	 * ウインドウ内におけるマウスカーソルの位置です.
	 */
	private Point location;
	/**
	 * スクリーン上におけるマウスカーソルの位置です.
	 */
	private Point locationOnScreen;
	/**
	 * ホイールの回転量です. 上に回すと負数となります。
	 */
	private int wheelValue;
	private Point draggedLocation;
	private Point prevDragLocation;

	/**
	 * マウスアダプタの実装です.
	 *
	 * @param e イベントが送信されます。<br>
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		drag = true;
		draggedLocation = e.getPoint();
	}

	/**
	 * マウスアダプタの実装です.
	 *
	 * @param e イベントが送信されます。<br>
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		location = e.getPoint();
		locationOnScreen = e.getLocationOnScreen();
	}

	/**
	 * マウスアダプタの実装です.
	 *
	 * @param e イベントが送信されます。<br>
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		buttonState[e.getButton() - 1] = true;
	}

	/**
	 * マウスアダプタの実装です.
	 *
	 * @param e イベントが送信されます。<br>
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		buttonState[e.getButton() - 1] = false;
		drag = false;
		draggedLocation = null;
	}

	/**
	 * マウスアダプタの実装です.
	 *
	 * @param e イベントが送信されます。<br>
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		entry = true;
	}

	/**
	 * マウスアダプタの実装です.
	 *
	 * @param e イベントが送信されます。<br>
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		entry = false;
	}

	/**
	 * マウスアダプタの実装です.
	 *
	 * @param e イベントが送信されます。<br>
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		wheelValue = e.getUnitsToScroll();
	}
}
