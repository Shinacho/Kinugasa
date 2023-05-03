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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_16:17:14<br>
 * @author Dra211<br>
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
			if (map.containsKey(p)) {
				map.put(p, map.get(p) + 1);
			} else {
				map.put(p, 1);
			}

		}
	}

	public void add(Material p) {
		if (map.containsKey(p)) {
			map.put(p, map.get(p) + 1);
		} else {
			map.put(p, 1);
		}
	}

	public void addAll(Map<Material, Integer> m) {
		for (Map.Entry<Material, Integer> e : m.entrySet()) {
			if (map.containsKey(e.getKey())) {
				map.put(e.getKey(), map.get(e.getKey()) + 1);
			} else {
				map.put(e.getKey(), 1);
			}
		}
	}

	public int size() {
		return map.size();
	}

}
