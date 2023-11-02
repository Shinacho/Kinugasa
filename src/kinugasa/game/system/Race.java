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

import java.util.Arrays;
import java.util.List;
import kinugasa.game.I18N;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_19:23:46<br>
 * @author Shinacho<br>
 */
public enum Race {
	人間(12, 8, Arrays.asList(EqipSlot.values())),
	ベルマ人(8, 14, Arrays.asList(EqipSlot.values())),
	やどかり(3, 0, List.of(EqipSlot.頭)),
	魔法生物(3, 0, List.of(EqipSlot.頭, EqipSlot.胴体, EqipSlot.装飾品)),
	野生生物(3, 0, List.of(EqipSlot.頭, EqipSlot.胴体, EqipSlot.足, EqipSlot.装飾品)),;
	private int itemBagSize;
	private int bookBagSize;
	private List<EqipSlot> slots;

	private Race(int itemBagSize, int bookBagSize, List<EqipSlot> slots) {
		this.itemBagSize = itemBagSize;
		this.bookBagSize = bookBagSize;
		this.slots = slots;
	}

	public List<EqipSlot> getEqipSlots() {
		return slots;
	}

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public int getItemBagSize() {
		return itemBagSize;
	}

	public int getBookBagSize() {
		return bookBagSize;
	}

}
