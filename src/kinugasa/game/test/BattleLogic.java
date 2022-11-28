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
import java.util.EnumMap;
import java.util.EnumSet;
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.system.AfterMoveCommandMessageWindow;
import kinugasa.game.system.BattleAction;
import kinugasa.game.system.BattleActionStorage;
import kinugasa.game.system.BattleActionTargetParameterType;
import kinugasa.game.system.BattleActionTargetType;
import kinugasa.game.system.BattleActionType;
import kinugasa.game.system.BattleCharacter;
import kinugasa.game.system.BattleCommand;
import kinugasa.game.system.BattleCommandMessageWindow;
import kinugasa.game.system.BattleFieldSystem;
import kinugasa.game.system.BattleResult;
import kinugasa.game.system.BattleResultValues;
import kinugasa.game.system.BattleSystem;
import kinugasa.game.system.BattleTargetSystem;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.MagicBattleCommand;
import kinugasa.game.ui.Text;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundBuilder;

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
		c5 = new SoundBuilder("resource/se/効果音＿選択1.wav").builde().load();
		c6 = new SoundBuilder("resource/se/効果音＿選択2.wav").builde().load();
		turnStart = new SoundBuilder("resource/se/効果音＿バトルターン開始.wav").builde().load();
	}

	@Override
	public void dispose() {
	}

	private int stage = 0;
	private BattleSystem battleSystem;
	private Point2D.Float playerMoveInitialLocation;
	private Sound c5, c6, turnStart;
	private int remMovPoint;

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
			case -1:
				//状態が待機かつINFOの表示が終わるまで待つ
				if (!battleSystem.getMessageWindowSystem().isClosedInfoWindow()) {
					break;
				}
				if (battleSystem.getStage() != BattleSystem.Stage.WAITING_USER_CMD) {
					break;
				}
				stage = 5;
				break;
			case 0:
				//状態が待機になるまで待つ
				if (battleSystem.getStage() != BattleSystem.Stage.WAITING_USER_CMD) {
					break;
				}
				stage = 1;
				break;
			case 1:
				//コマンド発生
				BattleCommand bc = battleSystem.getNextCmdAndExecNPCCmd();
				if (bc.getMode() == BattleCommand.Mode.PC) {
					if (!battleSystem.isCantMove()) {
						//魔法詠唱中等で動けなくない場合（＝動ける場合
						turnStart.stopAndPlay();
						battleSystem.getMessageWindowSystem().getCommandWindow().resetSelect();
						BattleAction ba = battleSystem.getMessageWindowSystem().getCommandWindow().getSelected();
						battleSystem.setBattleAction(ba, ba.getAreaWithEqip(battleSystem.getCurrentCmd().getUser().getStatus()));
						battleSystem.getTargetSystem().unset();
						stage = 2;
						break;
					}
					if (bc instanceof MagicBattleCommand) {
						stage = 0;
					}
				}
				stage = 0;
				break;
			case 2:
				//プレイヤー入力
				if (!battleSystem.getMessageWindowSystem().isClosedInfoWindow()) {
					break;
				}
				BattleCommandMessageWindow mw = battleSystem.getMessageWindowSystem().getCommandWindow();
				int baa = mw.isSelected("移動")
						? (int) battleSystem.getCurrentCmd().getUser().getStatus().getEffectedStatus().get("MOV").getValue()
						: mw.getSelected().getBattleActionType() == BattleActionType.OTHER
						? 0
						: battleSystem.calcArea(mw.getSelected());
				battleSystem.setBattleAction(mw.getSelected(), baa);
				//選択
				if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw.prevType();
				} else if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw.nextType();
				}
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw.prevAction();
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					c6.stopAndPlay();
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
					c5.stopAndPlay();
					battleSystem.toTargetSelectOrPCMoveMode();
					stage = 3;
				}

				break;
			case 3:
				//ターゲット選択または移動または移動確定かを判定（1回だけ実行
				BattleAction selected = battleSystem.getCurrentBA();
				if (selected.isOnlyBatpt(BattleActionTargetParameterType.MOVE)) {
					//移動の場合
					playerMoveInitialLocation = battleSystem.getCurrentCmd().getUser().getSprite().getCenter();
					stage = 4;
				} else if (selected.isOnlyBatpt(BattleActionTargetParameterType.NONE)) {
					//移動確定
					stage = 1;
				} else {
					//範囲効果内に対象がいるか確認、いない場合はステージ2に戻る
					if (!battleSystem.getTargetSystem().hasAnyTarget()) {
						battleSystem.setNoTargetMessage();
						if (battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow().isVisible()) {
							stage = 6;
						} else {
							stage = 2;
						}
					} else {
						//敵がいる場合はターゲット選択へ
//						battleSystem.getMessageWindowSystem().setActionMessage(selected.getName() + Text.getLineSep() + "  " + selected.getDesc());
						stage = 5;
					}
				}
				break;
			case 4:
				//移動モード
				assert playerMoveInitialLocation != null : "battle logic, player move initial location is null";
				BattleCharacter playerChara = battleSystem.getCurrentCmd().getUser();

				//戦況図
				if (is.isPressed(GamePadButton.LB, InputType.CONTINUE)) {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(false);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(false);
					battleSystem.getMessageWindowSystem().getTooltipWindow().setVisible(false);
				} else {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(true);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(true);
					battleSystem.getMessageWindowSystem().getTooltipWindow().setVisible(true);
				}
				//キャンセル
				if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
					c5.stopAndPlay();
					battleSystem.getMessageWindowSystem().closeTooltipWindow();
					battleSystem.setRemMovPoint(0);
					playerChara.getSprite().setLocationByCenter(playerMoveInitialLocation);
					battleSystem.getMessageWindowSystem().closeActionWindow();
					playerChara.to(FourDirection.WEST);
					stage = 2;
					break;
				}
