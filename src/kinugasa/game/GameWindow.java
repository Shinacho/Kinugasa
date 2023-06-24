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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:48:38<br>
 * @author Shinacho<br>
 */
public interface GameWindow {

	/**
	 * ウインドウのX座標を設定します.
	 *
	 * @param x X座標.<br>
	 */
	public void setX(int x);

	/**
	 * ウインドウのX座標を設定します.
	 *
	 * @param y Y座標.<br>
	 */
	public void setY(int y);

	/**
	 * ウインドウのX座標を取得します.
	 *
	 * @return ウインドウのX座標.<br>
	 */
	public int getX();

	/**
	 * ウインドウのY座標を取得します.
	 *
	 * @return ウインドウのY座標.<br>
	 */
	public int getY();

	/**
	 * ウインドウの幅を取得します.
	 *
	 * @return ウインドウの幅.<Br>
	 */
	public int getWidth();

	/**
	 * ウインドウの高さを取得します.
	 *
	 * @return ウインドウの高さ.<Br>
	 */
	public int getHeight();

	/**
	 * ウインドウの位置を設定します.
	 *
	 * @param x ウインドウのX座標.<br>
	 * @param y ウインドウのY座標.<br>
	 */
	public void setLocation(int x, int y);

	/**
	 * ウインドウの位置を設定します.
	 *
	 * @param p ウインドウの座標.<br>
	 */
	public void setLocation(Point p);

	/**
	 * ウインドウの位置を取得します.
	 *
	 * @return ウインドウの位置.<br>
	 */
	public Point getLocation();

	/**
	 * ウインドウの領域を取得します. このメソッドでは、ウインドウのボーダを含んだウインドウ全体の領域を返します。<br>
	 *
	 * @return ウインドウの位置およびサイズを返します。<br>
	 */
	public Rectangle getBounds();

	/**
	 * ウインドウの使用可能な内部領域を取得します. このメソッドでは、ウインドウのボーダが含まれない実際に描画可能な領域が返されます。<br>
	 * 通常、この領域の位置は0,0に調整され返されます。<br>
	 *
	 * @return ウインドウ内の使用可能な領域を返します。<br>
	 */
	public Rectangle getInternalBounds();

	/**
	 * ウインドウの枠の幅を返します.
	 *
	 * @return ウインドウのボーダの幅を返します。<Br>
	 */
	public Insets getInsets();

	/**
	 * ウインドウの背景色を設定します.
	 *
	 * @param color 背景色。<br>
	 */
	public void setBackground(Color color);

	/**
	 * ウインドウの背景色を取得します。<br>
	 *
	 * @return ウインドウの背景色。<br>
	 */
	public Color getBackground();

	/**
	 * ウインドウに設定されているアイコンを取得します.
	 *
	 * @return ウインドウのアイコン。<br>
	 */
	public Image getIconImage();

	/**
	 * ウインドウのアイコンを変更します.
	 *
	 * @param image ウインドウのアイコン。<br>
	 */
	public void setIconImage(Image image);

	/**
	 * ウインドウのタイトルバーの文字列を取得します.
	 *
	 * @return ウインドウのタイトルバーに表示されている文字列を返します。<br>
	 */
	public String getTitle();

	/**
	 * ウインドウのタイトルバーを設定します.
	 *
	 * @param title ウインドウのタイトルバーに表示する文字列を指定します。<br>
	 */
	public void setTitle(String title);

	/**
	 * ウインドウが選択されており、表示中であるかを検査します. 通常、ウインドウがアクティブでない場合はゲームの進行は停止します。<br>
	 *
	 * @return ウインドウがアクティブである場合はtrueを返します。<br>
	 */
	public boolean isActive();

	/**
	 * マウスカーソルを設定します.
	 *
	 * @param cursor 新しいカーソルを指定します。<br>
	 */
	public void setCursor(Cursor cursor);

	/**
	 * 現在のマウスカーソルを取得します.
	 *
	 * @return 使用中のマウスカーソルが返されます。<br>
	 */
	public Cursor getCursor();

}
