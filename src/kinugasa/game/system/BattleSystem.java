/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kinugasa.game.GameLog;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.LoopCall;
import kinugasa.game.NoLoopCall;
import kinugasa.game.OneceTime;
import kinugasa.game.field4.PlayerCharacterSprite;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.ui.Text;
import kinugasa.object.AnimationSprite;
import kinugasa.object.BasicSprite;
import kinugasa.object.Drawable;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.object.Sprite;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.resource.sound.CachedSound;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;

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
	//現在のステージ
	private StageHolder stage;
	//-------------------------------------------------------------------システム
	//メッセージウインドウシステムのインスタンス
	private BattleMessageWindowSystem messageWindowSystem;
	//ターゲット選択システムのインスタンス
	private BattleTargetSystem targetSystem;
	//バトルフィールドインスタンス
	private BattleFieldSystem battleFieldSystem;
	//状態異常マネージャ
	private ConditionManager conditionManager;
	//バトル終了フラグ（true=終了
	private boolean end = false;
	//戦況図モードかどうか
	private boolean showMode = false;
	//AfterMoveAction更新用の前回検査時の攻撃可否
	private boolean prevAttackOK = false;
	//移動後攻撃モードかどうか
	private boolean afterMove = false;
	//詠唱中アニメーション
	private Map<BattleCharacter, Sprite> castingSprite = new HashMap<>();
	//-----------------------------------------------------------アイテム
	//アイテムChoiceUseインデックス：-1：ターゲット選択未使用
	private int itemChoiceMode = -1;
	//アイテム使用とパスのアイテム本体
	private Item itemPassAndUse;

	class StageHolder {

		private Stage stage;
		private Stage next;
		private Stage prev;

		public void setStage(Stage stage) {
			setStage(stage, null);
		}

		public void setStage(Stage s, Stage n) {
			if (GameSystem.isDebugMode()) {
				kinugasa.game.GameLog.print(" changeStage : [" + this.stage + "] to [" + s + "] and[" + n + "]");
			}
			prev = s != this.stage ? this.stage : prev;
			this.stage = s;
			this.next = n;
			if (this.stage == Stage.AFTER_MOVE_CMD_SELECT) {
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.AFTER_MOVE);
				return;
			}
			if (this.stage == Stage.ITEM_CHOICE_USE) {
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ITEM_USE_SELECT);
				return;
			}
			if (this.stage == Stage.SHOW_ITEM_DESC) {
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.SHOW_ITEM_DESC);
				return;
			}
			if (this.stage == Stage.TARGET_SELECT) {
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.TGT_SELECT);
				return;
			}
			if (this.stage == Stage.SHOW_STATUS) {
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.SHOW_STATUS_DESC);
				//ステータス表示
				int i = 0;
				for (; !currentCmd.getUser().getStatus().equals(GameSystem.getInstance().getPartyStatus().get(i)); i++);
				messageWindowSystem.setStatusDescPCIDX(i);
				return;
			}
			if (this.stage == Stage.CMD_SELECT) {
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);
				return;
			}
			if (this.stage == Stage.BATLE_END) {
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.BATTLE_RESULT);
				return;
			}
			if (this.stage == Stage.EXECUTING_ACTION) {
				if (currentBAWaitTime == null) {
					currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
				}
				messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
				return;
			}
		}

		public Stage getNext() {
			return next;
		}

		public Stage getStage() {
			return stage;
		}

		public Stage getPrev() {
			return prev;
		}

		public void prev() {
			setStage(prev);
		}

		public void next() {
			if (next == null) {
				throw new GameSystemException("next stage is null");
			}
			setStage(next);
		}

	}

	public enum Stage {
		/**
		 * 開始?初期移動まで
		 */
		STARTUP,
		/**
		 * 初期移動中
		 */
		INITIAL_MOVING,
		/**
		 * ユーザ操作待ち
		 */
		WAITING_EXEC_CMD,
		/**
		 * コマンド選択中
		 */
		CMD_SELECT,
		/**
		 * 逃げアニメーション実行中。終わったらWAITに入る。
		 */
		ESCAPING,
		/**
		 * プレイヤーキャラクタ移動中。確定アクションが呼ばれるまで何もしない。
		 */
		PLAYER_MOVE,
		/**
		 * ターゲット選択中。execが呼ばれるまで何もしない。
		 */
		TARGET_SELECT,
		/**
		 * 自動処理実行中。アクションメッセージが表示されるので、OKを押すと次に進む。
		 */
		EXECUTING_ACTION,
		/**
		 * 移動後行動選択中
		 */
		AFTER_MOVE_CMD_SELECT,
		/**
		 * ステータス閲覧中
		 */
		SHOW_STATUS,
		/**
		 * アイテム用途選択画面
		 */
		ITEM_CHOICE_USE,
		/**
		 * アイテム詳細確認中
		 */
		SHOW_ITEM_DESC,
		/**
		 * 敵移動実行中。終わったらWAITに入る。
		 */
		EXECUTING_MOVE,
		/**
		 * バトルは終了して、ゲームシステムからの終了指示を待っている。
		 */
		BATLE_END,
	}

	private enum MessageType {
		INITIAL_ENEMY_INFO {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return String.join("/", option);
			}
		},
		SPELL_BUT_NO_TARGET {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.XはXを詠唱した, user.getName(), a.getName())
						+ Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.しかし効果範囲に対象がいない);
			}
		},
		SPELL_BUT_SHORTAGE {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.XはXを詠唱した, user.getName(), a.getName())
						+ Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.しかしXが足りない, String.join(",", option));
			}
		},
		SPELL_SUCCESS {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return String.join(Text.getLineSep(), option);
			}
		},
		STOPING_BY_CONDITION {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return toString() + "/" + user + "/" + option;
			}
		},
		STOP_BECAUSE_CONFU {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは混乱していて動けない, user.getName());
			}
		},
		IS_MOVED {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは移動した, user.getName());
			}
		},
		PC_USE_AVO {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは回避に専念した, user.getName());
			}
		},
		PC_USE_DEFENCE {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは防御に専念した, user.getName());
			}
		},
		PC_IS_ESCAPE {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは逃げ出した, user.getName());
			}
		},
		PC_IS_ESCAPE_MISS {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは逃げ出した, user.getName())
						+ Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.しかし戦闘エリアの中心にいては逃げられない);
			}
		},
		NO_TARGET {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.効果範囲に対象がいない);
			}

		},
		EQIP_ITEM {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは, user.getName()) + Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.Xを装備した, option.get(0));
			}
		},
		UNEQIP_ITEM {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは, user.getName()) + Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.Xを外した, option.get(0));
			}
		},
		CANT_EQIP {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xは装備できない, option.get(0));
			}
		},
		CANT_UNEQIP {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, option.get(0));
			}
		},
		CANT_USE_THIS_ITEM {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.XはXを使用した, user.getName(), option.get(0))
						+ Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.しかし効果がなかった);
			}
		},
		ITEM_WHO_TO_USE {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xの, user.getName())
						+ I18N.get(GameSystemI18NKeys.Xを誰に使う, option.get(0));
			}
		},
		ITEM_WHO_TO_PASS {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.Xの, user.getName())
						+ I18N.get(GameSystemI18NKeys.Xを誰に渡す, option.get(0));
			}
		},
		ITEM_PASSED {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.XはXにXを渡した, user.getName(), option.get(0), option.get(1));
			}
		},
		ITEM_USED {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				Item i = (Item) a;
				String s = I18N.get(GameSystemI18NKeys.XはXを使用した, user.getName(), i.getName());
				if (res.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
					//アイテム効果判定
					//ターゲット取得
					String tgtName = option.get(0);
					BattleCharacter tgt = null;
					for (BattleCharacter c : BattleSystem.getInstance().enemies) {
						if (tgtName.equals(c.getName())) {
							tgt = c;
						}
					}
					for (BattleCharacter c : GameSystem.getInstance().getParty()) {
						if (tgtName.equals(c.getName())) {
							tgt = c;
						}
					}
					//ターゲットの自動ダメージ計算結果を取得
					Map<StatusKey, Float> damage = tgt.getStatus().calcDamage();
					for (Map.Entry<StatusKey, Float> e : damage.entrySet()) {
						if (e.getValue() > 0) {
							s += (I18N.get(GameSystemI18NKeys.Xの, tgt.getName()));
							s += (I18N.get(GameSystemI18NKeys.Xは, e.getKey().getDesc()));
							s += (I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
							s += (Text.getLineSep());
						} else if (e.getValue() < 0) {
							s += (I18N.get(GameSystemI18NKeys.Xの, tgt.getName()));
							s += (I18N.get(GameSystemI18NKeys.Xに, e.getKey().getDesc()));
							s += (I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""));
						} else {
							s += (I18N.get(GameSystemI18NKeys.しかし効果がなかった));
							s += (Text.getLineSep());
						}
					}
				} else {
					s += (I18N.get(GameSystemI18NKeys.しかし効果がなかった));
					s += (Text.getLineSep());
				}
				return s;
			}
		},
		SPELL_START {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return I18N.get(GameSystemI18NKeys.XはXを詠唱した, user.getName(), a.getName());
			}
		},
		ACTION_SUCCESS {
			@Override
			String get(Action a, Status user, List<String> option, ActionResult res) {
				return String.join(Text.getLineSep(), option);
			}
		},;

		abstract String get(Action a, Status user, List<String> option, ActionResult res);
	}

	//---------------------------------------UTIL--------------------------------------
	@Deprecated
	public void setBattleResultValue(BattleResultValues battleResultValue) {
		this.battleResultValue = battleResultValue;
	}

	/**
	 * 現在のステージを取得します
	 *
	 * @return ステージ
	 */
	public Stage getStage() {
		return stage.getStage();
	}

	/**
	 * 現在のターン数を取得します。
	 *
	 * @return ターン数、1スタート。
	 */
	public int getTurn() {
		return turn;
	}

	private List<BattleCharacter> getAllChara() {
		List<BattleCharacter> result = new ArrayList<>();
		result.addAll(enemies);
		result.addAll(GameSystem.getInstance().getParty());
		return result;
	}

	//------------------------------------初期化---------------------------------------
	public void encountInit(EncountInfo enc) {
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print(" -----------------BATTLE_START------------------------------------------------");
		}
		stage = new StageHolder();
		stage.setStage(BattleSystem.Stage.STARTUP);
		//エンカウント情報の取得
		EnemySetStorage ess = enc.getEnemySetStorage().build();
		EnemySet es = ess.get();
		winLogicName = es.getWinLogicName();
		loseLogicName = es.getLoseLogicName();
		//前BGMの停止
		prevBGM = enc.getPrevBGM();
		if (prevBGM == null) {
			//prevがnullでも再生中のBGMがあるか検査する。あればそれを停止する
			for (Sound s : SoundStorage.getInstance()) {
				if (((CachedSound) s).getBuilder().getFile().getName().contains("bgm")) {
					if (s.isPlaying()) {
						s.stop();
					}
				}
			}
		}
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
		conditionManager = ConditionManager.getInstance();
		//念のためパーティーのアクションを更新
		GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction(true));
