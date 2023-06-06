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
