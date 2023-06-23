/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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

/**
 *
 * @vesion 1.0.0 - 2023/01/01_12:46:11<br>
 * @author Shinacho<br>
 */
public abstract class ItemEqipTerm {

	public abstract boolean canEqip(Status s, Item i);

	public enum Type {
		ANY,
		STATUS_IS,
		RACE_IS,
		STATUS_IS_OVER,;
	}
	private Type type;
	private String tgtName;
	private float value;

	ItemEqipTerm(Type type, String tgtName, float value) {
		this.type = type;
		this.tgtName = tgtName;
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public String getTgtName() {
		return tgtName;
	}

	public float getValue() {
		return value;
	}

	public static final ItemEqipTerm ANY = new ItemEqipTerm(Type.ANY, "", 0f) {
		@Override
		public boolean canEqip(Status s, Item i) {
			return true;
		}
	};

	public static ItemEqipTerm statusIs(StatusKey key, float val) {
		return new ItemEqipTerm(Type.STATUS_IS, key.getName(), val) {
			@Override
			public boolean canEqip(Status s, Item i) {
				return (int) (s.getBaseStatus().get(key.getName()).getValue()) == val;
			}
		};
	}

	public static ItemEqipTerm raceIs(Race r) {
		return new ItemEqipTerm(Type.RACE_IS, r.getName(), 0f) {
			@Override
			public boolean canEqip(Status s, Item i) {
				return s.getRace().equals(r);
			}
		};
	}

	public static ItemEqipTerm statusIsOver(StatusKey key, float val) {
		return new ItemEqipTerm(Type.STATUS_IS_OVER, key.getName(), val) {
			@Override
			public boolean canEqip(Status s, Item i) {
				return (int) (s.getBaseStatus().get(key.getName()).getValue()) >= val;
			}
		};
	}
}
