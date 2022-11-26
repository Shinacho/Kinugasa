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

import java.util.Objects;
import kinugasa.resource.Nameable;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_12:09:03<br>
 * @author Dra211<br>
 */
public class ConditionKey implements Nameable {

	private String name;
	private String desc;
	private int priority;

	public ConditionKey(String name, String desc, int priority) {
		this.name = name;
		this.desc = desc;
		this.priority = priority;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + Objects.hashCode(this.name);
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
		final ConditionKey other = (ConditionKey) obj;
		return Objects.equals(this.name, other.name);
	}

	@Override
	public String toString() {
		return "ConditionKey{" + "name=" + name + '}';
	}

}
