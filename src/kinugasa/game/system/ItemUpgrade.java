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

import java.util.HashMap;
import java.util.Map;

/**
 * アイテムアップグレードクラスは、アイテム1回の強化に使うマテリアルと値段を定義します。
 *
 * @vesion 1.0.0 - 2022/12/26_17:37:30<br>
 * @author Shinacho<br>
 */
public class ItemUpgrade implements Comparable<ItemUpgrade> {

	private int order;
	private int value;
	private Map<Material, Integer> materials = new HashMap<>();
	private Map<StatusKey, Float> addStatus = new HashMap<>();
	private Map<AttributeKey, Float> addAttrin = new HashMap<>();

	public ItemUpgrade(int order, int value) {
		this.order = order;
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public int getOrder() {
		return order;
	}

	public Map<Material, Integer> getMaterials() {
		return materials;
	}

	public void setAddAttrin(Map<AttributeKey, Float> addAttrin) {
		this.addAttrin = addAttrin;
	}

	public void setAddStatus(Map<StatusKey, Float> addStatus) {
		this.addStatus = addStatus;
	}

	public void addStatus(StatusKey k, float v) {
		this.addStatus.put(k, v);

	}

	public void addAttrIn(AttributeKey k, float v) {
		this.addAttrin.put(k, v);
	}

	public Map<AttributeKey, Float> getAddAttrin() {
		return addAttrin;
	}

	public Map<StatusKey, Float> getAddStatus() {
		return addStatus;
	}

	public void setMaterials(Map<Material, Integer> materials) {
		this.materials = materials;
	}

	public void addMaterial(Material m, int n) {
		if (materials.containsKey(m)) {
			materials.put(m, materials.get(m) + n);
		} else {
			materials.put(m, n);
		}
	}

	public void addMaterial(Material m) {
		addMaterial(m, 1);
	}

	@Override
	public int compareTo(ItemUpgrade o) {
		return order - o.order;
	}

	@Override
	public String toString() {
		return "ItemUpgradeMaterials{" + "order=" + order + ", value=" + value + ", materials=" + materials + '}';
	}

}
