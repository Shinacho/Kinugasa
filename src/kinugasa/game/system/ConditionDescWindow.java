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
import static kinugasa.game.system.PCStatusWindow.line;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2022/12/21_12:42:59<br>
 * @author Dra211<br>
 */
public class ConditionDescWindow extends PCStatusWindow {

	private MessageWindow mw;
	private List<Status> s;

	public ConditionDescWindow(float x, float y, float w, float h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;
		mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
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

	private int pcIdx;
	private int pageIdx = 0;
	private boolean hasNext = false;
	private int idx = 0;//•\Ž¦‚µ‚½Œ”

	@Override
	public void update() {
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.translate("CURRENT_CONDITION")).append(Text.getLineSep());

		int i = 0;
		int j = 1;//•\Ž¦s”
		for (ConditionValue v : s.get(pcIdx).getCondition()) {
			if (i < pageIdx) {
				i++;
				continue;
			}
			sb.append(" ");
			sb.append(v.getKey().getDesc()).append(Text.getLineSep());
			j++;
			if (j > line) {
				break;
			}

		}
		idx = j;
		hasNext = i < s.get(pcIdx).getCondition().size();

		mw.setText(new Text(sb.toString()));
		mw.allText();
	}

	@Override
	public void nextPage() {
		pageIdx += idx;
	}

	@Override
	public boolean hasNextPage() {
		return hasNext;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}

}
