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
package kinugasa.game.test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.system.BattleAction;
import kinugasa.game.system.BattleCommand;
import kinugasa.game.system.BattleCommandMessageWindow;
import kinugasa.game.system.BattleResult;
import kinugasa.game.system.BattleResultValues;
import kinugasa.game.system.BattleSystem;
import kinugasa.game.system.BattleTargetSystem;
import kinugasa.game.system.GameSystem;

/**
 *
 * @vesion 1.0.0 - 2022/11/22_6:32:48<br>
 * @author Dra211<br>
 */
public class BattleLogic extends GameLogic {

	BattleLogic(GameManager gm) {
		super("BATTLE", gm);
	}

	@Override
	public void load() {
		stage = 0;
		battleSystem = GameSystem.getInstance().getBattleSystem();
	}

	@Override
	public void dispose() {
	}

	private int stage = 0;
	private BattleSystem battleSystem;
	private Point2D.Float playerMoveInitialLocation;

	@Override
	public void update(GameTimeManager gtm) {
		battleSystem.update();
		InputState is = InputState.getInstance();

		//緊急脱出ボタン
		if (is.isPressed(GamePadButton.BACK, InputType.SINGLE)) {
			battleSystem.setBattleResultValue(new BattleResultValues(BattleResult.WIN, 123, new ArrayList<>(), "FIELD"));
			BattleResultValues result = GameSystem.getInstance().battleEnd();
			System.out.println("戦闘強制終了：" + result);
			gls.changeTo("FIELD");
		}

		switch (stage) {
			case 0:
				//状態が待機になるまで待つ
				if (battleSystem.getStage() != BattleSystem.Stage.WAITING_USER_CMD) {
					return;
				}
				stage = 1;
				break;
			case 1:
				//コマンド発生
				BattleCommand bc = battleSystem.getNextCmdAndExecNPCCmd();
				if (bc.getMode() == BattleCommand.Mode.PC) {
					stage = 2;
				} else {
					stage = 0;
				}
				break;
			case 2:
				//プレイヤー入力
				BattleCommandMessageWindow mw = battleSystem.getMessageWindowSystem().getCommandWindow();
				if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
					mw.prevType();
				} else if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
					mw.nextType();
				}
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					mw.prevAction();
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					mw.nextAction();
				}
				//戦況図
				if (is.isPressed(GamePadButton.LB, InputType.CONTINUE)) {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(false);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(false);
				} else {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(true);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(true);
				}

				//決定
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					//ターゲット選択または移動へ切替
					battleSystem.toTargetSelectOrPCMoveMode();
					stage = 3;
				}

				break;
			case 3:
				//ターゲット選択または移動かを判定（1回だけ実行
				BattleAction selected = battleSystem.getCurrentBA();
				//移動の場合
				if (selected.isMoveOnly()) {
					playerMoveInitialLocation = battleSystem.getCurrentCmd().getUser().getSprite().getLocation();
					stage = 4;
				} else {
					stage = 5;
				}
				break;
			case 4:
				//移動モード
				assert playerMoveInitialLocation != null : "battle logic, player move initial location is null";
				
				break;
			case 5:
				//ターゲット選択モード
				BattleTargetSystem targetSystem = battleSystem.getTargetSystem();

				break;

			default:
				throw new AssertionError("undefined battle logic stage");
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		battleSystem.draw(g);
	}

}
