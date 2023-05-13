/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
	private String bgmMapName;
	private String bgmName;
	private String winLogicName, loseLogicName;
	private BGMMode bgmMode;
	private String winBgmMap, winBgmName;

	public EnemySet(String name, List<EnemyBlueprint> enemies, float p, 
			String bgmMapName, String bgmName, BGMMode mode,
			String winbgmMap, String winBgmName,
			String winLogicName, String loseLogicName) {
		this.name = name;
		this.enemies = enemies;
		this.p = p;
		this.bgmMapName = bgmMapName;
		this.bgmName = bgmName;
		this.winBgmMap = winbgmMap;
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

	public String getBgmMapName() {
		return bgmMapName;
	}

	public boolean hasBgm() {
		return bgmMapName != null && bgmName != null;
	}

	public Sound getBgm() {
		if (!hasBgm()) {
			throw new GameSystemException("enemySet " + name + " is not have BGM");
		}
		return SoundStorage.getInstance().get(bgmMapName).get(bgmName);
	}

	public Sound getWinBgm() {
		if (winBgmMap != null && winBgmName != null) {
			return SoundStorage.getInstance().get(winBgmMap).get(winBgmName);
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
