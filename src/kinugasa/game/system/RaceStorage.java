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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_13:59:16<br>
 * @author Shinacho<br>
 */
public class RaceStorage extends DBStorage<Race> {

	private static RaceStorage INSTANCE = new RaceStorage();

	private RaceStorage() {
	}

	public static RaceStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Race select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from race where raceId ='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			String raceid = kr.row(0).get(0).get();
			int itembagSize = kr.row(0).get(1).asInt();
			int bookbagSize = kr.row(0).get(2).asInt();
			//スロット
			kr = DBConnection.getInstance().execDirect("select * from Race_Slot where raceid='" + id + "';");
			Set<ItemEqipmentSlot> eqipSlot = new HashSet<>();
			for (List<DBValue> v : kr) {
				eqipSlot.add(ItemEqipmentSlotStorage.getInstance().get(v.get(1).get()));
			}
			return new Race(raceid, itembagSize, bookbagSize, eqipSlot);
		}
		return null;
	}

	@Override
	protected List<Race> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from race;");
			if (kr.isEmpty()) {
				return null;
			}
			List<Race> list = new ArrayList<>();
			for (List<DBValue> line : kr) {
				String raceid = line.get(0).get();
				int itembagSize = line.get(1).asInt();
				int bookbagSize = line.get(2).asInt();
				//スロット
				KResultSet kr2 = DBConnection.getInstance().execDirect("select * from Race_Slot where raceid='" + raceid + "';");
				Set<ItemEqipmentSlot> eqipSlot = new HashSet<>();
				for (List<DBValue> v : kr) {
					eqipSlot.add(ItemEqipmentSlotStorage.getInstance().get(v.get(1).get()));
				}
				list.add(new Race(raceid, itembagSize, bookbagSize, eqipSlot));
			}
			return list;
		}
		return null;
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from race;").row(0).get(0).asInt();
		}
		return 0;
	}

}
