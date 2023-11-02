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
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_19:29:37<br>
 * @author Shinacho<br>
 */
public class MaterialStorage extends DBStorage<Material> {

	private MaterialStorage() {
	}

	private static final MaterialStorage INSTANCE = new MaterialStorage();

	public static MaterialStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Material select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select id, visibleName, price from material where id = '" + id + "';";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			if (r.isEmpty()) {
				return null;
			}
			for (List<DBValue> v : r) {
				String mid = v.get(0).get();
				String visibleName = v.get(1).get();
				int price = v.get(2).asInt();
				Material m = new Material(mid, visibleName, price);
				return m;
			}
		}
		return null;
	}

	@Override
	protected List<Material> selectAll() throws KSQLException {
		List<Material> res = new ArrayList<>();
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select id, visibleName, price from material;";
			KResultSet r = DBConnection.getInstance().execDirect(sql);
			if (r.isEmpty()) {
				return res;
			}
			for (List<DBValue> v : r) {
				String mid = v.get(0).get();
				String visibleName = v.get(1).get();
				int price = v.get(2).asInt();
				Material m = new Material(mid, visibleName, price);
				res.add(m);
			}
		}
		return res;
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select count(*) from material;";
			return DBConnection.getInstance().execDirect(sql).cell(0, 0).asInt();
		}
		return 0;
	}

}
