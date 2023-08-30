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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import kinugasa.resource.*;
import kinugasa.resource.db.DBRecord;

/**
 * アイテムの名称と、効果を保持するクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_11:58:36<br>
 * @author Shinacho<br>
 */
@DBRecord
public class Item extends Action implements Nameable, Cloneable {

	private StatusValueSet eqStatus;
	private AttributeValueSet eqAttr;
	private ItemEqipmentSlot eqipmentSlot;
	private WeaponType weaponMagicType;
	private int value;//ベース価値
	private boolean canSale = true;//売れるかどうか
	private int currentUpgrade = 0;
	private List<ItemUpgrade> upgradeMaterials = new ArrayList<>();//強化に必要な資源
	private Map<Material, Integer> dissasseMaterials = new HashMap<>();//解体時に得られる資源
	private List<ItemEqipTerm> eqipTerm = new ArrayList<>();

	public Item(String id, String name, String desc) {
		super(ActionType.ITEM, id, name, desc);
	}

	public Item setEqStatus(StatusValueSet eqStatus) {
		this.eqStatus = eqStatus;
		return this;
	}

	public Item setValue(int v) {
		this.value = v;
		return this;
	}

	@Override
	public void setName(String name) {
		super.setName(name); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
	}

	//マテリアルと金額は別途判定すること
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

	//注意：戻り値がアップグレードされたアイテム
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

	public Map<Material, Integer> getDisasseMaterials() {
		return dissasseMaterials;
	}

	public boolean canDisasse() {
		return !dissasseMaterials.isEmpty();
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

	public void setDisasseMaterials(Map<Material, Integer> dissasseMaterials) {
		this.dissasseMaterials = dissasseMaterials;
	}

	@Override
	public String getName() {
		return super.getName();
	}

	public String getID() {
		return getName();
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

	public Item setWeaponMagicType(WeaponType weaponMagicType) {
		this.weaponMagicType = weaponMagicType;
		return this;
	}

	public WeaponType getWeaponMagicType() {
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
		Item i = (Item) super.clone();
		if (eqAttr != null) {
			i.eqAttr = eqAttr.clone();
		}
		if (eqStatus != null) {
			i.eqStatus = eqStatus.clone();
		}
		return i;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 23 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Item other = (Item) obj;
		return Objects.equals(this.id, other.id);
	}

}
