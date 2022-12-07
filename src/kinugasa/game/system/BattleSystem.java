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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.LoopCall;
import kinugasa.game.NoLoopCall;
import kinugasa.game.field4.PlayerCharacterSprite;
import kinugasa.game.field4.VehicleStorage;
import static kinugasa.game.system.TargetType.SELF;
import kinugasa.game.ui.Text;
import kinugasa.object.AnimationSprite;
import kinugasa.object.BasicSprite;
import kinugasa.object.Drawable;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.object.Sprite;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;

/**
 * バトル管理クラス。
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
	//戦闘開始前BGM
	private Sound prevBGM, currentBGM;
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
	private List<AnimationSprite> animation = new ArrayList<>();
	//実行中バトルアクションから生成されたアクション待機時間
	private FrameTimeCounter currentBAWaitTime;
	//行動中コマンド
	private BattleCommand currentCmd;
	//ActionMessage表示時間
	private int messageWaitTime = 66;
	//戦闘結果
	private BattleResultValues battleResultValue = null;
	//カレントBAのNPC残移動ポイント
	private int remMovePoint;
	//移動開始時の位置
	private Point2D.Float moveIinitialLocation;

	//------------------------------------------------------updateメソッドステージ
	public enum Stage {
		/**
		 * 開始?初期移動開始前。終わったらINITIALに入る。
		 */
		STARTUP,
		/**
		 * 初期移動中。終わったらWAITに入る。
		 */
		INITIAL_MOVE,
		/**
		 * 逃げアニメーション実行中。終わったらWAITに入る。
		 */
		ESCAPING,
		/**
		 * ユーザコマンド待機。execが呼ばれるまで何もしない。
		 */
		WAITING_USER_CMD,
		/**
		 * プレイヤーキャラクタ?移動中。確定アクションが呼ばれるまで何もしない。
		 */
		PLAYER_MOVE,
		/**
		 * ターゲット選択中。execが呼ばれるまで何もしない。
		 */
		TARGET_SELECT,
		/**
		 * アクション実行中。終わったらWAITに入る。
		 */
		EXECUTING_ACTION,
		/**
		 * 敵移動実行中。終わったらWAITに入る。
		 */
		EXECUTING_MOVE,
		/**
		 * バトルは終了して、ゲームシステムからの終了指示を待っている。
		 */
		BATLE_END,
		/**
		 * INFOメッセージ表示中。終わったらWAITに入る。主にキャンセル（再行動可能な行動失敗）に使う。
		 */
		SHOW_INFO_MSG,
	}
	//現在のステージ
	private Stage stage;
	//-------------------------------------------------------------------システム
	//メッセージウインドウシステムのインスタンス
	private BattleMessageWindowSystem messageWindowSystem;
	//ターゲット選択システムのインスタンス
	private BattleTargetSystem targetSystem;
	//バトルフィールドインスタンス
	private BattleFieldSystem battleFieldSystem;
	//状態異常マネージャ
	private ConditionManager conditionManager;

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
		//前BGMの停止
		prevBGM = enc.getPrevBGM();
		if (prevBGM != null) {
			switch (es.getPrevBgmMode()) {
				case NOTHING:
					break;
				case PAUSE:
					prevBGM.pause();
					break;
				case STOP:
					prevBGM.stop();
					break;
				case STOP_AND_PLAY:
					prevBGM.stopAndPlay();
					break;
			}
		}
		//バトルBGMの再生
		if (es.hasBgm()) {
			SoundStorage.getInstance().get(es.getBgmMapName()).stopAll();
			currentBGM = es.getBgm().load();
			currentBGM.stopAndPlay();
		}
		//敵取得
		enemies = es.create();
		ess.dispose();
		//初期化
		GameSystem gs = GameSystem.getInstance();
		battleFieldSystem = BattleFieldSystem.getInstance();
		battleFieldSystem.init(enc.getChipAttribute());
		targetSystem = BattleTargetSystem.getInstance();
		targetSystem.init();
		messageWindowSystem = BattleMessageWindowSystem.getInstance();
		messageWindowSystem.init(gs.getPartyStatus());
		conditionManager = ConditionManager.getInstance();

		//出現MSG設定用マップ
		Map<String, Long> enemyNum = enemies.stream().collect(Collectors.groupingBy(Enemy::getId, Collectors.counting()));
		//出現MSG設定
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Long> e : enemyNum.entrySet()) {
			sb.append(e.getKey()).append(I18N.translate("WAS")).append(e.getValue()).append(I18N.translate("APPEARANCE")).append(Text.getLineSep());
		}
		messageWindowSystem.setActionMessage(sb.toString(), Integer.MAX_VALUE);//勝手に上書きされるので、最大時間表示でよい

		//リセット
		currentCmd = null;
		currentBAWaitTime = null;
		commandsOfThisTurn.clear();
		turn = 0;
		animation.clear();
		targetSystem.unsetCurrent();

		//敵の配置
		putEnemy();

		//味方の配置
		putParty();
		assert partyTargetLocationForFirstMove.size() == gs.getParty().size() : "initial move target is missmatch";
		//初期移動実行へ
		setStage(Stage.INITIAL_MOVE, "encountInit");
	}

	private void putParty() {
		GameSystem gs = GameSystem.getInstance();
		partyInitialDir.clear();
		partyInitialLocation.clear();
		partyTargetLocationForFirstMove.clear();

		//戦闘開始前位置・向き退避
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

		//アイテム使用をアクションに追加する
		for (PlayerCharacter pc : gs.getParty()) {
			pc.getStatus().getActions().addAll(pc.getStatus().getItemBag().getItems());
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
					BattleCommand bc = new BattleCommand(s.isPlayer()
							? BattleCommand.Mode.PC
							: BattleCommand.Mode.CPU,
							s.getUser())
							.setAction(Arrays.asList(s.getAction()))
							.setMagicSpell(true);
					int idx = Random.randomAbsInt(commandsOfThisTurn.size());
					//割り込ませるユーザの通常アクションを破棄する
					BattleCommand remove = null;
					for (BattleCommand c : commandsOfThisTurn) {
						if (c.getUser().equals(bc.getUser())) {
							remove = c;
						}
					}
					//ユーザがアンターゲット状態の場合、コマンドは破棄
					if (remove != null) {
						if (!remove.getUser().getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
							commandsOfThisTurn.remove(remove);
						}
						//削除してから割り込み実行
						commandsOfThisTurn.add(idx, bc);
					}
				}
				//詠唱中リストからこのターンのイベントを削除
				magics.remove(turn);
			}
		}
		//PC・NPCの状態異常の経過ターン更新・継続ダメージ処理
		updateCondition();

		//このターン行動可否をコマンドに設定
		for (BattleCommand cmd : commandsOfThisTurn) {
			if (cmd.getUser().getStatus().isConfu()) {
				//混乱
				cmd.setConfu(true);
			}
			if (!cmd.getUser().getStatus().canMoveThisTurn()) {
				//その他行動不能の状態異常
				cmd.setStop(true);
			}
		}

		setStage(Stage.WAITING_USER_CMD, "TURN_START");
	}

	@NoLoopCall
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

		//状態異常の効果時間を引く
		enemies.stream().map(p -> p.getStatus()).forEach(p -> p.update());
		GameSystem.getInstance().getPartyStatus().forEach(p -> p.update());

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
		//アイテムアクションを削除する
		for (PlayerCharacter pc : GameSystem.getInstance().getParty()) {
			//逃げた後死亡、死亡した後逃げるはできないので、これで問題ないはず
			if (pc.getStatus().hasCondition(BattleConfig.ConditionName.escaped)) {
				pc.getSprite().setVisible(true);
				//逃げたコンディションを外す
				pc.getStatus().removeCondition(BattleConfig.ConditionName.escaped);

				List<CmdAction> removeList = pc.getStatus().getActions().stream().filter(p -> p.getType() == ActionType.ITEM_USE).collect(Collectors.toList());
				pc.getStatus().getActions().removeAll(removeList);
			}
		}
		//BGMの処理
		if (currentBGM != null) {
			currentBGM.stop();
			currentBGM.dispose();
		}
		if (prevBGM != null) {
			prevBGM.play();
		}
		end = true;
	}

	BattleResultValues getBattleResultValue() {
		if (!end) {
			throw new GameSystemException("this battle is end not yet.");
		}
		assert battleResultValue != null : "battle is end, but result is null";
		return battleResultValue;
	}

	//次のコマンドを取得。NPCまたはPC。NPCの場合は自動実行。魔法詠唱イベントも自動実行。
	//このメソッドを起動して次のアクションを取得する。
	//取得したアクションがPCならコマンドウインドウが自動で開かれているので、選択する。
	//選択後、execPCActionを実行する。
	public BattleCommand execCmd() {
		//すべてのコマンドを実行したら次のターンを開始
		if (commandsOfThisTurn.isEmpty()) {
			turnStart();
		}
		currentCmd = commandsOfThisTurn.getFirst();
		assert currentCmd != null : "BS currentCMD is null";
		commandsOfThisTurn.removeFirst();

		//ターゲットシステム初期化
		targetSystem.unsetCurrent();
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
		if (user.getStatus().hasCondition(BattleConfig.ConditionName.defence)) {
			user.getStatus().removeCondition(BattleConfig.ConditionName.defence);
		}
		if (user.getStatus().hasCondition(BattleConfig.ConditionName.avoidance)) {
			user.getStatus().removeCondition(BattleConfig.ConditionName.avoidance);
		}

		//魔法詠唱完了イベントの場合、PCでもNPCでも自動実行、（詠唱中コンディションを外す
		//魔法のコストとターゲットは、詠唱開始と終了の2回判定する。
		//ここは「詠唱終了時」の処理。
		if (currentCmd.isMagicSpell()) {
			CmdAction ba = currentCmd.getFirstBattleAction();//1つしか入っていない
			//現状でのターゲットを取得
			BattleActionTarget target = BattleTargetSystem.instantTarget(currentCmd.getUser(), ba);
			//ターゲットがいない場合、詠唱失敗のメッセージ出す
			if (target.isEmpty()) {
				//対象なし
				StringBuilder s = new StringBuilder();
				s.append(currentCmd.getUser().getStatus().getName());
				s.append(I18N.translate("S"));
				s.append(currentCmd.getFirstBattleAction().getName());
				s.append(I18N.translate("ISFAILED"));
				messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.closeInfoWindow();
				messageWindowSystem.closeTooltipWindow();
				currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
				currentCmd.getUser().getStatus().removeCondition(BattleConfig.ConditionName.spelling);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			}
			//現状のステータスで対価を支払えるか確認
			//※実際に支払うのはexecしたとき。
			Map<StatusKey, Integer> damage = ba.selfBattleDirectDamage();
			//ダメージを合算
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//ダメージがあって、0の項目がある場合、対価を支払えないため空振り
			//この魔法の消費項目を取得
			List<StatusKey> shortageKey = new ArrayList<>();
			for (ActionEvent e : ba.getBattleEvent().stream().filter(p -> p.getTargetType() == TargetType.SELF).collect(Collectors.toList())) {
				shortageKey.add(StatusKeyStorage.getInstance().get(e.getTgtName()));
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
			//詠唱成功、魔法効果発動
			target.forEach(p -> p.getStatus().setDamageCalcPoint());
			ActionResult res = ba.exec(target);
			setActionMessage(user, ba, target, res);
			setActionAnimation(user, ba, target, res);
			currentCmd.getUser().getStatus().removeCondition(BattleConfig.ConditionName.spelling);
			currentBAWaitTime = new FrameTimeCounter(ba.getWaitTime());
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
			setActionMessage(s.toString());
			messageWindowSystem.closeTooltipWindow();
			messageWindowSystem.closeAfterMoveCommandWindow();
			messageWindowSystem.closeCommandWindow();
			setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
			return currentCmd;
		}

		//混乱で動けないときは、停止またはバトルアクションを適当に取得して自動実行する
		if (currentCmd.isConfu()) {
			if (Random.percent(BattleConfig.conguStopP)) {
				//動けない
				StringBuilder s = new StringBuilder();
				s.append(currentCmd.getUser().getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(I18N.translate("CONFU_STOP"));
				setActionMessage(s.toString());
				currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			} else {
				//動けるが混乱
				CmdAction ba = currentCmd.randomAction();
				currentBAWaitTime = ba.createWaitTime();
				execAction(ba);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			}
		}

		//NPC
		if (currentCmd.getMode() == BattleCommand.Mode.CPU) {
			//NPCアクションを自動実行、この中でステージも変わる
			messageWindowSystem.closeCommandWindow();
			execAction(currentCmd.getBattleActionEx(((Enemy) currentCmd.getUser()).getAI(), ActionType.OTHER, ActionType.ITEM_USE));
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

		//ターゲットシステムをウインドウの初期選択で初期化
		targetSystem.setCurrent(currentCmd.getUser(), messageWindowSystem.getCommandWindow().getSelected());

		//PCの操作なので、カレントコマンドのユーザオペレーション要否フラグをONに設定
		currentCmd.setUserOperation(true);

		if (GameSystem.isDebugMode()) {
			System.out.println("getNextCmdAndExecNPCCmd : return PC_CMD" + currentCmd);
		}
		setStage(Stage.WAITING_USER_CMD, "getNextCmdAndExecNPCCmd");
		return currentCmd;
	}

	public OperationResult execPCAction() {
		//コマンドウインドウまたは移動後攻撃ウインドウからアクションを取得
		return execAction(messageWindowSystem.isVisibleCommand()
				? messageWindowSystem.getCommandWindow().getSelected()
				: messageWindowSystem.getAfterMoveCommandWindow().getSelected());
	}

	//アクション実行
	OperationResult execAction(CmdAction a) {
		//PC,NPC問わず選択されたアクションを実行する。

		//ウインドウ状態初期化
		messageWindowSystem.closeTooltipWindow();
		messageWindowSystem.closeInfoWindow();

		//カレントユーザ
		BattleCharacter user = currentCmd.getUser();

		//混乱中の場合
		if (user.getStatus().isConfu()) {
			//ターゲットシステムのカレント起動しないで対象を取得する
			setActionMessage(user, a);
			BattleActionTarget tgt = BattleTargetSystem.instantTarget(user, a);
			return execAction(a, tgt);
		}

		//NPCの場合
		if (!user.isPlayer()) {
			//アクションの効果範囲に相手がいるか、インスタント確認
			BattleActionTarget tgt = BattleTargetSystem.instantTarget(user, a);
			if (tgt.isEmpty()) {
				//ターゲットがいない場合で、移動アクションを持っている場合は移動開始
				if (user.getStatus().hasAction(BattleConfig.ActionName.move)) {
					//移動ターゲットは最も近いPCとする
					BattleCharacter targetChara = BattleTargetSystem.nearPCs(user);
					user.setTargetLocation(targetChara.getCenter(), a.getAreaWithEqip(user));
					//移動距離を初期化
					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
					//移動した！のメッセージ表示
					StringBuilder s = new StringBuilder();
					s.append(user.getName());
					s.append(I18N.translate("ISMOVE"));
					messageWindowSystem.setActionMessage(s.toString(), Integer.MAX_VALUE);
					setStage(Stage.EXECUTING_MOVE, "execAction");//StageをNPC移動に上書き
					return OperationResult.SUCCESS;
				} else {
					//移動できないので何もしない
					return OperationResult.MISS;
				}
			} else {
				//ターゲットがいる場合は即時実行
				return execAction(a, tgt);
			}

		}

		//特殊コマンドの処理
		if (a.getType() == ActionType.OTHER) {
			if (a.getName().equals(BattleConfig.ActionName.avoidance)) {
				//回避・回避状態を付与する
				user.getStatus().addCondition(BattleConfig.ConditionName.avoidance);
				setActionMessage(user, a);
				return OperationResult.SUCCESS;
			}
			if (a.getName().equals(BattleConfig.ActionName.defence)) {
				//防御・防御状態を付与する
				user.getStatus().addCondition(BattleConfig.ConditionName.defence);
				setActionMessage(user, a);
				return OperationResult.SUCCESS;
			}
			if (a.getName().equals(BattleConfig.ActionName.move)) {
				//移動開始・初期位置を格納
				moveIinitialLocation = user.getSprite().getLocation();
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.getAfterMoveCommandWindow().setVisible(true);
				List<CmdAction> action = user.getStatus().getActions(ActionType.ATTACK);
				action.add(ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
				Collections.sort(action);
				messageWindowSystem.getAfterMoveCommandWindow().setActions(action);
				//ターゲットシステムのエリア表示を有効化：値はMOV
				targetSystem.setCurrent(user, a);
				setStage(Stage.PLAYER_MOVE, "execAction");
				return OperationResult.MOVE;
			}
			if (a.getName().equals(BattleConfig.ActionName.commit)) {
				//移動終了・キャラクタの向きとターゲット座標のクリアをする
				messageWindowSystem.closeAfterMoveCommandWindow();
				user.unsetTarget();
				user.to(FourDirection.WEST);
				setStage(Stage.WAITING_USER_CMD, "execAction");
				return OperationResult.SUCCESS;
			}
			if (a.getName().equals(BattleConfig.ActionName.status)) {
				//ステータスウインドウ閲覧
				//TODO:表示処理ここ
				setStage(Stage.WAITING_USER_CMD, "execAction");
				return OperationResult.SHOW_STATUS;
			}
			if (a.getName().equals(BattleConfig.ActionName.escape)) {
				//逃げる・逃げられるか判定
				//前提として、移動ポイント内にバトルエリアの境界（左右）がなければならない
				Point2D.Float w, e;
				int movPoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
				e = (Point2D.Float) user.getSprite().getCenter().clone();
				e.x += movPoint;
				w = (Point2D.Float) user.getSprite().getCenter().clone();
				w.x -= movPoint;
				if (!battleFieldSystem.getBattleFieldAllArea().contains(e)) {
					//逃走成功（→）
					user.getStatus().addCondition(ConditionValueStorage.getInstance().get(BattleConfig.ConditionName.escaped).getKey());
					user.setTargetLocation(e, 0);
					user.to(FourDirection.EAST);
					messageWindowSystem.closeCommandWindow();
					messageWindowSystem.closeInfoWindow();
					messageWindowSystem.closeAfterMoveCommandWindow();
					messageWindowSystem.setActionMessage(user.getStatus().getName() + I18N.translate("ISESCAPE"), messageWaitTime);
					if (user.isPlayer()) {
						messageWindowSystem.getStatusWindows().getMw().get(((PlayerCharacter) user).getOrder()).setVisible(false);
					}
					setStage(Stage.ESCAPING, "execAction");
					return OperationResult.SUCCESS;
				}
				if (!battleFieldSystem.getBattleFieldAllArea().contains(w)) {
					//逃走成功（←）
					user.getStatus().addCondition(ConditionValueStorage.getInstance().get(BattleConfig.ConditionName.escaped).getKey());
					user.setTargetLocation(w, 0);
					user.to(FourDirection.WEST);
					messageWindowSystem.closeCommandWindow();
					messageWindowSystem.closeInfoWindow();
					messageWindowSystem.closeAfterMoveCommandWindow();
					messageWindowSystem.setActionMessage(user.getStatus().getName() + I18N.translate("ISESCAPE"), messageWaitTime);
					if (user.isPlayer()) {
						messageWindowSystem.getStatusWindows().getMw().get(((PlayerCharacter) user).getOrder()).setVisible(false);
					}
					setStage(Stage.ESCAPING, "execAction");
					return OperationResult.SUCCESS;
				}
				//NPCの場合、逃げる体制に入る
				if (!user.isPlayer()) {
					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
					user.setTargetLocation(w, 1);
					setStage(Stage.EXECUTING_MOVE, "execAction");
					return OperationResult.SUCCESS;
				}
				//逃げられない
				messageWindowSystem.setInfoMessage(I18N.translate("CANT_ESCAPE"));
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return OperationResult.CANCEL;
			}
		}

		//ターゲットシステム起動要否判定
		boolean needTargetSystem = false;
		if (user.isPlayer()) {
			//ターゲット選択はプレイヤーのみ
			if (a.getType() != ActionType.OTHER) {
				//その他イベント以外はターゲット選択必要
				needTargetSystem = true;
			}
			if (a.getType() == ActionType.ITEM_USE) {
				//アイテム使用　※アイテムextendsアクション
				//アイテムターゲット選択要否
				//アイテム使用は、アイテム使用->装備の優先度とする
				if (a.isBattleUse() && a.getBattleEvent().stream().anyMatch(p -> p.getTargetType() != SELF) && !((Item) a).canEqip()) {
					//利用可能でSELFのみじゃない場合ターゲットシステム起動
					//ターゲットシステムを起動する前に、インスタントターゲットでターゲットがいるか確認する。いない場合キャンセルにする。
					if (!BattleTargetSystem.instantTarget(user, a).hasAnyTargetChara()) {
						messageWindowSystem.setInfoMessage(I18N.translate("NO_TARGET"));
						setStage(Stage.SHOW_INFO_MSG, "execAction");
						return OperationResult.CANCEL;
					}
					messageWindowSystem.setTolltipMessage(I18N.translate("TARGET_SELECT"));
					targetSystem.setCurrent(user, a);
					setStage(Stage.WAITING_USER_CMD, "execAction");
					return OperationResult.TO_TARGET_SELECT;
				}
				if (a.isBattleUse() && a.getBattleEvent().stream().allMatch(p -> p.getTargetType() == SELF)) {
					//利用可能でSELFのみの場合、即時実行
					BattleActionTarget tgt = BattleTargetSystem.instantTarget(user, a);//SELF
					tgt.getUser().getStatus().setDamageCalcPoint();
					ActionResult result = a.exec(tgt);
					setActionMessage(user, a, tgt, result);
					setActionAnimation(user, a, tgt, result);
					currentBAWaitTime = a.createWaitTime();
					setStage(Stage.EXECUTING_ACTION, "execAction");
					return OperationResult.SUCCESS;
				}
				if (((Item) a).canEqip()) {
					//装備可能アイテムの場合
					Item i = (Item) a;
					//このアイテムをすでに装備している場合、空振りさせる
					if (user.getStatus().isEqip(i.getName())) {
						//空振り
						StringBuilder s = new StringBuilder();
						s.append(user.getStatus().getName());
						s.append(I18N.translate("IS"));
						s.append(a.getName());
						s.append(I18N.translate("WAS_EQIP"));
						setActionMessage(s.toString());
						currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
						setStage(Stage.EXECUTING_ACTION, "execAction");
						return OperationResult.CANCEL;//returnするのでターゲット選択要否は無視される
					}
					//装備変更
					//同じスロットの装備を外す
					user.getStatus().removeEqip(i.getEqipmentSlot());
					//装備する
					user.getStatus().eqip(i);
					StringBuilder s = new StringBuilder();
					s.append(user.getStatus().getName());
					s.append(I18N.translate("IS"));
					s.append(a.getName());
					s.append(I18N.translate("IS_EQIP"));
					setActionMessage(s.toString());
					currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
					setStage(Stage.EXECUTING_ACTION, "execAction");
					return OperationResult.SUCCESS;//returnするのでターゲット選択要否は無視される
				}
				if (!a.isBattleUse()) {
					//使っても効果がないアイテムの場合
					StringBuilder s = new StringBuilder();
					s.append(user.getStatus().getName());
					s.append(I18N.translate("IS"));
					s.append(a.getName());
					s.append(I18N.translate("USE_ITEM"));
					s.append(Text.getLineSep());
					s.append(I18N.translate("BUT"));
					s.append(I18N.translate("NO_EFFECT"));
					setActionMessage(s.toString());
					currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
					setStage(Stage.EXECUTING_ACTION, "execAction");
					return OperationResult.MISS;//returnするのでターゲット選択要否は無視される
				}
			}
			//ターゲットシステムが初期化状態の場合、ターゲット選択必要
			if (targetSystem.isEmpty()) {
				needTargetSystem = true;
			} else {
				needTargetSystem = false;
			}
		}

		//ターゲットシステム起動
		if (needTargetSystem) {
			//ターゲットシステムを起動する前に、インスタントターゲットでターゲットがいるか確認する。いない場合キャンセルにする。
			if (!BattleTargetSystem.instantTarget(user, a).hasAnyTargetChara()) {//魔法で現状ターゲットがいない場合もここで吸収される
				messageWindowSystem.setInfoMessage(I18N.translate("NO_TARGET"));
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return OperationResult.CANCEL;
			}
			messageWindowSystem.setTolltipMessage(I18N.translate("TARGET_SELECT"));
			targetSystem.setCurrent(user, a);
			setStage(Stage.WAITING_USER_CMD, "execAction");
			return OperationResult.TO_TARGET_SELECT;
		}

		targetSystem.setCurrent(user, a);
		return execAction(a, targetSystem.getSelected());

	}

	//アクション実行
	OperationResult execAction(CmdAction a, BattleActionTarget tgt) {
		messageWindowSystem.closeTooltipWindow();//ターゲット選択中の表示を閉じる
		//ターゲットシステムが呼ばれているので、初期化
		targetSystem.unsetCurrent();
		//カレントユーザ
		BattleCharacter user = currentCmd.getUser();

		//魔法詠唱開始の場合、詠唱中リストに追加して戻る。
		if (a.getType() == ActionType.MAGIC) {
			//現状のステータスで対価を支払えるか確認
			//※実際に支払うのはexecしたとき。
			Map<StatusKey, Integer> damage = a.selfBattleDirectDamage();
			//ダメージを合算
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//ダメージがあって、0の項目がある場合、対価を支払えないため空振り
			//この魔法の消費項目を取得
			List<StatusKey> shortageKey = new ArrayList<>();
			for (ActionEvent e : a.getBattleEvent().stream().filter(p -> p.getTargetType() == TargetType.SELF).collect(Collectors.toList())) {
				shortageKey.add(StatusKeyStorage.getInstance().get(e.getTgtName()));
			}
			if (!damage.isEmpty() && simulateDamage.isZero(false, shortageKey)) {
				//対象項目で1つでも0の項目があったら空振り
				StringBuilder s = new StringBuilder();
				s.append(user.getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(a.getName());
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
				return user.isPlayer() ? OperationResult.CANCEL : OperationResult.MISS;
			}
			//ターゲット存在確認、現状でいない場合、空振り。発動時の再チェックがここ。
			if (tgt.isEmpty() && !a.battleEventIsOnly(SELF)) {
				StringBuilder s = new StringBuilder();
				s.append(user.getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(a.getName());
				s.append(I18N.translate("SPELL_START"));
				s.append(Text.getLineSep());
				s.append(I18N.translate("BUT"));
				s.append(I18N.translate("NO_TARGET"));
				setActionMessage(s.toString());
				currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
				setStage(Stage.EXECUTING_ACTION, "execAction");
				return user.isPlayer() ? OperationResult.CANCEL : OperationResult.MISS;
			}

			if (a.getSpellTime() > 0) {
				//詠唱時間がある場合は詠唱開始
				addSpelling(user, a);//MSG、STAGEもこの中で行う。
				return OperationResult.SUCCESS;
			}
		}
		//ターゲット不在の場合、空振り（ミス）、念のための処理、多分いらない
		if (tgt.isEmpty()) {
			StringBuilder s = new StringBuilder();
			s.append(user.getStatus().getName());
			s.append(I18N.translate("S"));
			s.append(a.getName());
			s.append(Text.getLineSep());
			s.append(I18N.translate("BUT"));
			s.append(I18N.translate("NO_TARGET"));
			setActionMessage(s.toString());
			currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
			setStage(Stage.EXECUTING_ACTION, "execAction");
			return OperationResult.MISS;
		}
		assert !tgt.isEmpty() : "target is empty(execAction)";
		//ターゲット存在のため、アクション実行
		tgt.getTarget().forEach(p -> p.getStatus().setDamageCalcPoint());
		ActionResult res = a.exec(tgt);
		setActionMessage(user, a, tgt, res);
		setActionAnimation(user, a, tgt, res);
		currentBAWaitTime = new FrameTimeCounter(a.getWaitTime());
		setStage(Stage.EXECUTING_ACTION, "execAction");
		//アイテムがREMOVEされている可能性があるため、全員のアクションを改める
		getAllChara().forEach(p -> p.getStatus().updateItemAction());
		return OperationResult.SUCCESS;
	}

	//PCの移動をキャンセルして、移動前の位置に戻す。確定は「commit」タイプのアクションから。
	public void cancelPCsMove() {
		currentCmd.getUser().getSprite().setLocation(moveIinitialLocation);
		currentCmd.getUser().unsetTarget();
		currentCmd.getUser().to(FourDirection.WEST);
		messageWindowSystem.getAfterMoveCommandWindow().setVisible(false);
		messageWindowSystem.getCommandWindow().setCmd(currentCmd);
		messageWindowSystem.getCommandWindow().setVisible(true);
		setStage(Stage.WAITING_USER_CMD, "cancelPCsMove");
	}

	private boolean prevAttackOK = false;

	//移動後攻撃の設定を行う。引数で攻撃できるかを渡す。
	@LoopCall
	public void setAftedMoveAction(boolean attackOK) {
		if (!messageWindowSystem.isVisibleAfterMoveCommand()) {
			throw new GameSystemException("after move window is not visible");
		}
		if (prevAttackOK == attackOK) {
			return;
		}
		prevAttackOK = attackOK;
		List<CmdAction> afterMoveActions = new ArrayList<>();
		if (attackOK) {
			afterMoveActions.addAll(currentCmd.getBattleActions().stream().filter(p -> p.getType() == ActionType.ATTACK).collect(Collectors.toList()));
		}
		afterMoveActions.add(ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
		Collections.sort(afterMoveActions);
		if (!attackOK) {
			targetSystem.getCurrentArea().setVisible(false);
			targetSystem.getCurrentArea().setArea(0);
		} else {
			targetSystem.getCurrentArea().setVisible(true);
		}

		messageWindowSystem.getAfterMoveCommandWindow().setActions(afterMoveActions);
	}

	private void addSpelling(BattleCharacter user, CmdAction ba) {
		if (ba.getSpellTime() == 0) {
			throw new GameSystemException("this magic is spell time is 0, bud logic : " + ba);
		}
		int t = turn + ba.getSpellTime();
		if (magics.containsKey(t)) {
			magics.get(t).add(new MagicSpell(user, ba, user.isPlayer()));
		} else {
			List<MagicSpell> list = new ArrayList();
			list.add(new MagicSpell(user, ba, user.isPlayer()));
			magics.put(t, list);
		}
		StringBuilder s = new StringBuilder();
		s.append(user.getName());
		s.append(I18N.translate("IS"));
		s.append(" [ ");
		s.append(ba.getName());
		s.append(" ] ");
		s.append(I18N.translate("SPELL_START"));
		setActionMessage(s.toString());
		currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
		setStage(Stage.EXECUTING_ACTION, "execAction");
	}

	private void setActionMessage(String s) {
		//ウェイトタイムとステージも更新！！！！！！
		messageWindowSystem.setActionMessage(s, messageWaitTime);
		currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
		setStage(Stage.EXECUTING_ACTION, "execAction");
	}

	private void setActionMessage(BattleCharacter user, CmdAction action) {
		StringBuilder s = new StringBuilder();
		//混乱、回避、防御の場合
		s.append(user.getName());
		if (user.getStatus().isConfu()) {
			s.append(I18N.translate("IS"));
			s.append(I18N.translate("CONFU_STOP"));
		}
		if (action.getName().equals(BattleConfig.ActionName.defence)) {
			s.append(I18N.translate("IS"));
			s.append(I18N.translate("DEFENCE"));
		}
		if (action.getName().equals(BattleConfig.ActionName.avoidance)) {
			s.append(I18N.translate("IS"));
			s.append(I18N.translate("AVOIDANCE"));
		}
		messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
		currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
		setStage(Stage.EXECUTING_ACTION, "execAction");
	}

	//resultに基づくメッセージをアクションウインドウに設定する
	private void setActionMessage(BattleCharacter user, CmdAction action, BattleActionTarget target, ActionResult result) {
		//アクションが実行されているので、必要ないウインドウは閉じる
		messageWindowSystem.closeCommandWindow();
		messageWindowSystem.closeAfterMoveCommandWindow();
		messageWindowSystem.closeInfoWindow();
		messageWindowSystem.closeTooltipWindow();
		StringBuilder s = new StringBuilder();
		final int LF = BattleConfig.actionWindowLF;
		final int LINE = 7;

		s.append(user.getName());
		s.append(I18N.translate("S"));
		s.append("[ ");
		s.append(action.getName());
		s.append(" ] ! !");
		s.append(Text.getLineSep());
		//アイテム効果発動の場合があるので注意。

		StatusKey hp = StatusKeyStorage.getInstance().get(BattleConfig.StatusKey.hp);
		//SELFはターゲットが入っていない。SELFダメージだけ表示する
		if (target.isSelfEvent()) {
			//すべてのイベントがSELDであることが確定
			int c = 0, lf = 0;
			for (List<ActionResultType> list : result.getResultType()) {//TGT
				s.append(user.getName());
				int effectIdx = 0;
				for (ActionResultType t : list) {
					ActionEvent e = action.getBattleEvent().get(effectIdx);
					switch (e.getParameterType()) {
						case STATUS:
							//ステータス効果の場合、ダメージ算出して表示
							if (t == ActionResultType.SUCCESS) {
								//ダメージ算出
								Map<StatusKey, Integer> damage = user.getStatus().calcDamage();
								if (damage.containsKey(hp)) {
									s.append(I18N.translate("TO"));
									damage.get(hp);
									s.append(I18N.translate("DAMAGE"));
								}
							} else {
								s.append(I18N.translate("IS"));
								s.append(I18N.translate("NODAMAGE"));
							}
							break;
						case ADD_CONDITION:
							//状態異常付与の場合、設置した状態異常を表示
							if (t == ActionResultType.SUCCESS) {
								//効果が発動した
								s.append(I18N.translate("IS"));
								s.append(action.getDesc());
							} else {
								//効果は発動しなった
								s.append(I18N.translate("TO"));
								s.append(I18N.translate("IS"));
								s.append(I18N.translate("NO_EFFECT"));
							}
							break;
						case REMOVE_CONDITION:
							//状態異常回復を表示
							if (t == ActionResultType.SUCCESS) {
								s.append(I18N.translate("IS"));
								s.append(action.getName());
								s.append(I18N.translate("REMOVE_CDNTION"));
							} else {
								s.append(I18N.translate("BUT"));
								s.append(I18N.translate("ACTION"));
								s.append(I18N.translate("ISFAILED"));
							}
							break;
						case ATTR_IN:
							//耐性変更の場合、下がった・上がったを表示
							if (t == ActionResultType.SUCCESS) {
								s.append(I18N.translate("IS"));
								s.append(action.getName());
								s.append(I18N.translate("ATTR_UP"));
							} else {
								s.append(I18N.translate("IS"));
								s.append(action.getName());
								s.append(I18N.translate("ATTR_DOWN"));
							}
							break;
						case ITEM_ADD:
							//アイテム追加イベントは入手した！を表示
							if (t == ActionResultType.SUCCESS) {
								s.append(I18N.translate("IS"));
								s.append(action.getName());
								s.append(I18N.translate("ITEM_ADD"));
							} else {
								s.append(I18N.translate("BUT"));
								s.append(I18N.translate("ACTION"));
								s.append(I18N.translate("ISFAILED"));
							}
							break;
						case ITEM_LOST:
							//アイテム破棄イベントはなくなった！を表示
							if (t == ActionResultType.SUCCESS) {
								s.append(I18N.translate("S"));
								s.append(action.getName());
								s.append(I18N.translate("WAS"));
								s.append(I18N.translate("ITEM_DROP"));
							} else {
								s.append(I18N.translate("BUT"));
								s.append(I18N.translate("ACTION"));
								s.append(I18N.translate("ISFAILED"));
							}
							break;
						case NONE:
							//NONEは何もしない
							break;
						default:
							throw new AssertionError("indefined parameter type : " + e);
					}
					effectIdx++;
				}
				c += s.length();
				if (c >= LF) {
					c = -s.length();
					s.append(Text.getLineSep());
					lf++;
				}
				if (lf > LINE) {
					//表示しきれない場合
					break;
				}
				s.append(" ");
			}
			messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
			currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
			setStage(Stage.EXECUTING_ACTION, "execAction");
			return;
		}
		if (target.isEmpty()) {
			throw new GameSystemException("set action message : target is empty");
		}
		//tgt.size == effect.size
		assert result.getResultType().size() == action.getBattleEvent().size() : "BS : result size is missmatch : " + result.getResultType().size() + " / " + action.getBattleEvent().size() + " / " + target.getUser() + " / " + target.getAction();

		int eventIdx = 0, tgtIdx = 0, ch = 0, lf = 0;
		for (List<ActionResultType> list : result.getResultType()) {//EVENT
			ActionEvent ae = action.getBattleEvent().get(eventIdx);
			tgtIdx = 0;
			for (ActionResultType t : list) {//TGT
				BattleCharacter c = result.getTarget().getTarget().get(tgtIdx);
				s.append(c.getStatus().getName());
				switch (ae.getParameterType()) {
					case ADD_CONDITION:
						//状態異常付与の場合、設置した状態異常を表示
						if (t == ActionResultType.SUCCESS) {
							//効果が発動した
							s.append(I18N.translate("IS"));
							s.append(action.getDesc());
						} else {
							//効果は発動しなった
							s.append(I18N.translate("TO"));
							s.append(I18N.translate("IS"));
							s.append(I18N.translate("NO_EFFECT"));
						}
						break;
					case ATTR_IN:
						//耐性変更の場合、下がった・上がったを表示
						if (t == ActionResultType.SUCCESS) {
							s.append(I18N.translate("IS"));
							s.append(action.getName());
							s.append(I18N.translate("ATTR_UP"));
						} else {
							s.append(I18N.translate("IS"));
							s.append(action.getName());
							s.append(I18N.translate("ATTR_DOWN"));
						}
						break;
					case ITEM_ADD:
						//アイテム追加イベントは入手した！を表示
						/*
						ITEM_ADD=を入手した
						ITEM_DROP=がなくなった
						 */
						if (t == ActionResultType.SUCCESS) {
							s.append(I18N.translate("IS"));
							s.append(action.getName());
							s.append(I18N.translate("ITEM_ADD"));
						} else {
							s.append(I18N.translate("BUT"));
							s.append(I18N.translate("ACTION"));
							s.append(I18N.translate("ISFAILED"));
						}
						break;
					case ITEM_LOST:
						//アイテム破棄イベントはなくなった！を表示
						if (t == ActionResultType.SUCCESS) {
							s.append(I18N.translate("S"));
							s.append(action.getName());
							s.append(I18N.translate("WAS"));
							s.append(I18N.translate("ITEM_DROP"));
						} else {
							s.append(I18N.translate("BUT"));
							s.append(I18N.translate("ACTION"));
							s.append(I18N.translate("ISFAILED"));
						}
						break;
					case STATUS:
						if (t == ActionResultType.SUCCESS) {
							//ダメージ算出
							Map<StatusKey, Integer> damage = c.getStatus().calcDamage();
							if (damage.containsKey(hp)) {
								s.append(I18N.translate("TO"));
								damage.get(hp);
								s.append(I18N.translate("DAMAGE"));
							}
						} else {
							s.append(I18N.translate("IS"));
							s.append(I18N.translate("NODAMAGE"));
						}
						break;
					case REMOVE_CONDITION:
						//状態異常回復を表示
						if (t == ActionResultType.SUCCESS) {
							s.append(I18N.translate("IS"));
							s.append(action.getName());
							s.append(I18N.translate("REMOVE_CDNTION"));
						} else {
							s.append(I18N.translate("BUT"));
							s.append(I18N.translate("ACTION"));
							s.append(I18N.translate("ISFAILED"));
						}
					case NONE:
						//NONEは何もしない
						break;
					default:
						throw new AssertionError("undefined parameter type " + ae);
				}

				ch += s.length();
				if (ch >= LF) {
					ch = -s.length();
					s.append(Text.getLineSep());
					lf++;
				}
				if (lf > LINE) {
					//表示しきれない場合
					break;
				}
				tgtIdx++;
			}

			eventIdx++;
		}

		messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
		currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
		setStage(Stage.EXECUTING_ACTION, "execAction");

	}

//resultに基づくアニメーションをthisに追加する
	private void setActionAnimation(BattleCharacter user, CmdAction action, BattleActionTarget target, ActionResult result) {
		animation.addAll(result.getAnimation());
		currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
		setStage(Stage.EXECUTING_ACTION, "setActionAnimation");
	}

	public void update() {
		battleFieldSystem.update();
		messageWindowSystem.update();
		targetSystem.update();
		enemies.forEach(v -> v.update());

		//エネミーとPCのY座標による描画順の更新
		Collections.sort(getAllChara(), (BattleCharacter o1, BattleCharacter o2) -> (int) (o1.getSprite().getY() - o2.getSprite().getY()));

		//ターゲットシステムのカレント表示位置更新
		if (targetSystem.getCurrentArea().isVisible()) {
			targetSystem.getCurrentArea().setLocationByCenter(currentCmd.getSpriteCenter());
		}

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
			int exp = enemies.stream().mapToInt(p -> (int) p.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.exp).getValue()).sum();
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
		List<AnimationSprite> removeList = new ArrayList<>();
		for (AnimationSprite a : animation) {
			if (a.getAnimation() == null) {//nullは基本入っていない
				removeList.add(a);
				continue;
			}
			if (a.getAnimation().isEnded() || !a.isVisible() || !a.isExist()) {
				removeList.add(a);
			}
		}
		animation.removeAll(removeList);

		//ステージ別処理
		GameSystem gs = GameSystem.getInstance();
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
					//PC全員逃げ判定、全員逃げた場合、戦闘終了
					if (party.stream().allMatch(p -> p.hasCondition(BattleConfig.ConditionName.escaped))) {
						//全員逃げた
						//アンターゲット状態異常付与（逃げる以外）の敵のEXPを合計して渡す
						int exp = 0;
						for (Enemy e : enemies) {
							if (e.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames()
									.stream().filter(p -> !p.equals(BattleConfig.ConditionName.escaped)).collect(Collectors.toList()))) {
								exp += (int) e.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.exp).getValue();
							}
						}
						battleResultValue = new BattleResultValues(BattleResult.ESCAPE, exp, new ArrayList<>(), winLogicName);
						setStage(Stage.BATLE_END, "update");
						messageWindowSystem.closeActionWindow();
						break;
					}
					//NPC全員逃げ判定
					if (enemies.stream().allMatch(p -> p.getStatus().hasCondition(BattleConfig.ConditionName.escaped))) {
						//アンターゲット状態異常付与（逃げる以外）の敵のEXPを合計して渡す
						int exp = 0;
						for (Enemy e : enemies) {
							if (e.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames()
									.stream().filter(p -> !p.equals(BattleConfig.ConditionName.escaped)).collect(Collectors.toList()))) {
								exp += (int) e.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.exp).getValue();
							}
						}
						battleResultValue = new BattleResultValues(BattleResult.ESCAPE, exp, new ArrayList<>(), winLogicName);
						//全員逃げた
						battleResultValue = new BattleResultValues(BattleResult.ESCAPE, 0, new ArrayList<>(), winLogicName);
						setStage(Stage.BATLE_END, "update");
						messageWindowSystem.closeActionWindow();
						break;
					}
					setStage(Stage.WAITING_USER_CMD, "update");
					break;
				}
				break;
			case WAITING_USER_CMD:
			case PLAYER_MOVE:
			case TARGET_SELECT:
				//プレイヤーの行動まちなので、何もしない。
				//コマンドウインドウ等から処理を実行される
				//次にバトルコマンドを取得したとき、NPCならNPCの行動のステージに入る。
				break;
			case SHOW_INFO_MSG:
				//INFOが閉じられるまで待つ
				if (!messageWindowSystem.isVisibleInfoMessage()) {
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case EXECUTING_ACTION:
				//カレントBATimeが切れるまで待つ
				assert currentBAWaitTime != null : "EXECITING_ACTION buf tc is null";
				if (currentBAWaitTime.isReaching()) {
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
				if (remMovePoint < currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue()) {
					return;
				}
				//アクションを抽選・・・このステージに入るときは必ずENEMYなのでキャスト失敗しない
				CmdAction eba = currentCmd.getBattleActionOf(((Enemy) currentCmd.getUser()).getAI(), ActionType.ATTACK);
				if (eba == null) {
					if (GameSystem.isDebugMode()) {
						System.out.println("enemy " + currentCmd.getUser().getStatus().getName() + " try afterMoveAttack, but dont have attackCMD");
					}
					return;
				}

				// イベント対象者別にターゲットを設定
				BattleActionTarget tgt = BattleTargetSystem.instantTarget(currentCmd.getUser(), eba);

				//ターゲットがいない場合、何もしない
				if (tgt.isEmpty()) {
					return;
				}

				//移動後攻撃実行
				ActionResult res = eba.exec(tgt);
				updateCondition();
				currentBAWaitTime = eba.createWaitTime();
				//メッセージ表示
				setActionMessage(currentCmd.getUser(), eba, tgt, res);
				//アニメーション追加
				setActionAnimation(currentCmd.getUser(), eba, tgt, res);
				//アクション実行中に入る
				setStage(Stage.EXECUTING_ACTION, "UPDATE");
				break;
			case BATLE_END:
				//何もしない（ユーザ操作待ち
				break;
			default:
				throw new AssertionError("UNDEFINED STAGE");

		}
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

	public void cancelTargetSelect() {
		messageWindowSystem.closeTooltipWindow();
		targetSystem.unsetCurrent();
		setStage(Stage.WAITING_USER_CMD, "cancelTargetSelect");
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

	public boolean userOperation() {
		return stage == Stage.WAITING_USER_CMD;
	}

	@LoopCall
	public boolean isEnd() {
		return stage == Stage.BATLE_END;
	}

	@LoopCall
	public boolean waitAction() {
		return stage == Stage.WAITING_USER_CMD;
	}

	List<Enemy> getEnemies() {
		return enemies;
	}

	@Deprecated
	public Stage getStage() {
		return stage;
	}

}
