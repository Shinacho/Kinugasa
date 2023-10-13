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
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2022/12/26_16:48:06<br>
 * @author Shinacho<br>
 */
public class MaterialStorage extends DBStorage<Material> {

	private static final MaterialStorage INSTANCE = new MaterialStorage();

	private MaterialStorage() {
	}

	public static MaterialStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Material select(String id) throws KSQLException {
		if (!DBConnection.getInstance().isUsing()) {
			return null;
		}
		KResultSet rs = DBConnection.getInstance().execDirect("select materialId, visibleName, val from material where materialId='" + id + "';");
		if (rs.isEmpty()) {
			return null;
		}
		for (List<DBValue> v : rs) {
			return new Material(v.get(0).get(), v.get(1).get(), v.get(2).asInt());
		}
		return null;
	}

	@Override
	protected List<Material> selectAll() throws KSQLException {
		if (!DBConnection.getInstance().isUsing()) {
			return Collections.emptyList();
		}
		KResultSet rs = DBConnection.getInstance().execDirect("select materialId, visibleName, val from material;");
		if (rs.isEmpty()) {
			return Collections.emptyList();
		}
		List<Material> m = new ArrayList<>();
		for (List<DBValue> v : rs) {
			m.add(new Material(v.get(0).get(), v.get(1).get(), v.get(2).asInt()));
		}
		return m;
	}

	@Override
	protected int count() throws KSQLException {
		if (!DBConnection.getInstance().isUsing()) {
			return 0;
		}
		return DBConnection.getInstance().execDirect("select count(*) from material;").cell(0, 0).asInt();
	}

}
