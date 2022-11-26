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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_12:07:46<br>
 * @author Dra211<br>
 */
public class ConditionValue implements Nameable {

	private ConditionKey key;
	private List<EffectMaster> effects = new ArrayList<>();

	public ConditionValue(ConditionKey key) {
		this.key = key;
	}

	public ConditionValue(ConditionKey key, EffectMaster e) {
		this.key = key;
		this.effects.add(e);
	}

	public ConditionValue(ConditionKey key, List<EffectMaster> effects) {
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

	public List<EffectMaster> getEffects() {
		return effects;
	}

	public void setEffects(List<EffectMaster> effects) {
		this.effects = effects;
	}

	@Override
	public String toString() {
		return super.toString(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
	}

	@Override
	public ConditionValue clone() {
		try {
			ConditionValue c = (ConditionValue) super.clone();
			c.effects = (List<EffectMaster>) ((ArrayList<EffectMaster>) effects).clone();
			return c;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
