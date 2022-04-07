/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
 * @author Dra0211<br>
 */
public class FlashEffect extends ScreenEffect {

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
