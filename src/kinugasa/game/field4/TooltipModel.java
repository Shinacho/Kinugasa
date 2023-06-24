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

import kinugasa.game.GraphicsContext;
import kinugasa.object.Model;

/**
 *
 * @vesion 1.0.0 - 2022/11/11_12:26:30<br>
 * @author Shinacho<br>
 */
public abstract class TooltipModel extends Model {

	public enum Mode {
		NODE,
		TALK,
		SEARCH,
		NONE,

	}
	protected Mode mode;

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public abstract void drawTooltip(FieldMap fm, GraphicsContext g);

	@Override
	public TooltipModel clone() {
		return (TooltipModel) super.clone(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
	}

}
