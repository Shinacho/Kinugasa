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
package kinugasa.game.input;

/**
 * キーやゲームパッドの入力判定の検査モードを指定します.
 * <br>
 * 
 * <br>
 * @version 1.0.0 - 2013/01/14_14:51:13<br>
 * @author Shinacho<br>
 */

public enum InputType {

	/** この列挙では、1回の入力処理を判定します.
	 * キーが押されていない状態から押されたときに最初の1回だけ「押されている」状態として
	 * 認識されます. */
	SINGLE,
	/** この列挙では、複数回の入力を判定します.
	 * キーが押されている間ずっと「押されている」状態として認識されます. */
	CONTINUE,;
}
