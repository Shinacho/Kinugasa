/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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

/**
 * このスプライトの実装は、指定された領域にフラッシュやフェード効果を表示することができます.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_18:47:19<br>
 * @author Shinacho<br>
 */
public abstract class ScreenEffect extends BasicSprite implements Statable {

	public ScreenEffect(float width, float height) {
		super(0, 0, width, height);
	}

	public ScreenEffect(float x, float y, float w, float h) {
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
