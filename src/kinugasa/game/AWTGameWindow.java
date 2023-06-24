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
 * AWTを利用した、標準のゲームウインドウの実装です。
 *
 * @vesion 1.0.0 - 2021/08/17_14:20:58<br>
 * @author Shinacho<br>
 */
public class AWTGameWindow extends Frame implements GameWindow {

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
		setUndecorated(false);
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
		super.setBackground(color);
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
