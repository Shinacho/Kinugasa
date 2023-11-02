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

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import kinugasa.game.I18N;

/**
 * アイテムスタイルはアイテムの製造時に決まる特性で、すべての装備アイテムが1つ持っています。
 *
 * @vesion 1.0.0 - 2023/10/14_12:01:25<br>
 * @author Shinacho<br>
 */
public enum ItemStyle implements ItemEqipEffect {
	普通の("特殊な効果はない") {
	},
	原始の("体力の上限が５００上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k1 = StatusKey.体力;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0, k1.getMin(), k1.getMax()));
			s1.setMax(+500);
			return r;
		}
	},
	古風な("魔力の上限が５００上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k1 = StatusKey.魔力;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0, k1.getMin(), k1.getMax()));
			s1.setMax(+500);
			return r;
		}
	},
	近代の("正気度の上限が１０上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k1 = StatusKey.正気度;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0, k1.getMin(), k1.getMax()));
			s1.setMax(+10);
			return r;
		}
	},
	軽量な("行動力が１２上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k1 = StatusKey.行動力;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0));
			s1.add(12);
			s1.setMax(+12);
			return r;
		}
	},
	英雄の("確率以外のステータスが５上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			for (StatusKey k : StatusKey.values()) {
				if (k.isPercent()) {
					continue;
				}
				if (k == StatusKey.装備属性
						|| k == StatusKey.魔術使用可否
						|| k == StatusKey.レベル
						|| k == StatusKey.次のレベルの経験値
						|| k == StatusKey.保有経験値) {
					continue;
				}
				switch (k) {
					case 体力:
					case 正気度:
					case 魔力: {
						StatusValue s = r.getOrCreate(k, () -> new StatusValue(k, 0, 0, 0));
						s.add(5);
						s.addMax(5);
						break;
					}
					default: {
						StatusValue s = r.getOrCreate(k, () -> new StatusValue(k, 0));
						s.add(5);
						break;
					}
				}
			}
			return r;
		}
	},
	危険な("攻撃力と魔法攻撃力が３０上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k1 = StatusKey.攻撃力;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0));
			s1.add(30);
			s1.setMax(+30);
			StatusKey k2 = StatusKey.魔法攻撃力;
			StatusValue s2 = r.getOrCreate(k2, () -> new StatusValue(k2, 0));
			s2.add(30);
			s2.setMax(+30);
			return r;
		}
	},
	頑丈な("防御力と魔法防御力が３０上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k1 = StatusKey.防御力;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0));
			s1.add(30);
			s1.setMax(+30);
			StatusKey k2 = StatusKey.魔法防御力;
			StatusValue s2 = r.getOrCreate(k2, () -> new StatusValue(k2, 0));
			s2.add(30);
			s2.setMax(+30);
			return r;
		}
	},
	簡素な("売値が半分になるが強化素材も半分になる") {
		@Override
		public int mulValue(int v) {
			return v / 2;
		}

		@Override
		public Map<Material, Integer> mulUpgradeMaterial(Map<Material, Integer> m) {
			Map<Material, Integer> map = new HashMap<>();
			for (Map.Entry<Material, Integer> e : m.entrySet()) {
				map.put(e.getKey(), e.getValue() / 2);
			}
			return map;
		}
	},
	緻密な("売値が２倍になるが強化素材も２倍になる") {
		@Override
		public int mulValue(int v) {
			return v * 2;
		}

		@Override
		public Map<Material, Integer> mulUpgradeMaterial(Map<Material, Integer> m) {
			Map<Material, Integer> map = new HashMap<>();
			for (Map.Entry<Material, Integer> e : m.entrySet()) {
				map.put(e.getKey(), e.getValue() * 2);
			}
			return map;
		}
	},
	我流の("解体で得られる素材が２倍になり強化素材が半分になる") {
		@Override
		public Map<Material, Integer> mulUpgradeMaterial(Map<Material, Integer> m) {
			Map<Material, Integer> map = new HashMap<>();
			for (Map.Entry<Material, Integer> e : m.entrySet()) {
				map.put(e.getKey(), e.getValue() / 2);
			}
			return map;
		}

		@Override
		public Map<Material, Integer> mulDissaseMaterial(Map<Material, Integer> m) {
			Map<Material, Integer> map = new HashMap<>();
			for (Map.Entry<Material, Integer> e : m.entrySet()) {
				map.put(e.getKey(), e.getValue() * 2);
			}
			return map;
		}

	},
	珍品の("売値と装備効果が５％上がる") {
		private static final float MUL = 1.05f;

		@Override
		public int mulValue(int v) {
			return (int) (v * MUL);
		}

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
	試作の("売値と装備効果が１０％上がる") {
		private static final float MUL = 1.10f;

		@Override
		public int mulValue(int v) {
			return (int) (v * MUL);
		}

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
	理想の("売値と装備効果が２５％上がる") {
		private static final float MUL = 1.25f;

		@Override
		public int mulValue(int v) {
			return (int) (v * MUL);
		}

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
	幻想の("売値と装備効果が５０％上がる") {
		private static final float MUL = 1.50f;

		@Override
		public int mulValue(int v) {
			return (int) (v * MUL);
		}

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
	神秘の("売値と装備効果が２倍になるが正気度が４２下がる") {
		private static final float MUL = 2f;

		@Override
		public int mulValue(int v) {
			return (int) (v * MUL);
		}

		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			for (StatusValue sv : r) {
				sv.mulMax(MUL);
				sv.mul(MUL);
			}

			StatusKey k1 = StatusKey.正気度;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0, k1.getMin(), k1.getMax()));
			s1.setMax(k1.getMax() - 42);
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
	伝説の("売値と装備効果が３倍になる") {
		private static final float MUL = 3f;

		@Override
		public int mulValue(int v) {
			return (int) (v * MUL);
		}

		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			for (StatusValue sv : r) {
				StatusKey k = sv.getKey();
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
	増幅の("このアイテムのエンチャント効果が４倍になる") {
	},
	猛攻の("物理と魔法のクリティカルダメージが８０％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k1 = StatusKey.クリティカルダメージ倍数;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0));
			s1.add(0.8f);
			StatusKey k2 = StatusKey.魔法クリティカルダメージ倍数;
			StatusValue s2 = r.getOrCreate(k2, () -> new StatusValue(k2, 0));
			s2.add(0.8f);
			return r;
		}

	},
	獰猛な("物理と魔法のクリティカル率が２１％上がる") {
		@Override
		public StatusValueSet getStatusValue(StatusValueSet v) {
			StatusValueSet r = v.clone();
			StatusKey k1 = StatusKey.クリティカル率;
			StatusValue s1 = r.getOrCreate(k1, () -> new StatusValue(k1, 0));
			s1.add(0.21f);
			StatusKey k2 = StatusKey.魔法クリティカル率;
			StatusValue s2 = r.getOrCreate(k2, () -> new StatusValue(k2, 0));
			s2.add(0.21f);
			return r;
		}

	},
	小振な("攻撃範囲が半分になるが攻撃回数が１上がる") {
		@Override
		public int mulAtkCount(int c) {
			return c + 1;
		}

		@Override
		public int mulArea(int a) {
			return a / 2;
		}

	},
	大振な("攻撃範囲が１０％上がる") {
		@Override
		public int mulArea(int a) {
			return (int) (a * 1.1f);
		}

	},
	注入の("このアイテムのエンチャントによる状態異常付与確率が５倍になる") {
		@Override
		public float mulCndPercent(float v) {
			return v * 5f;
		}

	};
	private String descI18NKey;

	private ItemStyle(String descI18NKey) {
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
