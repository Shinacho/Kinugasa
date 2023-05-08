/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
