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
			GameLog.print(ex);
		}
		GameLog.print("lockFile:" + lockFile.getPath() + " is created");
	}

	/**
	 * ロックファイルを強制的に削除します. このメソッドは途中で強制終了する可能性のあるテスト時の実行に最適です。<br>
	 * 通常の終了手段（gameExitやウインドウ閉じ）で終了された場合には、ロックファイルは自動的に削除されます。<br>
	 */
	public static void deleteAllLockFile() {
		File[] list = new File("./").listFiles(TEMP_FN_FILTER);
		for (File file : list) {
			file.delete();
			GameLog.print("lockFile:" + file.getPath() + " is deleted");
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
					GameLog.print( ex);
				}
				lockFile.delete();
				GameLog.print("lockFile:" + lockFile.getPath() + " is deleted");
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
