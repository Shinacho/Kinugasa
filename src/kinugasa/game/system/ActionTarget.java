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
package kinugasa.game.system;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.object.BasicSprite;
import kinugasa.object.EmptySprite;
import kinugasa.object.FourDirection;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_19:16:09<br>
 * @author Shinacho<br>
 */
public class ActionTarget implements Iterable<BattleCharacter> {

	private BattleCharacter user;
	private Action action;
	private boolean inField = false;
	private List<BattleCharacter> target = new ArrayList<>();
	private boolean selfTarget = false;

	public ActionTarget(BattleCharacter user, Action a) {
		if (user == null) {
			throw new GameSystemException("battle action result s user is null");
		}
		this.user = user;
		this.action = a;
	}

	public static ActionTarget instantTarget(Status user, Action a) {
		return instantTarget(user, a, new Status[]{});
	}

	public static ActionTarget instantTarget(Status user, Action a, List<Status> tgt) {
		boolean userIsPlayer = GameSystem.getInstance().getPartyStatus().contains(user);
		List<DummyCharacter> target = new ArrayList<>();
		for (Status s : tgt) {
			boolean f = GameSystem.getInstance().getPartyStatus().contains(s);
			target.add(new DummyCharacter(s, f));
		}

		return new ActionTarget(new DummyCharacter(user, userIsPlayer), a)
				.setTarget(target.stream().collect(Collectors.toList()));
	}

	public static ActionTarget instantTarget(Status user, Action a, Status... tgt) {
		boolean userIsPlayer = GameSystem.getInstance().getPartyStatus().contains(user);
		List<DummyCharacter> target = new ArrayList<>();
		for (Status s : tgt) {
			boolean f = GameSystem.getInstance().getPartyStatus().contains(s);
			target.add(new DummyCharacter(s, f));
		}

		return new ActionTarget(new DummyCharacter(user, userIsPlayer), a)
				.setTarget(target.stream().collect(Collectors.toList()));
	}

	private static class DummyCharacter implements BattleCharacter {

		private Status s;
		private boolean player;

		public DummyCharacter(Status s, boolean player) {
			this.s = s;
			this.player = player;
		}

		@Override
		public BasicSprite getSprite() {
			return new EmptySprite(-123, -123, 1, 1);
		}

		@Override
		public Status getStatus() {
			return s;
		}

		@Override
		public void setTargetLocation(Point2D.Float p, int area) {
		}

		@Override
		public void unsetTarget() {
		}

		@Override
		public boolean isMoving() {
			return false;
		}

		@Override
		public void moveToTgt() {
		}

		@Override
		public void move() {
		}

		@Override
		public void to(FourDirection dir) {
		}

		@Override
		public boolean isPlayer() {
			return player;
		}

		@Override
		public String getId() {
			return getName();
		}
	}

	public ActionTarget setInField(boolean inField) {
		this.inField = inField;
		return this;
	}

	public ActionTarget setTarget(List< BattleCharacter> target) {
		this.target = new ArrayList(target);
		return this;
	}

	public ActionTarget setSelfTarget(boolean f) {
		this.selfTarget = f;
		return this;
	}

	public int getArea() {
		return action.getAreaWithEqip(user.getStatus());
	}

	public Point2D.Float centerLocation() {
		return user.getSprite().getCenter();
	}

	public boolean isInField() {
		return inField;
	}

	public BattleCharacter getUser() {
		return user;
	}

	public Action getAction() {
		return action;
	}

	public List<BattleCharacter> getTarget() {
		return target;
	}

	public boolean hasAnyTargetChara() {
		return !target.isEmpty();
	}

	public boolean isSelfEvent() {
		return selfTarget && action.getBattleEvent().stream().allMatch(p -> p.getTargetType() == TargetType.SELF);
	}

	@Override
	public Iterator<BattleCharacter> iterator() {
		return target.iterator();
	}

	public boolean isEmpty() {
		if (selfTarget) {
			return user == null || (target == null || target.isEmpty());//基本入ってる
		}
		return target == null || target.isEmpty();
	}

	@Override
	public String toString() {
		return "BattleActionTarget{" + "user=" + user + ", action=" + action + ", inField=" + inField + ", target=" + target + '}';
	}

}
