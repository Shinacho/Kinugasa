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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import kinugasa.resource.*;

/**
 * �A�C�e���̖��̂ƁA���ʂ�ێ�����N���X�ł��B
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
	private int value;//�x�[�X���l
	private boolean canSale = true;//����邩�ǂ���
	private int currentUpgrade = 0;
	private List<ItemUpgrade> upgradeMaterials = new ArrayList<>();//�����ɕK�v�Ȏ���
	private Map<Material, Integer> dissasseMaterials = new HashMap<>();//��̎��ɓ����鎑��
	private List<ItemEqipTerm> eqipTerm = new ArrayList<>();

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

	public Item setValue(int v) {
		this.value = v;
		return this;
	}

	//�}�e���A���Ƌ��z�͕ʓr���肷�邱��
	public boolean canUpgrade() {
		return currentUpgrade < upgradeMaterials.size();
	}

	public List<ItemUpgrade> getUpgradeMaterials() {
		return upgradeMaterials;
	}

	public int getCurrentUpgrade() {
		return currentUpgrade;
	}

	public List<ItemEqipTerm> getEqipTerm() {
		return eqipTerm;
	}

	//���ӁF�߂�l���A�b�v�O���[�h���ꂽ�A�C�e��
	public Item doUpgrade() {
		if (!canUpgrade()) {
			throw new GameSystemException("this item is cant be upgrade : " + this);
		}
		Collections.sort(upgradeMaterials);
		Item i = clone();
		i.getEqAttr().addAll(upgradeMaterials.get(currentUpgrade).getAddAttrin());
		i.getEqStatus().addAll(upgradeMaterials.get(currentUpgrade).getAddStatus());
		String name = i.getName();
		if (name.contains("+")) {
			name = name.substring(0, name.indexOf("+"));
		}
		name += "+" + (currentUpgrade + 1);
		i.setName(name);
		currentUpgrade++;
		return i;
	}

	public Item setCanSale(boolean f) {
		this.canSale = f;
		return this;
	}

	public Map<Material, Integer> getDissasseMaterials() {
		return dissasseMaterials;
	}

	public Item addUpgrade(ItemUpgrade u) {
		upgradeMaterials.add(u);
		Collections.sort(upgradeMaterials);
		return this;
	}

	public Item setEqipTerm(List<ItemEqipTerm> t) {
		this.eqipTerm.addAll(t);
		return this;
	}

	public void setUpgradeMaterials(List<ItemUpgrade> upgradeMaterials) {
		this.upgradeMaterials = upgradeMaterials;
		Collections.sort(upgradeMaterials);
	}

	public void setDissasseMaterials(Map<Material, Integer> dissasseMaterials) {
		this.dissasseMaterials = dissasseMaterials;
	}

	@Override
	public String getName() {
		return super.getName();
	}

	public int getValue() {
		return value;
	}

	public boolean canSale() {
		return canSale;
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

	public boolean canEqip(Status s) {
		if (eqipmentSlot == null) {
			return false;
		}
		return eqipTerm.stream().allMatch(p -> p.canEqip(s, this));
	}

	public StatusValueSet getEqStatus() {
		return eqStatus;
	}

	public ItemEqipmentSlot getEqipmentSlot() {
		return eqipmentSlot;
	}

	public boolean isEqipItem() {
		return eqipmentSlot != null;
	}

	@Override
	public Item clone() {
		try {
			Item i = (Item) super.clone();
			if (eqAttr != null) {
				i.eqAttr = eqAttr.clone();
			}
			if (eqStatus != null) {
				i.eqStatus = eqStatus.clone();
			}
			return i;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
