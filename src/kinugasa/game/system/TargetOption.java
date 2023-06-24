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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @vesion 1.0.0 - May 23, 2023_11:47:19 AM<br>
 * @author Shinacho<br>
 */
public class TargetOption {

	public enum SelectType {
		ONE,
		IN_AREA,
	}

	public enum IFF {
		ON,
		OFF
	}

	public enum DefaultTarget {
		ENEMY,
		PARTY,
	}

	public enum SwitchTeam {
		OK,
		NG,
	}

	public enum SelfTarget {
		YES,
		NO,
	}

	public enum Targeting {
		ENABLE,
		DISABLE,;
	}
	private static Set<TargetOption> set = new HashSet<>();
	private SelectType selectType;
	private IFF iff;
	private DefaultTarget defaultTarget;
	private SwitchTeam switchTeam;
	private SelfTarget selfTarget;
	private Targeting targeting;

	public static TargetOption of(SelectType selectType,
			IFF iff,
			DefaultTarget defaultTarget,
			SwitchTeam switchTeam,
			SelfTarget selfTarget,
			Targeting targeting) {
		TargetOption o = new TargetOption(selectType, iff, defaultTarget, switchTeam, selfTarget, targeting);
		for (TargetOption to : set) {
			if (to.equals(o)) {
				return to;
			}
		}
		set.add(o);
		return o;
	}

	private TargetOption(SelectType selectType, IFF iff, DefaultTarget defaultTarget, SwitchTeam switchTeam, SelfTarget selfTarget, Targeting targeting) {
		this.selectType = selectType;
		this.iff = iff;
		this.defaultTarget = defaultTarget;
		this.switchTeam = switchTeam;
		this.selfTarget = selfTarget;
		this.targeting = targeting;
	}

	public SelfTarget getSelfTarget() {
		return selfTarget;
	}

	public SelectType getSelectType() {
		return selectType;
	}

	public IFF getIff() {
		return iff;
	}

	public DefaultTarget getDefaultTarget() {
		return defaultTarget;
	}

	public SwitchTeam getSwitchTeam() {
		return switchTeam;
	}

	public Targeting getTargeting() {
		return targeting;
	}

	@Override
	public String toString() {
		return "TargetOption{" + "selectType=" + selectType + ", iff=" + iff + ", defaultTarget=" + defaultTarget + ", switchTeam=" + switchTeam + ", selfTarget=" + selfTarget + ", targeting=" + targeting + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.selectType);
		hash = 67 * hash + Objects.hashCode(this.iff);
		hash = 67 * hash + Objects.hashCode(this.defaultTarget);
		hash = 67 * hash + Objects.hashCode(this.switchTeam);
		hash = 67 * hash + Objects.hashCode(this.selfTarget);
		hash = 67 * hash + Objects.hashCode(this.targeting);
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
		final TargetOption other = (TargetOption) obj;
		if (this.selectType != other.selectType) {
			return false;
		}
		if (this.iff != other.iff) {
			return false;
		}
		if (this.defaultTarget != other.defaultTarget) {
			return false;
		}
		if (this.switchTeam != other.switchTeam) {
			return false;
		}
		if (this.selfTarget != other.selfTarget) {
			return false;
		}
		return this.targeting == other.targeting;
	}

}
