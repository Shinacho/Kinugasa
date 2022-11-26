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
import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.resource.*;

/**
 * アイテムの名称と、効果を保持するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:58:36<br>
 * @author Dra211<br>
 */
public class Item implements Nameable, Cloneable {

	private String name;
	private String desc;
	private List<ItemAction> fieldAction;
	private List<ItemAction> battleAction;
	private AttributeValueSet eqAttr;
	private StatusValueSet eqStatus;
	private ItemEqipmentSlot eqipmentSlot;
	private WeaponMagicType weaponMagicType;
	private int area;

	public Item(String name, String desc, List<ItemAction> fieldAction, List<ItemAction> battleAction, AttributeValueSet eqAttr, StatusValueSet eqStatus, ItemEqipmentSlot eqipmentSlot, WeaponMagicType weaponMagicType, int area) {
		this.name = name;
		this.desc = desc;
		this.fieldAction = fieldAction;
		this.battleAction = battleAction;
		this.eqAttr = eqAttr;
		this.eqStatus = eqStatus;
		this.eqipmentSlot = eqipmentSlot;
		this.weaponMagicType = weaponMagicType;
		this.area = area;
	}

	public int getArea() {
		return area;
	}

	public List<ItemAction> getFieldAction() {
		return fieldAction;
	}

	public List<ItemAction> getBattleAction() {
		return battleAction;
	}

	public WeaponMagicType getWeaponMagicType() {
		return weaponMagicType;
	}

	public boolean canEqip() {
		return eqipmentSlot != null;
	}

	public boolean canUse(GameMode mode) {
		switch (mode) {
			case BATTLE:
				return battleAction != null && !battleAction.isEmpty();
			case FIELD:
				return fieldAction != null && !fieldAction.isEmpty();
			default:
				throw new AssertionError();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public AttributeValueSet getEqAttr() {
		return eqAttr;
	}

	public StatusValueSet getEqStatus() {
		return eqStatus;
	}

	public ItemEqipmentSlot getEqipmentSlot() {
		return eqipmentSlot;
	}

	@Override
	public String toString() {
		return "Item{" + "name=" + name + ", eqipmentSlot=" + eqipmentSlot + '}';
	}

	@Override
	public Item clone() {
		try {
			Item i = (Item) super.clone();
			return i;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
