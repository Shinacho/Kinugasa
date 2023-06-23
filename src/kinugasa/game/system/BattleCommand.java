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
