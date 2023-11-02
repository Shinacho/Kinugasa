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

/**
 *
 * @vesion 1.0.0 - 2023/10/14_22:12:47<br>
 * @author Shinacho<br>
 */
public enum ActionResultSummary {
	成功,
	成功＿ブロックされたが１以上,
	成功＿クリティカル,
	失敗＿不発, 
	失敗＿基礎威力０,
	失敗＿このアクションにはイベントがない,
	失敗＿実行したがミス,
	失敗＿回避された,
	失敗＿計算結果０,
	失敗＿反射された,
	失敗＿吸収された,
	失敗＿リソースが足りない,
	失敗＿術者死亡,;

	public boolean is成功() {
		return toString().startsWith("成功");
	}

	public boolean is失敗() {
		return toString().startsWith("失敗");
	}

}
