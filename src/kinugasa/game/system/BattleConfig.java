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

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kinugasa.game.GameOption;
import kinugasa.object.Sprite;

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

		public static String canMagic = "CAN_MAGIC";

	}

	public static class Sound {

		public static kinugasa.resource.sound.Sound avoidance;
		public static kinugasa.resource.sound.Sound block;
		public static kinugasa.resource.sound.Sound spellStart;
		public static kinugasa.resource.sound.Sound shock;

	}
	public static kinugasa.game.system.StatusKey shockDamageKey;
	public static int shockDamageDefault = 2;//ショック演出ダメージ基礎値

	public static float atkDefPercent = 0.75f;//ダメージ効率
	public static float damageMul = 2.5f;
	public static String weaponSlotName = "";
	public static Sprite castingAnimation = null;

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
			if (!ConditionStorage.getInstance().contains(n)) {
				throw new GameSystemException("undefined condition name : " + n);
			}
		}
	}

	public static void addUntargetConditionNames(String name) {
		BattleConfig.untargetConditionNames.add(name);
		for (String n : untargetConditionNames) {
			if (!ConditionStorage.getInstance().contains(n)) {
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

	public static Map<kinugasa.game.system.StatusKey, Color> damageColor = new HashMap<>();

}
