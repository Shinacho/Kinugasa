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
 * ���O�t�@�C���i���K�[�̃O���[�o�����O�j�ւ̏o�͂��s���܂�. ���O�t�@�C���̃t�H�[�}�b�g��SimpleFoomater���g�p����܂��B<br>
 * <br>
 * �����O�ւ̏o�͂́A�p�t�H�[�}���X�ɉe����^���܂��B<br>
 * <br>
 * �O���[�o�����O�̐ݒ肪GameConfig�ɂ���ĂȂ���Ă��Ȃ��ꍇ�́A���ׂẴ��O�͕W���o�͂ɏo�͂���܂��B<br>
 * ��GameConfig�̃O���[�o�����O�̐ݒ肪�Ȃ���Ă��Ȃ��ꍇ�́A�W���o�͂ł����Ă����O�̏o�͂��s���ׂ��ł͂���܂���B<br>
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
