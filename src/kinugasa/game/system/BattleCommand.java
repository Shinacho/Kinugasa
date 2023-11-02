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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/20_14:02:46<br>
 * @author Shinacho<br>
 */
public class BattleCommand {

	public enum Mode {
		CPU,
		PC,;

	}
	//CPUまたはPC
	private Mode mode;
	//このコマンドのユーザ
	private Actor user;
	//取れる行動
	private List<Action> ba;
	//ユーザオペレーション要否フラグ
	//注意：PCでもユーザオペレーション不要という場合がある
	private boolean userOperation = false;

	//詠唱完了イベントのフラグ
	private boolean magicSpell = false;

	private boolean userIsEnemy = false;

	public BattleCommand(Mode mode, Actor user) {
		this.mode = mode;
		this.user = user;
		userIsEnemy = user instanceof Enemy;
		this.ba = user.getStatus().getActions().asList();
		assert !ba.isEmpty() : user.getStatus().getName() + " s BA is EMPTY";
	}

	public BattleCommand setAction(List<Action> ba) {
		this.ba = ba;
		return this;
	}

	public boolean isMagicSpell() {
		return magicSpell;
	}

	public BattleCommand setMagicSpell(boolean magicSpell) {
		this.magicSpell = magicSpell;
		return this;
	}

	public boolean isUserOperation() {
		return userOperation;
	}

	public void setUserOperation(boolean userOperation) {
		this.userOperation = userOperation;
	}

	public Mode getMode() {
		return mode;
	}

	public Actor getUser() {
		return user;
	}

	public Action randomAction() {
		return Random.randomChoice(ba.stream().filter(p -> p.getType() != ActionType.行動).toList());
	}

	public List<Action> getActions() {
		return ba;
	}

	public Action getFirstBattleAction() {
		return getActions().get(0);
	}

	public List<Action> getActionEx(ActionType at) {
		return getActions().stream().filter(p -> p.getType() != at).collect(Collectors.toList());
	}

	public List<Action> getActionEx(ActionType... at) {
		return getActions().stream().filter(p -> !Arrays.asList(at).contains(p.getType())).collect(Collectors.toList());
	}

	public List<Action> getActionOf(ActionType at) {
		return getActions().stream().filter(p -> p.getType() == at).collect(Collectors.toList());
	}

	public List<Action> getActionOf(ActionType... at) {
		return getActions().stream().filter(p -> Arrays.asList(at).contains(p.getType())).collect(Collectors.toList());
	}

	public boolean hasAction(String id) {
		for (Action a : ba) {
			if (a.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMoveAction() {
		for (Action a : ba) {
			if (a.getName().equals(BattleConfig.ActionID.移動)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "BattleCommand{" + "mode=" + mode + ", user=" + user + ", ba=" + ba + '}';
	}

}
