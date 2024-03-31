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
import kinugasa.resource.Storage;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2023/05/31_19:57:01<br>
 * @author Shinacho<br>
 */
public class Counts {

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

		@Override
		public String toString() {
			return "Value{" + "name=" + name + ", num=" + num + '}';
		}

	}
	private static final Counts INSTANCE = new Counts();

	public static Counts getInstance() {
		return INSTANCE;
	}

	private Counts() {
	}

	private Storage<Counts.Value> storage = new Storage<>();

	@Deprecated
	public Storage<Value> getStorage() {
		return storage;
	}

	public void add1count(String name) {
		Counts.Value v = null;
		if (storage.contains(name)) {
			v = storage.get(name);
			storage.remove(name);
		} else if ((v = select(name)) != null) {
		}
		if (v == null) {
			v = new Value(name, 1);
		} else {
			v = new Value(v.name, v.num + 1);
		}
		storage.add(v);
		commit();
	}
	
	public void updateOrInsert(String name, long val){
		Counts.Value v = null;
		if (storage.contains(name)) {
			v = storage.get(name);
			storage.remove(name);
		} else if ((v = select(name)) != null) {
		}
		if (v == null) {
			v = new Value(name, val);
		} else {
			v = new Value(v.name, v.num + val);
		}
		storage.add(v);
		commit();
	}

	public Value select(String id) throws KSQLException {
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

	public List<Value> selectAll() throws KSQLException {
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

	private void commit() {
		for (Value v : storage) {
			if (select(v.name) == null) {
				String sql = "insert into counts values('" + v.name + "'," + v.num + ");";
				DBConnection.getInstance().execDirect(sql);
			} else {
				String sql = "update counts set num = " + v.num + " where id = '" + v.name + "';";
				DBConnection.getInstance().execDirect(sql);
			}
		}
		storage.clear();
	}

}
