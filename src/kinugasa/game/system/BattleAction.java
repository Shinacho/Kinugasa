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

import java.util.ArrayList;
import java.util.List;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:32:30<br>
 * @author Dra211<br>
 */
public class BattleAction implements Nameable {

	// このアクションのID
	private String name;
	private String desc;
	//操作の一覧
	private List<BattleActionEvent> events = new ArrayList<>();
	// このアクションが実施できるか判断するロジック
	private List<BattleActionEventTerm> term;

	public BattleAction(String name, String desc, List<BattleActionEventTerm> term) {
		this.name = name;
		this.desc = desc;
		this.term = term;
	}

	public BattleAction(String name, String desc, List<BattleActionEvent> actionVal, List<BattleActionEventTerm> term) {
		this.name = name;
		this.desc = desc;
		this.events = actionVal;
		this.term = term;
	}

	@Override
	public String getName() {
		return name;
	}

	// userがこのアクションを実行できるか判定します
	public boolean canDoThis(GameSystem gs, Status user) {
		return term.stream().anyMatch(p -> p.canDoThis(gs, user));
	}

	public void exec(GameSystem gs, Status user) {
		events.forEach(a -> a.exec(gs, this, user));
	}

	public List<BattleActionEvent> getActionVal() {
		return events;
	}

	public List<BattleActionEventTerm> getTerm() {
		return term;
	}

	// このアクションを発動した際の、指定のキーのステータスのSELF増減を計算します。＋または-です。SELFのみ対象です。
	public int calcSelfStatusDamage(Status self, String name) {
		int r = 0;
		for (BattleActionEvent e : events) {
			if (e.getTargetType() == BattleActionTargetType.SELF) {
				if (e.getTargetParameterType() == BattleActionTargetParameterType.STATUS) {
					if (name.equals(e.getTargetName())) {
						switch (e.getDamageCalcType()) {
							case DIRECT:
								r += e.getValue();
								break;
							case PERCENT_OF_MAX:
								float max = StatusKeyStorage.getInstance().get(name).getMax();
								r += (max * e.getValue());
								break;
							case PERCENT_OF_NOW:
								float current = self.getEffectedStatus().get(name).getValue();
								r += (current * e.getValue());
								break;
							default:
								throw new AssertionError();
						}
					}
				}
			}
		}
		return r;
	}

}
