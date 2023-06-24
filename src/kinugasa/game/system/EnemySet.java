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
import java.util.stream.Collectors;
import kinugasa.game.field4.BGMMode;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;

/**
 *
 * @vesion 1.0.0 - 2022/11/21_21:39:45<br>
 * @author Shinacho<br>
 */
public class EnemySet implements Nameable, Comparable<EnemySet> {

	private String name;
	private List<EnemyBlueprint> enemies;
	private float p;
	private String bgmName;
	private String winLogicName, loseLogicName;
	private BGMMode bgmMode;
	private String winBgmName;

	public EnemySet(String name, List<EnemyBlueprint> enemies, float p,
			String bgmName, BGMMode mode,
			String winBgmName,
			String winLogicName, String loseLogicName) {
		this.name = name;
		this.enemies = enemies;
		this.p = p;
		this.bgmName = bgmName;
		this.winBgmName = winBgmName;
		this.winLogicName = winLogicName;
		this.loseLogicName = loseLogicName;
		this.bgmMode = mode;
	}

	public BGMMode getPrevBgmMode() {
		return bgmMode;
	}

	public String getLoseLogicName() {
		return loseLogicName;
	}

	public String getWinLogicName() {
		return winLogicName;
	}

	public String getBgmName() {
		return bgmName;
	}

	public boolean hasBgm() {
		return bgmName != null;
	}

	public Sound getBgm() {
		if (!hasBgm()) {
			throw new GameSystemException("enemySet " + name + " is not have BGM");
		}
		return SoundStorage.getInstance().get(bgmName);
	}

	public Sound getWinBgm() {
		if (winBgmName != null) {
			return SoundStorage.getInstance().get(winBgmName);
		}
		throw new GameSystemException("enemySet " + name + " win bgm is null");
	}

	public List<EnemyBlueprint> getEnemies() {
		return enemies;
	}

	public List<Enemy> create() {
		return enemies.stream().map(v -> v.create()).collect(Collectors.toList());
	}

	@Override
	public String getName() {
		return name;
	}

	public float getP() {
		return p;
	}

	@Override
	public int compareTo(EnemySet o) {
		return (int) (p * 100 - o.p * 100);
	}

}
