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
import kinugasa.game.system.GameSystem;
import kinugasa.object.Model;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_19:23:32<br>
 * @author Dra211<br>
 */
public class FieldEvent extends Model implements Nameable, Comparable<FieldEvent> {

	private String name;
	private int order;
	private D2Idx location;
	private FieldEventType eventType;
	private String storageName;
	private String targetName;
	private String value;
	private boolean disposeWhenExec = true;
	private boolean exists = true;
	private List<EventTerm> term;

	public FieldEvent(String name, int order, D2Idx location,
			List<EventTerm> term, FieldEventType eventType,
			String storageName, String targetName, String value) {
		this.name = name;
		this.order = order;
		this.location = location;
		this.term = term;
		this.eventType = eventType;
		this.storageName = storageName;
		this.targetName = targetName;
		this.value = value;
	}

	public UserOperationRequire exec(FieldMap m) {
		//生存状態の判定
		if (!exists) {
			if (GameSystem.isDebugMode()) {
				System.out.println("->, but this event is dead");
			}
			return UserOperationRequire.CONTINUE;
		}
		//Termの判定
		if (term != null) {
			if (term.stream().anyMatch(p -> !p.canDoThis(GameSystem.getInstance().getPartyStatus(), this))) {
				if (GameSystem.isDebugMode()) {
					System.out.println("->, but this event is disable");
				}
				return UserOperationRequire.CONTINUE;
			}
		}
		//type別実行
		UserOperationRequire u = eventType.exec(GameSystem.getInstance().getPartyStatus(), this);
		//実行後生存状態の更新
		if (disposeWhenExec) {
			exists = false;
		}
		System.out.println("->done");
		return u;
	}

	public D2Idx getLocation() {
		return location;
	}

	public void setTerm(List<EventTerm> term) {
		this.term = term;
	}

	public List<EventTerm> getTerm() {
		return term;
	}

	public FieldEventType getEventType() {
		return eventType;
	}

	public void setEventType(FieldEventType eventType) {
		this.eventType = eventType;
	}

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isDisposeWhenExec() {
		return disposeWhenExec;
	}

	public void setDisposeWhenExec(boolean disposeWhenExec) {
		this.disposeWhenExec = disposeWhenExec;
	}

	public boolean isExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int compareTo(FieldEvent o) {
		return order - o.order;
	}

	@Override
	public String toString() {
		return "FieldEvent{" + "name=" + name + ", order=" + order + ", location=" + location + ", eventType=" + eventType + ", storageName=" + storageName + ", targetName=" + targetName + ", value=" + value + ", disposeWhenExec=" + disposeWhenExec + ", exists=" + exists + ", term=" + term + '}';
	}

}
