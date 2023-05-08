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
		add(new SpeedCalcModel("SPD_20%") {
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
					val = Random.percent(val, 0.20f);
					list.add(new SpdMap(st, val));
				}
				Collections.sort(list);
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
