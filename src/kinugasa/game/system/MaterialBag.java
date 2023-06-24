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
import java.util.List;
import java.util.Map;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_16:17:14<br>
 * @author Shinacho<br>
 */
public class MaterialBag {

	private Map<Material, Integer> map = new HashMap<>();

	public MaterialBag() {
	}

	public Map<Material, Integer> getMap() {
		return map;
	}

	public void addAll(List<Material> list) {
		for (Material p : list) {
			if (map.containsKey(p) && map.get(p) == 99) {
				continue;
			}
			if (map.containsKey(p)) {
				map.put(p, map.get(p) + 1);
			} else {
				map.put(p, 1);
			}

		}
	}

	public void add(Material p) {
		if (map.containsKey(p) && map.get(p) == 99) {
			return;
		}
		if (map.containsKey(p)) {
			map.put(p, map.get(p) + 1);
		} else {
			map.put(p, 1);
		}
	}

	public int size() {
		return map.size();
	}

}
