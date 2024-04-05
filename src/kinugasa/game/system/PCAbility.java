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

import kinugasa.game.I18N;

/**
 *
 * @vesion 1.0.0 - 2023/10/29_18:57:02<br>
 * @author Shinacho<br>
 */
public enum PCAbility implements AbilityEffect {

	毒舌なる若き風魔導士("風の与属性が２２％上昇する") {
		@Override
		public AttributeValueSet effectAttrIn(Status s, AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			r.get(AttributeKey.時空).mul(0.0001f);
			return r;
		}

		@Override
		public AttributeValueSet effectAttrOut(Status s, AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			r.get(AttributeKey.風).mul(1.22f);
			r.get(AttributeKey.時空).mul(100f);
			return r;
		}
	},
	敏腕探偵("打撃系武器か銃を装備しているときクリティカル率が５０％上昇する") {

		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			boolean is武器装備 = false;
			is武器装備 |= (s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.フレイル);
			is武器装備 |= (s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.棍棒);
			is武器装備 |= (s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.槌);
			is武器装備 |= (s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.銃);

			is武器装備 |= (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.フレイル);
			is武器装備 |= (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.棍棒);
			is武器装備 |= (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.槌);
			is武器装備 |= (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.銃);

			if (is武器装備) {
				r.get(StatusKey.クリティカル率).mul(1.5f);
			}
			return r;
		}
	},
	伝説を探す筋肉の塊("武器を装備していないとき筋力と回避率が２倍になる") {
		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			if (s.getEqip().get(EqipSlot.右手) == null && s.getEqip().get(EqipSlot.左手) == null) {
				r.get(StatusKey.筋力).mulMax(2f);
				r.get(StatusKey.筋力).mul(2f);
				r.get(StatusKey.回避率).mulMax(2f);
				r.get(StatusKey.回避率).mul(2f);
			}
			return r;
		}

	},
	盾の乙女("盾を装備しているときすべての被属性が１４％下がる") {

		@Override
		public AttributeValueSet effectAttrIn(Status s, AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			if ((s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.盾)
					|| (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.盾)) {
				r.stream().forEach(p -> p.mul(0.86f));
			}
			return r;
		}

	},
	役者("魔力と正気度が２５％上がる") {
		@Override
		public AttributeValueSet effectAttrIn(Status s, AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			r.get(AttributeKey.時空).mul(0.0001f);
			return r;
		}

		@Override
		public AttributeValueSet effectAttrOut(Status s, AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			r.get(AttributeKey.時空).mul(100f);
			return r;
		}

		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			float 割合 = r.get(StatusKey.魔力).get割合();
			r.get(StatusKey.魔力).mulMax(1.25f);
			r.get(StatusKey.魔力).setBy割合(割合);
			割合 = r.get(StatusKey.正気度).get割合();
			r.get(StatusKey.正気度).mulMax(1.25f);
			r.get(StatusKey.正気度).setBy割合(割合);
			return r;
		}

	},
	知恵ある錬金術師("錬金の与属性が１４％上昇し被属性が１４％低下する") {
		@Override
		public AttributeValueSet effectAttrIn(Status s, AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			r.get(AttributeKey.錬金).mul(0.86f);
			return r;
		}

		@Override
		public AttributeValueSet effectAttrOut(Status s, AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			r.get(AttributeKey.錬金).mul(1.14f);
			return r;
		}

	},
	田舎漁師の跡継ぎ息子("体力が１４％上がる") {
		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			float 割合 = r.get(StatusKey.体力).get割合();
			r.get(StatusKey.体力).mulMax(1.14f);
			r.get(StatusKey.体力).setBy割合(割合);
			return r;
		}
	},
	へこたれぬかわいそうな子("正気度が１４％上がる") {
		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			float 割合 = r.get(StatusKey.正気度).get割合();
			r.get(StatusKey.正気度).mulMax(1.14f);
			r.get(StatusKey.正気度).setBy割合(割合);
			return r;
		}
	},
	天真爛漫やんちゃガール("行動力と司馬屋さが１４％上がる") {
		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			r.get(StatusKey.行動力).mulMax(1.14f);
			r.get(StatusKey.行動力).mul(1.14f);
			r.get(StatusKey.素早さ).mulMax(1.14f);
			r.get(StatusKey.素早さ).mul(1.14f);
			return r;
		}

	},
	剣の長女("剣か細剣を装備中クリティカル率が３５％上がる") {

		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			boolean is剣装備 = false;
			is剣装備 |= (s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.剣);
			is剣装備 |= (s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.細剣);
			is剣装備 |= (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.剣);
			is剣装備 |= (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.細剣);

			if (is剣装備) {
				r.get(StatusKey.クリティカル率).mul(1.35f);
			}
			return r;
		}
	},
	飢えた放浪騎士("刀か大剣を装備中クリティカル確率が半分になるがクリティカルダメージが２倍になる") {
		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			if ((s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.刀)
					|| (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.刀)
					|| (s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.大剣)) {
				r.get(StatusKey.クリティカルダメージ倍数).mul(2f);
				r.get(StatusKey.クリティカル率).mul(0.5f);
			}
			return r;
		}
	},
	動物なかよし("弓を装備中命中率が２１％上がる") {
		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			boolean is弓装備 = false;
			is弓装備 |= (s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.弓);
			is弓装備 |= (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.弓);

			StatusValueSet r = v.clone();
			if (is弓装備) {
				r.get(StatusKey.命中率).mul(1.21f);
			}
			return r;
		}
	},
	硬き不動の魔法剣士("魔法剣を装備中物理ブロック率が２１％上がる") {
		@Override
		public StatusValueSet effectStatus(Status s, StatusValueSet v) {
			StatusValueSet r = v.clone();
			if ((s.getEqip().get(EqipSlot.右手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.魔法剣)
					|| (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.魔法剣)
					|| (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.魔法剣)
					|| (s.getEqip().get(EqipSlot.左手) != null && s.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.魔法剣)) {
				r.get(StatusKey.ブロック率).mul(1.21f);
			}
			return r;
		}
	},
	不思議ちゃん("精神と神秘の与属性が１４％上がる") {
		@Override
		public AttributeValueSet effectAttrOut(Status s, AttributeValueSet v) {
			AttributeValueSet r = v.clone();
			r.get(AttributeKey.精神).mul(1.14f);
			r.get(AttributeKey.神秘).mul(1.14f);
			return r;
		}
	},
	忠犬("近くにいる仲間の正気度を回復できる") {

	};
	private String desc;

	private PCAbility(String desc) {
		this.desc = desc;
	}

	public String getDescI18Nd() {
		return I18N.get(desc);
	}

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public String getDescI18NK() {
		return desc;
	}

	public static void main(String... args) {
		for (var v : PCAbility.values()) {
			System.out.println("\"" + v + '"' + "," + '"' + v + '"');
		}
		for (var v : PCAbility.values()) {
			System.out.println("\"" + v.desc + '"' + "," + '"' + v.desc + '"');
		}
	}

}
