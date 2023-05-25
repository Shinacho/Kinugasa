package kinugasa.game.system;

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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.system.BattleCharacter;
import kinugasa.object.BasicSprite;
import kinugasa.object.EmptySprite;
import kinugasa.object.FourDirection;
import kinugasa.object.Sprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_19:16:09<br>
 * @author Shinacho<br>
 */
public class ActionTarget implements Iterable<BattleCharacter> {

	private BattleCharacter user;
	private CmdAction action;
	private boolean inField = false;
	private List<BattleCharacter> target = new ArrayList<>();
	private boolean selfTarget = false;

	public ActionTarget(BattleCharacter user, CmdAction a) {
		if (user == null) {
			throw new GameSystemException("battle action result s user is null");
		}
		this.user = user;
		this.action = a;
	}

	public static ActionTarget instantTarget(Status user, CmdAction a) {
		return instantTarget(user, a, new Status[]{});
	}

	public static ActionTarget instantTarget(Status user, CmdAction a, List<Status> tgt) {
		boolean userIsPlayer = GameSystem.getInstance().getPartyStatus().contains(user);
		List<DummyCharacter> target = new ArrayList<>();
		for (Status s : tgt) {
			boolean f = GameSystem.getInstance().getPartyStatus().contains(s);
			target.add(new DummyCharacter(s, f));
		}

		return new ActionTarget(new DummyCharacter(user, userIsPlayer), a)
				.setTarget(target.stream().collect(Collectors.toList()));
	}

	public static ActionTarget instantTarget(Status user, CmdAction a, Status... tgt) {
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

	public CmdAction getAction() {
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
