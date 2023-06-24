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
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBRecord;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2022/11/12_20:19:52<br>
 * @author Shinacho<br>
 */
@DBRecord
public class FlagStorage extends DBStorage<Flag> implements Nameable {

	private static final FlagStorage INSTANCE = new FlagStorage();

	public static FlagStorage getInstance() {
		return INSTANCE;
	}

	public void update(Storage<Flag> fs) {
		for (Flag f : fs) {
			if (contains(f)) {
				get(f.getName()).set(f.get());
			} else {
				add(f);
			}
		}
	}

	@Override
	protected Flag select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from S_CurrentFlag where name = '" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			FlagStatus s = kr.row(0).get(1).of(FlagStatus.class);
			return new Flag(id, s);
		}
		return null;
	}

	@Override
	protected List<Flag> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from S_CurrentFlag;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			List<Flag> res = new ArrayList<>();
			for (List<DBValue> v : kr) {
				String id = kr.row(0).get(0).get();
				FlagStatus s = kr.row(0).get(1).of(FlagStatus.class);
				res.add(new Flag(id, s));
			}
			return res;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from S_CurrentFlag").row(0).get(0).asInt();
		}
		return 0;
	}

}
