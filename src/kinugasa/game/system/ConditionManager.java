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

import java.util.List;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2022/11/26_17:00:32<br>
 * @author Dra211<br>
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
					if (!ConditionValueStorage.getInstance().contains(condName)) {
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
