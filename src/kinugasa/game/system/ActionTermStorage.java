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
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2022/12/02_11:08:05<br>
 * @author Shinacho<br>
 */
public class ActionTermStorage extends DBStorage<ActionTerm> {

	private static final ActionTermStorage INSTANCE = new ActionTermStorage();

	private ActionTermStorage() {
	}

	public static ActionTermStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected ActionTerm select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from ActionTerm where ActionTermID='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			TermType type = TermType.valueOf(kr.row(0).get(1).get());
			String value = kr.row(0).get(2).get();
			return new ActionTerm(id, type, value);
		}
		return null;
	}

	@Override
	protected List<ActionTerm> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from ActionTerm;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			List<ActionTerm> res = new ArrayList<>();
			for (List<DBValue> line : kr) {
				String id = line.get(0).get();
				TermType type = TermType.valueOf(line.get(1).get());
				String value = line.get(2).get();
				res.add(new ActionTerm(id, type, value));
			}
			return res;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from ActionTerm").row(0).get(0).asInt();
		}
		return 0;
	}

}
