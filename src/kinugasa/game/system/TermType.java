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



/**
 * 条件タイプは、アクションの発動条件を定義する列挙です。
 * @vesion 1.0.0 - 2022/12/01_21:52:25<br>
 * @author Shinacho<br>
 */
public enum TermType {
	EQ_WEAPON_TYPE,
	EQ_ITEM,
	NO_EQ_WEAPON_TYPE,
	NO_EQ_ITEM,
	EQ_ANY_ITEM,
	NO_EQ_ANY_ITEM,
	HAS_ITEM,
	HAS_ITEM_NAME_CONTAINS,
	HAS_BOOK,
	HAS_CONDITION,
	NO_CONDITION,
	NONE,
	EQ_ITEM_NAME_CONTAINS,
	EQ_ITEM_DESC_CONTAINS,
	STATUS_IS,
	;
}
