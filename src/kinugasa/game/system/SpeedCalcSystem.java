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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import kinugasa.game.NewInstance;
import kinugasa.util.Random;
import kinugasa.game.NotNull;

/**
 *
 * @vesion 1.0.0 - 2023/11/01_8:27:22<br>
 * @author Shinacho<br>
 */
public class SpeedCalcSystem {

	private static class Spd implements Comparable<Spd> {

		final Actor a;
		final float spdSpread;
		final MagicSpell ms;

		Spd(Actor a, float spdSpread, MagicSpell ms) {
			this.a = a;
			this.spdSpread = spdSpread;
			this.ms = ms;
		}

		BattleCommand get() {
			BattleCommand res = new BattleCommand(a.isPlayer()
					? BattleCommand.Mode.PC
					: BattleCommand.Mode.CPU, a);
			if (ms != null) {
				res.setMagicSpell(true);
				res.setAction(List.of(ms.getAction()));
				res.setUserOperation(false);
			} else {
				res.setMagicSpell(false);
				res.setAction(a.getStatus().getActions().asList());
				res.setUserOperation(a.isPlayer());
			}
			return res;
		}

		@Override
		public int compareTo(Spd o) {
			return (int) (o.spdSpread * 100 - spdSpread * 100);
		}

	}

	@NewInstance
	@NotNull
	public static LinkedList<BattleCommand> doExec(List<Actor> a, List<MagicSpell> ms) {
		List<Spd> result = new ArrayList<>();
		//aを素早さ順に並べる。BattleConfigの乱数を使う。
		for (Actor ac : a) {
			float spd = Random.spread(ac.getStatus().getEffectedStatus().get(StatusKey.素早さ).getValue(), BattleConfig.SPEED_SPREAD);
			result.add(new Spd(ac, spd, null));
		}
		//詠唱完了イベントの挿入とその術者の削除
		List<String> remove = new ArrayList<>();
		for (MagicSpell sp : ms) {
			remove.add(sp.getUser().getId());
		}
		for (String s : remove) {
			Spd removeCmd = null;
			for (Spd spd : result) {
				if (s.equals(spd.a.getId())) {
					removeCmd = spd;
				}
			}
			if (removeCmd != null) {
				result.remove(removeCmd);
			}
		}
		for (MagicSpell sp : ms) {
			float spd = Random.spread(sp.getUser().getStatus().getEffectedStatus().get(StatusKey.詠唱).getValue(), BattleConfig.SPEED_SPREAD);
			result.add(new Spd(sp.getUser(), spd, sp));
		}

		//死亡者をリストから除外
		result = result
				.stream()
				.filter(p -> !p.get().getUser().getStatus().hasCondition(ConditionKey.解脱))
				.filter(p -> !p.get().getUser().getStatus().hasCondition(ConditionKey.損壊))
				.filter(p -> !p.get().getUser().getStatus().hasCondition(ConditionKey.気絶))
				.toList();

		result = new ArrayList<>(result);
		Collections.sort(result);
		return new LinkedList<>(result.stream().map(p -> p.get()).toList());
	}

}
