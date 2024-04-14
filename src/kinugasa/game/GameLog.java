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
package kinugasa.game;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import kinugasa.resource.Storage;

/**
 * ログファイル（ロガーのグローバルログ）への出力を行います. ログファイルのフォーマットはSimpleFoomaterが使用されます。<br>
 * <br>
 * ★ログへの出力は、パフォーマンスに影響を与えます。<br>
 * <br>
 * グローバルログの設定がGameConfigによってなされていない場合は、すべてのログは標準出力に出力されます。<br>
 * ※GameConfigのグローバルログの設定がなされていない場合は、標準出力であってもログの出力を行うべきではありません。<br>
 *
 * @version 1.0.0 - 2015/01/03<br>
 * @author Shinacho<br>
 * <br>
 */
public final class GameLog {

	private GameLog() {
	}
	private static boolean using = false;
	private static String logFilePath;
	private static BufferedWriter writer;

	protected static void usingLog(String path) {
		using = true;
		logFilePath = path;

		if (using) {
			try {
				writer = new BufferedWriter(new FileWriter(new File(path)));
			} catch (IOException ex) {
			}
		}
	}

	protected static void close() {
		if (writer == null) {
			return;
		}
		try {
			writer.close();
		} catch (IOException ex) {
		}
	}

	public static String getLogFilePath() {
		return logFilePath;
	}

	public static boolean isUsingLog() {
		return using;
	}

	public static void print(String string) {
		out(string);
	}

	public static void print(Object string) {
		if (string instanceof Collection<?>) {
			for (Object t : (Collection<?>) string) {
				print(t);
			}
		} else if (string instanceof Storage<?>) {
			for (Object t : (Storage<?>) string) {
				print(t);
			}
		} else {
			out(string.toString());
		}
	}

	private static void out(String s) {
		try {
			System.out.println(s);
			if (writer == null) {
				return;
			}
			writer.append(s);
			writer.newLine();
			writer.flush();
		} catch (IOException ex) {
		}
	}
}
