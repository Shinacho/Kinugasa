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
package kinugasa.game.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import kinugasa.game.GraphicsContext;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_15:42:51<br>
 * @author Shinacho<br>
 */
public class SimpleMessageWindowModel extends MessageWindowModel {

	public static int maxLine = 20;
	private String nextIcon = ">";
	private boolean iconVisible = false;

	private static String selectIcon = ">";
	private static boolean selectIconVisible = false;

	public SimpleMessageWindowModel setNextIcon(String nextIcon) {
		this.nextIcon = nextIcon;
		return this;
	}

	public String getNextIcon() {
		return nextIcon;
	}

	public static String getSelectIcon() {
		return selectIcon;
	}

	public static void setSelectIcon(String selectIcon) {
		SimpleMessageWindowModel.selectIcon = selectIcon;
	}

	private static TimeCounter tc = new FrameTimeCounter(30);

	private Color border1 = Color.WHITE;
	private Color border2 = Color.BLACK;
	private Color inner1 = new Color(0, 0, 136);
	private Color inner2 = new Color(44, 44, 196);
	private FontModel font;
	private final static int BORDER_SIZE = 2;
	private Color cColor = Color.WHITE;

	public SimpleMessageWindowModel() {
		font = FontModel.DEFAULT.clone();
	}

	public SimpleMessageWindowModel(String nextIcon) {
		setNextIcon(nextIcon);
	}

	@Override
	public SimpleMessageWindowModel clone() {
		return (SimpleMessageWindowModel) super.clone(); //To change body of generated methods, choose Tools | Templates.
	}

	public Color getBorder1() {
		return border1;
	}

	public void setBorder1(Color border1) {
		this.border1 = border1;
	}

	public Color getBorder2() {
		return border2;
	}

	public void setBorder2(Color border2) {
		this.border2 = border2;
	}
//
//	public Color getInner() {
//		return inner;
//	}
//
//	public void setInner(Color inner) {
//		this.inner = inner;
//	}

	public FontModel getFont() {
		return font;
	}

	public void setFont(FontModel font) {
		this.font = font;
	}

	@Override
	public void draw(GraphicsContext g, MessageWindow mw) {
		if (!mw.isVisible() || !mw.isExist()) {
			return;
		}
		int x = (int) mw.getX();
		int y = (int) mw.getY();
		int w = (int) mw.getWidth();
		int h = (int) mw.getHeight();
		if (w <= 0 || h <= 0) {
			return;
		}
		Graphics2D g2 = g.create();
		g2.setColor(border1);
		g2.fillRect(x, y, w, h);
		g2.setColor(border2);
		g2.fillRect(x + BORDER_SIZE, y + BORDER_SIZE, w - BORDER_SIZE * 2, h - BORDER_SIZE * 2);
		g2.setColor(border1);
		g2.fillRect(x + BORDER_SIZE * 2, y + BORDER_SIZE * 2, w - BORDER_SIZE * 4, h - BORDER_SIZE * 4);

		GradientPaint paint = new GradientPaint(x + BORDER_SIZE * 3, y + BORDER_SIZE * 3, inner1, w - BORDER_SIZE * 6, h - BORDER_SIZE * 6, inner2);
		Paint p = g2.getPaint();
		//グラデーションでインナー描画
		g2.setPaint(paint);
		g2.fillRect(x + BORDER_SIZE * 3, y + BORDER_SIZE * 3, w - BORDER_SIZE * 6, h - BORDER_SIZE * 6);

		//ペイントをもとに戻す
		g2.setPaint(p);

		g2.setColor(cColor);
		x += BORDER_SIZE * 5;
		float size = font == null || font.getFont() == null ? g2.getFont().getSize() : font.getFont().getSize();
		y += BORDER_SIZE * 5 + size;

		String visibleText = mw.getVisibleText();
		String[] text = visibleText.contains(Text.getLineSep()) ? visibleText.split(Text.getLineSep()) : new String[]{visibleText};
		if (font != null) {
			g2.setFont(font.getFont());
		}
		for (String t : text) {
			g2.drawString(t, x, y);
			y += size + BORDER_SIZE * 2;
		}
		y += size;
		// オプションと選択の表示
		if (mw.isAllVisible()) {
			if (mw.getText() instanceof Choice) {
				y -= size / 2;
				for (int i = 0; i < mw.getChoice().getOptions().size(); i++) {
					if (i == mw.getSelect()) {
						g2.drawString(selectIcon, x, y);
					}
					String optionVal = mw.getChoice().getOptions().get(i).getText();
					g2.drawString(optionVal, x + (size * 2), y);
					y += BORDER_SIZE * 3 + size;
				}
			}
		}

		if (mw.isAllVisible()) {
			if (tc.isReaching()) {
				iconVisible = !iconVisible;
			}
			if (iconVisible) {
				x = (int) (mw.getX() + mw.getWidth() - 18);
				y = (int) (mw.getY() + mw.getHeight() - 12);
				g2.drawString(nextIcon, x, y);
			}
		}

		g2.dispose();
	}

}
