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

import java.util.List;
import kinugasa.game.system.Actor;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_17:09:47<br>
 * @author Shinacho<br>
 */
public class EventTerm implements Nameable {

	private String name;
	private EventTermType termType;
	private String storageName;
	private String targetName;
	private String value;

	public EventTerm(String name, EventTermType termType, String storageName, String targetName, String value) {
		this.name = name;
		this.termType = termType;
		this.storageName = storageName;
		this.targetName = targetName;
		this.value = value;
	}

	public boolean canDoThis(List<Actor> party, FieldEvent e) {
		return termType.canExec(party, this);
	}

	@Override
	public String getName() {
		return name;
	}

	public EventTermType getTermType() {
		return termType;
	}

	public String getStorageName() {
		return storageName;
	}

	public String getTargetName() {
		return targetName;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "EventTerm{" + "name=" + name + ", termType=" + termType + ", storageName=" + storageName + ", targetName=" + targetName + ", value=" + value + '}';
	}

}
