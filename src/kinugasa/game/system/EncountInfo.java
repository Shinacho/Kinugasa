/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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

import kinugasa.game.field4.MapChipAttribute;

/**
 * エンカウントした際の、敵セット、障害物情報、乗っているチップの情報を持つクラスです。
 *
 * @vesion 1.0.0 - 2022/11/23_11:44:50<br>
 * @author Dra211<br>
 */
public class EncountInfo {

	private EnemySetStorage enemySetStorage;
	private MapChipAttribute chipAttribute;

	public EncountInfo(EnemySetStorage enemySetStorage, MapChipAttribute chipAttribute) {
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

}
