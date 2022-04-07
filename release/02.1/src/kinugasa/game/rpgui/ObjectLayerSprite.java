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
package kinugasa.game.rpgui;

import java.util.List;
import kinugasa.object.CompositeSprite;
import kinugasa.object.Sprite;
import kinugasa.resource.Disposable;

/**
 * レイヤが持つスプライトオブジェクトを管理する複合スプライトです.
 * <br>
 *
 * <br>
 * @version 1.0.0 - 2013/05/04_22:37:27<br>
 * @author Dra0211<br>
 */
public class ObjectLayerSprite extends CompositeSprite implements Disposable {

	private static final long serialVersionUID = -6848750219296981368L;

	public ObjectLayerSprite(List<Sprite> spr) {
		super(spr);
	}

	public ObjectLayerSprite(Sprite... spr) {
		super(spr);
	}

	@Override
	public void dispose() {
		clear();
	}
}
