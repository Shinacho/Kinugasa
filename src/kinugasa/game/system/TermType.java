package kinugasa.game.system;

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


/**
 * 条件タイプは、アクションの発動条件を定義する列挙です。
 * @vesion 1.0.0 - 2022/12/01_21:52:25<br>
 * @author Dra211<br>
 */
public enum TermType {
	EQ_WEAPON_TYPE,
	EQ_ITEM,
	NO_EQ_WEAPON_TYPE,
	NO_EQ_ITEM,
	EQ_ANY_ITEM,
	NO_EQ_ANY_ITEM,
	HAS_ITEM,
	HAS_BOOK,
	HAS_CONDITION,
	NO_CONDITION,
	NONE,
	EQ_ITEM_NAME_CONTAINS,
	EQ_ITEM_DESC_CONTAINS
	;
}
