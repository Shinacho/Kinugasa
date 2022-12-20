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
package kinugasa.game.field4;

/**
 * フィールドマップの状態による、ユーザ操作を要求します。
 *
 * @vesion 1.0.0 - 2022/12/12_18:04:27<br>
 * @author Dra211<br>
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
