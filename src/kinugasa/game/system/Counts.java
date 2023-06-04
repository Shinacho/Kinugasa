/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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
package kinugasa.game.system;

import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.db.DBRecord;
import kinugasa.resource.text.IniFile.Value;

/**
 *
 * @vesion 1.0.0 - 2023/05/31_19:57:01<br>
 * @author Shinacho<br>
 */
public class Counts extends Storage<Counts.Value> {

	private static final Counts INSTANCE = new Counts();

	public static Counts getInstance() {
		return INSTANCE;
	}

	private Counts() {
	}

	public static final String KEY_倒した敵の数 = "倒した敵の数";

	@DBRecord
	public static class Value implements Nameable {

		String name;
		int num;

		public Value(String name, int num) {
			this.name = name;
			this.num = num;
		}

		@Override
		public String getName() {
			return name;
		}

	}
}
