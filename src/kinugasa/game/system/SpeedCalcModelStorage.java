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
import java.util.List;
import kinugasa.resource.Storage;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/22_4:20:26<br>
 * @author Shinacho<br>
 */
public class SpeedCalcModelStorage extends Storage<SpeedCalcModel> {

	private static final SpeedCalcModelStorage INSTANCE = new SpeedCalcModelStorage();

	private SpeedCalcModelStorage() {
		add(new SpeedCalcModel("SPD_50%RANDOM") {
			@Override
			public List<BattleCharacter> sort(List<BattleCharacter> s) {
				class SpdMap implements Comparable<SpdMap> {

					public SpdMap(BattleCharacter s, float spd) {
						this.s = s;
						this.spd = spd;
					}

					BattleCharacter s;
					float spd;

					@Override
					public int compareTo(SpdMap o) {
						return (int) (spd - o.spd);
					}

				}
				List<SpdMap> list = new ArrayList<>();
				for (BattleCharacter st : s) {
					float val = st.getStatus().getEffectedStatus().get("SPD").getValue();
					val = Random.percent(val, 0.50f);
					list.add(new SpdMap(st, val));
				}
				Collections.sort(list);
				Collections.reverse(list);
				ArrayList<BattleCharacter> r = new ArrayList<>();

				for (SpdMap spdMap : list) {
					r.add(spdMap.s);
				}

				return r;
			}
		});
	}

	private SpeedCalcModel current;

	public void setCurrent(String name) {
		this.current = get(name);
	}

	public SpeedCalcModel getCurrent() {
		return current;
	}

	public static SpeedCalcModelStorage getInstance() {
		return INSTANCE;
	}

}
