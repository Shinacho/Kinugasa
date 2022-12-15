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
package kinugasa.game.field4;

import java.util.List;
import kinugasa.game.system.Status;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_17:09:47<br>
 * @author Dra211<br>
 */
public class EventTerm implements Nameable {

	private String name;
	private EventTermType termType;
	private String storageName;
	private String targetName;
	private String value;

	public EventTerm(String name, EventTermType termType, String storageName, String targetName, String value) {
		this.name = name;
		this.termType = termType;
		this.storageName = storageName;
		this.targetName = targetName;
		this.value = value;
	}

	public boolean canDoThis(List<Status> party, FieldEvent e) {
		return termType.canExec(party, this);
	}

	@Override
	public String getName() {
		return name;
	}

	public EventTermType getTermType() {
		return termType;
	}

	public String getStorageName() {
		return storageName;
	}

	public String getTargetName() {
		return targetName;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "EventTerm{" + "name=" + name + ", termType=" + termType + ", storageName=" + storageName + ", targetName=" + targetName + ", value=" + value + '}';
	}

}
