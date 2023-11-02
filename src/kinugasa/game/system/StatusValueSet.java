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

import kinugasa.game.NewInstance;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_11:23:58<br>
 * @author Shinacho<br>
 */
public class StatusValueSet extends Storage<StatusValue> implements Cloneable {

	public StatusValueSet() {
	}

	public void init() {
		for (StatusKey k : StatusKey.values()) {
			switch (k) {
				case 体力:
				case 正気度:
				case 魔力:
					add(new StatusValue(k, 0, k.getMin(), k.getMax()));
					break;
				default:
					add(new StatusValue(k, 0));
					break;
			}
		}
	}

	public boolean contains(StatusKey s) {
		return contains(s.getName());
	}

	public StatusValue get(StatusKey key) {
		return get(key.getName());
	}

	@Override
	public StatusValueSet clone() {
		StatusValueSet r = new StatusValueSet();
		for (StatusValue v : this) {
			r.add(v.clone());
		}
		return r;
	}

	@NewInstance
	public StatusValueSet composite(StatusValueSet v) {
		StatusValueSet r = clone();

		//thisには全キーが入っている
		for (StatusKey k : StatusKey.values()) {
			StatusValue sv = r.get(k);
			if (!v.contains(k.getName())) {
				continue;
			}
			switch (k) {
				case 体力:
				case 正気度:
				case 魔力: {
					float 割合 = sv.get割合();
					sv.setMax(r.get(k).getMax() + v.get(k).getMax());
					sv.setBy割合(割合);
					break;
				}
				default: {
					sv.add(v.get(k).getValue());
					break;
				}
			}
		}
		return r;
	}

}
