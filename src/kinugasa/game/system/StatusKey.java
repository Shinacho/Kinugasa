/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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

import kinugasa.resource.Nameable;

/**
 * ステータス1項目の名前と説明です
 *
 * @vesion 1.0.0 - 2022/11/15_11:11:32<br>
 * @author Shinacho<br>
 */
public class StatusKey implements Nameable, Comparable<StatusKey> {

	private String name;
	private String desc;
	private int order;
	private float min, max;
	private String when0ConditionName;

	public StatusKey(String name, String desc, int order, float min, float max, String when0ConditionName) {
		this.name = name;
		this.desc = desc;
		this.order = order;
		this.min = min;
		this.max = max;
		this.when0ConditionName = when0ConditionName;
	}

	public String getWhen0ConditionName() {
		return when0ConditionName;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

	@Override
	public String toString() {
		return "StatusKey{" + "name=" + name + ", desc=" + desc + ", order=" + order + ", min=" + min + ", max=" + max + ", when0ConditionName=" + when0ConditionName + '}';
	}

	@Override
	public int compareTo(StatusKey o) {
		return order - o.order;
	}

}
