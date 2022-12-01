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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.field4.PlayerCharacterSprite;
import kinugasa.game.field4.VehicleStorage;
import static kinugasa.game.system.ActionResult.MISS;
import static kinugasa.game.system.ActionResult.SUCCESS;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;
import kinugasa.object.Drawable;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.object.Sprite;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_20:52:15<br>
 * @author Dra211<br>
 */
public class BattleSystem implements Drawable {

	private static final BattleSystem INSTANCE = new BattleSystem();

	private BattleSystem() {
	}

	static BattleSystem getInstance() {
		return INSTANCE;
	}

	//ターン数
	private int turn = 0;
	//--------------------------------------------------------初期化・終了化
	//プレイヤ戦闘開始前位置
	private List<Point2D.Float> partyInitialLocation = new ArrayList<>();
	//プレイヤ戦闘開始前向き
	private List<FourDirection> partyInitialDir = new ArrayList<>();
	//プレイヤ初期移動目標座標
	private List<Point2D.Float> partyTargetLocationForFirstMove = new ArrayList<>();
	//勝利遷移ロジック名、敗北遷移ロジック名
	private String winLogicName, loseLogicName;
	//--------------------------------------------------------表示中・実行中
	//敵のスプライトとステータス
	private List<Enemy> enemies = new ArrayList<>();
	//このターンのバトルコマンド順序
	private LinkedList<BattleCommand> commandsOfThisTurn = new LinkedList<>();
	//このターンのバトルコマンド順序
	private LinkedHashMap<Integer, List<MagicSpell>> magics = new LinkedHashMap<>();
	//表示中バトルアクション・アニメーション
	private List<BattleActionAnimation> animation = new ArrayList<>();
	//実行中バトルアクションから生成されたアクション待機時間
	private FrameTimeCounter currentBAWaitTime;
	//行動中コマンド
	private BattleCommand currentCmd;
	//ActionMessage表示時間
	private int messageWaitTime = 66;

	//updateメソッドステージ
	public enum Stage {
		STARTUP,
		INITIAL_MOVE,
		ESCAPING,
		WAITING_USER_CMD,
		PLAYER_MOVE,
		TARGET_SELECT,
		EXECUTING_ACTION,
		SHOW_ACTION_MESSAGE,
		EXECUTING_MOVE,
		BATLE_END,
		SHOW_INFO_MSG
	}
	private Stage stage;
	//--------------------------------------------------------システム
	//メッセージウインドウシステムのインスタンス
	private BattleMessageWindowSystem messageWindowSystem;
	//ターゲット選択システムのインスタンス
	private BattleTargetSystem targetSystem;
	//バトルフィールドインスタンス
	private BattleFieldSystem battleFieldSystem;
	//状態異常マネージャ
	private ConditionManager conditionManager;
	//戦闘結果
	private BattleResultValues battleResultValue = null;
	//カレントBAのNPC残移動ポイント
	private int remMovePoint;

	//デバッグ用
	@Deprecated
	public void setBattleResultValue(BattleResultValues battleResultValue) {
		this.battleResultValue = battleResultValue;
	}

	//ステージ切替
	private void setStage(Stage next, String callMethod) {
		if (GameSystem.isDebugMode()) {
			System.out.println("BATTLE SYSTEM STAGE : " + stage + " -> " + next + " / by " + callMethod);
		}
		stage = next;
	}

	public int getTurn() {
		return turn;
	}

	public void encountInit(EncountInfo enc) {
		setStage(Stage.STARTUP, "encountInit");
		//エンカウント情報の取得
		EnemySetStorage ess = enc.getEnemySetStorage().load();
		EnemySet es = ess.get();
		//BGMの開始
		SoundStorage.getInstance().get(es.getBgmMapName()).stopAll();
		es.getBgm().load().stopAndPlay();
		//敵取得
		enemies = es.create();
		ess.dispose();
		//初期化
		GameSystem gs = GameSystem.getInstance();
		battleFieldSystem = BattleFieldSystem.getInstance();
		battleFieldSystem.init(enc.getChipAttribute());
		targetSystem = BattleTargetSystem.getInstance();
		targetSystem.init(gs.getParty(), enemies);
		messageWindowSystem = BattleMessageWindowSystem.getInstance();
		messageWindowSystem.init(gs.getPartyStatus());
		conditionManager = ConditionManager.getInstance();

		//出現MSG設定用マップ
		Map<String, Long> enemyNum
				= enemies.stream().collect(Collectors.groupingBy(Enemy::getId, Collectors.counting()));
		//出現MSG設定
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Long> e : enemyNum.entrySet()) {
			sb.append(e.getKey()).append(I18N.translate("WAS")).append(e.getValue()).append(I18N.translate("APPEARANCE")).append(Text.getLineSep());
		}
		messageWindowSystem.setActionMessage(sb.toString(), Integer.MAX_VALUE);

		//リセット
		currentCmd = null;
		currentBAWaitTime = null;
		commandsOfThisTurn.clear();
		turn = 0;
		animation.clear();
		targetSystem.unsetPCsTarget();

		//敵の配置
		putEnemy();

