/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
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
