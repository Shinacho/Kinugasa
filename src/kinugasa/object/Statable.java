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
package kinugasa.object;


/**
 * このインターフェースを実装したオブジェクトに、「開始している」「終了した」などの状態を調べる機能を提供します.
 * <br>
 * 主に、エフェクトの開始／破棄を判定するために使用されます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_19:11:08<br>
 * @author Shinacho<br>
 */
public interface Statable {

	/**
	 * このオブジェクトが「開始している」状態であるかを検査します.
	 * このオブジェクトの状態をリセットできる場合、リセット後も「開始されている」かどうかは
	 * 実装によって異なります。<br>
	 * @return 開始している場合はtrueを返します。<br>
	 */
	public boolean isRunning();

	/**
	 * このオブジェクトが「終了した」状態であるかを検査します.
	 * @return 終了している場合はtrueを返します。<br>
	 */
	public boolean isEnded();
}
