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
import java.util.Iterator;
import java.util.List;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_18:55:46<br>
 * @author Dra211<br>
 */
public class BookBag implements Cloneable, Iterable<Book> {

	private int max = 8;
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

	public void drop(Item i) {
		if (books.contains(i)) {
			books.remove(i);
		}
	}

	public void drop(String name) {
		drop(ItemStorage.getInstance().get(name));
	}

	public boolean contains(Book i) {
		return books.contains(i);
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
