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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_18:56:31<br>
 * @author Shinacho<br>
 */
public class BookStorage extends DBStorage<Book> {

	private static final BookStorage INSTANCE = new BookStorage();

	private BookStorage() {
	}

	public static BookStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Book select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from Book where bookid='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			for (List<DBValue> v : kr) {
				String visibleName = v.get(1).get();
				String desc = v.get(2).get();
				int val = v.get(3).asInt();
				return new Book(id, visibleName, desc, val);
			}
		}
		return null;
	}

	@Override
	protected List<Book> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from Book;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			//ページの取得
			List<Book> book = new ArrayList<>();
			for (List<DBValue> v : kr) {
				String id = v.get(0).get();
				String visibleName = v.get(1).get();
				String desc = v.get(2).get();
				int val = v.get(3).asInt();
				book.add(new Book(id, visibleName, desc, val));
			}
			return book;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from book").row(0).get(0).asInt();
		}
		return 0;
	}

}
