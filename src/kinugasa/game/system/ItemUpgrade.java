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

import java.util.HashMap;
import java.util.Map;

/**
 * �A�C�e���A�b�v�O���[�h�N���X�́A�A�C�e��1��̋����Ɏg���}�e���A���ƒl�i���`���܂��B
 *
 * @vesion 1.0.0 - 2022/12/26_17:37:30<br>
 * @author Dra211<br>
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
