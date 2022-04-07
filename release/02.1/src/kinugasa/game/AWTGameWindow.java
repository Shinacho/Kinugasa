/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_14:20:58<br>
 * @author Dra211<br>
 */
public class AWTGameWindow extends Frame implements GameWindow{

	/**
	 * このフレームの内部領域のキャッシュです.座標は0,0になります.
	 */
	private Rectangle internalBounds;
	/**
	 * 再描画に使用するBufferStrategyを提供するためのキャンバスです.
	 */
	private final Canvas canvas;
	/**
	 * ウインドウがアクティブであるかを検査するフラグです.
	 */
	private boolean active;
	/**
	 * フレームを作成します.
	 *
	 * @throws HeadlessException 実行環境がGUIをサポートしていない場合に投げられます。<br>
	 */
	AWTGameWindow() throws HeadlessException {
		add(canvas = new Canvas());
		canvas.setFocusable(false);
		canvas.setEnabled(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				active = true;
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				active = false;
			}
		});
		pack();
	}


	@Override
	public void setCursor(Cursor cursor) {
		super.setCursor(cursor);
	}

	@Override
	public Cursor getCursor() {
		return super.getCursor();
	}

	@Override
	public void setBackground(Color color) {
		canvas.setBackground(color);
	}

	@Override
	public void createBufferStrategy(int i) {
		canvas.createBufferStrategy(2);
	}

	@Override
	public BufferStrategy getBufferStrategy() {
		return canvas.getBufferStrategy();
	}

	@Override
	public void setVisible(boolean bln) {
		super.setVisible(bln);
		canvas.setVisible(true);
		Insets insets = getInsets();
		internalBounds = new Rectangle();
		internalBounds.setSize(getSize());
		canvas.setSize(internalBounds.getSize());
		setSize(getWidth() + insets.left + insets.right, getHeight() + insets.top + insets.bottom);
	}

	@Override
	public void setX(int x) {
		setLocation(x, getY());
	}

	@Override
	public void setY(int y) {
		setLocation(getX(), y);
	}

	@Override
	public int getWidth() {
		return internalBounds.width;
	}

	@Override
	public int getHeight() {
		return internalBounds.height;
	}

	@Override
	public Rectangle getInternalBounds() {
		return (Rectangle) internalBounds.clone();
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public KeyAdapter[] getKeyListeners() {
		return getKeyListeners();
	}

}

