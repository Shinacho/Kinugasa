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
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/21_10:42:47<br>
 * @author Shinacho<br>
 */
public class StatusDescWindow extends PCStatusWindow {

	private static List<String> unvisibleStatusList = new ArrayList<>();

	public static void setUnvisibleStatusList(List<String> unvisibleStatusList) {
		StatusDescWindow.unvisibleStatusList = unvisibleStatusList;
	}

	public static List<String> getUnvisibleStatusList() {
		return unvisibleStatusList;
	}

	private static List<String> visibleMaxStatusList = new ArrayList<>();

	public static void setVisibleMaxStatusList(List<String> visibleMaxStatusList) {
		StatusDescWindow.visibleMaxStatusList = visibleMaxStatusList;
	}

	public static List<String> getVisibleMaxStatusList() {
		return visibleMaxStatusList;
	}

	private List<Status> s;
	private MessageWindow main;

	public StatusDescWindow(int x, int y, int w, int h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;

		main = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		pcIdx = 0;
		update();
	}
	private int pcIdx;

	@Override
	public void setPcIdx(int pcIdx) {
		this.pcIdx = pcIdx;
		update();
	}

	@Override
	public MessageWindow getWindow() {
		return main;
	}

	@Override
	public void next() {
	}

	@Override
	public void prev() {
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
		sb.append("<---");
		sb.append(s.get(pcIdx).getName());
		sb.append("--->").append(Text.getLineSep());
		for (StatusValue v : s.get(pcIdx).getEffectedStatus().stream().sorted().collect(Collectors.toList())) {
			if (unvisibleStatusList.contains(v.getKey().getName())) {
				continue;
			}
			sb.append(" ");
			if (visibleMaxStatusList.contains(v.getKey().getName())) {
				if (v.getKey().getMax() == 1f) {
					float val = v.getValue() * 100;
					sb.append(v.getKey().getDesc()).append(":").append(val).append('%').append(Text.getLineSep());
				} else {
					int val = (int) v.getValue();
					int max = (int) v.getMax();
					sb.append(v.getKey().getDesc()).append(":").append(val).append('／').append(max).append(Text.getLineSep());
				}
			} else {
				if (v.getKey().getName().equals(BattleConfig.StatusKey.canMagic)) {
					sb.append(v.getKey().getDesc()).append(":").append(v.getValue() == 1
							? I18N.get(GameSystemI18NKeys.魔術利用可能) : I18N.get(GameSystemI18NKeys.魔術利用不可));
				} else if (v.getKey().getMax() == 1f) {
					float val = v.getValue() * 100;
					sb.append(v.getKey().getDesc()).append(":").append(val).append('%').append(Text.getLineSep());
				} else {
					int val = (int) v.getValue();
					sb.append(v.getKey().getDesc()).append(":").append(val).append(Text.getLineSep());
				}
			}
		}

		main.setText(new Text(sb.toString()));
		main.allText();

	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		main.draw(g);
	}

}
