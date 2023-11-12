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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import kinugasa.game.GraphicsContext;
import kinugasa.game.NoLoopCall;
import kinugasa.graphics.ARGBColor;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.object.BasicSprite;

/**
 * 1行テキスト表示用のスプライトです.
 * <br>
 *
 * @version 1.0.0 - 2015/03/24<br>
 * @author Shinacho<br>
 * <br>
 */
public class TextLabelSprite extends BasicSprite {

	private String text;
	private TextLabelModel labelModel;

	public TextLabelSprite(CharSequence text, TextLabelModel labelModel, float x, float y) {
		super(x, y, 1, 1);
		this.text = text.toString();
		this.labelModel = labelModel;
	}

	public TextLabelSprite(String text, TextLabelModel labelModel, float x, float y, float w, float h) {
		super(x, y, w, h);
		this.text = text;
		this.labelModel = labelModel;
	}

	@NoLoopCall("its heavy")
	public TextLabelSprite trimWSize() {
		BufferedImage image = ImageUtil.newImage(2048, labelModel.getFontSize());
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.SPEED);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, (int) getWidth(), (int) getHeight());
		g.setColor(Color.WHITE);
		g.setFont(labelModel.getFontConfig().getFont());
		g.drawString(text, 0, labelModel.getFontSize());
		g.dispose();

		//テキストサイズの探索
		int[][] pix = ImageUtil.getPixel2D(image);
		for (int y = 0; y < pix.length; y++) {
			for (int x = pix[y].length - 1; x >= 0; x--) {
				for (int yy = 0; yy < pix.length; yy++) {
					if (ARGBColor.getRed(pix[yy][x]) == 255) {
						setWidth(x);
						return this;
					}
				}
			}
		}
		setWidth(0);
		return this;
	}

	@NoLoopCall("its heavy")
	public TextLabelSprite trimHSize() {
		BufferedImage image = ImageUtil.newImage(2048, labelModel.getFontSize()*2);
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.SPEED);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, (int) getWidth(), (int) getHeight());
		g.setColor(Color.WHITE);
		g.setFont(labelModel.getFontConfig().getFont());
		g.drawString(text, 0, labelModel.getFontSize());
		g.dispose();

		//テキストサイズの探索
		int[][] pix = ImageUtil.getPixel2D(image);
		for (int y = pix.length -1; y >= 0 ; y--) {
			for (int x = 0; x < pix[y].length; x ++ ) {
				if (ARGBColor.getRed(pix[y][x]) == 255) {
					setHeight(y);
					return this;
				}
			}
		}
		setHeight(0);
		return this;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public TextLabelModel getLabelModel() {
		return labelModel;
	}

	public void setLabelModel(TextLabelModel labelModel) {
		this.labelModel = labelModel;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (isVisible() & isExist()) {

			labelModel.draw(g, this);
		}
	}

	public void draw(GraphicsContext g, TextLabelModel model) {
		if (isVisible() & isExist()) {
			model.draw(g, this);
		}
	}
}
