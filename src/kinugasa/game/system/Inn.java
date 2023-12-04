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
import java.util.Set;
import kinugasa.game.I18N;
import kinugasa.game.field4.D2Idx;
import kinugasa.game.field4.FieldMap;
import kinugasa.object.FourDirection;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;

/**
 * 宿の情報。宿は名前、開始地点、プランを持ちます。
 *
 * @vesion 1.0.0 - 2023/12/01_20:20:02<br>
 * @author Shinacho<br>
 */
public class Inn implements Nameable {

	private String id;
	private String visibleNameCache;
	private FieldMap fieldMap;
	private D2Idx startIdx;
	private FourDirection startDir;
	private Storage<InnPlan> plans;
	private Set<String> paymentName;

	private Inn() {

	}

	public static Inn readFromXml(String filePath) {
		Inn res = new Inn();

		res.visibleNameCache = I18N.get(res.id);
	}

	public Set<String> getPaymentName() {
		return paymentName;
	}

	public String getId() {
		return id;
	}

	public String getVisibleName() {
		return visibleNameCache;
	}

	public FieldMap getFieldMap() {
		return fieldMap;
	}

	public D2Idx getStartIdx() {
		return startIdx;
	}

	public Storage<InnPlan> getPlans() {
		return plans;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public String toString() {
		return "Inn{" + "id=" + id + ", visibleNameCache=" + visibleNameCache + ", fieldMap=" + fieldMap + ", startIdx=" + startIdx + ", plans=" + plans + '}';
	}

}
