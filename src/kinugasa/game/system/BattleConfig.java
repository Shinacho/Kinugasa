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

import java.util.function.IntSupplier;
import kinugasa.game.GameOption;
import kinugasa.object.ImageSprite;
import kinugasa.resource.KImage;
import kinugasa.resource.sound.Sound;
import kinugasa.util.Random;

/**
 * バトルで使うID等の設定です。適切に初期化されている必要があります。
 *
 * @vesion 1.0.0 - 2023/10/21_17:15:31<br>
 * @author Shinacho<br>
 */
public class BattleConfig {

	public static class Sounds {

		public static Sound 物理回避;
		public static Sound 魔法回避;
		public static Sound 物理ブロック;
		public static Sound 魔法ブロック;
		public static Sound 物理クリティカル;
		public static Sound 魔法クリティカル;
		public static Sound 物理反射;
		public static Sound 物理吸収;
		public static Sound 魔法反射;
		public static Sound 魔法吸収;
		public static Sound 魔法詠唱開始;
		public static Sound 死亡演出;
		public static Sound 手番開始;
		public static Sound 解脱;
	}
	//攻撃防御有効度
	public static float ATK_DEF_PERCENT = 0.85f;
	//最終ダメージ調整倍率
	public static float DAMAGE_MUL = 0.25f;

	//バトル時のキャラの移動速度
	public static float BATTLE_WALK_SPEED = 2.5f;
	public static float messageWindowY = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 135;

	//素早さ乱数
	public static float SPEED_SPREAD = 0.1f;

	public static IntSupplier 死亡演出＿敵の場合 = () -> Random.d3(1);
	public static IntSupplier 死亡演出＿味方の場合 = () -> Random.d6(2);
	public static ImageSprite castingAnimationMaster;
	public static KImage deadCharaImage;
	
	public static class ActionID {

		public static final String 移動 = "A001_MOV";
		public static final String 確定 = "A002_CMT";
		public static final String 防御 = "A003_DEF";
		public static final String 回避 = "A004_AVO";
		public static final String 状態 = "A005_STT";
		public static final String 逃走 = "A006_ESC";

	}
}
