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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.resource.text.TextFile;
import kinugasa.util.StopWatch;

/**
 *
 * @vesion 1.0.0 - May 25, 2023_11:15:31 PM<br>
 * @author Shinacho<br>
 */
public class DBConnection {

	private static final String URL = "jdbc:h2:";
	private static final DBConnection INSTANCE = new DBConnection();

	private DBConnection() {
	}

	private Connection connection;
	private Statement statement;

	public static DBConnection getInstance() {
		return INSTANCE;
	}

	public boolean isUsing() {
		return statement != null;
	}

	public void open(String dataFileName, String user, String password) throws KSQLException {
		try {
			connection = DriverManager.getConnection(URL + dataFileName, user, password);
			statement = connection.createStatement();
			GameLog.print("DBC open " + URL + dataFileName + "-----------------------------------------");
		} catch (SQLException ex) {
			GameLog.print(ex);
			throw new KSQLException(ex);
		}

	}

	public List<KResultSet> execByFile(String fileName) throws KSQLException {
		TextFile file = new TextFile(fileName);
		file.load();
		GameLog.print("DBC execFile : " + fileName + "---------------------------------------");
		List<String> data = file.getData();
		StringBuilder sb = new StringBuilder();
		for (String line : data) {
			if (line.trim().isEmpty()) {
				continue;
			}
			if (line.trim().startsWith("--")) {
				continue;
			}
			if (line.contains("--")) {
				line = line.substring(0, line.indexOf("--"));
			}
			sb.append(line.replaceAll("\r\n", "").replaceAll("\n", ""));
		}
		file.dispose();
		List<KResultSet> result = new ArrayList<>();
		for (String s : sb.toString().split(";")) {
			result.add(execDirect(s.trim() + ";"));
		}
		return result;
	}

	public KResultSet execDirect(String sql) throws KSQLException {
		StopWatch sw = new StopWatch().start();
		try {
			if (sql.trim().toLowerCase().startsWith("insert")) {
				statement.execute(sql);
				sw.stop();
				GameLog.print("DBC execDirect : " + sql + "(" + sw.getTime() + "ms)");
				return new KResultSet(null);
			}
			if (sql.trim().toLowerCase().startsWith("create")) {
				statement.execute(sql);
				sw.stop();
				GameLog.print("DBC execDirect : " + sql + "(" + sw.getTime() + "ms)");
				return new KResultSet(null);
			}
			if (sql.trim().toLowerCase().startsWith("drop")) {
				statement.execute(sql);
				sw.stop();
				GameLog.print("DBC execDirect : " + sql + "(" + sw.getTime() + "ms)");
				return new KResultSet(null);
			}
			if (sql.trim().toLowerCase().startsWith("truncate")) {
				statement.execute(sql);
				sw.stop();
				GameLog.print("DBC execDirect : " + sql + "(" + sw.getTime() + "ms)");
				return new KResultSet(null);
			}
			if (sql.trim().toLowerCase().startsWith("update")) {
				statement.execute(sql);
				sw.stop();
				GameLog.print("DBC execDirect : " + sql + "(" + sw.getTime() + "ms)");
				return new KResultSet(null);
			}
			sw.stop();
			GameLog.print("DBC execDirect : " + sql + "(" + sw.getTime() + "ms)");
			return new KResultSet(statement.executeQuery(sql));
		} catch (SQLException ex) {
			GameLog.print(ex);
			sw.stop();
			GameLog.print("DBC [ERROR] execDirect : " + sql + "(" + sw.getTime() + "ms)");
			throw new KSQLException(ex);
		}
	}

	public void close() throws KSQLException {
		try {
			if (statement != null) {
				statement.close();
				statement = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
			GameLog.print("DBC close ------------------------------------------------------");
		} catch (SQLException ex) {
			GameLog.print(ex);
			throw new KSQLException(ex);
		}
	}

}
