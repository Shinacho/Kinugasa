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
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_19:23:23<br>
 * @author Shinacho<br>
 */
public class PersonalBag<T extends Nameable> implements Cloneable, Iterable<T> {

	private int max = 8;
	private List<T> items = new ArrayList<>();

	public PersonalBag() {
	}

	public PersonalBag(int max) {
		this.max = max;
	}

	public void clear() {
		items.clear();
	}

	public boolean has(T i) {
		return contains(i);
	}

	public boolean hasAll(T... i) {
		boolean f = true;
		for (T item : this) {
			f &= Arrays.asList(i).contains(item);
		}
		return f;
	}

	@Override
	public Iterator<T> iterator() {
		return items.iterator();
	}

	public List<T> getItems() {
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

	public void add(T i) {
		items.add(i);
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("PersonalBags,addItem:" + i);
		}
	}

	public boolean canAdd() {
		return max > size();
	}

	public void drop(T i) {
		T remove = null;
		for (T item : this) {
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
		T remove = null;
		for (T item : this) {
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

	/**
	 * あとX個持てる　の取得
	 *
	 * @return 残りの猶予。
	 */
	public int remainingSize() {
		return max - size();
	}

	public T get(String id) {
		for (var t : items) {
			if (t.getName().equals(id)) {
				return t;
			}
		}
		throw new NameNotFoundException("PersonalBag : item is not found : " + id);
	}

	public T get(int idx) {
		return items.get(idx);
	}

	public boolean contains(T i) {
		return items.contains(i);
	}

	public boolean contains(String id) {
		for (var t : items) {
			if (t.getName().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public String toString() {
		return "PersonalBag{" + "max=" + max + ", items=" + items + '}';
	}

	@Override
	public PersonalBag clone() {
		try {
			PersonalBag i = (PersonalBag) super.clone();
			return i;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}
}
