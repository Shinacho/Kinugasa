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
 * �o�g���A�N�V�����̌��ʑΏۂł��B
 *
 * @vesion 1.0.0 - 2022/11/16_10:48:45<br>
 * @author Shinacho<br>
 */
public enum TargetType {
	/**
	 * ���g�̂݁B
	 */
	SELF,
	/**
	 * �Ώۈ�l�B�B
	 */
	ONE_ENEMY,
	ONE_PARTY,
	/**
	 * �G�S�̂܂��͖����S�́B
	 */
	TEAM_PARTY,
	TEAM_ENEMY,
	/**
	 * �G�����S�́B
	 */
	ALL,
	/**
	 * �t�B�[���h�B����ȍ�p�ł��B
	 */
	FIELD,
	/**
	 * �����_���ȕW�I�i�����܂ށB
	 */
	RANDOM_ONE,
	/**
	 * �����_���ȕW�I�i�G�̂݁B����𕡐���`���邱�ƂŐ��̂ւ̍U���������ł��邾�낤
	 */
	RANDOM_ONE_ENEMY,
	/**
	 * �����_���ȃp�[�e�B�����o�[
	 */
	RANDOM_ONE_PARTY,;

}