		//味方の配置
		putParty();
		assert partyTargetLocationForFirstMove.size() == gs.getParty().size() : "initial move target is missmatch";
		setStage(Stage.INITIAL_MOVE, "encountInit");
	}

	private void putParty() {
		GameSystem gs = GameSystem.getInstance();
		partyInitialDir.clear();
		partyInitialLocation.clear();
		partyTargetLocationForFirstMove.clear();

		//初期位置初期向き退避
		List<PlayerCharacterSprite> partySprite = gs.getPartySprite();
		List<Status> partyStatus = gs.getPartyStatus();
		for (BasicSprite s : partySprite) {
			partyInitialDir.add(s.getVector().round());
			partyInitialLocation.add(s.getLocation());
		}

		int size = partySprite.get(0).getImageHeight();
		//配置
		float y = battleFieldSystem.getPartyArea().y + battleFieldSystem.getPartyArea().height / (partySprite.size() + 1) - size;
		for (int i = 0; i < partySprite.size(); i++) {
			float x = partyStatus.get(i).getPartyLocation() == PartyLocation.FRONT
					? battleFieldSystem.getPartyArea().x
					: battleFieldSystem.getPartyArea().x + battleFieldSystem.getPartyArea().width - size;
			partyTargetLocationForFirstMove.add(new Point2D.Float(x, y));
			partySprite.get(i).setLocation(x + 200, y);
			partySprite.get(i).to(FourDirection.WEST);
			partySprite.get(i).setVector(new KVector(KVector.WEST, VehicleStorage.getInstance().get(BattleConfig.initialPCMoveVehicleKey).getSpeed()));
			size = partySprite.get(i).getImageHeight();
			y += size * 2;
		}
	}

	private void putEnemy() {
		List<Sprite> checkList = new ArrayList<>();
		for (Enemy e : enemies) {
			float w = e.getSprite().getWidth();
			float h = e.getSprite().getHeight();
			L2:
			do {
				e.getSprite().setLocation(Random.randomLocation(battleFieldSystem.getEnemytArea(), w, h));

				boolean hit = false;
				for (Sprite ee : checkList) {
					hit |= e.getSprite().hit(ee);
				}
				for (Sprite os : battleFieldSystem.getObstacle()) {
					hit |= e.getSprite().hit(os);
				}
				if (!hit) {
					break L2;
				}
			} while (true);
			checkList.add(e.getSprite());
		}

		Collections.sort(enemies, (Enemy o1, Enemy o2) -> (int) (o1.getSprite().getY() - o2.getSprite().getY()));
	}

	private List<BattleCharacter> getAllChara() {
		List<BattleCharacter> result = new ArrayList<>();
		result.addAll(enemies);
		result.addAll(GameSystem.getInstance().getParty());
		return result;
	}

	void turnStart() {
		turn++;
		if (GameSystem.isDebugMode()) {
			System.out.println("-----------------TURN START-----------------");
		}
		//このターンのバトルコマンドを作成
		List<BattleCharacter> list = getAllChara();
		SpeedCalcModelStorage.getInstance().getCurrent().sort(list);

		//行動順にバトルコマンドを格納
		assert commandsOfThisTurn.isEmpty() : "turnStart:cmd is not empty";
		for (BattleCharacter c : list) {
			BattleCommand.Mode mode = c.isPlayer() ? BattleCommand.Mode.PC : BattleCommand.Mode.CPU;
			commandsOfThisTurn.add(new BattleCommand(mode, c));
		}

		//このターンの魔法詠唱完了イベントをランダムな位置に割り込ませる
		List<MagicSpell> ms = magics.get(turn);
		if (ms != null) {
			if (!ms.isEmpty()) {
				//commandsOfThisTurn
				for (MagicSpell s : ms) {
					//魔法実行イベントをランダムな位置に割り込ませる
					BattleCommand bc = new MagicBattleCommand(s, s.getMagic());
					int idx = Random.randomAbsInt(commandsOfThisTurn.size());
					//割り込ませたユーザの通常アクションを破棄する
					BattleCommand remove = null;
					for (BattleCommand c : commandsOfThisTurn) {
						if (c.getUser().equals(bc.getUser())) {
							remove = c;
						}
					}
					if (remove != null) {
						commandsOfThisTurn.remove(remove);
					}
					//削除してから割り込み実行
					commandsOfThisTurn.add(idx, bc);
				}
				//詠唱中リストからこのターンのイベントを削除
				magics.remove(turn);
			}
		}
		updateCondition();

		//状態異常の効果時間を引く
		enemies.stream().map(p -> p.getStatus()).forEach(p -> p.update());
		GameSystem.getInstance().getPartyStatus().forEach(p -> p.update());

		//このターン行動可否をコマンドに設定
		for (BattleCommand cmd : commandsOfThisTurn) {
			if (cmd.getUser().getStatus().isConfu()) {
				cmd.setConfu(true);
			}
			if (!cmd.getUser().getStatus().canMoveThisTurn()) {
				cmd.setStop(true);
			}
		}

		setStage(Stage.INITIAL_MOVE, "TURN_START");
	}

	private void updateCondition() {
		//HPが0になったときなどの状態異常を付与する
		conditionManager.setCondition(GameSystem.getInstance().getPartyStatus());
		conditionManager.setCondition(enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList()));
		//スプライトの非表示化処理
		//アンターゲットコンディション発生中のユーザによるコマンドを除去
		List<BattleCommand> remove = new ArrayList<>();
		for (BattleCommand cmd : commandsOfThisTurn) {
			if (cmd.getUser().getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
				remove.add(cmd);
				cmd.getUser().getSprite().setVisible(false);
			}
		}
		commandsOfThisTurn.removeAll(remove);

	}

	//
	//--------------------------------------------------------------------------
	//
	private boolean end = false;

	void endBattle() {
		//味方の配置の初期化
		List<PlayerCharacterSprite> partySprite = GameSystem.getInstance().getPartySprite();
		for (int i = 0; i < partySprite.size(); i++) {
			partySprite.get(i).to(partyInitialDir.get(i));
			partySprite.get(i).setLocation(partyInitialLocation.get(i));
		}
		//敵番号の初期化
		EnemyBlueprint.initEnemyNoMap();
		//逃げたコンディションで非表示になっている場合表示する
		for (PlayerCharacter pc : GameSystem.getInstance().getParty()) {
			//逃げた後死亡、死亡した後逃げるはできないので、これで問題ないはず
			if (pc.getStatus().hasCondition(BattleConfig.escapedConditionName)) {
				pc.getSprite().setVisible(true);
				//逃げたコンディションを外す
				pc.getStatus().removeCondition(BattleConfig.escapedConditionName);
			}
		}
		end = true;
	}

	//isEndBattle→endBattleの順で呼び出すこと
	public boolean isEndBattle() {
		return end;
	}

	BattleResultValues getBattleResultValue() {
		if (!end) {
			throw new GameSystemException("this battle is end not yet.");
		}
		assert battleResultValue != null : "battle is end, but result is null";
		return battleResultValue;
	}

	//次のコマンドを取得。NPCまたはPC。NPCの場合は自動実行。魔法詠唱イベントも自動実行。
	public BattleCommand execCmd() {
		//すべてのコマンドを実行したら次のターンを開始
		if (commandsOfThisTurn.isEmpty()) {
			turnStart();
		}
		currentCmd = commandsOfThisTurn.getFirst();
		commandsOfThisTurn.removeFirst();

		//ターゲットシステム初期化
		targetSystem.unsetPCsTarget();
		currentBAWaitTime = null;

		BattleCharacter user = currentCmd.getUser();
		//アンターゲット状態異常の場合、メッセージ出さずに次に送る
		if (user.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
			if (GameSystem.isDebugMode()) {
				System.out.println(user.getStatus().getName() + " is bad condition");
			}
			return execCmd();
		}
		//防御または回避中の場合、そのフラグを外す
		if (user.getStatus().hasCondition(BattleConfig.defenceConditionName)) {
			user.getStatus().removeCondition(BattleConfig.defenceConditionName);
		}
		if (user.getStatus().hasCondition(BattleConfig.avoidanceConditionName)) {
			user.getStatus().removeCondition(BattleConfig.avoidanceConditionName);
		}

		//魔法詠唱完了イベントの場合、PCでもNPCでも自動実行、（詠唱中コンディションを外す
		if (currentCmd instanceof MagicBattleCommand) {
			BattleAction ba = currentCmd.getFirstBattleAction();
			//現状でのターゲットを取得
			List<BattleCharacter> target = targetSystem.getMagicTarget(((MagicBattleCommand) currentCmd).getMagicSpell());
			//ターゲットがいない場合、詠唱失敗のメッセージ出す
			if (target.isEmpty()) {
				//フィールドイベントの場合、ターゲットが入っていないが、詠唱成功させる
				if (!ba.isOnlyBatt(BattleActionTargetType.FIELD)) {
					//アニメーションは追加しないが、詠唱中フラグは外す
					List<BattleActionResult> result = ba.exec(GameSystem.getInstance(), currentCmd.getUser(), target);
					setActionMessage(user, ba, target, result);
					setActionAnimation(user, ba, target, result);
					currentCmd.getUser().getStatus().removeCondition(BattleConfig.spellingConditionName);
					currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
					setStage(Stage.ESCAPING, "getNextCmdAndExecNPCCmd");
					return currentCmd;
				}
				//ターゲット不在
				//アニメーションは追加しないが、詠唱中フラグは外す
				StringBuilder s = new StringBuilder();
				s.append(currentCmd.getUser().getStatus().getName());
				s.append(I18N.translate("S"));
				s.append(currentCmd.getFirstBattleAction().getName());
				s.append(I18N.translate("ISFAILED"));
				messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
				currentCmd.getUser().getStatus().removeCondition(BattleConfig.spellingConditionName);
				setStage(Stage.ESCAPING, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			}
			//現状のMPで詠唱できるか確認
			//対価が支払えない場合、空振りさせる
			Map<StatusKey, Integer> damage = ba.selfDamage(user.getStatus());
			//ダメージを合算
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//ダメージがあって、0の項目がある場合、対価を支払えないため空振り
			//この魔法の消費項目を取得
			List<StatusKey> shortageKey = new ArrayList<>();
			for (BattleActionEvent e : ba.getEvents().stream().filter(p -> p.getBatt() == BattleActionTargetType.SELF).collect(Collectors.toList())) {
				shortageKey.add(StatusKeyStorage.getInstance().get(e.getTargetName()));
			}
			if (!damage.isEmpty() && simulateDamage.isZero(false, shortageKey)) {
				//対象項目で1つでも0の項目があったら空振り
				StringBuilder s = new StringBuilder();
				s.append(user.getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(ba.getName());
				s.append(I18N.translate("SPELL_END"));
				s.append(Text.getLineSep());
				s.append(I18N.translate("BUT"));
				s.append(simulateDamage.getZeroStatus().stream().filter(p -> damage.containsKey(p)).collect(Collectors.toList()).toString());
				s.append(I18N.translate("WAS"));
				s.append(I18N.translate("SHORTAGE"));
				messageWindowSystem.setInfoMessage(s.toString());
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeActionWindow();
				messageWindowSystem.closeTooltipWindow();
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return currentCmd;
			}
			//詠唱成功したらアニメーションを追加してEXEC_ACTIONに入る
			List<BattleActionResult> result = ba.exec(GameSystem.getInstance(), currentCmd.getUser(), target);
			setActionMessage(user, ba, target, result);
			setActionAnimation(user, ba, target, result);
			currentCmd.getUser().getStatus().removeCondition(BattleConfig.spellingConditionName);
			currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
			messageWindowSystem.closeTooltipWindow();
			messageWindowSystem.closeAfterMoveCommandWindow();
			messageWindowSystem.closeCommandWindow();
			setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
			return currentCmd;
		}

		//状態異常で動けないときスキップ（メッセージは出す
		if (currentCmd.isStop()) {
			currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
			StringBuilder s = new StringBuilder();
			s.append(currentCmd.getUser().getStatus().getName());
			s.append(I18N.translate("IS"));
			s.append(currentCmd.getUser().getStatus().moveStopDesc().getKey().getDesc());
			messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
			setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
			return currentCmd;
		}

		//混乱で動けないときは、停止またはバトルアクションを適当に取得して自動実行する
		if (currentCmd.isConfu()) {
			if (Random.percent(BattleConfig.conguStopP)) {
				//動けない
				currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
				StringBuilder s = new StringBuilder();
				s.append(currentCmd.getUser().getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(I18N.translate("CONFU_STOP"));
				messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			} else {
				//動けるが混乱
				BattleAction ba = currentCmd.getRandom();
				currentBAWaitTime = ba.createWaitTime();
				execAction(ba, false);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			}
		}

		//NPC
		if (currentCmd.getMode() == BattleCommand.Mode.CPU) {
			//NPCアクションを実行、この中でステージも変わる
			messageWindowSystem.closeCommandWindow();
			execAction(currentCmd.getNPCActionExMove(), false);
			return currentCmd;
		}
		//PCの行動可能
		//カレントコマンド内容をコマンドウインドウに表示、その他ウインドウは閉じる
		messageWindowSystem.getCommandWindow().setCmd(currentCmd);
		messageWindowSystem.getCommandWindow().resetSelect();
		messageWindowSystem.getCommandWindow().setVisible(true);
		messageWindowSystem.closeActionWindow();
		messageWindowSystem.closeAfterMoveCommandWindow();
		messageWindowSystem.closeInfoWindow();
		messageWindowSystem.closeTooltipWindow();

		//ターゲットシステムを初期選択で初期化
		targetSystem.setPCsTarget(messageWindowSystem.getCommandWindow().getSelected(), currentCmd.getUser());

		//ユーザオペレーション要否フラグをONに設定
		currentCmd.setUserOperation(true);

		if (GameSystem.isDebugMode()) {
			System.out.println("getNextCmdAndExecNPCCmd : return PC_CMD" + currentCmd);
		}
		setStage(Stage.WAITING_USER_CMD, "getNextCmdAndExecNPCCmd");
		return currentCmd;
	}

	public ActionResult execPCAction() {
		//コマンドウインドウまたは移動後攻撃ウインドウからアクションを取得
		if (messageWindowSystem.isVisibleCommand()) {
			BattleAction ba = messageWindowSystem.getCommandWindow().getSelected();
			return execAction(ba, false);
		}
		BattleAction ba = messageWindowSystem.getAfterMoveCommandWindow().getSelected();
		return execAction(ba, false);
	}

	public void cancelPCsMove() {
		currentCmd.getUser().getSprite().setLocation(pcMoveInitialLocation);
		currentCmd.getUser().unsetTarget();
		currentCmd.getUser().to(FourDirection.WEST);
		messageWindowSystem.getAfterMoveCommandWindow().setVisible(false);
		messageWindowSystem.getCommandWindow().setVisible(true);
		messageWindowSystem.getCommandWindow().setCmd(currentCmd);
	}

	public void submitePCsMove(boolean aftedMoveAction) {
		currentCmd.getUser().to(FourDirection.WEST);
		currentCmd.getUser().unsetTarget();
		messageWindowSystem.getAfterMoveCommandWindow().setVisible(false);
		messageWindowSystem.getCommandWindow().setVisible(true);
		messageWindowSystem.getCommandWindow().setCmd(currentCmd);
	}

	private boolean prevAttackOK = false;

	public void setAftedMoveAction(boolean attackOK) {
		if (!messageWindowSystem.isVisibleAfterMoveCommand()) {
			throw new GameSystemException("after move window is not visible");
		}
		if (prevAttackOK == attackOK) {
			return;
		}
		prevAttackOK = attackOK;
		List<BattleAction> afterMoveActions = new ArrayList<>();
		if (attackOK) {
			afterMoveActions.addAll(currentCmd.getBattleActions().stream().filter(p -> p.getBattleActionType() == BattleActionType.ATTACK).collect(Collectors.toList()));
		}
		afterMoveActions.add(BattleActionStorage.getInstance().get(BattleConfig.ActionName.commit));
		Collections.sort(afterMoveActions);
		if (!attackOK) {
			targetSystem.getAfterMoveActionArea().setVisible(false);
			targetSystem.getAfterMoveActionArea().setArea(0);
		} else {
			targetSystem.getAfterMoveActionArea().setVisible(true);
		}

		messageWindowSystem.getAfterMoveCommandWindow().setActions(afterMoveActions);
	}
	private Point2D.Float pcMoveInitialLocation;

	void setAfterMoveArea(BattleAction ba) {
		if (ba.getBattleActionType() != BattleActionType.ATTACK) {
			targetSystem.getAfterMoveActionArea().setVisible(false);
			return;
		}
		int a = currentCmd.getAreaWithEqip(ba.getName());
		targetSystem.setAfterMoveActionArea(currentCmd.getUser().getSprite().getCenter(), a);
		targetSystem.updatePCsTarget(ba);
	}

	ActionResult execAction(BattleAction ba, boolean targetSystemCalled) {
		//PC,NPC問わず選択されたアクションを実行する。

		//メッセージウインドウやアニメーションの処理も行う
		BattleCharacter user = currentCmd.getUser();
		//ターゲットシステムからターゲットを取得
		List<BattleCharacter> target;
		if (currentCmd instanceof MagicBattleCommand) {
			//挿入された魔法イベントの場合は自動で対象を取得
			target = targetSystem.getMagicTarget(((MagicBattleCommand) currentCmd).getMagicSpell());
		} else {
			if (currentCmd.getMode() == BattleCommand.Mode.PC) {
				target = targetSystem.getSelected();
			} else {
				target = targetSystem.getNPCTarget(ba, user);
			}
		}
		messageWindowSystem.closeTooltipWindow();
		messageWindowSystem.closeItemWindow();
		user.unsetTarget();
		//PCの場合は特殊なアクションに入る
		if (currentCmd.isUserOperation() & !targetSystemCalled) {
			//確定
			if (ba.getName().equals(BattleConfig.ActionName.commit)) {
				user.unsetTarget();
				return SUCCESS;
			}
			//移動
			if (ba.getName().equals(BattleConfig.ActionName.move)) {
				messageWindowSystem.closeActionWindow();
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.closeInfoWindow();
				messageWindowSystem.closeTooltipWindow();
				List<BattleAction> afterMoveActions = currentCmd.getBattleActions().stream().filter(p -> p.getBattleActionType() == BattleActionType.ATTACK).collect(Collectors.toList());
				afterMoveActions.add(BattleActionStorage.getInstance().get(BattleConfig.ActionName.commit));
				Collections.sort(afterMoveActions);
				messageWindowSystem.setAfterMoveCommand(afterMoveActions);
				pcMoveInitialLocation = user.getSprite().getLocation();
				setAfterMoveArea(messageWindowSystem.getAfterMoveCommandWindow().getSelected());
				return ActionResult.MOVE;
			}
			//アイテム使用
			if (ba.getName().equals(BattleConfig.ActionName.itemUse)) {
				//アイテムバッグが空のときはメッセージ出して終了
				if (user.getStatus().getItemBag().isEmpty()) {
					messageWindowSystem.setInfoMessage(I18N.translate("NO_ITEM"));
					setStage(Stage.SHOW_INFO_MSG, "execAction");
					return ActionResult.MISS;
				}
				messageWindowSystem.getItemWindow().setBag(user.getStatus());
				messageWindowSystem.getItemWindow().setVisible(true);
				messageWindowSystem.closeActionWindow();
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeInfoWindow();
				messageWindowSystem.closeTooltipWindow();
				return ActionResult.SHOW_ITEM_WINDOW;
			}
			//防御
			if (ba.getName().equals(BattleConfig.ActionName.defence)) {
				StringBuilder s = new StringBuilder();
				s.append(user.getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(ba.getName());
				s.append(I18N.translate("DEFENCE"));
				messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeInfoWindow();
				user.getStatus().addCondition(BattleConfig.defenceConditionName);
				setStage(Stage.SHOW_ACTION_MESSAGE, "execAction");
				return ActionResult.SUCCESS;
			}
			//回避
			if (ba.getName().equals(BattleConfig.ActionName.avoidance)) {
				StringBuilder s = new StringBuilder();
				s.append(user.getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(ba.getName());
				s.append(I18N.translate("AVOIDANCE"));
				messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeInfoWindow();
				user.getStatus().addCondition(BattleConfig.avoidanceConditionName);
				setStage(Stage.SHOW_ACTION_MESSAGE, "execAction");
				return ActionResult.SUCCESS;
			}
			//状態
			if (ba.getName().equals(BattleConfig.ActionName.status)) {
				return ActionResult.SHOW_STATUS;
			}
			//逃げる
			if (ba.getName().equals(BattleConfig.ActionName.escape)) {
				//逃げるコマンドの成否判定
				//前提として、移動ポイント内に境界ななければならない
				Point2D.Float w, e;
				int movPoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue();
				e = (Point2D.Float) user.getSprite().getCenter().clone();
				e.x += movPoint;
				w = (Point2D.Float) user.getSprite().getCenter().clone();
				w.x -= movPoint;
				if (!battleFieldSystem.getBattleFieldAllArea().contains(e)) {
					//逃走成功（→）
					user.getStatus().addCondition(ConditionValueStorage.getInstance().get(BattleConfig.escapedConditionName).getKey());
					user.setTargetLocation(e, 0);
					user.to(FourDirection.EAST);
					messageWindowSystem.closeCommandWindow();
					messageWindowSystem.closeInfoWindow();
					messageWindowSystem.closeAfterMoveCommandWindow();
					messageWindowSystem.setActionMessage(user.getStatus().getName() + I18N.translate("ISESCAPE"), messageWaitTime);
					messageWindowSystem.getStatusWindows().getMw().get(((PlayerCharacter) user).getOrder()).setVisible(false);
					setStage(Stage.ESCAPING, "execAction");
					return ActionResult.ESCAPE;
				}
				if (!battleFieldSystem.getBattleFieldAllArea().contains(w)) {
					//逃走成功（←）
					user.getStatus().addCondition(ConditionValueStorage.getInstance().get(BattleConfig.escapedConditionName).getKey());
					user.setTargetLocation(w, 0);
					user.to(FourDirection.WEST);
					messageWindowSystem.closeCommandWindow();
					messageWindowSystem.closeInfoWindow();
					messageWindowSystem.closeAfterMoveCommandWindow();
					messageWindowSystem.setActionMessage(user.getStatus().getName() + I18N.translate("ISESCAPE"), messageWaitTime);
					messageWindowSystem.getStatusWindows().getMw().get(((PlayerCharacter) user).getOrder()).setVisible(false);
					setStage(Stage.ESCAPING, "execAction");
					return ActionResult.ESCAPE;
				}
				//逃げられない
				messageWindowSystem.setInfoMessage(I18N.translate("CANT_ESCAPE"));
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return ActionResult.MISS;
			}
			//攻撃と特殊攻撃と魔法・・・ターゲットがいればターゲット選択に入る
			if (ba.getBattleActionType() == BattleActionType.ATTACK
					|| ba.getBattleActionType() == BattleActionType.SPECIAL_ATTACK
					|| ba.getBattleActionType() == BattleActionType.MAGIC) {
				if (target.isEmpty() || (target.size() == 1 && target.get(0) == user)) {
					//ターゲットなし				
					messageWindowSystem.setInfoMessage(I18N.translate("NO_TARGET"));
					messageWindowSystem.closeActionWindow();
					messageWindowSystem.closeTooltipWindow();
					setStage(Stage.SHOW_INFO_MSG, "execAction");
					return ActionResult.NO_TARGET;
				}
				targetSystem.getAfterMoveActionArea().setVisible(false);
				messageWindowSystem.setTolltipMessage(I18N.translate("TARGET_SELECT"));
				return ActionResult.TARGET_SELECT;
			}
			//その他行動は実装エラー
			if (ba.getBattleActionType() != BattleActionType.MAGIC) {
				throw new GameSystemException("undefined PCs action : " + ba);
			}
		}

		//魔法詠唱の場合は魔法詠唱中リストに追加してメッセージを出して終了
		if (ba.getBattleActionType() == BattleActionType.MAGIC) {
			//対価が支払えない場合、空振りさせる
			Map<StatusKey, Integer> damage = ba.selfDamage(user.getStatus());
			//ダメージを合算
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//ダメージがあって、0の項目がある場合、対価を支払えないため空振り
			//この魔法の消費項目を取得
			List<StatusKey> shortageKey = new ArrayList<>();
			for (BattleActionEvent e : ba.getEvents().stream().filter(p -> p.getBatt() == BattleActionTargetType.SELF).collect(Collectors.toList())) {
				shortageKey.add(StatusKeyStorage.getInstance().get(e.getTargetName()));
			}
			if (!damage.isEmpty() && simulateDamage.isZero(false, shortageKey)) {
				//対象項目で1つでも0の項目があったら空振り
				StringBuilder s = new StringBuilder();
				s.append(user.getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(ba.getName());
				s.append(I18N.translate("SPELL_START"));
				s.append(Text.getLineSep());
				s.append(I18N.translate("BUT"));
				s.append(simulateDamage.getZeroStatus().stream().filter(p -> damage.containsKey(p)).collect(Collectors.toList()).toString());
				s.append(I18N.translate("WAS"));
				s.append(I18N.translate("SHORTAGE"));
				messageWindowSystem.setInfoMessage(s.toString());
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeActionWindow();
				messageWindowSystem.closeTooltipWindow();
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return ActionResult.NO_TARGET;
			}
			//SELFのみアクションの場合は実行可能

			//ターゲットがいない場合で、NPCで移動可能な場合は移動実行
			if (!user.isPlayer() && (target.isEmpty() || (target.size() == 1 && target.get(0).equals(user)))) {
				//移動アクションを持っている場合移動実行
				if (user.getStatus().hasAction(BattleActionTargetParameterType.MOVE)) {
					BattleCharacter targetChara = targetSystem.nearPlayer(user.getSprite().getCenter());
					user.setTargetLocation(targetChara.getSprite().getCenter(), ba.getAreaWithEqip(user.getStatus()));
					StringBuilder s = new StringBuilder();
					s.append(user.getStatus().getName());
					s.append(I18N.translate("ISMOVE"));
					messageWindowSystem.setActionMessage(s.toString(), Integer.MAX_VALUE);
					messageWindowSystem.closeCommandWindow();
					messageWindowSystem.closeAfterMoveCommandWindow();
					messageWindowSystem.closeInfoWindow();
					messageWindowSystem.closeTooltipWindow();
					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue();
					currentBAWaitTime = ba.createWaitTime();
					setStage(Stage.EXECUTING_MOVE, winLogicName);
					return ActionResult.SUCCESS;
				} else {
					//移動アクションを持っておらず、ターゲットもいない場合は何もしない
					return ActionResult.SUCCESS;
				}
			}
			//詠唱時間0ターンの場合でターゲットがいる場合は即時実行
			if (ba.getSpellTime() == 0 && !target.isEmpty()) {
				//ターゲット存在
				List<BattleActionResult> result = ba.exec(GameSystem.getInstance(), user, target);
				updateCondition();
				//メッセージウインドウ設定
				setActionMessage(user, ba, target, result);
				//アニメーション設定
				setActionAnimation(user, ba, target, result);
				//アニメーション待ちに遷移
				currentBAWaitTime = ba.createWaitTime();
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeInfoWindow();
				messageWindowSystem.closeTooltipWindow();
				setStage(Stage.EXECUTING_ACTION, "execAction");
				if (user.isPlayer() && !targetSystemCalled) {
					messageWindowSystem.setTolltipMessage(I18N.translate("TARGET_SELECT"));
					return ActionResult.SUCCESS;
				}
				return ActionResult.TARGET_SELECT;
			}
			//詠唱可能なので、ターゲット選択へ
			int t = ba.getSpellTime() + turn;
			addSpelling(user, ba, t);
			//詠唱中コンディションを追加
			user.getStatus().addCondition(BattleConfig.spellingConditionName);
			//詠唱したメッセージを設定
			StringBuilder s = new StringBuilder();
			s.append(user.getStatus().getName());
			s.append(I18N.translate("IS"));
			s.append(ba.getName());
			s.append(I18N.translate("SPELL_START"));
			messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
			messageWindowSystem.closeCommandWindow();
			messageWindowSystem.closeAfterMoveCommandWindow();
			messageWindowSystem.closeInfoWindow();
			messageWindowSystem.closeTooltipWindow();
			setStage(Stage.SHOW_ACTION_MESSAGE, "execAction");
			return ActionResult.SUCCESS;
		}
		//※フィールドイベントの場合、ターゲットは空で帰ってくる

		//ターゲット不在の判定
		//NPCアクションでターゲット不在の場合
		if (!user.isPlayer() && target.isEmpty() || (target.size() == 1 && target.get(0).equals(user))) {
			//移動アクションを持っている場合移動実行
			if (user.getStatus().hasAction(BattleActionTargetParameterType.MOVE)) {
				BattleCharacter targetChara = targetSystem.nearPlayer(user.getSprite().getCenter());
				user.setTargetLocation(targetChara.getSprite().getCenter(), ba.getAreaWithEqip(user.getStatus()));
				StringBuilder s = new StringBuilder();
				s.append(user.getStatus().getName());
				s.append(I18N.translate("ISMOVE"));
				messageWindowSystem.setActionMessage(s.toString(), Integer.MAX_VALUE);
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeInfoWindow();
				messageWindowSystem.closeTooltipWindow();
				remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue();
				currentBAWaitTime = ba.createWaitTime();
				setStage(Stage.EXECUTING_MOVE, winLogicName);
				return ActionResult.SUCCESS;
			}
			//移動アクションを持っていない場合は何もせず終了
			return ActionResult.NO_TARGET;
		}

		if (target.isEmpty() || (target.size() == 1 && target.get(0).equals(user))) {
			//ターゲット不在で空振り
			//フィールドイベントを含んでいる場合はターゲット不在でもOK
			if (!ba.hasBatt(BattleActionTargetType.FIELD)) {
				messageWindowSystem.setInfoMessage(I18N.translate("NO_TARGET"));
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeActionWindow();
				messageWindowSystem.closeTooltipWindow();
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return ActionResult.NO_TARGET;
			}
		}
		//ターゲット存在
		List<BattleActionResult> result = ba.exec(GameSystem.getInstance(), user, target);
		updateCondition();
		//メッセージウインドウ設定
		setActionMessage(user, ba, target, result);
		//アニメーション設定
		setActionAnimation(user, ba, target, result);
		//アニメーション待ちに遷移
		currentBAWaitTime = ba.createWaitTime();
		messageWindowSystem.closeCommandWindow();
		messageWindowSystem.closeAfterMoveCommandWindow();
		messageWindowSystem.closeInfoWindow();
		messageWindowSystem.closeTooltipWindow();
		setStage(Stage.EXECUTING_ACTION, "execAction");
		return ActionResult.SUCCESS;
	}
	
	private Item selectedItem;
	public ActionResult useItem(){
		selectedItem = messageWindowSystem.getItemWindow().getSelected();
		
		return null;
		
	}

	public CommandWindow visibleCommand() {
		return messageWindowSystem.isVisibleCommand() ? messageWindowSystem.getCommandWindow() : messageWindowSystem.getAfterMoveCommandWindow();
	}

	private void addSpelling(BattleCharacter user, BattleAction ba, int turn) {
		if (magics.containsKey(turn)) {
			magics.get(turn).add(new MagicSpell(user.isPlayer() ? BattleCommand.Mode.PC : BattleCommand.Mode.CPU, user, ba));
		} else {
			List<MagicSpell> list = new ArrayList();
			list.add(new MagicSpell(user.isPlayer() ? BattleCommand.Mode.PC : BattleCommand.Mode.CPU, user, ba));
			magics.put(turn, list);
		}
	}

	//resultに基づくメッセージをアクションウインドウに設定する
	private void setActionMessage(BattleCharacter user, BattleAction action, List<BattleCharacter> target, List<BattleActionResult> result) {
		messageWindowSystem.closeTooltipWindow();
		StringBuilder s = new StringBuilder();
		final int LF = BattleConfig.actionWindowLF;
		final int LINE = 7;

		s.append(user.getStatus().getName());
		s.append(I18N.translate("S"));
		s.append("[ ");
		s.append(action.getName());
		s.append(" ] ! !");
		s.append(Text.getLineSep());

		//魔法で、SELFのみじゃないのにターゲットSELFのみの場合、ターゲットなしを表示
		if (action.getBattleActionType() == BattleActionType.MAGIC) {
			boolean isSelf = action.isOnlyBatt(BattleActionTargetType.SELF);
			boolean targetIsOnlySelf = target.size() == 1 && user.equals(target.get(0));
			if (isSelf && targetIsOnlySelf) {
				s.append(I18N.translate("BUT"));
				s.append(I18N.translate("NO_TARGET"));
				messageWindowSystem.setActionMessage(s.toString(), action.createWaitTime().getCurrentTime());
				return;
			}
		}

		for (int k = 0; k < action.getEvents().size(); k++) {
			BattleActionEvent e = action.getEvents().get(k);
			BattleActionResult res = result.get(k);

			if (e.getBatt() == BattleActionTargetType.SELF) {

				continue;
			} else if (e.getBatt() == BattleActionTargetType.FIELD) {
				//フィールドの場合、フィールドに効果を与えた旨を表示
				switch (res) {
					case SUCCESS:
						s.append(I18N.translate("FIELD"));
						s.append(I18N.translate("IS"));
						ConditionValue val = FieldConditionValueStorage.getInstance().get(e.getTargetName());
						assert val != null : "field condition name is missmatch : " + e.getTargetName();
						s.append(val.getKey().getDesc());
						s.append(Text.getLineSep());
					case MISS:
						s.append(I18N.translate("OTHER"));
						s.append(I18N.translate("ISFAILED"));
						s.append(Text.getLineSep());
					default:
						System.out.println("kinugasa.game.system.BattleSystem.setActionMessage() default message");
						break;
				}
			} else {
				//それ以外の場合はメッセージを設定
				for (int i = 0, j = 0, line = 0; i < target.size(); i++, j++) {
					if (target.get(i).equals(currentCmd.getUser())) {
						//SELFは表示しない
						continue;
					}
					String tgtName = target.get(i).getStatus().getName();
					Map<StatusKey, Integer> damageMap = target.get(i).getStatus().calcDamage();
					int damage = 0;
					if (damageMap.containsKey(StatusKeyStorage.getInstance().get(BattleConfig.outputLogStatusKey))) {
						damage = damageMap.get(StatusKeyStorage.getInstance().get(BattleConfig.outputLogStatusKey));
					}
					switch (res) {
						case SUCCESS:
							s.append(tgtName);
							if (damage > 0) {
								s.append(I18N.translate("TO"));
								s.append(damage);
								s.append(I18N.translate("DAMAGE"));
							} else {
								s.append(I18N.translate("IS"));
								s.append(I18N.translate("NODAMAGE"));
							}
							break;
						case MISS:
							s.append(tgtName);
							s.append(I18N.translate("IS"));
							s.append(I18N.translate("NODAMAGE"));
							break;
						default:
							System.out.println("kinugasa.game.system.BattleSystem.setActionMessage() default message");
							break;

					}
					if (i == target.size() - 1) {
						s.append("  ");
					} else {
						s.append(", ");
					}
					if (j >= LF) {
						s.append(Text.getLineSep());
						line++;
						if (line > LINE) {
							break;
						}
					}
				}
			}
		}
		messageWindowSystem.setActionMessage(s.toString(), action.createWaitTime().getCurrentTime());

	}

	//resultに基づくアニメーションをthisに追加する
	private void setActionAnimation(BattleCharacter user, BattleAction action, List<BattleCharacter> target, List<BattleActionResult> result) {
		for (int i = 0; i < action.getEvents().size(); i++) {
			BattleActionEvent e = action.getEvents().get(i);
			BattleActionResult res = result.get(i);
			BattleActionAnimation anime = e.getAnimationClone();

			if (e.getBatt() == BattleActionTargetType.FIELD) {
				switch (res) {
					case ADD_CONDITION_FIELD:
					case SUCCESS:
					case ADD_CONDITION_TGT:
					case ATTR_IN:
					case REMOVE_CONDITION_TGT:
					case USE_ITEM:
						Sprite tgt = battleFieldSystem.getBattleFieldAllArea();
						anime.getAnimationSprite().setLocationByCenter(tgt.getCenter());
						animation.add(anime);
						break;
					case ITEM_LOST:
					case MISS:
					case MOVE:
					case NONE:
					case STOPED:
						//アイテム追加しない
						break;
					default:
						throw new AssertionError();
				}
			} else {
				for (BattleCharacter c : target) {
					//SELFは表示しない
					if (c.equals(user)) {
						continue;
					}
					//resによりアニメーションを追加
					switch (res) {
						case ADD_CONDITION_FIELD:
						case SUCCESS:
						case ADD_CONDITION_TGT:
						case ATTR_IN:
						case REMOVE_CONDITION_TGT:
						case USE_ITEM:
							anime.getAnimationSprite().setLocationByCenter(c.getSprite().getLocation());
							animation.add(anime);
							break;
						case ITEM_LOST:
						case MISS:
						case MOVE:
						case NONE:
						case STOPED:
							//アイテム追加しない
							break;
						default:
							throw new AssertionError();
					}
				}
			}

		}

	}

	public void update() {
		battleFieldSystem.update();
		messageWindowSystem.update();
		targetSystem.update();
		enemies.forEach(v -> v.update());

		if (targetSystem.getAfterMoveActionArea().isVisible()) {
			targetSystem.getAfterMoveActionArea().setLocationByCenter(currentCmd.getSpriteCenter());
		}

		GameSystem gs = GameSystem.getInstance();

		//勝敗判定
		List<BattleWinLoseLogic> winLoseLogic = BattleConfig.getWinLoseLogic();
		if (winLoseLogic.isEmpty()) {
			throw new GameSystemException("win lose logic is empty, this battle neber end.");
		}
		List<Status> party = GameSystem.getInstance().getPartyStatus();
		List<Status> enemy = enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList());
		for (BattleWinLoseLogic l : winLoseLogic) {
			BattleResult result = l.isWinOrLose(party, enemy);
			if (result == BattleResult.NOT_YET) {
				continue;
			}
			//戦闘終了処理
			String nextLogicName = result == BattleResult.WIN ? winLogicName : loseLogicName;
			int exp = enemies.stream().mapToInt(p -> (int) p.getStatus().getEffectedStatus().get(BattleConfig.expStatisKey).getValue()).sum();
			List<Item> dropItems = new ArrayList<>();
			for (Enemy e : enemies) {
				List<DropItem> items = e.getDropItem();
				for (DropItem i : items) {
					//ドロップアイテムの確率判定
					if (Random.percent(i.getP())) {
						dropItems.addAll(i.cloneN());
					}
				}
			}
			battleResultValue = new BattleResultValues(result, exp, dropItems, nextLogicName);

			if (GameSystem.isDebugMode()) {
				System.out.println("this battle is ended");
			}
			setStage(Stage.BATLE_END, "update");
		}

		//効果の終わったアニメーションを取り除く
		//アニメーションはアクションの待機時間より長く表示することも可能なためstage外で実施
		List<BattleActionAnimation> removeList = new ArrayList<>();
		for (BattleActionAnimation a : animation) {
			if (a.isEnded() || !a.getAnimationSprite().isVisible() || !a.getAnimationSprite().isVisible()) {
				removeList.add(a);
			}
		}
		animation.removeAll(removeList);

		switch (stage) {
			case STARTUP:
				throw new GameSystemException("update call before start");
			case INITIAL_MOVE:
				//プレイヤーキャラクターが目標の座標に近づくまで移動を実行、目的地に近づいたらステージWAITに変える
				gs.getPartySprite().forEach(v -> v.move());
				//移動終了判定
				boolean initialMoveEnd = true;
				for (int i = 0; i < gs.getPartySprite().size(); i++) {
					float speed = gs.getPartySprite().get(i).getSpeed();
					if (initialMoveEnd &= partyTargetLocationForFirstMove.get(i).distance(gs.getPartySprite().get(i).getLocation()) <= speed) {
						gs.getPartySprite().get(i).setLocation(partyTargetLocationForFirstMove.get(i));
						gs.getParty().get(i).unsetTarget();
					}
				}
				if (initialMoveEnd) {
					messageWindowSystem.closeActionWindow();
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case ESCAPING:
				//プレイヤーキャラクターが目標の座標に近づくまで移動を実行、目的地に近づいたらステージWAITに変える
				currentCmd.getUser().moveToTgt();

				if (!currentCmd.getUser().isMoving()) {
					//全員逃げ判定、全員逃げた場合、戦闘終了
					if (party.stream().allMatch(p -> p.hasCondition(BattleConfig.escapedConditionName))) {
						//全員逃げた
						battleResultValue = new BattleResultValues(BattleResult.ESCAPE, 0, new ArrayList<>(), winLogicName);

						if (GameSystem.isDebugMode()) {
							System.out.println("this battle is ended");
						}
						setStage(Stage.BATLE_END, "update");
						break;
					}
					messageWindowSystem.closeActionWindow();
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case WAITING_USER_CMD:
				//プレイヤーの行動まちなので、何もしない。
				//コマンドウインドウ等から処理を実行される
				//次にバトルコマンドを取得したとき、NPCならNPCの行動のステージに入る。
				break;
			case SHOW_ACTION_MESSAGE:
				//アクションウインドウが閉じられるまで待つ
				if (!messageWindowSystem.isVisibleActionMessage()) {
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case EXECUTING_ACTION:
				//カレントBATimeが切れるまで待つ
				assert currentBAWaitTime != null : "EXECITING_ACTION buf tc is null";
				if (currentBAWaitTime.isReaching()) {
					messageWindowSystem.closeActionWindow();
					currentBAWaitTime = null;
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case EXECUTING_MOVE:
				//NPCの移動実行、！！！！！！！！移動かんりょぅしたらメッセージウインドウ閉じる
				currentCmd.getUser().moveToTgt();
				remMovePoint--;
				//移動ポイントが切れた場合、移動終了してユーザコマンド待ちに移行
				if (remMovePoint <= 0 || !currentCmd.getUser().isMoving()) {
					currentCmd.getUser().unsetTarget();
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
					return;
				}
				//移動ポイントが切れていない場合で、移動ポイントが半分以上残っている場合は攻撃可能
				//半分以下の場合は行動終了
				if (remMovePoint < currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue()) {
					return;
				}
				//アクションを抽選
				EnemyBattleAction eba = currentCmd.getNPCActionOf(BattleActionType.ATTACK);
				if (eba == null) {
					if (GameSystem.isDebugMode()) {
						System.out.println("enemy " + currentCmd.getUser().getStatus().getName() + " try afterMoveAttack, but dont have attackCMD");
					}
					return;
				}
				//batt=FIELDの場合、即時実行
				if (eba.getEvents().stream().allMatch(p -> p.getBatt() == BattleActionTargetType.FIELD)) {
					//即時実行
					List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser());
					updateCondition();
					currentBAWaitTime = eba.createWaitTime();
					//メッセージ表示
					setActionMessage(currentCmd.getUser(), eba, Collections.emptyList(), result);
					//アニメーション追加
					setActionAnimation(currentCmd.getUser(), eba, Collections.emptyList(), result);
					setStage(Stage.EXECUTING_ACTION, "UPDATE");
					return;
				}
				//batt==SELFの場合、即時実行
				if (eba.getEvents().stream().allMatch(p -> p.getBatt() == BattleActionTargetType.SELF)) {
					//即時実行
					List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser());
					updateCondition();
					currentBAWaitTime = eba.createWaitTime();
					//メッセージ表示
					setActionMessage(currentCmd.getUser(), eba, Arrays.asList(currentCmd.getUser()), result);
					//アニメーション追加
					setActionAnimation(currentCmd.getUser(), eba, Arrays.asList(currentCmd.getUser()), result);
					setStage(Stage.EXECUTING_ACTION, "UPDATE");
					return;
				}

				// イベント対象者別にターゲットを設定
				List<BattleCharacter> target = getTargetOfCurrentCmd(eba);
				if (target.isEmpty()) {
					return;
				}
				//攻撃実行
				List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser(), target);
				updateCondition();
				currentBAWaitTime = eba.createWaitTime();
				//メッセージ表示
				setActionMessage(currentCmd.getUser(), eba, target, result);
				//アニメーション追加
				setActionAnimation(currentCmd.getUser(), eba, target, result);
				setStage(Stage.EXECUTING_ACTION, "UPDATE");
				break;
			case SHOW_INFO_MSG:
				if (!messageWindowSystem.isVisibleInfoMessage()) {
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case BATLE_END:
				//何もしない（ユーザ操作待ち
				break;
			default:
				throw new AssertionError("UNDEFINED STAGE");

		}
	}

	private List<BattleCharacter> getTargetOfCurrentCmd(EnemyBattleAction eba) {
		List<BattleCharacter> target = new ArrayList<>();
		//エリアは選択されたアクションか移動ポイントの小さいほう
		int area = Math.min(eba.getAreaWithEqip(currentCmd.getUser().getStatus()), remMovePoint);
		for (BattleActionEvent e : eba.getEvents()) {
			switch (e.getBatt()) {
				case ALL:
					target.addAll(targetSystem.getAllTarget(currentCmd.getSpriteCenter(), area));
					break;
				case ONE_ENEMY:
					List<BattleCharacter> l1 = targetSystem.nearPlayer(currentCmd.getSpriteCenter(), area);
					if (!l1.isEmpty()) {
						target.add(l1.get(0));
					}
					break;
				case ONE_PARTY:
					List<BattleCharacter> l2 = targetSystem.nearEnemy(currentCmd.getSpriteCenter(), area);
					if (!l2.isEmpty()) {
						target.add(l2.get(0));
					}
					break;
				case TEAM_ENEMY:
					target.addAll(targetSystem.nearPlayer(currentCmd.getSpriteCenter(), area));
					break;
				case TEAM_PARTY:
					target.addAll(targetSystem.nearEnemy(currentCmd.getSpriteCenter(), area));
					break;
				case RANDOM_ONE:
					List<BattleCharacter> l3 = targetSystem.getAllTarget(currentCmd.getSpriteCenter(), area);
					if (!l3.isEmpty()) {
						Collections.shuffle(l3);
						target.add(l3.get(0));
					}
					break;
				case RANDOM_ONE_ENEMY:
					List<BattleCharacter> l4 = targetSystem.nearPlayer(currentCmd.getSpriteCenter(), area);
					if (!l4.isEmpty()) {
						Collections.shuffle(l4);
						target.add(l4.get(0));
					}
					break;
				case RANDOM_ONE_PARTY:
					List<BattleCharacter> l5 = targetSystem.nearEnemy(currentCmd.getSpriteCenter(), area);
					if (!l5.isEmpty()) {
						Collections.shuffle(l5);
						target.add(l5.get(0));
					}
					break;
				case SELF:
					target.add(currentCmd.getUser());
				case FIELD:
					throw new GameSystemException("FIELD event is not executed");
			}
		}
		target = target.stream().distinct().collect(Collectors.toList());
		return target;
	}

	@Override
	public void draw(GraphicsContext g) {
		battleFieldSystem.draw(g);
		enemies.forEach(v -> v.draw(g));
		GameSystem.getInstance().getPartySprite().forEach(v -> v.draw(g));
		animation.forEach(v -> v.draw(g));
		targetSystem.draw(g);

		messageWindowSystem.draw(g);
	}

	public BattleMessageWindowSystem getMessageWindowSystem() {
		return messageWindowSystem;
	}

	public BattleTargetSystem getTargetSystem() {
		return targetSystem;
	}

	public BattleFieldSystem getBattleFieldSystem() {
		return battleFieldSystem;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public boolean stageIs(Stage s) {
		return stage == s;
	}

	BattleCommand getCurrentCmd() {
		return currentCmd;
	}

}
