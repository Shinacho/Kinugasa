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

import java.util.List;
import java.util.Map;

/**
 *
 * @vesion 1.0.0 - 2022/11/26_17:23:40<br>
 * @author Shinacho<br>
 */
public class BattleResultValues {

	private BattleResult battleResult;
	private int exp;
	private List<Item> dropItems;
	private String nextLogicName;

	public BattleResultValues(BattleResult battleResult, int exp, List<Item> dropItems, String nextLogicName) {
		this.battleResult = battleResult;
		this.exp = exp;
		this.dropItems = dropItems;
		this.nextLogicName = nextLogicName;
	}

	public String getNextLogicName() {
		return nextLogicName;
	}

	public List<Item> getDropItems() {
		return dropItems;
	}

	public int getExp() {
		return exp;
	}

	public BattleResult getBattleResult() {
		return battleResult;
	}

	@Override
	public String toString() {
		return "BattleResultValues{" + "battleResult=" + battleResult + ", exp=" + exp + ", dropItems=" + dropItems + ", nextLogicName=" + nextLogicName + '}';
	}

}
