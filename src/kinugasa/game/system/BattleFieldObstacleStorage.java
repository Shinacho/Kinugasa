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
import kinugasa.resource.Storage;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_12:01:11<br>
 * @author Shinacho<br>
 */
public class BattleFieldObstacleStorage extends Storage<BattleFieldObstacle> {

	private static final BattleFieldObstacleStorage INSTANCE = new BattleFieldObstacleStorage();

	private BattleFieldObstacleStorage() {
	}

	public static BattleFieldObstacleStorage getInstance() {
		return INSTANCE;
	}

	public List<BattleFieldObstacle> createN(int n, String... name) {
		List<BattleFieldObstacle> result = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			result.add(get(Random.randomChoice(name)).clone());
		}
		return result;
	}

}
