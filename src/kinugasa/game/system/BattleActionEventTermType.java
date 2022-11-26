/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_16:18:56<br>
 * @author Dra211<br>
 */
public enum BattleActionEventTermType {
	/**
	 * 術者がvalueのアイテムを装備しているか検査します。
	 */
	EQ_ITEM {
		@Override
		public boolean canDoThis(String value, Status user) {
			Item i = ItemStorage.getInstance().get(value);
			Set<Item> eqItems = new HashSet<>(user.getEqipment().values());
			return eqItems.contains(i);
		}
	},
	/**
	 * 術者がvalueのアイテムを保有しているか検査します。
	 */
	HAS_ITEM {
		@Override
		public boolean canDoThis(String value, Status user) {
			return user.getItemBag().contains(value);
		}
	},
	/**
	 * 術者がvalueのスロットにアイテムを装備していないことを確認します。
	 */
	NO_EQ_ITEM {
		@Override
		public boolean canDoThis(String value, Status user) {
			ItemEqipmentSlot slot = ItemEqipmentSlotStorage.getInstance().get(value);
			return !user.getEqipment().containsKey(slot);
		}
	},
	/**
	 * 術者がvalueのスロットに何らかのアイテムを装備していることを確認します。
	 */
	EQ_ANY_ITEM {
		@Override
		public boolean canDoThis(String value, Status user) {
			ItemEqipmentSlot slot = ItemEqipmentSlotStorage.getInstance().get(value);
			return user.getEqipment().containsKey(slot);
		}
	},
	/**
	 * 術者がvalueの属性の装備をしていることを確認します。
	 */
	EQ_WEAPON_TYPE {
		@Override
		public boolean canDoThis(String value, Status user) {
			Collection<Item> eq = user.getEqipment().values();
			for (Item i : eq) {
				if (i == null) {
					continue;
				}
				if (i.getWeaponMagicType() == null) {
					continue;
				}
				if (i.getWeaponMagicType().equals(WeaponMagicTypeStorage.getInstance().get(value))) {
					return true;
				}
			}
			return false;
		}
	},
	HAS_BOOK {
		@Override
		public boolean canDoThis(String value, Status user) {
			return user.getBookBag().contains(BookStorage.getInstance().get(value));
		}

	};

	public abstract boolean canDoThis(String value, Status user);

}
