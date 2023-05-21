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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.field4.GameSystemI18NKeys;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2022/12/21_16:24:36<br>
 * @author Shinacho<br>
 */
public class AttrDescWindow extends PCStatusWindow {

	private static List<String> unvisibleAttrName = new ArrayList<>();

	public static List<String> getUnvisibleAttrName() {
		return unvisibleAttrName;
	}

	public static void setUnvisibleAttrName(List<String> unvisibleConditionName) {
		AttrDescWindow.unvisibleAttrName = unvisibleConditionName;
	}

	private ScrollSelectableMessageWindow mw;
	private List<Status> s;

	public AttrDescWindow(int x, int y, int w, int h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new ScrollSelectableMessageWindow(x, y, w, h, SimpleMessageWindowModel.maxLine, false);
		mw.setLoop(true);
		updateText();
	}
	private int pcIdx;

	@Override
	public void setPcIdx(int pcIdx) {
		this.pcIdx = pcIdx;
		updateText();
	}

	@Override
	public void nextPc() {
		pcIdx++;
		if (pcIdx >= s.size()) {
			pcIdx = 0;
		}
		mw.reset();
		updateText();
	}

	@Override
	public void prevPc() {
		pcIdx--;
		if (pcIdx < 0) {
			pcIdx = s.size() - 1;
		}
		mw.reset();
		updateText();
	}

	@Override
	public int getPcIdx() {
		return pcIdx;
	}

	private void updateText() {

		Text line1 = new Text("<---" + I18N.get(GameSystemI18NKeys.Xの有効度, s.get(pcIdx).getName()) + "--->");

		List<AttributeValue> list = s.get(pcIdx).getEffectedAttrIn().stream().sorted().collect(Collectors.toList());
		List<Text> l = new ArrayList<>();
		l.add(line1);
		assert list != null;
		boolean midashi = false;
		for (AttributeValue v : list) {
			if (unvisibleAttrName.contains(v.getKey().getName())) {
				continue;
			}
			if (v.getKey().getOrder() > 100 && !midashi) {
				l.add(new Text("--" + I18N.get(GameSystemI18NKeys.状態異常)));
				midashi = true;
			}
			l.add(new Text("  " + v.getKey().getDesc() + ":" + (v.getValue() * 100) + '%'));
		}
		mw.setText(l);
	}

	@Override
	public void update() {
		mw.update();
	}

	@Override
	public void prev() {
		mw.prevSelect();
	}

	@Override
	public void next() {
		mw.nextSelect();
	}

	@Override
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
