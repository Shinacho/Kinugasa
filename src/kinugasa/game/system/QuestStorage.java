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
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 *
 * @vesion 1.0.0 - 2022/12/13_15:18:12<br>
 * @author Shinacho<br>
 */
public class QuestStorage extends DBStorage<Quest> {

	private static final QuestStorage INSTANCE = new QuestStorage();

	private QuestStorage() {
	}

	public static QuestStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Quest select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from Quest where QuestID='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			String qid = kr.row(0).get(0).get();
			int stage = kr.row(0).get(1).asInt();
			String title = kr.row(0).get(2).get();
			String desc = kr.row(0).get(3).get();
			return new Quest(qid, stage, title, desc);
		}
		return null;
	}

	@Override
	protected List<Quest> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from Quest;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			List<Quest> q = new ArrayList<>();
			for (List<DBValue> v : kr) {
				String qid = v.get(0).get();
				int stage = v.get(1).asInt();
				String title = v.get(2).get();
				String desc = v.get(3).get();
				add(new Quest(qid, stage, title, desc));
			}
			return q;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from Quest;").row(0).get(0).asInt();
		}
		return 0;
	}

}
