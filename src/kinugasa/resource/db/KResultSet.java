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
package kinugasa.resource.db;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kinugasa.game.GameLog;

/**
 *
 * @vesion 1.0.0 - May 26, 2023_12:26:27 PM<br>
 * @author Shinacho<br>
 */
public class KResultSet implements Iterable<List<DBValue>> {

	private List<List<DBValue>> data = new ArrayList<>();

	public KResultSet(ResultSet rs) throws KSQLException {
		if (rs == null) {
			return;
		}
		try {
			while (rs.next()) {
				List<DBValue> list = new ArrayList<>();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					list.add(new DBValue(rs.getMetaData().getColumnName(i),
							rs.getString(i)));
				}
				data.add(list);
			}
		} catch (SQLException ex) {
			GameLog.print(ex);
			throw new KSQLException(ex);
		} finally {
			try {
				rs.close();
			} catch (SQLException ex) {
				GameLog.print(ex);
				throw new KSQLException(ex);
			}
		}
	}

	public List<DBValue> row(int n) {
		return data.get(n);
	}

	public DBValue cell(int r, int c) {
		return row(r).get(c);
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public List<List<DBValue>> getData() {
		return data;
	}

	@Override
	public Iterator<List<DBValue>> iterator() {
		return data.iterator();
	}

	public Stream<List<DBValue>> stream() {
		return data.stream();
	}

	public List<DBValue> flatMap() {
		return data.stream().flatMap(p -> p.stream()).collect(Collectors.toList());
	}

	public <R> List<R> flatMap(Function<DBValue, R> f) {
		return flatMap().stream().map(f).collect(Collectors.toList());
	}

	public int size() {
		return data.size();
	}

	@Override
	public String toString() {
		return "KResultSet{" + "data=" + data + '}';
	}

}
