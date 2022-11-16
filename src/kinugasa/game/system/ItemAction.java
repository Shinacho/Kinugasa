/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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

import java.util.List;
import kinugasa.resource.*;
import kinugasa.util.*;

/**
 * アイテムを使った時の効果を定義するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:58:23<br>
 * @author Dra211<br>
 */
public class ItemAction implements Nameable {

	private String name;
	private String desc;
	private ItemActionTargetType targetType;
	private ItemActionTargetStatusType statusType;
	private String tgtName;
	private ItemValueCalcType calcType;
	private float value;
	private float spread;
	private float p;

	public ItemAction(String name, String desc, ItemActionTargetType targetType, ItemActionTargetStatusType statusType, float p) {
		this(name, desc, targetType, statusType, null, null, 0, 0, p);
	}

	public ItemAction(String name, String desc, ItemActionTargetType targetType, ItemActionTargetStatusType statusType, String tgtName, ItemValueCalcType calcType, float value, float spread, float p) {
		this.name = name;
		this.desc = desc;
		this.targetType = targetType;
		this.statusType = statusType;
		this.tgtName = tgtName;
		this.calcType = calcType;
		this.value = value;
		this.spread = spread;
		this.p = p;
	}

	@Override
	public String getName() {
		return name;
	}

	public float getBaseValue() {
		return value;
	}

	public float getValue() {
		if (spread <= 0) {
			return (int) value;
		}
		return Random.randomAbsInt((int) (value - spread / 2), (int) (value + spread));
	}

	public String getDesc() {
		return desc;
	}

	public ItemActionTargetType getTargetType() {
		return targetType;
	}

	public ItemActionTargetStatusType getStatusType() {
		return statusType;
	}

	public String getTgtName() {
		return tgtName;
	}

	public ItemValueCalcType getCalcType() {
		return calcType;
	}

	public float getSpread() {
		return spread;
	}

	public float getP() {
		return p;
	}

	public void exec(ItemBag bag, Item i, GameSystem gs) {
		if (p != 1) {
			if (!Random.percent(p)) {
				return;
			}
		}
		if (statusType == ItemActionTargetStatusType.DROP_THIS_ITEM) {
			bag.drop(i);
			return;
		}

	}

}
