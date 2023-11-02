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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_16:56:15<br>
 * @author Shinacho<br>
 */
public class Quest implements Nameable {

	public enum Type {
		メイン,
		サブ,
	}

	private String id;
	private Type type;
	private int stage;
	private String visibleName;
	private String desc;

	public Quest(String id, Type type, int stage, String visibleName, String desc) {
		this.id = id;
		this.type = type;
		this.stage = stage;
		this.visibleName = visibleName;
		this.desc = desc;
	}

	public String getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public int getStage() {
		return stage;
	}

	public String getVisibleName() {
		return visibleName;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.id);
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
		final Quest other = (Quest) obj;
		return Objects.equals(this.id, other.id);
	}

	@Override
	public String toString() {
		return "Quest{" + "id=" + id + ", type=" + type + ", stage=" + stage + ", visibleName=" + visibleName + ", desc=" + desc + '}';
	}

}
