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
 * @vesion 1.0.0 - 2023/10/15_18:36:21<br>
 * @author Shinacho<br>
 */
public enum ItemEqipTerm {
	ステータス_装備属性一致_0("男性のみ装備できる") {
		@Override
		public boolean canEqip(Actor a) {
			return a.getStatus().getEffectedStatus().get(StatusKey.装備属性).getValue() == 0f;
		}
	},
	ステータス_装備属性一致_1("女性のみ装備できる") {
		@Override
		public boolean canEqip(Actor a) {
			return a.getStatus().getEffectedStatus().get(StatusKey.装備属性).getValue() == 1f;
		}
	},
	ステータス_魔術使用可否_使用可能("魔法が使用できる者のみ装備できる") {
		@Override
		public boolean canEqip(Actor a) {
			return a.getStatus().getEffectedStatus().get(StatusKey.魔術使用可否).getValue() == StatusKey.魔術使用可否＿使用可能;
		}

	};
	private String descI18NKey;

	private ItemEqipTerm(String descI18NKey) {
		this.descI18NKey = descI18NKey;
	}

	public String i18N() {
		return I18N.get(toString());
	}

	public String descI18N() {
		return I18N.get(descI18NKey);
	}

	public abstract boolean canEqip(Actor a);
}
