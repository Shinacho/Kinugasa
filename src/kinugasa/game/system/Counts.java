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
