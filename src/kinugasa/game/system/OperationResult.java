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

/**
 *
 * @vesion 1.0.0 - 2022/11/29_21:31:59<br>
 * @author Dra211<br>
 */
public enum OperationResult {
	/**
	 * 行動が実行され、正常終了したことを表します。次の手番に送りますが、INFOまたはACTIONメッセージが表示されている間は待機してください。
	 */
	SUCCESS,
	/**
	 * 行動はキャンセルされました。再度コマンドを選択できます。
	 */
	CANCEL,
	/**
	 * 移動が要求されました。そのロジックに切り替えてください。
	 * afterウインドウが表示されている。
	 * 移動中に攻撃する場合は、execPCActionを実行する。
	 */
	MOVE,
	/**
	 * ステータス画面の表示が要求されました。そのロジックに切り替えてください。
	 */
	SHOW_STATUS,
	/**
	 * 行動は実行されましたが失敗しました。次の手番に送りますが、INFOまたはACTIONメッセージが表示されている間は待機してください。
	 */
	MISS,
	/**
	 * ターゲット選択が要求されました。そのロジックに切り替えてください。
	 */
	TO_TARGET_SELECT,;

}
