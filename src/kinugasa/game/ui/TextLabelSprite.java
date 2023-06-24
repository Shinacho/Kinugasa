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

import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
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
