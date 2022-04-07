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
import kinugasa.resource.Nameable;

/**
 * マップチップの属性を表すクラスです.
 * <br>
 * チップ属性とは、”平地”や”海”などの地形の分類です。 マップチップは、1つの属性を持ちます。<br>
 * <br>
 * 属性の名前は一意的である必要があります。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_21:49:21<br>
 * @author Dra0211<br>
 */
public class ChipAttribute implements Nameable, Serializable, Comparable<ChipAttribute> {

	private static final long serialVersionUID = 3944316626063761776L;
	/**
	 * この属性の名前です.
	 */
	private String name;

	/**
	 * 新しい属性を作成します.
	 *
	 * @param name この属性のユニークな名前を指定します。<br>
	 */
	public ChipAttribute(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
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
		final ChipAttribute other = (ChipAttribute) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ChipAttribute{" + "name=" + name + '}';
	}

	@Override
	public int compareTo(ChipAttribute o) {
		return this.name.compareTo(o.getName());
	}

}
