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
public class Quest {

	public enum Type {
		メイン,
		サブ,
	}

	private final String qid;
	private final Type type;
	private final int stage;
	private final String visibleName;
	private final String desc;

	public Quest(String qid, Type type, int stage, String visibleName, String desc) {
		this.qid = qid;
		this.type = type;
		this.stage = stage;
		this.visibleName = visibleName;
		this.desc = desc;
	}

	public String getQid() {
		return qid;
	}

	public Type getType() {
		return type;
	}

	public String getVisibleName() {
		return visibleName;
	}

	public String getDesc() {
		return desc;
	}

	public int getStage() {
		return stage;
	}

	@Override
	public String toString() {
		return "Quest{" + "qid=" + qid + ", type=" + type + ", stage=" + stage + ", visibleName=" + visibleName + ", desc=" + desc + '}';
	}

}
