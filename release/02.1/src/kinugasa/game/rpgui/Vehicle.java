/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
package kinugasa.game.rpgui;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;

/**
 * "移動手段"または"乗り物"クラスは、マップチップに対する移動の可否を判定します.
 * <br>
 * 移動手段は「移動可能なチップ属性」を持ちます。<br>
 * マップ上でキャラクタが移動しようとすると、次に乗るマップチップの持つ 属性と、現在の移動手段（VehicleStorage.currentVehicle）の検査が行われます。<br>
 * <br>
 * 移動手段は、その移動手段自体の移動速度を持ちます。<br>
 * マップのスクロール速度は、「マップ自体の移動速度×移動手段の移動速度」が使用されます。
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_21:43:17<br>
 * @author Dra0211<br>
 */
public class Vehicle implements Nameable, Serializable, Comparable<Vehicle> {

	private static final long serialVersionUID = -5624691892456676247L;
	/**
	 * 移動手段の一意的な名前です.
	 */
	private String name;
	/**
	 * この移動手段がマップをスクロールさせる速度です. 移動速度は「マップ自体の移動速度×移動手段の移動速度」が使用されます。
	 */
	private float speed;
	/**
	 * この移動手段が移動できる属性です.
	 */
	private Storage<ChipAttribute> attributeStorage;

	/**
	 * 新しい移動手段を作成します. 作成された移動手段は、自動的にVehicleStorageに追加されます。<br>
	 *
	 * @param name 移動手段の一意的な名前を指定します。<br>
	 * @param speed 移動手段の移動速度を指定します。1.0fを指定すると、 マップに設定された移動速度で移動します。<br>
	 * @param attributeSet この移動手段が移動できるチップ属性を送信します。<br>
	 * @throws DuplicateNameException この移動手段の名前がすでに使用されているときに 投げられます。<br>
	 */
	public Vehicle(String name, float speed, ChipAttribute... attributeSet)
			throws DuplicateNameException {
		this(name, speed, Arrays.asList(attributeSet));
	}

	/**
	 * 新しい移動手段を作成します. 作成された移動手段は、自動的にVehicleStorageに追加されます。<br>
	 *
	 * @param name 移動手段の一意的な名前を指定します。<br>
	 * @param speed 移動手段の移動速度を指定します。1.0fを指定すると、 マップに設定された移動速度で移動します。<br>
	 * @param attributeSet この移動手段が移動できるチップ属性を送信します。<br>
	 * @throws DuplicateNameException この移動手段の名前がすでに使用されているときに 投げられます。<br>
	 */
	public Vehicle(String name, float speed, Collection<ChipAttribute> attributeSet)
			throws DuplicateNameException {
		if (VehicleStorage.getInstance().contains(name)) {
			throw new DuplicateNameException("duplicate name : " + name);
		}
		this.name = name;
		this.speed = speed;
		this.attributeStorage = new Storage<ChipAttribute>();
		this.attributeStorage.addAll(attributeSet);
		addThis();
	}

	/**
	 * thisインスタンスをVehicleStorageに追加します.
	 */
	private void addThis() {
		VehicleStorage.getInstance().add(this);
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * 移動手段の移動速度を取得します.
	 *
	 * @return 移動速度を返します。<br>
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * 移動手段の移動速度を設定します.
	 *
	 * @param speed 移動速度を指定します。<Br>
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * この移動手段が持つ移動可能な属性が格納されたストレージを取得します.
	 *
	 * @return 移動可能な属性が格納されたストレージを返します。 "VOID"は追加されていません。<br>
	 */
	public Storage<ChipAttribute> getAttributeStorage() {
		return attributeStorage;
	}

	/**
	 * この移動手段が指定されたチップ属性に移動可能であるかを検査します. 次の手順で検査されます。<br>
	 * <ol>
	 * <li>CLOSEおよびVOIDの検査</li>
	 * <br>
	 * 送信されたチップ属性が、"VOID"の場合、必ずtrueを返します。<br>
	 * また、送信されたチップ属性が、"CLOSE"の場合、必ずfalseを返します。<br>
	 * <br>
	 * <li>移動可否判定</li>
	 * <br>
	 * この移動手段が持つ移動可能な属性が保管されたストレージに 送信された属性と同じ名前を持つインスタンスが存在する場合、trueを返します。<ber>
	 * <br>
	 * </ol>
	 *
	 * @param attribute キャラクタが次に乗るチップの属性を送信します。<br>
	 * @return この移動手段がattributeに移動できる場合はtrue、移動できない場合はfalseを返します。<br>
	 */
	public boolean stepOn(ChipAttribute attribute) {
		if (ChipAttributeStorage.getInstance().get("CLOSE").equals(attribute)) {
			return false;
		}
		if (ChipAttributeStorage.getInstance().get("VOID").equals(attribute)) {
			return true;
		}
		return this.attributeStorage.contains(attribute);
	}

	//すべて乗れるときにtrueを返す
	public boolean stepOn(List<ChipAttribute> attr) {
		for (int i = 0, size = attr.size(); i < size; i++) {
			if (!stepOn(attr.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Vehicle{" + "name=" + name + ", speed=" + speed + ", attributeStorage=" + attributeStorage + '}';
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Vehicle other = (Vehicle) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Vehicle o) {
		return this.name.compareTo(o.getName());
	}
}
