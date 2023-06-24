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

import kinugasa.game.field4.MapChipAttribute;
import kinugasa.resource.sound.Sound;

/**
 * エンカウントした際の、敵セット、障害物情報、乗っているチップの情報を持つクラスです。
 *
 * @vesion 1.0.0 - 2022/11/23_11:44:50<br>
 * @author Shinacho<br>
 */
public class EncountInfo {

	private Sound currentBGM;
	private EnemySetStorage enemySetStorage;
	private MapChipAttribute chipAttribute;

	public EncountInfo(Sound currentBGM, EnemySetStorage enemySetStorage, MapChipAttribute chipAttribute) {
		this.currentBGM = currentBGM;
		this.enemySetStorage = enemySetStorage;
		this.chipAttribute = chipAttribute;
		assert enemySetStorage != null;
		assert chipAttribute != null;
	}

	public MapChipAttribute getChipAttribute() {
		return chipAttribute;
	}

	public EnemySetStorage getEnemySetStorage() {
		return enemySetStorage;
	}

	public Sound getPrevBGM() {
		return currentBGM;
	}

}
