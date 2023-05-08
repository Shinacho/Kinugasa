/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			System.out.println("ItemBags,addItem:" + i);
		}
	}

	public boolean canAdd() {
		return max > size();
	}

	public void drop(Item i) {
		if (items.contains(i)) {
			items.remove(i);
			if (GameSystem.isDebugMode()) {
				System.out.println("ItemBags,dropItem:" + i);
			}
		}
	}

	public int size() {
		return items.size();
	}

	public Item get(int idx) {
		return items.get(idx);
	}

	void drop(String name) {
		drop(ItemStorage.getInstance().get(name));
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
