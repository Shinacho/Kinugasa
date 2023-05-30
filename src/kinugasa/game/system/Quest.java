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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import kinugasa.resource.Nameable;
import kinugasa.resource.db.DBRecord;

/**
 *
 * @vesion 1.0.0 - 2022/12/11_16:56:15<br>
 * @author Shinacho<br>
 */
@DBRecord
public class Quest implements Nameable {

	private String id;//クエスト種類
	private int stage;
	private String visibleName;
	private String desc;

	public Quest(String id, int stage, String visibleName, String desc) {
		this.id = id;
		this.stage = stage;
		this.visibleName = visibleName;
		this.desc = desc;
	}

	public String getId() {
		return id;
	}

	public int getStage() {
		return stage;
	}

	public String getVisibleName() {
		return visibleName;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.id);
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
		final Quest other = (Quest) obj;
		return Objects.equals(this.id, other.id);
	}

	@Override
	public String toString() {
		return "Quest{" + "id=" + id + ", stage=" + stage + ", visibleName=" + visibleName + ", desc=" + desc + '}';
	}

}
