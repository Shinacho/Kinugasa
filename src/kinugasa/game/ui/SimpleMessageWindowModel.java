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
package kinugasa.game.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.FontModel;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_15:42:51<br>
 * @author Dra211<br>
 */
public class SimpleMessageWindowModel extends MessageWindowModel {

	private static String nextIcon = ">";
	private static boolean iconVisible = false;

	private static String selectIcon = ">";
	private static boolean selectIconVisible = false;

	public static void setNextIcon(String nextIcon) {
		SimpleMessageWindowModel.nextIcon = nextIcon;
	}

	public static String getNextIcon() {
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
	private Color inner = new Color(0, 0, 136);
	private FontModel font = FontModel.DEFAULT.clone();
	private final static int BORDER_SIZE = 2;
	private Color cColor = Color.WHITE;

	public SimpleMessageWindowModel() {
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

	public Color getInner() {
		return inner;
	}

	public void setInner(Color inner) {
		this.inner = inner;
	}

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
		Graphics2D g2 = g.create();
		g2.setColor(border1);
		g2.fillRect(x, y, w, h);
		g2.setColor(border2);
		g2.fillRect(x + BORDER_SIZE, y + BORDER_SIZE, w - BORDER_SIZE * 2, h - BORDER_SIZE * 2);
		g2.setColor(border1);
		g2.fillRect(x + BORDER_SIZE * 2, y + BORDER_SIZE * 2, w - BORDER_SIZE * 4, h - BORDER_SIZE * 4);
		g2.setColor(inner);
		g2.fillRect(x + BORDER_SIZE * 3, y + BORDER_SIZE * 3, w - BORDER_SIZE * 6, h - BORDER_SIZE * 6);

		g2.setColor(cColor);
		x += BORDER_SIZE * 6;
		y += BORDER_SIZE * 6 + font.getFont().getSize2D();

		String visibleText = mw.getVisibleText();
		String[] text = visibleText.contains(Text.getLineSep()) ? visibleText.split(Text.getLineSep()) : new String[]{visibleText};

		for (String t : text) {
			g2.drawString(t, x, y);
			y += BORDER_SIZE * 2 + font.getFont().getSize2D();
		}
		y += BORDER_SIZE * 2 + font.getFont().getSize2D();
		// オプションと選択の表示
		if (mw.isAllVisible()) {
			if (mw.getText() instanceof Choice) {
				for (int i = 0; i < mw.getChoice().getOptions().size(); i++) {
					if (i == mw.getSelect()) {
						g2.drawString(selectIcon, x, y);
					}
					String optionVal = mw.getChoice().getOptions().get(i).getText();
					g2.drawString(optionVal, x + (font.getFont().getSize2D() * 2), y);
					y += BORDER_SIZE * 2 + font.getFont().getSize2D();
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
