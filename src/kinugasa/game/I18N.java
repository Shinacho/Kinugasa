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

import java.io.File;
import kinugasa.resource.text.IniFile;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:55:53<br>
 * @author Shinacho<br>
 */
public class I18N {

	private static IniFile ini;

	/**
	 * I18Nマップを初期化します。gameStart時に自動で実行されるため、通常は呼び出す必要はありません。
	 *
	 * @param lang ロケールの言語。
	 */
	public static void init(String lang) {
		ini = new IniFile("translate/" + lang + ".ini").load();

	}

	public I18N add(String lang, File dir) throws IllegalArgumentException {
		if (!dir.isDirectory() || !dir.exists()) {
			throw new IllegalArgumentException(dir + " is not directry");
		}
		String p = dir.getPath();
		if (!p.endsWith("/")) {
			p += "/";
		}
		p += lang;
		File f = new File(p + ".ini");
		ini.addAll(f.getPath());
		return this;
	}

	public static <T extends Enum<T>> String get(T t) {
		return get(t.toString());
	}

	public static boolean contains(String key) {
		return ini.containsKey(key);
	}

	public static String get(String key) {
		if(ini == null ){
			GameLog.print("WARNING : I18N is not loaded : " + key);
			return key;
		}
		if (!ini.containsKey(key)) {
			GameLog.print("WARNING : I18N key is not found : " + key);
			return key;
		}
		return ini.get(key).get().value();
	}

	public static String get(String key, String... param) {
		if (!ini.containsKey(key)) {
			GameLog.print("WARNING : I18N key is not found : " + key);
			return key;
		}

		String res = ini.get(key).get().value();
		for (int i = 0; i < param.length; i++) {
			res = res.replaceAll("!" + i, param[i]);
		}
		return res;
	}

}
