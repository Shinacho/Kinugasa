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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kinugasa.resource.NameNotFoundException;

/**
 * キャラクタ一人のアイテムを定義するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:59:31<br>
 * @author Shinacho<br>
 */
public class ItemBag implements Cloneable, Iterable<Item> {

	private int max = 8;
	private List<Item> items = new ArrayList<>();

	public ItemBag() {
	}

	public ItemBag(int max) {
		this.max = max;
	}

	public boolean has(Item i) {
		return contains(i);
	}

	public boolean hasAll(Item... i) {
		boolean f = true;
		for (Item item : this) {
			f &= Arrays.asList(i).contains(item);
		}
		return f;
	}

	@Override
	public Iterator<Item> iterator() {
		return items.iterator();
	}

	public List<Item> getItems() {
		return items;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public boolean isMax() {
		return items.size() == max - 1;
	}

	public void add(Item i) {
		items.add(i);
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("ItemBags,addItem:" + i);
		}
	}

	public boolean canAdd() {
		return max > size();
	}

	public void drop(Item i) {
		Item remove = null;
		for (Item item : this) {
			if (i.getName().equals(item.getName())) {
				remove = item;
				break;
			}
		}
		if (remove != null) {
			items.remove(remove);
		}
	}

	public void drop(String itemID) {
		Item remove = null;
		for (Item item : this) {
			if (itemID.equals(item.getName())) {
				remove = item;
				break;
			}
		}
		if (remove != null) {
			items.remove(remove);
		}
	}

	public int size() {
		return items.size();
	}

	public int remainingSize() {
		return max - size();
	}

	public Item get(String name) {
		for (var t : items) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		throw new NameNotFoundException("ItemBag : item is not found : " + name);
	}

	public Item get(int idx) {
		return items.get(idx);
	}

	public boolean contains(Item i) {
		return items.contains(i);
	}

	public boolean contains(String name) {
		return contains(ItemStorage.getInstance().get(name));
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public String toString() {
		return "ItemBag{" + "max=" + max + ", items=" + items + '}';
	}

	@Override
	public ItemBag clone() {
		try {
			ItemBag i = (ItemBag) super.clone();
			return i;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
