package kinugasa.game.system;

/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
import kinugasa.game.system.GameSystemException;
import kinugasa.resource.Nameable;

/**
 * アクションの発動条件を定義するクラスです。
 *
 * @vesion 1.0.0 - 2022/12/01_21:52:13<br>
 * @author Shinacho<br>
 */
public class ActionTerm implements Nameable {

	private String name;
	private TermType type;
	private String value;

	public ActionTerm(String name, TermType type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	public TermType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	//アクションが発動できるかを返す。true：可能
	public boolean canExec(ActionTarget tgt) {
		switch (type) {
			case NONE:
				return true;
			case EQ_ITEM:
				return tgt.getUser().getStatus().isEqip(value);
			case EQ_WEAPON_TYPE:
				return tgt.getUser().getStatus().isEqipWMType(value);
			case HAS_BOOK:
				return tgt.getUser().getStatus().getBookBag().contains(value);
			case HAS_ITEM:
				return tgt.getUser().getStatus().getItemBag().contains(value);
			case NO_EQ_ITEM:
				return !tgt.getUser().getStatus().isEqip(value);
			case NO_EQ_WEAPON_TYPE:
				return !tgt.getUser().getStatus().isEqipWMType(value);
			case HAS_CONDITION:
				return tgt.getUser().getStatus().hasCondition(value);
			case NO_CONDITION:
				return !tgt.getUser().getStatus().hasCondition(value);
			case EQ_ANY_ITEM:
				return tgt.getUser().getStatus().isEqip(ItemEqipmentSlotStorage.getInstance().get(value));
			case NO_EQ_ANY_ITEM:
				return !tgt.getUser().getStatus().isEqip(ItemEqipmentSlotStorage.getInstance().get(value));
			case EQ_ITEM_DESC_CONTAINS:
				for (Item i : tgt.getUser().getStatus().getEqipment().values()) {
					if (i == null) {
						continue;
					}
					if (i.getDesc().contains(value)) {
						return true;
					}
				}
				return false;
			case EQ_ITEM_NAME_CONTAINS:
				for (Item i : tgt.getUser().getStatus().getEqipment().values()) {
					if (i == null) {
						continue;
					}
					if (i.getName().contains(value)) {
						return true;
					}
				}
				return false;
			case STATUS_IS:
				String key = value.split(",")[0];
				String val = value.split(",")[1];
				return (tgt.getUser().getStatus().getEffectedStatus().get(key).getValue() + "").replaceAll(".0", "").equals(val);
			default:
				throw new GameSystemException("undefined termtype : " + type);
		}
	}

	@Override
	public String toString() {
		return "ActionTerm{" + "type=" + type + ", value=" + value + '}';
	}

}
