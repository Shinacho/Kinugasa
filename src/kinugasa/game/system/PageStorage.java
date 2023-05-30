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
 * @vesion 1.0.0 - 2022/12/25_13:58:05<br>
 * @author Shinacho<br>
 */
public class PageStorage extends DBStorage<Page> {

	private PageStorage() {
	}
	private static final PageStorage INSTANCE = new PageStorage();

	public static PageStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected Page select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from Page where pageID='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			for (List<DBValue> v : kr) {
				MagicCompositeType compositeType = v.get(1).of(MagicCompositeType.class);
				String visibleName = v.get(2).get();
				String targetName = v.get(3).get();
				float value = v.get(4).asFloat();
				return new Page(id, compositeType, visibleName, targetName, value);
			}
		}
		return null;
	}

	@Override
	protected List<Page> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select * from Page;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			List<Page> page = new ArrayList<>();
			for (List<DBValue> v : kr) {
				String id = v.get(0).get();
				MagicCompositeType compositeType = v.get(1).of(MagicCompositeType.class);
				String visibleName = v.get(2).get();
				String targetName = v.get(3).get();
				float value = v.get(4).asFloat();
				page.add(new Page(id, compositeType, visibleName, targetName, value));
			}
			return page;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from Page").row(0).get(0).asInt();
		}
		return 0;
	}

}
