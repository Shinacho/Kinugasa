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
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_14:03:15<br>
 * @author Dra211<br>
 */
public class ItemWindow extends MessageWindow {

	public ItemWindow(float x, float y, float w, float h) {
		super(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));

	}
	private Status status;
	private int idx;

	public Item getSelected() {
		return status.getItemBag().getItems().get(idx);
	}

	public void setBag(Status status) {
		this.status = status;
		idx = 0;
		updateText();
	}

	public void nextItem() {
		idx++;
		if (idx > status.getItemBag().size()) {
			idx = 0;
		}
		updateText();
	}

	public void prevItem() {
		idx--;
		if (idx < 0) {
			idx = status.getItemBag().size() - 1;
		}
		updateText();
	}

	private final int LINE = 7;

	private void updateText() {
		if (status.getItemBag() == null) {
			throw new GameSystemException("item windows itembag is null");
		}
		if (status.getItemBag().isEmpty()) {
			return;
		}
		StringBuilder s = new StringBuilder();
		List<Item> eqip = new ArrayList<>(status.getEqipment().values());
		for (int i = idx, c = 0; i < status.getItemBag().size(); i++, c++) {
			if (i == idx) {
				s.append("  ->");
			} else {
				s.append("    ");
			}
			if (eqip.contains(status.getItemBag().get(i))) {
				s.append("  (E)");
			} else {
				s.append("     ");
			}
			s.append(status.getItemBag().get(i).getName()).append(Text.getLineSep());
			if (c > LINE) {
				break;
			}
		}
		setText(new Text(s.toString()));
		allText();
	}

}
