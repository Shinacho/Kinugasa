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
package kinugasa.game.field4;

import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.game.system.GameSystem;
import kinugasa.object.Model;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_19:23:32<br>
 * @author Shinacho<br>
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
			kinugasa.game.GameLog.print("->, but this event is dead");
//			GameLog.print("->, but this event is dead");
			return UserOperationRequire.CONTINUE;
		}
		//Termの判定
		if (term != null) {
			if (term.stream().anyMatch(p -> !p.canDoThis(GameSystem.getInstance().getPartyStatus(), this))) {
				kinugasa.game.GameLog.print("->, but this event is disable");
//				GameLog.print("->, but this event is disable");
				return UserOperationRequire.CONTINUE;
			}
		}
		//type別実行
		UserOperationRequire u = eventType.exec(GameSystem.getInstance().getPartyStatus(), this);
		//FieldEventScriptExceptionが出たらここで落ちる。
		//実行後生存状態の更新
		if (disposeWhenExec) {
			exists = false;
		}
		kinugasa.game.GameLog.print("->done");
//		GameLog.print("->done");
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
