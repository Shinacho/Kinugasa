/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
 * @vesion 1.0.0 - 2022/11/16_10:53:21<br>
 * @author Shinacho<br>
 */
public enum ParameterType {
	/**
	 * ステータスの変更を行うアクションタイプです。
	 */
	STATUS(3),
	/**
	 * 属性耐性の変更を行うアクションタイプです。
	 */
	ATTR_IN(4),
	/**
	 * valueの名前のアイテムを破棄するタイプです。
	 */
	ITEM_LOST(1),
	/**
	 * valueの名前のアイテムを追加するタイプです。
	 */
	ITEM_ADD(2),
	/**
	 * 何も行わないアクションタイプです。
	 */
	NONE(999),
	/**
	 * 状態異常を付与するアクションです。
	 */
	ADD_CONDITION(5),
	/**
	 * 状態異常を削除するアクションです。
	 */
	REMOVE_CONDITION(6),
	;
	private int value;

	private ParameterType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
