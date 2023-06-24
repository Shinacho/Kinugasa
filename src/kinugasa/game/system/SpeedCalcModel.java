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
import kinugasa.object.Model;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/22_4:19:23<br>
 * @author Shinacho<br>
 */
public abstract class SpeedCalcModel extends Model implements Nameable {

	private String name;

	public SpeedCalcModel(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public abstract List<BattleCharacter> sort(List<BattleCharacter> s);

	@Override
	public SpeedCalcModel clone() {
		return (SpeedCalcModel) super.clone();
	}

}
