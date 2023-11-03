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
public class QuestSystem {

	private static final QuestSystem INSTANCE = new QuestSystem();

	private QuestSystem() {
	}

	public static QuestSystem getInstance() {
		return INSTANCE;
	}

	public Quest get(String qid, int stage) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select"
					+ " qid,typ,stage,visibleName,Description"
					+ " from Quest where qid='" + qid + "' and stage=" + stage + ";");
			if (kr.isEmpty()) {
				return null;
			}
			List<Quest> q = new ArrayList<>();
			for (List<DBValue> v : kr) {
				String dqid = v.get(0).get();
				Quest.Type typ = v.get(1).of(Quest.Type.class);
				int dstage = v.get(2).asInt();
				String visibleName = v.get(3).get();
				String desc = v.get(4).get();
				return new Quest(dqid, typ, dstage, visibleName, desc);
			}
		}
		return null;
	}

}
