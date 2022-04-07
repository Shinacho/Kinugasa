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
package kinugasa.game.ui;

import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;

/**
 * 1行テキスト表示用のスプライトです.
 * <br>
 *
 * @version 1.0.0 - 2015/03/24<br>
 * @author Dra<br>
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
