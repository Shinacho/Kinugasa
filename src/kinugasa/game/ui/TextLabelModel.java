/*
 * The MIT License
 *
 * Copyright 2015 Shinacho.
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
import kinugasa.object.Model;
import kinugasa.resource.Nameable;

/**
 * このモデルは、テキストラベルのフォントや表示位置を決定します.
 * <br>
 *
 * @version 1.0.0 - 2015/03/24<br>
 * @author Shinacho<br>
 * <br>
 */
public abstract class TextLabelModel extends Model implements Nameable {

	private final String name;
	private FontModel font;

	public TextLabelModel(String name, FontModel font) {
		this.name = name;
		this.font = font;
		addThis();
	}

	private void addThis() {
		if (!TextLabelModelStorage.getInstance().contains(name)) {
			TextLabelModelStorage.getInstance().add(this);
		}
	}

	@Override
	public final String getName() {
		return name;
	}

	protected final void setProperty(Graphics2D g) {
		g.setFont(font.getFont());
		g.setColor(font.getColor());
	}

	public abstract void draw(GraphicsContext g, TextLabelSprite l);

	public int getFontSize() {
		return font.getFont().getSize();
	}

	public FontModel getFontConfig() {
		return font;
	}

	@Override
	public TextLabelModel clone() {
		return (TextLabelModel) super.clone();
	}

}
