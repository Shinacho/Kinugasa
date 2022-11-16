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

/**
 * アイテムの名称と、効果を保持するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:58:36<br>
 * @author Dra211<br>
 */
public class Item implements Nameable {

	private String name;
	private String desc;
	private List<ItemAction> action;
	private AttributeValueSet eqAttr;
	private StatusValueSet eqStatus;
	private ItemEqipmentSlot eqipmentSlot;
	private WeaponMagicType weaponMagicType;

	public Item(String name, String desc, List<ItemAction> action, AttributeValueSet eqAttr, StatusValueSet eqStatus, ItemEqipmentSlot eqipmentSlot, WeaponMagicType weaponMagicType) {
		this.name = name;
		this.desc = desc;
		this.action = action;
		this.eqAttr = eqAttr;
		this.eqStatus = eqStatus;
		this.eqipmentSlot = eqipmentSlot;
		this.weaponMagicType = weaponMagicType;
	}

	public WeaponMagicType getWeaponMagicType() {
		return weaponMagicType;
	}

	public boolean canEqip() {
		return eqipmentSlot != null;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public List<ItemAction> getAction() {
		return action;
	}

	public void use(ItemBag userBag, GameSystem gs) {
		action.forEach(a -> a.exec(userBag, this, gs));
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

}
