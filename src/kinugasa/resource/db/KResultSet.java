/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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
package kinugasa.resource.db;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Iterator;
import kinugasa.game.GameLog;

/**
 *
 * @vesion 1.0.0 - May 26, 2023_12:26:27 PM<br>
 * @author Shinacho<br>
 */
public class KResultSet implements Iterable<List<DBValue>> {

	private List<List<DBValue>> data = new ArrayList<>();

	public KResultSet(ResultSet rs) throws KSQLException {
		try {
			while (rs.next()) {
				List<DBValue> list = new ArrayList<>();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					list.add(new DBValue(rs.getMetaData().getColumnName(i),
							rs.getString(i)));
				}
				data.add(list);
			}
		} catch (SQLException ex) {
			GameLog.print(ex);
			throw new KSQLException(ex);
		} finally {
			try {
				rs.close();
			} catch (SQLException ex) {
				GameLog.print(ex);
				throw new KSQLException(ex);
			}
		}
	}

	public List<List<DBValue>> getData() {
		return data;
	}

	@Override
	public Iterator<List<DBValue>> iterator() {
		return data.iterator();
	}
}
