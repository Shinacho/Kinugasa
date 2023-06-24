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

import java.awt.Point;
import kinugasa.object.Model;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_19:19:27<br>
 * @author Shinacho<br>
 */
public abstract class NPCMoveModel extends Model implements Nameable {

	private String name;

	public NPCMoveModel(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public abstract D2Idx getNextTargetIdx(NPC n, FieldMap map);

	public abstract int nextMoveFrameTime(NPC n, FieldMap map);

	public abstract D2Idx getMin(NPC n);

	public abstract D2Idx getMax(NPC n);

	@Override
	public NPCMoveModel clone() {
		return (NPCMoveModel) super.clone();
	}

}
