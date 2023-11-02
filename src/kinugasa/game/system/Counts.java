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
import java.util.List;
import kinugasa.resource.Nameable;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2023/05/31_19:57:01<br>
 * @author Shinacho<br>
 */
public class Counts extends DBStorage<Counts.Value> {

	public static class Value implements Nameable {

		public final String name;
		public final long num;

		public Value(String name, long num) {
			this.name = name;
			this.num = num;
		}

		@Deprecated
		@Override
		public String getName() {
			return name;
		}

		public String getVisibleName() {
			return name;
		}
	}
	private static final Counts INSTANCE = new Counts();

	public static Counts getInstance() {
		return INSTANCE;
	}

	private Counts() {
	}

	public void add1count(String name) {
		long c = 0;
		if (contains(name)) {
			c = get(name).num;
			remove(name);
		}
		c++;
		Counts.Value v = v = new Value(name, c);
		add(v);
		commit();
	}

	@Override
	protected Value select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select id, num from counts where id = '" + id + "';";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			if (r.isEmpty()) {
				return null;
			}
			for (List<DBValue> v : r) {
				String i = v.get(0).get();
				long n = v.get(1).asLong();
				return new Value(i, n);
			}
		}
		return null;
	}

	@Override
	protected List<Value> selectAll() throws KSQLException {
		List<Value> res = new ArrayList<>();
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select id, num from counts;";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			if (r.isEmpty()) {
				return res;
			}
			for (List<DBValue> v : r) {
				String i = v.get(0).get();
				long n = v.get(1).asLong();
				res.add(new Value(i, n));
			}
		}
		return res;
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select count(*) from counts;";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			if (r.isEmpty()) {
				return 0;
			}
			return r.cell(0, 0).asInt();
		}
		return 0;
	}

	public void commit() {
		for (Value v : this) {
			String sql = "update from counts set num = " + v.num + " where id = '" + v.name + "';";
			DBConnection.getInstance().execDirect(sql);
		}
		clear();
	}

}
