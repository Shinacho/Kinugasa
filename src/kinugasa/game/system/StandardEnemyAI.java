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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *
 * @vesion 1.0.0 - 2022/12/02_16:46:35<br>
 * @author Dra211<br>
 */
public enum StandardEnemyAI implements EnemyAI {
	MAX_POWER {
		@Override
		public CmdAction getNext(BattleCharacter user, List<CmdAction> list) {
			List<CmdAction> l = new ArrayList<>(list);
			Collections.sort(l, new Comparator<CmdAction>() {
				@Override
				public int compare(CmdAction o1, CmdAction o2) {
					Map<StatusKey, Integer> o1Map = o1.selfBattleDirectDamage();
					Map<StatusKey, Integer> o2Map = o2.selfBattleDirectDamage();
					StatusKey hpKey = StatusKeyStorage.getInstance().get(BattleConfig.StatusKey.hp);
					int o1Hp = o1Map.containsKey(hpKey) ? o1Map.get(hpKey) : 0;
					int o2Hp = o2Map.containsKey(hpKey) ? o2Map.get(hpKey) : 0;
					return o2Hp - o1Hp;
				}
			});
			return l.get(0);
		}
	}, RANDOM {
		@Override
		public CmdAction getNext(BattleCharacter user, List<CmdAction> list) {
			List<CmdAction> l = new ArrayList<>(list);
			Collections.shuffle(l);
			return l.get(0);
		}
	}, SORTED {
		@Override
		public CmdAction getNext(BattleCharacter user, List<CmdAction> list) {
			List<CmdAction> l = new ArrayList<>(list);
			Collections.sort(l);
			return l.get(0);
		}
	};

	static {
		EnemyAIStorage.getInstance().addAll(StandardEnemyAI.values());
	}

	@Override
	public String getName() {
		return toString();
	}

}
