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
package kinugasa.game.system;

import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/21_16:42:22<br>
 * @author Shinacho<br>
 */
public abstract class PCStatusWindow extends BasicSprite {

	public PCStatusWindow(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	public abstract MessageWindow getWindow();

	public abstract void nextPc();

	public abstract void prevPc();

	public abstract int getPcIdx();

	public abstract void setPcIdx(int pcIdx);

	public abstract void next();

	public abstract void prev();

}
