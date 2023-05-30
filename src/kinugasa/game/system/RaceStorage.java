/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
