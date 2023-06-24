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
 * エフェクトの設定対象を指定する列挙です。
 *
 * @vesion 1.0.0 - 2022/11/15_12:08:31<br>
 * @author Shinacho<br>
 */
public enum EffectTargetType {
	/**
	 * ステータスを改変します。
	 */
	STATUS,
	/**
	 * ATTR_INを改変します（バフ・デバフ）。
	 */
	ATTR,
	/**
	 * コンディションを追加します。
	 */
	ADD_CONDITION,
	/**
	 * 行動を停止します。
	 */
	STOP,
	/**
	 * 行動をランダム化します。
	 */
	CONFU,;

}
