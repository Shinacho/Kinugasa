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

import java.util.Map;
import kinugasa.game.Nullable;

/**
 * アイテムを装備したときのステータス効果などを定義します。
 *
 * @vesion 1.0.0 - 2023/10/20_21:08:40<br>
 * @author Shinacho<br>
 */
public interface ItemEqipEffect {

	//送られてくるのはアイテムの装備効果！！！！
	public default StatusValueSet getStatusValue(StatusValueSet v) {
		return v.clone();
	}

	public default AttributeValueSet getAttrOut(AttributeValueSet v) {
		return v.clone();
	}

	public default AttributeValueSet getAttrIn(AttributeValueSet v) {
		return v.clone();
	}

	public default ConditionRegist getCndRegist(ConditionRegist c) {
		return c.clone();
	}

	public default int mulValue(int v) {
		return v;
	}

	public default int mulAtkCount(int c) {
		return c;
	}

	public default int mulArea(int a) {
		return a;
	}

	public default int mulExp(int v) {
		return v;
	}

	public default Map<Material, Integer> mulDissaseMaterial(Map<Material, Integer> m) {
		return m;
	}

	public default Map<Material, Integer> mulUpgradeMaterial(Map<Material, Integer> m) {
		return m;
	}

	public default void startEffect(ConditionFlags f, float mul) {

	}

	public default void endEffect(ConditionFlags f, float mul) {
	}

	public default float mulCndPercent(float v) {
		return v;
	}

	public default float getCndPercent() {
		return 0f;
	}

	@Nullable
	public default ConditionKey getCndKey() {
		return null;
	}
	
	public default int getCndTime(){
		return 0;
	}

}
