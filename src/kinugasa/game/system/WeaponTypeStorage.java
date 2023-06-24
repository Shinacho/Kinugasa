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

import kinugasa.resource.db.DBStorage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kinugasa.resource.*;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_16:55:15<br>
 * @author Shinacho<br>
 */
public class WeaponTypeStorage extends DBStorage<WeaponType> {

	private static final WeaponTypeStorage INSTANCE = new WeaponTypeStorage();

	private WeaponTypeStorage() {
	}

	public static WeaponTypeStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected WeaponType select(String name) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet rs = DBConnection.getInstance().execDirect("select * from weaponType where weaponTypeID = '" + name + "';");
			if (rs.isEmpty()) {
				return null;
			}
			List<DBValue> values = rs.row(0);
			return convert(values);
		}
		return null;
	}

	@Override
	protected List<WeaponType> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet rs = DBConnection.getInstance().execDirect("select * from weaponType;");
			if (rs.isEmpty()) {
				return Collections.emptyList();
			}
			List<WeaponType> res = new ArrayList<>();
			for (List<DBValue> rec : rs) {
				res.add(convert(rec));
			}
			return res;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from weaponType;").row(0).get(0).asInt();
		}
		return 0;
	}

	protected WeaponType convert(List<DBValue> record) {
		return new WeaponType(record.get(0).get(), record.get(1).get(), record.get(2).get());
	}

}
