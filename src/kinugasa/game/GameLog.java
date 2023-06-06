/*
 * The MIT License
 *
 * Copyright 2015 Shinacho.
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
		out(string.toString());
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
