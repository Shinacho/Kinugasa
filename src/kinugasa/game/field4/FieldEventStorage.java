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
package kinugasa.game.field4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kinugasa.resource.Disposable;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_19:24:33<br>
 * @author Shinacho<br>
 */
public class FieldEventStorage extends Storage<FieldEvent> implements Nameable, Disposable {

	private String name;

	public FieldEventStorage(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void dispose() {
		clear();
	}

	public List<FieldEvent> get(int x, int y) {
		return get(new D2Idx(x, y));
	}

	public List<FieldEvent> get(D2Idx p) {
		List<FieldEvent> result = new ArrayList<>();
		for (FieldEvent e : this) {
			if (e.getLocation().equals(p)) {
				result.add(e);
			}
		}
		Collections.sort(result);
		return result;
	}

}
