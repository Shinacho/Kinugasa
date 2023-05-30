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

import java.util.Objects;
import java.util.Set;
import kinugasa.resource.Nameable;
import kinugasa.resource.db.DBRecord;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_13:27:22<br>
 * @author Shinacho<br>
 */
@DBRecord
public class Race implements Nameable {

	private String name;
	private int itemBagSize, bookBagSize;
	private Set<ItemEqipmentSlot> eqipSlot;

	public Race(String name, int itemBagSize, int bookBagSize, Set<ItemEqipmentSlot> eqipSlot) {
		this.name = name;
		this.itemBagSize = itemBagSize;
		this.bookBagSize = bookBagSize;
		this.eqipSlot = eqipSlot;
	}

	public int getBookBagSize() {
		return bookBagSize;
	}

	public int getItemBagSize() {
		return itemBagSize;
	}

	public Set<ItemEqipmentSlot> getEqipSlot() {
		return eqipSlot;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Race{" + "name=" + name + ", eqipSlot=" + eqipSlot + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Race other = (Race) obj;
		return Objects.equals(this.name, other.name);
	}

}
