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

import kinugasa.game.I18N;
import kinugasa.game.field4.GameSystemI18NKeys;

/**
 *
 * @vesion 1.0.0 - 2022/11/20_15:14:04<br>
 * @author Shinacho<br>
 */
public enum ActionType {
	ATTACK,
	MAGIC,
	ITEM,
	OTHER,;

	public String displayName() {
		switch (this) {
			case ATTACK:
				return I18N.get(GameSystemI18NKeys.攻撃);
			case ITEM:
				return I18N.get(GameSystemI18NKeys.道具);
			case MAGIC:
				return I18N.get(GameSystemI18NKeys.魔術);
			case OTHER:
				return I18N.get(GameSystemI18NKeys.行動);
			default:
				throw new AssertionError("undefined ActionType");
		}
	}

}
