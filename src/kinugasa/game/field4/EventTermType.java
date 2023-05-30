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
package kinugasa.game.field4;

import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.system.CurrentQuest;
import kinugasa.game.system.Flag;
import kinugasa.game.system.FlagStatus;
import kinugasa.game.system.FlagStorage;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.QuestStorage;
import kinugasa.game.system.Status;

/**
 * ステータスやゲームシステムと密接に結びついたイベントの発生条件です。
 *
 * @vesion 1.0.0 - 2022/12/11_17:09:05<br>
 * @author Shinacho<br>
 */
public enum EventTermType {

	//TGTのキャラがvalueのアイテムを持っていることを確認する
	TGT_HAS_ITEM {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			List<Status> list = party.stream().filter(p -> p.getName().equals(t.getTargetName())).collect(Collectors.toList());
			if (list.isEmpty()) {
				return false;
			}
			return list.get(0).getItemBag().contains(t.getValue());
		}
	},
	TGT_HASNT_ITEM {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			List<Status> list = party.stream().filter(p -> p.getName().equals(t.getTargetName())).collect(Collectors.toList());
			if (list.isEmpty()) {
				return false;
			}
			return !list.get(0).getItemBag().contains(t.getValue());
		}
	},
	HAS_ITEM_ANY_PC {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return party.stream().map(p -> p.getItemBag()).anyMatch(p -> p.contains(t.getValue()));
		}
	},
	HASNT_ITEM_ANY_PC {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return party.stream().map(p -> p.getItemBag()).allMatch(p -> !p.contains(t.getValue()));
		}
	},
	//tgtNameのキャラがいるかどうか
	PARTY_CONTAINS {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return party.stream().anyMatch(p -> p.getName().equals(t.getTargetName()));
		}
	},
	PARTY_NO_CONTAINS {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return party.stream().allMatch(p -> !p.getName().equals(t.getTargetName()));
		}
	},
	MONEY_IS_OVER {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return GameSystem.getInstance().getMoneySystem().get(t.getTargetName()).getValue() > Integer.parseInt(t.getValue());
		}
	},
	MONEY_IS_ZERO {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return GameSystem.getInstance().getMoneySystem().get(t.getTargetName()).getValue() <= 0;
		}
	},
	PARTY_SIZE_IS {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return party.size() == Integer.parseInt(t.getValue());
		}
	},
	FLG_IS {
		@Override
		boolean canExec(List<Status> party, EventTerm t) {
			FlagStorage s = FlagStorage.getInstance();
			if (s == null) {
				return false;
			}
			if (!s.contains(t.getTargetName())) {
				return false;
			}
			Flag f = s.get(t.getTargetName());
			if (f == null) {
				return false;
			}
			return f.get() == FlagStatus.valueOf(t.getValue());
		}
	},
	NO_EXISTS_FLG {
		@Override
		boolean canExec(List<Status> party, EventTerm t) {
			FlagStorage s = FlagStorage.getInstance();
			if (s == null) {
				return true;
			}
			if (!s.contains(t.getTargetName())) {
				return true;
			}
			Flag f = s.get(t.getTargetName());
			if (f == null) {
				return true;
			}
			return false;
		}
	},
	EXISTS_FLG {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			FlagStorage s = FlagStorage.getInstance();
			if (s == null) {
				return false;
			}
			if (!s.contains(t.getTargetName())) {
				return false;
			}
			Flag f = s.get(t.getTargetName());
			if (f == null) {
				return false;
			}
			return true;
		}
	},
	QUEST_LINE_IS {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return CurrentQuest.getInstance().get(t.getTargetName()).getStage() == Integer.parseInt(t.getValue());
		}
	},
	QUEST_LINE_IS_OVER {
		@Override
		public boolean canExec(List<Status> party, EventTerm t) {
			return CurrentQuest.getInstance().get(t.getTargetName()).getStage() >= Integer.parseInt(t.getValue());
		}
	},;

	abstract boolean canExec(List<Status> party, EventTerm t);

}
