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
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.LoopCall;
import kinugasa.game.NoLoopCall;
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

	//�^�[����
	private int turn = 0;
	//--------------------------------------------------------�������E�I����
	//�v���C���퓬�J�n�O�ʒu
	private List<Point2D.Float> partyInitialLocation = new ArrayList<>();
	//�v���C���퓬�J�n�O����
	private List<FourDirection> partyInitialDir = new ArrayList<>();
	//�v���C�������ړ��ڕW���W
	private List<Point2D.Float> partyTargetLocationForFirstMove = new ArrayList<>();
	//�퓬�J�n�OBGM
	private Sound prevBGM;
	//�퓬BGM
	private Sound currentBGM;
	//����BGM
	private Sound winBGM;
	//�����J�ڃ��W�b�N���A�s�k�J�ڃ��W�b�N��
	private String winLogicName, loseLogicName;
	//--------------------------------------------------------�\�����E���s��
	//�G�̃X�v���C�g�ƃX�e�[�^�X
	private List<Enemy> enemies = new ArrayList<>();
	//���̃^�[���̃o�g���R�}���h����
	private LinkedList<BattleCommand> commandsOfThisTurn = new LinkedList<>();
	//���̃^�[���̃o�g���R�}���h����
	private LinkedHashMap<Integer, List<MagicSpell>> magics = new LinkedHashMap<>();
	//�\�����o�g���A�N�V�����E�A�j���[�V����
	private List<AnimationSprite> animation = new ArrayList<>();
	//���s���o�g���A�N�V�������琶�����ꂽ�A�N�V�����ҋ@����
	private FrameTimeCounter currentBAWaitTime;
	//�s�����R�}���h
	private BattleCommand currentCmd;
	//ActionMessage�\������
	private int messageWaitTime = 66;
	//�퓬����
	private BattleResultValues battleResultValue = null;
	//�J�����gBA��NPC�c�ړ��|�C���g
	private int remMovePoint;
	//�ړ��J�n���̈ʒu
	private Point2D.Float moveIinitialLocation;
	//���݂̃X�e�[�W
	private StageHolder stage;
	//-------------------------------------------------------------------�V�X�e��
	//���b�Z�[�W�E�C���h�E�V�X�e���̃C���X�^���X
	private BattleMessageWindowSystem messageWindowSystem;
	//�^�[�Q�b�g�I���V�X�e���̃C���X�^���X
	private BattleTargetSystem targetSystem;
	//�o�g���t�B�[���h�C���X�^���X
	private BattleFieldSystem battleFieldSystem;
	//��Ԉُ�}�l�[�W��
	private ConditionManager conditionManager;
	//�o�g���I���t���O�itrue=�I��
	private boolean end = false;
	//�틵�}���[�h���ǂ���
	private boolean showMode = false;
	//AfterMoveAction�X�V�p�̑O�񌟍����̍U����
	private boolean prevAttackOK = false;
	//-----------------------------------------------------------�A�C�e��
	//�A�C�e��ChoiceUse�C���f�b�N�X�F-1�F�^�[�Q�b�g�I�𖢎g�p
	private int itemChoiceMode = -1;
	//�A�C�e���g�p�ƃp�X�̃A�C�e���{��
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
				System.out.println(" changeStage : [" + this.stage + "] to [" + s + "] and[" + n + "]");
			}
			prev = this.stage;
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
				//�X�e�[�^�X�\��
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
		 * �J�n?�����ړ��܂�
		 */
		STARTUP,
		/**
		 * �����ړ���
		 */
		INITIAL_MOVING,
		/**
		 * ���[�U����҂�
		 */
		WAITING_EXEC_CMD,
		/**
		 * �R�}���h�I��
		 */
		CMD_SELECT,
		/**
		 * �����A�j���[�V�������s���B�I�������WAIT�ɓ���B
		 */
		ESCAPING,
		/**
		 * �v���C���[�L�����N�^�ړ����B�m��A�N�V�������Ă΂��܂ŉ������Ȃ��B
		 */
		PLAYER_MOVE,
		/**
		 * �^�[�Q�b�g�I�𒆁Bexec���Ă΂��܂ŉ������Ȃ��B
		 */
		TARGET_SELECT,
		/**
		 * �����������s���B�A�N�V�������b�Z�[�W���\�������̂ŁAOK�������Ǝ��ɐi�ށB
		 */
		EXECUTING_ACTION,
		/**
		 * �ړ���s���I��
		 */
		AFTER_MOVE_CMD_SELECT,
		/**
		 * �X�e�[�^�X�{����
		 */
		SHOW_STATUS,
		/**
		 * �A�C�e���p�r�I�����
		 */
		ITEM_CHOICE_USE,
		/**
		 * �A�C�e���ڍ׊m�F��
		 */
		SHOW_ITEM_DESC,
		/**
		 * �G�ړ����s���B�I�������WAIT�ɓ���B
		 */
		EXECUTING_MOVE,
		/**
		 * �o�g���͏I�����āA�Q�[���V�X�e������̏I���w����҂��Ă���B
		 */
		BATLE_END,
	}

	private enum MessageType {
		INITIAL_ENEMY_INFO {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		SPELL_BUT_NO_TARGET {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString() + "/" + user + "/" + option;
			}
		},
		SPELL_BUT_SHORTAGE {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString() + "/" + user + "/" + option;
			}
		},
		SPELL_SUCCESS {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString() + "/" + user + "/" + option;
			}
		},
		STOPING_BY_CONDITION {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString() + "/" + user + "/" + option;
			}
		},
		STOP_BECAUSE_CONFU {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		IS_MOVED {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString() + "/" + user + "/" + option;
			}
		},
		PC_USE_AVO {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		PC_USE_DEFENCE {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		PC_IS_MOVE {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		PC_IS_COMMIT_MOVE {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		PC_IS_ESCAPE {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		PC_IS_ESCAPE_MISS {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		NO_TARGET {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString() + "/" + user + "/" + option;
			}

		},
		EQIP_ITEM {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		UNEQIP_ITEM {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		CANT_EQIP {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		CANT_USE_THIS_ITEM {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		ITEM_WHO_TO_USE {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		ITEM_WHO_TO_PASS {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		ITEM_WHO_TO_THROW {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		ITEM_PASSED {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		ITEM_USED {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString();
			}
		},
		SPELL_START {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return toString() + "/" + user + "/" + option;
			}
		},
		ACTION_SUCCESS {
			@Override
			String get(CmdAction a, Status user, List<String> option, ActionResult res) {
				return String.join(Text.getLineSep(), option);
			}
		},;

		abstract String get(CmdAction a, Status user, List<String> option, ActionResult res);
	}

	//---------------------------------------UTIL--------------------------------------
	@Deprecated
	public void setBattleResultValue(BattleResultValues battleResultValue) {
		this.battleResultValue = battleResultValue;
	}

	/**
	 * ���݂̃X�e�[�W���擾���܂�
	 *
	 * @return �X�e�[�W
	 */
	public Stage getStage() {
		return stage.getStage();
	}

	/**
	 * ���݂̃^�[�������擾���܂��B
	 *
	 * @return �^�[�����A1�X�^�[�g�B
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

	//------------------------------------������---------------------------------------
	public void encountInit(EncountInfo enc) {
		if (GameSystem.isDebugMode()) {
			System.out.println(" -----------------BATTLE_START------------------------------------------------");
		}
		stage = new StageHolder();
		stage.setStage(BattleSystem.Stage.STARTUP);
		//�G���J�E���g���̎擾
		EnemySetStorage ess = enc.getEnemySetStorage().load();
		EnemySet es = ess.get();
		//�OBGM�̒�~
		prevBGM = enc.getPrevBGM();
		if (prevBGM == null) {
			//prev��null�ł��Đ�����BGM�����邩��������B����΂�����~����
			SoundStorage.getInstance().get("BGM").forEach(p -> p.stop());
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
		//�o�g��BGM�̍Đ�
		if (es.hasBgm()) {
			currentBGM = es.getBgm().load();
			currentBGM.stopAndPlay();
		}
		winBGM = enc.getEnemySetStorage().get().getWinBgm();
		//�G�擾
		enemies = es.create();
		ess.dispose();
		//������
		GameSystem gs = GameSystem.getInstance();
		battleFieldSystem = BattleFieldSystem.getInstance();
		battleFieldSystem.init(enc.getChipAttribute());
		targetSystem = BattleTargetSystem.getInstance();
		targetSystem.init();
		messageWindowSystem = BattleMessageWindowSystem.getInstance();
		messageWindowSystem.init();
		conditionManager = ConditionManager.getInstance();
		//�O�̂��߃p�[�e�B�[�̃A�N�V�������X�V
		GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction(true));
//		//�A�C�e���g�p���A�N�V�����ɒǉ�����
//		for (PlayerCharacter pc : gs.getParty()) {
//			pc.getStatus().getActions().addAll(pc.getStatus().getItemBag().getItems());
//		}

		//�o��MSG�ݒ�p�}�b�v
		Map<String, Long> enemyNum = enemies.stream().collect(Collectors.groupingBy(Enemy::getId, Collectors.counting()));
		//�o��MSG�ݒ�
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Long> e : enemyNum.entrySet()) {
			sb.append(e.getKey()).append(I18N.translate("WAS")).append(e.getValue()).append(I18N.translate("APPEARANCE")).append(Text.getLineSep());
		}
		//�G�o�������Z�b�g
		setMsg(MessageType.INITIAL_ENEMY_INFO, List.of(sb.toString().split(Text.getLineSep())));
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);

		//���Z�b�g
		currentCmd = null;
		currentBAWaitTime = null;
		commandsOfThisTurn.clear();
		turn = 0;
		animation.clear();
		targetSystem.unsetCurrent();

		//�G�̔z�u
		putEnemy();

		//�����̔z�u
		putParty();
		assert partyTargetLocationForFirstMove.size() == gs.getParty().size() : "initial move target is missmatch";

		//�����ړ����s��
		stage.setStage(BattleSystem.Stage.INITIAL_MOVING);
	}

	private void putParty() {
		GameSystem gs = GameSystem.getInstance();
		partyInitialDir.clear();
		partyInitialLocation.clear();
		partyTargetLocationForFirstMove.clear();

		//�퓬�J�n�O�ʒu�E�����ޔ�
		List<PlayerCharacterSprite> partySprite = gs.getPartySprite();
		List<Status> partyStatus = gs.getPartyStatus();
		for (BasicSprite s : partySprite) {
			partyInitialDir.add(s.getVector().round());
			partyInitialLocation.add(s.getLocation());
		}

		int size = partySprite.get(0).getImageHeight();
		//�z�u
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

	//------------------------------------------PP����M------------------------------------
	void turnStart() {
		turn++;
		if (GameSystem.isDebugMode()) {
			System.out.println(" -----------------TURN[" + turn + "] START-----------------");
		}
		//���̃^�[���̃o�g���R�}���h���쐬
		List<BattleCharacter> list = getAllChara();
		if (SpeedCalcModelStorage.getInstance().getCurrent() == null) {
			throw new GameSystemException("speed calc model is null");
		}
		SpeedCalcModelStorage.getInstance().getCurrent().sort(list);

		//�s�����Ƀo�g���R�}���h���i�[
		assert commandsOfThisTurn.isEmpty() : "turnStart:cmd is not empty";
		for (BattleCharacter c : list) {
			BattleCommand.Mode mode = c.isPlayer() ? BattleCommand.Mode.PC : BattleCommand.Mode.CPU;
			commandsOfThisTurn.add(new BattleCommand(mode, c));
		}

		//���̃^�[���̖��@�r�������C�x���g�������_���Ȉʒu�Ɋ��荞�܂���
		List<MagicSpell> ms = magics.get(turn);
		if (ms != null) {
			if (!ms.isEmpty()) {
				//commandsOfThisTurn
				for (MagicSpell s : ms) {
					//���@���s�C�x���g�������_���Ȉʒu�Ɋ��荞�܂���
					BattleCommand bc = new BattleCommand(s.isPlayer()
							? BattleCommand.Mode.PC
							: BattleCommand.Mode.CPU,
							s.getUser())
							.setAction(Arrays.asList(s.getAction()))
							.setMagicSpell(true);
					int idx = Random.randomAbsInt(commandsOfThisTurn.size());
					//���荞�܂��郆�[�U�̒ʏ�A�N�V������j������
					BattleCommand remove = null;
					for (BattleCommand c : commandsOfThisTurn) {
						if (c.getUser().equals(bc.getUser())) {
							remove = c;
						}
					}
					//���[�U���A���^�[�Q�b�g��Ԃ̏ꍇ�A�R�}���h�͔j��
					if (remove != null) {
						if (!remove.getUser().getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
							commandsOfThisTurn.remove(remove);
						}
						//�폜���Ă��犄�荞�ݎ��s
						commandsOfThisTurn.add(idx, bc);
					}
				}
				//�r�������X�g���炱�̃^�[���̃C�x���g���폜
				magics.remove(turn);
			}
		}
		//PC�ENPC�̏�Ԉُ�̌o�߃^�[���X�V�E�p���_���[�W����
		updateCondition();

		//���̃^�[���s���ۂ��R�}���h�ɐݒ�
		for (BattleCommand cmd : commandsOfThisTurn) {
			if (cmd.getUser().getStatus().isConfu()) {
				//����
				cmd.setConfu(true);
			}
			if (!cmd.getUser().getStatus().canMoveThisTurn()) {
				//���̑��s���s�\�̏�Ԉُ�
				cmd.setStop(true);
			}
		}

		//���[�U���ɑ΂���1�̃R�}���h�����邱�Ƃ��m�F
		Set<String> set = new HashSet<>();
		for (BattleCommand cmd : commandsOfThisTurn) {
			String name = cmd.getUser().getName();
			if (set.contains(name)) {
				throw new GameSystemException("duplicate command user:" + name);
			}
			set.add(name);
		}

		stage.setStage(BattleSystem.Stage.WAITING_EXEC_CMD);
	}

	@NoLoopCall
	private void updateCondition() {
		//HP��0�ɂȂ����Ƃ��Ȃǂ̏�Ԉُ��t�^����
		conditionManager.setCondition(GameSystem.getInstance().getPartyStatus());
		conditionManager.setCondition(enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList()));
		//�X�v���C�g�̔�\��������
		//�A���^�[�Q�b�g�R���f�B�V�����������̃��[�U�ɂ��R�}���h������
		List<BattleCommand> remove = new ArrayList<>();
		for (BattleCommand cmd : commandsOfThisTurn) {
			if (cmd.getUser().getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
				remove.add(cmd);
				cmd.getUser().getSprite().setVisible(false);
			}
		}
		commandsOfThisTurn.removeAll(remove);

		//��Ԉُ�̌��ʎ��Ԃ�����
		enemies.stream().map(p -> p.getStatus()).forEach(p -> p.update());
		GameSystem.getInstance().getPartyStatus().forEach(p -> p.update());

	}
	//
	//--------------------------------END_BATTLE------------------------------------------
	//

	void endBattle() {
		//�����̔z�u�̏�����
		List<PlayerCharacterSprite> partySprite = GameSystem.getInstance().getPartySprite();
		for (int i = 0; i < partySprite.size(); i++) {
			//�ʒu�̕���
			partySprite.get(i).to(partyInitialDir.get(i));
			partySprite.get(i).setLocation(partyInitialLocation.get(i));
		}
		//�G�ԍ��̏�����
		EnemyBlueprint.initEnemyNoMap();
		//�������R���f�B�V�����Ŕ�\���ɂȂ��Ă���ꍇ�\������
		//�A�C�e���A�N�V�������폜����
		for (PlayerCharacter pc : GameSystem.getInstance().getParty()) {
			if (pc.getStatus().hasCondition(BattleConfig.ConditionName.escaped)) {
				//�������l�̃X�v���C�g��\���ɖ߂�
				pc.getSprite().setVisible(true);
				//�������R���f�B�V�������O��
				pc.getStatus().removeCondition(BattleConfig.ConditionName.escaped);
			}
			//�A�C�e���A�N�V�����̍폜
			List<CmdAction> removeList = pc.getStatus().getActions().stream().filter(p -> p.getType() == ActionType.ITEM).collect(Collectors.toList());
			pc.getStatus().getActions().removeAll(removeList);
			//�r�����R���f�B�V�������O��
			pc.getStatus().removeCondition(BattleConfig.ConditionName.casting);
			//�h��E����R���f�B�V�������O��
			pc.getStatus().removeCondition(BattleConfig.ConditionName.defence);
			pc.getStatus().removeCondition(BattleConfig.ConditionName.avoidance);

		}
		//BGM�̏���
//		if (currentBGM != null) {
//			currentBGM.stop();
//			currentBGM.dispose();
//		}
		winBGM.stop();//�������s
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

	//���̃R�}���h���擾�BNPC�܂���PC�BNPC�̏ꍇ�͎������s�B���@�r���C�x���g���������s�B
	//���̃��\�b�h���N�����Ď��̃A�N�V�������擾����B
	//�擾�����A�N�V������PC�Ȃ�R�}���h�E�C���h�E�������ŊJ����Ă���̂ŁA�I������B
	//�I����AexecPCAction�����s����B
	public BattleCommand execCmd() {
		//���ׂẴR�}���h�����s�����玟�̃^�[�����J�n
		if (commandsOfThisTurn.isEmpty()) {
			turnStart();
		}
		currentCmd = commandsOfThisTurn.getFirst();
		assert currentCmd != null : "currentCMD is null";
		commandsOfThisTurn.removeFirst();
		BattleCharacter user = currentCmd.getUser();

		if (GameSystem.isDebugMode()) {
			System.out.println(" currentCMD:" + currentCmd);
		}

		//�^�[�Q�b�g�V�X�e��������
		targetSystem.unsetCurrent();
		currentBAWaitTime = null;
		//�A���^�[�Q�b�g��Ԉُ�̏ꍇ�A���b�Z�[�W�o�����Ɏ��ɑ���
		if (user.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
			if (GameSystem.isDebugMode()) {
				System.out.println(user.getStatus().getName() + " is bad condition");
			}
			stage.setStage(BattleSystem.Stage.WAITING_EXEC_CMD);
			return execCmd();
		}
		//�h��܂��͉�𒆂̏ꍇ�A1�^�[�������L���Ȃ��߁A���񂻂̃t���O���O��
		if (user.getStatus().hasCondition(BattleConfig.ConditionName.defence)) {
			user.getStatus().removeCondition(BattleConfig.ConditionName.defence);
		}
		if (user.getStatus().hasCondition(BattleConfig.ConditionName.avoidance)) {
			user.getStatus().removeCondition(BattleConfig.ConditionName.avoidance);
		}

		//��Ԉُ�œ����Ȃ��Ƃ��X�L�b�v�i���b�Z�[�W�͏o��
		//�r�����̏ꍇ�͂̂���
		if (currentCmd.isStop()) {
			if (currentCmd.getUser().getStatus().getCondition().stream().filter(p -> p.getName().equals(BattleConfig.ConditionName.casting)).count() != 1) {
				setMsg(MessageType.STOPING_BY_CONDITION, null, user.getStatus(), List.of(user.getStatus().moveStopDesc().getKey().getDesc()));
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				return currentCmd;
			}
		}

		//�����œ����Ȃ��Ƃ��́A��~�܂��̓o�g���A�N�V������K���Ɏ擾���Ď������s����
		if (currentCmd.isConfu()) {
			if (Random.percent(BattleConfig.confuStopP)) {
				//�����Ȃ�
				CmdAction ba = currentCmd.getFirstBattleAction();
				setMsg(MessageType.STOP_BECAUSE_CONFU, ba, user.getStatus());
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				return currentCmd;
			} else {
				//�����邪����
				CmdAction ba = currentCmd.randomAction();
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				execAction(ba);
				return currentCmd;
			}
		}

		//���@�r�������C�x���g�̏ꍇ�APC�ł�NPC�ł��������s�A�i�r�����R���f�B�V�������O��
		//���@�̃R�X�g�ƃ^�[�Q�b�g�́A�r���J�n�ƏI����2�񔻒肷��B
		//�����́u�r���I�����v�̏����B
		if (currentCmd.isMagicSpell()) {
			CmdAction ba = currentCmd.getFirstBattleAction();//1���������Ă��Ȃ�
			//�r������Ԉُ���O��
			currentCmd.getUser().getStatus().removeCondition(BattleConfig.ConditionName.casting);
			//����ł̃^�[�Q�b�g���擾
			ActionTarget target = BattleTargetSystem.instantTarget(currentCmd.getUser(), ba).setSelfTarget(true);
			//�^�[�Q�b�g�����Ȃ��ꍇ�A�r�����s�̃��b�Z�[�W�o��
			if (target.isEmpty()) {
				setMsg(MessageType.SPELL_BUT_NO_TARGET, ba, user.getStatus());
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				return currentCmd;
			}
			//����̃X�e�[�^�X�őΉ����x�����邩�m�F
			//�����ۂɎx�����̂�exec�����Ƃ��B
			Map<StatusKey, Integer> damage = ba.selfBattleDirectDamage();
			//�_���[�W�����Z
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//�_���[�W�������āA�}�C�i�X�̍��ڂ�����ꍇ�A�Ή����x�����Ȃ����ߋ�U��
			//���̖��@�̏���ڂ��擾
			if (!damage.isEmpty() && simulateDamage.hasMinus()) {
				//������MP������Ȃ�
				List<String> shortageStatusDesc = simulateDamage.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList());
				setMsg(MessageType.SPELL_BUT_SHORTAGE, ba, user.getStatus(), shortageStatusDesc);
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				return currentCmd;
			}
			//�r�������A���@���ʔ���
			target.forEach(p -> p.getStatus().setDamageCalcPoint());
			ActionResult res = ba.exec(target);
			setMsg(MessageType.SPELL_SUCCESS, ba, res);
			animation.addAll(res.getAnimation());
			currentBAWaitTime = res.getWaitTime().clone();
			stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
			return currentCmd;
		}//���@�����܂�

		//NPC
		if (currentCmd.getMode() == BattleCommand.Mode.CPU) {
			//NPC�A�N�V�������������s�A���̒��ŃX�e�[�W���ς�邵MSG���ݒ肳���
			execAction(currentCmd.getBattleActionEx(((Enemy) currentCmd.getUser()).getAI(), ActionType.OTHER, ActionType.ITEM));
			return currentCmd;
		}

		//PC�̃A�N�V�������s
		//�J�����g�R�}���h���e���R�}���h�E�C���h�E�ɕ\���A���̑��E�C���h�E�͕���
		messageWindowSystem.getCmdW().setCmd(currentCmd);
		messageWindowSystem.getCmdW().resetSelect();
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.CMD_SELECT);

		//�^�[�Q�b�g�V�X�e�����E�C���h�E�̏����I���ŏ�����
		assert messageWindowSystem.getCmdW().getSelectedCmd() != null : "cmdW initial select action is null";
		targetSystem.setCurrent(currentCmd.getUser(), messageWindowSystem.getCmdW().getSelectedCmd());

		//PC�̑���Ȃ̂ŁA�J�����g�R�}���h�̃��[�U�I�y���[�V�����v�ۃt���O��ON�ɐݒ�
		currentCmd.setUserOperation(true);

		stage.setStage(BattleSystem.Stage.CMD_SELECT);
		return currentCmd;
	}

	@NoLoopCall
	public void commitCmd() {
		//�ړ���U�����ʏ�U�����𔻒�
		boolean afterMove = messageWindowSystem.getAfterMoveW().isVisible();
		if (!afterMove) {
			if (messageWindowSystem.getCmdW().getSelectedCmd() == null) {
				return;//�g���閂�@�^�A�C�e�����Ȃ�
			}
		} else {
			targetSystem.getCurrentArea().setVisible(false);
		}
		//�R�}���h�E�C���h�E�܂��͈ړ���U���E�C���h�E����A�N�V�������擾
		execAction(afterMove
				? messageWindowSystem.getAfterMoveW().getSelectedCmd()
				: messageWindowSystem.getCmdW().getSelectedCmd());
	}

	//�A�N�V�������s�i�R�~�b�g�A�����j
	void execAction(CmdAction a) {
		//PC,NPC��킸�I�����ꂽ�A�N�V���������s����B

		//�E�C���h�E��ԏ������E�E�E�A�N�V�������s�O
		messageWindowSystem.getActionResultW().setText("");
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);

		//�J�����g���[�U
		BattleCharacter user = currentCmd.getUser();

		//�������̏ꍇ
		if (user.getStatus().isConfu()) {
			//�^�[�Q�b�g�V�X�e���̃J�����g�N�����Ȃ��őΏۂ��擾����
			ActionTarget tgt = BattleTargetSystem.instantTarget(user, a);
			execAction(a, tgt);
			return;
		}

		//NPC�̏ꍇ
		if (!user.isPlayer()) {
			//�A�N�V�����̌��ʔ͈͂ɑ��肪���邩�A�C���X�^���g�m�F
			ActionTarget tgt = BattleTargetSystem.instantTarget(user, a);
			if (tgt.isEmpty()) {
				//�^�[�Q�b�g�����Ȃ��ꍇ�ŁA�ړ��A�N�V�����������Ă���ꍇ�͈ړ��J�n
				if (user.getStatus().hasAction(BattleConfig.ActionName.move)) {
					//�ړ��^�[�Q�b�g�͍ł��߂�PC�Ƃ���
					Point2D.Float tgtLocation = ((Enemy) user).getAI().targetLocation(user);
					user.setTargetLocation(tgtLocation, a.getAreaWithEqip(user));
					//�ړ�������������
					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
					//�ړ������I�̃��b�Z�[�W�\��
					setMsg(MessageType.IS_MOVED, a, user.getStatus());
					messageWindowSystem.setVisible(
							BattleMessageWindowSystem.Mode.ACTION);
					stage.setStage(BattleSystem.Stage.EXECUTING_MOVE);
					return;
				} else {
					//�ړ��ł��Ȃ��̂ŉ������Ȃ�
					return;
				}
			} else {
				//�^�[�Q�b�g������ꍇ�͑������s
				execAction(a, tgt);
				return;
			}
		}

		//PC�̏���
		assert user.isPlayer() : "PC action, but action is not PC : " + user + " \r\n " + currentCmd;
		assert user.getStatus().getActions().contains(a) : "user not have action";

		//PC�̓���R�}���h�̏���
		if (a.getType() == ActionType.OTHER) {
			if (a.getName().equals(BattleConfig.ActionName.avoidance)) {
				//����E�����Ԃ�t�^����
				user.getStatus().addCondition(BattleConfig.ConditionName.avoidance);
				setMsg(MessageType.PC_USE_AVO, a, user.getStatus());
				stage.setStage(Stage.EXECUTING_ACTION);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.defence)) {
				//�h��E�h���Ԃ�t�^����
				user.getStatus().addCondition(BattleConfig.ConditionName.defence);
				setMsg(MessageType.PC_USE_DEFENCE, a, user.getStatus());
				stage.setStage(Stage.EXECUTING_ACTION);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.move)) {
				//�ړ��J�n�E�����ʒu���i�[
				moveIinitialLocation = user.getSprite().getLocation();
				messageWindowSystem.setVisible(
						BattleMessageWindowSystem.StatusVisible.ON,
						BattleMessageWindowSystem.Mode.AFTER_MOVE,
						BattleMessageWindowSystem.InfoVisible.ON);
				List<CmdAction> action = user.getStatus().getActions(ActionType.ATTACK);
				Collections.sort(action);
				action.add(0, ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
				messageWindowSystem.getAfterMoveW().setActions(action);
				//�^�[�Q�b�g�V�X�e���̃G���A�\����L�����F�l��MOV
				targetSystem.setCurrent(user, a);
				stage.setStage(Stage.PLAYER_MOVE);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.commit)) {
				//�ړ��I���E�L�����N�^�̌����ƃ^�[�Q�b�g���W�̃N���A������
				user.unsetTarget();
				user.to(FourDirection.WEST);
				stage.setStage(BattleSystem.Stage.WAITING_EXEC_CMD);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.status)) {
				//�X�e�[�^�X�\��
				stage.setStage(Stage.SHOW_STATUS, Stage.CMD_SELECT);
				return;
			}
			if (a.getName().equals(BattleConfig.ActionName.escape)) {
				//������E�������邩����
				//�O��Ƃ��āA�ړ��|�C���g���Ƀo�g���G���A�̋��E�i���E�j���Ȃ���΂Ȃ�Ȃ�
				Point2D.Float w, e;
				int movPoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
				e = (Point2D.Float) user.getSprite().getCenter().clone();
				e.x += movPoint;
				w = (Point2D.Float) user.getSprite().getCenter().clone();
				w.x -= movPoint;
				if (!battleFieldSystem.getBattleFieldAllArea().contains(e)) {
					//���������i���j
					user.getStatus().addCondition(ConditionValueStorage.getInstance().get(BattleConfig.ConditionName.escaped).getKey());
					user.setTargetLocation(e, 0);
					user.to(FourDirection.EAST);
					setMsg(MessageType.PC_IS_ESCAPE, a, user.getStatus());
					stage.setStage(BattleSystem.Stage.ESCAPING);
					return;
				}
				if (!battleFieldSystem.getBattleFieldAllArea().contains(w)) {
					//���������i���j
					user.getStatus().addCondition(ConditionValueStorage.getInstance().get(BattleConfig.ConditionName.escaped).getKey());
					user.setTargetLocation(w, 0);
					user.to(FourDirection.WEST);
					setMsg(MessageType.PC_IS_ESCAPE, a, user.getStatus());
					stage.setStage(BattleSystem.Stage.ESCAPING);
					return;
				}
//TODO:NPC�̓����͂����łȂ��B
//				//NPC�̏ꍇ�A������̐��ɓ���
//				if (!user.isPlayer()) {
//					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
//					user.setTargetLocation(w, 1);
//					setStage(BattleSystem.Stage.EXECUTING_MOVE, "execAction");
//					return OperationResult.SUCCESS;
//				}
				//�������Ȃ�
				setMsg(MessageType.PC_IS_ESCAPE_MISS, a, user.getStatus());
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
				return;
			}
		}

		//�A�C�e���I�����̏���
		if (a.getType() == ActionType.ITEM) {
			//�A�C�e���g�p�@���A�C�e��extends�A�N�V����
			//�A�C�e��ChoiceUse���J�������B
			messageWindowSystem.openItemChoiceUse();
			stage.setStage(BattleSystem.Stage.ITEM_CHOICE_USE);
			return;
		}

		assert a.getType() == ActionType.MAGIC || a.getType() == ActionType.ATTACK : "actions are processed in the wrong order.";
		targetSystem.setCurrent(user, a);
		//�^�[�Q�b�g�s��
		if (targetSystem.getInAreaDirect().isEmpty()) {//���@�Ō���^�[�Q�b�g�����Ȃ��ꍇ�������ŋz�������
			setMsg(MessageType.NO_TARGET, a, user.getStatus());
			//�ړ���U������J�ڂ��Ă����ꍇ�͋�U�肳����
			if (stage.getStage() == Stage.CMD_SELECT) {
				if (GameSystem.isDebugMode()) {
					System.out.println("no target(cmd)");
				}
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
			} else {
				if (GameSystem.isDebugMode()) {
					System.out.println("no target(after)");
				}
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.AFTER_MOVE_CMD_SELECT);
			}
			return;
		}
		//�����_��1�̏ꍇ�A�^�[�Q�b�g�I���ł��Ȃ��iINAREA����K���ɏE���Ďg��
		if (a.hasBattleTT(TargetType.RANDOM_ONE)
				|| a.hasBattleTT(TargetType.RANDOM_ONE_ENEMY)
				|| a.hasBattleTT(TargetType.RANDOM_ONE_PARTY)) {
			//�����_��1��
			ActionTarget tgt = targetSystem.getSelected();
			execAction(a, tgt);
			return;
		}

		//ALL�̏ꍇ�A�^�[�Q�b�g�I��s�v
		if (a.hasBattleTT(TargetType.ALL)) {
			ActionTarget tgt = targetSystem.getSelectedInArea();
			execAction(a, tgt);
			return;
		}

		//FIELD�̏ꍇ�A�^�[�Q�b�g�I��s�v
		if (a.hasBattleTT(TargetType.FIELD)) {
			ActionTarget tgt = targetSystem.getSelected();
			execAction(a, tgt);
			return;
		}

		//�p�[�e�B�[�̏ꍇ�^�[�Q�b�g�I��s�v(INAREA���ׂāj
		if (a.hasBattleTT(TargetType.TEAM_ENEMY) || a.hasBattleTT(TargetType.TEAM_PARTY)) {
			//INAREA�g�p
			ActionTarget tgt = targetSystem.getSelectedInArea();
			execAction(a, tgt);
			return;
		}

		//SELF�݂̂̏ꍇ�A�^�[�Q�b�g�I��s�v
		if (a.battleEventIsOnly(TargetType.SELF)) {
			ActionTarget tgt = targetSystem.getSelected();
			execAction(a, tgt);
			return;
		}
		//���@�r���J�n�̏ꍇ�A�r�������X�g�ɒǉ����Ė߂�B
		if (a.getType() == ActionType.MAGIC) {
			//����̃X�e�[�^�X�őΉ����x�����邩�m�F
			//�����ۂɎx�����̂�exec�����Ƃ��B
			Map<StatusKey, Integer> damage = a.selfBattleDirectDamage();
			//�_���[�W�����Z
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//�_���[�W�������āA-�̍��ڂ�����ꍇ�A�Ή����x�����Ȃ����ߋ�U��
			//���̖��@�̏���ڂ��擾
			if (!damage.isEmpty() && simulateDamage.hasMinus()) {
				//�Ώۍ��ڂ�1�ł�0�̍��ڂ����������U��
				List<String> shortageStatusDesc = simulateDamage.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList());
				setMsg(MessageType.SPELL_BUT_SHORTAGE, a, user.getStatus(), shortageStatusDesc);
				if (user.isPlayer()) {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
				} else {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				}
				return;
			}
			//�^�[�Q�b�g���݊m�F�A����ł��Ȃ��ꍇ�A��U��B�������̍ă`�F�b�N�������B
			if (targetSystem.isEmpty() && !a.battleEventIsOnly(TargetType.SELF)) {
				setMsg(MessageType.SPELL_BUT_NO_TARGET, a, user.getStatus());
				if (user.isPlayer()) {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
				} else {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				}
				return;
			}

			if (a.getSpellTime() > 0) {
				//�r�����Ԃ�����ꍇ�͉r���J�n
				addSpelling(user, a);//MSG�ASTAGE�����̒��ōs���B
				return;
			}
		}

		//���̑��iONE�j�̏ꍇ�̓^�[�Q�b�g�I��K�v
		List<String> tgt = targetSystem.getInAreaDirect().stream().map(p -> p.getName()).collect(Collectors.toList());
		List<Text> text = tgt.stream().map(p -> new Text(" " + p)).collect(Collectors.toList());
		text.add(0, new Text(a.getName() + I18N.translate("WHO_TO")));
		messageWindowSystem.getTgtW().setText(text);
		messageWindowSystem.getTgtW().reset();
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.TGT_SELECT);
		stage.setStage(BattleSystem.Stage.TARGET_SELECT);
	}

	//�A�N�V�������s�i�R�~�b�g�A�^�[�Q�b�g����j
	void execAction(CmdAction ba, ActionTarget tgt) {
		if (!ba.getName().equals(tgt.getAction().getName())) {
			ba = tgt.getAction();
		}
		if (GameSystem.isDebugMode()) {
			System.out.println("exec action ba=" + ba.getName() + " TGT:" + tgt);
		}
		//���b�Z�[�W�E�C���h�E��������
		messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.ACTION);
		messageWindowSystem.getActionResultW().setText("");
		//�^�[�Q�b�g�V�X�e�����Ă΂�Ă���̂ŁA������
		targetSystem.unsetCurrent();
		//�J�����g���[�U
		BattleCharacter user = currentCmd.getUser();
		if (user.isPlayer()) {
			if (ba.getName().equals(BattleConfig.ActionName.commit)) {
				//�ړ��I���E�L�����N�^�̌����ƃ^�[�Q�b�g���W�̃N���A������
				if (GameSystem.isDebugMode()) {
					System.out.println("commit move");
				}
				user.unsetTarget();
				user.to(FourDirection.WEST);
				stage.setStage(BattleSystem.Stage.WAITING_EXEC_CMD);
				return;
			}
		}
		//���@�r���J�n�̏ꍇ�A�r�������X�g�ɒǉ����Ė߂�B
		if (ba.getType() == ActionType.MAGIC) {
			//����̃X�e�[�^�X�őΉ����x�����邩�m�F
			//�����ۂɎx�����̂�exec�����Ƃ��B
			Map<StatusKey, Integer> damage = ba.selfBattleDirectDamage();
			//�_���[�W�����Z
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//�_���[�W�������āA-�̍��ڂ�����ꍇ�A�Ή����x�����Ȃ����ߋ�U��
			//���̖��@�̏���ڂ��擾
			if (!damage.isEmpty() && simulateDamage.hasMinus()) {
				//�Ώۍ��ڂ�1�ł�0�̍��ڂ����������U��
				List<String> shortageStatusDesc = simulateDamage.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList());
				setMsg(MessageType.SPELL_BUT_SHORTAGE, ba, user.getStatus(), shortageStatusDesc);
				if (user.isPlayer()) {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
				} else {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				}
				return;
			}
			//�^�[�Q�b�g���݊m�F�A����ł��Ȃ��ꍇ�A��U��B�������̍ă`�F�b�N�������B
			if (tgt.isEmpty() && !ba.battleEventIsOnly(TargetType.SELF)) {
				setMsg(MessageType.SPELL_BUT_NO_TARGET, ba, user.getStatus());
				if (user.isPlayer()) {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.CMD_SELECT);
				} else {
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				}
				return;
			}

			if (ba.getSpellTime() > 0) {
				//�r�����Ԃ�����ꍇ�͉r���J�n
				addSpelling(user, ba);//MSG�ASTAGE�����̒��ōs���B
				return;
			}
		}

		//�^�[�Q�b�g�s�݂̏ꍇ�A��U��i�~�X�j
		if (tgt.isEmpty()) {
			setMsg(MessageType.NO_TARGET, ba, user.getStatus());
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
		//�^�[�Q�b�g���݂̂��߁A�A�N�V�������s
		tgt.getTarget().forEach(p -> p.getStatus().setDamageCalcPoint());
		ActionResult res = ba.exec(tgt);
		//HP��0�ɂȂ����Ƃ��Ȃǂ̏�Ԉُ��t�^����
		conditionManager.setCondition(GameSystem.getInstance().getPartyStatus());
		conditionManager.setCondition(enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList()));
		//�X�v���C�g�̔�\���������ƃA���^�[�Q�b�g�R���f�B�V�����������̃��[�U�ɂ��R�}���h������
		List<BattleCommand> removeList = new ArrayList<>();
		Map<String, String> deadEnemyName = new HashMap<>();//�����\������
		for (BattleCommand cmd : commandsOfThisTurn) {
			for (String conditionName : BattleConfig.getUntargetConditionNames()) {
				ConditionKey k = ConditionValueStorage.getInstance().get(conditionName).getKey();
				//�A���^�[�Q�b�g��Ԉُ�������Ă��邩����
				if (cmd.getUser().getStatus().hasCondition(conditionName)) {
					removeList.add(cmd);
					cmd.getUser().getSprite().setVisible(false);
					deadEnemyName.put(cmd.getUser().getName(), cmd.getUser().getName() + I18N.translate("IS") + k.getDesc() + I18N.translate("ADD_UNTGT_CONDITION"));
					if (cmd.getUser() instanceof Enemy) {
						if (((Enemy) cmd.getUser()).getDeadSound() != null) {
							((Enemy) cmd.getUser()).getDeadSound().load().stopAndPlay();
						}
					}
					break;
				}
			}
		}
		commandsOfThisTurn.removeAll(removeList);
		//�A���^�[�Q�b�g��Ԉُ�ɂȂ����L�����̃X�v���C�g���\���ɂ���
		for (BattleCharacter c : GameSystem.getInstance().getParty()) {
			for (String cndKey : BattleConfig.getUntargetConditionNames()) {
				if (c.getStatus().hasCondition(cndKey)) {
					c.getSprite().setVisible(false);
					deadEnemyName.put(c.getName(), c.getName() + I18N.translate("IS") + ConditionValueStorage.getInstance().get(cndKey).getKey().getDesc() + I18N.translate("ADD_UNTGT_CONDITION"));
				}
			}
		}
		for (BattleCharacter c : enemies) {
			for (String cndKey : BattleConfig.getUntargetConditionNames()) {
				if (c.getStatus().hasCondition(cndKey)) {
					c.getSprite().setVisible(false);
					deadEnemyName.put(c.getName(), c.getName() + I18N.translate("IS") + ConditionValueStorage.getInstance().get(cndKey).getKey().getDesc() + I18N.translate("ADD_UNTGT_CONDITION"));
				}
			}
		}
		List<String> text = new ArrayList<>();
		//1�s��
		text.add(user.getName() + " " + I18N.translate("S") + " " + ba.getName() + " !!");//���s�s�v
		//2�s�ڈȍ~
		class Tgt {

			String name;
			Map<StatusKey, Float> damage;

			Tgt(String name, Map<StatusKey, Float> damage) {
				this.name = name;
				this.damage = damage;
			}

		}
		List<Tgt> map = tgt.getTarget().stream().map(p -> new Tgt(p.getName(), p.getStatus().calcDamage())).collect(Collectors.toList());
		if (map.stream().flatMap(f -> f.damage.entrySet().stream()).count() >= 7) {
			//���σ��[�h
			for (String statusKey : BattleConfig.getVisibleStatus().stream().sorted().collect(Collectors.toList())) {
				float avg = 0;
				for (Tgt t : map) {
					if (t.damage.containsKey(statusKey)) {
						avg += t.damage.get(statusKey);
					}
				}
				avg /= map.size();
				String txt = I18N.translate("AVERAGE")
						+ " " + Math.abs((int) avg) + " "
						+ StatusKeyStorage.getInstance().get(statusKey).getDesc();
				if (avg < 0) {
					txt += " " + I18N.translate("GET_DAMAGE");
				} else {
					txt += " " + I18N.translate("HEALDAMAGE");
				}
				txt += " !";
				text.add(txt);
			}
			for (String v : deadEnemyName.values()) {
				text.add(v);
			}
		} else {
			//�S�s�\�����[�h
			for (String statusKey : BattleConfig.getVisibleStatus().stream().sorted().collect(Collectors.toList())) {
				for (Tgt t : map) {
					if (t.damage.containsKey(statusKey)) {
						//visibleStatus��3�܂ŕ\��
						String txt = t.name + I18N.translate("S");
						txt += " " + StatusKeyStorage.getInstance().get(statusKey).getDesc() + " " + I18N.translate("TO");
						float v = t.damage.get(statusKey);
						txt += (Math.abs((int) v)) + "";
						if (v < 0) {
							txt += " " + I18N.translate("GET_DAMAGE");
						} else {
							txt += " " + I18N.translate("HEALDAMAGE");
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
		System.out.println(text);
		setMsg(MessageType.ACTION_SUCCESS, ba, res, text);
		animation.addAll(res.getAnimation());
		currentBAWaitTime = new FrameTimeCounter(ba.getWaitTime());
		stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);

	}

	public void commitPCsMove() {
		//�^�[�Q�b�g�V�X�e���ɒl���Z�b�g
		//AFTER_MOVE�I���ɓ���
		stage.setStage(Stage.AFTER_MOVE_CMD_SELECT);
	}

	//PC�̈ړ����L�����Z�����āA�ړ��O�̈ʒu�ɖ߂��B�m��́ucommit�v�^�C�v�̃A�N�V��������B
	public void cancelPCsMove() {
		//�ꏊ��������
		currentCmd.getUser().getSprite().setLocation(moveIinitialLocation);
		currentCmd.getUser().unsetTarget();
		//��ԋ߂��G�̕���������
		BattleCharacter e = BattleTargetSystem.nearEnemys(currentCmd.getUser());
		KVector v = new KVector();
		v.setAngle(currentCmd.getUser().getCenter(), e.getCenter());
		currentCmd.getUser().to(v.round());

		//CMD_SELECT�ɖ߂�
		stage.setStage(BattleSystem.Stage.CMD_SELECT);
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
		//���[�U�ɉr�����̏�Ԉُ��t�^
		currentCmd.getUser().getStatus().addCondition(BattleConfig.ConditionName.casting);
		//�r�����J�n������\��
		setMsg(MessageType.SPELL_START, ba, user.getStatus());
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
				//�`�F�b�N�E�C���h�E���o��
				messageWindowSystem.setItemDesc(currentCmd.getUser().getStatus(), i);
				stage.setStage(BattleSystem.Stage.SHOW_ITEM_DESC);
				break;
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_EQIP:
				//�����ł��Ȃ�
				if (i.getEqipmentSlot() == null) {
					setMsg(MessageType.CANT_EQIP, List.of(i.getName()));
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
					return;
				}
				//�����ł��Ȃ��i�����l�j
				if (!currentCmd.getUser().getStatus().canEqip(i)) {
					setMsg(MessageType.CANT_EQIP, List.of(i.getName()));
					stage.setStage(BattleSystem.Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
					return;
				}
				//���������E�O����
				assert i.getEqipmentSlot() != null : "item is not eqip";
				if (currentCmd.getUser().getStatus().isEqip(i.getName())) {
					currentCmd.getUser().getStatus().removeEqip(i);
				} else {
					currentCmd.getUser().getStatus().addEqip(i);
				}
				MessageType t = currentCmd.getUser().getStatus().isEqip(i.getName())
						? MessageType.EQIP_ITEM
						: MessageType.UNEQIP_ITEM;
				setMsg(t, List.of(i.getName()));
				stage.setStage(BattleSystem.Stage.EXECUTING_ACTION);
				break;
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_USE:
				//�o�g���A�N�V�����������Ă��Ȃ��ꍇ�A�g���Ȃ����b�Z�[�W�\��
				if (!i.isBattleUse()) {
					setMsg(MessageType.CANT_USE_THIS_ITEM, List.of(i.getName()));
					stage.setStage(Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
					break;
				}
				//�g��
				itemChoiceMode = BattleMessageWindowSystem.ITEM_CHOICE_USE_USE;
				area = (int) (currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
				setMsg(MessageType.ITEM_WHO_TO_USE, List.of(i.getName()));
				break;
			case BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS:
				//�n��
				itemChoiceMode = BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS;
				area = (int) (currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
				setMsg(MessageType.ITEM_WHO_TO_PASS, List.of(i.getName()));
				break;
			default:
				throw new AssertionError("undefined item choice use No");
		}
		//�N�ɁH
		if (itemChoiceMode >= 0) {
			ActionTarget t = BattleTargetSystem.instantTarget(currentCmd.getUser(), i, area,
					false,
					itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_USE
					|| itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS
			);
			//�^�[�Q�b�g�s�݂̏ꍇ
			if (itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS && t.getTarget().isEmpty()) {
				//�p�X���閡�������Ȃ�
				setMsg(MessageType.NO_TARGET, List.of(i.getName()));
				stage.setStage(Stage.EXECUTING_ACTION, Stage.ITEM_CHOICE_USE);
				return;
			}
			List<String> tgt = t.getTarget().stream().map(p -> p.getName()).collect(Collectors.toList());
			//�^�[�Q�b�g�I����
			itemPassAndUse = i;
			String msg = (itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS)
					? i.getName() + I18N.translate("WHO_DO_PASS")
					: i.getName() + I18N.translate("WHO_DO_USE");
			tgt.add(0, msg);
			messageWindowSystem.getTgtW().setText(tgt.stream().map(p -> new Text(p)).collect(Collectors.toList()));
			stage.setStage(BattleSystem.Stage.TARGET_SELECT);
		}
	}

	public BattleCommand getCurrentCmd() {
		return currentCmd;
	}

	//�ړ���U���ہi�G���A�j�̐ݒ���s���B�����ōU���ł��邩��n���B
	@LoopCall
	public void setMoveAction(boolean attackOK, int p) {
		if (prevAttackOK == attackOK) {
			return;
		}
		prevAttackOK = attackOK;
		if (!attackOK) {
			messageWindowSystem.getInfoW().setText(I18N.translate("AFTER_MOVE_ATK_NG"));
			messageWindowSystem.getInfoW().allText();
			List<CmdAction> list = new ArrayList<>();
			list.add(ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
			messageWindowSystem.getAfterMoveW().setActions(list);
			messageWindowSystem.setVisible(
					BattleMessageWindowSystem.StatusVisible.ON,
					BattleMessageWindowSystem.Mode.AFTER_MOVE,
					BattleMessageWindowSystem.InfoVisible.ON);
		} else {
			messageWindowSystem.getInfoW().setText(I18N.translate("AFTER_MOVE_ATK_OK"));
			messageWindowSystem.getInfoW().allText();
			List<CmdAction> actions = currentCmd.getBattleActionOf(ActionType.ATTACK);
			Collections.sort(actions);
			actions.add(0, ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
			messageWindowSystem.getAfterMoveW().setActions(actions);
			messageWindowSystem.setVisible(
					BattleMessageWindowSystem.StatusVisible.ON,
					BattleMessageWindowSystem.Mode.AFTER_MOVE,
					BattleMessageWindowSystem.InfoVisible.ON);
		}
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
			//�p�Xor�g��
			if (itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_USE) {
				if (GameSystem.isDebugMode()) {
					System.out.println("use item : " + itemPassAndUse + " to " + messageWindowSystem.getTgtW().getSelected().getText());
				}
				//�^�[�Q�b�g�ɑ΂��ăA�N�V���������s
				String tgtName = messageWindowSystem.getTgtW().getSelected().getText();
				//PC,NPC���疼�O����
				List<BattleCharacter> all = new ArrayList();
				all.addAll(enemies);
				all.addAll(GameSystem.getInstance().getParty());
				BattleCharacter tgt = all.stream().filter(p -> p.getName().equals(tgtName)).collect(Collectors.toList()).get(0);
				itemPassAndUse.exec(BattleTargetSystem.instantTarget(currentCmd.getUser(), itemPassAndUse).setTarget(List.of(tgt)));
				//�h���b�v�A�C�e���C�x���g�̎��s
				if (itemPassAndUse.getBattleEvent().stream().filter(p -> p.getParameterType() == ParameterType.ITEM_LOST).count() > 0) {
					//TODO:�b��
					currentCmd.getUser().getStatus().getItemBag().drop(itemPassAndUse);
				}
				//�A�N�V�����X�V
				GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction(true));
				//���ʂ�\��
				setMsg(MessageType.ITEM_USED, List.of(itemPassAndUse.getName()));
				itemPassAndUse = null;
				stage.setStage(Stage.EXECUTING_ACTION);
				itemChoiceMode = -1;
				return;
			}
			if (itemChoiceMode == BattleMessageWindowSystem.ITEM_CHOICE_USE_PASS) {
				if (GameSystem.isDebugMode()) {
					System.out.println("pass item : " + itemPassAndUse + " to " + messageWindowSystem.getTgtW().getSelected().getText());
				}
				//�^�[�Q�b�g�ɑ΂��ăp�X�����s
				//PC,NPC���疼�O����
				String tgtName = messageWindowSystem.getTgtW().getSelected().getText();
				List<BattleCharacter> all = new ArrayList();
				all.addAll(enemies);
				all.addAll(GameSystem.getInstance().getParty());
				BattleCharacter tgt = all.stream().filter(p -> p.getName().equals(tgtName)).collect(Collectors.toList()).get(0);
				BattleCharacter user = currentCmd.getUser();
				user.getStatus().passItem(tgt.getStatus(), itemPassAndUse);
				//�A�N�V�����X�V
				GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction(true));
				setMsg(MessageType.ITEM_PASSED, List.of(itemPassAndUse.getName()));
				stage.setStage(Stage.EXECUTING_ACTION);
				itemPassAndUse = null;
				itemChoiceMode = -1;
				return;
			}
			return;
		}
		//�U���^�[�Q�b�g�Z���N�g�m��(ONE�̂�
		assert stage.getStage() == Stage.TARGET_SELECT : "target select not yet :" + stage.getStage();
		if (messageWindowSystem.getCmdW().isVisible()) {
			assert messageWindowSystem.getCmdW().getSelectedCmd().getType() != ActionType.ITEM : "atk commit, but action is item:" + messageWindowSystem.getCmdW().getSelectedCmd();
			assert messageWindowSystem.getCmdW().getSelectedCmd().getType() != ActionType.OTHER : "atk commit, but action is other:" + messageWindowSystem.getCmdW().getSelectedCmd();
			execAction(messageWindowSystem.getCmdW().getSelectedCmd(), targetSystem.getSelected());
		} else {
			assert messageWindowSystem.getAfterMoveW().getSelectedCmd().getType() != ActionType.ITEM : "atk commit(A), but action is item:" + messageWindowSystem.getAfterMoveW().getSelectedCmd();
			//�m��͓���
			execAction(messageWindowSystem.getAfterMoveW().getSelectedCmd(), targetSystem.getSelected());
		}
	}

	/**
	 * �틵�}���[�h�̐ؑ�
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

	private void setMsg(MessageType t) {
		//t == BATTLE_END
		String s = t.get(null, null, null, null);
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
	}

	private void setMsg(MessageType t, List<String> option) {
		String s = t.get(null, null, option, null);
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
	}

	private void setMsg(MessageType t, CmdAction a, ActionResult res) {
		String s = t.get(a, null, null, res);
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
	}

	private void setMsg(MessageType t, CmdAction a, ActionResult res, List<String> option) {
		String s = t.get(a, null, option, res);
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
	}

	private void setMsg(MessageType t, CmdAction a, Status user) {
		String s = t.get(a, user, null, null);
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
	}

	private void setMsg(MessageType t, CmdAction a, Status user, List<String> option) {
		String s = t.get(a, user, option, null);
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
	}

	private void setMsg(MessageType t, CmdAction a, Status user, ActionResult res) {
		String s = t.get(a, user, null, res);
		messageWindowSystem.getActionResultW().setText(s);
		messageWindowSystem.getActionResultW().allText();
	}

	public void update() {
		battleFieldSystem.update();
		messageWindowSystem.update();
		targetSystem.update();
		enemies.forEach(v -> v.update());
		if (stage.getStage() == Stage.BATLE_END) {
			return;
		}

		//�^�[�Q�b�g�V�X�e���̃J�����g�\���ʒu�X�V
		if (targetSystem.getCurrentArea().isVisible()) {
			targetSystem.getCurrentArea().setLocationByCenter(currentCmd.getSpriteCenter());
		}

		//���s����
		List<BattleWinLoseLogic> winLoseLogic = BattleConfig.getWinLoseLogic();
		if (winLoseLogic.isEmpty()) {
			throw new GameSystemException("win lose logic is empty, this battle never end.");
		}
		List<Status> party = GameSystem.getInstance().getPartyStatus();
		List<Status> enemy = enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList());
		for (BattleWinLoseLogic l : winLoseLogic) {
			BattleResult result = l.isWinOrLose(party, enemy);
			if (result == BattleResult.NOT_YET) {
				continue;
			}
			targetSystem.getCurrentArea().setArea(0);
			targetSystem.getInitialArea().setArea(0);
			//�퓬�I������
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
					//�h���b�v�A�C�e���̊m������
					if (Random.percent(i.getP())) {
						dropItems.addAll(i.cloneN());
					}
				}
			}
			battleResultValue = new BattleResultValues(result, exp, dropItems, nextLogicName);
			String text = "---" + I18N.translate("BATTLE_RESULT") + "---" + Text.getLineSep() + I18N.translate("WIN_BATTLE") + Text.getLineSep();
			text += I18N.translate("GET_EXP") + ":" + exp + Text.getLineSep();
			text += I18N.translate("DROP_ITEM") + ":" + Text.getLineSep();
			for (Item i : dropItems) {
				text += " " + i.getName() + Text.getLineSep();
			}
			messageWindowSystem.getBattleResultW().setText(text);
			messageWindowSystem.getBattleResultW().allText();
			messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.BATTLE_RESULT);

			if (GameSystem.isDebugMode()) {
				System.out.println(" this battle is ended");
			}
			stage.setStage(Stage.BATLE_END);
		}

		//���ʂ̏I������A�j���[�V��������菜��
		//�A�j���[�V�����̓A�N�V�����̑ҋ@���Ԃ�蒷���\�����邱�Ƃ��\�Ȃ���stage�O�Ŏ��{
		List<AnimationSprite> removeList = new ArrayList<>();
		for (AnimationSprite a : animation) {
			if (a.getAnimation() == null) {//null�͊�{�����Ă��Ȃ��̂ł��������������
				removeList.add(a);
				continue;
			}
			if (a.getAnimation().isEnded() || !a.isVisible() || !a.isExist()) {
				removeList.add(a);
			}
		}
		animation.removeAll(removeList);

		//�X�e�[�W�ʏ���
		GameSystem gs = GameSystem.getInstance();
		switch (stage.getStage()) {
			case STARTUP:
				//�X�^�[�g�A�b�v����update���Ă΂�邱�Ƃ͂Ȃ����߃G���[
				throw new GameSystemException("update call before start");
			case INITIAL_MOVING:
				//�v���C���[�L�����N�^�[���ڕW�̍��W�ɋ߂Â��܂ňړ������s�A�ړI�n�ɋ߂Â�����X�e�[�WWAIT�ɕς���
				gs.getParty().forEach(p -> p.move());
				//�ړ��I������
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
				targetSystem.getInitialArea().setArea(0);
				//�v���C���[�L�����N�^�[���ڕW�̍��W�ɋ߂Â��܂ňړ������s�A�ړI�n�ɋ߂Â�����X�e�[�WWAIT�ɕς���
				currentCmd.getUser().moveToTgt();

				if (!currentCmd.getUser().isMoving()) {
					//�X�v���C�g�ƃX�e�[�^�X���\���ɂ���
					int i = 0;
					for (; GameSystem.getInstance().getPartyStatus().equals(currentCmd.getUser().getStatus()); i++);
					currentCmd.getUser().getSprite().setVisible(false);
					messageWindowSystem.getStatusW().getMw().get(i).setVisible(false);

					//PC�S����������A�S���������ꍇ�A�퓬�I��
					if (party.stream().allMatch(p -> p.hasCondition(BattleConfig.ConditionName.escaped))) {
						//�S��������
						//�A���^�[�Q�b�g��Ԉُ�t�^�i������ȊO�j�̓G��EXP�����v���ēn��
						int exp = 0;
						for (Enemy e : enemies) {
							if (e.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames()
									.stream().filter(p -> !p.equals(BattleConfig.ConditionName.escaped)).collect(Collectors.toList()))) {
								exp += (int) e.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.exp).getValue();
							}
						}
						battleResultValue = new BattleResultValues(BattleResult.ESCAPE, exp, new ArrayList<>(), winLogicName);
						String text = "---" + I18N.translate("BATTLE_RESULT") + "---" + Text.getLineSep() + I18N.translate("PLAYER_WAS_ESCAPED") + Text.getLineSep();
						text += I18N.translate("GET_EXP") + ":" + exp + Text.getLineSep();
						text += I18N.translate("DROP_ITEM") + ":" + I18N.translate("NOTHING") + Text.getLineSep();
						messageWindowSystem.getBattleResultW().setText(text);
						messageWindowSystem.getBattleResultW().allText();
						messageWindowSystem.setVisible(BattleMessageWindowSystem.Mode.BATTLE_RESULT);
						currentBGM.stop();
						currentBGM.dispose();
						winBGM.load().stopAndPlay();
						stage.setStage(Stage.BATLE_END);
						break;
					}
					//NPC�S����������
					if (enemies.stream().allMatch(p -> p.getStatus().hasCondition(BattleConfig.ConditionName.escaped))) {
						targetSystem.getCurrentArea().setArea(0);
						targetSystem.getInitialArea().setArea(0);
						//�A���^�[�Q�b�g��Ԉُ�t�^�i������ȊO�j�̓G��EXP�����v���ēn��
						int exp = 0;
						List<Item> dropItems = new ArrayList<>();
						for (Enemy e : enemies) {
							if (e.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames()
									.stream().filter(p -> !p.equals(BattleConfig.ConditionName.escaped)).collect(Collectors.toList()))) {
								exp += (int) e.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.exp).getValue();
								//�h���b�v�A�C�e���̔���
								List<DropItem> items = e.getDropItem();
								for (DropItem ii : items) {
									//�h���b�v�A�C�e���̊m������
									if (Random.percent(ii.getP())) {
										dropItems.addAll(ii.cloneN());
									}
								}
							}
						}
						battleResultValue = new BattleResultValues(BattleResult.ESCAPE, exp, new ArrayList<>(), winLogicName);
						String text = "---" + I18N.translate("BATTLE_RESULT") + "---" + Text.getLineSep() + I18N.translate("ENEMY_WAS_ESCAPED") + Text.getLineSep();
						text += I18N.translate("GET_EXP") + ":" + exp + Text.getLineSep();
						text += I18N.translate("DROP_ITEM") + ":" + Text.getLineSep();
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
				//�v���C���[�̍s���܂��Ȃ̂ŁA�������Ȃ��B
				//�R�}���h�E�C���h�E�����珈�������s�����
				//���Ƀo�g���R�}���h���擾�����Ƃ��ANPC�Ȃ�NPC�̍s���̃X�e�[�W�ɓ���B
				break;
			case EXECUTING_ACTION:
				//�J�����gBATime���؂��܂ő҂�
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
				//NPC�̈ړ����s�A�I�I�I�I�I�I�I�I�ړ������傣�����烁�b�Z�[�W�E�C���h�E����
				currentCmd.getUser().moveToTgt();
				remMovePoint--;
				//�ړ��|�C���g���؂ꂽ�ꍇ�A�ړ��I�����ă��[�U�R�}���h�҂��Ɉڍs
				if (remMovePoint <= 0 || !currentCmd.getUser().isMoving()) {
					currentCmd.getUser().unsetTarget();
					stage.setStage(Stage.WAITING_EXEC_CMD);
					return;
				}
				//�ړ��|�C���g���؂�Ă��Ȃ��ꍇ�ŁA�ړ��|�C���g�������ȏ�c���Ă���ꍇ�͍U���\
				//�����ȉ��̏ꍇ�͍s���I��
				if (remMovePoint < currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue()) {
					return;
				}
				//�A�N�V�����𒊑I�E�E�E���̃X�e�[�W�ɓ���Ƃ��͕K��ENEMY�Ȃ̂ŃL���X�g���s���Ȃ�
				Enemy user;
				CmdAction eba = currentCmd.getBattleActionOf((user = (Enemy) currentCmd.getUser()).getAI(), ActionType.ATTACK);
				if (eba == null) {
					if (GameSystem.isDebugMode()) {
						System.out.println(" enemy " + currentCmd.getUser().getStatus().getName() + " try afterMoveAttack, but dont have attackCMD");
					}
					return;
				}

				// �C�x���g�ΏێҕʂɃ^�[�Q�b�g��ݒ�
				ActionTarget tgt = BattleTargetSystem.instantTarget(currentCmd.getUser(), eba);

				//�^�[�Q�b�g�����Ȃ��ꍇ�A�������Ȃ�
				if (tgt.isEmpty()) {
					return;
				}

				//�ړ���U�����s
				ActionResult res = eba.exec(tgt);
				//HP��0�ɂȂ����Ƃ��Ȃǂ̏�Ԉُ��t�^����
				conditionManager.setCondition(GameSystem.getInstance().getPartyStatus());
				conditionManager.setCondition(enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList()));
				//�X�v���C�g�̔�\���������ƃA���^�[�Q�b�g�R���f�B�V�����������̃��[�U�ɂ��R�}���h������
				List<BattleCommand> removeList2 = new ArrayList<>();
				Map<String, String> deadEnemyName = new HashMap<>();//�����\������
				for (BattleCommand cmd : commandsOfThisTurn) {
					for (String conditionName : BattleConfig.getUntargetConditionNames()) {
						ConditionKey k = ConditionValueStorage.getInstance().get(conditionName).getKey();
						//�A���^�[�Q�b�g��Ԉُ�������Ă��邩����
						if (cmd.getUser().getStatus().hasCondition(conditionName)) {
							removeList2.add(cmd);
							cmd.getUser().getSprite().setVisible(false);
							deadEnemyName.put(cmd.getUser().getName(), cmd.getUser().getName() + I18N.translate("IS") + k.getDesc() + I18N.translate("ADD_UNTGT_CONDITION"));
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
				//�A���^�[�Q�b�g��Ԉُ�ɂȂ����L�����̃X�v���C�g���\���ɂ���
				for (BattleCharacter c : GameSystem.getInstance().getParty()) {
					for (String cndKey : BattleConfig.getUntargetConditionNames()) {
						if (c.getStatus().hasCondition(cndKey)) {
							c.getSprite().setVisible(false);
							deadEnemyName.put(c.getName(), c.getName() + I18N.translate("IS") + ConditionValueStorage.getInstance().get(cndKey).getKey().getDesc() + I18N.translate("ADD_UNTGT_CONDITION"));
						}
					}
				}
				for (BattleCharacter c : enemies) {
					for (String cndKey : BattleConfig.getUntargetConditionNames()) {
						if (c.getStatus().hasCondition(cndKey)) {
							c.getSprite().setVisible(false);
							deadEnemyName.put(c.getName(), c.getName() + I18N.translate("IS") + ConditionValueStorage.getInstance().get(cndKey).getKey().getDesc() + I18N.translate("ADD_UNTGT_CONDITION"));
						}
					}
				}
				List<String> text = new ArrayList<>();
				//1�s��
				text.add(user.getName() + " " + I18N.translate("S") + " " + eba.getName() + " !!");//���s�s�v

				//2�s�ڈȍ~
				class Tgt {

					String name;
					Map<StatusKey, Float> damage;

					Tgt(String name, Map<StatusKey, Float> damage) {
						this.name = name;
						this.damage = damage;
					}

				}
				List<Tgt> map = tgt.getTarget().stream().map(p -> new Tgt(p.getName(), p.getStatus().calcDamage())).collect(Collectors.toList());
				if (map.stream().flatMap(f -> f.damage.entrySet().stream()).count() >= 7) {
					//���σ��[�h
					for (String statusKey : BattleConfig.getVisibleStatus().stream().sorted().collect(Collectors.toList())) {
						float avg = 0;
						for (Tgt t : map) {
							if (t.damage.containsKey(statusKey)) {
								avg += t.damage.get(statusKey);
							}
						}
						avg /= map.size();
						String txt = I18N.translate("AVERAGE")
								+ " " + Math.abs((int) avg) + " "
								+ StatusKeyStorage.getInstance().get(statusKey).getDesc();
						if (avg < 0) {
							txt += " " + I18N.translate("GET_DAMAGE");
						} else {
							txt += " " + I18N.translate("HEALDAMAGE");
						}
						txt += " !";
						text.add(txt);
					}
					for (String v : deadEnemyName.values()) {
						text.add(v);
					}
				} else {
					//�S�s�\�����[�h
					for (String statusKey : BattleConfig.getVisibleStatus().stream().sorted().collect(Collectors.toList())) {
						for (Tgt t : map) {
							if (t.damage.containsKey(statusKey)) {
								//visibleStatus��3�܂ŕ\��
								String txt = t.name + I18N.translate("S");
								txt += " " + StatusKeyStorage.getInstance().get(statusKey).getDesc() + " " + I18N.translate("TO");
								float v = t.damage.get(statusKey);
								txt += (Math.abs((int) v)) + "";
								if (v < 0) {
									txt += " " + I18N.translate("GET_DAMAGE");
								} else {
									txt += " " + I18N.translate("HEALDAMAGE");
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

				setMsg(MessageType.ACTION_SUCCESS, eba, res, text);
				animation.addAll(res.getAnimation());
				currentBAWaitTime = new FrameTimeCounter(eba.getWaitTime());
				//�A�N�V�������s���ɓ���
				stage.setStage(Stage.EXECUTING_ACTION);
				break;
			case ITEM_CHOICE_USE:
			case SHOW_ITEM_DESC:
			case SHOW_STATUS:
				//�������Ȃ��i��p���\�b�h���瑀�삷��
				break;
			case BATLE_END:
				//�������Ȃ��i���[�U����҂�
				break;
			default:
				throw new AssertionError("UNDEFINED STAGE");
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		battleFieldSystem.draw(g);

		enemies.forEach(p -> p.draw(g));
		GameSystem.getInstance().getPartySprite().forEach(p -> p.draw(g));

		animation.forEach(v -> v.draw(g));

		targetSystem.draw(g);

		messageWindowSystem.draw(g);
	}

	//�^�[�Q�b�g�I�����[�h���L�����Z�����ĕ���B�A�N�V�����I���ɖ߂�
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
