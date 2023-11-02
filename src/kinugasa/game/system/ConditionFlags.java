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

import kinugasa.util.Random;
import kinugasa.game.NewInstance;

/**
 * 状態異常フラグは状態異常によって発生する混乱や行動停止といったフラグを管理するクラスです。
 *
 * @vesion 1.0.0 - 2023/10/16_19:25:24<br>
 * @author Shinacho<br>
 */
public class ConditionFlags {

	public static class ConditionPercent {

		public float 停止;
		public float 混乱;

		public ConditionPercent(float 停止, float 混乱) {
			this.停止 = 停止;
			this.混乱 = 混乱;
		}

		public boolean is停止() {
			return Random.percent(停止);
		}

		public boolean is混乱() {
			return Random.percent(混乱);
		}

	}
	private ConditionPercent p = new ConditionPercent(0f, 0f);
	private float 物理反射確率 = 0f;
	private float 物理吸収確率 = 0f;
	private float 魔法反射確率 = 0f;
	private float 魔法吸収確率 = 0f;
	private boolean 解脱中, 死亡中, 気絶中;

	{
		解脱中 = 死亡中 = 気絶中 = false;
	}
	private ConditionKey 停止理由 = null;
	private ConditionKey 混乱理由 = null;

	public ConditionFlags() {
	}

	public boolean is解脱中() {
		return 解脱中;
	}

	public void set解脱中(boolean 解脱中) {
		this.解脱中 = 解脱中;
	}

	public boolean is死亡中() {
		return 死亡中;
	}

	public void set死亡中(boolean 死亡中) {
		this.死亡中 = 死亡中;
	}

	public boolean is気絶中() {
		return 気絶中;
	}

	public void set気絶中(boolean 気絶中) {
		this.気絶中 = 気絶中;
	}

	public ConditionKey get停止理由() {
		return 停止理由;
	}

	public void set停止理由(ConditionKey 停止理由) {
		this.停止理由 = 停止理由;
	}

	public ConditionKey get混乱理由() {
		return 混乱理由;
	}

	public void set混乱理由(ConditionKey 混乱理由) {
		this.混乱理由 = 混乱理由;
	}

	public void setP(ConditionPercent p) {
		this.p = p;
	}

	public void set物理反射確率(float 物理反射確率) {
		this.物理反射確率 = 物理反射確率;
	}

	public void set物理吸収確率(float 物理吸収確率) {
		this.物理吸収確率 = 物理吸収確率;
	}

	public void set魔法反射確率(float 魔法反射確率) {
		this.魔法反射確率 = 魔法反射確率;
	}

	public void set魔法吸収確率(float 魔法吸収確率) {
		this.魔法吸収確率 = 魔法吸収確率;
	}

	public float get物理反射確率() {
		return 物理反射確率;
	}

	public float get物理吸収確率() {
		return 物理吸収確率;
	}

	public float get魔法反射確率() {
		return 魔法反射確率;
	}

	public float get魔法吸収確率() {
		return 魔法吸収確率;
	}

	public ConditionPercent getP() {
		return p;
	}

	@NewInstance
	public ConditionFlags add(ConditionFlags f) {
		ConditionFlags r = new ConditionFlags();
		r.p.停止 = this.p.停止 + f.p.停止;
		r.p.混乱 = this.p.混乱 + f.p.混乱;
		r.停止理由 = noNull(this.停止理由, f.停止理由);
		r.気絶中 = this.気絶中 || f.気絶中;
		r.混乱理由 = noNull(this.混乱理由, f.混乱理由);
		r.物理反射確率 = this.物理反射確率 + f.物理反射確率;
		r.物理吸収確率 = this.物理吸収確率 + f.物理吸収確率;
		r.解脱中 = this.解脱中 || f.解脱中;
		r.死亡中 = this.死亡中 || f.死亡中;
		r.魔法反射確率 = this.魔法反射確率 + f.魔法反射確率;
		r.魔法吸収確率 = this.魔法吸収確率 + f.魔法吸収確率;
		return r;
	}

	private static <T> T noNull(T t1, T t2) {
		return t1 == null ? t2 == null ? null : t2 : t1;
	}

	public void add物理反射確率(float v) {
		物理反射確率 += v;
	}

	public void add物理吸収確率(float v) {
		物理吸収確率 += v;
	}

	public void add魔法反射確率(float v) {
		魔法反射確率 += v;
	}

	public void add魔法吸収確率(float v) {
		魔法吸収確率 += v;
	}
}
