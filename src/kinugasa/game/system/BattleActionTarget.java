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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kinugasa.game.system.BattleCharacter;
import kinugasa.object.Sprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_19:16:09<br>
 * @author Dra211<br>
 */
public class BattleActionTarget implements Iterable<BattleCharacter> {

	private BattleCharacter user;
	private CmdAction action;
	private boolean fieldTarget = false;
	private boolean inField = false;
	private List<BattleCharacter> target = new ArrayList<>();
	private boolean selfTarget = false;

	public BattleActionTarget(BattleCharacter user, CmdAction a) {
		if (user == null) {
			throw new GameSystemException("battle action result s user is null");
		}
		this.user = user;
		this.action = a;
	}

	public BattleActionTarget setFieldTarget(boolean fieldTarget) {
		this.fieldTarget = fieldTarget;
		return this;
	}

	public BattleActionTarget setInField(boolean inField) {
		this.inField = inField;
		return this;
	}

	public BattleActionTarget setTarget(List<BattleCharacter> target) {
		this.target = target;
		return this;
	}

	public BattleActionTarget setSelfTarget(boolean f) {
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

	public boolean isFieldTarget() {
		return fieldTarget;
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
		if (fieldTarget) {
			return false;
		}
		if (selfTarget) {
			return user == null || (target == null || target.isEmpty());//��{�����Ă�
		}
		return target == null || target.isEmpty();
	}

	@Override
	public String toString() {
		return "BattleActionTarget{" + "user=" + user + ", action=" + action + ", fieldTarget=" + fieldTarget + ", inField=" + inField + ", target=" + target + '}';
	}

}