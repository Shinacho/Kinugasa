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
 *
 * @vesion 1.0.0 - 2022/11/16_10:53:21<br>
 * @author Shinacho<br>
 */
public enum ParameterType {
	/**
	 * ステータスの変更を行うアクションタイプです。
	 */
	STATUS(3),
	/**
	 * 属性耐性の変更を行うアクションタイプです。
	 */
	ATTR_IN(4),
	/**
	 * valueの名前のアイテムを破棄するタイプです。
	 */
	ITEM_LOST(1),
	/**
	 * valueの名前のアイテムを追加するタイプです。
	 */
	ITEM_ADD(2),
	/**
	 * 何も行わないアクションタイプです。
	 */
	NONE(999),
	/**
	 * 状態異常を付与するアクションです。
	 */
	ADD_CONDITION(5),
	/**
	 * 状態異常を削除するアクションです。
	 */
	REMOVE_CONDITION(6),
	;
	private int value;

	private ParameterType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
