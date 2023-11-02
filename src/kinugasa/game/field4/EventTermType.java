/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.game.field4;

import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.system.Actor;
import kinugasa.game.system.CurrentQuest;
import kinugasa.game.system.Flag;
import kinugasa.game.system.FlagStatus;
import kinugasa.game.system.FlagStorage;
import kinugasa.game.system.GameSystem;

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
		public boolean canExec(List<Actor> party, EventTerm t) {
			List<Actor> list = party.stream().filter(p -> p.getId().equals(t.getTargetName())).collect(Collectors.toList());
			if (list.isEmpty()) {
				return false;
			}
			return list.get(0).getStatus().getItemBag().contains(t.getValue());
		}
	},
	TGT_HASNT_ITEM {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			List<Actor> list = party.stream().filter(p -> p.getId().equals(t.getTargetName())).collect(Collectors.toList());
			if (list.isEmpty()) {
				return false;
			}
			return !list.get(0).getStatus().getItemBag().contains(t.getValue());
		}
	},
	HAS_ITEM_ANY_PC {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			return party.stream().map(p -> p.getStatus().getItemBag()).anyMatch(p -> p.contains(t.getValue()));
		}
	},
	HASNT_ITEM_ANY_PC {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			return party.stream().map(p -> p.getStatus().getItemBag()).allMatch(p -> !p.contains(t.getValue()));
		}
	},
	//tgtNameのキャラがいるかどうか
	PARTY_CONTAINS_BYID {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			return party.stream().anyMatch(p -> p.getId().equals(t.getTargetName()));
		}
	},
	PARTY_NO_CONTAINS_BYID {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			return party.stream().allMatch(p -> !p.getId().equals(t.getTargetName()));
		}
	},
	MONEY_IS_OVER {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			return GameSystem.getInstance().getMoneySystem().get(t.getTargetName()).getValue() > Integer.parseInt(t.getValue());
		}
	},
	MONEY_IS_ZERO {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			return GameSystem.getInstance().getMoneySystem().get(t.getTargetName()).getValue() <= 0;
		}
	},
	PARTY_SIZE_IS {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			return party.size() == Integer.parseInt(t.getValue());
		}
	},
	FLG_IS {
		@Override
		boolean canExec(List<Actor> party, EventTerm t) {
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
		boolean canExec(List<Actor> party, EventTerm t) {
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
		public boolean canExec(List<Actor> party, EventTerm t) {
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
		public boolean canExec(List<Actor> party, EventTerm t) {
			return CurrentQuest.getInstance().get(t.getTargetName()).getStage() == Integer.parseInt(t.getValue());
		}
	},
	QUEST_LINE_IS_OVER {
		@Override
		public boolean canExec(List<Actor> party, EventTerm t) {
			return CurrentQuest.getInstance().get(t.getTargetName()).getStage() >= Integer.parseInt(t.getValue());
		}
	},;

	abstract boolean canExec(List<Actor> party, EventTerm t);

}
