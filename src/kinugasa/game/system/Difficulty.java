/*
 * Copyright (C) 2024 Shinacho
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

import kinugasa.game.I18N;

/**
 *
 * @vesion 1.0.0 - 2024/03/25_19:00:17<br>
 * @author Shinacho<br>
 */
public enum Difficulty {
	VERY_EASY(1.5f, 0.5f, 0.5f, 0.5f, 0.5f, 1f, false, false),
	EASY(1.25f, 0.75f, 1f, 1f, 1f, 1f, false, false),
	NOMAL(1f, 1f, 1f, 1f, 1f, 1f, false, false),
	HARD(1.25f, 1.25f, 1f, 1f, 1f, 1f, false, false),
	VERY_HARD(1.5f, 1.5f, 1f, 1f, 1f, 1f, false, false),
	HELL(1.5f, 1.5f, 1f, 2f, 2f, 0.75f, false, false),
	CRUELTY(1.5f, 1.5f, 2f, 2f, 2f, 0.5f, true, true),;

	private float 与ダメ倍率, 被ダメ倍率, 正気度ダメージ倍率;
	private float 販売価格倍率, 宿代倍率;
	private float エンカウント歩数倍率;
	private boolean 特別なenemySet抽選, 敵AI2を使用;

	private Difficulty(float 与ダメ倍率, float 被ダメ倍率, float 正気度ダメージ倍率, float 販売価格倍率, float 宿代倍率, float エンカウント歩数倍率, boolean 特別なenemySet抽選, boolean 敵AI2を使用) {
		this.与ダメ倍率 = 与ダメ倍率;
		this.被ダメ倍率 = 被ダメ倍率;
		this.正気度ダメージ倍率 = 正気度ダメージ倍率;
		this.販売価格倍率 = 販売価格倍率;
		this.宿代倍率 = 宿代倍率;
		this.エンカウント歩数倍率 = エンカウント歩数倍率;
		this.特別なenemySet抽選 = 特別なenemySet抽選;
		this.敵AI2を使用 = 敵AI2を使用;
	}

	public String getNameI18Nd() {
		return I18N.get("DIFF_" + toString());
	}

	public String getDescI18Nd() {
		return I18N.get("DIFF_" + toString() + "_DESC");
	}

	public float get与ダメ倍率() {
		return 与ダメ倍率;
	}

	public float get被ダメ倍率() {
		return 被ダメ倍率;
	}

	public float get正気度ダメージ倍率() {
		return 正気度ダメージ倍率;
	}

	public float get販売価格倍率() {
		return 販売価格倍率;
	}

	public float get宿代倍率() {
		return 宿代倍率;
	}

	public float getエンカウント歩数倍率() {
		return エンカウント歩数倍率;
	}

	public boolean is特別なenemySet抽選() {
		return 特別なenemySet抽選;
	}

	public boolean is敵AI2を使用() {
		return 敵AI2を使用;
	}

}
