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

import kinugasa.resource.*;

/**
 * �o�g���A�N�V�����̒l�����s�ł��邩�ǂ����𔻒f����N���X�ł��B
 * �Ⴆ�΁A���������Ă��Ȃ��ƌ��ɂ��X�L���������ł��Ȃ��A���A�A�N�V�������ǉ�����Ă��邩�ǂ��������Ⴂ���x���ł̔�����s���܂��B
 *
 * @vesion 1.0.0 - 2022/11/16_16:00:49<br>
 * @author Dra211<br>
 */
public class BattleActionEventTerm implements Nameable {

	private String name;
	private BattleActionEventTermType type;
	private String value;

	public BattleActionEventTerm(String name, BattleActionEventTermType type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	public BattleActionEventTermType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public boolean canDoThis(GameSystem gs, Status user) {
		return type.canDoThis(gs, value, user);
	}

}
