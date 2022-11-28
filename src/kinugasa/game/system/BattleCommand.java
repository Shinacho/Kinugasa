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

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/20_14:02:46<br>
 * @author Dra211<br>
 */
public class BattleCommand {

	public enum Mode {
		CPU,
		PC,;

	}
	private Mode mode;
	private BattleCharacter user;
	//取れる行動
	private List<BattleAction> ba;

	protected void setBattleAction(List<BattleAction> ba) {
		this.ba = ba;
	}

	public BattleCommand(Mode mode, BattleCharacter user) {
		this.mode = mode;
		this.user = user;
		this.ba = user.getStatus().getBattleActions().asList();
		assert !ba.isEmpty() : user.getStatus().getName() + " s BA is EMPTY";
	}

	public Mode getMode() {
		return mode;
	}

	public BattleCharacter getUser() {
		return user;
	}

	public BattleAction random() {
		int i = Random.randomAbsInt(ba.size());
		return ba.get(i);
	}

	public List<BattleAction> getBattleActions() {
		return ba;
	}

	public BattleAction getFirstBattleAction() {
		return getBattleActions().get(0);
	}

	public EnemyBattleAction getNPCAction() {
		if (mode == Mode.PC) {
			throw new GameSystemException("enemyBattleAction Requested, but its PC");
		}
		assert !ba.isEmpty() : user.getStatus().getName() + "s BA is empty";
		List<EnemyBattleAction> eba = ba.stream().map(v -> (EnemyBattleAction) v).collect(Collectors.toList());
		assert !eba.isEmpty() : user.getStatus().getName() + "s EBA is empty";
		Collections.sort(eba);
		int i = 0;
		int lp = 0;
		while (true) {
			if (Random.percent(eba.get(i).getP())) {
				return eba.get(i);
			}
			i++;
			lp++;
			if (lp > 1000) {
				throw new GameSystemException("getNPCActionExMove is infinity looping");
			}
			if (i >= eba.size()) {
				i = 0;
			}
		}
	}

	//行動以外のアクションを抽選
	public EnemyBattleAction getNPCActionExMove() {
		if (mode == Mode.PC) {
			throw new GameSystemException("enemyBattleAction Requested, but its PC");
		}
		assert ba != null && !ba.isEmpty() : user.getStatus().getName() + "s BA is empty or null";
		List<EnemyBattleAction> ebaList = ba.stream().map(v -> (EnemyBattleAction) v).collect(Collectors.toList());
		assert !ebaList.isEmpty() : user.getStatus().getName() + "s EBA is empty";
		Collections.sort(ebaList);
		int i = 0;
		int lp = 0;
		while (true) {
			EnemyBattleAction eba = ebaList.get(i);
			if (eba.getEvents().stream().allMatch(p -> p.getBatpt() == BattleActionTargetParameterType.MOVE)) {
				lp++;
				if (lp > 1000) {
					throw new GameSystemException("getNPCActionExMove is infinity looping");
				}
				i++;
				if (i >= ebaList.size()) {
					i = 0;
				}
				continue;
			}
			if (Random.percent(eba.getP())) {
				return eba;
			}
			i++;
			lp++;
			if (lp > 1000) {
				throw new GameSystemException("getNPCActionExMove is infinity looping");
			}
			if (i >= ebaList.size()) {
				i = 0;
			}
		}
	}

	public EnemyBattleAction getNPCActionOf(BattleActionType type) {
		if (mode == Mode.PC) {
			throw new GameSystemException("enemyBattleAction Requested, but its PC");
		}
		assert ba != null && !ba.isEmpty() : user.getStatus().getName() + "s BA is empty or null";
		List<EnemyBattleAction> ebaList = ba.stream().map(v -> (EnemyBattleAction) v).collect(Collectors.toList());
		assert !ebaList.isEmpty() : user.getStatus().getName() + "s EBA is empty";
		if (ebaList.stream().allMatch(p -> p.getBattleActionType() != type)) {
			//持っていない場合
			return null;
		}
		Collections.sort(ebaList);
		int i = 0;
		int lp = 0;
		while (true) {
			EnemyBattleAction eba = ebaList.get(i);
			if (eba.getBattleActionType() != type) {
				lp++;
				if (lp > 1000) {
					throw new GameSystemException("getNPCActionExMove is infinity looping, ");
				}
				i++;
				if (i >= ebaList.size()) {
					i = 0;
				}
				continue;
			}
			if (eba.getEvents().stream().allMatch(p -> p.getBatpt() == BattleActionTargetParameterType.MOVE)) {
				lp++;
				if (lp > 1000) {
					throw new GameSystemException("getNPCActionExMove is infinity looping");
				}
				i++;
				if (i >= ebaList.size()) {
					i = 0;
				}
				continue;
			}
			if (Random.percent(eba.getP())) {
				return eba;
			}
			i++;
			lp++;
			if (lp > 1000) {
				throw new GameSystemException("getNPCActionExMove is infinity looping");
			}
			if (i >= ebaList.size()) {
				i = 0;
			}
		}
	}

	public int getAreaWithEqip(String actionName) {
		return user.getStatus().getBattleActionArea(actionName);
	}

	public List<BattleAction> getAll(BattleActionType type, Status user) {
		return ba.stream().filter(p -> p.getBattleActionType() == type).filter(p -> p.canDoThis(user)).collect(Collectors.toList());
	}

	public boolean hasMoveAction() {
		for (BattleAction a : ba) {
			for (BattleActionEvent e : a.getEvents()) {
				if (e.getBatpt() == BattleActionTargetParameterType.MOVE) {
					return true;
				}
			}
		}
		return false;
	}

	public Point2D.Float getSpriteCenter() {
		return getUser().getSprite().getCenter();
	}

	@Override
	public String toString() {
		return "BattleCommand{" + "mode=" + mode + ", user=" + user + ", ba=" + ba + '}';
	}

}
