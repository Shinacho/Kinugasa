/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the eqipTerms of the GNU General Public License as published by
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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.I18N;
import kinugasa.game.NewInstance;
import kinugasa.game.NotNewInstance;
import kinugasa.game.Nullable;
import kinugasa.object.AnimationSprite;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_11:41:27<br>
 * @author Shinacho<br>
 */
public class Item extends Action implements Cloneable {

	private boolean canSale;
	private int price;
	private EqipSlot slot;
	private StatusValueSet status;
	private AttributeValueSet attrIn;
	private AttributeValueSet attrOut;
	private ConditionRegist conditionRegist;
	private int atkCount;
	private Map<Material, Integer> material;
	private WeaponType weaponType;
	private EnumSet<ItemEqipTerm> eqipTerms = EnumSet.noneOf(ItemEqipTerm.class);
	private ItemStyle style;
	private ItemEnchant enchant;
	private StatusKey dcs = null;
	private int currentUpgradeNum = 0;

	public static Item of(String id) {
		return ActionStorage.getInstance().itemOf(id);
	}

	Item(String id, String visibleName) {
		super(id, visibleName, ActionType.アイテム);
	}

	Item(String id, String visibleName, ItemStyle style) {
		super(id, visibleName, ActionType.アイテム);
		this.style = style;
	}

	Item(String id, String visibleName, ItemStyle style, ItemEnchant enchant) {
		super(id, visibleName, ActionType.アイテム);
		this.style = style;
		this.enchant = enchant;
	}

