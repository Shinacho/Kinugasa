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

import java.awt.Color;
import kinugasa.game.GraphicsContext;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.util.TimeCounter;

/**
 * 単色のフラッシュ効果のエフェクトの実装です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:10:48<br>
 * @author Shinacho<br>
 */
public class FlashEffect extends Effect {

	private Color color;
	private TimeCounter blinkRate;//TRUEでフラッシュ
	private TimeCounter flashTime;//TRUEで終了
	private boolean ended = false;
	private boolean running = false;

	;

	public FlashEffect(Color color, TimeCounter blinkRate, TimeCounter flashTime, float x, float y, float w, float h) {
		super(x, y, w, h);
		this.color = color;
		this.blinkRate = blinkRate;
		this.flashTime = flashTime;
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
		if (blinkRate.isReaching()) {
			g.setColor(color);
			GraphicsUtil.fillRect(g, getBounds());
		}
		if (flashTime.isReaching()) {
			ended = true;
			setVisible(false);
		}
	}

	@Override
	public boolean isEnded() {
		return ended;
	}

	@Override
	public void reset() {
		blinkRate.reset();
		flashTime.reset();
		ended = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}
}
