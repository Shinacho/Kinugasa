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
import kinugasa.resource.KImage;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_6:48:46<br>
 * @author Shinacho<br>
 */
public class MapChip implements Nameable {

	private String name;
	private MapChipAttribute attr;
	private KImage image;

	public MapChip(String name, MapChipAttribute attr, KImage image) {
		this.name = name;
		this.attr = attr;
		this.image = image;
	}

	public MapChipAttribute getAttr() {
		return attr;
	}

	public KImage getImage() {
		return image;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "MapChip{" + "name=" + name + ", attr=" + attr + '}';
	}

}
