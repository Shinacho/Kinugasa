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
import java.util.Iterator;
import java.util.List;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_18:55:46<br>
 * @author Shinacho<br>
 */
public class BookBag implements Cloneable, Iterable<Book> {

	private int max = 6;
	private List<Book> books = new ArrayList<>();

	public BookBag() {
	}

	public BookBag(int max) {
		this.max = max;
	}

	@Override
	public Iterator<Book> iterator() {
		return books.iterator();
	}

	public List<Book> getBooks() {
		return books;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public boolean isMax() {
		return books.size() == max - 1;
	}

	public void add(Book i) {
		books.add(i);
	}

	public void drop(Book i) {
		if (books.contains(i)) {
			books.remove(i);
		}
	}

	public boolean isEmpty() {
		return books.isEmpty();
	}

	public int size() {
		return books.size();
	}

	public int sizeAt() {
		return max - size();
	}

	public void drop(String name) {
		drop(BookStorage.getInstance().get(name));
	}

	public boolean contains(Book i) {
		return books.contains(i);
	}

	public boolean canAdd() {
		return size() < max;
	}

	public boolean contains(String name) {
		return contains(BookStorage.getInstance().get(name));
	}

	@Override
	public String toString() {
		return "BookBag{" + "max=" + max + ", items=" + books + '}';
	}

	@Override
	public BookBag clone() {
		try {
			BookBag i = (BookBag) super.clone();
			return i;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
