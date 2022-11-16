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

import java.util.ArrayList;
import java.util.List;

/**
 * キャラクタ一人のアイテムを定義するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:59:31<br>
 * @author Dra211<br>
 */
public class ItemBag {

	private int max = 8;
	private List<Item> items = new ArrayList<>();

	public ItemBag() {
	}

	public ItemBag(int max) {
		this.max = max;
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
		return items.size() == max;
	}

	public void add(Item i) {
		items.add(i);
	}

	public void drop(Item i) {
		items.remove(i);
	}

	public boolean contains(Item i) {
		return contains(i.getName());
	}

	public boolean contains(String name) {
		for (Item i : items) {
			if (name.equals(i.getName())) {
				return true;
			}
		}
		return false;
	}

}
