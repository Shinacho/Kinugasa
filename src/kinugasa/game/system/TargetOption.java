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
