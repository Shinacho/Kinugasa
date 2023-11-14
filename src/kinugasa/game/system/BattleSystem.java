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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kinugasa.game.GameLog;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.LoopCall;
import kinugasa.game.NewInstance;
import kinugasa.game.NotNewInstance;
import kinugasa.game.OneceTime;
import kinugasa.game.ui.Text;
import kinugasa.object.Drawable;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.object.Sprite;
import kinugasa.resource.sound.Sound;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;
import kinugasa.game.NotNull;
import static kinugasa.game.system.Action.死亡者ターゲティング.気絶損壊解脱者を選択可能;
import static kinugasa.game.system.ActionType.行動;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.object.Effect;
import kinugasa.object.FlashEffect;
import kinugasa.object.ImageSprite;

/**
 *
 * @vesion 1.0.0 - 2023/05/10_14:07:36<br>
 * @author Shinacho<br>
 */
public class BattleSystem implements Drawable {

	private static final BattleSystem INSTANCE = new BattleSystem();

	public static BattleSystem getInstance() {
		return INSTANCE;
	}

	private BattleSystem() {
	}

	public enum Stage {
		未使用,
		/**
		 * 開始?初期移動まで
		 */
		開始_to_初期移動開始,
		/**
		 * 初期移動中
		 */
		初期移動中,
		/**
		 * ターン開始がゲームから呼ばれるのを待っている。これは同期待ちである。
		 */
		ターン開始コール待機,
		/**
		 * ターン開始処理が完了して、EXECが呼ばれるのを待っている。
		 */
		EXECコール待機,
		/**
		 * 自動処理のイベントを処理していて、アニメーションを再生している等で待機しているが、時間経過で進む。
		 */
		待機中＿時間あり＿手番送り,
		/**
		 * 自動処理のイベントを処理していて、アニメーションを再生している等で待機しているが、時間経過で進む。
		 */
		待機中＿時間あり＿手番戻り,
		/**
		 * コマンド選択中
		 */
		コマンド選択中,
		/**
		 * PC逃げアニメーション実行中。終わったらWAITに入る。
		 */
		PC逃げアニメーション実行中,
		/**
		 * プレイヤーキャラクタ移動中。確定アクションが呼ばれるまで何もしない。
		 * この時、キャラの現在の行動力を更新して、ターゲットシステムをアップデートする必要がある。
		 * 入る前にターゲットシステムの各種可視状態を確認せよ。 INFO_WINDOWにも残り移動力の割合を表示しなければならない。
		 */
		プレイヤキャラ移動中_コミット待ち,
		/**
		 * ターゲット選択中。execが呼ばれるまで何もしない。
		 */
		プレイヤキャラターゲット選択中_コミット待ち,
		/**
		 * 自動処理実行中。
		 */
		待機中＿処理中,
		/**
		 * 敵の逃走アニメーションを実行中。
		 */
		待機中＿敵逃走中,
		/**
		 * 画面効果を再生している
		 */
		エフェクト再生中_終了待ち,
		/**
		 * 移動後行動選択中
		 */
		移動後行動選択中_コミット待ち,
		/**
		 * ステータス閲覧中
		 */
		ステータス閲覧中_閉じる待ち,
		/**
		 * アイテム用途選択画面
		 */
		アイテム用途選択画面表示中,
		/**
		 * アイテム詳細確認中
		 */
		アイテム詳細画面表示中,
		/**
		 * 敵移動実行中。終わったらWAITに入る。
		 */
		待機中＿敵移動中,
		/**
		 * バトルは終了して、ゲームシステムからの終了指示を待っている。
		 */
		バトル終了済み,
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
	private Sound prevBGM;
	//戦闘BGM
	private Sound currentBGM;
	//勝利BGM
	private Sound winBGM;
	//勝利遷移ロジック名、敗北遷移ロジック名
	private String winLogicName, loseLogicName;
	//--------------------------------------------------------表示中・実行中
	//敵のスプライトとステータス
	private List<Enemy> enemies = new ArrayList<>();
	//このターンのバトルコマンド順序
	private LinkedList<BattleCommand> commandsOfThisTurn = new LinkedList<>();
	//ターンごとの魔法詠唱完了イベント
	private LinkedHashMap<Integer, List<MagicSpell>> magics = new LinkedHashMap<>();
	//表示中バトルアクション・アニメーション
	private List<Sprite> animation = new ArrayList<>();
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
	//現在のステージ
	private Stage prevStage, stage;
	//-------------------------------------------------------------------システム
	//メッセージウインドウシステムのインスタンス
	private BattleMessageWindowSystem messageWindowSystem;
	//ターゲット選択システムのインスタンス
	private BattleTargetSystem targetSystem;
	//バトルフィールドインスタンス
	private BattleFieldSystem battleFieldSystem;
	//バトル終了フラグ（true=終了
	private boolean end = false;
	//戦況図モードかどうか
	private boolean showMode = false;
	//AfterMoveAction更新用の前回検査時の攻撃可否
	private boolean prevAttackOK = false;
	//移動後攻撃モードかどうか
	private boolean afterMove = false;
	//詠唱中アニメーション
	private Map<Actor, Sprite> castingSprites = new HashMap<>();
	//スクリーンエフェクト
	private Effect effect;
	//メッセージキュー
	private LinkedList<ActionResult.EventActorResult> messageQueue = new LinkedList<>();
	//-----------------------------------------------------------アイテム
	//アイテムChoiceUseインデックス：-1：ターゲット選択未使用
	private int itemChoiceMode = -1;
	//アイテム使用とパスのアイテム本体
	private Item itemPassAndUse;

