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
