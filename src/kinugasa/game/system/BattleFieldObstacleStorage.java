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
			result.add(get(Random.random(name)).clone());
		}
		return result;
	}

}
