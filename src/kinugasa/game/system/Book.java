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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
	private String visibleName;
	private String desc;
	private int value;

	public Book(String id, String name, String desc, int value) {
		this.id = id;
		this.visibleName = name;
		this.desc = desc;
		this.value = value;
	}

	public Book setValue(int value) {
		this.value = value;
		return this;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String getName() {
		return id;
	}

	public String getDesc() {
		return desc;
	}

	public String getVisibleName() {
		return visibleName;
	}

	@Override
	public Book clone() {
		try {
			Book b = (Book) super.clone();
			return b;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}

	@Override
	public String toString() {
		return "Book{" + "name=" + visibleName + ", desc=" + desc + '}';
	}

	public List<Action> getBookAction() {
		if (DBConnection.getInstance().isUsing()) {
			//BOOK_ACTIONテーブルからアクションIDを取得
			KResultSet kr = DBConnection.getInstance().execDirect("select ACTIONID from BOOK_ACTION where BookID='" + this.id + "';");
			List<Action> a = new ArrayList<>();
			for (List<DBValue> v : kr) {
				if (!ActionStorage.getInstance().contains(v.get(0).get())) {
					throw new GameSystemException("action id[" + v.get(0).get() + "] is not found. from book[" + this.id + "]");
				}
				a.add(ActionStorage.getInstance().get(v.get(0).get()));
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

	public List<Page> getPage() {
		List<Page> res = new ArrayList<>();
		for (Action a : getBookAction()) {
			res.addAll(a.getBattleEvent().stream().map(p -> new Page(p)).collect(Collectors.toSet()));
			res.addAll(a.getFieldEvent().stream().map(p -> new Page(p)).collect(Collectors.toSet()));
		}
		return res;
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
