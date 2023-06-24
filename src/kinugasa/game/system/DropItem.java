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

/**
 *
 * @vesion 1.0.0 - 2022/11/21_8:56:26<br>
 * @author Shinacho<br>
 */
public class DropItem {

	private Item item;
	private int n;
	private float p;

	public DropItem(Item item, int n, float p) {
		this.item = item;
		this.n = n;
		this.p = p;
	}

	public Item getItem() {
		return item;
	}

	public int getN() {
		return n;
	}

	public float getP() {
		return p;
	}

	public List<Item> cloneN() {
		List<Item> result = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			result.add(item.clone());
		}
		return result;
	}

}
