/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.NoLoopCall;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_16:31:21<br>
 * @author Dra211<br>
 */
public class MaterialPageWindow extends BasicSprite {

	public MaterialPageWindow(float x, float y, float w, float h) {
		super(x, y, w, h);
		mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		update();
	}
	private MessageWindow mw;

	public enum Mode {
		MATERIAL,
		PAGE,
	}
	private Mode mode = Mode.MATERIAL;
	private int start = 0;

	@NoLoopCall
	@Override
	public void update() {
		List<String> list = new ArrayList<>();
		switch (mode) {
			case MATERIAL:
				Map<Material, Integer> map1 = GameSystem.getInstance().getMaterialBag().getMap();
				for (Map.Entry<Material, Integer> e : map1.entrySet()) {
					list.add(e.getKey().getName() + ":" + e.getValue());
				}
				break;
			case PAGE:
				Map<BookPage, Integer> map2 = GameSystem.getInstance().getBookPageBag().getMap();
				for (Map.Entry<BookPage, Integer> e : map2.entrySet()) {
					list.add(e.getKey().getName() + ":" + e.getValue());
				}
				break;
		}
		Collections.sort(list);
		StringBuilder sb = new StringBuilder();
		sb.append("<----").append(I18N.translate(mode.toString())).append(start / PCStatusWindow.line + 1).append("---->").append(Text.getLineSep());
		int i = 0, c = 0;
		for (String s : list) {
			if (c >= PCStatusWindow.line) {
				break;
			}
			if (i < start) {
				i++;
				continue;
			}
			sb.append("  ");
			sb.append(s);
			sb.append(Text.getLineSep());
			i++;
			c++;
		}
		mw.setText(sb.toString());
		mw.allText();
	}

	public void switchMode() {
		switch (mode) {
			case MATERIAL:
				mode = Mode.PAGE;
				break;
			case PAGE:
				mode = Mode.MATERIAL;
				break;
		}
		start = 0;
		update();
	}

	public void nextPage() {
		start += PCStatusWindow.line;
		switch (mode) {
			case MATERIAL:
				if (start >= GameSystem.getInstance().getMaterialBag().size()) {
					start = 0;
				}
				break;
			case PAGE:
				if (start >= GameSystem.getInstance().getBookPageBag().size()) {
					start = 0;
				}
				break;
		}
		update();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}

}
