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
		public static Sound 正気度減少演出;
		public static Sound 手番開始;
		public static Sound 手番開始＿犬;
		public static Sound 解脱;
		public static Sound 気絶;
		public static Sound 損壊;
	}
	//攻撃防御有効度
	public static float ATK_DEF_PERCENT = 0.75f;
	//最終ダメージ調整倍率
	public static float DAMAGE_MUL = 2f;

	//バトル時のキャラの移動速度
	public static float BATTLE_WALK_SPEED = 2.5f;
	public static float messageWindowY = BattleFieldSystem.getInstance().getBattleFieldAllArea().getY()
			+ BattleFieldSystem.getInstance().getBattleFieldAllArea().getHeight() + 1;
	//素早さ乱数
	public static float SPEED_SPREAD = 0.1f;

	public static IntSupplier 正気度減少イベントの数値＿味方の場合 = () -> Random.d6(3);
	public static IntSupplier 正気度減少イベントの数値＿敵の場合 = () -> Random.d3(1);
	public static ImageSprite castingAnimationMaster;
	public static KImage deadCharaImage1;
	public static KImage deadCharaImage2;

	public static class ActionID {

		public static final String 移動 = "A001_MOV";
		public static final String 確定 = "A002_CMT";
		public static final String 防御 = "A003_DEF";
		public static final String 回避 = "A004_AVO";
		public static final String 状態 = "A005_STT";
		public static final String 逃走 = "A006_ESC";

	}
}
