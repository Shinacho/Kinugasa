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
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.system.ActionResult;
import kinugasa.game.system.AfterMoveActionMessageWindow;
import kinugasa.game.system.BattleCharacter;
import kinugasa.game.system.BattleCommand;
import kinugasa.game.system.BattleCommandMessageWindow;
import kinugasa.game.system.BattleResult;
import kinugasa.game.system.BattleResultValues;
import kinugasa.game.system.BattleSystem;
import kinugasa.game.system.BattleTargetSystem;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.Item;
import kinugasa.game.system.ItemWindow;
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
	private int prev;
	private BattleSystem battleSystem;
	private Point2D.Float playerMoveInitialLocation;
	private Sound c5, c6, turnStart;
	private BattleCommand cmd;

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
		//戦闘終了判定
		if (battleSystem.stageIs(BattleSystem.Stage.BATLE_END)) {
			BattleResultValues result = GameSystem.getInstance().battleEnd();
			System.out.println("戦闘終了：" + result);
			gls.changeTo("FIELD");
		}

		switch (stage) {
			case 0:
				//BSの処理が完了するまで待機
				if (battleSystem.stageIs(BattleSystem.Stage.WAITING_USER_CMD)) {
					stage = 1;
				}
				break;
			case 1:
				//コマンド選択（1回だけ
				cmd = battleSystem.execCmd();
				if (cmd.isUserOperation()) {
					//コマンドウインドウ表示、コマンド選択
					turnStart.load().stopAndPlay();
					stage = 2;
					break;
				} else {
					//NPCアクションが終わるまで待機
					stage = 0;
					break;
				}
			case 2:
				//コマンドウインドウ表示、コマンド選択
				//INFOが表示されている場合は操作不能
				if (!battleSystem.stageIs(BattleSystem.Stage.WAITING_USER_CMD)) {
					break;
				}
				BattleCommandMessageWindow mw = battleSystem.getMessageWindowSystem().getCommandWindow();
				if (!mw.isVisible()) {
					mw.setVisible(true);
				}
				if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw.prevType();
				} else if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw.nextType();
				}
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw.prevAction();
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw.nextAction();
				}

				//戦況図
				if (is.isPressed(GamePadButton.LB, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					battleSystem.getMessageWindowSystem().switchVisible();
				}

				//決定
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					//ターゲット選択
					ActionResult result = battleSystem.execPCAction();
					switch (result) {
						case MISS:
						case NO_TARGET:
							//何もしない（INFO表示）
							break;
						case SUCCESS:
							stage = 0;
							break;
						//制御が戻るまで待つ
						case MOVE:
							playerMoveInitialLocation = cmd.getUser().getSprite().getLocation();
							stage = 4;
							break;
						case SHOW_ITEM_WINDOW:
							stage = 5;
							break;
						case ESCAPE:
							stage = 0;
							break;
						case SHOW_STATUS:
							break;
						case TARGET_SELECT:
							prev = 2;
							stage = 3;
							break;
					}
				}
				//キャンセル（カーソルを戻す
				if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw.resetSelect();
				}

				break;
			case 3:
				//ターゲットセレクト
				BattleTargetSystem targetSystem = battleSystem.getTargetSystem();

				if (!battleSystem.stageIs(BattleSystem.Stage.WAITING_USER_CMD)) {
					break;
				}
				if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					targetSystem.prev();
				} else if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					targetSystem.next();
				}
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					targetSystem.prev();
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					targetSystem.next();
				}

				//戦況図
				if (is.isPressed(GamePadButton.LB, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					battleSystem.getMessageWindowSystem().switchVisible();
				}

				//決定
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					ActionResult result = targetSystem.collect(battleSystem.visibleCommand());
					switch (result) {
						case ESCAPE:
						case MOVE:
						case NO_TARGET:
						case SHOW_STATUS:
						case TARGET_SELECT:
						case SHOW_ITEM_WINDOW:
						case MISS:
						case SUCCESS:
							stage = 0;
							break;
					}
				}
				if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					stage = prev;
					break;
				}
				break;
			case 4:
				//移動フェーズ
				//INFOが表示されている場合は操作不能
				if (!battleSystem.stageIs(BattleSystem.Stage.WAITING_USER_CMD)) {
					break;
				}
				AfterMoveActionMessageWindow mw2 = battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow();
				assert playerMoveInitialLocation != null : "battle logic, player move initial location is null";

				BattleCharacter playerChara = cmd.getUser();

				//戦況図
				if (is.isPressed(GamePadButton.LB, InputType.SINGLE)) {
					battleSystem.getMessageWindowSystem().switchVisible();
				}
				//キャンセル
				if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
					c5.stopAndPlay();
					battleSystem.cancelPCsMove();
					stage = 2;
					break;
				}

				//決定
				//確定の場合、次のコマンドへ、攻撃の場合、ターゲット選択に移動する
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					ActionResult res = battleSystem.execPCAction();
					if (res == ActionResult.TARGET_SELECT) {
						prev = 4;
						stage = 3;
						break;
					}
					if (res == ActionResult.SUCCESS) {
						stage = 0;
						break;
					}
					if (res == ActionResult.NO_TARGET) {
						break;
					}
				}
				//移動後攻撃の判定
				int remMovPoint = (int) (playerChara.getStatus().getEffectedStatus().get("MOV").getValue()
						- playerMoveInitialLocation.distance(cmd.getUser().getSprite().getCenter()));
				//残ポイントが最大値の半分以下の場合は攻撃できない
				battleSystem.setAftedMoveAction(remMovPoint > playerChara.getStatus().getEffectedStatus().get("MOV").getValue() / 2);

				//コマンド選択
				if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw2.prevAction();
				} else if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw2.nextAction();
				}
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw2.prevAction();
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw2.nextAction();
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
				//アイテム選択
				ItemWindow mw3 = battleSystem.getMessageWindowSystem().getItemWindow();
				//コマンド
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw3.prevItem();
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					c6.load().stopAndPlay();
					mw3.nextItem();
				}
				//キャンセル
				if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
					stage = 2;
					break;
				}
				//決定
				if(is.isPressed(GamePadButton.A, InputType.SINGLE)){
					ActionResult res = battleSystem.useItem();
					if(res == ActionResult.MISS || res == ActionResult.NO_TARGET){
						//アイテム使用できなかった
						//ミス：バトル効果なし
						//ノーターゲット：範囲内に対象なし
					}
					if(res == ActionResult.SUCCESS){
						//アイテム使用できた（全体、必ず発動、装備品等
						stage = 0;
						break;
					}
					if(res == ActionResult.TARGET_SELECT){
						//アイテムターゲット選択に遷移
						stage = 6;
						break;
					}
				}
			case 6:
				//アイテムターゲット選択
				//MOVの半分の値（行って戻ってくるという設定）で計算
				

				break;
			default:
				throw new AssertionError("undefined Test2.BattleLogic s stage");
		}

	}

	@Override
	public void draw(GraphicsContext g) {
		battleSystem.draw(g);
	}

}
