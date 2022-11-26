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
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.field4.PlayerCharacterSprite;
import kinugasa.game.field4.VehicleStorage;
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
	//表示中バトルアクション・アニメーション
	private List<BattleActionAnimation> animation = new ArrayList<>();
	//実行中バトルアクションから生成された待機時間
	private FrameTimeCounter currentBAWaitTime;
	//実行中バトルアクション
	private BattleAction currentBA;
	//実行中バトルアクションのエリア
	private int currentBAArea;
	//行動中コマンド
	private BattleCommand currentCmd;
	//行動中エネミーの残行動力
	private int enemyMovPoint;
	//PCの行動範囲／攻撃範囲の点滅制御
	private FrameTimeCounter playerAreaBlinkCounter = new FrameTimeCounter(12);
	private boolean playerAreaVisible = false;

	//updateメソッドステージ
	public enum Stage {
		STARTUP,
		INITIAL_MOVE,
		WAITING_USER_CMD,
		TARGET_SELECT,
		EXECUTING_ACTION,
		EXECUTING_MOVE,
		BATLE_END
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

		//リセット
		currentCmd = null;
		currentBA = null;
		currentBAWaitTime = null;
		currentBAArea = 0;
		commandsOfThisTurn.clear();
		turn = 0;
		animation.clear();
		playerAreaBlinkCounter = new FrameTimeCounter(12);
		playerAreaVisible = false;

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
		//このターンのバトルコマンドを作成
		List<BattleCharacter> list = getAllChara();
		SpeedCalcModelStorage.getInstance().getCurrent().sort(list);

		//行動順にバトルコマンドを格納
		assert commandsOfThisTurn.isEmpty() : "turnStart:cmd is not empty";
		for (BattleCharacter c : list) {
			BattleCommand.Mode mode = c.isPlayer() ? BattleCommand.Mode.PC : BattleCommand.Mode.CPU;
			commandsOfThisTurn.add(new BattleCommand(mode, c));
		}

		if (GameSystem.isDebugMode()) {
			System.out.println("-----------------TURN START-----------------");
		}
		setStage(Stage.INITIAL_MOVE, "TURN_START");
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

	//次のコマンドを取得。NPCまたはPC。
	public BattleCommand getNextCmdAndExecNPCCmd() {
		if (commandsOfThisTurn.isEmpty()) {
			turnStart();
		}
		currentCmd = commandsOfThisTurn.getFirst();
		commandsOfThisTurn.removeFirst();
		if (GameSystem.isDebugMode()) {
			System.out.println("getNextCmdAndExecNPCCmd : " + currentCmd);
		}
		//NPC
		if (currentCmd.getMode() == BattleCommand.Mode.CPU) {
			//NPCアクションを実行、この中でステージも変わる
			execNPCAction();
			messageWindowSystem.closeCommandWindow();
			return currentCmd;
		}
		//PC
		//カレント更新コマンド内容をメッセージウインドウに表示
		messageWindowSystem.setCommand(currentCmd);
		currentBA = messageWindowSystem.getCommandWindow().getSelected();//初期選択を取得
		messageWindowSystem.setActionMessage("");
		messageWindowSystem.closeActionWindow();
		assert currentBA != null : "PCs battle action is null, BAMWs initial is missmatch";

		currentBAArea = (int) (currentBA.isMoveOnly()
				? currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue()
				: currentBA.getAreaWithEqip(currentCmd.getUser().getStatus()));

		currentBAWaitTime = null;

		playerAreaBlinkCounter.reset();
		playerAreaVisible = true;

		if (GameSystem.isDebugMode()) {
			System.out.println("getNextCmdAndExecNPCCmd : return PC_CMD" + currentCmd);
		}
		setStage(Stage.WAITING_USER_CMD, "getNextCmdAndExecNPCCmd");
		return currentCmd;

	}

	public boolean nextCmdIsNpc() {
		return !commandsOfThisTurn.getFirst().getUser().isPlayer();
	}

	public void execPCAction() {
		BattleAction ba = messageWindowSystem.getCommandWindow().getSelected();
		List<BattleCharacter> target = targetSystem.getSelected();
		List<BattleActionResult> result = ba.exec(GameSystem.getInstance(), currentCmd.getUser(), target);
		addMessage(ba, target, result);
		addAnimation(ba, target, result);
		currentBAWaitTime = ba.createWaitTime();
		setStage(Stage.EXECUTING_ACTION, "execPCAction");
	}

	private void execNPCAction() {
		//アサーション
		//そもそもカレントが入っていること
		assert currentCmd != null : "execNPCAction but currentCmd is null";

		//カレントコマンドがNPCであること
		if (currentCmd.getMode() == BattleCommand.Mode.PC) {
			throw new GameSystemException("execNpcAction but cmd is PC");
		}
		//実行中バトルアクションとそのエリアを設定
		EnemyBattleAction eba = currentCmd.getNPCActionExMove();
		currentBAArea = currentCmd.getAreaWithEqip(eba.getName());
		currentBA = eba;
		if (GameSystem.isDebugMode()) {
			System.out.println("execNPCAction : selected=" + eba);
		}

		//SELFアクションのみ場合は即時実行して終了
		if (eba.getTargetTypeList().stream().allMatch(p -> p == BattleActionTargetType.SELF)) {
			currentBAWaitTime = eba.createWaitTime();
			List<BattleActionResult> result = eba.exec(GameSystem.getInstance(), currentCmd.getUser());
			//メッセージ表示
			addMessage(eba, Arrays.asList(currentCmd.getUser()), result);
			//アニメーション追加
			addAnimation(eba, Arrays.asList(currentCmd.getUser()), result);
			setStage(Stage.EXECUTING_ACTION, "execNPCAction");
			return;
		}
		// SELFのみでないアクションの場合は攻撃範囲内に対象者がいるか確認
		enemyMovPoint = (int) currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue();
		List<BattleCharacter> targetList = getTargetOfCurrentCmd(eba);

		//いない場合で、行動アクションを持っている場合は行動を実行
		if (targetList.isEmpty() && currentCmd.hasMoveAction()) {
			//移動モードにして終了
			//一番近い敵を検索
			BattleCharacter nearTarget = targetSystem.nearPlayer(currentCmd.getUser().getSprite().getCenter());
			//ターゲット座標設定
			currentCmd.getUser().setTargetLocation(nearTarget.getSprite().getCenter(), currentBAArea);
			//メッセージ表示
			addMoveMessage();
			//アニメーションはない
			setStage(Stage.EXECUTING_MOVE, "execNPCAction");
			return;
		}
		//ターゲットがいない場合で、移動アクションを持っていない場合は何もしない
		if (targetList.isEmpty()) {
			setStage(Stage.EXECUTING_MOVE, "execNPCAction");
			return;
		}
		//ターゲットがいる場合、アクションを実行
		currentBAWaitTime = eba.createWaitTime();
		List<BattleActionResult> result = eba.exec(GameSystem.getInstance(), currentCmd.getUser());
		//メッセージ表示
		addMessage(eba, targetList, result);
		//アニメーション追加
		addAnimation(eba, targetList, result);
		setStage(Stage.EXECUTING_ACTION, "execNPCAction");
	}

	private void addAnimation(BattleAction ba, List<BattleCharacter> target, List<BattleActionResult> result) {
		assert ba.getEvents().size() == result.size() : "battle action event size is mismatch";
		for (int i = 0; i < ba.getEvents().size(); i++) {
			BattleActionEvent e = ba.getEvents().get(i);
			BattleActionResult r = result.get(i);

			if (r == BattleActionResult.MISS || r == BattleActionResult.NONE || r == BattleActionResult.STOPED || r == BattleActionResult.MOVE) {
				if (GameSystem.isDebugMode()) {
					System.out.println("addAnimatio, but result is " + r);
				}
				continue;
			}
			//ターゲットにアニメーションの複製を配置
			if (e.getBatt() == BattleActionTargetType.FIELD) {
				BattleActionAnimation a = e.getAnimationClone();
				switch (a.getAnimationTargetType()) {
					case BATTLE_FIELD_AREA:
						//フィールド全体を対象とする
						a.getAnimationSprite().setLocation(battleFieldSystem.getBattleFieldAllArea());
						animation.add(a);
						break;
					case TEAM_AREA:
						if (ba instanceof EnemyBattleAction) {
							//敵のコマンドなので、味方の領域に設定
							float x = battleFieldSystem.getEnemytArea().x;
							float y = battleFieldSystem.getEnemytArea().y;
							a.getAnimationSprite().setLocation(x, y);
							animation.add(a);
						} else {
							//味方のコマンドなので敵の領域に設定
							float x = battleFieldSystem.getPartyArea().x;
							float y = battleFieldSystem.getPartyArea().y;
							a.getAnimationSprite().setLocation(x, y);
							animation.add(a);
						}
						break;
					default:
						throw new GameSystemException("FIELD ANIMATION, but targetType is " + a.getAnimationTargetType());
				}
				continue;
			}
			if (e.getBatt() == BattleActionTargetType.SELF) {
				//SELFイベントなので、対象者は自分自身とする
				BattleActionAnimation a = e.getAnimationClone();
				a.getAnimationSprite().setLocation(currentCmd.getUser().getSprite());
				animation.add(a);
				continue;
			}
			// ターゲットありのアニメーション
			assert !target.isEmpty() : "addAnimation, but target is empty";
			BattleActionAnimation a = e.getAnimationClone();
			for (BattleCharacter bc : target) {
				BattleActionAnimation ani = a.clone();
				ani.getAnimationSprite().setLocation(bc.getSprite());
				animation.add(ani);
			}
		}
	}

	//カレントのキャラが移動した旨をアクションメッセージウインドウに表示
	private void addMoveMessage() {
		String text = currentCmd.getUser().getStatus().getName();
		text += I18N.translate("ISMOVE");
		getMessageWindowSystem().setActionMessage(text);
	}

	// アクションに基づく行動結果をアクションメッセージウインドウに設定
	private String addMessage(BattleAction ba, List<BattleCharacter> target, List<BattleActionResult> result) {
		StringBuilder s = new StringBuilder();
		//イベントに対する成否をまとめる
		assert ba.getEvents().size() == result.size() : "action event size is mismatch";
		Map<BattleActionEvent, BattleActionResult> actionResult = new HashMap<>();
		List<BattleActionEvent> events = ba.getEvents();
		for (int i = 0; i < result.size(); i++) {
			actionResult.put(events.get(i), result.get(i));
		}

		//行動
		s.append(currentCmd.getUser().getStatus().getName());
		s.append(I18N.translate("S"));
		s.append(I18N.translate(ba.getBattleActionType().toString()));
		s.append("「");
		s.append(ba.getName());
		s.append("」");
		//失敗時
		if (actionResult.values().stream().allMatch(p -> p == BattleActionResult.MISS)) {
			s.append(I18N.translate("ISFAILED"));
			return s.toString();
		}
		if (actionResult.values().stream().allMatch(p -> p == BattleActionResult.STOPED)) {
			throw new GameSystemException("battle action result is STOPED");
		}
		if (actionResult.values().stream().allMatch(p -> p == BattleActionResult.NONE)) {
			throw new GameSystemException("battle action result is NONE");
		}
		s.append(Text.getLineSep());

		//ダメージ
		//N人ずつ改行
		int lfN = 6;
		int i = 0;
		for (BattleCharacter c : target) {
			Map<StatusKey, Integer> damage = c.getStatus().calcDamage();
			//outputLogStatusKeyへのダメージがない場合
			if (!damage.containsKey(StatusKeyStorage.getInstance().get(BattleConfig.outputLogStatusKey))) {
				continue;
			}
			int value = damage.get(StatusKeyStorage.getInstance().get(BattleConfig.outputLogStatusKey));
			value = Math.abs(value);
			boolean isPlus = value >= 0;
			if (isPlus) {
				s.append(c.getStatus().getName());
				s.append(I18N.translate("IS")).append(value).append(I18N.translate("HEALDAMAGE"));
			} else {
				s.append(c.getStatus().getName());
				s.append(I18N.translate("TO")).append(value).append(I18N.translate("DAMAGE"));
			}
			s.append("  ");

			i++;
			if (i > lfN) {
				s.append(Text.getLineSep());
				i = 0;
			}
		}

		return s.toString();
	}

	public void toTargetSelectOrPCMoveMode() {
		//移動モードまたはターゲット選択に入る
		
		
		BattleAction selected = messageWindowSystem.getCommandWindow().getSelected();
		currentBA = selected;

		setStage(Stage.TARGET_SELECT, "toTargetSelectMode");
	}

	public void update() {
		battleFieldSystem.update();
		messageWindowSystem.update();
		targetSystem.update();
		enemies.forEach(v -> v.update());

		GameSystem gs = GameSystem.getInstance();

		if (playerAreaBlinkCounter.isReaching()) {
			playerAreaBlinkCounter.reset();
			playerAreaVisible = !playerAreaVisible;
		}

		//ステータス変更による状態異常の付与
		conditionManager.setCondition(GameSystem.getInstance().getPartyStatus());
		conditionManager.setCondition(enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList()));
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
					//確率判定
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
		removeEndAnimation();

		switch (stage) {
			case INITIAL_MOVE:
				//プレイヤーキャラクターが目標の座標に近づくまで移動を実行、目的地に近づいたらステージを変える
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
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case WAITING_USER_CMD:
				//プレイヤーの行動まちなので、何もしない。
				//コマンドウインドウ等から処理を実行される
				//次にバトルコマンドを取得したとき、NPCならNPCの行動のステージに入る。
				break;
			case TARGET_SELECT:
				break;
			case EXECUTING_ACTION:
				assert currentBAWaitTime != null : "EXECITING_ACTION buf tc is null";
				if (currentBAWaitTime.isReaching()) {
					currentBAWaitTime = null;
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case EXECUTING_MOVE:
				//移動実行
				currentCmd.getUser().moveToTgt();
				enemyMovPoint--;
				//移動ポイントが切れた場合、移動終了してユーザコマンド待ちに移行
				if (enemyMovPoint <= 0 || !currentCmd.getUser().isMoving()) {
					currentCmd.getUser().unsetTarget();
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
					return;
				}
				//移動ポイントが切れていない場合、対象がいるか判定
				//アクションを抽選
				EnemyBattleAction eba = currentCmd.getNPCActionExMove();
				//batt=FIELDの場合、即時実行
				if (eba.getEvents().stream().allMatch(p -> p.getBatt() == BattleActionTargetType.FIELD)) {
					//即時実行
					List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser());
					currentBAWaitTime = eba.createWaitTime();
					//メッセージ表示
					addMessage(eba, Collections.emptyList(), result);
					//アニメーション追加
					addAnimation(eba, Collections.emptyList(), result);
					setStage(Stage.EXECUTING_ACTION, "UPDATE");
					return;
				}
				//batt==SELFの場合、即時実行
				if (eba.getEvents().stream().allMatch(p -> p.getBatt() == BattleActionTargetType.SELF)) {
					//即時実行
					List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser());
					currentBAWaitTime = eba.createWaitTime();
					//メッセージ表示
					addMessage(currentBA, Arrays.asList(currentCmd.getUser()), result);
					//アニメーション追加
					addAnimation(currentBA, Arrays.asList(currentCmd.getUser()), result);
					setStage(Stage.EXECUTING_ACTION, "UPDATE");
					return;
				}

				// イベント対象者別にターゲットを設定
				List<BattleCharacter> target = getTargetOfCurrentCmd(eba);
				if (target.isEmpty()) {
					//行動しようとしたが、対象者がいない場合終了
//					if (GameSystem.isDebugMode()) {
//						System.out.println("target is empty");
//					}
					return;
				}
				//攻撃実行
				List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser(), target);
				currentBAWaitTime = eba.createWaitTime();
				//メッセージ表示
				addMessage(currentBA, target, result);
				//アニメーション追加
				addAnimation(currentBA, target, result);
				setStage(Stage.EXECUTING_ACTION, "UPDATE");

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
		for (BattleActionEvent e : eba.getEvents()) {
			switch (e.getBatt()) {
				case ALL:
					target.addAll(targetSystem.getAllTarget(currentCmd.getSpriteCenter(), enemyMovPoint));
					break;
				case ONE_ENEMY:
					List<BattleCharacter> l1 = targetSystem.nearPlayer(currentCmd.getSpriteCenter(), enemyMovPoint);
					if (!l1.isEmpty()) {
						target.add(l1.get(0));
					}
					break;
				case ONE_PARTY:
					List<BattleCharacter> l2 = targetSystem.nearEnemy(currentCmd.getSpriteCenter(), enemyMovPoint);
					if (!l2.isEmpty()) {
						target.add(l2.get(0));
					}
					break;
				case TEAM_ENEMY:
					target.addAll(targetSystem.nearPlayer(currentCmd.getSpriteCenter(), enemyMovPoint));
					break;
				case TEAM_PARTY:
					target.addAll(targetSystem.nearEnemy(currentCmd.getSpriteCenter(), enemyMovPoint));
					break;
				case RANDOM_ONE:
					List<BattleCharacter> l3 = targetSystem.getAllTarget(currentCmd.getSpriteCenter(), enemyMovPoint);
					if (!l3.isEmpty()) {
						Collections.shuffle(l3);
						target.add(l3.get(0));
					}
					break;
				case RANDOM_ONE_ENEMY:
					List<BattleCharacter> l4 = targetSystem.nearPlayer(currentCmd.getSpriteCenter(), enemyMovPoint);
					if (!l4.isEmpty()) {
						Collections.shuffle(l4);
						target.add(l4.get(0));
					}
					break;
				case RANDOM_ONE_PARTY:
					List<BattleCharacter> l5 = targetSystem.nearEnemy(currentCmd.getSpriteCenter(), enemyMovPoint);
					if (!l5.isEmpty()) {
						Collections.shuffle(l5);
						target.add(l5.get(0));
					}
					break;
				case FIELD:
				case SELF:
					throw new GameSystemException("FIELD and SELF event is not executed");
			}
		}
		target = target.stream().distinct().collect(Collectors.toList());
		return target;
	}

	private void removeEndAnimation() {
		List<BattleActionAnimation> removeList = new ArrayList<>();
		removeList.addAll(animation.stream().filter(p -> p.isEnded()).collect(Collectors.toList()));
		animation.removeAll(removeList);
	}

	@Override
	public void draw(GraphicsContext g) {
		battleFieldSystem.draw(g);
		enemies.forEach(v -> v.draw(g));
		GameSystem.getInstance().getPartySprite().forEach(v -> v.draw(g));
		animation.forEach(v -> v.draw(g));
		targetSystem.draw(g);
		//プレイヤーキャラクタの行動エリアを表示
		if (currentCmd != null) {
			if (currentCmd.getMode() == BattleCommand.Mode.PC) {
				if (playerAreaVisible) {
					if (currentBAArea != 0) {
						Graphics2D g2 = g.create();
						g2.setColor(Color.GREEN);
						int x = (int) currentCmd.getUser().getSprite().getCenterX() - currentBAArea;
						int y = (int) currentCmd.getUser().getSprite().getCenterY() - currentBAArea;
						g2.drawOval(x, y, currentBAArea * 2, currentBAArea * 2);
						g2.dispose();
					}
				}
			}
		}

		messageWindowSystem.draw(g);
	}

	public void setBattleAction(BattleAction ba, int area) {
		currentBA = ba;
		currentBAArea = area;
	}

	public Stage getStage() {
		return stage;
	}

	public BattleTargetSystem getTargetSystem() {
		return targetSystem;
	}

	public BattleMessageWindowSystem getMessageWindowSystem() {
		return messageWindowSystem;
	}

	public String getWinLogicName() {
		return winLogicName;
	}

	public String getLoseLogicName() {
		return loseLogicName;
	}

	public BattleCommand getCurrentCmd() {
		return currentCmd;
	}

	public FrameTimeCounter getCurrentBAWaitTime() {
		return currentBAWaitTime;
	}

	public int getCurrentBAArea() {
		return currentBAArea;
	}

	public BattleAction getCurrentBA() {
		return currentBA;
	}

	public LinkedList<BattleCommand> getCommandsOfThisTurn() {
		return commandsOfThisTurn;
	}

	public List<BattleActionAnimation> getAnimation() {
		return animation;
	}

	public BattleFieldSystem getBattleFieldSystem() {
		return battleFieldSystem;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public ConditionManager getConditionManager() {
		return conditionManager;
	}

}
