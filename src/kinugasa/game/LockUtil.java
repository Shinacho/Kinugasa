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
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import kinugasa.resource.ContentsIOException;

/**
 * 多重起動防止用のロックファイルに関するユーティリティ.
 * <br>
 * ロックファイルの生成はできません。 削除はできます。<br>
 * <br>
 * ロックファイルは、"./"に作成されます。 また、ロックファイルはゲーム実行中は削除できません。
 * <br>
 *
 * @version 1.0.0 - 2015/01/04<br>
 * @author Shinacho<br>
 * <br>
 */
public final class LockUtil {

	private LockUtil() {
	}

	public static final String LOCK_FILE_PREFIX = "KLOCK_";
	public static final String LOCK_FILE_SUFFIX = ".lck";

	public static final FilenameFilter TEMP_FN_FILTER = (File dir, String name) -> name.startsWith(LOCK_FILE_PREFIX) & name.endsWith(LOCK_FILE_SUFFIX);

	private static File lockFile;
	private static BufferedWriter writer;

	static void createLockFile() throws ContentsIOException {
		try {
			lockFile = File.createTempFile(LOCK_FILE_PREFIX, LOCK_FILE_SUFFIX, new File("./"));
			writer = new BufferedWriter(new FileWriter(lockFile));
			writer.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
			writer.flush();
		} catch (IOException ex) {
			GameLog.print(Level.WARNING, ex);
		}
		GameLog.printInfoIfUsing("lockFile:" + lockFile.getPath() + " is created");
	}

	/**
	 * ロックファイルを強制的に削除します. このメソッドは途中で強制終了する可能性のあるテスト時の実行に最適です。<br>
	 * 通常の終了手段（gameExitやウインドウ閉じ）で終了された場合には、ロックファイルは自動的に削除されます。<br>
	 */
	public static void deleteAllLockFile() {
		File[] list = new File("./").listFiles(TEMP_FN_FILTER);
		for (File file : list) {
			file.delete();
			GameLog.printInfoIfUsing("lockFile:" + file.getPath() + " is deleted");
		}
	}

	static void deleteLockFile() {
		if (lockFile != null) {
			if (lockFile.exists()) {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException ex) {
					GameLog.print(Level.WARNING, ex);
				}
				lockFile.delete();
				GameLog.printInfoIfUsing("lockFile:" + lockFile.getPath() + " is deleted");
			}
		}
	}

	static boolean isExistsLockFile() {
		return new File("./").list(TEMP_FN_FILTER).length != 0;
	}

	static String[] listLockFile() {
		return new File("./").list(TEMP_FN_FILTER);
	}

}
