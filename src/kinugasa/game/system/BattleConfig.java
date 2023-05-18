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

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameOption;
import kinugasa.game.I18N;

/**
 *
 * @vesion 1.0.0 - 2022/11/24_21:58:24<br>
 * @author Shinacho<br>
 */
public class BattleConfig {

	public static float messageWindowY = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 135;

	public static String initialPCMoveVehicleKey = "WALK";

	public static class StatusKey {

		public static String hp = "HP";
		public static String move = "MOV";
		public static String exp = "EXP";
		public static String str = "STR";
		//ダメージ計算
		public static String critAtk = "CRT_P";
		public static String critMgk = "M_CRT_P";
		public static String critAtkVal = "CRT_V";
		public static String critMgkVal = "M_CRT_V";
		public static String avoAtk = "AVO";
		public static String avoMgk = "M_AVO";
		public static String cutAtk = "CUT_P";
		public static String cutMgk = "M_CUT_P";
		public static String cutAtkVal = "CUT_V";
		public static String cutMgkVal = "M_CUT_V";
		public static String defAtk = "DEF";
		public static String defMgk = "M_DEF";
		public static String atk = "ATK";
		public static String mgk = "M_ATK";
		
	}
	
	public static class Sound{
		public static Sound avoidance;
		public static Sound block;
		public static Sound spellStart;
		
	}
	
	public static float atkDefPercent = 0.75f;//ダメージ効率
	public static float damageMul = 2.5f;
	public static String weaponSlotName = "";

	public static class ConditionName {

		public static String casting = "CASTING";
		public static String escaped = "ESCAPED";
		public static String defence = "DEFENCE";
		public static String avoidance = "AVOIDANCE";
	}

	//
	public static class ActionName {

		public static String move = "MOVE";
		public static String defence = "DEFENCE";
		public static String avoidance = "AVOID";
		public static String status = "STATUS";
		public static String escape = "ESCAPE";
		public static String commit = "COMMIT";
	}
	//
	public static float confuStopP = 0.5f;
	public static int actionWindowLF = 48;

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

	private static List<String> visibleStatus = new ArrayList<>();

	public static void setVisibleStatus(List<String> visibleStatus) {
		BattleConfig.visibleStatus = visibleStatus;
	}

	public static List<String> getVisibleStatus() {
		return visibleStatus;
	}
	private static List<String> magicVisibleStatusKey = new ArrayList<>();

	public static List<String> getMagicVisibleStatusKey() {
		return magicVisibleStatusKey;
	}

	public static void setMagicVisibleStatusKey(List<String> statusKey) {
		BattleConfig.magicVisibleStatusKey = statusKey;
	}

	public static class AttributeKey {

		public static String defaultAttrName = "";
	}
}
