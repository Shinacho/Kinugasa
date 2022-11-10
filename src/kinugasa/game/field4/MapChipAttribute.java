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
package kinugasa.game.field4;

import kinugasa.resource.Nameable;

/**
 * このクラスは、マップチップ1枚が持つ属性を定義します。属性には、「平地」「草原」「山」等があります。 チップの属性は、歩数エンカウントの基本値を持ちます。
 *
 * @vesion 1.0.0 - 2022/11/08_16:04:58<br>
 * @author Dra211<br>
 */
public class MapChipAttribute implements Nameable {

	private String name;
	private int encountBaseValue;

	/**
	 * チップを定義します.
	 *
	 * @param name このチップの属性名.
	 * @param encountBaseValue 歩数エンカウントの基本値.
	 */
	public MapChipAttribute(String name, int encountBaseValue) {
		this.name = name;
		this.encountBaseValue = encountBaseValue;
	}

	/**
	 * エンカウントしないチップを定義します.
	 *
	 * @param name このチップの属性名.
	 */
	public MapChipAttribute(String name) {
		this(name, 0);
	}

	@Override
	public String getName() {
		return name;
	}

	public int getEncountBaseValue() {
		return encountBaseValue;
	}

	@Override
	public String toString() {
		return "MapChipAttribute{" + "name=" + name + ", encountBaseValue=" + encountBaseValue + '}';
	}

}
