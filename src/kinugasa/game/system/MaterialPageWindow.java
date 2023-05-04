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
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.NoLoopCall;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_16:31:21<br>
 * @author Dra211<br>
 */
public class MaterialPageWindow extends BasicSprite {

	public MaterialPageWindow(int x, int y, int w, int h) {
		super(x, y, w, h);
		mw = new ScrollSelectableMessageWindow(x, y, w, h, 20, false);
		mw.setLoop(true);
		updateText();
	}
	private ScrollSelectableMessageWindow mw;

	public enum Mode {
		MATERIAL,
		PAGE,
	}
	private Mode mode = Mode.MATERIAL;

	private void updateText() {
		List<String> list = new ArrayList<>();
		switch (mode) {
			case MATERIAL:
				Map<Material, Integer> map1 = GameSystem.getInstance().getMaterialBag().getMap();
				for (Map.Entry<Material, Integer> e : map1.entrySet()) {
					list.add(e.getKey().getName() + ":" + e.getValue() + (I18N.translate("VALUE") + ":" + e.getKey().getValue()));
				}
				break;
			case PAGE:
				Map<BookPage, Integer> map2 = GameSystem.getInstance().getBookPageBag().getMap();
				for (Map.Entry<BookPage, Integer> e : map2.entrySet()) {
					list.add(e.getKey().getName() + ":" + e.getValue() + (I18N.translate("VALUE") + ":" + e.getKey().getSaleValue()));
				}
				break;
		}
		Collections.sort(list);

		list.add(0, "<----" + I18N.translate(mode.toString()) + "---->");

		mw.setText(list.stream().map(p -> new Text(p)).collect(Collectors.toList()));
	}

	@Override
	public void update() {
		mw.update();
	}

	public void switchMode() {
		switch (mode) {
			case MATERIAL:
				mode = Mode.PAGE;
				mw.reset();
				break;
			case PAGE:
				mode = Mode.MATERIAL;
				mw.reset();
				break;
		}
		updateText();
	}

	public void prev() {
		mw.prevSelect();
	}

	public void next() {
		mw.nextSelect();
	}

	public MessageWindow getWindow() {
		return mw.getWindow();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}

}
