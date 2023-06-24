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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_12:07:46<br>
 * @author Shinacho<br>
 */
public class Condition implements Nameable, Cloneable {

	private ConditionKey key;
	private List<ConditionEffect> effects = new ArrayList<>();

	public Condition(ConditionKey key) {
		this.key = key;
	}

	public Condition(ConditionKey key, ConditionEffect e) {
		this.key = key;
		this.effects.add(e);
	}

	public Condition(ConditionKey key, List<ConditionEffect> effects) {
		this.key = key;
		this.effects = effects;
	}

	@Override
	public String getName() {
		return key.getName();
	}

	public ConditionKey getKey() {
		return key;
	}

	public List<ConditionEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<ConditionEffect> effects) {
		this.effects = effects;
	}

	@Override
	public String toString() {
		return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
	}

	@Override
	public Condition clone() {
		try {
			Condition c = (Condition) super.clone();
			c.effects = (List<ConditionEffect>) ((ArrayList<ConditionEffect>) effects).clone();
			return c;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
