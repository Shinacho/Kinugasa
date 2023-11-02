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

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kinugasa.game.NewInstance;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_15:04:00<br>
 * @author Shinacho<br>
 */
public class ConditionRegist extends EnumMap<ConditionKey, Float> {

	public ConditionRegist() {
		super(ConditionKey.class);
		init(0);
	}

	public void init() {
		init(0.5f);
	}

	public void init(float v) {
		for (ConditionKey k : Stream.of(ConditionKey.values()).filter(p -> p.isRegistOn()).collect(Collectors.toSet())) {
			put(k, v);
		}
	}

	@NewInstance
	public ConditionRegist mul(ConditionRegist c) {
		ConditionRegist r = new ConditionRegist();
		r.putAll(this);
		for (Map.Entry<ConditionKey, Float> e : c.entrySet()) {
			if (r.containsKey(e.getKey())) {
				float val = r.get(e.getKey()) * e.getValue();
				if (val < 0) {
					val = 0;
				}
				r.put(e.getKey(), val);
			}
		}

		return r;
	}

	@NewInstance
	public ConditionRegist add(ConditionRegist c) {
		ConditionRegist r = new ConditionRegist();
		r.putAll(this);
		for (Map.Entry<ConditionKey, Float> e : c.entrySet()) {
			if (r.containsKey(e.getKey())) {
				float val = r.get(e.getKey()) + e.getValue();
				if (val < 0) {
					val = 0;
				}
				r.put(e.getKey(), val);
			}
		}

		return r;
	}

	@Override
	public ConditionRegist clone() {
		ConditionRegist r = new ConditionRegist();
		for (Map.Entry<ConditionKey, Float> v : entrySet()) {
			r.put(v.getKey(), v.getValue());
		}
		return r;
	}

}