//		//アイテム使用をアクションに追加する
//		for (PlayerCharacter pc : gs.getParty()) {
//			pc.getStatus().getActions().addAll(pc.getStatus().getItemBag().getItems());
//		}

		//出現MSG設定用マップ
		Map<String, Long> enemyNum = enemies.stream().collect(Collectors.groupingBy(Enemy::getId, Collectors.counting()));
		//出現MSG設定
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Long> e : enemyNum.entrySet()) {
			sb.append(I18N.get(GameSystemI18NKeys.XがX体現れた, e.getKey(), e.getValue() + "")).append(Text.getLineSep());
		}
		//敵出現情報をセット
		setMsg(MessageType.INITIAL_ENEMY_INFO, null, null, null, List.of(sb.toString().split(Text.getLineSep())));
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);

		//リセット
		currentCmd = null;
		currentBAWaitTime = null;
		commandsOfThisTurn.clear();
		turn = 0;
		animation.clear();
		targetSystem.unsetCurrent();
		afterMove = false;

		//敵の配置
		putEnemy();

		//味方の配置
		putParty();
		assert partyTargetLocationForFirstMove.size() == gs.getParty().size() : "initial move target is missmatch";

		//初期移動実行へ
		stage.setStage(BattleSystem.Stage.INITIAL_MOVING);
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
		//ステータスイニシャル保存
		for (Status s : GameSystem.getInstance().getPartyStatus()) {
			for (StatusValue v : s.getBaseStatus()) {
				v.saveInitial();
			}
			for (AttributeValue v : s.getBaseAttrIn()) {
				v.saveInitial();
			}
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

	//------------------------------------------PP処理M------------------------------------
	void turnStart() {
		turn++;
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print(" -----------------TURN[" + turn + "] START-----------------");
		}
		//このターンのバトルコマンドを作成
		List<BattleCharacter> list = getAllChara();
		if (SpeedCalcModelStorage.getInstance().getCurrent() == null) {
			throw new GameSystemException("speed calc model is null");
		}
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

		//ユーザ名に対して1つのコマンドがあることを確認
		Set<String> set = new HashSet<>();
		for (BattleCommand cmd : commandsOfThisTurn) {
			String name = cmd.getUser().getName();
			if (set.contains(name)) {
				throw new GameSystemException("duplicate command user:" + name);
			}
			set.add(name);
		}

		//コマンドがパーティーだけの場合、戦闘終了
		if (commandsOfThisTurn.stream().map(p -> p.getUser()).collect(Collectors.toList())
				.equals(GameSystem.getInstance().getParty())) {
			stage.setStage(Stage.BATLE_END);
			return;
		}

		stage.setStage(BattleSystem.Stage.WAITING_EXEC_CMD);
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
	//--------------------------------END_BATTLE------------------------------------------
	//

	@OneceTime
	void endBattle() {
		//味方の配置の初期化
		List<PlayerCharacterSprite> partySprite = GameSystem.getInstance().getPartySprite();
		for (int i = 0; i < partySprite.size(); i++) {
			//位置の復元
			partySprite.get(i).to(partyInitialDir.get(i));
			partySprite.get(i).setLocation(partyInitialLocation.get(i));
		}
		//敵番号の初期化
		EnemyBlueprint.initEnemyNoMap();
		//逃げたコンディションで非表示になっている場合表示する
		//アイテムアクションを削除する
		for (PlayerCharacter pc : GameSystem.getInstance().getParty()) {
			if (pc.getStatus().hasCondition(BattleConfig.ConditionName.escaped)) {
				//逃げた人のスプライトを表示に戻す
				pc.getSprite().setVisible(true);
				//逃げたコンディションを外す
				pc.getStatus().removeCondition(BattleConfig.ConditionName.escaped);
			}
			//アイテムアクションの削除
			List<Action> removeList = pc.getStatus().getActions().stream().filter(p -> p.getType() == ActionType.ITEM).collect(Collectors.toList());
			pc.getStatus().getActions().removeAll(removeList);
			//詠唱中コンディションを外す
			pc.getStatus().removeCondition(BattleConfig.ConditionName.casting);
			//防御・回避コンディションを外す
			pc.getStatus().removeCondition(BattleConfig.ConditionName.defence);
			pc.getStatus().removeCondition(BattleConfig.ConditionName.avoidance);
			//ステータス復元（状態異常効果を消す）最大値を超えている場合は最大値にする
			pc.getStatus().getBaseAttrIn().forEach(p -> p.setMaxIfOverMax());
			pc.getStatus().getBaseStatus().forEach(p -> p.setMaxIfOverMax());
		}
		//倒した敵の数カウント
		//TODO:RACE別にする
		int c = (int) enemies.stream().filter(p -> p.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())).count();
		if (GameSystem.getInstance().getCountSystem().contains(Counts.KEY_倒した敵の数)) {
			c += Counts.getInstance().get(Counts.KEY_倒した敵の数).num;
		}
		Counts.getInstance().put(new Counts.Value(Counts.KEY_倒した敵の数, c));

		//BGMの処理
//		if (currentBGM != null) {
//			currentBGM.stop();
//			currentBGM.dispose();
//		}
		winBGM.stop();//強制実行
		winBGM.dispose();
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
		afterMove = false;
		currentCmd = commandsOfThisTurn.getFirst();
		assert currentCmd != null : "currentCMD is null";
		commandsOfThisTurn.removeFirst();
		BattleCharacter user = currentCmd.getUser();

		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print(" currentCMD:" + currentCmd);
		}

		//ターゲットシステム初期化
		targetSystem.unsetCurrent();
		currentBAWaitTime = null;
		//アンターゲット状態異常の場合、メッセージ出さずに次に送る
		if (user.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
			if (GameSystem.isDebugMode()) {
				kinugasa.game.GameLog.print(user.getStatus().getName() + " is bad condition");
			}
			stage.setStage(BattleSystem.Stage.WAITING_EXEC_CMD);
			return execCmd();
		}
		//防御または回避中の場合、1ターンだけ有効なため、今回そのフラグを外す
		if (user.getStatus().hasCondition(BattleConfig.ConditionName.defence)) {
			user.getStatus().removeCondition(BattleConfig.ConditionName.defence);
		}
		if (user.getStatus().hasCondition(BattleConfig.ConditionName.avoidance)) {
			user.getStatus().removeCondition(BattleConfig.ConditionName.avoidance);
		}

		//状態異常で動けないときスキップ（メッセージは出す
		//詠唱中の場合はのぞく
		if (currentCmd.isStop()) {
			if (currentCmd.getUser().getStatus().getCondition().stream().filter(p -> p.getName().equals(BattleConfig.ConditionName.casting)).count() != 1) {
				setMsg(MessageType.STOPING_BY_CONDITION, user.getStatus(), null, null, List.of(user.getStatus().moveStopDesc().getKey().getDesc()));
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				return currentCmd;
			}
		}

		//混乱で動けないときは、停止またはバトルアクションを適当に取得して自動実行する
		if (currentCmd.isConfu()) {
			if (Random.percent(BattleConfig.confuStopP)) {
				//動けない
				Action ba = currentCmd.getFirstBattleAction();
				setMsg(MessageType.STOP_BECAUSE_CONFU, user.getStatus(), ba, null, null);
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				return currentCmd;
			} else {
				//動けるが混乱
				Action ba = currentCmd.randomAction();
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				execAction(ba);
				return currentCmd;
			}
		}

		//魔法詠唱完了イベントの場合、PCでもNPCでも自動実行、（詠唱中コンディションを外す
		//魔法のコストとターゲットは、詠唱開始と終了の2回判定する。
		//ここは「詠唱終了時」の処理。
		if (currentCmd.isMagicSpell()) {
			Action ba = currentCmd.getFirstBattleAction();//1つしか入っていない
			//保存したターゲットを取得
			ActionTarget target = targetSystem.getTarget(user);
			if (target == null) {
				throw new GameSystemException("saving target is null : " + ba);
			}
			if (GameSystem.isDebugMode()) {
				GameLog.print("BS AUTO MAGIC CAST : " + ba + " / " + target);
			}
			//詠唱中状態異常を外す
			currentCmd.getUser().getStatus().removeCondition(BattleConfig.ConditionName.casting);
			//詠唱中アニメーション破棄
			castingSprite.remove(user);
			//保存したターゲットから距離外れた対象を除去
			List<BattleCharacter> removeList = new ArrayList<>();
			for (BattleCharacter c : target.getTarget()) {
				if (ba.getAreaWithEqip(currentCmd.getUser()) < currentCmd.getUser().getCenter().distance(c.getCenter())) {
					removeList.add(c);
				}
			}
			target.getTarget().removeAll(removeList);
			//ターゲットがいない場合、詠唱失敗のメッセージ出す
			if (target.isEmpty()) {
				setMsg(MessageType.SPELL_BUT_NO_TARGET, user.getStatus(), ba, null, null);
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				return currentCmd;
			}

			//現状のステータスで対価を支払えるか確認
			//※実際に支払うのはexecしたとき。
			Map<StatusKey, Integer> damage = ba.selfBattleDirectDamage();
			//ダメージを合算
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//ダメージがあって、マイナスの項目がある場合、対価を支払えないため空振り
			//この魔法の消費項目を取得
			if (!damage.isEmpty() && simulateDamage.hasMinus()) {
				//しかしMPが足りない
				List<String> shortageStatusDesc = simulateDamage.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList());
				setMsg(MessageType.SPELL_BUT_SHORTAGE, user.getStatus(), ba, null, shortageStatusDesc);
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				return currentCmd;
			}
			//詠唱成功、魔法効果発動
			target.forEach(p -> p.getStatus().setDamageCalcPoint());
			ActionResult res = ba.exec(target);
			setMsg(MessageType.SPELL_SUCCESS, null, ba, res, actionResultProc(user, ba, target));
			animation.addAll(res.getAnimation());
			currentBAWaitTime = res.getWaitTime().clone();
			stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
			return currentCmd;
		}//魔法ここまで

		//NPC
		if (currentCmd.getMode() == BattleCommand.Mode.CPU) {
			//NPCアクションを自動実行、この中でステージも変わるしMSGも設定される
			execAction(currentCmd.getBattleActionEx(((Enemy) currentCmd.getUser()).getAI(), ActionType.OTHER, ActionType.ITEM));
			return currentCmd;
		}

		//PCのアクション実行
		//カレントコマンド内容をコマンドウインドウに表示、その他ウインドウは閉じる
		messageWindowSystem.getCmdW().setCmd(currentCmd);
		messageWindowSystem.getCmdW().resetSelect();
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);

		//ターゲットシステムをウインドウの初期選択で初期化
		assert messageWindowSystem.getCmdW().getSelectedCmd() != null : "cmdW initial select action is null";
		targetSystem.setCurrent(currentCmd.getUser(), messageWindowSystem.getCmdW().getSelectedCmd());

		//PCの操作なので、カレントコマンドのユーザオペレーション要否フラグをONに設定
		currentCmd.setUserOperation(true);

		stage.setStage(BattleSystem.Stage.CMD_SELECT);
		return currentCmd;
	}

	@NoLoopCall
	public void commitCmd() {
		//移動後攻撃か通常攻撃かを判定
		if (afterMove) {
			targetSystem.getCurrentArea().setVisible(false);
			execAction(messageWindowSystem.getAfterMoveW().getSelectedCmd());
		} else {
			if (messageWindowSystem.getCmdW().getSelectedCmd() == null) {
				afterMove = false;
				return;//使える魔法／アイテムがない
			}
			execAction(messageWindowSystem.getCmdW().getSelectedCmd());
		}
	}

	//アクション実行（コミット、窓口）
	void execAction(Action a) {
		//PC,NPC問わず選択されたアクションを実行する。

		//ウインドウ状態初期化・・・アクション実行前
		messageWindowSystem.getActionResultW().setText("");
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);

		//カレントユーザ
		BattleCharacter user = currentCmd.getUser();

		//混乱中の場合
		if (user.getStatus().isConfu()) {
			//ターゲットシステムのカレント起動しないで対象を取得する
			ActionTarget tgt = BattleTargetSystem.instantTarget(user, a);
			execAction(a, tgt);
			return;
		}

		//NPCの場合
		if (!user.isPlayer()) {
			//アクションの効果範囲に相手がいるか、インスタント確認
			ActionTarget tgt = BattleTargetSystem.instantTarget(user, a);
			//アイテム使用の場合
			if (a instanceof Item) {
				//自分またはインスタ内の誰かを選択
				List<BattleCharacter> l = new ArrayList<>();
				l.add(user);
				l.addAll(tgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
						< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax()).collect(Collectors.toList()));
				Collections.shuffle(l);
				//抽選されたターゲット
				BattleCharacter t = l.get(0);
				execAction(a, new ActionTarget(user, a).setInField(false).setTarget(List.of(t)).setSelfTarget(t.equals(user)));
				return;
			}
			if (a.getType() == ActionType.MAGIC) {
				//回復魔法抽選の場合か判定
				//効果が＋か確認
				if (a.getBattleEvent().stream().mapToInt(p -> (int) p.getValue()).filter(p -> p > 0).sum() > 0) {
					boolean hpIsUnderHarf = user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
							< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax();
					boolean otherHpHarfTgt = tgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
							< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax()).collect(Collectors.toList()).size() > 0;
					if (hpIsUnderHarf || otherHpHarfTgt) {
						//自分またはインスタ内の誰かを選択
						List<BattleCharacter> l = new ArrayList<>();
						l.add(user);
						l.addAll(tgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
								< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax()).collect(Collectors.toList()));
						Collections.shuffle(l);
						//抽選されたターゲット
						BattleCharacter t = l.get(0);
						execAction(a, new ActionTarget(user, a).setInField(false).setTarget(List.of(t)).setSelfTarget(t.equals(user)));
						return;
					}
				}
			}

			if (tgt.isEmpty()) {
				//ターゲットがいない場合で、移動アクションを持っている場合は移動開始
				if (user.getStatus().hasAction(BattleConfig.ActionName.move)) {
					//移動ターゲットは最も近いPCとする
					Point2D.Float tgtLocation = ((Enemy) user).getAI().targetLocation(user);
					user.setTargetLocation(tgtLocation, a.getAreaWithEqip(user));
					//移動距離を初期化
					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
					//移動した！のメッセージ表示
					setMsg(MessageType.IS_MOVED, user.getStatus(), a, null, null);
					messageWindowSystem.setVisible(
							BattleMessageWindowSystem.Mode.ACTION);
					stage.setStage(BattleSystem.Stage.EXECUTING_MOVE);
					return;
				} else {
					//移動できないので何もしない
					return;
				}
			} else {
				//ターゲットがいる場合は即時実行
				execAction(a, tgt);
				return;
			}
		}

		//PCの処理
		assert user.isPlayer() : "PC action, but action is not PC : " + user + " \r\n " + currentCmd;
		assert user.getStatus().getActions().contains(a) : "user not have action";

		//PCの特殊コマンドの処理
		if (a.getType() == ActionType.OTHER) {
			if (a.getName().equals(BattleConfig.ActionName.avoidance)) {
				//回避・回避状態を付与する
				user.getStatus().addCondition(BattleConfig.ConditionName.avoidance);
				setMsg(MessageType.PC_USE_AVO, user.getStatus(), a, null, null);
				stage.setStage(Stage.EXECUTING_ACTION);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.defence)) {
				//防御・防御状態を付与する
				user.getStatus().addCondition(BattleConfig.ConditionName.defence);
				setMsg(MessageType.PC_USE_DEFENCE, user.getStatus(), a, null, null);
				stage.setStage(Stage.EXECUTING_ACTION);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.move)) {
				//移動開始・初期位置を格納
				moveIinitialLocation = user.getSprite().getLocation();
				messageWindowSystem.setVisible(
						BattleMessageWindowSystem.StatusVisible.ON,
						BattleMessageWindowSystem.Mode.AFTER_MOVE,
						BattleMessageWindowSystem.InfoVisible.ON);
				List<Action> action = user.getStatus().getActions(ActionType.ATTACK);
				Collections.sort(action);
				action.add(0, ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
				messageWindowSystem.getAfterMoveW().setActions(action);
				//ターゲットシステムのエリア表示を有効化：値はMOV
				targetSystem.setCurrent(user, a);
				stage.setStage(Stage.PLAYER_MOVE);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.commit)) {
				//移動終了・キャラクタの向きとターゲット座標のクリアをする
				user.unsetTarget();
				user.to(FourDirection.WEST);
				stage.setStage(BattleSystem.Stage.WAITING_EXEC_CMD);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.status)) {
				//ステータス表示
				stage.setStage(Stage.SHOW_STATUS, Stage.CMD_SELECT);
				return;
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
					user.getStatus().addCondition(ConditionStorage.getInstance().get(BattleConfig.ConditionName.escaped).getKey());
					user.setTargetLocation(e, 0);
					user.to(FourDirection.EAST);
					setMsg(MessageType.PC_IS_ESCAPE, user.getStatus(), a, null, null);
					stage.setStage(BattleSystem.Stage.ESCAPING);
					return;
				}
				if (!battleFieldSystem.getBattleFieldAllArea().contains(w)) {
					//逃走成功（←）
					user.getStatus().addCondition(ConditionStorage.getInstance().get(BattleConfig.ConditionName.escaped).getKey());
					user.setTargetLocation(w, 0);
					user.to(FourDirection.WEST);
					setMsg(MessageType.PC_IS_ESCAPE, user.getStatus(), a, null, null);
					stage.setStage(BattleSystem.Stage.ESCAPING);
					return;
				}
