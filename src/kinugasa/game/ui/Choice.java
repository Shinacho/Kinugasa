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

import java.util.List;
import kinugasa.resource.NameNotFoundException;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_9:33:24<br>
 * @author Shinacho<br>
 */
public class Choice extends Text {

	private List<Text> options;

	public Choice(List<Text> options, String name, String text) {
		super(name, text, TimeCounter.TRUE, Integer.MAX_VALUE);
		this.options = options;
	}

	public Choice(List<Text> options, String name, String text, FrameTimeCounter tc, int visibleIdx) {
		super(name, text, tc, visibleIdx);
		this.options = options;
	}

	public List<Text> getOptions() {
		return options;
	}

	public Text getOption(String name) throws NameNotFoundException {
		for (Text t : options) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		throw new NameNotFoundException(name + " is not found");
	}

}
