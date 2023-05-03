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

import java.util.Map;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_12:21:49<br>
 * @author Dra211<br>
 */
public class AttributeValueSet extends Storage<AttributeValue> implements Cloneable {

	public AttributeValueSet() {
		for (AttributeKey k : AttributeKeyStorage.getInstance()) {
			add(new AttributeValue(k, 1, 1, 0, 1));
		}

	}

	public void setAll(float val) {
		for (AttributeValue v : this) {
			v.set(val);
		}
	}

	@Override
	public AttributeValueSet clone() {
		AttributeValueSet r = new AttributeValueSet();
		r.clear();
		for (AttributeValue v : this) {
			r.add(v.clone());
		}
		return r;
	}

	public void addAll(Map<AttributeKey, Float> v) {
		for (Map.Entry<AttributeKey, Float> e : v.entrySet()) {
			for (AttributeValue av : this) {
				if (av.getKey().equals(e.getKey())) {
					av.add(e.getValue());
				}
			}
		}
	}

}
