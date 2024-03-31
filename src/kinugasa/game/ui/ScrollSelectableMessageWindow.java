/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 * @author Shinacho<br>
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
		this.pos = line1select ? 0 : 1;
		this.visibleIdx = 0;
	}

	public ScrollSelectableMessageWindow(int x, int y, int w, int h, int line) {
		window = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		this.visibleLine = line;
		this.select = line1select ? 0 : 1;
		this.pos = line1select ? 0 : 1;
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

	public void setText(Text t) {
		setText(Text.split(t));
	}

	public void setText(String s) {
		setText(Text.split(Text.noI18N(s)));
	}

	public void setText(List<? extends Text> text) {
		this.text.clear();
		this.text.addAll(text);
		select = line1select ? 0 : 1;
		pos = line1select ? 0 : 1;
		visibleIdx = 0;
	}

	public void allText() {
		window.allText();
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
		if (sb.toString().trim().isEmpty()) {
			window.clearText();
			return;
		}
		window.setText(Text.noI18N(sb.substring(0, sb.length() - 1)));
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
		pos++;
		select++;
		if (pos >= visibleLine) {
			pos--;
			visibleIdx++;
		}
		if (select == size()) {
			pos = (line1select ? 0 : 1);
			select = (line1select ? 0 : 1);
			visibleIdx = 0;
		}
	}

	public void prevSelect() {
		pos--;
		select--;
		if (pos <= - 1) {
			pos++;
			visibleIdx--;
			if (visibleIdx == 1 && !line1select) {
				visibleIdx = 0;
				pos = 1;
			}
		}
		if (select == (line1select ? -1 : 0)) {
			pos = visibleLine - 1;
			select = size() - 1;
			visibleIdx = size() - visibleLine;
		}
	}

	public boolean isFirst() {
		return select == 0;
	}

	public boolean isEnd() {
		return select == size() - 1;
	}

	public void toFirst() {
		pos = visibleIdx = 0;
		select = line1select ? 0 : 1;
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
