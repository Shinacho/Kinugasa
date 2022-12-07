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

import java.util.HashSet;
import java.util.Set;
import kinugasa.resource.*;

/**
 * アイテムの名称と、効果を保持するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:58:36<br>
 * @author Dra211<br>
 */
public class Item extends CmdAction implements Nameable, Cloneable {

	private StatusValueSet eqStatus;
	private AttributeValueSet eqAttr;
	private ItemEqipmentSlot eqipmentSlot;
	private WeaponMagicType weaponMagicType;
	private Set<StatusKey> damageCalcStatusKey = new HashSet<>();

	public Item(String name, String desc) {
		super(ActionType.ITEM_USE, name, desc);
	}

	public Item setEqStatus(StatusValueSet eqStatus) {
		this.eqStatus = eqStatus;
		return this;
	}

	public Set<StatusKey> getDamageCalcStatusKey() {
		return damageCalcStatusKey;
	}

	public Item setDamageCalcStatusKey(Set<StatusKey> damageCalcStatusKey) {
		this.damageCalcStatusKey = damageCalcStatusKey;
		return this;
	}
	

	public Item setEqipmentSlot(ItemEqipmentSlot eqipmentSlot) {
		this.eqipmentSlot = eqipmentSlot;
		return this;
	}

	public AttributeValueSet getEqAttr() {
		return eqAttr;
	}

	public Item setEqAttr(AttributeValueSet eqAttr) {
		this.eqAttr = eqAttr;
		return this;
	}

	public Item setWeaponMagicType(WeaponMagicType weaponMagicType) {
		this.weaponMagicType = weaponMagicType;
		return this;
	}

	public WeaponMagicType getWeaponMagicType() {
		return weaponMagicType;
	}

	public boolean canEqip() {
		return eqipmentSlot != null;
	}

	public StatusValueSet getEqStatus() {
		return eqStatus;
	}

	public ItemEqipmentSlot getEqipmentSlot() {
		return eqipmentSlot;
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
