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
package kinugasa.game.field4;

/**
 * フィールドマップの状態による、ユーザ操作を要求します。
 *
 * @vesion 1.0.0 - 2022/12/12_18:04:27<br>
 * @author Shinacho<br>
 */
public enum UserOperationRequire {

	/**
	 * イベントを実行しますので、操作を受け付けずに待機してください。FMupdate、 moveを実行し続けてください。
	 * FieldEventSystemが操作可能になるまで待機してください。
	 */
	WAIT_FOR_EVENT,
	/**
	 * 戦闘システムの起動が要求されました。戦闘ロジックに切り替えてください。 エンカウント情報はフィールドイベントシステムから取得してください。
	 */
	TO_BATTLE,
	/**
	 * ゲームオーバーが要求されました。
	 */
	GAME_OVER,
	/**
	 * マップ変更が要求されました。
	 */
	CHANGE_MAP,
	/**
	 * メッセージウインドウの表示が要求されました。テキストをFieldEventSystemから取得して表示してください。
	 */
	SHOW_MESSAGE,
	CLOSE_MESSAGE,
	GET_ITEAM,
	/**
	 * 通常のフィールドマップ移動を続行してください。
	 */
	CONTINUE,
	/**
	 * フェードアウトが要求されました。フェードアウトエフェクトを再生して、終了したら次のイベントを実行してください。
	 */
	FADE_OUT,
	/**
	 * フェードインが要求されました。フェードインエフェクトを再生して、終了したら次のイベントを実行してください。
	 */
	FADE_IN,
	BLACKOUT,

}