//TODO:NPCの逃げはここでない。
//				//NPCの場合、逃げる体制に入る
//				if (!user.isPlayer()) {
//					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
//					user.setTargetLocation(w, 1);
//					setStage(BattleSystem.Stage.EXECUTING_MOVE, "execAction");
//					return OperationResult.SUCCESS;
//				}
				//逃げられない
				setMsg(MessageType.PC_IS_ESCAPE_MISS, user.getStatus(), a, null, null);
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
				return;
			}
		}

		//アイテム選択時の処理
		if (a.getType() == ActionType.ITEM) {
			//アイテム使用　※アイテムextendsアクション
			//アイテムChoiceUseを開くだけ。
			messageWindowSystem.openItemChoiceUse();
			stage.setStage(BattleSystem.Stage.ITEM_CHOICE_USE);
			return;
		}

		assert a.getType() == ActionType.MAGIC || a.getType() == ActionType.ATTACK : "actions are processed in the wrong order.";
		targetSystem.setCurrent(user, a);
		//ターゲット不在
		if (targetSystem.getInAreaDirect().isEmpty()) {//魔法で現状ターゲットがいない場合もここで吸収される
			setMsg(MessageType.NO_TARGET, user.getStatus(), a, null, null);
			//移動後攻撃から遷移してきた場合は空振りさせる
			if (stage.getStage() == Stage.CMD_SELECT) {
				if (GameSystem.isDebugMode()) {
					kinugasa.game.GameLog.print("no target(cmd)");
				}
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
			} else {
				if (GameSystem.isDebugMode()) {
					kinugasa.game.GameLog.print("no target(after)");
				}
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.AFTER_MOVE_CMD_SELECT);
			}
			return;
		}
		//ランダムTGTの場合、ターゲット選択できない（INAREAから適当に拾って使う
		if (a.getTargetOption().getTargeting() == TargetOption.Targeting.DISABLE) {
			//ランダム1体
			ActionTarget tgt = targetSystem.getSelected();
			execAction(a, tgt);
			return;
		}
