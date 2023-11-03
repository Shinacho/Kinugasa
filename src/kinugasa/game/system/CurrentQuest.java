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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import kinugasa.game.Nullable;

/**
 *
 * @vesion 1.0.0 - May 28, 2023_1:31:44 PM<br>
 * @author Shinacho<br>
 */
public class CurrentQuest {

	private HashMap<String, Quest> questMap = new HashMap<>();
	private static final CurrentQuest INSTANCE = new CurrentQuest();

	public static CurrentQuest getInstance() {
		return INSTANCE;
	}

	public void put(Quest q) {
		questMap.put(q.getQid(), q);
	}

	public Set<Quest> get() {
		return new HashSet<>(questMap.values());
	}

	@Nullable
	public Quest get(String qid) {
		return questMap.get(qid);
	}
}
