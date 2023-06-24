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

/**
 *
 * @vesion 1.0.0 - 2022/12/25_6:29:30<br>
 * @author Shinacho<br>
 */
public class MessageWindowGroup {

	private List<MessageWindow> windows = new ArrayList<>();

	public MessageWindowGroup(MessageWindow... w) {
		windows.addAll(Arrays.asList(w));
	}

	public void show(int idx) {
		windows.forEach(p -> p.close());
		windows.get(idx).setVisible(true);
	}

	public void show(MessageWindow w) {
		windows.forEach(p -> p.close());
		w.setVisible(true);
	}

	public void closeAll() {
		windows.forEach(p -> p.close());
	}

	public List<MessageWindow> getWindows() {
		return windows;
	}

}
