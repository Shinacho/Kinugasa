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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_12:23:37<br>
 * @author Shinacho<br>
 */
public class StatusValueSet extends Storage<StatusValue> implements Cloneable {

	public StatusValueSet() {
		for (StatusKey k : StatusKeyStorage.getInstance()) {
			add(new StatusValue(k, 0, 0, 0, 0));
		}
	}

	public void setAll(float val) {
		for (StatusValue v : this) {
			v.set(val);
		}
	}

	@Override
	public StatusValueSet clone() {
		StatusValueSet r = new StatusValueSet();
		for (StatusValue v : this) {
			r.put(v.clone());
		}
		return r;
	}

	public boolean isZero(String name) {
		return get(name).getValue() <= 0;
	}

	public boolean hasZero() {
		for (StatusValue v : this) {
			if (v.getValue() <= 0) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMinus() {
		for (StatusValue v : this) {
			if (v.getValue() < 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isZero(boolean and, List<StatusKey> key) {
		boolean result = and;
		for (StatusValue v : this) {
			if (key.contains(v.getKey())) {
				if (and) {
					result &= v.isZero();
				} else {
					result |= v.isZero();
				}
			}
		}
		return result;
	}

	public List<StatusKey> getZeroStatus() {
		List<StatusKey> result = new ArrayList<>();
		for (StatusValue v : this) {
			if (v.getValue() <= 0) {
				result.add(v.getKey());
			}
		}
		return result;
	}

	public void addAll(Map<StatusKey, Float> v) {
		for (Map.Entry<StatusKey, Float> e : v.entrySet()) {
			for (StatusValue av : this) {
				if (av.getKey().equals(e.getKey())) {
					av.add(e.getValue());
				}
			}
		}
	}

	@Override
	public String toString() {
		return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
	}

}
