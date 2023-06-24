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

import java.awt.Point;
import java.awt.Shape;

/**
 * マウスによる入力状態を格納します.
 * <br>
 * AnyInput判定メソッドでは、マウスカーソルの位置が0,0でない場合も 入力ありと判定されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:08:38<br>
 * @author Shinacho<br>
 */
public final class MouseState extends InputDeviceState {

	private static final long serialVersionUID = -7984070482396918667L;

	/**
	 * マウスの状態を作成します.
	 *
	 * @param LEFT_BUTTON 左ボタンの入力状態です。<br>
	 * @param RIGHT_BUTTON 右ボタンの入力状態です。<br>
	 * @param WHEEL_BUTTON ホイールボタンの入力状態です。<br>
	 * @param IN_WINDOW ウインドウ領域内にある場合はtrueを送信します。<br>
	 * @param DRAG_NOW ドラッグ中の場合はtrueを送信します。<br>
	 * @param WHEEL_VALUE 直前に入力されたホイールの回転量です。<br>
	 * @param location ウインドウ上のカーソルの位置です。<br>
	 * @param locationOnScreen 。<br>
	 */
	MouseState(
			boolean LEFT_BUTTON,
			boolean RIGHT_BUTTON,
			boolean WHEEL_BUTTON,
			boolean IN_WINDOW,
			boolean DRAG_NOW,
			int WHEEL_VALUE,
			Point location,
			Point locationOnScreen,
			Point draggedLocation) {
		this.LEFT_BUTTON = LEFT_BUTTON;
		this.RIGHT_BUTTON = RIGHT_BUTTON;
		this.WHEEL_BUTTON = WHEEL_BUTTON;
		this.IN_WINDOW = IN_WINDOW;
		this.DRAG_NOW = DRAG_NOW;
		this.WHEEL_VALUE = WHEEL_VALUE;
		this.location = (Point) location.clone();
		this.locationOnScreen = (Point) locationOnScreen.clone();
		this.draggedLocation = draggedLocation;

	}
	/**
	 * 左ボタンの入力状態です.
	 */
	public final boolean LEFT_BUTTON;
	/**
	 * 右ボタンの入力状態です.
	 */
	public final boolean RIGHT_BUTTON;
	/**
	 * ホイールボタンの入力状態です.
	 */
	public final boolean WHEEL_BUTTON;
	/**
	 * ウインドウ領域内にある場合はtrueを送信します.
	 */
	public final boolean IN_WINDOW;
	/**
	 * ドラッグ中の場合はtrueを送信します.
	 */
	public final boolean DRAG_NOW;
	/**
	 * 直前に入力されたホイールの回転量です.
	 */
	public final int WHEEL_VALUE;
	/**
	 * ウインドウ上のカーソルの位置です.
	 */
	private Point location;
	/**
	 * スクリーン上のカーソルの位置です.
	 */
	private Point locationOnScreen;
	/**
	 * 0,0を表す座標です.
	 */
	private static final Point ZERO_POINT = new Point(0, 0);
	private Point draggedLocation;

	@Override
	public boolean isAnyInput() {
		return LEFT_BUTTON
				|| RIGHT_BUTTON
				|| WHEEL_BUTTON
				|| WHEEL_VALUE != 0
				|| DRAG_NOW;
	}

	@Override
	public boolean isAnyButtonInput() {
		return LEFT_BUTTON || RIGHT_BUTTON || WHEEL_BUTTON;
	}

	@Override
	public boolean isEmptyInput() {
		return location.equals(ZERO_POINT)
				&& locationOnScreen.equals(ZERO_POINT)
				&& !LEFT_BUTTON
				&& !RIGHT_BUTTON
				&& !WHEEL_BUTTON
				&& WHEEL_VALUE == 0
				&& !DRAG_NOW;
	}

	/**
	 * マウスのウインドウ上の位置を取得します.
	 * この座標はタイトルバーを含めた座標となりますので、実際にはその補正が必要です。<br>
	 * 補正は、Game.gerWindowから行います。<br>
	 *
	 * @return ウインドウ上の位置のクローンです。<br>
	 */
	public Point getLocation() {
		return (Point) location.clone();
	}

	/**
	 * ウインドウ上の座標を取得します.
	 *
	 * @return ウインドウの位置からのX位置を返します。
	 */
	public int getX() {
		return location.x;
	}

	/**
	 * ウインドウ上の座標を取得します.
	 *
	 * @return ウインドウの位置からのY置を返します。
	 */
	public int getY() {
		return location.y;
	}

	/**
	 * マウスのスクリーン上の位置を取得します.
	 *
	 * @return スクリーン上の位置のクローンです。<br>
	 */
	public Point getLocationOnScreen() {
		return (Point) locationOnScreen.clone();
	}

	/**
	 * スクリーン上の位置を取得します.
	 *
	 * @return スクリーンの左上からのX位置を返します。
	 */
	public int getXOnScreen() {
		return locationOnScreen.x;
	}

	/**
	 * スクリーン上の位置を取得します.
	 *
	 * @return スクリーンの左上からのY位置を返します。
	 */
	public int getYOnScreen() {
		return locationOnScreen.y;
	}

	/**
	 * ウインドウ上のマウスの位置が、shape内にあるかを調べます.
	 *
	 * @param shape 判定する領域を送信します。<br>
	 * @return ウインドウ上のカーソル位置がshape内にある場合はtrueを返します。<br>
	 */
	public boolean contains(Shape shape) {
		return shape == null ? false : LEFT_BUTTON & shape.contains(location);
	}

	/**
	 * マウスボタンが押されているかを検査します.
	 *
	 * @param button 検査するボタンを指定します。<br>
	 * @return 指定されたボタンが押されている場合にtrueを返します。<br>
	 */
	public boolean isPressed(MouseButtons button) {
		switch (button) {
			case LEFT_BUTTON: {
				return LEFT_BUTTON;
			}
			case RIGHT_BUTTON: {
				return RIGHT_BUTTON;
			}
			case WHEEL_BUTTON: {
				return WHEEL_BUTTON;
			}
			default:
				throw new AssertionError();
		}
	}

	public Point getDraggedLocation() {
		return (Point) draggedLocation.clone();
	}

	@Override
	public String toString() {
		return "MouseState{" + "LEFT_BUTTON=" + LEFT_BUTTON + ", RIGHT_BUTTON=" + RIGHT_BUTTON
				+ ", WHEEL_BUTTON=" + WHEEL_BUTTON + ", IN_WINDOW=" + IN_WINDOW + ", DRAG_NOW="
				+ DRAG_NOW + ", WHEEL_VALUE=" + WHEEL_VALUE + ", location=["
				+ location.x + ", " + location.y + "], locationOnScreen=["
				+ locationOnScreen.x + ", " + locationOnScreen.y + "]}";
	}

	@Override
	public MouseState clone() {
		MouseState result = (MouseState) super.clone();
		result.location = (Point) this.location.clone();
		result.locationOnScreen = (Point) this.locationOnScreen.clone();
		return result;
	}
}
