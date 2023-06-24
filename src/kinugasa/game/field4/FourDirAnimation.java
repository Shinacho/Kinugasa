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

import kinugasa.graphics.Animation;
import kinugasa.object.FourDirection;

/**
 *
 * @vesion 1.0.0 - 2022/11/10_21:24:35<br>
 * @author Shinacho<br>
 */
public class FourDirAnimation {

	private Animation south, west, east, north;

	public FourDirAnimation(Animation south, Animation west, Animation east, Animation north) {
		this.south = south;
		this.west = west;
		this.east = east;
		this.north = north;
	}

	public Animation getEast() {
		return east;
	}

	public Animation getNorth() {
		return north;
	}

	public Animation getSouth() {
		return south;
	}

	public Animation getWest() {
		return west;
	}

	public Animation get(FourDirection d) {
		switch (d) {
			case EAST:
				return east;
			case WEST:
				return west;
			case NORTH:
				return north;
			case SOUTH:
				return south;
		}
		throw new InternalError(d + " is not found");
	}

}
