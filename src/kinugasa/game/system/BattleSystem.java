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
 * �o�g���Ǘ��N���X�B
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
	private Sound prevBGM, currentBGM;
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

	//------------------------------------------------------update���\�b�h�X�e�[�W
	public enum Stage {
		/**
		 * �J�n?�����ړ��J�n�O�B�I�������INITIAL�ɓ���B
		 */
		STARTUP,
		/**
		 * �����ړ����B�I�������WAIT�ɓ���B
		 */
		INITIAL_MOVE,
		/**
		 * �����A�j���[�V�������s���B�I�������WAIT�ɓ���B
		 */
		ESCAPING,
		/**
		 * ���[�U�R�}���h�ҋ@�Bexec���Ă΂��܂ŉ������Ȃ��B
		 */
		WAITING_USER_CMD,
		/**
		 * �v���C���[�L�����N�^?�ړ����B�m��A�N�V�������Ă΂��܂ŉ������Ȃ��B
		 */
		PLAYER_MOVE,
		/**
		 * �^�[�Q�b�g�I�𒆁Bexec���Ă΂��܂ŉ������Ȃ��B
		 */
		TARGET_SELECT,
		/**
		 * �A�N�V�������s���B�I�������WAIT�ɓ���B
		 */
		EXECUTING_ACTION,
		/**
		 * �G�ړ����s���B�I�������WAIT�ɓ���B
		 */
		EXECUTING_MOVE,
		/**
		 * �o�g���͏I�����āA�Q�[���V�X�e������̏I���w����҂��Ă���B
		 */
		BATLE_END,
		/**
		 * INFO���b�Z�[�W�\�����B�I�������WAIT�ɓ���B��ɃL�����Z���i�čs���\�ȍs�����s�j�Ɏg���B
		 */
		SHOW_INFO_MSG,
	}
	//���݂̃X�e�[�W
	private Stage stage;
	//-------------------------------------------------------------------�V�X�e��
	//���b�Z�[�W�E�C���h�E�V�X�e���̃C���X�^���X
	private BattleMessageWindowSystem messageWindowSystem;
	//�^�[�Q�b�g�I���V�X�e���̃C���X�^���X
	private BattleTargetSystem targetSystem;
	//�o�g���t�B�[���h�C���X�^���X
	private BattleFieldSystem battleFieldSystem;
	//��Ԉُ�}�l�[�W��
	private ConditionManager conditionManager;

	//�f�o�b�O�p
	@Deprecated
	public void setBattleResultValue(BattleResultValues battleResultValue) {
		this.battleResultValue = battleResultValue;
	}

	//�X�e�[�W�ؑ�
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
		//�G���J�E���g���̎擾
		EnemySetStorage ess = enc.getEnemySetStorage().load();
		EnemySet es = ess.get();
		//�OBGM�̒�~
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
		//�o�g��BGM�̍Đ�
		if (es.hasBgm()) {
			SoundStorage.getInstance().get(es.getBgmMapName()).stopAll();
			currentBGM = es.getBgm().load();
			currentBGM.stopAndPlay();
		}
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
		messageWindowSystem.init(gs.getPartyStatus());
		conditionManager = ConditionManager.getInstance();

		//�o��MSG�ݒ�p�}�b�v
		Map<String, Long> enemyNum = enemies.stream().collect(Collectors.groupingBy(Enemy::getId, Collectors.counting()));
		//�o��MSG�ݒ�
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Long> e : enemyNum.entrySet()) {
			sb.append(e.getKey()).append(I18N.translate("WAS")).append(e.getValue()).append(I18N.translate("APPEARANCE")).append(Text.getLineSep());
		}
		messageWindowSystem.setActionMessage(sb.toString(), Integer.MAX_VALUE);//����ɏ㏑�������̂ŁA�ő厞�ԕ\���ł悢

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
		setStage(Stage.INITIAL_MOVE, "encountInit");
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

		//�A�C�e���g�p���A�N�V�����ɒǉ�����
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
		//���̃^�[���̃o�g���R�}���h���쐬
		List<BattleCharacter> list = getAllChara();
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

		setStage(Stage.WAITING_USER_CMD, "TURN_START");
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
	//--------------------------------------------------------------------------
	//
	private boolean end = false;

	void endBattle() {
		//�����̔z�u�̏�����
		List<PlayerCharacterSprite> partySprite = GameSystem.getInstance().getPartySprite();
		for (int i = 0; i < partySprite.size(); i++) {
			partySprite.get(i).to(partyInitialDir.get(i));
			partySprite.get(i).setLocation(partyInitialLocation.get(i));
		}
		//�G�ԍ��̏�����
		EnemyBlueprint.initEnemyNoMap();
		//�������R���f�B�V�����Ŕ�\���ɂȂ��Ă���ꍇ�\������
		//�A�C�e���A�N�V�������폜����
		for (PlayerCharacter pc : GameSystem.getInstance().getParty()) {
			//�������㎀�S�A���S�����㓦����͂ł��Ȃ��̂ŁA����Ŗ��Ȃ��͂�
			if (pc.getStatus().hasCondition(BattleConfig.ConditionName.escaped)) {
				pc.getSprite().setVisible(true);
				//�������R���f�B�V�������O��
				pc.getStatus().removeCondition(BattleConfig.ConditionName.escaped);

				List<CmdAction> removeList = pc.getStatus().getActions().stream().filter(p -> p.getType() == ActionType.ITEM_USE).collect(Collectors.toList());
				pc.getStatus().getActions().removeAll(removeList);
			}
		}
		//BGM�̏���
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
		assert currentCmd != null : "BS currentCMD is null";
		commandsOfThisTurn.removeFirst();

		//�^�[�Q�b�g�V�X�e��������
		targetSystem.unsetCurrent();
		currentBAWaitTime = null;

		BattleCharacter user = currentCmd.getUser();
		//�A���^�[�Q�b�g��Ԉُ�̏ꍇ�A���b�Z�[�W�o�����Ɏ��ɑ���
		if (user.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
			if (GameSystem.isDebugMode()) {
				System.out.println(user.getStatus().getName() + " is bad condition");
			}
			return execCmd();
		}
		//�h��܂��͉�𒆂̏ꍇ�A���̃t���O���O��
		if (user.getStatus().hasCondition(BattleConfig.ConditionName.defence)) {
			user.getStatus().removeCondition(BattleConfig.ConditionName.defence);
		}
		if (user.getStatus().hasCondition(BattleConfig.ConditionName.avoidance)) {
			user.getStatus().removeCondition(BattleConfig.ConditionName.avoidance);
		}

		//���@�r�������C�x���g�̏ꍇ�APC�ł�NPC�ł��������s�A�i�r�����R���f�B�V�������O��
		//���@�̃R�X�g�ƃ^�[�Q�b�g�́A�r���J�n�ƏI����2�񔻒肷��B
		//�����́u�r���I�����v�̏����B
		if (currentCmd.isMagicSpell()) {
			CmdAction ba = currentCmd.getFirstBattleAction();//1���������Ă��Ȃ�
			//����ł̃^�[�Q�b�g���擾
			BattleActionTarget target = BattleTargetSystem.instantTarget(currentCmd.getUser(), ba);
			//�^�[�Q�b�g�����Ȃ��ꍇ�A�r�����s�̃��b�Z�[�W�o��
			if (target.isEmpty()) {
				//�ΏۂȂ�
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
			//����̃X�e�[�^�X�őΉ����x�����邩�m�F
			//�����ۂɎx�����̂�exec�����Ƃ��B
			Map<StatusKey, Integer> damage = ba.selfBattleDirectDamage();
			//�_���[�W�����Z
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//�_���[�W�������āA0�̍��ڂ�����ꍇ�A�Ή����x�����Ȃ����ߋ�U��
			//���̖��@�̏���ڂ��擾
			List<StatusKey> shortageKey = new ArrayList<>();
			for (ActionEvent e : ba.getBattleEvent().stream().filter(p -> p.getTargetType() == TargetType.SELF).collect(Collectors.toList())) {
				shortageKey.add(StatusKeyStorage.getInstance().get(e.getTgtName()));
			}
			if (!damage.isEmpty() && simulateDamage.isZero(false, shortageKey)) {
				//�Ώۍ��ڂ�1�ł�0�̍��ڂ����������U��
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
			//�r�������A���@���ʔ���
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

		//��Ԉُ�œ����Ȃ��Ƃ��X�L�b�v�i���b�Z�[�W�͏o��
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

		//�����œ����Ȃ��Ƃ��́A��~�܂��̓o�g���A�N�V������K���Ɏ擾���Ď������s����
		if (currentCmd.isConfu()) {
			if (Random.percent(BattleConfig.conguStopP)) {
				//�����Ȃ�
				StringBuilder s = new StringBuilder();
				s.append(currentCmd.getUser().getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(I18N.translate("CONFU_STOP"));
				setActionMessage(s.toString());
				currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			} else {
				//�����邪����
				CmdAction ba = currentCmd.randomAction();
				currentBAWaitTime = ba.createWaitTime();
				execAction(ba);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			}
		}

		//NPC
		if (currentCmd.getMode() == BattleCommand.Mode.CPU) {
			//NPC�A�N�V�������������s�A���̒��ŃX�e�[�W���ς��
			messageWindowSystem.closeCommandWindow();
			execAction(currentCmd.getBattleActionEx(((Enemy) currentCmd.getUser()).getAI(), ActionType.OTHER, ActionType.ITEM_USE));
			return currentCmd;
		}
		//PC�̍s���\
		//�J�����g�R�}���h���e���R�}���h�E�C���h�E�ɕ\���A���̑��E�C���h�E�͕���
		messageWindowSystem.getCommandWindow().setCmd(currentCmd);
		messageWindowSystem.getCommandWindow().resetSelect();
		messageWindowSystem.getCommandWindow().setVisible(true);
		messageWindowSystem.closeActionWindow();
		messageWindowSystem.closeAfterMoveCommandWindow();
		messageWindowSystem.closeInfoWindow();
		messageWindowSystem.closeTooltipWindow();

		//�^�[�Q�b�g�V�X�e�����E�C���h�E�̏����I���ŏ�����
		targetSystem.setCurrent(currentCmd.getUser(), messageWindowSystem.getCommandWindow().getSelected());

		//PC�̑���Ȃ̂ŁA�J�����g�R�}���h�̃��[�U�I�y���[�V�����v�ۃt���O��ON�ɐݒ�
		currentCmd.setUserOperation(true);

		if (GameSystem.isDebugMode()) {
			System.out.println("getNextCmdAndExecNPCCmd : return PC_CMD" + currentCmd);
		}
		setStage(Stage.WAITING_USER_CMD, "getNextCmdAndExecNPCCmd");
		return currentCmd;
	}

	public OperationResult execPCAction() {
		//�R�}���h�E�C���h�E�܂��͈ړ���U���E�C���h�E����A�N�V�������擾
		return execAction(messageWindowSystem.isVisibleCommand()
				? messageWindowSystem.getCommandWindow().getSelected()
				: messageWindowSystem.getAfterMoveCommandWindow().getSelected());
	}

	//�A�N�V�������s
	OperationResult execAction(CmdAction a) {
		//PC,NPC��킸�I�����ꂽ�A�N�V���������s����B

		//�E�C���h�E��ԏ�����
		messageWindowSystem.closeTooltipWindow();
		messageWindowSystem.closeInfoWindow();

		//�J�����g���[�U
		BattleCharacter user = currentCmd.getUser();

		//�������̏ꍇ
		if (user.getStatus().isConfu()) {
			//�^�[�Q�b�g�V�X�e���̃J�����g�N�����Ȃ��őΏۂ��擾����
			setActionMessage(user, a);
			BattleActionTarget tgt = BattleTargetSystem.instantTarget(user, a);
			return execAction(a, tgt);
		}

		//NPC�̏ꍇ
		if (!user.isPlayer()) {
			//�A�N�V�����̌��ʔ͈͂ɑ��肪���邩�A�C���X�^���g�m�F
			BattleActionTarget tgt = BattleTargetSystem.instantTarget(user, a);
			if (tgt.isEmpty()) {
				//�^�[�Q�b�g�����Ȃ��ꍇ�ŁA�ړ��A�N�V�����������Ă���ꍇ�͈ړ��J�n
				if (user.getStatus().hasAction(BattleConfig.ActionName.move)) {
					//�ړ��^�[�Q�b�g�͍ł��߂�PC�Ƃ���
					BattleCharacter targetChara = BattleTargetSystem.nearPCs(user);
					user.setTargetLocation(targetChara.getCenter(), a.getAreaWithEqip(user));
					//�ړ�������������
					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
					//�ړ������I�̃��b�Z�[�W�\��
					StringBuilder s = new StringBuilder();
					s.append(user.getName());
					s.append(I18N.translate("ISMOVE"));
					messageWindowSystem.setActionMessage(s.toString(), Integer.MAX_VALUE);
					setStage(Stage.EXECUTING_MOVE, "execAction");//Stage��NPC�ړ��ɏ㏑��
					return OperationResult.SUCCESS;
				} else {
					//�ړ��ł��Ȃ��̂ŉ������Ȃ�
					return OperationResult.MISS;
				}
			} else {
				//�^�[�Q�b�g������ꍇ�͑������s
				return execAction(a, tgt);
			}

		}

		//����R�}���h�̏���
		if (a.getType() == ActionType.OTHER) {
			if (a.getName().equals(BattleConfig.ActionName.avoidance)) {
				//����E�����Ԃ�t�^����
				user.getStatus().addCondition(BattleConfig.ConditionName.avoidance);
				setActionMessage(user, a);
				return OperationResult.SUCCESS;
			}
			if (a.getName().equals(BattleConfig.ActionName.defence)) {
				//�h��E�h���Ԃ�t�^����
				user.getStatus().addCondition(BattleConfig.ConditionName.defence);
				setActionMessage(user, a);
				return OperationResult.SUCCESS;
			}
			if (a.getName().equals(BattleConfig.ActionName.move)) {
				//�ړ��J�n�E�����ʒu���i�[
				moveIinitialLocation = user.getSprite().getLocation();
				messageWindowSystem.closeCommandWindow();
				messageWindowSystem.getAfterMoveCommandWindow().setVisible(true);
				List<CmdAction> action = user.getStatus().getActions(ActionType.ATTACK);
				action.add(ActionStorage.getInstance().get(BattleConfig.ActionName.commit));
				Collections.sort(action);
				messageWindowSystem.getAfterMoveCommandWindow().setActions(action);
				//�^�[�Q�b�g�V�X�e���̃G���A�\����L�����F�l��MOV
				targetSystem.setCurrent(user, a);
				setStage(Stage.PLAYER_MOVE, "execAction");
				return OperationResult.MOVE;
			}
			if (a.getName().equals(BattleConfig.ActionName.commit)) {
				//�ړ��I���E�L�����N�^�̌����ƃ^�[�Q�b�g���W�̃N���A������
				messageWindowSystem.closeAfterMoveCommandWindow();
				user.unsetTarget();
				user.to(FourDirection.WEST);
				setStage(Stage.WAITING_USER_CMD, "execAction");
				return OperationResult.SUCCESS;
			}
			if (a.getName().equals(BattleConfig.ActionName.status)) {
				//�X�e�[�^�X�E�C���h�E�{��
				//TODO:�\����������
				setStage(Stage.WAITING_USER_CMD, "execAction");
				return OperationResult.SHOW_STATUS;
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
					//���������i���j
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
				//NPC�̏ꍇ�A������̐��ɓ���
				if (!user.isPlayer()) {
					remMovePoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
					user.setTargetLocation(w, 1);
					setStage(Stage.EXECUTING_MOVE, "execAction");
					return OperationResult.SUCCESS;
				}
				//�������Ȃ�
				messageWindowSystem.setInfoMessage(I18N.translate("CANT_ESCAPE"));
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return OperationResult.CANCEL;
			}
		}

		//�^�[�Q�b�g�V�X�e���N���v�۔���
		boolean needTargetSystem = false;
		if (user.isPlayer()) {
			//�^�[�Q�b�g�I���̓v���C���[�̂�
			if (a.getType() != ActionType.OTHER) {
				//���̑��C�x���g�ȊO�̓^�[�Q�b�g�I��K�v
				needTargetSystem = true;
			}
			if (a.getType() == ActionType.ITEM_USE) {
				//�A�C�e���g�p�@���A�C�e��extends�A�N�V����
				//�A�C�e���^�[�Q�b�g�I��v��
				//�A�C�e���g�p�́A�A�C�e���g�p->�����̗D��x�Ƃ���
				if (a.isBattleUse() && a.getBattleEvent().stream().anyMatch(p -> p.getTargetType() != SELF) && !((Item) a).canEqip()) {
					//���p�\��SELF�݂̂���Ȃ��ꍇ�^�[�Q�b�g�V�X�e���N��
					//�^�[�Q�b�g�V�X�e�����N������O�ɁA�C���X�^���g�^�[�Q�b�g�Ń^�[�Q�b�g�����邩�m�F����B���Ȃ��ꍇ�L�����Z���ɂ���B
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
					//���p�\��SELF�݂̂̏ꍇ�A�������s
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
					//�����\�A�C�e���̏ꍇ
					Item i = (Item) a;
					//���̃A�C�e�������łɑ������Ă���ꍇ�A��U�肳����
					if (user.getStatus().isEqip(i.getName())) {
						//��U��
						StringBuilder s = new StringBuilder();
						s.append(user.getStatus().getName());
						s.append(I18N.translate("IS"));
						s.append(a.getName());
						s.append(I18N.translate("WAS_EQIP"));
						setActionMessage(s.toString());
						currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
						setStage(Stage.EXECUTING_ACTION, "execAction");
						return OperationResult.CANCEL;//return����̂Ń^�[�Q�b�g�I��v�ۂ͖��������
					}
					//�����ύX
					//�����X���b�g�̑������O��
					user.getStatus().removeEqip(i.getEqipmentSlot());
					//��������
					user.getStatus().eqip(i);
					StringBuilder s = new StringBuilder();
					s.append(user.getStatus().getName());
					s.append(I18N.translate("IS"));
					s.append(a.getName());
					s.append(I18N.translate("IS_EQIP"));
					setActionMessage(s.toString());
					currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
					setStage(Stage.EXECUTING_ACTION, "execAction");
					return OperationResult.SUCCESS;//return����̂Ń^�[�Q�b�g�I��v�ۂ͖��������
				}
				if (!a.isBattleUse()) {
					//�g���Ă����ʂ��Ȃ��A�C�e���̏ꍇ
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
					return OperationResult.MISS;//return����̂Ń^�[�Q�b�g�I��v�ۂ͖��������
				}
			}
			//�^�[�Q�b�g�V�X�e������������Ԃ̏ꍇ�A�^�[�Q�b�g�I��K�v
			if (targetSystem.isEmpty()) {
				needTargetSystem = true;
			} else {
				needTargetSystem = false;
			}
		}

		//�^�[�Q�b�g�V�X�e���N��
		if (needTargetSystem) {
			//�^�[�Q�b�g�V�X�e�����N������O�ɁA�C���X�^���g�^�[�Q�b�g�Ń^�[�Q�b�g�����邩�m�F����B���Ȃ��ꍇ�L�����Z���ɂ���B
			if (!BattleTargetSystem.instantTarget(user, a).hasAnyTargetChara()) {//���@�Ō���^�[�Q�b�g�����Ȃ��ꍇ�������ŋz�������
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

	//�A�N�V�������s
	OperationResult execAction(CmdAction a, BattleActionTarget tgt) {
		messageWindowSystem.closeTooltipWindow();//�^�[�Q�b�g�I�𒆂̕\�������
		//�^�[�Q�b�g�V�X�e�����Ă΂�Ă���̂ŁA������
		targetSystem.unsetCurrent();
		//�J�����g���[�U
		BattleCharacter user = currentCmd.getUser();

		//���@�r���J�n�̏ꍇ�A�r�������X�g�ɒǉ����Ė߂�B
		if (a.getType() == ActionType.MAGIC) {
			//����̃X�e�[�^�X�őΉ����x�����邩�m�F
			//�����ۂɎx�����̂�exec�����Ƃ��B
			Map<StatusKey, Integer> damage = a.selfBattleDirectDamage();
			//�_���[�W�����Z
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//�_���[�W�������āA0�̍��ڂ�����ꍇ�A�Ή����x�����Ȃ����ߋ�U��
			//���̖��@�̏���ڂ��擾
			List<StatusKey> shortageKey = new ArrayList<>();
			for (ActionEvent e : a.getBattleEvent().stream().filter(p -> p.getTargetType() == TargetType.SELF).collect(Collectors.toList())) {
				shortageKey.add(StatusKeyStorage.getInstance().get(e.getTgtName()));
			}
			if (!damage.isEmpty() && simulateDamage.isZero(false, shortageKey)) {
				//�Ώۍ��ڂ�1�ł�0�̍��ڂ����������U��
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
			//�^�[�Q�b�g���݊m�F�A����ł��Ȃ��ꍇ�A��U��B�������̍ă`�F�b�N�������B
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
				//�r�����Ԃ�����ꍇ�͉r���J�n
				addSpelling(user, a);//MSG�ASTAGE�����̒��ōs���B
				return OperationResult.SUCCESS;
			}
		}
		//�^�[�Q�b�g�s�݂̏ꍇ�A��U��i�~�X�j�A�O�̂��߂̏����A��������Ȃ�
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
		//�^�[�Q�b�g���݂̂��߁A�A�N�V�������s
		tgt.getTarget().forEach(p -> p.getStatus().setDamageCalcPoint());
		ActionResult res = a.exec(tgt);
		setActionMessage(user, a, tgt, res);
		setActionAnimation(user, a, tgt, res);
		currentBAWaitTime = new FrameTimeCounter(a.getWaitTime());
		setStage(Stage.EXECUTING_ACTION, "execAction");
		//�A�C�e����REMOVE����Ă���\�������邽�߁A�S���̃A�N�V���������߂�
		getAllChara().forEach(p -> p.getStatus().updateItemAction());
		return OperationResult.SUCCESS;
	}

	//PC�̈ړ����L�����Z�����āA�ړ��O�̈ʒu�ɖ߂��B�m��́ucommit�v�^�C�v�̃A�N�V��������B
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

	//�ړ���U���̐ݒ���s���B�����ōU���ł��邩��n���B
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
		//�E�F�C�g�^�C���ƃX�e�[�W���X�V�I�I�I�I�I�I
		messageWindowSystem.setActionMessage(s, messageWaitTime);
		currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
		setStage(Stage.EXECUTING_ACTION, "execAction");
	}

	private void setActionMessage(BattleCharacter user, CmdAction action) {
		StringBuilder s = new StringBuilder();
		//�����A����A�h��̏ꍇ
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

	//result�Ɋ�Â����b�Z�[�W���A�N�V�����E�C���h�E�ɐݒ肷��
	private void setActionMessage(BattleCharacter user, CmdAction action, BattleActionTarget target, ActionResult result) {
		//�A�N�V���������s����Ă���̂ŁA�K�v�Ȃ��E�C���h�E�͕���
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
		//�A�C�e�����ʔ����̏ꍇ������̂Œ��ӁB

		StatusKey hp = StatusKeyStorage.getInstance().get(BattleConfig.StatusKey.hp);
		//SELF�̓^�[�Q�b�g�������Ă��Ȃ��BSELF�_���[�W�����\������
		if (target.isSelfEvent()) {
			//���ׂẴC�x���g��SELD�ł��邱�Ƃ��m��
			int c = 0, lf = 0;
			for (List<ActionResultType> list : result.getResultType()) {//TGT
				s.append(user.getName());
				int effectIdx = 0;
				for (ActionResultType t : list) {
					ActionEvent e = action.getBattleEvent().get(effectIdx);
					switch (e.getParameterType()) {
						case STATUS:
							//�X�e�[�^�X���ʂ̏ꍇ�A�_���[�W�Z�o���ĕ\��
							if (t == ActionResultType.SUCCESS) {
								//�_���[�W�Z�o
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
							//��Ԉُ�t�^�̏ꍇ�A�ݒu������Ԉُ��\��
							if (t == ActionResultType.SUCCESS) {
								//���ʂ���������
								s.append(I18N.translate("IS"));
								s.append(action.getDesc());
							} else {
								//���ʂ͔������Ȃ���
								s.append(I18N.translate("TO"));
								s.append(I18N.translate("IS"));
								s.append(I18N.translate("NO_EFFECT"));
							}
							break;
						case REMOVE_CONDITION:
							//��Ԉُ�񕜂�\��
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
							//�ϐ��ύX�̏ꍇ�A���������E�オ������\��
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
							//�A�C�e���ǉ��C�x���g�͓��肵���I��\��
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
							//�A�C�e���j���C�x���g�͂Ȃ��Ȃ����I��\��
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
							//NONE�͉������Ȃ�
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
					//�\��������Ȃ��ꍇ
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
						//��Ԉُ�t�^�̏ꍇ�A�ݒu������Ԉُ��\��
						if (t == ActionResultType.SUCCESS) {
							//���ʂ���������
							s.append(I18N.translate("IS"));
							s.append(action.getDesc());
						} else {
							//���ʂ͔������Ȃ���
							s.append(I18N.translate("TO"));
							s.append(I18N.translate("IS"));
							s.append(I18N.translate("NO_EFFECT"));
						}
						break;
					case ATTR_IN:
						//�ϐ��ύX�̏ꍇ�A���������E�オ������\��
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
						//�A�C�e���ǉ��C�x���g�͓��肵���I��\��
						/*
						ITEM_ADD=����肵��
						ITEM_DROP=���Ȃ��Ȃ���
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
						//�A�C�e���j���C�x���g�͂Ȃ��Ȃ����I��\��
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
							//�_���[�W�Z�o
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
						//��Ԉُ�񕜂�\��
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
						//NONE�͉������Ȃ�
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
					//�\��������Ȃ��ꍇ
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

//result�Ɋ�Â��A�j���[�V������this�ɒǉ�����
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

		//�G�l�~�[��PC��Y���W�ɂ��`�揇�̍X�V
		Collections.sort(getAllChara(), (BattleCharacter o1, BattleCharacter o2) -> (int) (o1.getSprite().getY() - o2.getSprite().getY()));

		//�^�[�Q�b�g�V�X�e���̃J�����g�\���ʒu�X�V
		if (targetSystem.getCurrentArea().isVisible()) {
			targetSystem.getCurrentArea().setLocationByCenter(currentCmd.getSpriteCenter());
		}

		//���s����
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
			//�퓬�I������
			String nextLogicName = result == BattleResult.WIN ? winLogicName : loseLogicName;
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

			if (GameSystem.isDebugMode()) {
				System.out.println("this battle is ended");
			}
			setStage(Stage.BATLE_END, "update");
		}

		//���ʂ̏I������A�j���[�V��������菜��
		//�A�j���[�V�����̓A�N�V�����̑ҋ@���Ԃ�蒷���\�����邱�Ƃ��\�Ȃ���stage�O�Ŏ��{
		List<AnimationSprite> removeList = new ArrayList<>();
		for (AnimationSprite a : animation) {
			if (a.getAnimation() == null) {//null�͊�{�����Ă��Ȃ�
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
		switch (stage) {
			case STARTUP:
				throw new GameSystemException("update call before start");
			case INITIAL_MOVE:
				//�v���C���[�L�����N�^�[���ڕW�̍��W�ɋ߂Â��܂ňړ������s�A�ړI�n�ɋ߂Â�����X�e�[�WWAIT�ɕς���
				gs.getPartySprite().forEach(v -> v.move());
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
					messageWindowSystem.closeActionWindow();
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case ESCAPING:
				//�v���C���[�L�����N�^�[���ڕW�̍��W�ɋ߂Â��܂ňړ������s�A�ړI�n�ɋ߂Â�����X�e�[�WWAIT�ɕς���
				currentCmd.getUser().moveToTgt();

				if (!currentCmd.getUser().isMoving()) {
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
						setStage(Stage.BATLE_END, "update");
						messageWindowSystem.closeActionWindow();
						break;
					}
					//NPC�S����������
					if (enemies.stream().allMatch(p -> p.getStatus().hasCondition(BattleConfig.ConditionName.escaped))) {
						//�A���^�[�Q�b�g��Ԉُ�t�^�i������ȊO�j�̓G��EXP�����v���ēn��
						int exp = 0;
						for (Enemy e : enemies) {
							if (e.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames()
									.stream().filter(p -> !p.equals(BattleConfig.ConditionName.escaped)).collect(Collectors.toList()))) {
								exp += (int) e.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.exp).getValue();
							}
						}
						battleResultValue = new BattleResultValues(BattleResult.ESCAPE, exp, new ArrayList<>(), winLogicName);
						//�S��������
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
				//�v���C���[�̍s���܂��Ȃ̂ŁA�������Ȃ��B
				//�R�}���h�E�C���h�E�����珈�������s�����
				//���Ƀo�g���R�}���h���擾�����Ƃ��ANPC�Ȃ�NPC�̍s���̃X�e�[�W�ɓ���B
				break;
			case SHOW_INFO_MSG:
				//INFO��������܂ő҂�
				if (!messageWindowSystem.isVisibleInfoMessage()) {
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case EXECUTING_ACTION:
				//�J�����gBATime���؂��܂ő҂�
				assert currentBAWaitTime != null : "EXECITING_ACTION buf tc is null";
				if (currentBAWaitTime.isReaching()) {
					currentBAWaitTime = null;
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case EXECUTING_MOVE:
				//NPC�̈ړ����s�A�I�I�I�I�I�I�I�I�ړ������傣�����烁�b�Z�[�W�E�C���h�E����
				currentCmd.getUser().moveToTgt();
				remMovePoint--;
				//�ړ��|�C���g���؂ꂽ�ꍇ�A�ړ��I�����ă��[�U�R�}���h�҂��Ɉڍs
				if (remMovePoint <= 0 || !currentCmd.getUser().isMoving()) {
					currentCmd.getUser().unsetTarget();
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
					return;
				}
				//�ړ��|�C���g���؂�Ă��Ȃ��ꍇ�ŁA�ړ��|�C���g�������ȏ�c���Ă���ꍇ�͍U���\
				//�����ȉ��̏ꍇ�͍s���I��
				if (remMovePoint < currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue()) {
					return;
				}
				//�A�N�V�����𒊑I�E�E�E���̃X�e�[�W�ɓ���Ƃ��͕K��ENEMY�Ȃ̂ŃL���X�g���s���Ȃ�
				CmdAction eba = currentCmd.getBattleActionOf(((Enemy) currentCmd.getUser()).getAI(), ActionType.ATTACK);
				if (eba == null) {
					if (GameSystem.isDebugMode()) {
						System.out.println("enemy " + currentCmd.getUser().getStatus().getName() + " try afterMoveAttack, but dont have attackCMD");
					}
					return;
				}

				// �C�x���g�ΏێҕʂɃ^�[�Q�b�g��ݒ�
				BattleActionTarget tgt = BattleTargetSystem.instantTarget(currentCmd.getUser(), eba);

				//�^�[�Q�b�g�����Ȃ��ꍇ�A�������Ȃ�
				if (tgt.isEmpty()) {
					return;
				}

				//�ړ���U�����s
				ActionResult res = eba.exec(tgt);
				updateCondition();
				currentBAWaitTime = eba.createWaitTime();
				//���b�Z�[�W�\��
				setActionMessage(currentCmd.getUser(), eba, tgt, res);
				//�A�j���[�V�����ǉ�
				setActionAnimation(currentCmd.getUser(), eba, tgt, res);
				//�A�N�V�������s���ɓ���
				setStage(Stage.EXECUTING_ACTION, "UPDATE");
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
