/*
 * The MIT License
 *
 * Copyright 2023 Dra.
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
package kinugasa.game.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2023/03/31_6:51:04<br>
 * @author Dra211<br>
 */
public class ScrollSelectableMessageWindow extends BasicSprite {

	private String selectIcon = ">";

	public ScrollSelectableMessageWindow setSelectIcon(String selectIcon) {
		this.selectIcon = selectIcon;
		return this;
	}

	public String getSelectIcon() {
		return selectIcon;
	}

	private List<Text> text = new ArrayList<>();
	private MessageWindow window;
	private int select;//全体の中での選択位置
	private int visibleIdx;
	private int visibleLine = 6;
	private boolean loop = false;
	private boolean line1select = true;

	public MessageWindow getWindow() {
		return window;
	}

	public ScrollSelectableMessageWindow(int x, int y, int w, int h, int line, boolean line1) {
		window = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		this.visibleLine = line;
		this.line1select = line1;
		this.select = line1select ? 0 : 1;
		this.visibleIdx = 0;
	}

	public ScrollSelectableMessageWindow(int x, int y, int w, int h, int line) {
		window = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		this.visibleLine = line;
		this.select = 0;
		this.visibleIdx = 0;
	}

	public ScrollSelectableMessageWindow(int x, int y, int w, int h, int line, String... text) {
		this(x, y, w, h, line);
		this.text.addAll(Arrays.asList(text).stream().map(p -> new Text(p)).collect(Collectors.toList()));
	}

	public ScrollSelectableMessageWindow(int x, int y, int w, int h, int line, Text... text) {
		this(x, y, w, h, line);
		this.text.addAll(Arrays.asList(text));
	}

	public ScrollSelectableMessageWindow(int x, int y, int w, int h, int line, List<String> text) {
		this(x, y, w, h, line);
		this.text.addAll(text.stream().map(p -> new Text(p)).collect(Collectors.toList()));
	}

	public void setText(List<? extends Text> text) {
		this.text.clear();
		this.text.addAll(text);
		select = line1select ? 0 : 1;
		visibleIdx = 0;
	}

	private void updateText() {
		StringBuilder sb = new StringBuilder();
		int l = 0, m = 0;
		for (Text tt : text) {
			if (l < visibleIdx) {
				l++;
				continue;
			}
			if (select == l) {
				sb.append(" ").append(selectIcon);
			} else {
				sb.append("  ");
			}
			sb.append(tt.getText()).append(Text.getLineSep());
			m++;
			if (m >= visibleLine) {
				break;
			}
			l++;
		}
		window.setText(new Text(sb.substring(0, sb.length() - 1)));
		window.allText();
	}

	public List<Text> getText() {
		return text;
	}

	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public Text getSelected() {
		return text.get(select);
	}

	public boolean isLine1select() {
		return line1select;
	}

	public void setLine1select(boolean line1select) {
		this.line1select = line1select;
	}

	public boolean isChoice() {
		return getSelected() instanceof Choice;
	}

	public boolean isChoice(int n) {
		return text.get(n) instanceof Choice;
	}

	public Choice getChoice() {
		return (Choice) getText();
	}

	public int getSelectedIdx() {
		return select;
	}

	@Override
	public void draw(GraphicsContext g) {
		window.draw(g);
	}

	@Override
	public void setVisible(boolean visible) {
		window.setVisible(visible);
	}

	@Override
	public boolean isVisible() {
		return window.isVisible();
	}

	@Override
	public void update() {
		updateText();
		window.update();
	}

	public boolean isLoop() {
		return loop;
	}

	//表示中行中の選択位置
	private int pos = 0;

	public void nextSelect() {
		select++;
		if (select >= size()) {
			if (isLoop()) {
				select = line1select ? 0 : 1;
				visibleIdx = 0;
				pos = 0;
				return;
			} else {
				//最後の選択中の場合は何もしない
				select--;
				return;
			}
		}
		//selectが最終行の場合、次の行を表示
		if (pos < visibleLine - 1) {
			pos++;
		} else {
			visibleIdx++;
		}
	}

	public void prevSelect() {
		select--;
		if (select == 0 && !line1select) {
			if (loop) {
				select = size() - 1;
				visibleIdx = size() - visibleLine;
				pos = visibleLine - 1;
				return;
			} else {
				select++;
			}
		}
		if (select < 0) {
			if (isLoop()) {
				select = size() - 1;
				visibleIdx = size() - visibleLine;
				pos = visibleLine - 1;
				return;
			} else {
				//最後の選択中の場合は何もしない
				select++;
				return;
			}
		}
		//selectが最終行の場合、次の行を表示
		if (pos > 0) {
			pos--;
		} else {
			visibleIdx--;
		}
	}

	public boolean isFirst() {
		return select == 0;
	}

	public boolean isEnd() {
		return select == size() - 1;
	}

	public void toFirst() {
		setText(text);
	}

	public void reset() {
		toFirst();
	}

	public void toEnd() {
		select = visibleIdx = size() - visibleLine;
	}

	public int size() {
		return text.size();
	}

}
