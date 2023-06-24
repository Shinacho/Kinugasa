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
