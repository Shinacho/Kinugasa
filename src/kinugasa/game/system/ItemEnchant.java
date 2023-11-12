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

/**
 * アイテムエンチャントはアイテムに後付けされる属性で、装備アイテムは1つ持つことができます。
 *
 * @vesion 1.0.0 - 2023/10/14_11:55:16<br>
 * @author Shinacho<br>
 */
public enum ItemEnchant implements ItemEqipEffect {
	複雑("解体したときの素材が２倍になる") {
		@Override
		public Map<Material, Integer> mulDissaseMaterial(Map<Material, Integer> m) {
			Map<Material, Integer> r = new HashMap<>();
			for (Map.Entry<Material, Integer> e : m.entrySet()) {
				r.put(e.getKey(), e.getValue() * 2);
			}
			return r;
		}

	},
	装飾("売値が２倍になる") {
		@Override
		public int mulValue(int v) {
			return v * 2;
		}
	},
	残像("攻撃回数が１増える") {
		@Override
		public int mulAtkCount(int c) {
			return c + 1;
		}

	},
	加護("装備効果が１０％上がる") {
		private static final float MUL = 1.1f;

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
	万化("装備効果が５０％上がる") {
		private static final float MUL = 1.5f;

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
	神妙("装備効果が２倍になる") {
		private static final float MUL = 2f;

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
	交信("魔法が使えるようになる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusValue sv = r.getOrCreate(StatusKey.魔術使用可否, () -> new StatusValue(StatusKey.魔術使用可否, 0));
			sv.setValue(StatusKey.魔術使用可否＿使用可能);
			return r;
		}

	},
	技巧("物理クリティカル率が７％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.クリティカル率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(0.07f);
			return r;
		}

	},
	残酷("物理クリティカルダメージが５０％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(0.5f);
			return r;
		}
	},
	奇跡("魔法クリティカル率が７％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.魔法クリティカル率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(0.07f);
			return r;
		}
	},
	共鳴("魔法クリティカルダメージが５０％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.魔法クリティカルダメージ倍数;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(0.5f);
			return r;
		}
	},
	身軽("物理回避率が７％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.回避率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(0.07f);
			return r;
		}
	},
	騎士("物理ブロック率が７％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.ブロック率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(0.07f);
			return r;
		}
	},
	鉄塊("物理ブロックダメージが５０％下がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(-0.5f);
			return r;
		}
	},
	予知("魔法回避率が７％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.魔法回避率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(0.07f);
			return r;
		}
	},
	導師("魔法ブロック率が７％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.魔法ブロック率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(0.07f);
			return r;
		}
	},
	障壁("魔法ブロックダメージが５０％下がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.魔法ブロックダメージ倍率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(-0.5f);
			return r;
		}
	},
	怪力("筋力が７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.筋力;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	繊細("器用さが７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.器用さ;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	迅速("素早さが７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.素早さ;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	瞑想("精神が７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.精神力;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	祈願("信仰が７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.信仰;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	速読("詠唱が７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.詠唱;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	強靱("体力が５００上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.体力;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.addMax(500f);
			return r;
		}
	},
	霊符("魔力が５００上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.魔力;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.addMax(500f);
			return r;
		}
	},
	安心("正気度が７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.正気度;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.addMax(7f);
			return r;
		}
	},
	不敗("物理防御力が７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.防御力;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	結界("魔法防御力が７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.魔法防御力;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	躍動("行動力が７上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.行動力;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.add(7f);
			return r;
		}
	},
	誘導("命中が７％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.命中率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.mul(7f);
			return r;
		}
	},
	予言("魔法命中が７％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k = StatusKey.魔法命中率;
			StatusValue sv = r.getOrCreate(k, () -> new StatusValue(k, 0));
			sv.mul(7f);
			return r;
		}
	},
	鳴神("雷属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.雷;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	炎帝("火属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.炎;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	雪霙("氷属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.氷;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	清水("水属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.水;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	地動("土属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.土;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	風車("風属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.風;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	常闇("闇属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.闇;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	光司("光属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.光;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	未知("神秘属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.神秘;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	侵害("精神属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.精神;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	想起("錬金属性攻撃の与ダメージが７％上がる") {
		@Override
		public AttributeValueSet getAttrOut(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.錬金;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(0.07f);
			return r;
		}

	},
	遠雷("雷属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.雷;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	耐熱("火属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.炎;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	炎熱("氷属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.氷;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	撥水("水属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.水;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	安定("土属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.土;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	防風("風属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.風;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	天照("闇属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.闇;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	暗闇("光属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.光;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	捕縛("神秘属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.神秘;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	鈍感("精神属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.精神;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	耐性("錬金属性攻撃の与ダメージが７％下がる") {
		@Override
		public AttributeValueSet getAttrIn(AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			AttributeKey k = AttributeKey.錬金;
			AttributeValue av = r.getOrCreate(k, () -> new AttributeValue(k, 0));
			av.add(-0.07f);
			return r;
		}

	},
	延伸("攻撃範囲が１８上がる"),
	絶望("攻撃時７％の確率で敵を解脱させる") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.解脱;
		}

	},
	粉砕("攻撃時７％の確率で敵を損壊させる") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.損壊;
		}

	},
	気絶("攻撃時７％の確率で３ターンの気絶の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.気絶;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	木化("攻撃時７％の確率で３ターンの木化の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.木化;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	黄金("攻撃時７％の確率で３ターンの黄金化の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.黄金化;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	封印("攻撃時７％の確率で３ターンの封印の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.封印;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	出血("攻撃時７％の確率で３ターンの出血の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.出血;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	微睡("攻撃時７％の確率で３ターンの眠りの状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.眠り;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	麻痺("攻撃時７％の確率で３ターンの麻痺の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.麻痺;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	炎上("攻撃時７％の確率で３ターンの炎上の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.炎上;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	凍結("攻撃時７％の確率で３ターンの凍結の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.凍結;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	混乱("攻撃時７％の確率で３ターンの混乱の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.混乱;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	毒々("攻撃時７％の確率で３ターンの毒の状態異常を付与する") {
		@Override
		public float getCndPercent() {
			return 0.07f;
		}

		@Override
		public ConditionKey getCndKey() {
			return ConditionKey.毒;
		}

		@Override
		public int getCndTime() {
			return 3;
		}

	},
	真理("解脱の状態異常耐性が７％上がる"),
	存在("損壊の状態異常耐性が７％上がる"),
	意思("気絶の状態異常耐性が７％上がる"),
	冬枯("木化の状態異常耐性が７％上がる"),
	鍍金("黄金化の状態異常耐性が７％上がる"),
	知覚("封印の状態異常耐性が７％上がる"),
	止血("出血の状態異常耐性が７％上がる"),
	不眠("眠りの状態異常耐性が７％上がる"),
	振動("麻痺の状態異常耐性が７％上がる"),
	氷水("炎上の状態異常耐性が７％上がる"),
	暖炉("凍結の状態異常耐性が７％上がる"),
	安堵("混乱の状態異常耐性が７％上がる"),
	解毒("毒の状態異常耐性が７％上がる"),
	修練("獲得経験値が７％増加する") {
	},
	反撃("７％の確率で物理攻撃を反射する"),
	回天("７％の確率で魔法攻撃を反射する"),
	掌握("７％の確率で物理攻撃を吸収する"),
	吸引("７％の確率で魔法攻撃を吸収する"),;
	private String descI18NKey;

	private ItemEnchant(String descI18NKey) {
		this.descI18NKey = descI18NKey;
	}

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public String descI18N() {
		return I18N.get(descI18NKey);
	}

	public String getDescI18NKey() {
		return descI18NKey;
	}
}
