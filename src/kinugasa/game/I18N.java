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

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import kinugasa.resource.db.DBConnection;
import kinugasa.resource.text.IniFile;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:55:53<br>
 * @author Shinacho<br>
 */
public class I18N {

	private static IniFile ini;
	private static Set<String> notFoundKeySet = new HashSet<>();
	private static Set<String> nullValueKeySet = new HashSet<>();
	//
	private static Map<String, SoftReference<String>> cache = new HashMap<>();
	private static String tableName;

	/**
	 * I18Nマップを初期化します。gameStart時に自動で実行されるため、通常は呼び出す必要はありません。
	 *
	 * @param lang ロケールの言語。
	 */
	public static void init(String lang) {
		ini = new IniFile("translate/" + lang + ".ini").load();
		tableName = ini.get("tableName").get().value();
	}

	public static String getTableName() {
		return tableName;
	}

	public static Set<String> getNotFoundKeySet() {
		return notFoundKeySet;
	}

	public static <T extends Enum<T>> String get(T t) {
		return get(t.toString());
	}

	private static String getText(String key) {
		if (notFoundKeySet.contains(key)) {
			return key;
		}
		if (nullValueKeySet.contains(key)) {
			return "";
		}
		if (key == null || key.isEmpty()) {
			notFoundKeySet.add(key);
			nullValueKeySet.add(key);
			GameLog.print("!> WARNING : I18N key,value is empty : " + key);
			return "";
		}
		if (cache.containsKey(key)) {
			var v = cache.get(key);
			if (v != null && v.get() != null) {
				return v.get();
			}
		}
		if (DBConnection.getInstance().isUsing()) {
			String sql = "select text from " + tableName + " where id = '" + key + "';";
			var v = DBConnection.getInstance().execDirect(sql);
			if (v.isEmpty()) {
				notFoundKeySet.add(key);
				GameLog.print("!> WARNING : I18N is not found : " + key);
				return key;
			}
			var res = v.cell(0, 0).get();
			if (res == null) {
				nullValueKeySet.add(key);
				GameLog.print("!> WARNING : I18N value is empty : " + key);
				return "";
			}
			cache.put(key, new SoftReference<>(res));
			return res;
		}
		notFoundKeySet.add(key);
		GameLog.print("!> WARNING : I18N DB using=false : " + key);
		return key;
	}

	@NoLoopCall
	public static boolean contains(String key) {
		String sql = "select count(*) from " + tableName + " where id = '" + key + "';";
		return DBConnection.getInstance().execDirect(sql).cell(0, 0).asInt() != 0;
	}

	@NoLoopCall
	public static String get(String key) {
		return getText(key);
	}

	@NoLoopCall
	public static String getOrThat(String key) {
		if (contains(key)) {
			return get(key);
		}
		return key;
	}

	@NoLoopCall
	public static String get(String key, Object... param) {
		String res = getText(key);
		for (int i = 0; i < param.length; i++) {
			res = res.replaceAll("!" + i, param[i].toString());
		}
		return res;
	}

}