	@OneceTime
	public void encountInit(EncountInfo enc) {
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print(" -----------------BATTLE_START------------------------------------------------");
		}
		setStage(Stage.開始_to_初期移動開始);
		//エンカウント情報の取得
		EnemySetStorage ess = enc.getEnemySetStorage().build();
		EnemySet es = ess.get();
		winLogicName = es.getWinLogicName();
		loseLogicName = es.getLoseLogicName();
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
				default:
					throw new AssertionError("BS undefined bgm mode : " + es);
			}
		}
		//バトルBGMの再生
		if (es.hasBgm()) {
			currentBGM = es.getBgm().load();
			currentBGM.stopAndPlay();
		}
		winBGM = enc.getEnemySetStorage().get().getWinBgm();
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
		messageWindowSystem.init();
		//念のため全員のアクションを更新
		gs.getParty().forEach(p -> p.getStatus().updateAction());
		enemies.forEach(p -> p.getStatus().updateAction());

		//出現MSG設定用マップ
		Map<String, Long> enemyNum
				= enemies.stream().sorted().collect(Collectors.groupingBy(
						Enemy::getVisibleNameNoNumber,
						Collectors.counting()));
		//出現MSG設定
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Long> e : enemyNum.entrySet()) {
			sb.append(I18N.get(GameSystemI18NKeys.XがX体現れた, e.getKey(), e.getValue() + "")).append(Text.getLineSep());
		}
		//敵出現情報をセット
		setMsg(sb.toString());
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);

		//リセット
		currentCmd = null;
		currentBAWaitTime = null;
		commandsOfThisTurn.clear();
		turn = 0;
		animation.clear();
		targetSystem.unset();
		afterMove = false;

		//敵の配置
		putEnemy();

		//味方の配置
		putParty();
		assert partyTargetLocationForFirstMove.size() == gs.getParty().size() : "initial move target is missmatch";

		//初期移動実行へ
		setStage(BattleSystem.Stage.初期移動中);
	}

	@OneceTime
	private void putParty() {
		GameSystem gs = GameSystem.getInstance();
		partyInitialDir.clear();
		partyInitialLocation.clear();
		partyTargetLocationForFirstMove.clear();

		//戦闘開始前位置・向き退避
		List<PCSprite> partySprite = gs.getPartySprite();
		List<Status> partyStatus = gs.getPartyStatus();
		for (PCSprite s : partySprite) {
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
			partySprite.get(i).setVector(new KVector(KVector.WEST, BattleConfig.BATTLE_WALK_SPEED));
			size = partySprite.get(i).getImageHeight();
			y += size * 2;
		}

	}

	@OneceTime
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

	void setEndStatus(BattleResult v) {

		//敵番号の初期化
		EnemyBlueprint.initEnemyNoMap();
		//戦闘中専用コンディションの解除
		for (Actor a : GameSystem.getInstance().getParty()) {
			if (a.getStatus().hasCondition(ConditionKey.逃走した)) {
				a.getStatus().removeCondition(ConditionKey.逃走した);
				a.getSprite().setVisible(true);
			}
		}
		GameSystem.getInstance().getParty().forEach(p -> p.getStatus().removeCondition(ConditionKey.逃走した));
		GameSystem.getInstance().getParty().forEach(p -> p.getStatus().removeCondition(ConditionKey.詠唱中));
		GameSystem.getInstance().getParty().forEach(p -> p.getStatus().removeCondition(ConditionKey.防御中));
		GameSystem.getInstance().getParty().forEach(p -> p.getStatus().removeCondition(ConditionKey.回避中));
		//友好的召喚者の消去
		List<Actor> remove = new ArrayList<>();
		for (Actor a : GameSystem.getInstance().getParty()) {
			if (a.isSummoned()) {
				remove.add(a);
			}
		}
		GameSystem.getInstance().getParty().removeAll(remove);

		//倒した敵の数カウント
		for (Actor a : enemies) {
			if (a.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.気絶, ConditionKey.損壊)) {
				Counts.getInstance().add1count(GameSystemI18NKeys.CountKey.倒した敵の数);
			}
		}

		List<Item> dropItems = new ArrayList<>();
		List<Material> dropMaterials = new ArrayList<>();
		int exp = 0;
		String nextLogicName = winLogicName;

		//味方が逃げた場合と負けた場合はドロップアイテムを獲得できない
		switch (v) {
			case 敗北_こちらが全員逃げた:
			case 敗北_味方全滅: {
				for (Enemy e : enemies) {
					if (e.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.気絶, ConditionKey.損壊)) {
						exp += (int) e.getStatus().getEffectedStatus().get(StatusKey.保有経験値).getValue();
					}
				}
				nextLogicName = loseLogicName;
				break;
			}
			case 勝利_敵が全員逃げた:
			case 勝利_敵全滅: {
				//倒した敵のドロップアイテム回収
				for (Enemy e : enemies) {
					if (e.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.気絶, ConditionKey.損壊)) {
						exp += (int) e.getStatus().getEffectedStatus().get(StatusKey.保有経験値).getValue();
						for (DropItem i : e.getDropItem()) {
							if (Random.percent(i.getP())) {
								//個数
								int n = Random.randomAbsInt(i.getN() - 1) + 1;
								if (i.getItem() == null) {
									for (int j = 0; j < n; j++) {
										dropMaterials.add(i.getMaterial());
									}
								} else {
									for (int j = 0; j < n; j++) {
										dropItems.add(i.getItem().clone());
									}
								}
							}
						}
					}
				}
				break;
			}
		}
		//リザルトセット
		battleResultValue = new BattleResultValues(v, exp, dropItems, dropMaterials, nextLogicName);
		String text = "---" + I18N.get(GameSystemI18NKeys.戦闘結果) + "---" + Text.getLineSep();
		text += v.getVisibleName() + Text.getLineSep();
		text += I18N.get(GameSystemI18NKeys.獲得経験値) + ":" + exp + Text.getLineSep();
		text += I18N.get(GameSystemI18NKeys.獲得アイテム) + ":" + Text.getLineSep();
		if (dropItems.isEmpty()) {
			text += " " + I18N.get(GameSystemI18NKeys.なし);
		} else {
			for (Item ii : dropItems) {
				text += " " + ii.getVisibleName() + Text.getLineSep();
			}
		}
		text += I18N.get(GameSystemI18NKeys.獲得物資) + ":" + Text.getLineSep();;
		if (dropMaterials.isEmpty()) {
			text += " " + I18N.get(GameSystemI18NKeys.なし);
		} else {
			Map<String, Long> map = dropMaterials.stream().collect(Collectors.groupingBy(Material::getVisibleName, Collectors.counting()));
			for (String m : map.keySet()) {
				text += " " + m + "×" + map.get(m) + Text.getLineSep();
			}
		}
		//レベルアップシステムの起動
		//レベルアップ結果がある場合はメッセージに追加
		for (Actor a : LevelSystem.addExp(exp)) {
			text += I18N.get(GameSystemI18NKeys.Xはレベルアップできる, a.getVisibleName()) + Text.getLineSep();
		}
		messageWindowSystem.getBattleResultW().setText(text);
		messageWindowSystem.getBattleResultW().allText();
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.BATTLE_RESULT);
		currentBGM.stop();
		currentBGM.dispose();
		winBGM.load().stopAndPlay();

		setStage(Stage.バトル終了済み);
	}

	@OneceTime
	void endBattle() {
		//味方の配置の初期化
		List<PCSprite> partySprite = GameSystem.getInstance().getPartySprite();
		for (int i = 0; i < partySprite.size(); i++) {
			//位置の復元
			partySprite.get(i).to(partyInitialDir.get(i));
			partySprite.get(i).setLocation(partyInitialLocation.get(i));
		}
		currentBGM.stop();
		currentBGM.dispose();
		winBGM.stop();//強制実行
		winBGM.dispose();
		if (prevBGM != null) {
			prevBGM.play();
		}
		enemies.clear();
		end = true;
		setStage(Stage.未使用);
	}

	private void turnStart() {
		turn++;
		if (GameSystem.isDebugMode()) {
			GameLog.print(" -----------------TURN[" + turn + "] START-----------------");
		}
		//PC・NPCの状態異常の経過ターン更新・継続ダメージ処理
		boolean conditionMsg = false;
		List<String> text = new ArrayList<>();
		if (turn != 1) {
			for (Actor a : allActors()) {
				String s = a.getStatus().updateCondition();
				if (s != null) {
					text.add(s);
				}
			}
		}

		//このターンのバトルコマンドを作成
		commandsOfThisTurn = SpeedCalcSystem.doExec(allActors(),
				magics.containsKey(turn) ? magics.get(turn) : Collections.emptyList());

		//このターンの魔法詠唱完了をバトルコマンドに置いたので、マップからは破棄
		if (magics.containsKey(turn)) {
			magics.remove(turn);
		}

		//コマンドがパーティーだけの場合、戦闘終了
		if (commandsOfThisTurn.stream().allMatch(p -> p.getUser().isPlayer())) {
			setEndStatus(BattleResult.勝利_敵全滅);
			return;
		}
		//コマンドが敵だけの場合、戦闘終了
		if (commandsOfThisTurn.stream().allMatch(p -> !p.getUser().isPlayer())) {
			setEndStatus(BattleResult.敗北_味方全滅);
			return;
		}

		//状態異常追加MSGがある場合、アクションで待つ。
		if (!text.isEmpty()) {
			currentBAWaitTime = new FrameTimeCounter(100);
			setMsg(text);
			setStage(Stage.待機中＿時間あり＿手番送り);
			return;
		}
		setStage(Stage.EXECコール待機);
	}

	public enum BSExecResult {
		PCのコマンド選択に入った,
		STAGEが待機中の間待機しその後EXECを再度コールせよ,
	}

	// このターンの次のコマンドを実行する。もしPCの場合はPCを返す。
	public BSExecResult exec() {
		setStage(Stage.待機中＿処理中);
		if (commandsOfThisTurn.isEmpty()) {
			turnStart();
		}
		afterMove = false;
		currentCmd = commandsOfThisTurn.getFirst();
		assert currentCmd != null : "currentCMD is null";
		commandsOfThisTurn.removeFirst();
		Actor user = currentCmd.getUser();
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print(" currentCMD:" + currentCmd);
		}
		//ターゲットシステム初期化
		targetSystem.unset();
		currentBAWaitTime = null;
		//この時点でアンターゲット状態異常の場合、メッセージ出さずに次に送る
		if (user.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.損壊, ConditionKey.気絶, ConditionKey.逃走した)) {
			if (GameSystem.isDebugMode()) {
				GameLog.print(user.getVisibleName() + " is bad condition");
			}
			return exec();
		}
		//防御または回避中の場合、1ターンだけ有効なため、今回そのフラグを外す
		if (user.getStatus().hasCondition(ConditionKey.防御中)) {
			user.getStatus().removeCondition(ConditionKey.防御中);
		}
		if (user.getStatus().hasCondition(ConditionKey.回避中)) {
			user.getStatus().removeCondition(ConditionKey.回避中);
		}

		//状態異常で動けないときスキップ（メッセージは出す
		//詠唱中の場合はのぞく
		if (!user.getStatus().hasCondition(ConditionKey.詠唱中)) {
			if (Random.percent(user.getStatus().getConditionFlags().getP().停止)) {
				assert user.getStatus().getConditionFlags().get停止理由() != null : "stop desc is null  : " + user + " / " + this;
				setMsg(user.getVisibleName()
						+ user.getStatus().getConditionFlags().get停止理由().getExecMsgI18Nd());
				currentBAWaitTime = new FrameTimeCounter(100);
				setStage(Stage.待機中＿時間あり＿手番送り);
				return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
			}
		}
		//詠唱中でも行動できない状態異常を持っている場合は行動できない
		if (user.getStatus().hasCondition(ConditionKey.詠唱中)) {
			//停止理由はConditionFlagに2つ置けないので、手動で探す
			ConditionKey 停止理由 = null;
			for (ConditionKey k : List.of(ConditionKey.眠り, ConditionKey.麻痺)) {
				if (user.getStatus().hasCondition(k)) {
					停止理由 = k;
				}
			}
			assert 停止理由 != null : "unknown stop desc : " + user + "/ " + this;
			setMsg(user.getVisibleName() + 停止理由.getExecMsgI18Nd());
			currentBAWaitTime = new FrameTimeCounter(100);
			setStage(Stage.待機中＿時間あり＿手番送り);
			return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;

		}
		//混乱で動けないときは、アクションを適当に取得して自動実行する
		if (Random.percent(user.getStatus().getConditionFlags().getP().混乱)) {
			//混乱
			currentCmd.setUserOperation(false);//ユーザオペレーション要否を不要に設定
			Action a = currentCmd.randomAction();
			Actor tgt = BattleTargetSystem.random(user, a);
			if (tgt == null) {
				//ターゲット不在なので何もできなかった
				setMsg(Xは混乱していてXを実行したが何も起こらなかったのMSG(user, a));
				currentBAWaitTime = new FrameTimeCounter(100);
				setStage(Stage.待機中＿時間あり＿手番送り);
				return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
			} else {
				//ターゲット存在なので実行
				execAction(new ActionTarget(user, a, List.of(tgt), false));
			}
			return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
		}
		//魔法詠唱完了イベントの場合、PCでもNPCでも自動実行、（詠唱中コンディションを外す
		//魔法のコストとターゲットは、詠唱開始と終了の2回判定する。
		//ここは「詠唱終了時」の処理。
		if (currentCmd.isMagicSpell()) {
			//詠唱中コンディションとアニメーション除去
			user.getStatus().removeCondition(ConditionKey.詠唱中);
			castingSprites.remove(user);
			//魔法アクション取得
			Action a = currentCmd.getFirstBattleAction();//1つしか入っていない

			//---------------ターゲット再検査------------------
			//セーブしたターゲットを取得
			List<Actor> tgtList = targetSystem.getSavedTarget(user);
			//セーブターゲットエラーでないこと
			assert tgtList != null : "BS TSs saved tgt is error : " + this;
			//ターゲット不在理由
			NoTgtDesc notgtDesc = null;
			//保存したターゲットから距離敵に外れた対象を除去
			int tgtSize = tgtList.size();
			tgtList = BattleTargetSystem.recalcDistance(tgtList, user.getSprite().getCenter(), user.getStatus().getEffectedArea(a));
			if (tgtList.size() != tgtSize) {
				notgtDesc = NoTgtDesc.しかし効果範囲内に対象者はいなかった;
			}
			//保存したターゲットからコンディションで外れた対象者を除外
			//アクションがアンターゲット可能である場合は外さない
			{
				List<Actor> remove = new ArrayList<>();
				for (Actor ac : tgtList) {
					switch (a.getDeadTgt()) {
						case 気絶損壊解脱者を選択可能: {
							//処理なし
							break;
						}
						case 気絶損壊解脱者は選択不可能: {
							if (ac.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.損壊, ConditionKey.気絶)) {
								remove.add(ac);
							}
							break;
						}
						case 損壊者を選択可能: {
							if (ac.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.気絶)) {
								remove.add(ac);
							}
							break;
						}
						case 気絶者を選択可能: {
							if (ac.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.損壊)) {
								remove.add(ac);
							}
							break;
						}
						case 解脱者を選択可能: {
							if (ac.getStatus().hasAnyCondition(ConditionKey.損壊, ConditionKey.気絶)) {
								remove.add(ac);
							}
							break;
						}
					}

				}
				tgtList.removeAll(remove);
				if (!remove.isEmpty()) {
					notgtDesc = NoTgtDesc.しかし対象者はすでに意識がない;
				}
			}
			//ターゲットがいない場合失敗
			if (tgtList.isEmpty()) {
				setMsg(詠唱したがターゲットがいないのMSG(user, a, notgtDesc));
				currentBAWaitTime = new FrameTimeCounter(100);
				setStage(Stage.待機中＿時間あり＿手番送り);
				return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
			}

			//-----------対価計算-----------------
			//アクションの消費が可能か判定
			Action.ResourceShortage rs = a.checkResource(user.getStatus());
			if (rs.is足りないステータスあり()) {
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
				setMsg(詠唱したがリソースが足りないのMSG(user, a, rs.keys));
				currentBAWaitTime = new FrameTimeCounter(100);
				setStage(Stage.待機中＿時間あり＿手番送り);
				return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
			}

			//-----------起動-----------------
			//詠唱ができるので、自動発動
			execAction(new ActionTarget(user, a, tgtList, false));
			return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
		}

		//----------------------------------NPCの場合-------------------------------------
		if (!user.isPlayer()) {
			//AIからアクションとターゲットを取得して実行
			ActionTarget a = ((Enemy) user).getActionTgt();

			//移動、防御などのアクションの場合（逃げるも入る、確定も入る
			if (a.getAction().getType() == ActionType.行動) {
				switch (a.getAction().getId()) {
					case BattleConfig.ActionID.逃走: {
						//逃げ
						//行動力の範囲内で逃げられることを確認
						//逃げる・逃げられるか判定
						//前提として、移動ポイント内にバトルエリアの境界（左右）がなければならない
						Point2D.Float w, e;
						int movPoint = (int) user.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue();
						e = (Point2D.Float) user.getSprite().getCenter().clone();
						e.x += movPoint;
						w = (Point2D.Float) user.getSprite().getCenter().clone();
						w.x -= movPoint;
						if (!battleFieldSystem.getBattleFieldAllArea().contains(e)) {
							//逃走成功（→）
							user.getStatus().addCondition(ConditionKey.逃走した, Integer.MAX_VALUE);
							user.getSprite().setTargetLocation(e, 0);
							user.getSprite().to(FourDirection.EAST);
							setMsg(Xは逃げ出したのMSG(user));
							setStage(Stage.待機中＿敵逃走中);
							return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
						}
						if (!battleFieldSystem.getBattleFieldAllArea().contains(w)) {
							//逃走成功（←）
							user.getStatus().addCondition(ConditionKey.逃走した, Integer.MAX_VALUE);
							user.getSprite().setTargetLocation(w, 0);
							user.getSprite().to(FourDirection.WEST);
							setMsg(Xは逃げ出したのMSG(user));
							setStage(Stage.待機中＿敵逃走中);
							return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
						}
						//逃げられない（基本入らない）
						setMsg(Xは逃げ出したが逃げられなかったのMSG(user));
						setStage(Stage.待機中＿時間あり＿手番送り);
						currentBAWaitTime = new FrameTimeCounter(100);
						return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
					}
					case BattleConfig.ActionID.防御: {
						//防御・・・特に失敗する要素もないので、そのまま実行して防御を付与する
						user.getStatus().addCondition(ConditionKey.防御中, 1);
						setMsg(Xは防御に専念したのMSG(user));
						setStage(Stage.待機中＿時間あり＿手番送り);
						currentBAWaitTime = new FrameTimeCounter(100);
						return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
					}
					case BattleConfig.ActionID.回避: {
						//回避・・・特に失敗する要素もないので、そのまま実行して防御を付与する
						user.getStatus().addCondition(ConditionKey.回避中, 1);
						setMsg(Xは回避に専念したのMSG(user));
						setStage(Stage.待機中＿時間あり＿手番送り);
						currentBAWaitTime = new FrameTimeCounter(100);
						return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
					}
					case BattleConfig.ActionID.確定: {
						//確定（動かない）・・・特に処理なし
						setMsg(Xは様子をうかがっているのMSG(user));
						setStage(Stage.待機中＿時間あり＿手番送り);
						currentBAWaitTime = new FrameTimeCounter(100);
						return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
					}
					case BattleConfig.ActionID.移動: {
						//移動・・・行動力が0の場合選出されない。もし0の場合はエラーとする
						assert user.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue() > 0 : "NPC move point is <= 0 : " + this;
						((Enemy) user).setMoveTgtLocation();
						//行動力残数をセット
						user.getStatus().getBaseStatus().get(StatusKey.残り行動力)
								.setValue(user.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue());
						setMsg(Xは移動したのMSG(user));
						setStage(Stage.待機中＿敵移動中);
						return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
					}
					default: {
						throw new GameSystemException("illegal ai action select : " + a);
					}
				}
			}
			//攻撃or魔法
			//移動は行動アクションを返すので、この時点で射程内にターゲットがいる
			ActionTarget tgt = ((Enemy) user).getActionTgt();

			//詠唱時間のある魔法だった場合、詠唱開始イベントを実行してＭＳＧ表示して次へ
			if (a.getAction().getType() == ActionType.魔法 && a.getAction().getCastTime() != 0) {
				//リソースが足りるかチェック（足りない場合はAIが返さないが念のため
				Action.ResourceShortage res = a.getAction().checkResource(user.getStatus());
				if (res.is足りないステータスあり()) {
					setMsg(詠唱したがリソースが足りないのMSG(user, a.getAction(), res.keys));
					messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
					currentBAWaitTime = new FrameTimeCounter(100);
					setStage(Stage.待機中＿時間あり＿手番送り);
					return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
				}
				//詠唱開始イベント
				//ターゲット状態保存
				cast予約(turn + a.getAction().getCastTime(), new MagicSpell(user, a.getAction(), false));
				targetSystem.saveTgt(user, tgt.getTgt());
				setMsg(詠唱を開始したのMSG(user, a.getAction()));
				currentBAWaitTime = new FrameTimeCounter(100);
				setStage(Stage.待機中＿時間あり＿手番送り);
				return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
			}
			//それ以外の攻撃or0ターン魔法は即時発動
			//ターゲットは射程内にいる
			execAction(tgt);
			return BSExecResult.STAGEが待機中の間待機しその後EXECを再度コールせよ;
		}
		//----------------------------------PCの場合-------------------------------------

		if (BattleConfig.Sounds.手番開始 != null) {
			BattleConfig.Sounds.手番開始.load().stopAndPlay();
		}
		//コマンドウインドウを出す
		targetSystem.setCurrent(user);
		messageWindowSystem.getCmdW().setCmd(currentCmd);
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);
		setStage(Stage.コマンド選択中);
		return BSExecResult.PCのコマンド選択に入った;
	}

	private void cast予約(int t, MagicSpell s) {
		if (BattleConfig.Sounds.魔法詠唱開始 != null) {
			BattleConfig.Sounds.魔法詠唱開始.load().stopAndPlay();
		}
		if (magics.containsKey(t)) {
			magics.get(t).add(s);
		} else {
			magics.put(t, new ArrayList<>(List.of(s)));
		}
		//詠唱アニメーション
		if (BattleConfig.castingAnimationMaster != null) {
			ImageSprite sp = BattleConfig.castingAnimationMaster.clone();
			sp.setLocationByCenter(s.getUser().getSprite().getCenter());
			castingSprites.put(s.getUser(), sp);
		}
	}

	private String Xは混乱していてXを実行したが何も起こらなかったのMSG(Actor user, Action a) {
		String res = I18N.get(GameSystemI18NKeys.Xは混乱している, user.getVisibleName());
		res += Text.getLineSep();
		switch (a.getType()) {
			case アイテム: {
				res += I18N.get(GameSystemI18NKeys.XはXを使った, user.getVisibleName(), a.getVisibleName());
				res += Text.getLineSep();
				res += I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				break;
			}
			case 攻撃: {
				res += I18N.get(GameSystemI18NKeys.XのX, user.getVisibleName(), a.getVisibleName());
				res += Text.getLineSep();
				res += I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				break;
			}
			case 魔法: {
				res += I18N.get(GameSystemI18NKeys.XはXを詠唱した, user.getVisibleName(), a.getVisibleName());
				res += Text.getLineSep();
				res += I18N.get(GameSystemI18NKeys.しかしうまくきまらなかった);
				break;
			}
			default: {
				//入らない
				break;
			}
		}
		return res;
	}

	public void nextCmd() {
		messageWindowSystem.getCmdW().nextAction();
	}

	public void prevCmd() {
		messageWindowSystem.getCmdW().prevAction();
	}

	public void nextCmdType() {
		messageWindowSystem.getCmdW().nextType();
	}

	public void prevCmdType() {
		messageWindowSystem.getCmdW().prevType();
	}

	public enum BSCommitCmdResult {
		STAGEが待機中の間待機しその後次のEXECをコールせよ,
		STAGEが待機中の間待機しその後再度コマンド選択してCOMMIT_CMDをコールせよ,
		移動モードに入った,
		ステータス確認に入った,
		アイテム用途選択に入った,
		ターゲット選択に入った,
	}

	public BSCommitCmdResult commitCmd() {
		Actor user = currentCmd.getUser();
		Action a = messageWindowSystem.getCmdW().getSelectedCmd();
		//選択されたアクションにより分岐
		if (a.getType() == ActionType.行動) {
			switch (a.getId()) {
				case BattleConfig.ActionID.逃走: {
					//逃げ
					//行動力の範囲内で逃げられることを確認
					//逃げる・逃げられるか判定
					//前提として、移動ポイント内にバトルエリアの境界（左右）がなければならない
					Point2D.Float w, e;
					int movPoint = (int) user.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue();
					e = (Point2D.Float) user.getSprite().getCenter().clone();
					e.x += movPoint;
					w = (Point2D.Float) user.getSprite().getCenter().clone();
					w.x -= movPoint;
					if (!battleFieldSystem.getBattleFieldAllArea().contains(e)) {
						//逃走成功（→）
						user.getStatus().addCondition(ConditionKey.逃走した, Integer.MAX_VALUE);
						user.getSprite().setTargetLocation(e, 0);
						user.getSprite().to(FourDirection.EAST);
						setMsg(Xは逃げ出したのMSG(user));
						setStage(Stage.PC逃げアニメーション実行中);
						return BSCommitCmdResult.STAGEが待機中の間待機しその後次のEXECをコールせよ;
					}
					if (!battleFieldSystem.getBattleFieldAllArea().contains(w)) {
						//逃走成功（←）
						user.getStatus().addCondition(ConditionKey.逃走した, Integer.MAX_VALUE);
						user.getSprite().setTargetLocation(w, 0);
						user.getSprite().to(FourDirection.WEST);
						setMsg(Xは逃げ出したのMSG(user));
						setStage(Stage.PC逃げアニメーション実行中);
						return BSCommitCmdResult.STAGEが待機中の間待機しその後次のEXECをコールせよ;
					}
					//逃げられない（基本入らない）
					setMsg(Xは逃げ出したが逃げられなかったのMSG(user));
					setStage(Stage.待機中＿時間あり＿手番送り);
					currentBAWaitTime = new FrameTimeCounter(100);
					return BSCommitCmdResult.STAGEが待機中の間待機しその後次のEXECをコールせよ;
				}
				case BattleConfig.ActionID.防御: {
					//防御・・・特に失敗する要素もないので、そのまま実行して防御を付与する
					user.getStatus().addCondition(ConditionKey.防御中, 1);
					setMsg(Xは防御に専念したのMSG(user));
					setStage(Stage.待機中＿時間あり＿手番送り);
					currentBAWaitTime = new FrameTimeCounter(100);
					return BSCommitCmdResult.STAGEが待機中の間待機しその後次のEXECをコールせよ;
				}
				case BattleConfig.ActionID.回避: {
					//回避・・・特に失敗する要素もないので、そのまま実行して防御を付与する
					user.getStatus().addCondition(ConditionKey.回避中, 1);
					setMsg(Xは回避に専念したのMSG(user));
					setStage(Stage.待機中＿時間あり＿手番送り);
					currentBAWaitTime = new FrameTimeCounter(100);
					return BSCommitCmdResult.STAGEが待機中の間待機しその後次のEXECをコールせよ;
				}
				case BattleConfig.ActionID.確定: {
					//確定（動かない）・・・特に処理なし
					setMsg(Xは様子をうかがっているのMSG(user));
					setStage(Stage.待機中＿時間あり＿手番送り);
					currentBAWaitTime = new FrameTimeCounter(100);
					return BSCommitCmdResult.STAGEが待機中の間待機しその後次のEXECをコールせよ;
				}
				case BattleConfig.ActionID.状態: {
					//ステータス表示
					int i = GameSystem.getInstance().getOrder(user);
					messageWindowSystem.setStatusDescPCIDX(i);
					messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.SHOW_STATUS_DESC);
					setStage(Stage.ステータス閲覧中_閉じる待ち);
					return BSCommitCmdResult.ステータス確認に入った;
				}
				case BattleConfig.ActionID.移動: {
					if (user.getStatus().getEffectedStatus().get(StatusKey.行動力).isZero()) {
						setMsg(Xは移動できないのMSG(user));
						currentBAWaitTime = new FrameTimeCounter(100);
						setStage(Stage.待機中＿時間あり＿手番送り);
						return BSCommitCmdResult.STAGEが待機中の間待機しその後再度コマンド選択してCOMMIT_CMDをコールせよ;
					}
					//行動力残数をセット
					user.getStatus().getBaseStatus().get(StatusKey.残り行動力)
							.setValue(user.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue());
					//移動前位置の保存
					this.moveIinitialLocation = user.getSprite().getLocation();
					targetSystem.setCurrent(user);
					targetSystem.setCurrent(a);
					targetSystem.setInitialAreaLocation(moveIinitialLocation);
					targetSystem.setAreaVisible(true, true);
					messageWindowSystem.setVisible(BattleMessageWindowSystem.StatusVisible.ON,
							BattleMessageWindowSystem.Mode.NOTHING,
							BattleMessageWindowSystem.InfoVisible.ON);
					setStage(Stage.プレイヤキャラ移動中_コミット待ち);
					return BSCommitCmdResult.移動モードに入った;
				}
				default: {
					throw new GameSystemException("illegal ai action select : " + a);
				}
			}
		}//行動の場合

		//アイテムの場合
		if (a.getType() == ActionType.アイテム) {
			targetSystem.setCurrent(user);
			targetSystem.setCurrent(a);
			targetSystem.setAreaVisible(false, true);
			messageWindowSystem.setItemDesc(user, (Item) a);
			messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ITEM_USE_SELECT);
			setStage(Stage.アイテム用途選択画面表示中);
			return BSCommitCmdResult.アイテム用途選択に入った;
		}//アイテムの場合

		//攻撃、魔法
		//リソース不足確認
		Action.ResourceShortage rs = a.checkResource(user.getStatus());
		if (rs.is足りないステータスあり()) {
			//不発、次へ送る
			setMsg(詠唱したがリソースが足りないのMSG(user, a, rs.keys));
			messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
			currentBAWaitTime = new FrameTimeCounter(100);
			setStage(Stage.待機中＿時間あり＿手番送り);
			return BSCommitCmdResult.STAGEが待機中の間待機しその後次のEXECをコールせよ;
		}
		//ターゲットシステムにセット
		targetSystem.setCurrent(user);
		targetSystem.setCurrent(a);
		targetSystem.setIconVisible(true);
		targetSystem.setAreaVisible(false, true);
		afterMove = false;
		return BSCommitCmdResult.ターゲット選択に入った;
	}

	public void itemUseSelectNext() {
		messageWindowSystem.itemChoiceUseNextSelect();
	}

	public void itemUseSelectPrev() {
		messageWindowSystem.itemChoiceUsePrevSelect();
	}

	public void cancelItemUseSelect() {
		//コマンドウインドウを出す
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);
		setStage(Stage.コマンド選択中);
	}

	public enum ItemUseSelectResult {
		アイテム詳細に入った,
		装備に失敗したので待機中の間待ってから再度コマンド選択せよ,
		装備に成功したので待機中の間待ってから次のEXECをコールせよ,
		アイテムを使用したのでターゲット選択に入った,
		アイテムの使用に失敗したので待機中の間待ってから次のEXECをコールせよ,
		アイテムを渡したので待機中の間待ってから次のEXECをコールせよ,
		アイテムを渡せなかったので待機中の間待ってから次のEXECをコールせよ,
	}

	public ItemUseSelectResult commitItemUseSelect() {
		switch (messageWindowSystem.itemChoiceUseCommit()) {
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_CHECK: {
				//アイテム詳細
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.SHOW_ITEM_DESC);
				return ItemUseSelectResult.アイテム詳細に入った;
			}
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_EQIP: {
				//装備
				Actor user = currentCmd.getUser();
				Item i = (Item) messageWindowSystem.getCmdW().getSelectedCmd();//キャスト可能
				//装備できるアイテムかどうかで分岐
				if (user.getStatus().getEqip().values().contains(i)) {
					//すでに装備している時は外す
					//バッグに分類されるアイテムかつアイテム数がもともと持てる数を上回る場合外せない
					if (ActionStorage.isItemBagItem(i.getId())) {
						//もともとのサイズ
						int itemBagDefaultMax = user.getStatus().getRace().getItemBagSize();
						//現在の持ってる数
						int currentSize = user.getStatus().getItemBag().size();
						//現在のサイズがもともともサイズより大きい場合は外せない
						if (currentSize > itemBagDefaultMax) {
							//外せない
							setMsg(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
							messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
							currentBAWaitTime = new FrameTimeCounter(100);
							setStage(Stage.待機中＿時間あり＿手番戻り);
							return ItemUseSelectResult.装備に失敗したので待機中の間待ってから再度コマンド選択せよ;
						}
					}
					if (ActionStorage.isBookBagItem(i.getId())) {
						//もともとのサイズ
						int itemBagDefaultMax = user.getStatus().getRace().getBookBagSize();
						//現在の持ってる数
						int currentSize = user.getStatus().getBookBag().size();
						//現在のサイズがもともともサイズより大きい場合は外せない
						if (currentSize > itemBagDefaultMax) {
							//外せない
							setMsg(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
							messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
							currentBAWaitTime = new FrameTimeCounter(100);
							setStage(Stage.待機中＿時間あり＿手番戻り);
							return ItemUseSelectResult.装備に失敗したので待機中の間待ってから再度コマンド選択せよ;
						}
					}
					//外すスロットを設定
					//iが弓の場合、左手を外す（同時に右手も外す
					if (i.getWeaponType() == WeaponType.弓) {
						user.getStatus().unEqip(EqipSlot.右手);
						user.getStatus().unEqip(EqipSlot.左手);
					} else {
						//このアイテムを左手に装備している場合は左てを、そうでなければアイテムのスロットを利用
						EqipSlot tgtSlot
								= i.equals(user.getStatus().getEqip().get(EqipSlot.左手))
								? EqipSlot.左手 : i.getSlot();
						user.getStatus().unEqip(tgtSlot);
						//右手を外した場合で左手が両手持ちの場合は左手も外す
						if (tgtSlot == EqipSlot.右手) {
							if (ActionStorage.getInstance().両手持ち.equals(user.getStatus().getEqip().get(EqipSlot.左手))) {
								user.getStatus().unEqip(EqipSlot.左手);
							}
						}
					}

					user.getStatus().updateAction();
					String cnd = user.getStatus().addWhen0Condition();
					//アイテム所持数の再計算
					user.getStatus().updateBagSize();
					if (cnd == null) {
						setMsg(I18N.get(GameSystemI18NKeys.Xを外した, i.getVisibleName()));
					} else {
						setMsg(I18N.get(GameSystemI18NKeys.Xを外した, i.getVisibleName())
								+ Text.getLineSep() + cnd);
					}
					//外した
					currentBAWaitTime = new FrameTimeCounter(100);
					messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
					setStage(Stage.待機中＿時間あり＿手番送り);
					return ItemUseSelectResult.装備に成功したので待機中の間待ってから次のEXECをコールせよ;
				} else if (i.canEqip(user)) {
					//装備する
					user.getStatus().eqip(i);
					//両手持ち武器の場合は左手を強制的に両手持ちにする
					boolean ryoute = false;
					if (i.getWeaponType() == WeaponType.弓) {
						user.getStatus().eqip(EqipSlot.右手, ActionStorage.getInstance().両手持ち_弓);
						user.getStatus().eqip(EqipSlot.左手, i);
						ryoute = true;
					} else if (Set.of(WeaponType.大剣, WeaponType.大杖, WeaponType.銃, WeaponType.弩, WeaponType.薙刀)
							.contains(i.getWeaponType())) {
						user.getStatus().eqipLeftHand(ActionStorage.getInstance().両手持ち);
						ryoute = true;
					}
					user.getStatus().updateAction();
					String cnd = user.getStatus().addWhen0Condition();
					//アイテム所持数の再計算
					user.getStatus().updateBagSize();
					if (cnd == null) {
						if (ryoute) {
							setMsg(I18N.get(GameSystemI18NKeys.Xを両手持ちで装備した, i.getVisibleName()));
						} else {
							setMsg(I18N.get(GameSystemI18NKeys.Xを装備した, i.getVisibleName()));
						}
					} else {
						if (ryoute) {
							setMsg(I18N.get(GameSystemI18NKeys.Xを両手持ちで装備した, i.getVisibleName())
									+ Text.getLineSep() + cnd);
						} else {
							setMsg(I18N.get(GameSystemI18NKeys.Xを装備した, i.getVisibleName())
									+ Text.getLineSep() + cnd);
						}
					}
					//装備した
					messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
					currentBAWaitTime = new FrameTimeCounter(100);
					setStage(Stage.待機中＿時間あり＿手番送り);
					return ItemUseSelectResult.装備に成功したので待機中の間待ってから次のEXECをコールせよ;
				} else {
					//装備できない
					setMsg(I18N.get(GameSystemI18NKeys.Xは装備できない, i.getVisibleName()));
					messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
					currentBAWaitTime = new FrameTimeCounter(100);
					setStage(Stage.待機中＿時間あり＿手番戻り);
					return ItemUseSelectResult.装備に失敗したので待機中の間待ってから再度コマンド選択せよ;
				}
			}
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_USE: {
				//つかう
				Actor user = currentCmd.getUser();
				Item i = (Item) messageWindowSystem.getCmdW().getSelectedCmd();//キャスト可能
				if (!i.isBattle()) {
					//しかし何も起こらなかった
					setMsg(
							I18N.get(GameSystemI18NKeys.XはXを使った, user.getVisibleName(), i.getVisibleName())
							+ Text.getLineSep()
							+ I18N.get(GameSystemI18NKeys.しかし効果がなかった));
					messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
					currentBAWaitTime = new FrameTimeCounter(100);
					setStage(Stage.待機中＿時間あり＿手番送り);
					return ItemUseSelectResult.アイテムの使用に失敗したので待機中の間待ってから次のEXECをコールせよ;
				}
				//ターゲット選択へ
				targetSystem.setCurrent(user);
				targetSystem.setCurrent(i);
				targetSystem.setIconVisible(true);
				targetSystem.setAreaVisible(false, true);
				afterMove = false;
				return ItemUseSelectResult.アイテムを使用したのでターゲット選択に入った;
			}
			default:
				//パスタ
				break;
		}
		//パス可能
		//パスタ選択
		Actor user = currentCmd.getUser();
		Item i = (Item) messageWindowSystem.getCmdW().getSelectedCmd();//キャスト可能
		int idx = messageWindowSystem.getItemChoiceUseW().getSelect() - 3;

		Actor tgt = GameSystem.getInstance().getParty().get(idx);

		assert tgt.getSprite().getCenter().distance(user.getSprite().getCenter())
				<= user.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue() / 2 : "BS illegal item pass target : " + user + " to " + tgt;

		assert tgt.getStatus().getItemBag().canAdd() : "BS cant pass item : " + tgt;

		user.getStatus().pass(i, tgt.getStatus());
		//渡したメッセージの表示
		setMsg(I18N.get(GameSystemI18NKeys.XはXにXを渡した,
				user.getVisibleName(),
				tgt.getVisibleName(),
				i.getVisibleName()));
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
		currentBAWaitTime = new FrameTimeCounter(100);
		setStage(Stage.待機中＿時間あり＿手番送り);
		return ItemUseSelectResult.アイテムを渡したので待機中の間待ってから次のEXECをコールせよ;
	}

	public void closeItemDesc() {
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);
		setStage(Stage.コマンド選択中);
	}

	public void showStatusNextPage() {
		messageWindowSystem.getStatusDescW().next();
	}

	public void showStatusNextPC() {
		messageWindowSystem.getStatusDescW().nextPc();
	}

	public void showStatusPrevPC() {
		messageWindowSystem.getStatusDescW().prevPc();
	}

	public void closeShowStatus() {
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);
		setStage(Stage.コマンド選択中);
	}

	private String Xは移動できないのMSG(Actor a) {
		return I18N.get(GameSystemI18NKeys.しかしXは行動力が０のため移動できない, a.getVisibleName());
	}

	public void calcelMove() {
		//元の位置に戻してコマンド選択に戻す
		currentCmd.getUser().getSprite().setLocation(moveIinitialLocation);
		targetSystem.unset();
		targetSystem.setCurrent(currentCmd.getUser());
		messageWindowSystem.getCmdW().setCmd(currentCmd);
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);
		setStage(Stage.コマンド選択中);
	}

	public void commitMove() {
		//移動後攻撃ウインドウを表示
		//アクション抽出
		List<Action> a = new ArrayList<>();
		a.add(ActionStorage.getInstance().get(BattleConfig.ActionID.確定));
		a.addAll(currentCmd.getUser().getStatus().get現状実行可能なアクション()
				.stream().filter(p -> p.getType() == ActionType.攻撃)
				.toList());
		targetSystem.setAreaVisible(false, true);
		messageWindowSystem.getAfterMoveW().setActions(a);
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.AFTER_MOVE);
		setStage(Stage.移動後行動選択中_コミット待ち);
	}

	public void nextAfterMoveCmd() {
		messageWindowSystem.getAfterMoveW().nextAction();
	}

	public void prevAfterMoveCmd() {
		messageWindowSystem.getAfterMoveW().prevAction();
	}

	public void calcelAfterMoveCmd() {
		messageWindowSystem.setVisible(BattleMessageWindowSystem.StatusVisible.ON,
				BattleMessageWindowSystem.Mode.NOTHING,
				BattleMessageWindowSystem.InfoVisible.ON);
		setStage(Stage.プレイヤキャラ移動中_コミット待ち);
	}

	public enum AfterMoveCmdResult {
		移動を確定したので次のEXECをコールせよ,
		ターゲット選択に入った,
	}

	public AfterMoveCmdResult commitAfterMoveCmd() {
		//確定なら次へ送り、攻撃ならターゲット選択に遷移する
		Action a = messageWindowSystem.getAfterMoveW().getSelectedCmd();
		if (a.getId().equals(BattleConfig.ActionID.確定)) {
			//確定
			targetSystem.unset();
			return AfterMoveCmdResult.移動を確定したので次のEXECをコールせよ;
		}
		targetSystem.setCurrent(currentCmd.getUser());
		targetSystem.setCurrent(a);
		targetSystem.setIconVisible(true);
		targetSystem.setAreaVisible(false, true);
		afterMove = true;
		return AfterMoveCmdResult.ターゲット選択に入った;
	}

	public void nextTarget() {
		targetSystem.nextTgt();
	}

	public void prevTarget() {
		targetSystem.prevTgt();
	}

	public void switchTeam() {
		targetSystem.switchTeam();
	}

	public enum TargetSelectCalcelResult {
		移動後コマンド選択に戻れ,
		コマンド選択に戻れ,
	}

	public TargetSelectCalcelResult calcelTargetSelect() {
		if (afterMove) {
			//移動後攻撃からだったら移動後攻撃アクション選択に戻る
			List<Action> a = new ArrayList<>();
			a.add(ActionStorage.getInstance().get(BattleConfig.ActionID.確定));
			a.addAll(currentCmd.getUser().getStatus().get現状実行可能なアクション()
					.stream().filter(p -> p.getType() == ActionType.攻撃)
					.toList());
			targetSystem.setAreaVisible(false, true);
			messageWindowSystem.getAfterMoveW().setActions(a);
			messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.AFTER_MOVE);
			setStage(Stage.移動後行動選択中_コミット待ち);
			return TargetSelectCalcelResult.移動後コマンド選択に戻れ;
		} else {
			//通常の攻撃の場合はコマンド選択に戻る
			targetSystem.setCurrent(currentCmd.getUser());
			messageWindowSystem.getCmdW().setCmd(currentCmd);
			messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);
			setStage(Stage.コマンド選択中);
			return TargetSelectCalcelResult.コマンド選択に戻れ;
		}
	}

	public void commitTargetSelect() {
		//ターゲット取得
		List<Actor> tgt = targetSystem.getSelected();
		assert !tgt.isEmpty() : "tgt is empty : " + this;
		Action a = afterMove ? messageWindowSystem.getAfterMoveW().getSelectedCmd() : messageWindowSystem.getCmdW().getSelectedCmd();

		//アクション自動実行開始
		execAction(new ActionTarget(currentCmd.getUser(), a, tgt, false));
	}

	private String Xは移動したのMSG(Actor user) {
		return I18N.get(GameSystemI18NKeys.Xは移動した, user.getVisibleName());
	}

	private String Xは様子をうかがっているのMSG(Actor user) {
		return I18N.get(GameSystemI18NKeys.Xは様子をうかがっている, user.getVisibleName());
	}

	private String Xは回避に専念したのMSG(Actor user) {
		return I18N.get(GameSystemI18NKeys.Xは回避に専念した, user.getVisibleName());
	}

	private String Xは防御に専念したのMSG(Actor user) {
		return I18N.get(GameSystemI18NKeys.Xは防御に専念した, user.getVisibleName());
	}

	private String Xは逃げ出したが逃げられなかったのMSG(Actor user) {
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.get(GameSystemI18NKeys.Xは逃走しようとした, user.getVisibleName()));
		sb.append(Text.getLineSep());
		sb.append(I18N.get(GameSystemI18NKeys.しかし戦闘エリアの中心にいては逃げられない));
		return sb.toString();
	}

	private String Xは逃げ出したのMSG(Actor user) {
		return I18N.get(GameSystemI18NKeys.Xは逃走した, user.getVisibleName());
	}

	private String 詠唱を開始したのMSG(Actor user, Action a) {
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.get(GameSystemI18NKeys.XはXの詠唱を開始した, user.getVisibleName(), a.getVisibleName()));
		return sb.toString();
	}

	private String 詠唱したがリソースが足りないのMSG(Actor user, Action a, Set<StatusKey> desc) {
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, user.getVisibleName(), a.getVisibleName()));
		sb.append(I18N.get(GameSystemI18NKeys.しかしXが足りない,
				String.join(",", desc.stream().map(p -> p.getVisibleName()).toList())));
		return sb.toString();
	}

	boolean 魔法詠唱を破棄(Actor a) {
		boolean res = false;
		for (int i : magics.keySet()) {
			MagicSpell remove = null;
			for (MagicSpell m : magics.get(i)) {
				if (m.getUser().equals(a)) {
					remove = m;
					break;
				}
			}
			if (remove != null) {
				magics.get(i).remove(remove);
				res = true;
				break;
			}
		}
		castingSprites.remove(a);
		a.getStatus().removeCondition(ConditionKey.詠唱中);
		return res;
	}

	// I18NKから参照するためpp
	enum NoTgtDesc {
		しかし対象者はすでに意識がない,
		しかし効果範囲内に対象者はいなかった,;

		public String getVisibleName() {
			return I18N.get(toString());
		}

	}

	private String 詠唱したがターゲットがいないのMSG(Actor user, Action a, NoTgtDesc desc) {
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, user.getVisibleName(), a.getVisibleName()));
		sb.append(I18N.get(desc.getVisibleName()));
		return sb.toString();
	}

	private void execAction(ActionTarget selectedTgt) {
		Actor user = selectedTgt.getUser();
		Action a = selectedTgt.getAction();
		//ターゲット不在の場合空振り
		if (selectedTgt.getTgt().isEmpty()) {
			setMsg(詠唱したがターゲットがいないのMSG(user, a, NoTgtDesc.しかし効果範囲内に対象者はいなかった));
			messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
			return;
		}

		//魔法詠唱完了の場合発動
		if (currentCmd.isMagicSpell()) {
			ActionResult res = a.exec(selectedTgt);
			setStage(攻撃結果処理(res));
			return;
		}

		//魔法詠唱開始の場合予約
		if (a.getType() == ActionType.魔法 && a.getCastTime() != 0) {
			//リソース検査
			Action.ResourceShortage rs = selectedTgt.getAction().checkResource(user.getStatus());
			if (rs.is足りないステータスあり()) {
				setMsg(詠唱したがリソースが足りないのMSG(user, a, rs.keys));
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
				currentBAWaitTime = new FrameTimeCounter(100);
				setStage(Stage.待機中＿時間あり＿手番送り);
			}
			//キャスト予約
			cast予約(turn + a.getCastTime(), new MagicSpell(user, a, user.isPlayer()));
			targetSystem.saveTgt(user, selectedTgt.getTgt());
			setMsg(詠唱を開始したのMSG(user, a));
			currentBAWaitTime = new FrameTimeCounter(100);
			setStage(Stage.待機中＿時間あり＿手番送り);
			return;
		}
		//その他行動の場合は実行
		ActionResult res = a.exec(selectedTgt);
		setStage(攻撃結果処理(res));
	}

	private Stage 攻撃結果処理(ActionResult res) {
		if (res.isイベント未起動()) {
			return Stage.EXECコール待機;
		}

		//ユーザアニメーションだけここで入れる
		if (res.getUserAnimation() != null) {
			animation.add(res.getUserAnimation());
		}

		//resを分解してmessageQueueに詰める。詰めたらキューの最初から実行する。
		messageQueue.clear();
		for (var v : res.getUserEventResultAsList()) {
			if (v.summary != ActionResultSummary.失敗＿起動条件未達) {
				messageQueue.add(v);
			}
		}
		for (var v : res.getMainEventResultAsList()) {
			if (v.summary != ActionResultSummary.失敗＿起動条件未達) {
				messageQueue.addAll(v.perActor.values());
			}
		}

		return messageQueue消化();
	}

	private Stage messageQueue消化() {
		if (messageQueue.isEmpty()) {
			//すべて使ったので終わり
			return Stage.EXECコール待機;
		}

		//キューに入ってる最初の最初の1件についてMSGとダメージ表示とアニメーション処理を行う
		ActionResult.EventActorResult res = messageQueue.getFirst();
		messageQueue.removeFirst();

		//アニメーション
		addAnimation(res);

		//ダメージ表示
		addDamageAnimation(res);

		//MSG
		//MSGは派生を出さない（量が多いので）
		setMsg(res.msgI18Nd);

		//死亡演出
		if (is死亡演出あり(res)) {
			return Stage.エフェクト再生中_終了待ち;
		}

		currentBAWaitTime = new FrameTimeCounter(25);
		return Stage.待機中＿時間あり＿手番送り;
	}

	private boolean is死亡演出あり(ActionResult.EventActorResult res) {
		boolean result = false;
		if (!res.派生イベントの結果リスト.isEmpty()) {
			for (var v : res.派生イベントの結果リスト) {
				result |= is死亡演出あり(v);
			}
		}

		if (res.is損壊) {
			if (res.tgt instanceof Enemy) {
				if (((Enemy) res.tgt).getDeadSound() != null) {
					((Enemy) res.tgt).getDeadSound().load().stopAndPlay();;
				} else {
					if (BattleConfig.Sounds.損壊 != null) {
						BattleConfig.Sounds.損壊.load().stopAndPlay();
					}
				}
			}
			result = true;
		}
		if (res.is気絶) {
			if (res.tgt instanceof Enemy) {
				if (((Enemy) res.tgt).getDeadSound() != null) {
					((Enemy) res.tgt).getDeadSound().load().stopAndPlay();;
				} else {
					if (BattleConfig.Sounds.気絶 != null) {
						BattleConfig.Sounds.気絶.load().stopAndPlay();
					}
				}
			}
			result = true;
		}
		if (res.is解脱) {
			if (res.tgt instanceof Enemy) {
				if (((Enemy) res.tgt).getDeadSound() != null) {
					((Enemy) res.tgt).getDeadSound().load().stopAndPlay();;
				} else {
					if (BattleConfig.Sounds.解脱 != null) {
						BattleConfig.Sounds.解脱.load().stopAndPlay();
					}
				}
			}
			result = true;
		}

		if (result) {
			if (effect == null || effect.isEnded()) {
				this.effect = new FlashEffect(
						GraphicsUtil.transparent(Color.RED, 128),
						new FrameTimeCounter(20),
						new FrameTimeCounter(4),
						0, 0,
						(int) GameOption.getInstance().getWindowSize().getWidth(),
						(int) GameOption.getInstance().getWindowSize().getHeight());
				if (BattleConfig.Sounds.正気度減少演出 != null) {
					BattleConfig.Sounds.正気度減少演出.load().stopAndPlay();
				}
			}
			if (!res.tgt.isSummoned()) {
				if (res.tgt.isPlayer()) {
					//死んだのはPC
					for (Actor a : GameSystem.getInstance().getParty().stream().filter(p -> !p.isSummoned()).toList()) {
						int damage = BattleConfig.正気度減少イベントの数値＿味方の場合.getAsInt();
						if (damage > 0) {
							damage = -damage;
						}
						if (damage == 0) {
							continue;
						}
						a.getStatus().getBaseStatus().get(StatusKey.正気度).add(damage);
						DamageAnimationSprite ds = new DamageAnimationSprite(
								a.getSprite().getX() + 12,
								a.getSprite().getY() + 12,
								Math.abs(damage),
								Color.RED);
						animation.add(ds);
					}
					for (Enemy e : this.enemies) {
						int damage = BattleConfig.正気度減少イベントの数値＿敵の場合.getAsInt();
						if (damage > 0) {
							damage = -damage;
						}
						if (damage == 0) {
							continue;
						}
						e.getStatus().getBaseStatus().get(StatusKey.正気度).add(damage);
						DamageAnimationSprite ds = new DamageAnimationSprite(
								e.getSprite().getX() + 12,
								e.getSprite().getY() + 12,
								Math.abs(damage),
								Color.RED);
						animation.add(ds);
					}
				} else {
					//死んだのは敵
					for (Actor a : GameSystem.getInstance().getParty().stream().filter(p -> !p.isSummoned()).toList()) {
						int damage = BattleConfig.正気度減少イベントの数値＿敵の場合.getAsInt();
						if (damage > 0) {
							damage = -damage;
						}
						if (damage == 0) {
							continue;
						}
						a.getStatus().getBaseStatus().get(StatusKey.正気度).add(damage);
						DamageAnimationSprite ds = new DamageAnimationSprite(
								a.getSprite().getX() + 12,
								a.getSprite().getY() + 12,
								Math.abs(damage),
								Color.RED);
						animation.add(ds);
					}
					for (Enemy e : this.enemies) {
						int damage = BattleConfig.正気度減少イベントの数値＿味方の場合.getAsInt();
						if (damage > 0) {
							damage = -damage;
						}
						if (damage == 0) {
							continue;
						}
						e.getStatus().getBaseStatus().get(StatusKey.正気度).add(damage);
						DamageAnimationSprite ds = new DamageAnimationSprite(
								e.getSprite().getX() + 12,
								e.getSprite().getY() + 12,
								Math.abs(damage),
								Color.RED);
						animation.add(ds);
					}
				}
			}
		}//正気度演出あり
		return result;
	}

	private void addDamageAnimation(ActionResult.EventActorResult res) {
		if (res.tgtDamageHp != 0) {
			DamageAnimationSprite ds = new DamageAnimationSprite(
					res.tgt.getSprite().getX() - Random.randomAbsInt(9),
					res.tgt.getSprite().getY() - Random.randomAbsInt(9),
					Math.abs(res.tgtDamageHp),
					Color.WHITE);
			animation.add(ds);
		}
		if (res.tgtDamageMp != 0) {
			DamageAnimationSprite ds = new DamageAnimationSprite(
					res.tgt.getSprite().getX(),
					res.tgt.getSprite().getY(),
					Math.abs(res.tgtDamageMp),
					Color.YELLOW);
			animation.add(ds);
		}
		if (res.tgtDamageSAN != 0) {
			DamageAnimationSprite ds = new DamageAnimationSprite(
					res.tgt.getSprite().getX() + Random.randomAbsInt(9),
					res.tgt.getSprite().getY() + Random.randomAbsInt(9),
					Math.abs(res.tgtDamageSAN),
					Color.RED);
			animation.add(ds);
		}
		if (!res.派生イベントの結果リスト.isEmpty()) {
			for (var v : res.派生イベントの結果リスト) {
				addDamageAnimation(v);
			}
		}
	}

	private void addAnimation(ActionResult.EventActorResult res) {
		//アニメーション
		if (res.otherAnimation != null) {
			this.animation.add(res.otherAnimation);
		}
		if (res.tgtAnimation != null) {
			this.animation.add(res.tgtAnimation);
		}
		if (!res.派生イベントの結果リスト.isEmpty()) {
			for (var v : res.派生イベントの結果リスト) {
				addAnimation(v);
			}
		}
	}

	@LoopCall
	public void update() {
		GameSystem gs = GameSystem.getInstance();
		//MW表示内容の更新
		messageWindowSystem.update();
		//SELECTEDのアイコンとエリア更新
		targetSystem.update();
		//敵のプログレスバー更新
		enemies.forEach(v -> v.update());

		//終了したアニメーションの除去
		List<Sprite> remove = new ArrayList<>();
		for (Sprite s : animation) {
			if (!s.isVisible() || !s.isExist()) {
				remove.add(s);
			}
		}
		animation.removeAll(remove);
		Set<Actor> removeCastAnimation = castingSprites
				.keySet()
				.stream()
				.filter(p -> !p.getStatus().hasCondition(ConditionKey.詠唱中))
				.collect(Collectors.toSet());
		removeCastAnimation.forEach(p -> castingSprites.remove(p));

		//ステージ別処理
		switch (stage) {
			case 未使用: {
				//入らない。
				return;
			}
			case 開始_to_初期移動開始: {
				//入らない。
				throw new GameSystemException("BS illegal state : " + this);
			}
			case 初期移動中: {
				//プレイヤーキャラクターが目標の座標に近づくまで移動を実行、目的地に近づいたらステージWAITに変える
				gs.getParty().forEach(p -> p.getSprite().move());
				//移動終了判定
				boolean initialMoveEnd = true;
				for (int i = 0; i < gs.getPartySprite().size(); i++) {
					float speed = gs.getPartySprite().get(i).getSpeed();
					if (initialMoveEnd &= partyTargetLocationForFirstMove.get(i).distance(gs.getPartySprite().get(i).getLocation()) <= speed) {
						gs.getPartySprite().get(i).setLocation(partyTargetLocationForFirstMove.get(i));
						gs.getParty().get(i).getSprite().unsetTarget();
					}
				}
				if (initialMoveEnd) {
					//ターンスタートが呼ばれるまで待つ。
					messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.NOTHING);
					setStage(Stage.ターン開始コール待機);
				}
				break;
			}
			case ターン開始コール待機: {
				//処理なし
				break;
			}
			case EXECコール待機: {
				//処理なし
				break;
			}
			case 待機中＿時間あり＿手番送り: {
				if (currentBAWaitTime == null) {
					throw new GameSystemException("waiting, but wait time is null : " + this);
				}
				if (currentBAWaitTime.isReaching()) {
					if (!messageQueue.isEmpty()) {
						//次のメッセージ表示へ
						messageQueue消化();
						return;
					}
					currentBAWaitTime = null;
					//EXECコール待機に入る
					setStage(Stage.EXECコール待機);
				}
				break;
			}
			case 待機中＿時間あり＿手番戻り: {
				if (currentBAWaitTime == null) {
					throw new GameSystemException("waiting, but wait time is null : " + this);
				}
				if (currentBAWaitTime.isReaching()) {
					currentBAWaitTime = null;
					//コマンド選択に戻る
					setStage(Stage.コマンド選択中);
				}
				break;
			}
			case 待機中＿敵逃走中: {
				currentCmd.getUser().getSprite().moveToTgt();
				if (!currentCmd.getUser().getSprite().isMoving()
						|| !battleFieldSystem.getBattleFieldAllArea().hit(currentCmd.getUser().getSprite())) {
					currentCmd.getUser().getSprite().unsetTarget();
					//全員逃げた場合終了
					if (enemies.stream()
							.allMatch(p -> p.getStatus().hasAnyCondition(ConditionKey.逃走した, ConditionKey.解脱, ConditionKey.気絶, ConditionKey.損壊))) {
						setEndStatus(BattleResult.勝利_敵が全員逃げた);
						return;
					}
					setStage(Stage.EXECコール待機);
				}
				break;
			}
			case PC逃げアニメーション実行中: {
				currentCmd.getUser().getSprite().moveToTgt();
				if (!currentCmd.getUser().getSprite().isMoving()
						|| !battleFieldSystem.getBattleFieldAllArea().hit(currentCmd.getUser().getSprite())) {
					currentCmd.getUser().getSprite().unsetTarget();

					//全員逃げた場合終了
					if (GameSystem.getInstance().getParty().stream()
							.allMatch(p -> p.getStatus().hasAnyCondition(ConditionKey.逃走した, ConditionKey.解脱, ConditionKey.気絶, ConditionKey.損壊))) {
						setEndStatus(BattleResult.敗北_こちらが全員逃げた);
						return;
					}

					setStage(Stage.EXECコール待機);
				}
				break;
			}
			case プレイヤキャラ移動中_コミット待ち: {
				//残り行動力の更新
				float distance = (float) currentCmd.getUser().getSprite().getLocation().distance(moveIinitialLocation);
				currentCmd.getUser().getStatus().getBaseStatus().get(StatusKey.残り行動力).setValue(distance);
				String v = (int) (currentCmd.getUser().getStatus().getEffectedStatus().get(StatusKey.残り行動力).getValue()
						/ currentCmd.getUser().getStatus().getEffectedStatus().get(StatusKey.行動力).getValue()) + "%";
				messageWindowSystem.getInfoW().setText(v);
				targetSystem.setCurrentLocation();
				break;
			}
			case 待機中＿敵移動中: {
				//NPCの移動実行、！！！！！！！！移動かんりょぅしたらメッセージウインドウ閉じる
				currentCmd.getUser().getSprite().moveToTgt();
				currentCmd.getUser().getStatus().getBaseStatus().get(StatusKey.残り行動力).add(-1);
				int remMovePoint = (int) currentCmd.getUser().getStatus().getBaseStatus().get(StatusKey.残り行動力).getValue();
				//移動ポイントが切れた場合、移動終了してユーザコマンド待ちに移行
				if (remMovePoint <= 0 || !currentCmd.getUser().getSprite().isMoving()) {
					currentCmd.getUser().getSprite().unsetTarget();
					setStage(Stage.EXECコール待機);
					break;
				}
				//移動ポイントが切れていない場合で、移動ポイントが半分以上残っている場合は攻撃可能
				//半分以下の場合は行動終了
				if (remMovePoint <= 0) {
					break;
				}
				//アクションを抽選・・・このステージに入るときは必ずENEMYなのでキャスト失敗しない
				Enemy user = (Enemy) currentCmd.getUser();
				List<Action> eba = user.getStatus().get現状実行可能なアクション()
						.stream()
						.filter(p -> p.getType() == ActionType.攻撃)
						.toList();
				if (eba.isEmpty()) {
					//実行可能な攻撃はない
					break;
				}
				//エリア計算
				Collections.shuffle(eba);
				Action selected = null;
				Point2D.Float center = user.getSprite().getCenter();
				List<Actor> tgt = null;
				L1:
				for (Action a : eba) {
					int area = user.getStatus().getEffectedArea(a);
					tgt = BattleTargetSystem.recalcDistance(GameSystem.getInstance().getParty(), center, area);
					if (!tgt.isEmpty() && a.canDo(user.getStatus())) {
						selected = a;
						break L1;
					}
				}
				if (selected == null || tgt == null || tgt.isEmpty()) {
					//実行可能な攻撃はない
					break;
				}
				//移動後攻撃実行
				ActionResult res = selected.exec(new ActionTarget(user, selected, tgt, false));
				setStage(攻撃結果処理(res));
				return;
			}
			case バトル終了済み: {
				//処理なし
				break;
			}
			case エフェクト再生中_終了待ち: {
				assert effect != null : "effect is null : " + this;
				if (effect.isEnded()) {
					//再度死亡者がいるかチェックする。
					messageQueue消化();
					return;
				}
				break;
			}
			case アイテム用途選択画面表示中:
			case アイテム詳細画面表示中:
			case コマンド選択中:
			case ステータス閲覧中_閉じる待ち:
			case プレイヤキャラターゲット選択中_コミット待ち:
			case 待機中＿処理中:
			case 移動後行動選択中_コミット待ち:
				//処理なし
				break;
			default: {
				throw new GameSystemException("BS illegal state : " + this);
			}
		}
	}

	//アクションリザルトウインドウに出す。主に1行用。
	//"/" ok
	private void setMsg(String s) {
		if (s.contains("/")) {
			setMsg(Arrays.asList(s.split("/")));
			return;
		}
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
	}

	//アクションリザルトウインドウに出す。主に複数行用。
	//"/" ng
	private void setMsg(List<String> s) {
		//8行以上ある場合は2列づつ出す。
		boolean isOver8Line = s.size() >= 8;

		//リスト要素の展開
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (true) {
			if (isOver8Line) {
				sb.append(s.get(i));
				i++;
				if (i >= s.size()) {
					break;
				}
				sb.append(s.get(i));
				i++;
			} else {
				sb.append(s.get(i));
				i++;
			}
			if (i >= s.size()) {
				break;
			}
			sb.append(Text.getLineSep());
		}
		messageWindowSystem.getActionResultW().setText(sb.toString());
		messageWindowSystem.getActionResultW().allText();
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
	}

	private void setStage(Stage s) {
		GameLog.print("BS " + prevStage + " -> " + stage + " -> " + s);
		this.prevStage = stage;
		this.stage = s;
	}

	@Override
	@LoopCall
	public void draw(GraphicsContext g) {
		battleFieldSystem.draw(g);

		enemies.forEach(p -> p.getSprite().draw(g));
		GameSystem.getInstance().getPartySprite().forEach(p -> p.draw(g));

		animation.forEach(v -> v.draw(g));

		castingSprites.values().forEach(p -> p.draw(g));

		targetSystem.draw(g);

		messageWindowSystem.draw(g);

		if (effect != null) {
			effect.draw(g);
		}
	}

	void addCmdFirst(BattleCommand cmd) {
		commandsOfThisTurn.add(0, cmd);
	}

	void addCmdLast(BattleCommand cmd) {
		commandsOfThisTurn.add(cmd);
	}

	void addCmdFirst(MagicSpell ms) {
		BattleCommand c = new BattleCommand(
				ms.isPlayer() ? BattleCommand.Mode.PC : BattleCommand.Mode.CPU,
				ms.getUser());
		c.setMagicSpell(true);
		c.setAction(List.of(ms.getAction()));
		c.setUserOperation(false);
		addCmdFirst(c);
	}

	void addCmdLast(MagicSpell ms) {
		BattleCommand c = new BattleCommand(
				ms.isPlayer() ? BattleCommand.Mode.PC : BattleCommand.Mode.CPU,
				ms.getUser());
		c.setMagicSpell(true);
		c.setAction(List.of(ms.getAction()));
		c.setUserOperation(false);
		addCmdLast(c);
	}

	void moveToFirst(BattleCommand cmd) {
		LinkedList<BattleCommand> res = new LinkedList<>();
		res.add(cmd);
		res.addAll(commandsOfThisTurn.stream().filter(p -> !p.equals(cmd)).toList());
		commandsOfThisTurn = res;
	}

	void moveToLast(BattleCommand cmd) {
		LinkedList<BattleCommand> res = new LinkedList<>();
		res.addAll(commandsOfThisTurn.stream().filter(p -> !p.equals(cmd)).toList());
		res.add(cmd);
		commandsOfThisTurn = res;
	}

	//--------------------------------------------------------------------------get/set
	public int getTurn() {
		return turn;
	}

	@Deprecated
	public void setTurn(int turn) {
		this.turn = turn;
	}

	public List<Point2D.Float> getPartyInitialLocation() {
		return partyInitialLocation;
	}

	@Deprecated
	public void setPartyInitialLocation(List<Point2D.Float> partyInitialLocation) {
		this.partyInitialLocation = partyInitialLocation;
	}

	public List<FourDirection> getPartyInitialDir() {
		return partyInitialDir;
	}

	@Deprecated
	public void setPartyInitialDir(List<FourDirection> partyInitialDir) {
		this.partyInitialDir = partyInitialDir;
	}

	public List<Point2D.Float> getPartyTargetLocationForFirstMove() {
		return partyTargetLocationForFirstMove;
	}

	@Deprecated
	public void setPartyTargetLocationForFirstMove(List<Point2D.Float> partyTargetLocationForFirstMove) {
		this.partyTargetLocationForFirstMove = partyTargetLocationForFirstMove;
	}

	public Sound getPrevBGM() {
		return prevBGM;
	}

	@Deprecated
	public void setPrevBGM(Sound prevBGM) {
		this.prevBGM = prevBGM;
	}

	public Sound getCurrentBGM() {
		return currentBGM;
	}

	@Deprecated
	public void setCurrentBGM(Sound currentBGM) {
		this.currentBGM = currentBGM;
	}

	public Sound getWinBGM() {
		return winBGM;
	}

	@Deprecated
	public void setWinBGM(Sound winBGM) {
		this.winBGM = winBGM;
	}

	public String getWinLogicName() {
		return winLogicName;
	}

	@Deprecated
	public void setWinLogicName(String winLogicName) {
		this.winLogicName = winLogicName;
	}

	public String getLoseLogicName() {
		return loseLogicName;
	}

	@Deprecated
	public void setLoseLogicName(String loseLogicName) {
		this.loseLogicName = loseLogicName;
	}

	@NotNewInstance
	public List<Enemy> getEnemies() {
		return enemies;
	}

	@Deprecated
	public void setEnemies(List<Enemy> enemies) {
		this.enemies = enemies;
	}

	public LinkedList<BattleCommand> getCommandsOfThisTurn() {
		return commandsOfThisTurn;
	}

	@Deprecated
	public void setCommandsOfThisTurn(LinkedList<BattleCommand> commandsOfThisTurn) {
		this.commandsOfThisTurn = commandsOfThisTurn;
	}

	public LinkedHashMap<Integer, List<MagicSpell>> getMagics() {
		return magics;
	}

	@Deprecated
	public void setMagics(LinkedHashMap<Integer, List<MagicSpell>> magics) {
		this.magics = magics;
	}

	public List<Sprite> getAnimation() {
		return animation;
	}

	@Deprecated
	public void setAnimation(List<Sprite> animation) {
		this.animation = animation;
	}

	public FrameTimeCounter getCurrentBAWaitTime() {
		return currentBAWaitTime;
	}

	@Deprecated
	public void setCurrentBAWaitTime(FrameTimeCounter currentBAWaitTime) {
		this.currentBAWaitTime = currentBAWaitTime;
	}

	public BattleCommand getCurrentCmd() {
		return currentCmd;
	}

	@Deprecated
	public void setCurrentCmd(BattleCommand currentCmd) {
		this.currentCmd = currentCmd;
	}

	public int getMessageWaitTime() {
		return messageWaitTime;
	}

	@Deprecated
	public void setMessageWaitTime(int messageWaitTime) {
		this.messageWaitTime = messageWaitTime;
	}

	//ここから戦利品を取得せよ
	public BattleResultValues getBattleResultValue() {
		return battleResultValue;
	}

	@Deprecated
	public void setBattleResultValue(BattleResultValues battleResultValue) {
		this.battleResultValue = battleResultValue;
		setEndStatus(battleResultValue.getBattleResult());
	}

	public void initBattleResult() {
		this.battleResultValue = null;
	}

	public int getRemMovePoint() {
		return remMovePoint;
	}

	@Deprecated
	public void setRemMovePoint(int remMovePoint) {
		this.remMovePoint = remMovePoint;
	}

	public Point2D.Float getMoveIinitialLocation() {
		return moveIinitialLocation;
	}

	@Deprecated
	public void setMoveIinitialLocation(Point2D.Float moveIinitialLocation) {
		this.moveIinitialLocation = moveIinitialLocation;
	}

	public Stage getStage() {
		return stage;
	}

	public BattleMessageWindowSystem getMessageWindowSystem() {
		return messageWindowSystem;
	}

	@Deprecated
	public void setMessageWindowSystem(BattleMessageWindowSystem messageWindowSystem) {
		this.messageWindowSystem = messageWindowSystem;
	}

	public BattleTargetSystem getTargetSystem() {
		return targetSystem;
	}

	@Deprecated
	public void setTargetSystem(BattleTargetSystem targetSystem) {
		this.targetSystem = targetSystem;
	}

	public BattleFieldSystem getBattleFieldSystem() {
		return battleFieldSystem;
	}

	@Deprecated
	public void setBattleFieldSystem(BattleFieldSystem battleFieldSystem) {
		this.battleFieldSystem = battleFieldSystem;
	}

	public boolean isEnd() {
		return end;
	}

	@Deprecated
	public void setEnd(boolean end) {
		this.end = end;
	}

	public boolean isShowMode() {
		return showMode;
	}

	@Deprecated
	public void setShowMode(boolean showMode) {
		this.showMode = showMode;
	}

	public boolean isPrevAttackOK() {
		return prevAttackOK;
	}

	@Deprecated
	public void setPrevAttackOK(boolean prevAttackOK) {
		this.prevAttackOK = prevAttackOK;
	}

	public boolean isAfterMove() {
		return afterMove;
	}

	@Deprecated
	public void setAfterMove(boolean afterMove) {
		this.afterMove = afterMove;
	}

	public Map<Actor, Sprite> getCastingSprite() {
		return castingSprites;
	}

	@Deprecated
	public void setCastingSprite(Map<Actor, Sprite> castingSprite) {
		this.castingSprites = castingSprite;
	}

	public int getItemChoiceMode() {
		return itemChoiceMode;
	}

	@Deprecated
	public void setItemChoiceMode(int itemChoiceMode) {
		this.itemChoiceMode = itemChoiceMode;
	}

	public Item getItemPassAndUse() {
		return itemPassAndUse;
	}

	@Deprecated
	public void setItemPassAndUse(Item itemPassAndUse) {
		this.itemPassAndUse = itemPassAndUse;
	}

	@NewInstance
	@NotNull
	public List<Actor> allActors() {
		List<Actor> res = new ArrayList<>(GameSystem.getInstance().getParty());
		res.addAll(enemies);
		return res;
	}

	//デバッグ用。非常に長いので注意
	@Deprecated
	@Override
	public String toString() {
		return "BattleSystem{" + "turn=" + turn + ", partyInitialLocation=" + partyInitialLocation + ", partyInitialDir=" + partyInitialDir + ", partyTargetLocationForFirstMove=" + partyTargetLocationForFirstMove + ", prevBGM=" + prevBGM + ", currentBGM=" + currentBGM + ", winBGM=" + winBGM + ", winLogicName=" + winLogicName + ", loseLogicName=" + loseLogicName + ", enemies=" + enemies + ", commandsOfThisTurn=" + commandsOfThisTurn + ", magics=" + magics + ", animation=" + animation + ", currentBAWaitTime=" + currentBAWaitTime + ", currentCmd=" + currentCmd + ", messageWaitTime=" + messageWaitTime + ", battleResultValue=" + battleResultValue + ", remMovePoint=" + remMovePoint + ", moveIinitialLocation=" + moveIinitialLocation + ", stage=" + stage + ", messageWindowSystem=" + messageWindowSystem + ", targetSystem=" + targetSystem + ", battleFieldSystem=" + battleFieldSystem + ", end=" + end + ", showMode=" + showMode + ", prevAttackOK=" + prevAttackOK + ", afterMove=" + afterMove + ", castingSprite=" + castingSprites + ", itemChoiceMode=" + itemChoiceMode + ", itemPassAndUse=" + itemPassAndUse + '}';
	}

}
