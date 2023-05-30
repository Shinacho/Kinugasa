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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import kinugasa.resource.Nameable;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBRecord;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_18:56:25<br>
 * @author Shinacho<br>
 */
@DBRecord
public class Book implements Nameable, Cloneable {

	private String id;
	private String name;
	private String desc;
	private ArrayList<Page> pages = new ArrayList<>();
	private int value;

	public Book(String id, String name, String desc, int value) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.value = value;
	}

	public Book setPages(List<Page> pages) {
		this.pages = new ArrayList<>(pages);
		return this;
	}

	public Book setValue(int value) {
		this.value = value;
		return this;
	}

	public int getValue() {
		return value;
	}

	public List<Page> getPages() {
		return pages;
	}

	@Override
	public String getName() {
		return id;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public Book clone() {
		try {
			Book b = (Book) super.clone();
			b.pages = (ArrayList<Page>) this.pages.clone();
			return b;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public String toString() {
		return "Book{" + "name=" + name + ", desc=" + desc + '}';
	}

	public List<Action> getBookAction() {
		if (DBConnection.getInstance().isUsing()) {
			//BOOK_ACTIONテーブルからアクションIDを取得
			KResultSet kr = DBConnection.getInstance().execDirect("select * from BOOK_ACTION where BookID='" + this.name + "';");
			List<Action> a = new ArrayList<>();
			for (List<DBValue> v : kr) {
				a.add(ActionStorage.getInstance().get(v.get(1).get()));
			}
			return a;
		}
		return Collections.emptyList();
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(this.id);
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
		final Book other = (Book) obj;
		return Objects.equals(this.id, other.id);
	}

}
