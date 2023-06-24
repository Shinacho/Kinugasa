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

import java.util.Map;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_12:21:49<br>
 * @author Shinacho<br>
 */
public class AttributeValueSet extends Storage<AttributeValue> implements Cloneable {

	public AttributeValueSet() {
		for (AttributeKey k : AttributeKeyStorage.getInstance()) {
			add(new AttributeValue(k, 1, 1, 0, 1));
		}

	}

	public void setAll(float val) {
		for (AttributeValue v : this) {
			v.set(val);
		}
	}

	@Override
	public AttributeValueSet clone() {
		AttributeValueSet r = new AttributeValueSet();
		r.clear();
		for (AttributeValue v : this) {
			r.add(v.clone());
		}
		return r;
	}

	public void addAll(Map<AttributeKey, Float> v) {
		for (Map.Entry<AttributeKey, Float> e : v.entrySet()) {
			for (AttributeValue av : this) {
				if (av.getKey().equals(e.getKey())) {
					av.add(e.getValue());
				}
			}
		}
	}

}