	@Override
	public Item pack() throws GameSystemException {
		if (slot == null && weaponType != null) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.スロットと武器タイプの整合性がとれていません) + " : " + this);
		}
		if (weaponType == null && slot != null) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.スロットと武器タイプの整合性がとれていません) + " : " + this);
		}
		if (atkCount == 0 && (weaponType != null && slot != null)) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.武器ですが攻撃回数が０です) + " : " + this);
		}
		if (slot != null && style == null) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.装備品ですがスタイルが入っていません) + " : " + this);
		}
		if (weaponType != null && dcs == null) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.武器ですがDCSが入っていません) + " : " + this);
		}
		if (weaponType != null && getArea() == 0) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.武器ですがAREAが０です) + " : " + this);
		}
		if (status != null && !status.isEmpty()) {
			for (StatusValue v : getEffectedStatus()) {
				if (v.getValue() > 9999999) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.装備効果のステータスが大きすぎます) + " : " + this);
				}
				if (v.getMax() > 9999999) {
					throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.装備効果のステータス最大値が大きすぎます) + " : " + this);
				}
			}
		}
		if (canSale && price == 0) {
			throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.売れますが価格が０です) + " : " + this);
		}
		if (slot != null) {
			if ((status == null || status.isEmpty())
					&& (attrIn == null || attrIn.isEmpty())
					&& (attrOut == null || attrOut.isEmpty())
					&& (conditionRegist == null || conditionRegist.isEmpty())) {
				throw new GameSystemException(I18N.get(GameSystemI18NKeys.ErrorMsg.装備品ですが装備効果が入っていません) + " : " + this);
			}
		}

		super.pack();
		return this;
	}

	//getNameはスーパークラス。
	public boolean hasEnchant() {
		return enchant != null;
	}

	public boolean hasStyle() {
		return style != null;
	}

	@Override
	Item setBattle(boolean battle) {
		return (Item) super.setBattle(battle);
	}

	@Override
	Item setDesc(String desc) {
		return (Item) super.setDesc(desc);
	}

	@Override
	Item setField(boolean field) {
		return (Item) super.setField(field);
	}

	@Override
	Item setMainEvents(List<ActionEvent> mainEvents) {
		return (Item) super.setMainEvents(mainEvents);
	}

	@Override
	Item setTgtType(ターゲットモード tgtType) {
		return (Item) super.setTgtType(tgtType);
	}

	@Override
	Item setUserEvents(List<ActionEvent> userEvents) {
		return (Item) super.setUserEvents(userEvents);
	}

	@Override
	Item set死亡者ターゲティング(死亡者ターゲティング a) {
		return (Item) super.set死亡者ターゲティング(a);
	}

	Item setCanSale(boolean canSale) {
		this.canSale = canSale;
		return this;
	}

	Item setPrice(int p) {
		this.price = p;
		return this;
	}

	Item setSlot(EqipSlot slot) {
		this.slot = slot;
		return this;
	}

	Item setStatus(StatusValueSet status) {
		this.status = status;
		return this;
	}

	Item setAttrIn(AttributeValueSet attr) {
		this.attrIn = attr;
		return this;
	}

	Item setAttrOut(AttributeValueSet attr) {
		this.attrOut = attr;
		return this;
	}

	@Override
	Item setArea(int area) {
		super.setArea(area);
		return this;
	}

	Item setMaterial(Map<Material, Integer> material) {
		this.material = material;
		return this;
	}

	Item setWeaponType(WeaponType WeaponType) {
		this.weaponType = WeaponType;
		return this;
	}

	Item setTerms(EnumSet<ItemEqipTerm> terms) {
		this.eqipTerms = terms;
		return this;
	}

	Item setStyle(ItemStyle style) {
		this.style = style;
		return this;
	}

	Item setAtkCount(int atkCount) {
		this.atkCount = atkCount;
		return this;
	}

	Item setConditionRegist(ConditionRegist conditionRegist) {
		this.conditionRegist = conditionRegist;
		return this;
	}

	Item setDcs(StatusKey dcs) {
		this.dcs = dcs;
		return this;
	}

	@Override
	public String getVisibleName() {
		String r = super.getVisibleName();
		if (style != null) {
			r = style.getVisibleName() + " " + r;
		}
		if (currentUpgradeNum != 0) {
			r += "+" + currentUpgradeNum;
		}
		if (hasEnchant()) {
			r += " [" + enchant.getVisibleName() + "]";
		}
		return r;
	}

	public boolean canUse() {
		return hasEvent();
	}

	public boolean canSale() {
		return canSale;
	}

	public int getEffectedValue() {
		int r = price;
		if (hasStyle()) {
			r = style.mulValue(r);
		}
		if (hasEnchant()) {
			int encValue = enchant.mulValue(r);
			int sa = encValue - r;
			float m = (style != null && style == ItemStyle.増幅の) ? 4f : 1f;
			sa *= m;
			r += sa;
			r += 100;
		}
		return r;
	}

	public int getEffectedATKCount() {
		int r = atkCount;
		if (hasStyle()) {
			r = style.mulAtkCount(r);
		}
		if (hasEnchant()) {
			int encValue = enchant.mulAtkCount(r);
			int sa = encValue - r;
			float m = (style != null && style == ItemStyle.増幅の) ? 4f : 1f;
			sa *= m;
			r += sa;
		}
		return r;
	}

	public int get残り強化回数() {
		return 99 - currentUpgradeNum;
	}

	@NewInstance
	public void doUpgrade() {
		if (!canUpgrade(GameSystem.getInstance().getMaterialBag())) {
			throw new GameSystemException("this item cant upgrade : " + this);
		}
		//マテリアル消費
		if (!GameSystem.isDebugMode()) {
			for (Map.Entry<Material, Integer> m : this.material.entrySet()) {
				GameSystem.getInstance().getMaterialBag().sub(m.getKey().getName(), m.getValue());
			}
		}

		//装備効果上昇
		float 上昇効果 = switch (currentUpgradeNum) {
			case 0 ->
				1.07f;
			case 98 ->
				2;
			default ->
				1.025f;
		};
		for (StatusValue sk : this.status) {
			if (sk.getKey().isPercent()) {
				float v = sk.getValue() * 上昇効果;
				sk.add(v);
			} else {
				switch (sk.getKey()) {
					case 体力:
					case 正気度:
					case 魔力: {
						float v = sk.getMax() * 上昇効果;
						sk.add(v);
						sk.addMax(v);
						break;
					}
					default: {
						float v = sk.getValue() * 上昇効果;
						sk.add(v);
						break;
					}
				}
			}
		}
		currentUpgradeNum++;
	}

	public boolean canUpgrade() {
		return get残り強化回数() != 0;
	}

	public boolean canUpgrade(MaterialBag mb) {
		if (GameSystem.isDebugMode()) {
			return true;
		}
		if (material == null || material.isEmpty()) {
			return false;
		}
		if (currentUpgradeNum == 99) {
			return false;
		}
		boolean ok = false;
		for (Map.Entry<Material, Integer> m : this.material.entrySet()) {
			ok |= (mb.getNum(m.getKey().getName()) > m.getValue());
		}
		return ok;
	}

	public void setCurrentUpgradeNum(int currentUpgradeNum) {
		this.currentUpgradeNum = currentUpgradeNum;
	}

	public int getCurrentUpgradeNum() {
		return currentUpgradeNum;
	}

	public boolean canDisasse() {
		return material != null && !material.isEmpty();
	}

	//アイテム破棄とマテリアルバッグへの追加は呼び出し側で
	public Map<Material, Integer> getEffectedMaterials() {
		Map<Material, Integer> map = new HashMap<>();
		for (Map.Entry<Material, Integer> m : this.material.entrySet()) {
			map.put(m.getKey(), m.getValue());
		}
		if (hasStyle()) {
			map = style.mulDissaseMaterial(map);
		}
		if (hasEnchant()) {
			Map<Material, Integer> map2 = new HashMap<>();
			for (Map.Entry<Material, Integer> m : enchant.mulDissaseMaterial(map).entrySet()) {
				map2.put(m.getKey(), m.getValue());
			}
			if (style != null && style == ItemStyle.増幅の) {
				for (Material m : map.keySet()) {
					int n = map2.get(m) - map.get(m);
					if (n == 0) {
						continue;
					}
					n *= 4;
					map2.put(m, n);
				}
			}
			//強化されている場合、強化回数分戻ってくる
			if (currentUpgradeNum != 0) {
				for (Map.Entry<Material, Integer> e : map2.entrySet()) {
					map2.put(e.getKey(), e.getValue() * currentUpgradeNum);
				}
			}
			map.clear();
			map.putAll(map2);
		}
		return map;
	}

	//このアイテムのエリアを返す
	//この時、このアイテムは必ず装備しているが武器とは限らない
	public int getEffectedArea() {
		int r = getArea();
		if (r == 0) {
			return 0;
		}
		if (hasStyle()) {
			r = style.mulArea(r);
		}
		if (hasEnchant()) {
			int rr = enchant.mulArea(r);
			if (style != null && style == ItemStyle.増幅の) {
				int sa = rr - r;
				sa *= 4;
				r += sa;
			} else {
				r = rr;
			}
		}
		return r;
	}

	public boolean isAddConditionP() {
		if (!hasEnchant()) {
			return false;
		}
		if (enchant.getCndPercent() == 0) {
			return false;
		}
		float n = style == ItemStyle.増幅の
				? 4f
				: style == ItemStyle.注入の
						? 3f
						: 1f;
		return Random.percent(enchant.getCndPercent() * n);
	}

	@Nullable
	public ConditionKey getAddCondition() {
		if (!hasEnchant()) {
			return null;
		}
		if (enchant.getCndPercent() == 0) {
			return null;
		}
		return enchant.getCndKey();
	}

	public int getCnditionTime() {
		if (!hasEnchant()) {
			return 0;
		}
		if (enchant.getCndPercent() == 0) {
			return 0;
		}
		int n = style == ItemStyle.増幅の ? 4 : 1;
		return enchant.getCndTime() * n;
	}

	public boolean hasSlot() {
		return slot != null;
	}

	public EqipSlot getSlot() {
		return slot;
	}

	public boolean canEqip(Actor a) {
		if (!hasSlot()) {
			return false;
		}
		return eqipTerms.stream().allMatch(p -> p.canEqip(a));
	}

	public boolean isCanSale() {
		return canSale;
	}

	@Deprecated
	@NotNewInstance
	public StatusValueSet getStatus() {
		return status;
	}

	@Deprecated
	@NotNewInstance
	public AttributeValueSet getAttrIn() {
		return attrIn;
	}

	@Deprecated
	@NotNewInstance
	public AttributeValueSet getAttrOut() {
		return attrOut;
	}

	@Deprecated
	@NotNewInstance
	public ConditionRegist getConditionRegist() {
		return conditionRegist;
	}

	@Deprecated
	public int getAtkCount() {
		return atkCount;
	}

	@Deprecated
	@NotNewInstance
	public Map<Material, Integer> getMaterial() {
		return material;
	}

	public boolean isWeapon() {
		return weaponType != null;
	}

	@Nullable
	public WeaponType getWeaponType() {
		return weaponType;
	}

	@Nullable
	public ItemStyle getStyle() {
		return style;
	}

	public boolean hasStatusValue() {
		return status != null && !status.isEmpty();
	}

	public boolean hasAttrIn() {
		return attrIn != null && !attrIn.isEmpty();
	}

	public boolean hasAttrOut() {
		return attrOut != null && !attrOut.isEmpty();
	}

	public boolean hasConditionRegist() {
		return conditionRegist != null && !conditionRegist.isEmpty();
	}

	//このアイテム単品の効果を返す。足し込みは呼び出し側で！
	@NewInstance
	public StatusValueSet getEffectedStatus() {
		StatusValueSet effectedItem = this.status == null ? new StatusValueSet() : this.status.clone();
		if (hasStyle()) {
			effectedItem = style.getStatusValue(effectedItem);
		}
		if (hasEnchant()) {
			StatusValueSet enc = enchant.getStatusValue(effectedItem);
			if (style != null && style == ItemStyle.増幅の) {
				for (StatusValue v : enc) {
					float n = v.getValue() - effectedItem.get(v.getKey()).getValue();
					n *= 4;
					v.setValue(n);
				}
			}
			effectedItem.clear();
			effectedItem.addAll(enc);
		}
		return effectedItem;
	}

	@NewInstance
	public AttributeValueSet getEffectedAttrIn() {
		AttributeValueSet v = this.attrIn == null ? new AttributeValueSet() : this.attrIn.clone();
		if (hasStyle()) {
			v = style.getAttrIn(v);
		}
		if (hasEnchant()) {
			AttributeValueSet enc = enchant.getAttrIn(v);
			if (style != null && style == ItemStyle.増幅の) {
				for (AttributeValue vv : enc) {
					float n = vv.getValue() - v.get(vv.getKey()).getValue();
					n *= 4;
					vv.setValue(n);
				}
			}
			v.clear();
			v.addAll(enc);
		}
		return v;
	}

	@NewInstance
	public AttributeValueSet getEffectedAttrOut() {
		AttributeValueSet v = this.attrOut == null ? new AttributeValueSet() : this.attrOut.clone();
		if (hasStyle()) {
			v = style.getAttrOut(v);
		}
		if (hasEnchant()) {
			AttributeValueSet enc = enchant.getAttrOut(v);
			if (style != null && style == ItemStyle.増幅の) {
				for (AttributeValue vv : enc) {
					float n = vv.getValue() - v.get(vv.getKey()).getValue();
					n *= 4;
					vv.setValue(n);
				}
			}
			v.clear();
			v.addAll(enc);
		}
		return v;
	}

	@NewInstance
	public ConditionRegist getEffectedConditionRegist() {
		ConditionRegist c = this.conditionRegist == null ? new ConditionRegist() : this.conditionRegist.clone();
		if (hasStyle()) {
			c = style.getCndRegist(c);
		}
		if (hasEnchant()) {
			ConditionRegist cc = enchant.getCndRegist(c);
			if (style != null && style == ItemStyle.増幅の) {
				for (Map.Entry<ConditionKey, Float> v : cc.entrySet()) {
					float n = v.getValue() - c.get(v.getKey());
					n *= 4;
					cc.put(v.getKey(), n);
				}
			}
			c.clear();
			c.putAll(cc);
		}
		return c;
	}

	public void eqip(ConditionFlags c) {
		if (hasStyle()) {
			style.startEffect(c, 1f);
		}
		if (hasEnchant()) {
			enchant.startEffect(c, (style == ItemStyle.増幅の) ? 4f : 1f);
		}
	}

	public void unEqip(ConditionFlags c) {
		if (hasStyle()) {
			style.endEffect(c, 1f);
		}
		if (hasEnchant()) {
			enchant.endEffect(c, (style == ItemStyle.増幅の) ? 4f : 1f);
		}
	}

	public StatusKey getDcs() {
		return dcs;
	}

	public int getPrice() {
		return price;
	}

	public void setEnchant(ItemEnchant enchant) {
		this.enchant = enchant;
	}

	@Nullable
	public ItemEnchant getEnchant() {
		return enchant;
	}

	public boolean canEnchant() {
		return getSlot() != null;
	}

	@Override
	public String toString() {
		return "Item{" + "id=" + getId() + " / " + getVisibleName() + '}';
	}

	public int getEffectedExp(int f) {
		int res = f;
		if (style != null) {
			res = style.mulExp(res);
		}
		if (enchant != null) {
			res = enchant.mulExp(res);
		}
		return (int) res;
	}

	@NotNewInstance
	public EnumSet<ItemEqipTerm> getEqipTerms() {
		return eqipTerms;
	}

	@Override
	protected Item clone() {
		return (Item) super.clone();
	}
}