//
//		//ALLの場合、ターゲット選択不要
//		if (a.hasBattleTT(TargetType.ALL)) {
//			ActionTarget tgt = targetSystem.getSelectedInArea();
//			execAction(a, tgt);
//			return;
//		}
//
//		//SELFのみの場合、ターゲット選択不要
//		if (a.battleEventIsOnly(TargetType.SELF)) {
//			ActionTarget tgt = targetSystem.getSelected();
//			execAction(a, tgt);
//			return;
//		}
//		//魔法詠唱の場合、詠唱中リストに追加して戻る。
//		if (a.getType() == ActionType.MAGIC) {
//			//現状のステータスで対価を支払えるか確認
//			//※実際に支払うのはexecしたとき。
//			Map<StatusKey, Integer> damage = a.selfBattleDirectDamage();
//			//ダメージを合算
//			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
//			//ダメージがあって、-の項目がある場合、対価を支払えないため空振り
//			//この魔法の消費項目を取得
//			if (!damage.isEmpty() && simulateDamage.hasMinus()) {
//				//対象項目で1つでも0の項目があったら空振り
//				List<String> shortageStatusDesc = simulateDamage.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList());
//				setMsg(MessageType.SPELL_BUT_SHORTAGE, user.getStatus(), a, null, shortageStatusDesc);
//				if (user.isPlayer()) {
//					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
//				} else {
//					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
//				}
//				return;
//			}
//			//ターゲット存在確認、現状でいない場合、空振り。発動時の再チェックがここ。
//			if (targetSystem.isEmpty() && !a.battleEventIsOnly(TargetType.SELF)) {
//				setMsg(MessageType.SPELL_BUT_NO_TARGET, user.getStatus(), a, null, null);
//				if (user.isPlayer()) {
//					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
//				} else {
//					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
//				}
//				return;
//			}
//		}
		//ターゲットシステムを起動しない場合は直接実行
		//・targeting = DISABLEの場合
		//・inareaかつIFF OFFの場合
		if (a.getTargetOption().getTargeting() == TargetOption.Targeting.DISABLE) {
			execAction(a, targetSystem.getSelected());
			return;
		}
		if (a.getTargetOption().getSelectType() == TargetOption.SelectType.IN_AREA && a.getTargetOption().getIff() == TargetOption.IFF.OFF) {
			execAction(a, targetSystem.getSelectedInArea());
			return;
		}
		//ターゲットシステム更新
		targetSystem.setCurrent(user, a);

		//その他の場合はターゲット選択必要
		List<String> tgt = new ArrayList<>();
		if (a.getTargetOption().getSelectType() == TargetOption.SelectType.IN_AREA) {
			if (a.getTargetOption().getIff() == TargetOption.IFF.ON) {
				if (a.getTargetOption().getDefaultTarget() == TargetOption.DefaultTarget.ENEMY) {
					tgt.add(I18N.get(GameSystemI18NKeys.敵));
				} else {
					tgt.add(I18N.get(GameSystemI18NKeys.味方));
				}
			} else {
				if (a.getTargetOption().getDefaultTarget() == TargetOption.DefaultTarget.ENEMY) {
					tgt.add(I18N.get(GameSystemI18NKeys.敵));
				} else {
					tgt.add(I18N.get(GameSystemI18NKeys.味方));
				}
			}
		} else {
			if (a.getTargetOption().getDefaultTarget() == TargetOption.DefaultTarget.ENEMY) {
				tgt.addAll(targetSystem.getInAreaEnemy().stream().map(p -> p.getName()).collect(Collectors.toList()));
			} else {
				tgt.addAll(targetSystem.getInAreaTeam().stream().map(p -> p.getName()).collect(Collectors.toList()));
			}
		}
		List<Text> text = tgt.stream().map(p -> new Text(" " + p)).collect(Collectors.toList());
		text.add(0, new Text(I18N.get(GameSystemI18NKeys.Xを誰に, a.getName())));
		messageWindowSystem.getTgtW().setText(text);
		messageWindowSystem.getTgtW().reset();
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.TGT_SELECT);
		stage.setStage(BattleSystem.Stage.TARGET_SELECT);
	}

	//アクション実行（コミット、ターゲットあり）
	void execAction(Action ba, ActionTarget tgt) {
		if (!ba.getName().equals(tgt.getAction().getName())) {
			ba = tgt.getAction();
		}
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("exec action ba=" + ba.getName() + " TGT:" + tgt);
		}
		//メッセージウインドウを初期化
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
		messageWindowSystem.getActionResultW().setText("");
		//ターゲットシステムが呼ばれているので、初期化
		targetSystem.unsetCurrent();
		//カレントユーザ
		BattleCharacter user = currentCmd.getUser();
		if (user.isPlayer()) {
			if (ba.getName().equals(BattleConfig.ActionName.commit)) {
				//移動終了・キャラクタの向きとターゲット座標のクリアをする
				if (GameSystem.isDebugMode()) {
					kinugasa.game.GameLog.print("commit move");
				}
				user.unsetTarget();
				user.to(FourDirection.WEST);
				stage.setStage(BattleSystem.Stage.WAITING_EXEC_CMD);
				return;
			}
		}
		//魔法詠唱開始の場合、詠唱中リストに追加して戻る。
		if (ba.getType() == ActionType.MAGIC) {
			//現状のステータスで対価を支払えるか確認
			//※実際に支払うのはexecしたとき。
			Map<StatusKey, Integer> damage = ba.selfBattleDirectDamage();
			//ダメージを合算
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//ダメージがあって、-の項目がある場合、対価を支払えないため空振り
			//この魔法の消費項目を取得
			if (!damage.isEmpty() && simulateDamage.hasMinus()) {
				//対象項目で1つでも0の項目があったら空振り
				List<String> shortageStatusDesc = simulateDamage.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList());
				setMsg(MessageType.SPELL_BUT_SHORTAGE, user.getStatus(), ba, null, shortageStatusDesc);
				if (user.isPlayer()) {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
				} else {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				}
				return;
			}
			//ターゲット存在確認、現状でいない場合、空振り。発動時の再チェックがここ。
			if (tgt.isEmpty() && !ba.battleEventIsOnly(TargetType.SELF)) {
				setMsg(MessageType.SPELL_BUT_NO_TARGET, user.getStatus(), ba, null, null);
				if (user.isPlayer()) {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
				} else {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				}
				return;
			}
			//詠唱開始サウンド再生
			if (BattleConfig.Sound.spellStart != null) {
				BattleConfig.Sound.spellStart.load().stopAndPlay();
			}

			if (ba.getSpellTime() > 0) {
				//詠唱時間がある場合は詠唱開始
				targetSystem.saveTarget(user, tgt);
				//詠唱アニメーション追加
				if (BattleConfig.castingAnimation != null) {
					Sprite s = BattleConfig.castingAnimation.clone();
					s.setLocationByCenter(user.getCenter());
					castingSprite.put(user, s);
				}
				addSpelling(user, ba);//MSG、STAGEもこの中で行う。
				return;
			}
			//詠唱時間0場合はターゲット選択へ
		}

		//ターゲット不在の場合、空振り（ミス）
		if (tgt.isEmpty()) {
			setMsg(MessageType.NO_TARGET, user.getStatus(), ba, null, null);
			if (user.isPlayer()) {
				if (messageWindowSystem.getCmdW().isVisible()) {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
					return;
				} else {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.AFTER_MOVE_CMD_SELECT);
					return;
				}
			} else {
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
			}
			return;
		}

		assert !tgt.isEmpty() : "target is empty(execAction)";
		//ターゲット存在のため、アクション実行
		tgt.getTarget().forEach(p -> p.getStatus().setDamageCalcPoint());
		ActionResult res = ba.exec(tgt);
		setMsg(MessageType.ACTION_SUCCESS, currentCmd.getUser().getStatus(), ba, res, actionResultProc(user, ba, tgt));
		//アイテム除去
		if (ba instanceof Item) {//NPCの場合だけ
			if (((Item) ba).hasBattlePT(ParameterType.ITEM_LOST)) {
				user.getStatus().getItemBag().drop((Item) ba);
			}
		}
		animation.addAll(res.getAnimation());
		currentBAWaitTime = new FrameTimeCounter(ba.getWaitTime());
		stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);

	}

	public void commitPCsMove() {
		//ターゲットシステムに値をセット
		//AFTER_MOVE選択に入る
		stage.setStage(Stage.AFTER_MOVE_CMD_SELECT);
		afterMove = true;
	}

	//PCの移動をキャンセルして、移動前の位置に戻す。確定は「commit」タイプのアクションから。
	public void cancelPCsMove() {
		//場所を初期化
		currentCmd.getUser().getSprite().setLocation(moveIinitialLocation);
		currentCmd.getUser().unsetTarget();
		//一番近い敵の方向を向く
		BattleCharacter e = BattleTargetSystem.nearEnemy(currentCmd.getUser());
		KVector v = new KVector();
		v.setAngle(currentCmd.getUser().getCenter(), e.getCenter());
		currentCmd.getUser().to(v.round());

		//CMD_SELECTに戻る
		stage.setStage(BattleSystem.Stage.CMD_SELECT);
	}

	private void addSpelling(BattleCharacter user, Action ba) {
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
		//ユーザに詠唱中の状態異常を付与
		currentCmd.getUser().getStatus().addCondition(BattleConfig.ConditionName.casting);
		//詠唱を開始したを表示
		setMsg(MessageType.SPELL_START, user.getStatus(), ba, null, null);
		stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
	}

	public void cancelItemDescShow() {
		stage.setStage(Stage.ITEM_CHOICE_USE);
	}

	public void nextItemChoiceUseWindowSelect() {
		if (!messageWindowSystem.getItemChoiceUseW().isVisible()) {
			throw new GameSystemException("item choice use, but window is not active");
		}
		messageWindowSystem.getItemChoiceUseW().nextSelect();
	}

	public void prevItemChoiceUseWindowSelect() {
		if (!messageWindowSystem.getItemChoiceUseW().isVisible()) {
			throw new GameSystemException("item choice use, but window is not active");
		}
		messageWindowSystem.getItemChoiceUseW().prevSelect();
	}

	public void cancelItemChoice() {
		stage.setStage(Stage.CMD_SELECT);
	}

	public void commitItemChoiceUse() {
		messageWindowSystem.getTgtW().setText(List.of());
		itemChoiceMode = -1;
		if (!messageWindowSystem.getItemChoiceUseW().isVisible()) {
			throw new GameSystemException("item choice use, but window is not active");
		}
		int selected = messageWindowSystem.itemChoiceUseCommit();
		int area = 0;
		Item i = (Item) messageWindowSystem.getCmdW().getSelectedCmd();
		switch (selected) {
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_CHECK:
				//チェックウインドウを出す
				messageWindowSystem.setItemDesc(currentCmd.getUser().getStatus(), i);
				stage.setStage(BattleSystem.Stage.SHOW_ITEM_DESC);
				break;
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_EQIP:
				//装備できない
				if (i.getEqipmentSlot() == null) {
					setMsg(MessageType.CANT_EQIP, null, null, null, List.of(i.getName()));
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
					return;
				}
				//装備できない（属性値）
				if (!currentCmd.getUser().getStatus().canEqip(i)) {
					setMsg(MessageType.CANT_EQIP, null, null, null, List.of(i.getName()));
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
					return;
				}
				//装備した・外した
				//バッグに分類されるアイテムかつアイテム数がもともと持てる数を上回る場合外せない
				if (ItemStorage.bagItems.containsKey(i.getName())
						&& currentCmd.getUser().getStatus().isEqip(i.getName())) {
					//もともとのサイズ
					int itemBagDefaultMax = currentCmd.getUser().getStatus().getRace().getItemBagSize();
					//現在のサイズ
					int currentSize = currentCmd.getUser().getStatus().getItemBag().size();
					//現在のサイズがもともともサイズより大きい場合は外せない
					if (currentSize > itemBagDefaultMax) {
						//外せない
						setMsg(MessageType.CANT_UNEQIP, null, null, null, List.of(i.getName()));
						stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
						return;
					}
				}
				assert i.getEqipmentSlot() != null : "item is not eqip";
				if (currentCmd.getUser().getStatus().isEqip(i.getName())) {
					currentCmd.getUser().getStatus().removeEqip(i);
				} else {
					currentCmd.getUser().getStatus().addEqip(i);
				}
				//アイテム所持数の再計算
				currentCmd.getUser().getStatus().updateItemBagSize();
				MessageType t = currentCmd.getUser().getStatus().isEqip(i.getName())
						? MessageType.EQIP_ITEM
						: MessageType.UNEQIP_ITEM;
				setMsg(t, null, null, null, List.of(i.getName()));
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				break;
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_USE:
				//バトルアクションが入っていない場合、使えないメッセージ表示
				if (!i.isBattleUse()) {
					setMsg(MessageType.CANT_USE_THIS_ITEM, null, null, null, List.of(i.getName()));
					stage.setStage(Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
					break;
				}
				//使う
				itemChoiceMode = BattleMessageWindowSystem.ITEM_CHOICE_USE_USE;
				area = (int) (currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
				setMsg(MessageType.ITEM_WHO_TO_USE, null, null, null, List.of(i.getName()));
				break;
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS:
				//渡す
				itemChoiceMode = BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS;
				area = (int) (currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
				setMsg(MessageType.ITEM_WHO_TO_PASS, null, null, null, List.of(i.getName()));
				break;
			default:
				throw new AssertionError("undefined item choice use No");
		}
		//誰に？
		if (itemChoiceMode >= 0) {
			ActionTarget t = BattleTargetSystem.instantTarget(currentCmd.getUser(), i);
			//ターゲット不在の場合
			if (itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS && t.getTarget().isEmpty()) {
				//パスする味方がいない
				setMsg(MessageType.NO_TARGET, null, null, null, List.of(i.getName()));
				stage.setStage(Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
				return;
			}
			List<String> tgt = t.getTarget().stream().map(p -> p.getName()).collect(Collectors.toList());
			//ターゲット選択へ
			itemPassAndUse = i;
			String msg = (itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS)
					? I18N.get(GameSystemI18NKeys.Xを誰に渡す, i.getName())
					: I18N.get(GameSystemI18NKeys.Xを誰に使う, i.getName());
			tgt.add(0, msg);
			messageWindowSystem.getTgtW().setText(tgt.stream().map(p -> new Text(p)).collect(Collectors.toList()));
			stage.setStage(BattleSystem.Stage.TARGET_SELECT);
		}
	}

	public BattleCommand getCurrentCmd() {
		return currentCmd;
	}

	//移動後攻撃可否（エリア）の設定を行う。引数で攻撃できるかを渡す。
	@LoopCall
	public void setMoveAction(boolean attackOK, int p) {
		if (prevAttackOK == attackOK) {
			return;
		}
		prevAttackOK = attackOK;
		if (!attackOK) {
			messageWindowSystem.getInfoW().setText(I18N.get(GameSystemI18NKeys.移動後攻撃不可));
			messageWindowSystem.getInfoW().allText();
			List<Action> list = new ArrayList<>();
			list.add(ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
			messageWindowSystem.getAfterMoveW().setActions(list);
			messageWindowSystem.setVisible(
					BattleMessageWindowSystem.StatusVisible.ON,
					BattleMessageWindowSystem.Mode.AFTER_MOVE,
					BattleMessageWindowSystem.InfoVisible.ON);
		} else {
			messageWindowSystem.getInfoW().setText(I18N.get(GameSystemI18NKeys.移動後攻撃可能));
			messageWindowSystem.getInfoW().allText();
			List<Action> actions = currentCmd.getBattleActionOf(ActionType.ATTACK);
			Collections.sort(actions);
			actions.add(0, ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
			messageWindowSystem.getAfterMoveW().setActions(actions);
			messageWindowSystem.setVisible(
					BattleMessageWindowSystem.StatusVisible.ON,
					BattleMessageWindowSystem.Mode.AFTER_MOVE,
					BattleMessageWindowSystem.InfoVisible.ON);
		}
	}

	public Sound getCurrentBGM() {
		return currentBGM;
	}

	public void nextTargetSelect() {
		targetSystem.next();
		messageWindowSystem.getTgtW().nextSelect();
	}

	public void prevTargetSelect() {
		targetSystem.prev();
		messageWindowSystem.getTgtW().prevSelect();
	}

	public void nextStatusWindowPage() {
		messageWindowSystem.statusDescWindowNextPage();
	}

	public void nextStatusWindowSelect() {
		messageWindowSystem.statusDescWindowNextSelect();
	}

	public void prevStatusWindowSelect() {
		messageWindowSystem.statusDescWindowPrevSelect();
	}

	public void nextStatusWindowChara() {
		messageWindowSystem.getStatusDescW().nextPc();
	}

	public void prevStatusWindowChara() {
		messageWindowSystem.getStatusDescW().prevPc();
	}

	public void cancelStatusDesc() {
		stage.setStage(Stage.CMD_SELECT);
	}

	public void commitTargetSelect() {
		if (itemChoiceMode != -1) {
			assert messageWindowSystem.getCmdW().getSelectedCmd().getType() == ActionType.ITEM : "item use commit, but action is not item";
			//パスor使う
			if (itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_USE) {
				if (GameSystem.isDebugMode()) {
					kinugasa.game.GameLog.print("use item : " + itemPassAndUse + " to " + messageWindowSystem.getTgtW().getSelected().getText());
				}
				//ターゲットに対してアクションを実行
				String tgtName = messageWindowSystem.getTgtW().getSelected().getText();
				//PC,NPCから名前検索
				List<BattleCharacter> all = new ArrayList();
				all.addAll(enemies);
				all.addAll(GameSystem.getInstance().getParty());
				BattleCharacter tgt = all.stream().filter(p -> p.getName().equals(tgtName)).collect(Collectors.toList()).get(0);
				tgt.getStatus().setDamageCalcPoint();
				ActionResult res = itemPassAndUse.exec(BattleTargetSystem.instantTarget(currentCmd.getUser(), itemPassAndUse).setTarget(List.of(tgt)));
				//ドロップアイテムイベントの実行
				if (itemPassAndUse.getBattleEvent().stream().filter(p -> p.getParameterType() == ParameterType.ITEM_LOST).count() > 0) {
					//TODO:暫定
					currentCmd.getUser().getStatus().getItemBag().drop(itemPassAndUse);
				}
				//アクション更新
				GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction(true));
				//効果を表示
				setMsg(MessageType.ITEM_USED, currentCmd.getUser().getStatus(), itemPassAndUse, res, List.of(tgt.getName()));
				itemPassAndUse = null;
				stage.setStage(Stage.EXECUTING_ACTION);
				itemChoiceMode = -1;
				return;
			}
			if (itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS) {
				if (GameSystem.isDebugMode()) {
					kinugasa.game.GameLog.print("pass item : " + itemPassAndUse + " to " + messageWindowSystem.getTgtW().getSelected().getText());
				}
				//ターゲットに対してパスを実行
				//PC,NPCから名前検索
				String tgtName = messageWindowSystem.getTgtW().getSelected().getText();
				List<BattleCharacter> all = new ArrayList();
				all.addAll(enemies);
				all.addAll(GameSystem.getInstance().getParty());
				BattleCharacter tgt = all.stream().filter(p -> p.getName().equals(tgtName)).collect(Collectors.toList()).get(0);
				BattleCharacter user = currentCmd.getUser();
				user.getStatus().passItem(tgt.getStatus(), itemPassAndUse);
				//アクション更新
				GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction(true));
				setMsg(MessageType.ITEM_PASSED, currentCmd.getUser().getStatus(), null, null, List.of(tgt.getName(), itemPassAndUse.getName()));
				stage.setStage(Stage.EXECUTING_ACTION);
				itemPassAndUse = null;
				itemChoiceMode = -1;
				return;
			}
			return;
		}
		//攻撃ターゲットセレクト確定(ONEのみ
		assert stage.getStage() == Stage.TARGET_SELECT : "target select not yet :" + stage.getStage();
		if (!afterMove) {
			assert messageWindowSystem.getCmdW().getSelectedCmd().getType() != ActionType.ITEM : "atk commit, but action is item:" + messageWindowSystem.getCmdW().getSelectedCmd();
			assert messageWindowSystem.getCmdW().getSelectedCmd().getType() != ActionType.OTHER : "atk commit, but action is other:" + messageWindowSystem.getCmdW().getSelectedCmd();
			execAction(messageWindowSystem.getCmdW().getSelectedCmd(), targetSystem.getSelected());
		} else {
			assert messageWindowSystem.getAfterMoveW().getSelectedCmd().getType() != ActionType.ITEM : "atk commit(A), but action is item:" + messageWindowSystem.getAfterMoveW().getSelectedCmd();
			//確定は入る
			execAction(messageWindowSystem.getAfterMoveW().getSelectedCmd(), targetSystem.getSelected());
		}
	}

	/**
	 * 戦況図モードの切替
	 */
	public void switchShowMode() {
		if (showMode) {
			messageWindowSystem.setVisibleFromSave();
		} else {
			messageWindowSystem.saveVisible();
			messageWindowSystem.setVisible(
					BattleMessageWindowSystem.StatusVisible.OFF,
					BattleMessageWindowSystem.Mode.NOTHING);

		}
		showMode = !showMode;
	}

	//ターゲット選択中のチーム切替
	public void switcTargetTeam() {
		//スイッチチーム可否を確認
		assert stage.getStage() == Stage.TARGET_SELECT : "stage is not target select";
		Action a = afterMove
				? messageWindowSystem.getAfterMoveW().getSelectedCmd()
				: messageWindowSystem.getCmdW().getSelectedCmd();
		if (a.getTargetOption().getSwitchTeam() == TargetOption.SwitchTeam.NG) {
			return;
		}
		//スイッチチーム実行
		TargetOption.DefaultTarget t = targetSystem.switchTeam();
		//TGTウインドウ更新
		List<String> tgt = new ArrayList<>();
		if (a.getTargetOption().getSelectType() == TargetOption.SelectType.IN_AREA) {
			if (a.getTargetOption().getIff() == TargetOption.IFF.ON) {
				if (t == TargetOption.DefaultTarget.ENEMY) {
					tgt.add(I18N.get(GameSystemI18NKeys.敵));
				} else {
					tgt.add(I18N.get(GameSystemI18NKeys.味方));
				}
			} else {
				if (t == TargetOption.DefaultTarget.ENEMY) {
					tgt.add(I18N.get(GameSystemI18NKeys.敵));
				} else {
					tgt.add(I18N.get(GameSystemI18NKeys.味方));
				}
			}
		} else {
			if (t == TargetOption.DefaultTarget.ENEMY) {
				tgt.addAll(targetSystem.getInAreaEnemy().stream().map(p -> p.getName()).collect(Collectors.toList()));
			} else {
				tgt.addAll(targetSystem.getInAreaTeam().stream().map(p -> p.getName()).collect(Collectors.toList()));
			}
		}
		List<Text> text = tgt.stream().map(p -> new Text(" " + p)).collect(Collectors.toList());
		text.add(0, new Text(I18N.get(GameSystemI18NKeys.Xを誰に, a.getName())));
		messageWindowSystem.getTgtW().setText(text);
		messageWindowSystem.getTgtW().reset();
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.TGT_SELECT);
	}

//	private void setMsg(MessageType t) {
//		//t == BATTLE_END
//		String s = t.get(null, null, null, null);
//		messageWindowSystem.getActionResultW().setText(s);
//		messageWindowSystem.getActionResultW().allText();
//	}
//
//	private void setMsg(MessageType t, List<String> option) {
//		String s = t.get(null, null, option, null);
//		messageWindowSystem.getActionResultW().setText(s);
//		messageWindowSystem.getActionResultW().allText();
//	}
//
//	private void setMsg(MessageType t, Action a, ActionResult res) {
//		String s = t.get(a, null, null, res);
//		messageWindowSystem.getActionResultW().setText(s);
//		messageWindowSystem.getActionResultW().allText();
//	}
	private void setMsg(MessageType t, Status user, Action a, ActionResult res, List<String> option) {
		String s = t.get(a, user, option, res);
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
	}

//	private void setMsg(MessageType t, Action a, Status user) {
//		String s = t.get(a, user, null, null);
//		messageWindowSystem.getActionResultW().setText(s);
//		messageWindowSystem.getActionResultW().allText();
//	}
//
//	private void setMsg(MessageType t, Action a, Status user, List<String> option) {
//		String s = t.get(a, user, option, null);
//		messageWindowSystem.getActionResultW().setText(s);
//		messageWindowSystem.getActionResultW().allText();
//	}
//
//	private void setMsg(MessageType t, Action a, Status user, ActionResult res) {
//		String s = t.get(a, user, null, res);
//		messageWindowSystem.getActionResultW().setText(s);
//		messageWindowSystem.getActionResultW().allText();
//	}
//
	public void update() {
		messageWindowSystem.update();
		targetSystem.update();
		enemies.forEach(v -> v.update());
		if (stage.getStage() == Stage.BATLE_END) {
			return;
		}

		//ターゲットシステムのカレント表示位置更新
//		if (targetSystem.getCurrentArea().isVisible()) {
//			targetSystem.getCurrentArea().setLocationByCenter(currentCmd.getSpriteCenter());
//		}
		//ステージ別処理
		GameSystem gs = GameSystem.getInstance();
		List<Status> party = GameSystem.getInstance().getPartyStatus();
		List<Status> enemy = enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList());
		switch (stage.getStage()) {
			case STARTUP:
				//スタートアップ時にupdateが呼ばれることはないためエラー
				throw new GameSystemException("update call before start");
			case INITIAL_MOVING:
				//プレイヤーキャラクターが目標の座標に近づくまで移動を実行、目的地に近づいたらステージWAITに変える
				gs.getParty().forEach(p -> p.move());
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
					messageWindowSystem.setVisible(
							BattleMessageWindowSystem.Mode.NOTHING);
					stage.setStage(Stage.WAITING_EXEC_CMD);
				}
				break;
			case ESCAPING:
				targetSystem.getCurrentArea().setArea(0);
				//プレイヤーキャラクターが目標の座標に近づくまで移動を実行、目的地に近づいたらステージWAITに変える
				currentCmd.getUser().moveToTgt();

				if (!currentCmd.getUser().isMoving()) {
					//スプライトとステータスを非表示にする
					int i = 0;
					for (; GameSystem.getInstance().getPartyStatus().equals(currentCmd.getUser().getStatus()); i++);
					currentCmd.getUser().getSprite().setVisible(false);
					messageWindowSystem.getStatusW().getMw().get(i).setVisible(false);

					//PC全員逃げ判定、全員逃げた場合、戦闘終了
					if (party.stream().filter(p -> !p.hasConditions(false, BattleConfig.getUntargetConditionNames())).allMatch(p -> p.hasCondition(BattleConfig.ConditionName.escaped))) {
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
						String text = "---" + I18N.get(GameSystemI18NKeys.戦闘結果) + "---" + Text.getLineSep()
								+ I18N.get(GameSystemI18NKeys.全員が逃げた) + Text.getLineSep();
						text += I18N.get(GameSystemI18NKeys.獲得経験値) + ":" + exp + Text.getLineSep();
						text += I18N.get(GameSystemI18NKeys.獲得物資) + ":" + I18N.get(GameSystemI18NKeys.なし) + Text.getLineSep();
						messageWindowSystem.getBattleResultW().setText(text);
						messageWindowSystem.getBattleResultW().allText();
						messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.BATTLE_RESULT);
						currentBGM.stop();
						currentBGM.dispose();
						winBGM.load().stopAndPlay();
						stage.setStage(Stage.BATLE_END);
						break;
					}
					//NPC全員逃げ判定
					if (enemies.stream().filter(p -> !p.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())).allMatch(p -> p.getStatus().hasCondition(BattleConfig.ConditionName.escaped))) {
						targetSystem.getCurrentArea().setArea(0);
						//アンターゲット状態異常付与（逃げる以外）の敵のEXPを合計して渡す
						int exp = 0;
						List<Item> dropItems = new ArrayList<>();
						for (Enemy e : enemies) {
							if (e.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames()
									.stream().filter(p -> !p.equals(BattleConfig.ConditionName.escaped)).collect(Collectors.toList()))) {
								exp += (int) e.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.exp).getValue();
								//ドロップアイテムの判定
								List<DropItem> items = e.getDropItem();
								for (DropItem ii : items) {
									//ドロップアイテムの確率判定
									if (Random.percent(ii.getP())) {
										dropItems.addAll(ii.cloneN());
									}
								}
							}
						}
						battleResultValue = new BattleResultValues(BattleResult.ESCAPE, exp, new ArrayList<>(), winLogicName);
						String text = "---" + I18N.get(GameSystemI18NKeys.戦闘結果) + "---" + Text.getLineSep()
								+ I18N.get(GameSystemI18NKeys.全員が逃げた) + Text.getLineSep();
						text += I18N.get(GameSystemI18NKeys.獲得経験値) + ":" + exp + Text.getLineSep();
						text += I18N.get(GameSystemI18NKeys.獲得物資) + ":" + Text.getLineSep();;
						for (Item ii : dropItems) {
							text += " " + ii.getName() + Text.getLineSep();
						}
						messageWindowSystem.getBattleResultW().setText(text);
						messageWindowSystem.getBattleResultW().allText();
						messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.BATTLE_RESULT);
						currentBGM.stop();
						currentBGM.dispose();
						winBGM.load().stopAndPlay();
						stage.setStage(Stage.BATLE_END);
						break;
					}
					stage.setStage(Stage.WAITING_EXEC_CMD);
					break;
				}
				break;
			case CMD_SELECT:
			case AFTER_MOVE_CMD_SELECT:
			case WAITING_EXEC_CMD:
			case PLAYER_MOVE:
			case TARGET_SELECT:
				//プレイヤーの行動まちなので、何もしない。
				//コマンドウインドウ等から処理を実行される
				//次にバトルコマンドを取得したとき、NPCならNPCの行動のステージに入る。
				break;
			case EXECUTING_ACTION:
				//カレントBATimeが切れるまで待つ
				assert currentBAWaitTime != null : "currentBAWaitTime is null";
				if (currentBAWaitTime.isReaching()) {
					currentBAWaitTime = null;
					if (stage.getNext() != null) {
						stage.next();
					} else {
						stage.setStage(Stage.WAITING_EXEC_CMD);
					}
				}
				break;
			case EXECUTING_MOVE:
				//NPCの移動実行、！！！！！！！！移動かんりょぅしたらメッセージウインドウ閉じる
				currentCmd.getUser().moveToTgt();
				remMovePoint--;
				//移動ポイントが切れた場合、移動終了してユーザコマンド待ちに移行
				if (remMovePoint <= 0 || !currentCmd.getUser().isMoving()) {
					currentCmd.getUser().unsetTarget();
					stage.setStage(Stage.WAITING_EXEC_CMD);
					break;
				}
				//移動ポイントが切れていない場合で、移動ポイントが半分以上残っている場合は攻撃可能
				//半分以下の場合は行動終了
				if (remMovePoint < currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2) {
					break;
				}
				//アクションを抽選・・・このステージに入るときは必ずENEMYなのでキャスト失敗しない
				Enemy user;
				Action eba = currentCmd.getBattleActionOf((user = (Enemy) currentCmd.getUser()).getAI(), ActionType.ATTACK);
				if (eba == null) {
					if (GameSystem.isDebugMode()) {
						kinugasa.game.GameLog.print(" enemy " + currentCmd.getUser().getStatus().getName() + " try afterMoveAttack, but dont have attackCMD");
					}
					break;
				}

				// イベント対象者別にターゲットを設定
				ActionTarget tgt = BattleTargetSystem.instantTarget(currentCmd.getUser(), eba);

				//ターゲットがいない場合、何もしない
				if (tgt.isEmpty()) {
					break;
				}

				//移動後攻撃実行
				ActionResult res = eba.exec(tgt);
				setMsg(MessageType.ACTION_SUCCESS, user.getStatus(), eba, res, actionResultProc(user, eba, tgt));
				animation.addAll(res.getAnimation());
				currentBAWaitTime = new FrameTimeCounter(eba.getWaitTime());
				//アクション実行中に入る
				stage.setStage(Stage.EXECUTING_ACTION);
				break;
			case ITEM_CHOICE_USE:
			case SHOW_ITEM_DESC:
			case SHOW_STATUS:
				//何もしない（専用メソッドから操作する
				break;
			case BATLE_END:
				//何もしない（ユーザ操作待ち
				break;
			default:
				throw new AssertionError("UNDEFINED STAGE");
		}
		//勝敗判定
		if (stage.getStage() == Stage.EXECUTING_ACTION) {
			return;
		}
		List<BattleWinLoseLogic> winLoseLogic = BattleConfig.getWinLoseLogic();
		if (winLoseLogic.isEmpty()) {
			throw new GameSystemException("win lose logic is empty, this battle never end.");
		}
		for (BattleWinLoseLogic l : winLoseLogic) {
			BattleResult result = l.isWinOrLose(party, enemy);
			if (result == BattleResult.NOT_YET) {
				continue;
			}
			//勝敗どちらかになった
			castingSprite.clear();
			if (result == BattleResult.LOSE) {
				currentBGM.stop();
				currentBGM.dispose();
				prevBGM.stop();
				prevBGM.dispose();
				if (GameSystem.isDebugMode()) {
					kinugasa.game.GameLog.print(" this battle is ended");
				}
				battleResultValue = new BattleResultValues(result, 0, new ArrayList<>(), loseLogicName);
				stage.setStage(Stage.BATLE_END);
				return;
			}
			targetSystem.getCurrentArea().setArea(0);
			//戦闘終了処理
			String nextLogicName = result == BattleResult.WIN ? winLogicName : loseLogicName;
			if (result == BattleResult.WIN) {
				currentBGM.stop();
				currentBGM.dispose();
				winBGM.load().stopAndPlay();
			}
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

			String text = "---" + I18N.get(GameSystemI18NKeys.戦闘結果) + "---" + Text.getLineSep()
					+ I18N.get(GameSystemI18NKeys.勝利した) + Text.getLineSep();
			text += I18N.get(GameSystemI18NKeys.獲得経験値) + ":" + exp + Text.getLineSep();
			text += I18N.get(GameSystemI18NKeys.獲得物資) + ":" + Text.getLineSep();

			for (Item i : dropItems) {
				text += " " + i.getName() + Text.getLineSep();
			}
			messageWindowSystem.getBattleResultW().setText(text);
			messageWindowSystem.getBattleResultW().allText();
			messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.BATTLE_RESULT);

			if (GameSystem.isDebugMode()) {
				kinugasa.game.GameLog.print(" this battle is ended");
			}
			stage.setStage(Stage.BATLE_END);
		}

		//効果の終わったアニメーションを取り除く
		//アニメーションはアクションの待機時間より長く表示することも可能なためstage外で実施
		List<AnimationSprite> removeList = new ArrayList<>();
		for (AnimationSprite a : animation) {
			if (a.getAnimation() == null) {//nullは基本入っていないのでもしあったら消す
				removeList.add(a);
				continue;
			}
			if (a.getAnimation().isEnded() || !a.isVisible() || !a.isExist()) {
				removeList.add(a);
			}
		}
		animation.removeAll(removeList);
	}

	//アクション成功時の処理
	private List<String> actionResultProc(BattleCharacter user, Action ba, ActionTarget tgt) {
		//HPが0になったときなどの状態異常を付与する
		conditionManager.setCondition(GameSystem.getInstance().getPartyStatus());
		conditionManager.setCondition(enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList()));
		//スプライトの非表示化処理とアンターゲットコンディション発生中のユーザによるコマンドを除去
		List<BattleCommand> removeList2 = new ArrayList<>();
		Map<String, String> deadEnemyName = new HashMap<>();//これを表示する
		for (BattleCommand cmd : commandsOfThisTurn) {
			for (String conditionName : BattleConfig.getUntargetConditionNames()) {
				ConditionKey k = ConditionStorage.getInstance().get(conditionName).getKey();
				//アンターゲット状態異常を持っているか検査
				if (cmd.getUser().getStatus().hasCondition(conditionName)) {
					removeList2.add(cmd);
					if (castingSprite.containsKey(cmd.getUser())) {
						castingSprite.remove(cmd.getUser());
					}
					cmd.getUser().getSprite().setVisible(false);
					deadEnemyName.put(cmd.getUser().getName(), I18N.get(GameSystemI18NKeys.XはX状態になった, cmd.getUser().getName(), k.getDesc()));
					if (cmd.getUser() instanceof Enemy) {
						if (((Enemy) cmd.getUser()).getDeadSound() != null) {
							((Enemy) cmd.getUser()).getDeadSound().load().stopAndPlay();
						}
					}
					break;
				}
			}
		}
		commandsOfThisTurn.removeAll(removeList2);
		//アンターゲット状態異常になったキャラのスプライトを非表示にする
		for (BattleCharacter c : GameSystem.getInstance().getParty()) {
			for (String cndKey : BattleConfig.getUntargetConditionNames()) {
				if (c.getStatus().hasCondition(cndKey)) {
					if (castingSprite.containsKey(c)) {
						castingSprite.remove(c);
					}
					c.getSprite().setVisible(false);
					deadEnemyName.put(c.getName(), I18N.get(GameSystemI18NKeys.XはX状態になった, c.getName(), ConditionStorage.getInstance().get(cndKey).getKey().getDesc()));
				}
			}
		}
		for (BattleCharacter c : enemies) {
			for (String cndKey : BattleConfig.getUntargetConditionNames()) {
				if (c.getStatus().hasCondition(cndKey)) {
					c.getSprite().setVisible(false);
					if (castingSprite.containsKey(c)) {
						castingSprite.remove(c);
					}
					deadEnemyName.put(c.getName(), I18N.get(GameSystemI18NKeys.XはX状態になった, c.getName(), ConditionStorage.getInstance().get(cndKey).getKey().getDesc()));
					if (((Enemy) c).getDeadSound() != null) {
						((Enemy) c).getDeadSound().load().stopAndPlay();
					}
				}
			}
		}
		List<String> text = new ArrayList<>();
		//1行目
		text
				.add(I18N.get(GameSystemI18NKeys.XのX, user.getName(), ba.getName()) + " !!");//改行不要

		//2行目以降
		class Tgt {

			String name;
			Map<StatusKey, Float> damage;

			Tgt(String name, Map<StatusKey, Float> damage) {
				this.name = name;
				this.damage = damage;
			}

		}
		List<Tgt> map = tgt.getTarget().stream().map(p -> new Tgt(p.getName(), p.getStatus().calcDamage())).collect(Collectors.toList());

		//ダメージが発生していない場合はミスを表示
		if (map.stream().flatMap(f -> f.damage.values().stream()).collect(Collectors.toList()).isEmpty()
				|| map.stream().flatMap(f -> f.damage.values().stream()).mapToDouble(p -> p).allMatch(p -> 0 == p)) {
			//ダメージなし
			text.add(I18N.get(GameSystemI18NKeys.しかし当たらなかった));
			return text;
		}

		if (map.stream().flatMap(f -> f.damage.entrySet().stream()).count() >= 7) {
			//平均モード
			for (String statusKey : BattleConfig.getVisibleStatus().stream().sorted().collect(Collectors.toList())) {
				float avg = 0;
				for (Tgt t : map) {
					if (t.damage.containsKey(StatusKeyStorage.getInstance().get(statusKey))) {
						avg += t.damage.get(StatusKeyStorage.getInstance().get(statusKey));
					}
				}
				avg /= map.size() == 0 ? 1 : map.size();
				String txt = I18N.get(GameSystemI18NKeys.平均)
						+ " " + Math.abs((int) avg) + " "
						+ StatusKeyStorage.getInstance().get(statusKey).getDesc();
				if (avg < 0) {
					txt += " " + I18N.get(GameSystemI18NKeys.回復した);
				} else {
					txt += " " + I18N.get(GameSystemI18NKeys.のダメージ);
				}
				txt += " !";
				text.add(txt);
			}
			for (String v : deadEnemyName.values()) {
				text.add(v);
			}
		} else {
			//全行表示モード
			for (String statusKey : BattleConfig.getVisibleStatus().stream().sorted().collect(Collectors.toList())) {
				for (Tgt t : map) {
					if (t.damage.containsKey(StatusKeyStorage.getInstance().get(statusKey))) {
						//visibleStatusを3つまで表示
						String txt = "";
						float v = t.damage.get(StatusKeyStorage.getInstance().get(statusKey));
						if (v < 0) {
							txt += I18N.get(GameSystemI18NKeys.XのXは, t.name, statusKey);
							txt += (Math.abs((int) v)) + "";
							txt += " " + I18N.get(GameSystemI18NKeys.回復した);
						} else {
							txt += I18N.get(GameSystemI18NKeys.XのXに, t.name, statusKey);
							txt += (Math.abs((int) v)) + "";
							txt += " " + I18N.get(GameSystemI18NKeys.のダメージ);
						}
						txt += " ! ";
						if (deadEnemyName.containsKey(t.name)) {
							txt += deadEnemyName.get(t.name);
						}
						text.add(txt);
					}
				}
			}
		}

		if (text.size() > 7) {
			text = text.subList(0, 8);
		}
		return text;
	}

	@Override
	public void draw(GraphicsContext g) {
		battleFieldSystem.draw(g);

		enemies.forEach(p -> p.draw(g));
		GameSystem.getInstance().getPartySprite().forEach(p -> p.draw(g));

		animation.forEach(v -> v.draw(g));

		castingSprite.values().forEach(p -> p.draw(g));

		targetSystem.draw(g);

		messageWindowSystem.draw(g);
	}

	//ターゲット選択モードをキャンセルして閉じる。アクション選択に戻る
	public void cancelTargetSelect() {
		targetSystem.unsetCurrent();
		stage.prev();
	}

	BattleMessageWindowSystem getMessageWindowSystem() {
		return messageWindowSystem;
	}

	BattleTargetSystem getTargetSystem() {
		return targetSystem;
	}

	public BattleFieldSystem getBattleFieldSystem() {
		return battleFieldSystem;
	}

	public void nextCmdSelect() {
		if (stage.getStage() == Stage.CMD_SELECT) {
			messageWindowSystem.getCmdW().nextAction();
			return;
		}
		if (stage.getStage() == Stage.AFTER_MOVE_CMD_SELECT) {
			messageWindowSystem.getAfterMoveW().nextAction();
			return;
		}
	}

	public void prevCmdSelect() {
		if (stage.getStage() == Stage.CMD_SELECT) {
			messageWindowSystem.getCmdW().prevAction();
			return;
		}
		if (stage.getStage() == Stage.AFTER_MOVE_CMD_SELECT) {
			messageWindowSystem.getAfterMoveW().prevAction();
			return;
		}
	}

	public void nextCmdType() {
		messageWindowSystem.getCmdW().nextType();
	}

	public void prevCmdType() {
		messageWindowSystem.getCmdW().prevType();
	}

	@LoopCall
	public boolean isBattleEnd() {
		return stage.getStage() == Stage.BATLE_END;
	}

	@LoopCall
	public boolean waitAction() {
		return stage.getStage() == Stage.EXECUTING_ACTION;
	}

	List<Enemy> getEnemies() {
		return enemies;
	}

}
