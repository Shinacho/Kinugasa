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
 * 状態異常による効果を定義します。
 *
 * @vesion 1.0.0 - 2023/10/20_21:14:22<br>
 * @author Shinacho<br>
 */
public interface ConditionEffect {

	public default void startEffect(ConditionFlags f) {
	}

	public default void endEffect(ConditionFlags f) {
	}
	
	public default void turnStart(Status s){
		
	}

	//差分だけ返す
	public default StatusValueSet getStatusValue(StatusValueSet v) {
		return new StatusValueSet();
	}

	//差分だけ返す
	public default AttributeValueSet getAttrOut(AttributeValueSet v) {
		return new AttributeValueSet();
	}

	//差分だけ返す
	public default AttributeValueSet getAttrIn(AttributeValueSet v) {
		return new AttributeValueSet();
	}

	//差分だけ返す
	public default ConditionRegist getCndRegist(ConditionRegist v) {
		return new ConditionRegist();
	}

	public default ConditionFlags.ConditionPercent getPercent() {
		return new ConditionFlags.ConditionPercent(0f, 0f);
	}

	public default int mulExp(int v) {
		return v;
	}
}
