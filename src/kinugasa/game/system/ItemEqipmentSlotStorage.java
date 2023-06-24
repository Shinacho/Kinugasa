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
 * @vesion 1.0.0 - 2022/11/16_13:22:40<br>
 * @author Shinacho<br>
 */
public class ItemEqipmentSlotStorage extends DBStorage<ItemEqipmentSlot> {

	private static ItemEqipmentSlotStorage INSTANCE = new ItemEqipmentSlotStorage();

	private ItemEqipmentSlotStorage() {
	}

	public static ItemEqipmentSlotStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected ItemEqipmentSlot select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from EQipSlot where slotID='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			return convert(kr.row(0));
		}
		return null;
	}

	@Override
	protected List<ItemEqipmentSlot> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from EQipSlot;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			List<ItemEqipmentSlot> list = new ArrayList<>();
			for (List<DBValue> line : kr) {
				list.add(convert(line));
			}
			return list;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from EqipSlot").row(0).get(0).asInt();
		}
		return 0;
	}

	protected ItemEqipmentSlot convert(List<DBValue> record) {
		return new ItemEqipmentSlot(record.get(0).get(), record.get(1).get());
	}
}
