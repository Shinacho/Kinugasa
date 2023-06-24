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
package kinugasa.game.field4;

/**
 *
 * @vesion 1.0.0 - 2022/11/10_16:53:30<br>
 * @author Shinacho<br>
 */
public enum FieldMapCameraMode {

	/**
	 * キャラ移動に合わせる（キャラを常に中心に表示する）。
	 */
	FOLLOW_TO_CENTER,
	/**
	 * 追従しない。
	 */
	FREE,
//	/**
//	 * 追従するが、フィールドマップデータの端の場合、停止する。
//	 */
//	STOP_IN_EDGE,;
}
