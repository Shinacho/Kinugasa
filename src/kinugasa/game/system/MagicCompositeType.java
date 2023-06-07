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
package kinugasa.game.system;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_12:00:08<br>
 * @author Shinacho<br>
 */
public enum MagicCompositeType {
	SET_ATTR(4),
	HEAL_ATTRIN(6),
	ADD_ATTRIN(6),
	DAMAGE_STATUS_DIRECT(8),
	DAMAGE_STATUS_CALC(8),
	HEAL_STATUS(6),
	ADD_CONDITION(6),
	REMOVE_CONDITION(6),
	ADD_CONDITION_TIME(6),
	ADD_AREA(4),
	ADD_SPELL_TIME(8),
	P(2),
	TO_ALL(20),
	TO_ONE(2),
	TO_TEAM(10),
	ENEMY(6),
	FRIEND(6),
	CAST_COST(0),;

	private int point;

	private MagicCompositeType(int point) {
		this.point = point;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

}
