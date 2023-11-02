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

/**
 *
 * @vesion 1.0.0 - 2022/11/15_14:34:32<br>
 * @author Shinacho<br>
 */
public class GameSystemXMLLoader {

	public GameSystemXMLLoader() {
	}
	private List<String> battleField = new ArrayList<>();
	private List<String> ess = new ArrayList<>();
	private List<String> conditionAnimation = new ArrayList<>();

	public GameSystemXMLLoader addBattleField(String fileName) {
		battleField.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addEnemySetStorage(String fileName) {
		this.ess.add(fileName);
		return this;
	}

	public GameSystemXMLLoader addConditionAnimation(String fileName) {
		this.conditionAnimation.add(fileName);
		return this;
	}

	public List<String> getEnemySetList() {
		return ess;
	}

	public List<String> getBattleField() {
		return battleField;
	}

	public List<String> getConditionAnimation() {
		return conditionAnimation;
	}

	public void load() throws IllegalStateException {
		if (battleField.isEmpty()) {
			throw new IllegalStateException("battleField is empty");
		}
		battleField.forEach(f -> BattleFieldSystem.getInstance().readFromXML(f));

		conditionAnimation.forEach(p -> ConditionKey.loadAnimationFromXML(p));

		if (ess.isEmpty()) {
			throw new IllegalStateException("ess is empty");
		}
		ess.forEach(f -> EnemySetStorageStorage.getInstance().readFromXML(f));
		EnemySetStorageStorage.getInstance().pack();

	}

}
