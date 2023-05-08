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
 * @vesion 1.0.0 - 2022/12/11_21:45:01<br>
 * @author Shinacho<br>
 */
public enum AnimationMoveType {
	NONE(0),
	USER_TO_TGT_4(4),
	TGT_TO_USER_4(4),
	USER_TO_TGT_8(8),
	TGT_TO_USER_8(8),
	USER_TO_TGT_12(12),
	TGT_TO_USER_12(12),
	USER_TO_TGT_16(16),
	TGT_TO_USER_16(16),
	USER_TO_TGT_20(20),
	TGT_TO_USER_20(20),
	USER_TO_TGT_24(24),
	TGT_TO_USER_24(24),
	USER_TO_TGT_28(28),
	TGT_TO_USER_28(28),
	USER_TO_TGT_32(32),
	TGT_TO_USER_32(32),;
	private float speed;

	private AnimationMoveType(float speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}

}
