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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @vesion 1.0.0 - 2023/11/01_19:04:37<br>
 * @author Shinacho<br>
 */
public class LevelSystem {

	public static final float 必要経験値倍率 = 1.15139f;
	public static final float レベル１初期経験値 = 100f;
	private static Map<Integer, Float> 必要経験値テーブル = new HashMap<>();

	static {
		float needExp = レベル１初期経験値;
		必要経験値テーブル.put(1, 100f);
		for (int i = 2; i <= 99; i++) {
			needExp *= 必要経験値倍率;
			必要経験値テーブル.put(i, needExp);
		}
	}

	public static List<Actor> addExp(int expSum) {
		List<Actor> res = new ArrayList<>();
		for (Actor a : GameSystem.getInstance().getParty()) {
			float add = a.getStatus().mulExp(expSum);
			a.getStatus().getBaseStatus().get(StatusKey.保有経験値).add(add);
			float 現在経験値 = a.getStatus().getEffectedStatus().get(StatusKey.保有経験値).getValue();
			//現在経験値に基づくレベルを取得
			int maxLv = 経験値からレベル算出(現在経験値);
			int nowLv = (int) a.getStatus().getEffectedStatus().get(StatusKey.レベル).getValue();
			if (maxLv - nowLv < 0) {
				throw new GameSystemException("lv - exp is missmatch : " + a);
			}
			if (maxLv - nowLv > 0) {
				res.add(a);
			}
			a.getStatus().getBaseStatus().get(StatusKey.レベルアップ未使用スキルポイント).setValue(maxLv - nowLv);
			a.getStatus().getBaseStatus().get(StatusKey.次のレベルの経験値).setValue(必要経験値テーブル.get(maxLv));

		}
		return res;
	}

	public static int 経験値からレベル算出(float 保有経験値) {
		for (int i = 1; i <= 99; i++) {
			if (必要経験値テーブル.get(i) > 保有経験値) {
				return i - 1;
			}
		}
		return 99;
	}

	public static Map<Integer, Float> get必要経験値テーブル() {
		return 必要経験値テーブル;
	}

}
