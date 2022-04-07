/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
package kinugasa.game.event;

import java.io.Serializable;

/**
 * エントリモデルはあるイベントがその時点で起動するかどうかを判定する機能です.
 * <br>
 * <br>
 *
 * @version 1.0.0 - 2012/10/19_18:14:04.<br>
 * @author Dra0211<br>
 */
public interface EntryModel extends Serializable {

	/**
	 * 指定したイベントが、現時点で実行可能であるかを判定します.
	 * 通常はイベントマネージャの機能を使用できるように、イベントマネージャの実装にprivate finalなフィールドとして
	 * 定義してください。<br>
	 *
	 * @param evt 判定するイベント。<br>
	 *
	 * @return このイベントを実行できる場合はtrue、そうでない場合はfalseを返す。<br>
	 */
	public abstract boolean isReaching(TimeEvent<?> evt);
}
