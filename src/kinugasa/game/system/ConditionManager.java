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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.object.AnimationSprite;
import kinugasa.object.ImageSprite;

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

	public void setCondition(List<? extends Actor> target) {
		if (BattleConfig.undeadDebugMode) {
			return;
		}

		for (Actor s : target) {
			for (StatusValue val : s.getStatus().getEffectedStatus()) {
				String condName = val.getKey().getWhen0ConditionName();
				if (condName == null) {
					continue;
				}
				if (val.getValue() <= 0) {
					if (!s.getStatus().hasCondition(condName)) {
						s.getStatus().addCondition(condName);
					}
					//
					if (s.getStatus().hasConditions(false, BattleConfig.deadConditionNames)) {
						if (s.getSprite() instanceof AnimationSprite) {
							((AnimationSprite) s).getAnimation().setStop(true);
						}
						if (s.getSprite() instanceof ImageSprite) {
							((AnimationSprite) s).setTmpImage(BattleConfig.deadCharaImage);
						}
					} else if (s.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
						s.getSprite().setVisible(false);
						s.getStatus().setExists(false);
					}
				} else {
					if (s.getStatus().hasCondition(condName)) {
						s.getStatus().removeCondition(condName);
						s.getSprite().setVisible(true);
						s.getStatus().setExists(true);
						if (s.getSprite() instanceof AnimationSprite) {
							((AnimationSprite) s).getAnimation().setStop(false);
						}
						if (s.getSprite() instanceof ImageSprite) {
							((AnimationSprite) s).clearTmpImage();
						}
					}
				}
			}
		}
	}

}
