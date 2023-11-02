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

import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2022/11/12_20:19:52<br>
 * @author Shinacho<br>
 */
public class FlagStorage extends Storage<Flag> {

	private static final FlagStorage INSTANCE = new FlagStorage();

	public static FlagStorage getInstance() {
		return INSTANCE;
	}

	public void update(Storage<Flag> fs) {
		for (Flag f : fs) {
			if (contains(f)) {
				get(f.getName()).set(f.get());
			} else {
				add(f);
			}
		}
	}

}
