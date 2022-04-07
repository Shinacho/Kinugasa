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
 * @author Dra0211<br>
 */
public class FadeEffect extends ScreenEffect {

	private ColorChanger color;
	private ColorChanger initialColor;
	private boolean running = false;
	private boolean ended = false;

	public FadeEffect(float width, float height, ColorChanger color) {
		super(width, height);
		this.color = color;
		this.initialColor = color.clone();
	}

	;
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
