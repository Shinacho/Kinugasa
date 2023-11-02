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
package kinugasa.resource.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.game.NewInstance;
import kinugasa.resource.text.TextFile;
import kinugasa.util.StopWatch;
import kinugasa.game.NotNull;

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
		return connection != null;
	}
	private String dataFileName, user, password;

	public void open(String dataFileName, String user, String password) throws KSQLException {
		this.dataFileName = dataFileName;
		this.user = user;
		this.password = password;
		try {
			StopWatch sw = new StopWatch().start();
			connection = DriverManager.getConnection(URL + dataFileName, user, password);
			sw.stop();
			GameLog.print("DBC open " + URL + dataFileName + "(" + sw.getTime() + "ms)-----------------------------------------");
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

	@NewInstance
	@NotNull
	public KResultSet execDirect(String sql) throws KSQLException {
		StopWatch sw = new StopWatch().start();
		try {
			if (connection.isClosed()) {
				StopWatch s = new StopWatch().start();
				connection = DriverManager.getConnection(URL + dataFileName, user, password);
				s.stop();
				GameLog.print("DBC REopen " + URL + dataFileName + "(" + sw.getTime() + "ms)-----------------------------------------");
			}
			statement = connection.createStatement();
			if (sql.trim().toLowerCase().startsWith("insert")) {
				statement.execute(sql);
				sw.stop();
				GameLog.print("DBC execDirect : " + sql + "(" + sw.getTime() + "ms)");
				return new KResultSet(null);
			}
			if (sql.trim().toLowerCase().startsWith("call")) {
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
		} finally {
			try {
				statement.close();
			} catch (SQLException ex) {
			}
		}
	}

	public void close() throws KSQLException {
		StopWatch sw = new StopWatch().start();
		try {
			if (statement != null) {
				statement.close();
				statement = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
			sw.stop();
			GameLog.print("DBC close (" + sw.getTime() + "ms)------------------------------------------------------");
		} catch (SQLException ex) {
			GameLog.print(ex);
			throw new KSQLException(ex);
		}
	}

}
