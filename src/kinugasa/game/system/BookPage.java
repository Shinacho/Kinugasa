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

import java.util.Objects;
import kinugasa.game.I18N;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;

/**
 * ブックページは、本を構成するページで、魔法効果か合成効果のどちらかを持っています。
 *
 * @vesion 1.0.0 - 2022/12/25_10:50:45<br>
 * @author Dra211<br>
 */
public class BookPage implements Nameable {

	private MagicCompositeType compositeType;
	private String name;
	private String tgtName;
	private float value;

	public BookPage(MagicCompositeType compositeType, String name, String tgtName, float value) {
		this.compositeType = compositeType;
		this.name = name;
		this.tgtName = tgtName;
		this.value = value;
	}

	public MagicCompositeType getCompositeType() {
		return compositeType;
	}

	public String getTgtName() {
		return tgtName;
	}

	public float getValue() {
		return value;
	}

	@Override
	public String getName() {
		return getDesc();
	}

	public String getDesc() {
		//中でtoStringやgetNameを使わないように注意！！！！！！！
		String s = name;
		boolean percent = false;
		switch (compositeType) {
			case SET_ATTR:
				percent = true;
				s += "(" + AttributeKeyStorage.getInstance().get(tgtName).getDesc();
				break;
			case HEAL_ATTRIN:
			case ADD_ATTRIN:
				percent = true;
				s += "(" + AttributeKeyStorage.getInstance().get(tgtName).getDesc();
				break;
			case DAMAGE_STATUS_DIRECT:
			case DAMAGE_STATUS_CALC:
			case HEAL_STATUS:
			case CAST_COST:
				percent = StatusKeyStorage.getInstance().get(tgtName).getMax() == 1f;
				s += "(" + StatusKeyStorage.getInstance().get(tgtName).getDesc();
				break;
			case ADD_CONDITION:
			case REMOVE_CONDITION:
			case ADD_CONDITION_TIME:
				if (ConditionValueStorage.getInstance().contains(tgtName)) {
					s += "(" + ConditionValueStorage.getInstance().get(tgtName).getKey().getDesc();
				}
				if (FieldConditionValueStorage.getInstance().contains(tgtName)) {
					s += "(" + FieldConditionValueStorage.getInstance().get(tgtName).getKey().getDesc();
				}
				break;
			case ADD_AREA:
			case ADD_SPELL_TIME:
			case TO_ALL:
			case TO_ONE:
			case TO_TEAM:
			case TO_FIELD:
			case ENEMY:
			case FRIEND:
				break;
			case P:
				percent = true;
				break;
		}
		if (value != 0f) {
			if (!s.contains("(")) {
				if (percent) {
					if (value < 0) {
						s += "(" + (value * 100) + "%";
					} else {
						s += "(+" + (value * 100) + "%";
					}
				} else {
					if (value < 0) {
						s += "(" + (int) value;
					} else {
						s += "(+" + (int) value;
					}
				}
			} else {
				if (percent) {
					if (value < 0) {
						s += "," + (value * 100) + "%";
					} else {
						s += ",+" + (value * 100) + "%";
					}
				} else {
					if (value < 0) {
						s += "," + (int) value;
					} else {
						s += ",+" + (int) value;
					}
				}
			}
		}
		if (s.contains("(")) {
			s += ")";
		}
		return s;
	}

	@Override
	public String toString() {
		return getDesc();
	}

	@Override
	public int hashCode() {
		int hash = 7;
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
		final BookPage other = (BookPage) obj;
		if (Float.floatToIntBits(this.value) != Float.floatToIntBits(other.value)) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.tgtName, other.tgtName)) {
			return false;
		}
		return this.compositeType == other.compositeType;
	}

}
