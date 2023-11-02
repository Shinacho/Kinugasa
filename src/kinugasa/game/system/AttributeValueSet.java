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
 * @vesion 1.0.0 - 2023/10/14_11:23:52<br>
 * @author Shinacho<br>
 */
public class AttributeValueSet extends Storage<AttributeValue> implements Cloneable {

	public AttributeValueSet() {
	}

	public void init() {
		for (AttributeKey k : AttributeKey.values()) {
			add(new AttributeValue(k, 1.0f));
		}
	}

	public AttributeValue get(AttributeKey key) {
		return get(key.getName());
	}

	@Override
	public AttributeValueSet clone() {
		AttributeValueSet r = new AttributeValueSet();
		for (AttributeValue v : this) {
			r.add(v.clone());
		}
		return r;
	}

	@NewInstance
	public AttributeValueSet composite(AttributeValueSet v) {
		AttributeValueSet r = clone();

		//thisには全キーが入っている
		for (AttributeKey k : AttributeKey.values()) {
			AttributeValue sv = r.get(k);
			if (!v.contains(k.getName())) {
				continue;
			}
			sv.add(v.get(k).getValue());
		}
		return r;
	}
}
