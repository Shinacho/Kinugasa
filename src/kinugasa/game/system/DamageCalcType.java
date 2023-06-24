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
 * ダメージ計算に使用するタイプです。
 *
 * @vesion 1.0.0 - 2022/11/16_17:54:58<br>
 * @author Shinacho<br>
 */
public enum DamageCalcType {
	/**
	 * 最大値のvalue%のダメージ。
	 */
	PERCENT_OF_MAX,
	/**
	 * 現在値のvalue%のダメージ。
	 */
	PERCENT_OF_NOW,
	/**
	 * valueを直接加算します。
	 */
	DIRECT,
	/**
	 * ダメージ計算式を使用して計算します。
	 */
	USE_DAMAGE_CALC,
}
