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
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/21_16:24:36<br>
 * @author Dra211<br>
 */
public class AttrDescWindow extends PCStatusWindow {

	private static List<String> unvisibleAttrName = new ArrayList<>();

	public static List<String> getUnvisibleAttrName() {
		return unvisibleAttrName;
	}

	public static void setUnvisibleAttrName(List<String> unvisibleConditionName) {
		AttrDescWindow.unvisibleAttrName = unvisibleConditionName;
	}

	private MessageWindow mw;
	private List<Status> s;

	public AttrDescWindow(float x, float y, float w, float h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		pageIdx = 0;
		update();
	}
	private int pcIdx;
	private int pageIdx = 0;

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
		sb.append(I18N.translate("ATTR")).append(pageIdx + 1).append(Text.getLineSep());

		List<AttributeValue> list = null;
		switch (pageIdx) {
			case 0:
				list = s.get(pcIdx).getEffectedAttrIn().stream().sorted().filter(p -> p.getKey().getOrder() <= 100).collect(Collectors.toList());
				break;
			case 1:
				list = s.get(pcIdx).getEffectedAttrIn().stream().sorted().filter(p -> p.getKey().getOrder() > 100).collect(Collectors.toList());
				break;
		}
		assert list != null;
		for (AttributeValue v : list) {
			if (unvisibleAttrName.contains(v.getKey().getName())) {
				continue;
			}
			sb.append(" ");
			sb.append(v.getKey().getDesc()).append(":").append(v.getValue() * 100).append('%').append(Text.getLineSep());
		}

		mw.setText(new Text(sb.toString()));
		mw.allText();
	}

	@Override
	public void nextPage() {
		pageIdx++;
		if (pageIdx > 1) {
			pageIdx = 1;
		}
	}

	@Override
	public boolean hasNextPage() {
		return pageIdx == 0;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}
}
