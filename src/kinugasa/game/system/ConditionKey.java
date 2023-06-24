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

import java.util.Objects;
import kinugasa.resource.Nameable;
import kinugasa.resource.db.DBRecord;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_12:09:03<br>
 * @author Shinacho<br>
 */
@DBRecord
public class ConditionKey implements Nameable {

	private String name;
	private String desc;
	private int priority;

	public ConditionKey(String name, String desc, int priority) {
		this.name = name;
		this.desc = desc;
		this.priority = priority;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ConditionKey other = (ConditionKey) obj;
		return Objects.equals(this.name, other.name);
	}

	@Override
	public String toString() {
		return "ConditionKey{" + "name=" + name + '}';
	}

}
