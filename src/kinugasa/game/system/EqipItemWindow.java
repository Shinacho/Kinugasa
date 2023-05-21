/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.field4.GameSystemI18NKeys;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2022/12/23_19:50:14<br>
 * @author Shinacho<br>
 */
public class EqipItemWindow extends PCStatusWindow {

	private MessageWindow mw;
	private List<Status> s;

	public EqipItemWindow(int x, int y, int w, int h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		update();
	}
	private int pcIdx;

	@Override
	public void setPcIdx(int pcIdx) {
		this.pcIdx = pcIdx;
		update();
	}

	@Override
	public void nextPc() {
		pcIdx++;
		if (pcIdx >= s.size()) {
			pcIdx = 0;
		}
	}

	@Override
	public void prevPc() {
		pcIdx--;
		if (pcIdx < 0) {
			pcIdx = s.size() - 1;
		}
	}

	@Override
	public int getPcIdx() {
		return pcIdx;
	}

	@Override
	public void update() {
		StringBuilder sb = new StringBuilder();
		sb.append("<---").append(I18N.get(GameSystemI18NKeys.Xの装備, s.get(pcIdx).getName())).append("--->").append(Text.getLineSep());

		for (Map.Entry<ItemEqipmentSlot, Item> e : s.get(pcIdx).getEqipment().entrySet()) {
			String key = e.getKey().getName();
			String value = e.getValue() == null ? I18N.get(GameSystemI18NKeys.なし) : e.getValue().getName();
			sb.append("  ").append(key).append(":").append(value).append(Text.getLineSep());
		}

		mw.setText(new Text(sb.toString()));
		mw.allText();
	}

	@Override
	public void prev() {
	}

	@Override
	public void next() {
	}

	@Override
	public MessageWindow getWindow() {
		return mw;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}
}
