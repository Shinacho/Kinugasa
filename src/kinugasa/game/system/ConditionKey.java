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

import java.util.HashMap;
import java.util.Map;
import kinugasa.game.I18N;
import kinugasa.game.Nullable;
import kinugasa.graphics.Animation;
import kinugasa.graphics.SpriteSheet;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;

/**
 * 状態異常（バフデバフ）です。 優先度１が小さいものにより上書きされます。優先度１が同じ場合、優先度２の小さいもので上書きされます。
 *
 * @vesion 1.0.0 - 2023/10/14_11:28:13<br>
 * @author Shinacho<br>
 */
public enum ConditionKey implements ConditionEffect {
	解脱(true, "魂が肉体を離れている", "は解脱した", "は解脱している", "は精神を取り戻した") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.set解脱中(true);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.set解脱中(false);
			f.getP().停止 -= 1f;
		}

	},//自然には治らない。専用の治療で治る。
	損壊(true, "肉体が破壊されている", "は死亡した", "は死亡している", "は肉体を取り戻した") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.set死亡中(true);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.set死亡中(false);
			f.getP().停止 -= 1f;
		}
	},//自然には治らない。体力回復魔法や治療で治る。
	気絶(true, "気を失っていてしばらく行動できない", "は気絶した", "は気絶している", "は意識を取り戻した") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.set気絶中(true);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.set気絶中(false);
			f.getP().停止 -= 1f;
		}
	},
	木化(true, "体が木になっている", "は足が木になった", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusValue vs = r.get(StatusKey.行動力);
			vs.setValue(vs.getValue() * 0.1f);
			return r;
		}

	},//地・水攻撃を受けると回復する
	黄金化(true, "体が金になっている", "は体が金になった", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusValue vs = r.get(StatusKey.行動力);
			vs.setValue(vs.getValue() * 0.1f);
			vs = r.get(StatusKey.防御力);
			vs.mul(2f);
			return r;
		}
	},//物理防御が上がる
	封印(true, "行動が制限されている", "は封印された", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusValue vs = r.get(StatusKey.行動力);
			vs.setValue(vs.getValue() * 0.1f);
			vs = r.get(StatusKey.魔法防御力);
			vs.mul(2f);
			return r;
		}
	},//魔法防御が上がる
	消滅(true, "消滅している", "は消滅した", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.getP().停止 -= 1f;
		}
	},//行動できない
	灰燼(true, "灰になっている", "は灰になった", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.getP().停止 -= 1f;
		}
	},//行動できない
	発狂(true, "発狂している", "は発狂した", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.getP().停止 -= 1f;
		}
	},//行動できない
	スタン(true, "スタンしている", "はスタンした", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.getP().停止 -= 1f;
		}
	},//行動できない
	眠り(true, "眠っている", "は眠った", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.getP().停止 -= 1f;
		}
	},//行動できない（数ターン
	麻痺(true, "体が思い通りに動かない", "は麻痺した", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.getP().停止 += 0.5f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.getP().停止 -= 0.5f;
		}
	},//行動できない（1ターン
	混乱(true, "混乱している", "は混乱した", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set混乱理由(this);
			f.getP().混乱 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set混乱理由(null);
			f.getP().混乱 -= 1f;
		}
	},//ランダムに選択された行動になる（数ターン
	毒(true, "毒を受けている", "は毒を受けた", "", "") {
		@Override
		public void turnStart(Status s) {
			float value = -(s.getEffectedStatus().get(StatusKey.体力).getMax() * 0.07f);
			s.addDamage(StatusKey.体力, value);
		}

	},//継続ダメージ弱を得る
	炎上(true, "炎上している", "は炎上した", "", "") {
		@Override
		public void turnStart(Status s) {
			float value = -(s.getEffectedStatus().get(StatusKey.体力).getMax() * 0.14f);
			s.addDamage(StatusKey.体力, value);
		}

		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusValue vs = r.get(StatusKey.精神);
			vs.setValue(vs.getValue() * 0.5f);
			return r;
		}
	},//精神が半分になる。継続ダメージ中を得る
	凍結(true, "凍結している", "は凍結した", "", "") {
		@Override
		public void turnStart(Status s) {
			float value = -(s.getEffectedStatus().get(StatusKey.体力).getMax() * 0.14f);
			s.addDamage(StatusKey.体力, value);
		}

		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusValue vs = r.get(StatusKey.筋力);
			vs.setValue(vs.getValue() * 0.5f);
			return r;
		}
	},//筋力が半分になる。継続ダメージ中を得る
	出血(true, "出血している", "は出血した", "", "") {
		@Override
		public void turnStart(Status s) {
			float value = -(s.getEffectedStatus().get(StatusKey.体力).getMax() * 0.21f);
			s.addDamage(StatusKey.体力, value);
		}
	},//継続ダメージ強を得る
	詠唱中(false, "魔法を詠唱している", "はXの詠唱を開始した", "は魔法を詠唱している", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.getP().停止 -= 1f;
		}
	},
	防御中(false, "防御している", "は防御に専念した", "は防御している", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusValue vs = r.get(StatusKey.防御力);
			vs.setValue(vs.getValue() * 2f);
			vs = r.get(StatusKey.魔法防御力);
			vs.setValue(vs.getValue() * 2f);
			return r;
		}
	},
	回避中(false, "回避している", "は回避に専念した", "は回避している", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusValue vs = r.get(StatusKey.回避率);
			vs.setValue(vs.getValue() * 2f);
			vs = r.get(StatusKey.魔法回避率);
			vs.setValue(vs.getValue() * 2f);
			return r;
		}
	},
	逃走した(false, "逃走した", "は戦場から逃走した", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.set停止理由(this);
			f.getP().停止 += 1f;
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.set停止理由(null);
			f.getP().停止 -= 1f;
		}
	},
	物理反射_弱(false, "２５％の確率で物理攻撃を反射できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add物理反射確率(0.25f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add物理反射確率(-0.25f);
		}

	},//
	物理吸収_弱(false, "２５％の確率で物理攻撃を吸収できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add物理吸収確率(0.25f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add物理吸収確率(-0.25f);
		}

	},//
	魔法反射_弱(false, "２５％の確率で魔法攻撃を反射できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add魔法反射確率(0.25f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add魔法反射確率(-0.25f);
		}
	},//
	魔法吸収_弱(false, "２５％の確率で魔法攻撃を吸収できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add魔法吸収確率(0.25f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add魔法吸収確率(-0.25f);
		}
	},//
	物理反射_中(false, "５０％の確率で物理攻撃を反射できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add物理反射確率(0.5f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add物理反射確率(-0.5f);
		}

	},//
	物理吸収_中(false, "５０％の確率で物理攻撃を吸収できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add物理吸収確率(0.5f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add物理吸収確率(-0.5f);
		}

	},//
	魔法反射_中(false, "５０％の確率で魔法攻撃を反射できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add魔法反射確率(0.5f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add魔法反射確率(-0.5f);
		}
	},//
	魔法吸収_中(false, "５０％の確率で魔法攻撃を吸収できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add魔法吸収確率(0.5f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add魔法吸収確率(-0.5f);
		}
	},//
	物理反射_強(false, "７５％の確率で物理攻撃を反射できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add物理反射確率(0.75f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add物理反射確率(-0.75f);
		}
	},//
	物理吸収_強(false, "７５％の確率で物理攻撃を吸収できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add物理吸収確率(0.75f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add物理吸収確率(-0.75f);
		}
	},//
	魔法反射_強(false, "７５％の確率で魔法攻撃を反射できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add魔法反射確率(0.75f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add魔法反射確率(-0.75f);
		}
	},//
	魔法吸収_強(false, "７５％の確率で魔法攻撃を吸収できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add魔法吸収確率(0.75f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add魔法吸収確率(-0.75f);
		}
	},//
	物理反射_確定(false, "１００％の確率で物理攻撃を反射できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add物理反射確率(1f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add物理反射確率(-1f);
		}
	},//
	物理吸収_確定(false, "１００％の確率で物理攻撃を吸収できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add物理吸収確率(1f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add物理吸収確率(-1f);
		}
	},//
	魔法反射_確定(false, "１００％の確率で魔法攻撃を反射できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add魔法反射確率(1f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add魔法反射確率(-1f);
		}
	},//
	魔法吸収_確定(false, "１００％の確率で魔法攻撃を吸収できる", "", "", "") {
		@Override
		public void startEffect(ConditionFlags f) {
			f.add魔法吸収確率(1f);
		}

		@Override
		public void endEffect(ConditionFlags f) {
			f.add魔法吸収確率(-1f);
		}
	},//
	魔術使用可否_使用可能(false, "魔法が使用できるようになる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔術使用可否;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.setValue(StatusKey.魔術使用可否＿使用可能);
			return r;
		}
	},//
	魔術使用可否_使用不可(false, "魔法が使用できなくなる。", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔術使用可否;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.setValue(0);
			return r;
		}
	},//
	体力_上昇_弱(false, "体力が１２％上がる", "", "", "") {
		//ステータス上下のバフデバフは現在値の再計算をすること！
		// →getStatusからベースに設定
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.体力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.12f);
			sv.setBy割合(val);
			return r;
		}

	},//
	体力_上昇_中(false, "体力が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.体力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.24f);
			sv.setBy割合(val);
			return r;
		}

	},//
	体力_上昇_強(false, "体力が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.体力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.36f);
			sv.setBy割合(val);
			return r;
		}
	},//
	体力_低下_弱(false, "体力が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.体力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.12f);
			sv.setBy割合(val);
			return r;
		}
	},//
	体力_低下_中(false, "体力が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.体力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.24f);
			sv.setBy割合(val);
			return r;
		}
	},//
	体力_低下_強(false, "体力が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.体力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.36f);
			sv.setBy割合(val);
			return r;
		}
	},//
	魔力_上昇_弱(false, "魔力が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.12f);
			sv.setBy割合(val);
			return r;
		}

	},//
	魔力_上昇_中(false, "魔力が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.24f);
			sv.setBy割合(val);
			return r;
		}

	},//
	魔力_上昇_強(false, "魔力が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.36f);
			sv.setBy割合(val);
			return r;
		}
	},//
	魔力_低下_弱(false, "魔力が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.12f);
			sv.setBy割合(val);
			return r;
		}
	},//
	魔力_低下_中(false, "魔力が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.24f);
			sv.setBy割合(val);
			return r;
		}
	},//
	魔力_低下_強(false, "魔力が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.36f);
			sv.setBy割合(val);
			return r;
		}
	},//
	正気度_上昇_弱(false, "正気度が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.正気度;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.12f);
			sv.setBy割合(val);
			return r;
		}

	},//
	正気度_上昇_中(false, "正気度が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.正気度;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.24f);
			sv.setBy割合(val);
			return r;
		}

	},//
	正気度_上昇_強(false, "正気度が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.正気度;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * 0.36f);
			sv.setBy割合(val);
			return r;
		}
	},//
	正気度_低下_弱(false, "正気度が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.正気度;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.12f);
			sv.setBy割合(val);
			return r;
		}
	},//
	正気度_低下_中(false, "正気度が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.正気度;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.24f);
			sv.setBy割合(val);
			return r;
		}
	},//
	正気度_低下_強(false, "正気度が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.正気度;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			float val = v.get(sk).get割合();
			sv.setMax(v.get(sk).getMax() * -0.36f);
			sv.setBy割合(val);
			return r;
		}
	},//
	筋力_上昇_弱(false, "筋力が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.筋力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	筋力_上昇_中(false, "筋力が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.筋力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	筋力_上昇_強(false, "筋力が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.筋力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	筋力_低下_弱(false, "筋力が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.筋力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	筋力_低下_中(false, "筋力が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.筋力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	筋力_低下_強(false, "筋力が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.筋力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	器用さ_上昇_弱(false, "器用さが１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.器用さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	器用さ_上昇_中(false, "器用さが２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.器用さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	器用さ_上昇_強(false, "器用さが３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.器用さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	器用さ_低下_弱(false, "器用さが１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.器用さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	器用さ_低下_中(false, "器用さが２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.器用さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	器用さ_低下_強(false, "器用さが３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.器用さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	素早さ_上昇_弱(false, "素早さが１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.素早さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	素早さ_上昇_中(false, "素早さが２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.素早さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	素早さ_上昇_強(false, "素早さが３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.素早さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	素早さ_低下_弱(false, "素早さが１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.素早さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	素早さ_低下_中(false, "素早さが２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.素早さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	素早さ_低下_強(false, "素早さが３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.素早さ;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	精神_上昇_弱(false, "精神が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.精神;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	精神_上昇_中(false, "精神が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.精神;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	精神_上昇_強(false, "精神が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.精神;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	精神_低下_弱(false, "精神が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.精神;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	精神_低下_中(false, "精神が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.精神;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	精神_低下_強(false, "精神が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.精神;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	信仰_上昇_弱(false, "信仰が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.信仰;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	信仰_上昇_中(false, "信仰が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.信仰;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	信仰_上昇_強(false, "信仰が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.信仰;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	信仰_低下_弱(false, "信仰が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.信仰;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	信仰_低下_中(false, "信仰が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.信仰;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	信仰_低下_強(false, "信仰が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.信仰;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	詠唱_上昇_弱(false, "詠唱が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.詠唱;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	詠唱_上昇_中(false, "詠唱が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.詠唱;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	詠唱_上昇_強(false, "詠唱が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.詠唱;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	詠唱_低下_弱(false, "詠唱が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.詠唱;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	詠唱_低下_中(false, "詠唱が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.詠唱;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	詠唱_低下_強(false, "詠唱が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.詠唱;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	攻撃力_上昇_弱(false, "攻撃力が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	攻撃力_上昇_中(false, "攻撃力が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	攻撃力_上昇_強(false, "攻撃力が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	攻撃力_低下_弱(false, "攻撃力が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	攻撃力_低下_中(false, "攻撃力が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	攻撃力_低下_強(false, "攻撃力が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	魔法攻撃力_上昇_弱(false, "魔法攻撃力が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	魔法攻撃力_上昇_中(false, "魔法攻撃力が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	魔法攻撃力_上昇_強(false, "魔法攻撃力が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	魔法攻撃力_低下_弱(false, "魔法攻撃力が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	魔法攻撃力_低下_中(false, "魔法攻撃力が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	魔法攻撃力_低下_強(false, "魔法攻撃力が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法攻撃力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	防御力_上昇_弱(false, "防御力が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	防御力_上昇_中(false, "防御力が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	防御力_上昇_強(false, "防御力が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	防御力_低下_弱(false, "防御力が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	防御力_低下_中(false, "防御力が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	防御力_低下_強(false, "防御力が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	魔法防御力_上昇_弱(false, "魔法防御力が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	魔法防御力_上昇_中(false, "魔法防御力が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	魔法防御力_上昇_強(false, "魔法防御力が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	魔法防御力_低下_弱(false, "魔法防御力が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	魔法防御力_低下_中(false, "魔法防御力が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	魔法防御力_低下_強(false, "魔法防御力が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法防御力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	クリティカル率_上昇_弱(false, "クリティカル率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	クリティカル率_上昇_中(false, "クリティカル率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	クリティカル率_上昇_強(false, "クリティカル率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	クリティカル率_低下_弱(false, "クリティカル率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	クリティカル率_低下_中(false, "クリティカル率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	クリティカル率_低下_強(false, "クリティカル率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	クリティカルダメージ倍数_上昇_弱(false, "クリティカルダメージ倍数が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	クリティカルダメージ倍数_上昇_中(false, "クリティカルダメージ倍数が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	クリティカルダメージ倍数_上昇_強(false, "クリティカルダメージ倍数が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	クリティカルダメージ倍数_低下_弱(false, "クリティカルダメージ倍数が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	クリティカルダメージ倍数_低下_中(false, "クリティカルダメージ倍数が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	クリティカルダメージ倍数_低下_強(false, "クリティカルダメージ倍数が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	魔法クリティカル率_上昇_弱(false, "魔法クリティカル率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	魔法クリティカル率_上昇_中(false, "魔法クリティカル率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	魔法クリティカル率_上昇_強(false, "魔法クリティカル率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	魔法クリティカル率_低下_弱(false, "魔法クリティカル率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	魔法クリティカル率_低下_中(false, "魔法クリティカル率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	魔法クリティカル率_低下_強(false, "魔法クリティカル率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカル率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	魔法クリティカルダメージ倍数_上昇_弱(false, "魔法クリティカルダメージ倍数が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	魔法クリティカルダメージ倍数_上昇_中(false, "魔法クリティカルダメージ倍数が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	魔法クリティカルダメージ倍数_上昇_強(false, "魔法クリティカルダメージ倍数が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	魔法クリティカルダメージ倍数_低下_弱(false, "魔法クリティカルダメージ倍数が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	魔法クリティカルダメージ倍数_低下_中(false, "魔法クリティカルダメージ倍数が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	魔法クリティカルダメージ倍数_低下_強(false, "魔法クリティカルダメージ倍数が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	行動力_上昇_弱(false, "行動力が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.行動力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	行動力_上昇_中(false, "行動力が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.行動力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	行動力_上昇_強(false, "行動力が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.行動力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	行動力_低下_弱(false, "行動力が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.行動力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	行動力_低下_中(false, "行動力が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.行動力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	行動力_低下_強(false, "行動力が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.行動力;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	回避率_上昇_弱(false, "回避率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	回避率_上昇_中(false, "回避率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	回避率_上昇_強(false, "回避率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	回避率_低下_弱(false, "回避率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	回避率_低下_中(false, "回避率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	回避率_低下_強(false, "回避率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	ブロック率_上昇_弱(false, "ブロック率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	ブロック率_上昇_中(false, "ブロック率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	ブロック率_上昇_強(false, "ブロック率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	ブロック率_低下_弱(false, "ブロック率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	ブロック率_低下_中(false, "ブロック率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	ブロック率_低下_強(false, "ブロック率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	ブロックダメージ倍率_上昇_弱(false, "ブロックダメージ倍率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	ブロックダメージ倍率_上昇_中(false, "ブロックダメージ倍率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	ブロックダメージ倍率_上昇_強(false, "ブロックダメージ倍率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	ブロックダメージ倍率_低下_弱(false, "ブロックダメージ倍率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	ブロックダメージ倍率_低下_中(false, "ブロックダメージ倍率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	ブロックダメージ倍率_低下_強(false, "ブロックダメージ倍率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	魔法回避率_上昇_弱(false, "魔法回避率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	魔法回避率_上昇_中(false, "魔法回避率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	魔法回避率_上昇_強(false, "魔法回避率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	魔法回避率_低下_弱(false, "魔法回避率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	魔法回避率_低下_中(false, "魔法回避率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	魔法回避率_低下_強(false, "魔法回避率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法回避率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	魔法ブロック率_上昇_弱(false, "魔法ブロック率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	魔法ブロック率_上昇_中(false, "魔法ブロック率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	魔法ブロック率_上昇_強(false, "魔法ブロック率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	魔法ブロック率_低下_弱(false, "魔法ブロック率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	魔法ブロック率_低下_中(false, "魔法ブロック率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	魔法ブロック率_低下_強(false, "魔法ブロック率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロック率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	魔法ブロックダメージ倍率_上昇_弱(false, "魔法ブロックダメージ倍率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	魔法ブロックダメージ倍率_上昇_中(false, "魔法ブロックダメージ倍率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	魔法ブロックダメージ倍率_上昇_強(false, "魔法ブロックダメージ倍率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	魔法ブロックダメージ倍率_低下_弱(false, "魔法ブロックダメージ倍率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	魔法ブロックダメージ倍率_低下_中(false, "魔法ブロックダメージ倍率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	魔法ブロックダメージ倍率_低下_強(false, "魔法ブロックダメージ倍率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	リジェネ_弱(false, "ターン開始時に体力を最大値の７％回復する", "", "", "") {
	},//
	リジェネ_中(false, "ターン開始時に体力を最大値の１４％回復する", "", "", "") {
	},//
	リジェネ_強(false, "ターン開始時に体力を最大値の２１％回復する", "", "", "") {
	},//
	スリップ_弱(false, "ターン開始時に体力の最大値の７％ダメージを受ける", "", "", "") {
	},//
	スリップ_中(false, "ターン開始時に体力の最大値の１４％ダメージを受ける", "", "", "") {
	},//
	スリップ_強(false, "ターン開始時に体力の最大値の２１％ダメージを受ける", "", "", "") {
	},//
	命中率_上昇_弱(false, "命中率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	命中率_上昇_中(false, "命中率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	命中率_上昇_強(false, "命中率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	命中率_低下_弱(false, "命中率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	命中率_低下_中(false, "命中率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	命中率_低下_強(false, "命中率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	魔法命中率_上昇_弱(false, "魔法命中率が１２％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.12f);
			return r;
		}
	},//
	魔法命中率_上昇_中(false, "魔法命中率が２４％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.24f);
			return r;
		}
	},//
	魔法命中率_上昇_強(false, "魔法命中率が３６％上がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(1.36f);
			return r;
		}
	},//
	魔法命中率_低下_弱(false, "魔法命中率が１２％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.88f);
			return r;
		}
	},//
	魔法命中率_低下_中(false, "魔法命中率が２４％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.76f);
			return r;
		}
	},//
	魔法命中率_低下_強(false, "魔法命中率が３６％下がる", "", "", "") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = new StatusValueSet();
			StatusKey sk = StatusKey.魔法命中率;
			StatusValue sv = r.getOrCreate(sk, () -> new StatusValue(sk, 0, 0, 0));
			sv.mul(0.64f);
			return r;
		}
	},//
	与属性_斬撃_上昇_弱(false, "与属性_斬撃が７％上がる", "", "", "") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.斬撃;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},//
	与属性_斬撃_上昇_中(false, "与属性_斬撃が１４％上がる", "", "", "") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.斬撃;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.14f);
			return r;
		}
	},//
	与属性_斬撃_上昇_強(false, "与属性_斬撃が２１％上がる", "", "", "") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.斬撃;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.21f);
			return r;
		}
	},//
	与属性_斬撃_低下_弱(false, "与属性_斬撃が７％下がる", "", "", "") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.斬撃;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}
	},//
	与属性_斬撃_低下_中(false, "与属性_斬撃が１４％下がる", "", "", "") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.斬撃;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.14f);
			return r;
		}
	},//
	与属性_斬撃_低下_強(false, "与属性_斬撃が２１％下がる", "", "", "") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.斬撃;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.21f);
			return r;
		}
	},//
	与属性_刺突_上昇_弱(false, "与属性_刺突が７％上がる", "", "", "") {
	},//
	与属性_刺突_上昇_中(false, "与属性_刺突が１４％上がる", "", "", "") {
	},//
	与属性_刺突_上昇_強(false, "与属性_刺突が２１％上がる", "", "", "") {
	},//
	与属性_刺突_低下_弱(false, "与属性_刺突が７％下がる", "", "", "") {
	},//
	与属性_刺突_低下_中(false, "与属性_刺突が１４％下がる", "", "", "") {
	},//
	与属性_刺突_低下_強(false, "与属性_刺突が２１％下がる", "", "", "") {
	},//
	与属性_衝撃_上昇_弱(false, "与属性_衝撃が７％上がる", "", "", "") {
	},//
	与属性_衝撃_上昇_中(false, "与属性_衝撃が１４％上がる", "", "", "") {
	},//
	与属性_衝撃_上昇_強(false, "与属性_衝撃が２１％上がる", "", "", "") {
	},//
	与属性_衝撃_低下_弱(false, "与属性_衝撃が７％下がる", "", "", "") {
	},//
	与属性_衝撃_低下_中(false, "与属性_衝撃が１４％下がる", "", "", "") {
	},//
	与属性_衝撃_低下_強(false, "与属性_衝撃が２１％下がる", "", "", "") {
	},//
	与属性_炎_上昇_弱(false, "与属性_炎が７％上がる", "", "", "") {
	},//
	与属性_炎_上昇_中(false, "与属性_炎が１４％上がる", "", "", "") {
	},//
	与属性_炎_上昇_強(false, "与属性_炎が２１％上がる", "", "", "") {
	},//
	与属性_炎_低下_弱(false, "与属性_炎が７％下がる", "", "", "") {
	},//
	与属性_炎_低下_中(false, "与属性_炎が１４％下がる", "", "", "") {
	},//
	与属性_炎_低下_強(false, "与属性_炎が２１％下がる", "", "", "") {
	},//
	与属性_氷_上昇_弱(false, "与属性_氷が７％上がる", "", "", "") {
	},//
	与属性_氷_上昇_中(false, "与属性_氷が１４％上がる", "", "", "") {
	},//
	与属性_氷_上昇_強(false, "与属性_氷が２１％上がる", "", "", "") {
	},//
	与属性_氷_低下_弱(false, "与属性_氷が７％下がる", "", "", "") {
	},//
	与属性_氷_低下_中(false, "与属性_氷が１４％下がる", "", "", "") {
	},//
	与属性_氷_低下_強(false, "与属性_氷が２１％下がる", "", "", "") {
	},//
	与属性_水_上昇_弱(false, "与属性_水が７％上がる", "", "", "") {
	},//
	与属性_水_上昇_中(false, "与属性_水が１４％上がる", "", "", "") {
	},//
	与属性_水_上昇_強(false, "与属性_水が２１％上がる", "", "", "") {
	},//
	与属性_水_低下_弱(false, "与属性_水が７％下がる", "", "", "") {
	},//
	与属性_水_低下_中(false, "与属性_水が１４％下がる", "", "", "") {
	},//
	与属性_水_低下_強(false, "与属性_水が２１％下がる", "", "", "") {
	},//
	与属性_雷_上昇_弱(false, "与属性_雷が７％上がる", "", "", "") {
	},//
	与属性_雷_上昇_中(false, "与属性_雷が１４％上がる", "", "", "") {
	},//
	与属性_雷_上昇_強(false, "与属性_雷が２１％上がる", "", "", "") {
	},//
	与属性_雷_低下_弱(false, "与属性_雷が７％下がる", "", "", "") {
	},//
	与属性_雷_低下_中(false, "与属性_雷が１４％下がる", "", "", "") {
	},//
	与属性_雷_低下_強(false, "与属性_雷が２１％下がる", "", "", "") {
	},//
	与属性_風_上昇_弱(false, "与属性_風が７％上がる", "", "", "") {
	},//
	与属性_風_上昇_中(false, "与属性_風が１４％上がる", "", "", "") {
	},//
	与属性_風_上昇_強(false, "与属性_風が２１％上がる", "", "", "") {
	},//
	与属性_風_低下_弱(false, "与属性_風が７％下がる", "", "", "") {
	},//
	与属性_風_低下_中(false, "与属性_風が１４％下がる", "", "", "") {
	},//
	与属性_風_低下_強(false, "与属性_風が２１％下がる", "", "", "") {
	},//
	与属性_土_上昇_弱(false, "与属性_土が７％上がる", "", "", "") {
	},//
	与属性_土_上昇_中(false, "与属性_土が１４％上がる", "", "", "") {
	},//
	与属性_土_上昇_強(false, "与属性_土が２１％上がる", "", "", "") {
	},//
	与属性_土_低下_弱(false, "与属性_土が７％下がる", "", "", "") {
	},//
	与属性_土_低下_中(false, "与属性_土が１４％下がる", "", "", "") {
	},//
	与属性_土_低下_強(false, "与属性_土が２１％下がる", "", "", "") {
	},//
	与属性_光_上昇_弱(false, "与属性_光が７％上がる", "", "", "") {
	},//
	与属性_光_上昇_中(false, "与属性_光が１４％上がる", "", "", "") {
	},//
	与属性_光_上昇_強(false, "与属性_光が２１％上がる", "", "", "") {
	},//
	与属性_光_低下_弱(false, "与属性_光が７％下がる", "", "", "") {
	},//
	与属性_光_低下_中(false, "与属性_光が１４％下がる", "", "", "") {
	},//
	与属性_光_低下_強(false, "与属性_光が２１％下がる", "", "", "") {
	},//
	与属性_闇_上昇_弱(false, "与属性_闇が７％上がる", "", "", "") {
	},//
	与属性_闇_上昇_中(false, "与属性_闇が１４％上がる", "", "", "") {
	},//
	与属性_闇_上昇_強(false, "与属性_闇が２１％上がる", "", "", "") {
	},//
	与属性_闇_低下_弱(false, "与属性_闇が７％下がる", "", "", "") {
	},//
	与属性_闇_低下_中(false, "与属性_闇が１４％下がる", "", "", "") {
	},//
	与属性_闇_低下_強(false, "与属性_闇が２１％下がる", "", "", "") {
	},//
	与属性_神秘_上昇_弱(false, "与属性_神秘が７％上がる", "", "", "") {
	},//
	与属性_神秘_上昇_中(false, "与属性_神秘が１４％上がる", "", "", "") {
	},//
	与属性_神秘_上昇_強(false, "与属性_神秘が２１％上がる", "", "", "") {
	},//
	与属性_神秘_低下_弱(false, "与属性_神秘が７％下がる", "", "", "") {
	},//
	与属性_神秘_低下_中(false, "与属性_神秘が１４％下がる", "", "", "") {
	},//
	与属性_神秘_低下_強(false, "与属性_神秘が２１％下がる", "", "", "") {
	},//
	与属性_精神_上昇_弱(false, "与属性_精神が７％上がる", "", "", "") {
	},//
	与属性_精神_上昇_中(false, "与属性_精神が１４％上がる", "", "", "") {
	},//
	与属性_精神_上昇_強(false, "与属性_精神が２１％上がる", "", "", "") {
	},//
	与属性_精神_低下_弱(false, "与属性_精神が７％下がる", "", "", "") {
	},//
	与属性_精神_低下_中(false, "与属性_精神が１４％下がる", "", "", "") {
	},//
	与属性_精神_低下_強(false, "与属性_精神が２１％下がる", "", "", "") {
	},//
	与属性_錬金_上昇_弱(false, "与属性_錬金が７％上がる", "", "", "") {
	},//
	与属性_錬金_上昇_中(false, "与属性_錬金が１４％上がる", "", "", "") {
	},//
	与属性_錬金_上昇_強(false, "与属性_錬金が２１％上がる", "", "", "") {
	},//
	与属性_錬金_低下_弱(false, "与属性_錬金が７％下がる", "", "", "") {
	},//
	与属性_錬金_低下_中(false, "与属性_錬金が１４％下がる", "", "", "") {
	},//
	与属性_錬金_低下_強(false, "与属性_錬金が２１％下がる", "", "", "") {
	},//
	与属性_時空_上昇_弱(false, "与属性_時空が７％上がる", "", "", "") {
	},//
	与属性_時空_上昇_中(false, "与属性_時空が１４％上がる", "", "", "") {
	},//
	与属性_時空_上昇_強(false, "与属性_時空が２１％上がる", "", "", "") {
	},//
	与属性_時空_低下_弱(false, "与属性_時空が７％下がる", "", "", "") {
	},//
	与属性_時空_低下_中(false, "与属性_時空が１４％下がる", "", "", "") {
	},//
	与属性_時空_低下_強(false, "与属性_時空が２１％下がる", "", "", "") {
	},//
	被属性_斬撃_上昇_弱(false, "被属性_斬撃が７％上がる", "", "", "") {
	},//
	被属性_斬撃_上昇_中(false, "被属性_斬撃が１４％上がる", "", "", "") {
	},//
	被属性_斬撃_上昇_強(false, "被属性_斬撃が２１％上がる", "", "", "") {
	},//
	被属性_斬撃_低下_弱(false, "被属性_斬撃が７％下がる", "", "", "") {
	},//ｘ
	被属性_斬撃_低下_中(false, "被属性_斬撃が１４％下がる", "", "", "") {
	},//
	被属性_斬撃_低下_強(false, "被属性_斬撃が２１％下がる", "", "", "") {
	},//
	被属性_刺突_上昇_弱(false, "被属性_刺突が７％上がる", "", "", "") {
	},//
	被属性_刺突_上昇_中(false, "被属性_刺突が１４％上がる", "", "", "") {
	},//
	被属性_刺突_上昇_強(false, "被属性_刺突が２１％上がる", "", "", "") {
	},//
	被属性_刺突_低下_弱(false, "被属性_刺突が７％下がる", "", "", "") {
	},//
	被属性_刺突_低下_中(false, "被属性_刺突が１４％下がる", "", "", "") {
	},//
	被属性_刺突_低下_強(false, "被属性_刺突が２１％下がる", "", "", "") {
	},//
	被属性_衝撃_上昇_弱(false, "被属性_衝撃が７％上がる", "", "", "") {
	},//
	被属性_衝撃_上昇_中(false, "被属性_衝撃が１４％上がる", "", "", "") {
	},//
	被属性_衝撃_上昇_強(false, "被属性_衝撃が２１％上がる", "", "", "") {
	},//
	被属性_衝撃_低下_弱(false, "被属性_衝撃が７％下がる", "", "", "") {
	},//
	被属性_衝撃_低下_中(false, "被属性_衝撃が１４％下がる", "", "", "") {
	},//
	被属性_衝撃_低下_強(false, "被属性_衝撃が２１％下がる", "", "", "") {
	},//
	被属性_炎_上昇_弱(false, "被属性_炎が７％上がる", "", "", "") {
	},//
	被属性_炎_上昇_中(false, "被属性_炎が１４％上がる", "", "", "") {
	},//
	被属性_炎_上昇_強(false, "被属性_炎が２１％上がる", "", "", "") {
	},//
	被属性_炎_低下_弱(false, "被属性_炎が７％下がる", "", "", "") {
	},//
	被属性_炎_低下_中(false, "被属性_炎が１４％下がる", "", "", "") {
	},//
	被属性_炎_低下_強(false, "被属性_炎が２１％下がる", "", "", "") {
	},//
	被属性_氷_上昇_弱(false, "被属性_氷が７％上がる", "", "", "") {
	},//
	被属性_氷_上昇_中(false, "被属性_氷が１４％上がる", "", "", "") {
	},//
	被属性_氷_上昇_強(false, "被属性_氷が２１％上がる", "", "", "") {
	},//
	被属性_氷_低下_弱(false, "被属性_氷が７％下がる", "", "", "") {
	},//
	被属性_氷_低下_中(false, "被属性_氷が１４％下がる", "", "", "") {
	},//
	被属性_氷_低下_強(false, "被属性_氷が２１％下がる", "", "", "") {
	},//
	被属性_水_上昇_弱(false, "被属性_水が７％上がる", "", "", "") {
	},//
	被属性_水_上昇_中(false, "被属性_水が１４％上がる", "", "", "") {
	},//
	被属性_水_上昇_強(false, "被属性_水が２１％上がる", "", "", "") {
	},//
	被属性_水_低下_弱(false, "被属性_水が７％下がる", "", "", "") {
	},//
	被属性_水_低下_中(false, "被属性_水が１４％下がる", "", "", "") {
	},//
	被属性_水_低下_強(false, "被属性_水が２１％下がる", "", "", "") {
	},//
	被属性_雷_上昇_弱(false, "被属性_雷が７％上がる", "", "", "") {
	},//
	被属性_雷_上昇_中(false, "被属性_雷が１４％上がる", "", "", "") {
	},//
	被属性_雷_上昇_強(false, "被属性_雷が２１％上がる", "", "", "") {
	},//
	被属性_雷_低下_弱(false, "被属性_雷が７％下がる", "", "", "") {
	},//
	被属性_雷_低下_中(false, "被属性_雷が１４％下がる", "", "", "") {
	},//
	被属性_雷_低下_強(false, "被属性_雷が２１％下がる", "", "", "") {
	},//
	被属性_風_上昇_弱(false, "被属性_風が７％上がる", "", "", "") {
	},//
	被属性_風_上昇_中(false, "被属性_風が１４％上がる", "", "", "") {
	},//
	被属性_風_上昇_強(false, "被属性_風が２１％上がる", "", "", "") {
	},//
	被属性_風_低下_弱(false, "被属性_風が７％下がる", "", "", "") {
	},//
	被属性_風_低下_中(false, "被属性_風が１４％下がる", "", "", "") {
	},//
	被属性_風_低下_強(false, "被属性_風が２１％下がる", "", "", "") {
	},//
	被属性_土_上昇_弱(false, "被属性_土が７％上がる", "", "", "") {
	},//
	被属性_土_上昇_中(false, "被属性_土が１４％上がる", "", "", "") {
	},//
	被属性_土_上昇_強(false, "被属性_土が２１％上がる", "", "", "") {
	},//
	被属性_土_低下_弱(false, "被属性_土が７％下がる", "", "", "") {
	},//
	被属性_土_低下_中(false, "被属性_土が１４％下がる", "", "", "") {
	},//
	被属性_土_低下_強(false, "被属性_土が２１％下がる", "", "", "") {
	},//
	被属性_光_上昇_弱(false, "被属性_光が７％上がる", "", "", "") {
	},//
	被属性_光_上昇_中(false, "被属性_光が１４％上がる", "", "", "") {
	},//
	被属性_光_上昇_強(false, "被属性_光が２１％上がる", "", "", "") {
	},//
	被属性_光_低下_弱(false, "被属性_光が７％下がる", "", "", "") {
	},//
	被属性_光_低下_中(false, "被属性_光が１４％下がる", "", "", "") {
	},//
	被属性_光_低下_強(false, "被属性_光が２１％下がる", "", "", "") {
	},//
	被属性_闇_上昇_弱(false, "被属性_闇が７％上がる", "", "", "") {
	},//
	被属性_闇_上昇_中(false, "被属性_闇が１４％上がる", "", "", "") {
	},//
	被属性_闇_上昇_強(false, "被属性_闇が２１％上がる", "", "", "") {
	},//
	被属性_闇_低下_弱(false, "被属性_闇が７％下がる", "", "", "") {
	},//
	被属性_闇_低下_中(false, "被属性_闇が１４％下がる", "", "", "") {
	},//
	被属性_闇_低下_強(false, "被属性_闇が２１％下がる", "", "", "") {
	},//
	被属性_神秘_上昇_弱(false, "被属性_神秘が７％上がる", "", "", "") {
	},//
	被属性_神秘_上昇_中(false, "被属性_神秘が１４％上がる", "", "", "") {
	},//
	被属性_神秘_上昇_強(false, "被属性_神秘が２１％上がる", "", "", "") {
	},//
	被属性_神秘_低下_弱(false, "被属性_神秘が７％下がる", "", "", "") {
	},//
	被属性_神秘_低下_中(false, "被属性_神秘が１４％下がる", "", "", "") {
	},//
	被属性_神秘_低下_強(false, "被属性_神秘が２１％下がる", "", "", "") {
	},//
	被属性_精神_上昇_弱(false, "被属性_精神が７％上がる", "", "", "") {
	},//
	被属性_精神_上昇_中(false, "被属性_精神が１４％上がる", "", "", "") {
	},//
	被属性_精神_上昇_強(false, "被属性_精神が２１％上がる", "", "", "") {
	},//
	被属性_精神_低下_弱(false, "被属性_精神が７％下がる", "", "", "") {
	},//
	被属性_精神_低下_中(false, "被属性_精神が１４％下がる", "", "", "") {
	},//
	被属性_精神_低下_強(false, "被属性_精神が２１％下がる", "", "", "") {
	},//
	被属性_錬金_上昇_弱(false, "被属性_錬金が７％上がる", "", "", "") {
	},//
	被属性_錬金_上昇_中(false, "被属性_錬金が１４％上がる", "", "", "") {
	},//
	被属性_錬金_上昇_強(false, "被属性_錬金が２１％上がる", "", "", "") {
	},//
	被属性_錬金_低下_弱(false, "被属性_錬金が７％下がる", "", "", "") {
	},//
	被属性_錬金_低下_中(false, "被属性_錬金が１４％下がる", "", "", "") {
	},//
	被属性_錬金_低下_強(false, "被属性_錬金が２１％下がる", "", "", "") {
	},//
	被属性_時空_上昇_弱(false, "被属性_時空が７％上がる", "", "", "") {
	},//
	被属性_時空_上昇_中(false, "被属性_時空が１４％上がる", "", "", "") {
	},//
	被属性_時空_上昇_強(false, "被属性_時空が２１％上がる", "", "", "") {
	},//
	被属性_時空_低下_弱(false, "被属性_時空が７％下がる", "", "", "") {
	},//
	被属性_時空_低下_中(false, "被属性_時空が１４％下がる", "", "", "") {
	},//
	被属性_時空_低下_強(false, "被属性_時空が２１％下がる", "", "", "") {
	},//
	耐性_木化_上昇_弱(false, "耐性_木化が７％上がる", "", "", "") {
	},//
	耐性_木化_上昇_中(false, "耐性_木化が１４％上がる", "", "", "") {
	},//
	耐性_木化_上昇_強(false, "耐性_木化が２１％上がる", "", "", "") {
	},//
	耐性_木化_低下_弱(false, "耐性_木化が７％下がる", "", "", "") {
	},//
	耐性_木化_低下_中(false, "耐性_木化が１４％下がる", "", "", "") {
	},//
	耐性_木化_低下_強(false, "耐性_木化が２１％下がる", "", "", "") {
	},//
	耐性_黄金化_上昇_弱(false, "耐性_黄金化が７％上がる", "", "", "") {
	},//
	耐性_黄金化_上昇_中(false, "耐性_黄金化が１４％上がる", "", "", "") {
	},//
	耐性_黄金化_上昇_強(false, "耐性_黄金化が２１％上がる", "", "", "") {
	},//
	耐性_黄金化_低下_弱(false, "耐性_黄金化が７％下がる", "", "", "") {
	},//
	耐性_黄金化_低下_中(false, "耐性_黄金化が１４％下がる", "", "", "") {
	},//
	耐性_黄金化_低下_強(false, "耐性_黄金化が２１％下がる", "", "", "") {
	},//
	耐性_封印_上昇_弱(false, "耐性_封印が７％上がる", "", "", "") {
	},//
	耐性_封印_上昇_中(false, "耐性_封印が１４％上がる", "", "", "") {
	},//
	耐性_封印_上昇_強(false, "耐性_封印が２１％上がる", "", "", "") {
	},//
	耐性_封印_低下_弱(false, "耐性_封印が７％下がる", "", "", "") {
	},//
	耐性_封印_低下_中(false, "耐性_封印が１４％下がる", "", "", "") {
	},//
	耐性_封印_低下_強(false, "耐性_封印が２１％下がる", "", "", "") {
	},//
	耐性_眠り_上昇_弱(false, "耐性_眠りが７％上がる", "", "", "") {
	},//
	耐性_眠り_上昇_中(false, "耐性_眠りが１４％上がる", "", "", "") {
	},//
	耐性_眠り_上昇_強(false, "耐性_眠りが２１％上がる", "", "", "") {
	},//
	耐性_眠り_低下_弱(false, "耐性_眠りが７％下がる", "", "", "") {
	},//
	耐性_眠り_低下_中(false, "耐性_眠りが１４％下がる", "", "", "") {
	},//
	耐性_眠り_低下_強(false, "耐性_眠りが２１％下がる", "", "", "") {
	},//
	耐性_麻痺_上昇_弱(false, "耐性_麻痺が７％上がる", "", "", "") {
	},//
	耐性_麻痺_上昇_中(false, "耐性_麻痺が１４％上がる", "", "", "") {
	},//
	耐性_麻痺_上昇_強(false, "耐性_麻痺が２１％上がる", "", "", "") {
	},//
	耐性_麻痺_低下_弱(false, "耐性_麻痺が７％下がる", "", "", "") {
	},//
	耐性_麻痺_低下_中(false, "耐性_麻痺が１４％下がる", "", "", "") {
	},//
	耐性_麻痺_低下_強(false, "耐性_麻痺が２１％下がる", "", "", "") {
	},//
	耐性_混乱_上昇_弱(false, "耐性_混乱が７％上がる", "", "", "") {
	},//
	耐性_混乱_上昇_中(false, "耐性_混乱が１４％上がる", "", "", "") {
	},//
	耐性_混乱_上昇_強(false, "耐性_混乱が２１％上がる", "", "", "") {
	},//
	耐性_混乱_低下_弱(false, "耐性_混乱が７％下がる", "", "", "") {
	},//
	耐性_混乱_低下_中(false, "耐性_混乱が１４％下がる", "", "", "") {
	},//
	耐性_混乱_低下_強(false, "耐性_混乱が２１％下がる", "", "", "") {
	},//
	耐性_毒_上昇_弱(false, "耐性_毒が７％上がる", "", "", "") {
	},//
	耐性_毒_上昇_中(false, "耐性_毒が１４％上がる", "", "", "") {
	},//
	耐性_毒_上昇_強(false, "耐性_毒が２１％上がる", "", "", "") {
	},//
	耐性_毒_低下_弱(false, "耐性_毒が７％下がる", "", "", "") {
	},//
	耐性_毒_低下_中(false, "耐性_毒が１４％下がる", "", "", "") {
	},//
	耐性_毒_低下_強(false, "耐性_毒が２１％下がる", "", "", "") {
	},//
	耐性_炎上_上昇_弱(false, "耐性_炎上が７％上がる", "", "", "") {
	},//
	耐性_炎上_上昇_中(false, "耐性_炎上が１４％上がる", "", "", "") {
	},//
	耐性_炎上_上昇_強(false, "耐性_炎上が２１％上がる", "", "", "") {
	},//
	耐性_炎上_低下_弱(false, "耐性_炎上が７％下がる", "", "", "") {
	},//
	耐性_炎上_低下_中(false, "耐性_炎上が１４％下がる", "", "", "") {
	},//
	耐性_炎上_低下_強(false, "耐性_炎上が２１％下がる", "", "", "") {
	},//
	耐性_凍結_上昇_弱(false, "耐性_凍結が７％上がる", "", "", "") {
	},//
	耐性_凍結_上昇_中(false, "耐性_凍結が１４％上がる", "", "", "") {
	},//
	耐性_凍結_上昇_強(false, "耐性_凍結が２１％上がる", "", "", "") {
	},//
	耐性_凍結_低下_弱(false, "耐性_凍結が７％下がる", "", "", "") {
	},//
	耐性_凍結_低下_中(false, "耐性_凍結が１４％下がる", "", "", "") {
	},//
	耐性_凍結_低下_強(false, "耐性_凍結が２１％下がる", "", "", "") {
	},//
	耐性_出血_上昇_弱(false, "耐性_出血が７％下がる", "", "", "") {
	},//
	耐性_出血_上昇_中(false, "耐性_出血が１４％下がる", "", "", "") {
	},//
	耐性_出血_上昇_強(false, "耐性_出血が２１％下がる", "", "", "") {
	},//
	耐性_出血_低下_弱(false, "耐性_出血が７％上がる", "", "", "") {
	},//
	耐性_出血_低下_中(false, "耐性_出血が１４％上がる", "", "", "") {
	},//
	耐性_出血_低下_強(false, "耐性_出血が２１％上がる", "", "", "") {
	},//
	経験値増大_弱(false, "取得経験値が１０％上がる", "", "", "") {
	},//
	経験値増大_中(false, "取得経験値が２０％上がる", "", "", "") {
	},//
	経験値増大_強(false, "取得経験値が３０％上がる", "", "", "") {
	},//
	ほろ酔い(false, "ちょっと酔っているのであらゆるステータスが５％下がる", "", "", "") {

		private static final float MUL = 0.95f;

		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			for (StatusValue sv : r) {
				sv.mulMax(MUL);
				sv.mul(MUL);
			}
			return r;
		}

		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			for (AttributeValue sv : r) {
				float val = sv.getValue();
				val *= MUL;
				sv.add(-(val - sv.getValue()));
			}

			return r;
		}

		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			for (AttributeValue sv : r) {
				float val = sv.getValue();
				val *= MUL;
				sv.add(val - sv.getValue());
			}

			return r;
		}
	},
	泥酔(false, "ひどく酔っているのであらゆるステータスが４０％下がる", "", "", "") {

		private static final float MUL = 0.6f;

		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			for (StatusValue sv : r) {
				sv.mulMax(MUL);
				sv.mul(MUL);
			}
			return r;
		}

		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			for (AttributeValue sv : r) {
				float val = sv.getValue();
				val *= MUL;
				sv.add(-(val - sv.getValue()));
			}

			return r;
		}

		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			for (AttributeValue sv : r) {
				float val = sv.getValue();
				val *= MUL;
				sv.add(val - sv.getValue());
			}

			return r;
		}
	},
	二日酔い(false, "ひどい二日酔いであらゆるステータスが２０％下がる", "", "", "") {

		private static final float MUL = 0.8f;

		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			for (StatusValue sv : r) {
				sv.mulMax(MUL);
				sv.mul(MUL);
			}
			return r;
		}

		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			for (AttributeValue sv : r) {
				float val = sv.getValue();
				val *= MUL;
				sv.add(-(val - sv.getValue()));
			}

			return r;
		}

		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			for (AttributeValue sv : r) {
				float val = sv.getValue();
				val *= MUL;
				sv.add(val - sv.getValue());
			}

			return r;
		}
	};
	private boolean registOn;
	private String descI18NKey;
	private String startMsg, execMsg, endMsg;

	private ConditionKey(boolean registOn, String descI18NKey, String startMsg, String execMsg, String endMsg) {
		this.registOn = registOn;
		this.descI18NKey = descI18NKey;
		this.startMsg = startMsg;
		this.execMsg = execMsg;
		this.endMsg = endMsg;
	}

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public String getStartMsgI18NK() {
		return startMsg;
	}

	public String getExecMsgI18NK() {
		return execMsg;
	}

	public String getEndMsgI18NK() {
		return endMsg;
	}

	public String getDescI18NK() {
		return descI18NKey;
	}

	public String getDescI18Nd() {
		return I18N.get(descI18NKey);
	}

	public String getStartMsgI18Nd() {
		return I18N.get(startMsg);
	}

	public String getExecMsgI18Nd() {
		return I18N.get(execMsg);
	}

	public String getEndMsgI18Nd() {
		return I18N.get(endMsg);
	}

	public boolean isRegistOn() {
		return registOn;
	}
	private static Map<ConditionKey, Animation> conditinoAnimations = new HashMap<>();

	static void loadAnimationFromXML(String filePath) {
		XMLFile f = new XMLFile(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(f);
		}
		XMLElement root = f.load().getFirst();
		for (XMLElement e : root.getElement("cndAnimation")) {
			ConditionKey k = e.getAttributes().get("key").of(ConditionKey.class);

			String fileName = e.getAttributes().get("image").getValue();
			int w = e.getAttributes().get("w").getIntValue();
			int h = e.getAttributes().get("h").getIntValue();
			int[] speed = e.getAttributes().get("tc").safeParseInt(",");
			Animation a = new Animation(new FrameTimeCounter(speed), new SpriteSheet(fileName).rows(0, w, h).images());
			conditinoAnimations.put(k, a);
		}
		f.dispose();
	}

	public boolean hasAnimation() {
		return conditinoAnimations.containsKey(this);
	}

	@Nullable
	public Animation getAnimation() {
		return conditinoAnimations.get(this);
	}

	public boolean isバフ() {
		return getDescI18NK().endsWith("上がる");
	}

	public boolean isデバフ() {
		return getDescI18NK().endsWith("下がる");
	}
}
