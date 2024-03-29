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
package kinugasa.object;

import kinugasa.game.GraphicsContext;

/**
 * このスプライトの実装は、指定された領域にフラッシュやフェード効果を表示することができます.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_18:47:19<br>
 * @author Shinacho<br>
 */
public abstract class Effect extends BasicSprite implements Statable {

	public Effect(float width, float height) {
		super(0, 0, width, height);
	}

	public Effect(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	@Override
	public abstract void draw(GraphicsContext g);

	@Override
	public abstract boolean isEnded();

	@Override
	public abstract boolean isRunning();

	public abstract void reset();
}
