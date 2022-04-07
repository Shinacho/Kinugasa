/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ログファイル（ロガーのグローバルログ）への出力を行います. ログファイルのフォーマットはSimpleFoomaterが使用されます。<br>
 * <br>
 * ★ログへの出力は、パフォーマンスに影響を与えます。<br>
 * <br>
 * グローバルログの設定がGameConfigによってなされていない場合は、すべてのログは標準出力に出力されます。<br>
 * ※GameConfigのグローバルログの設定がなされていない場合は、標準出力であってもログの出力を行うべきではありません。<br>
 *
 * @version 1.0.0 - 2015/01/03<br>
 * @author Dra<br>
 * <br>
 */
public final class GameLog {

	private GameLog() {
	}
	private static boolean using = false;
	private static String logFilePath;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

	protected static void usingLog(String path) {
		using = true;
		logFilePath = path;
	}

	public static String getLogFilePath() {
		return logFilePath;
	}

	public static boolean isUsingLog() {
		return using;
	}

	public static void print(Level lv, String message) {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(lv, message);
		System.out.println(DATE_FORMAT.format(Calendar.getInstance().getTime()) + " " + message);
	}

	public static void print(Level lv, Throwable t) {
		Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(lv, t.toString());
		System.out.println(DATE_FORMAT.format(Calendar.getInstance().getTime()) + " " + t);
	}

	public static void printIfUsing(Level lv, String message) {
		if (using) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(lv, message);
			System.out.println(DATE_FORMAT.format(Calendar.getInstance().getTime()) + " " + message);
		}
	}

	public static void printIfUsing(Level lv, Throwable t) {
		if (using) {
			Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(lv, t.toString());
			System.out.println(DATE_FORMAT.format(Calendar.getInstance().getTime()) + " " + t);
		}
	}

	public static void printInfoIfUsing(String string) {
		printIfUsing(Level.INFO, string);
	}

	public static void printInfo(String string) {
		print(Level.INFO, string);
	}
}
