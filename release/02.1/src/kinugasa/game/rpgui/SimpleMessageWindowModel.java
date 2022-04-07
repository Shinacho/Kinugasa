package kinugasa.game.rpgui;

/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.FontModel;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;

/**
 * シンプルなメッセージウインドウの実装です.
 * <br>
 *
 * @version 1.0.0 - 2015/03/29<br>
 * @author Dra<br>
 * <br>
 */
public class SimpleMessageWindowModel extends MessageWindowModel {

	private Color primaryColor;
	private Color secondaryColor;
	private FontModel font;
	private float lineGap;
	private float firstLineXGap = 8;
	private float yGap = 12;
	private float borderWidth;
	private boolean textAntiAlias = true;
	//
	private BufferedImage backgroundImage;
	//
	private boolean initSelectIcon = false;

	public SimpleMessageWindowModel(String name, Color primaryColor, Color secondaryColor, FontModel font, float lineGap, float borderWidth) {
		super(name);
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.font = font;
		this.lineGap = lineGap;
		this.borderWidth = borderWidth;
	}

	@Override
	public void draw(GraphicsContext g, MessageWindowSprite w) {
		Graphics2D g2 = (Graphics2D) g.create();
		if (backgroundImage == null || w.getWidth() != width || w.getHeight() != height) {
			setSize(w.getWidth(), w.getHeight());
			load();
		}
		if (textAntiAlias) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		g2.drawImage(backgroundImage, (int) w.getX(), (int) w.getY(), null);
		g2.setColor(font.getColor());
		g2.setFont(font.getFont());

		String[] data = w.split();
		float fontSize = font.getFont().getSize2D();
		float x = w.getX() + firstLineXGap;
		float y = w.getY() + fontSize + yGap;
		for (int i = 0; i < data.length; i++) {
			g2.drawString(data[i], x, y + (i * (fontSize + lineGap)));
		}
		w.getCurrentText().isReachingNextCher();
		if (w.getCurrentText().isVisibleAllChar()) {
			if (!w.getCurrentText().hasNext() || w.getCurrentText().getNextIdNum() <= 1) {
				if (w.getContinueIcon() != null && w.getContinueIcon().isVisible()) {
					w.getContinueIcon().draw(g);
				}
			} else {
				if (w.getSelectIcon() != null) {
					if (!initSelectIcon) {
						initSelectIcon = true;
					}
					w.getSelectIcon().draw(g);
				}
			}
		} else {
			initSelectIcon = false;
		}
		g2.dispose();
	}

	public boolean isLoaded() {
		return backgroundImage != null;
	}

	private float width, height;

	public SimpleMessageWindowModel load() {
		if (width != 0 & height != 0) {
			int w = (int) width;
			int h = (int) height;
			backgroundImage = ImageUtil.newImage(w, h);
			Graphics2D g = ImageUtil.createGraphics2D(backgroundImage, RenderingQuality.QUALITY);
			g.setColor(primaryColor);
			g.fillRect(0, 0, w, h);
			g.setColor(secondaryColor);
			g.fillRect((int) borderWidth, (int) borderWidth, w - (int) (borderWidth * 2), h - (int) (borderWidth * 2));
			g.setColor(secondaryColor);
			g.fillRect((int) (borderWidth * 2), (int) (borderWidth * 2), w - (int) (borderWidth * 3), h - (int) (borderWidth * 3));
			g.dispose();
		}
		return this;
	}

	public float getWidth() {
		return width;
	}

	public SimpleMessageWindowModel setWidth(float width) {
		this.width = width;
		return this;
	}

	public float getHeight() {
		return height;
	}

	public SimpleMessageWindowModel setHeight(float height) {
		this.height = height;
		return this;
	}

	public SimpleMessageWindowModel setSize(float w, float h) {
		this.width = w;
		this.height = h;
		return this;
	}

	public Color getPrimaryColor() {
		return primaryColor;
	}

	public SimpleMessageWindowModel setPrimaryColor(Color primaryColor) {
		this.primaryColor = primaryColor;
		return this;
	}

	public Color getSecondaryColor() {
		return secondaryColor;
	}

	public SimpleMessageWindowModel setSecondaryColor(Color secondaryColor) {
		this.secondaryColor = secondaryColor;
		return this;
	}

	public FontModel getFont() {
		return font;
	}

	public SimpleMessageWindowModel setFont(FontModel font) {
		this.font = font;
		return this;
	}

	public float getLineGap() {
		return lineGap;
	}

	public SimpleMessageWindowModel setLineGap(float lineGap) {
		this.lineGap = lineGap;
		return this;
	}

	public float getFirstLineXGap() {
		return firstLineXGap;
	}

	public SimpleMessageWindowModel setFirstLineXGap(float firstLineXGap) {
		this.firstLineXGap = firstLineXGap;
		return this;
	}

	public float getFirstLineYGap() {
		return yGap;
	}

	public SimpleMessageWindowModel setFirstLineYGap(float firstLineYGap) {
		this.yGap = firstLineYGap;
		return this;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public SimpleMessageWindowModel setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

	public boolean isTextAntiAlias() {
		return textAntiAlias;
	}

	public SimpleMessageWindowModel setTextAntiAlias(boolean textAntiAlias) {
		this.textAntiAlias = textAntiAlias;
		return this;
	}

	@Override
	public SimpleMessageWindowModel clone() {
		return (SimpleMessageWindowModel) super.clone();
	}

}
