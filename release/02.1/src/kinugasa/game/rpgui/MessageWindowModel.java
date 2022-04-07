/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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

import kinugasa.game.GraphicsContext;
import kinugasa.object.Model;
import kinugasa.resource.Nameable;

/**
 * このモデルは、メッセージウインドウに設定されたパラメタにしたがって、ウインドウの枠やテキストを描画します.
 * <br>
 *
 * @version 1.0.0 - 2015/03/25<br>
 * @author Dra<br>
 * <br>
 */
public abstract class MessageWindowModel extends Model implements Nameable {

	protected final String name;

	public MessageWindowModel(String name) {
		this.name = name;
		addThis();
	}

	private void addThis() {
		MessageWindowModelStorage.getInstance().add(this);
	}

	public abstract void draw(GraphicsContext g, MessageWindowSprite w);

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public MessageWindowModel clone() {
		return (MessageWindowModel) super.clone();
	}

}
