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
package kinugasa.game.field4;

import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;

/**
 *
 * @vesion 1.0.0 - 2023/11/09_19:39:10<br>
 * @author Shinacho<br>
 */
public class MiniMapLabel {

	private D2Idx idx;
	private String value;

	public MiniMapLabel(D2Idx idx, String value) {
		this.idx = idx;
		this.value = value;
	}

	public int getX() {
		return idx.x;
	}

	public int getY() {
		return idx.y;
	}

	public TextLabelSprite getSprite() {
		return new TextLabelSprite(value, new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 0, 0).trimWSize().trimHSize();
	}

	@Override
	public String toString() {
		return "MiniMapLabel{" + "idx=" + idx + ", value=" + value + '}';
	}

}
