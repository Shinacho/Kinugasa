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
 * @vesion 1.0.0 - 2022/11/16_10:53:21<br>
 * @author Dra211<br>
 */
public enum ParameterType {
	/**
	 * �X�e�[�^�X�̕ύX���s���A�N�V�����^�C�v�ł��B
	 */
	@Deprecated
	STATUS(3),
	/**
	 * �����ϐ��̕ύX���s���A�N�V�����^�C�v�ł��B
	 */
	@Deprecated
	ATTR_IN(4),
	/**
	 * value�̖��O�̃A�C�e����j������^�C�v�ł��B
	 */
	ITEM_LOST(1),
	/**
	 * value�̖��O�̃A�C�e����ǉ�����^�C�v�ł��B
	 */
	ITEM_ADD(2),
	/**
	 * �����s��Ȃ��A�N�V�����^�C�v�ł��B
	 */
	NONE(999),
	/**
	 * ��Ԉُ��t�^����A�N�V�����ł��B
	 */
	ADD_CONDITION(5),
	/**
	 * ��Ԉُ���폜����A�N�V�����ł��B
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
