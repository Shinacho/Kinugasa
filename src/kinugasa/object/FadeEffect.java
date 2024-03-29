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
import kinugasa.graphics.ColorChanger;
import kinugasa.graphics.GraphicsUtil;

/**
 * 単色フェードイン／フェードアウト効果のエフェクトの実装です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_19:55:37<br>
 * @author Shinacho<br>
 */
public class FadeEffect extends Effect {

	private ColorChanger color;
	private ColorChanger initialColor;
	private boolean running = false;
	private boolean ended = false;

	public FadeEffect(float width, float height, ColorChanger color) {
		super(width, height);
		this.color = color;
		this.initialColor = color.clone();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		if (ended) {
			return;
		}
		running = true;
		g.setColor(color.createColor());
		GraphicsUtil.fillRect(g, getBounds());
		color.update();
		ended = color.isEnded();
	}

	@Override
	public boolean isEnded() {
		return ended;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void reset() {
		color = initialColor.clone();
		ended = false;
	}
}
