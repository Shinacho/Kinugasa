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
import kinugasa.game.Nullable;

/**
 *
 * @vesion 1.0.0 - 2022/11/21_8:56:26<br>
 * @author Shinacho<br>
 */
public class DropItem {

	private Item item;
	private Material m;
	private int n;
	private float p;

	public static DropItem itemOf(Item i, int n, float p) {
		return new DropItem(i, null, n, p);
	}

	public static DropItem materialOf(Material i, int n, float p) {
		return new DropItem(null, i, n, p);
	}

	private DropItem(Item item, Material m, int n, float p) {
		this.item = item;
		this.m = m;
		this.n = n;
		this.p = p;
	}

	@Nullable
	public Item getItem() {
		return item;
	}

	@Nullable
	public Material getMaterial() {
		return m;
	}

	public int getN() {
		return n;
	}

	public float getP() {
		return p;
	}

	public List<Item> cloneItems() {
		List<Item> result = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			result.add(item.clone());
		}
		return result;
	}

	public List<Material> cloneMaterials() {
		List<Material> result = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			result.add(m);
		}
		return result;
	}

}
