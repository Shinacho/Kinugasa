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
	private BattleCharacter user;
	//取れる行動
	private List<Action> ba;
	//状態異常関連
	private boolean stop = false;
	private boolean confu = false;
	//ユーザオペレーション要否フラグ
	//注意：PCでもユーザオペレーション不要という場合がある
	private boolean userOperation = false;

	//詠唱完了イベントのフラグ
	private boolean magicSpell = false;

	public BattleCommand(Mode mode, BattleCharacter user) {
		this.mode = mode;
		this.user = user;
		this.ba = user.getStatus().getActions();
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

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	public boolean isConfu() {
		return confu;
	}

	public void setConfu(boolean confu) {
		this.confu = confu;
	}

	public Mode getMode() {
		return mode;
	}

	public BattleCharacter getUser() {
		return user;
	}

	public Action randomAction() {
		int i = Random.randomAbsInt(ba.size());
		return ba.get(i);
	}

	public List<Action> getBattleActions() {
		return ba;
	}

	public Action getFirstBattleAction() {
		return getBattleActions().get(0);
	}

	public Action getBattleAction(EnemyAI mode) {
		return mode.getNext(user, getBattleActions());
	}

	public Action getBattleActionEx(EnemyAI mode, ActionType at) {
		List<Action> a = getBattleActions().stream().filter(p -> p.getType() != at).collect(Collectors.toList());
		return mode.getNext(user, a);
	}

	public Action getBattleActionEx(EnemyAI mode, ActionType... at) {
		List<Action> a = getBattleActions().stream().filter(p -> !Arrays.asList(at).contains(p.getType())).collect(Collectors.toList());
		return mode.getNext(user, a);
	}

	public Action getBattleActionOf(EnemyAI mode, ActionType at) {
		List<Action> a = getBattleActions().stream().filter(p -> p.getType() == at).collect(Collectors.toList());
		return mode.getNext(user, a);
	}

	public Action getBattleActionOf(EnemyAI mode, ActionType... at) {
		List<Action> a = getBattleActions().stream().filter(p -> Arrays.asList(at).contains(p.getType())).collect(Collectors.toList());
		return mode.getNext(user, a);
	}

	public List<Action> getBattleActionEx(ActionType at) {
		return getBattleActions().stream().filter(p -> p.getType() != at).collect(Collectors.toList());
	}

	public List<Action> getBattleActionEx(ActionType... at) {
		return getBattleActions().stream().filter(p -> !Arrays.asList(at).contains(p.getType())).collect(Collectors.toList());
	}

	public List<Action> getBattleActionOf(ActionType at) {
		return getBattleActions().stream().filter(p -> p.getType() == at).collect(Collectors.toList());
	}

	public List<Action> getBattleActionOf(ActionType... at) {
		return getBattleActions().stream().filter(p -> Arrays.asList(at).contains(p.getType())).collect(Collectors.toList());
	}

	public boolean hasAction(String name) {
		for (Action a : ba) {
			if (a.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMoveAction() {
		for (Action a : ba) {
			if (a.getName().equals(BattleConfig.ActionName.move)) {
				return true;
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