//				//確定
//				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
//					c5.stopAndPlay();
//					battleSystem.getMessageWindowSystem().closeTooltipWindow();
//					battleSystem.submitPlayerMove(true);
//					playerChara.to(FourDirection.WEST);
//					stage = 1;
//					break;
//				}

				//移動後攻撃の判定
				remMovPoint = (int) (playerChara.getStatus().getEffectedStatus().get("MOV").getValue()
						- playerMoveInitialLocation.distance(battleSystem.getCurrentCmd().getUser().getSprite().getCenter()));
				battleSystem.setRemMovPoint(Math.min(remMovPoint, battleSystem.getCurrentBAArea()));
				if (remMovPoint > 0) {
					battleSystem.getMessageWindowSystem().setTolltipMessage("通常攻撃可能");
				} else {
					battleSystem.getMessageWindowSystem().setTolltipMessage("通常攻撃不可");
				}
				if (is.isPressed(GamePadButton.A, InputType.SINGLE) && remMovPoint != 0) {
					//移動後攻撃選択へ遷移
					battleSystem.getMessageWindowSystem().closeTooltipWindow();
					battleSystem.getMessageWindowSystem().closeCommandWindow();
					battleSystem.submitPlayerMove();
					battleSystem.afterMoveAttackMode("確定", remMovPoint);
					battleSystem.getCurrentCmd().getUser().to(FourDirection.WEST);
					stage = 6;
					break;
				}
				//シミュレートムーブ起動して次フレームの位置取得
				KVector v = new KVector(is.getGamePadState().sticks.LEFT.getLocation(VehicleStorage.getInstance().get("WALK").getSpeed()));
				if (v.getSpeed() <= 0) {
					break;
				}
				Point2D.Float nextFrameLocation = playerChara.getSprite().simulateMoveCenterLocation(v);
				//領域判定
				if (!battleSystem.getBattleFieldSystem().getBattleFieldAllArea().contains(nextFrameLocation)) {
					break;
				}
				//障害物判定
				if (battleSystem.getBattleFieldSystem().hitObstacle(nextFrameLocation)) {
					break;
				}
				//距離判定
				if (playerChara.getStatus().getEffectedStatus().get("MOV").getValue() <= playerMoveInitialLocation.distance(nextFrameLocation)) {
					break;
				}
				//移動実行
				playerChara.getSprite().setVector(v);
				playerChara.getSprite().move();
				playerChara.to(playerChara.getSprite().getVector().round());

				break;
			case 5:
				//ターゲット選択モード
				//ターゲット選択システムは、toTargetSelectOrPCMoveModeを実行した時点で、選択したアクションが設定されている
				//ターゲット選択できるのは、ONE_ENEMY/ONE_PARTYイベントを持っている場合のみ
				BattleTargetSystem targetSystem = battleSystem.getTargetSystem();
				if (targetSystem.contains(BattleActionTargetType.ONE_ENEMY) || targetSystem.contains(BattleActionTargetType.ONE_PARTY)) {
					//ターゲット選択
					if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
						c6.stopAndPlay();
						targetSystem.prev();
					} else if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
						c6.stopAndPlay();
						targetSystem.next();
					}
				}

				//戦況図
				if (is.isPressed(GamePadButton.LB, InputType.CONTINUE)) {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(false);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(false);
				} else {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(true);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(true);
				}

				//キャンセル
				if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
					c5.stopAndPlay();
					battleSystem.updatePlayerLocation();
					battleSystem.getMessageWindowSystem().closeActionWindow();
					stage = 2;
					break;
				}
				//決定
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					c5.stopAndPlay();
					battleSystem.execPCAction();
					battleSystem.getMessageWindowSystem().getActionWindow().setVisible(true);
					stage = 0;
					break;
				}
				break;
			case 6:
				//移動後攻撃選択

				//コマンド選択
				AfterMoveCommandMessageWindow mw2 = battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow();
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw2.prevAction();
					if (mw2.getSelected().getName().equals("確定")) {
						battleSystem.setBattleAction(mw2.getSelected(), 0);
					} else {
						int area = Math.min(mw2.getSelected().getAreaWithEqip(battleSystem.getCurrentCmd().getUser().getStatus()), remMovPoint);
						battleSystem.setBattleAction(mw2.getSelected(), area);
					}
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw2.nextAction();
					if (mw2.getSelected().getName().equals("確定")) {
						battleSystem.setBattleAction(mw2.getSelected(), 0);
					} else {
						int area = Math.min(mw2.getSelected().getAreaWithEqip(battleSystem.getCurrentCmd().getUser().getStatus()), remMovPoint);
						battleSystem.setBattleAction(mw2.getSelected(), area);
					}
				}
				//戦況図
				if (is.isPressed(GamePadButton.LB, InputType.CONTINUE)) {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(false);
					battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow().setVisible(false);
				} else {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(true);
					battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow().setVisible(true);
				}

				//決定
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					//ターゲット選択または次のキャラの移動へ
					c5.stopAndPlay();
					battleSystem.toTargetSelectOrPCMoveEnd();
					stage = 3;
				}
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
