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

import java.util.List;

/**
 *
 * @vesion 1.0.0 - 2022/11/26_17:00:32<br>
 * @author Shinacho<br>
 */
public class ConditionManager {

	private static final ConditionManager INSTANCE = new ConditionManager();

	private ConditionManager() {
	}

	static ConditionManager getInstance() {
		return INSTANCE;
	}

	public void setCondition(List<Status> target) {
		if (BattleConfig.undeadDebugMode) {
			return;
		}
		for (Status s : target) {
			for (StatusValue val : s.getEffectedStatus()) {
				if (val.getValue() <= 0) {
					String condName = val.getKey().getWhen0ConditionName();
					if (condName == null) {
						continue;
					}
					if (!ConditionStorage.getInstance().contains(condName)) {
						throw new GameSystemException("when 0 condition " + condName + " is not found.");
					}
					s.addCondition(condName);
					if (s.hasConditions(false, BattleConfig.getUntargetConditionNames())) {
						s.setExists(false);
					}
				}
			}
		}
	}

}
