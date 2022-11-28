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

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameOption;

/**
 *
 * @vesion 1.0.0 - 2022/11/24_21:58:24<br>
 * @author Dra211<br>
 */
public class BattleConfig {

	public static float messageWindowY = GameOption.getInstance().getWindowSize().height - 135;
	public static String outputLogStatusKey = "HP";
	public static String moveStatusKey = "MOV";
	public static String initialPCMoveVehicleKey = "WALK";
	public static String expStatisKey = "EXP";
	public static String spellingConditionName = "SPELLING";

	private static List<BattleWinLoseLogic> winLoseLogic = new ArrayList<>();

	public static void setWinLoseLogic(List<BattleWinLoseLogic> winLoseLogic) {
		BattleConfig.winLoseLogic = winLoseLogic;
	}

	public static void addWinLoseLogic(BattleWinLoseLogic winLoseLogic) {
		BattleConfig.winLoseLogic.add(winLoseLogic);
	}

	public static List<BattleWinLoseLogic> getWinLoseLogic() {
		return winLoseLogic;
	}

	private static List<String> untargetConditionNames = new ArrayList<>();

	public static List<String> getUntargetConditionNames() {
		return untargetConditionNames;
	}

	public static void setUntargetConditionNames(List<String> untargetConditionNames) {
		BattleConfig.untargetConditionNames = untargetConditionNames;
		for (String n : untargetConditionNames) {
			if (!ConditionValueStorage.getInstance().contains(n)) {
				throw new GameSystemException("undefined condition name : " + n);
			}
		}
	}

	public static void addUntargetConditionNames(String name) {
		BattleConfig.untargetConditionNames.add(name);
		for (String n : untargetConditionNames) {
			if (!ConditionValueStorage.getInstance().contains(n)) {
				throw new GameSystemException("undefined condition name : " + n);
			}
		}
	}
	
	public static boolean undeadDebugMode = false;

}
