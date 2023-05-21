/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
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

import java.io.File;
import java.lang.reflect.Field;
import java.util.logging.Level;
import kinugasa.game.system.GameSystemException;
import kinugasa.resource.text.IniFile;
import kinugasa.resource.text.FileFormatException;

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
		if (!ini.containsKey(key)) {
			throw new GameSystemException("undefined I18N key : " + key);
		}
		return ini.get(key).get().value();
	}

	public static String get(String key, String... value) {
		if (!ini.containsKey(key)) {
			throw new GameSystemException("undefined I18N key : " + key);
		}

		String res = ini.get(key).get().value();
		for (int i = 0; i < value.length; i++) {
			res = res.replaceAll("!" + i, value[i]);
		}
		return res;
	}

}
