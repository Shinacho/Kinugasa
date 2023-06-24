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

import kinugasa.resource.Nameable;

/**
 * このクラスは、マップチップ1枚が持つ属性を定義します。属性には、「平地」「草原」「山」等があります。 チップの属性は、歩数エンカウントの基本値を持ちます。
 *
 * @vesion 1.0.0 - 2022/11/08_16:04:58<br>
 * @author Shinacho<br>
 */
public class MapChipAttribute implements Nameable {

	private String name;
	private int encountBaseValue;

	/**
	 * チップを定義します.
	 *
	 * @param name このチップの属性名.
	 * @param encountBaseValue 歩数エンカウントの基本値.
	 */
	public MapChipAttribute(String name, int encountBaseValue) {
		this.name = name;
		this.encountBaseValue = encountBaseValue;
	}

	/**
	 * エンカウントしないチップを定義します.
	 *
	 * @param name このチップの属性名.
	 */
	public MapChipAttribute(String name) {
		this(name, 0);
	}

	@Override
	public String getName() {
		return name;
	}

	public int getEncountBaseValue() {
		return encountBaseValue;
	}

	@Override
	public String toString() {
		return "MapChipAttribute{" + "name=" + name + ", encountBaseValue=" + encountBaseValue + '}';
	}

}
