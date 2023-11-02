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
import java.util.stream.Collectors;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.db.DBValue;
import kinugasa.resource.db.KResultSet;
import kinugasa.resource.db.KSQLException;

/**
 * このクラスはクエストのDBデータを管理します。カレントクエストはメモリ上で動作するので、ＤＢから取ってきたデータを入れます。
 * クエストはＤＢにすべてのステージのマスタデータが入っていて、それを取ってきて使うのみで、ＤＢを書き換えることはしません。
 * 詳しくはEventTermTypeやFieldEventTypeを見てください。
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
			KResultSet kr = DBConnection.getInstance().execDirect("select"
					+ " id,typ,stage,visibleName,Description"
					+ " from Quest where id='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			String qid = kr.row(0).get(0).get();
			Quest.Type typ = kr.row(0).get(1).of(Quest.Type.class);
			int stage = kr.row(0).get(2).asInt();
			String title = kr.row(0).get(3).get();
			String desc = kr.row(0).get(4).get();
			return new Quest(qid, typ, stage, title, desc);
		}
		return null;
	}

	@Override
	protected List<Quest> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select"
					+ " id,typ,stage,visibleName,Description"
					+ " from Quest;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			List<Quest> q = new ArrayList<>();
			for (List<DBValue> v : kr) {
				String qid = v.get(0).get();
				Quest.Type typ = kr.row(0).get(1).of(Quest.Type.class);
				int stage = v.get(2).asInt();
				String title = v.get(3).get();
				String desc = v.get(4).get();
				q.add(new Quest(qid, typ, stage, title, desc));
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
