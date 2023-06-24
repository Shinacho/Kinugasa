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
