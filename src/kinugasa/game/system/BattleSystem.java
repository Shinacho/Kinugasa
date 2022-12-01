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

	//�^�[����
	private int turn = 0;
	//--------------------------------------------------------�������E�I����
	//�v���C���퓬�J�n�O�ʒu
	private List<Point2D.Float> partyInitialLocation = new ArrayList<>();
	//�v���C���퓬�J�n�O����
	private List<FourDirection> partyInitialDir = new ArrayList<>();
	//�v���C�������ړ��ڕW���W
	private List<Point2D.Float> partyTargetLocationForFirstMove = new ArrayList<>();
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
	private List<BattleActionAnimation> animation = new ArrayList<>();
	//���s���o�g���A�N�V�������琶�����ꂽ�A�N�V�����ҋ@����
	private FrameTimeCounter currentBAWaitTime;
	//�s�����R�}���h
	private BattleCommand currentCmd;
	//ActionMessage�\������
	private int messageWaitTime = 66;

	//update���\�b�h�X�e�[�W
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
	//--------------------------------------------------------�V�X�e��
	//���b�Z�[�W�E�C���h�E�V�X�e���̃C���X�^���X
	private BattleMessageWindowSystem messageWindowSystem;
	//�^�[�Q�b�g�I���V�X�e���̃C���X�^���X
	private BattleTargetSystem targetSystem;
	//�o�g���t�B�[���h�C���X�^���X
	private BattleFieldSystem battleFieldSystem;
	//��Ԉُ�}�l�[�W��
	private ConditionManager conditionManager;
	//�퓬����
	private BattleResultValues battleResultValue = null;
	//�J�����gBA��NPC�c�ړ��|�C���g
	private int remMovePoint;

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
		//BGM�̊J�n
		SoundStorage.getInstance().get(es.getBgmMapName()).stopAll();
		es.getBgm().load().stopAndPlay();
		//�G�擾
		enemies = es.create();
		ess.dispose();
		//������
		GameSystem gs = GameSystem.getInstance();
		battleFieldSystem = BattleFieldSystem.getInstance();
		battleFieldSystem.init(enc.getChipAttribute());
		targetSystem = BattleTargetSystem.getInstance();
		targetSystem.init(gs.getParty(), enemies);
		messageWindowSystem = BattleMessageWindowSystem.getInstance();
		messageWindowSystem.init(gs.getPartyStatus());
		conditionManager = ConditionManager.getInstance();

		//�o��MSG�ݒ�p�}�b�v
		Map<String, Long> enemyNum
				= enemies.stream().collect(Collectors.groupingBy(Enemy::getId, Collectors.counting()));
		//�o��MSG�ݒ�
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Long> e : enemyNum.entrySet()) {
			sb.append(e.getKey()).append(I18N.translate("WAS")).append(e.getValue()).append(I18N.translate("APPEARANCE")).append(Text.getLineSep());
		}
		messageWindowSystem.setActionMessage(sb.toString(), Integer.MAX_VALUE);

		//���Z�b�g
		currentCmd = null;
		currentBAWaitTime = null;
		commandsOfThisTurn.clear();
		turn = 0;
		animation.clear();
		targetSystem.unsetPCsTarget();

		//�G�̔z�u
		putEnemy();

		//�����̔z�u
		putParty();
		assert partyTargetLocationForFirstMove.size() == gs.getParty().size() : "initial move target is missmatch";
		setStage(Stage.INITIAL_MOVE, "encountInit");
	}

	private void putParty() {
		GameSystem gs = GameSystem.getInstance();
		partyInitialDir.clear();
		partyInitialLocation.clear();
		partyTargetLocationForFirstMove.clear();

		//�����ʒu���������ޔ�
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
					BattleCommand bc = new MagicBattleCommand(s, s.getMagic());
					int idx = Random.randomAbsInt(commandsOfThisTurn.size());
					//���荞�܂������[�U�̒ʏ�A�N�V������j������
					BattleCommand remove = null;
					for (BattleCommand c : commandsOfThisTurn) {
						if (c.getUser().equals(bc.getUser())) {
							remove = c;
						}
					}
					if (remove != null) {
						commandsOfThisTurn.remove(remove);
					}
					//�폜���Ă��犄�荞�ݎ��s
					commandsOfThisTurn.add(idx, bc);
				}
				//�r�������X�g���炱�̃^�[���̃C�x���g���폜
				magics.remove(turn);
			}
		}
		updateCondition();

		//��Ԉُ�̌��ʎ��Ԃ�����
		enemies.stream().map(p -> p.getStatus()).forEach(p -> p.update());
		GameSystem.getInstance().getPartyStatus().forEach(p -> p.update());

		//���̃^�[���s���ۂ��R�}���h�ɐݒ�
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
		for (PlayerCharacter pc : GameSystem.getInstance().getParty()) {
			//�������㎀�S�A���S�����㓦����͂ł��Ȃ��̂ŁA����Ŗ��Ȃ��͂�
			if (pc.getStatus().hasCondition(BattleConfig.escapedConditionName)) {
				pc.getSprite().setVisible(true);
				//�������R���f�B�V�������O��
				pc.getStatus().removeCondition(BattleConfig.escapedConditionName);
			}
		}
		end = true;
	}

	//isEndBattle��endBattle�̏��ŌĂяo������
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

	//���̃R�}���h���擾�BNPC�܂���PC�BNPC�̏ꍇ�͎������s�B���@�r���C�x���g���������s�B
	public BattleCommand execCmd() {
		//���ׂẴR�}���h�����s�����玟�̃^�[�����J�n
		if (commandsOfThisTurn.isEmpty()) {
			turnStart();
		}
		currentCmd = commandsOfThisTurn.getFirst();
		commandsOfThisTurn.removeFirst();

		//�^�[�Q�b�g�V�X�e��������
		targetSystem.unsetPCsTarget();
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
		if (user.getStatus().hasCondition(BattleConfig.defenceConditionName)) {
			user.getStatus().removeCondition(BattleConfig.defenceConditionName);
		}
		if (user.getStatus().hasCondition(BattleConfig.avoidanceConditionName)) {
			user.getStatus().removeCondition(BattleConfig.avoidanceConditionName);
		}

		//���@�r�������C�x���g�̏ꍇ�APC�ł�NPC�ł��������s�A�i�r�����R���f�B�V�������O��
		if (currentCmd instanceof MagicBattleCommand) {
			BattleAction ba = currentCmd.getFirstBattleAction();
			//����ł̃^�[�Q�b�g���擾
			List<BattleCharacter> target = targetSystem.getMagicTarget(((MagicBattleCommand) currentCmd).getMagicSpell());
			//�^�[�Q�b�g�����Ȃ��ꍇ�A�r�����s�̃��b�Z�[�W�o��
			if (target.isEmpty()) {
				//�t�B�[���h�C�x���g�̏ꍇ�A�^�[�Q�b�g�������Ă��Ȃ����A�r������������
				if (!ba.isOnlyBatt(BattleActionTargetType.FIELD)) {
					//�A�j���[�V�����͒ǉ����Ȃ����A�r�����t���O�͊O��
					List<BattleActionResult> result = ba.exec(GameSystem.getInstance(), currentCmd.getUser(), target);
					setActionMessage(user, ba, target, result);
					setActionAnimation(user, ba, target, result);
					currentCmd.getUser().getStatus().removeCondition(BattleConfig.spellingConditionName);
					currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
					setStage(Stage.ESCAPING, "getNextCmdAndExecNPCCmd");
					return currentCmd;
				}
				//�^�[�Q�b�g�s��
				//�A�j���[�V�����͒ǉ����Ȃ����A�r�����t���O�͊O��
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
			//�����MP�ŉr���ł��邩�m�F
			//�Ή����x�����Ȃ��ꍇ�A��U�肳����
			Map<StatusKey, Integer> damage = ba.selfDamage(user.getStatus());
			//�_���[�W�����Z
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//�_���[�W�������āA0�̍��ڂ�����ꍇ�A�Ή����x�����Ȃ����ߋ�U��
			//���̖��@�̏���ڂ��擾
			List<StatusKey> shortageKey = new ArrayList<>();
			for (BattleActionEvent e : ba.getEvents().stream().filter(p -> p.getBatt() == BattleActionTargetType.SELF).collect(Collectors.toList())) {
				shortageKey.add(StatusKeyStorage.getInstance().get(e.getTargetName()));
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
			//�r������������A�j���[�V������ǉ�����EXEC_ACTION�ɓ���
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

		//��Ԉُ�œ����Ȃ��Ƃ��X�L�b�v�i���b�Z�[�W�͏o��
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

		//�����œ����Ȃ��Ƃ��́A��~�܂��̓o�g���A�N�V������K���Ɏ擾���Ď������s����
		if (currentCmd.isConfu()) {
			if (Random.percent(BattleConfig.conguStopP)) {
				//�����Ȃ�
				currentBAWaitTime = new FrameTimeCounter(messageWaitTime);
				StringBuilder s = new StringBuilder();
				s.append(currentCmd.getUser().getStatus().getName());
				s.append(I18N.translate("IS"));
				s.append(I18N.translate("CONFU_STOP"));
				messageWindowSystem.setActionMessage(s.toString(), messageWaitTime);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			} else {
				//�����邪����
				BattleAction ba = currentCmd.getRandom();
				currentBAWaitTime = ba.createWaitTime();
				execAction(ba, false);
				setStage(Stage.EXECUTING_ACTION, "getNextCmdAndExecNPCCmd");
				return currentCmd;
			}
		}

		//NPC
		if (currentCmd.getMode() == BattleCommand.Mode.CPU) {
			//NPC�A�N�V���������s�A���̒��ŃX�e�[�W���ς��
			messageWindowSystem.closeCommandWindow();
			execAction(currentCmd.getNPCActionExMove(), false);
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

		//�^�[�Q�b�g�V�X�e���������I���ŏ�����
		targetSystem.setPCsTarget(messageWindowSystem.getCommandWindow().getSelected(), currentCmd.getUser());

		//���[�U�I�y���[�V�����v�ۃt���O��ON�ɐݒ�
		currentCmd.setUserOperation(true);

		if (GameSystem.isDebugMode()) {
			System.out.println("getNextCmdAndExecNPCCmd : return PC_CMD" + currentCmd);
		}
		setStage(Stage.WAITING_USER_CMD, "getNextCmdAndExecNPCCmd");
		return currentCmd;
	}

	public ActionResult execPCAction() {
		//�R�}���h�E�C���h�E�܂��͈ړ���U���E�C���h�E����A�N�V�������擾
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
		//PC,NPC��킸�I�����ꂽ�A�N�V���������s����B

		//���b�Z�[�W�E�C���h�E��A�j���[�V�����̏������s��
		BattleCharacter user = currentCmd.getUser();
		//�^�[�Q�b�g�V�X�e������^�[�Q�b�g���擾
		List<BattleCharacter> target;
		if (currentCmd instanceof MagicBattleCommand) {
			//�}�����ꂽ���@�C�x���g�̏ꍇ�͎����őΏۂ��擾
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
		//PC�̏ꍇ�͓���ȃA�N�V�����ɓ���
		if (currentCmd.isUserOperation() & !targetSystemCalled) {
			//�m��
			if (ba.getName().equals(BattleConfig.ActionName.commit)) {
				user.unsetTarget();
				return SUCCESS;
			}
			//�ړ�
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
			//�A�C�e���g�p
			if (ba.getName().equals(BattleConfig.ActionName.itemUse)) {
				//�A�C�e���o�b�O����̂Ƃ��̓��b�Z�[�W�o���ďI��
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
			//�h��
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
			//���
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
			//���
			if (ba.getName().equals(BattleConfig.ActionName.status)) {
				return ActionResult.SHOW_STATUS;
			}
			//������
			if (ba.getName().equals(BattleConfig.ActionName.escape)) {
				//������R�}���h�̐��۔���
				//�O��Ƃ��āA�ړ��|�C���g���ɋ��E�ȂȂ���΂Ȃ�Ȃ�
				Point2D.Float w, e;
				int movPoint = (int) user.getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue();
				e = (Point2D.Float) user.getSprite().getCenter().clone();
				e.x += movPoint;
				w = (Point2D.Float) user.getSprite().getCenter().clone();
				w.x -= movPoint;
				if (!battleFieldSystem.getBattleFieldAllArea().contains(e)) {
					//���������i���j
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
					//���������i���j
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
				//�������Ȃ�
				messageWindowSystem.setInfoMessage(I18N.translate("CANT_ESCAPE"));
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return ActionResult.MISS;
			}
			//�U���Ɠ���U���Ɩ��@�E�E�E�^�[�Q�b�g������΃^�[�Q�b�g�I���ɓ���
			if (ba.getBattleActionType() == BattleActionType.ATTACK
					|| ba.getBattleActionType() == BattleActionType.SPECIAL_ATTACK
					|| ba.getBattleActionType() == BattleActionType.MAGIC) {
				if (target.isEmpty() || (target.size() == 1 && target.get(0) == user)) {
					//�^�[�Q�b�g�Ȃ�				
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
			//���̑��s���͎����G���[
			if (ba.getBattleActionType() != BattleActionType.MAGIC) {
				throw new GameSystemException("undefined PCs action : " + ba);
			}
		}

		//���@�r���̏ꍇ�͖��@�r�������X�g�ɒǉ����ă��b�Z�[�W���o���ďI��
		if (ba.getBattleActionType() == BattleActionType.MAGIC) {
			//�Ή����x�����Ȃ��ꍇ�A��U�肳����
			Map<StatusKey, Integer> damage = ba.selfDamage(user.getStatus());
			//�_���[�W�����Z
			StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
			//�_���[�W�������āA0�̍��ڂ�����ꍇ�A�Ή����x�����Ȃ����ߋ�U��
			//���̖��@�̏���ڂ��擾
			List<StatusKey> shortageKey = new ArrayList<>();
			for (BattleActionEvent e : ba.getEvents().stream().filter(p -> p.getBatt() == BattleActionTargetType.SELF).collect(Collectors.toList())) {
				shortageKey.add(StatusKeyStorage.getInstance().get(e.getTargetName()));
			}
			if (!damage.isEmpty() && simulateDamage.isZero(false, shortageKey)) {
				//�Ώۍ��ڂ�1�ł�0�̍��ڂ����������U��
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
			//SELF�̂݃A�N�V�����̏ꍇ�͎��s�\

			//�^�[�Q�b�g�����Ȃ��ꍇ�ŁANPC�ňړ��\�ȏꍇ�͈ړ����s
			if (!user.isPlayer() && (target.isEmpty() || (target.size() == 1 && target.get(0).equals(user)))) {
				//�ړ��A�N�V�����������Ă���ꍇ�ړ����s
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
					//�ړ��A�N�V�����������Ă��炸�A�^�[�Q�b�g�����Ȃ��ꍇ�͉������Ȃ�
					return ActionResult.SUCCESS;
				}
			}
			//�r������0�^�[���̏ꍇ�Ń^�[�Q�b�g������ꍇ�͑������s
			if (ba.getSpellTime() == 0 && !target.isEmpty()) {
				//�^�[�Q�b�g����
				List<BattleActionResult> result = ba.exec(GameSystem.getInstance(), user, target);
				updateCondition();
				//���b�Z�[�W�E�C���h�E�ݒ�
				setActionMessage(user, ba, target, result);
				//�A�j���[�V�����ݒ�
				setActionAnimation(user, ba, target, result);
				//�A�j���[�V�����҂��ɑJ��
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
			//�r���\�Ȃ̂ŁA�^�[�Q�b�g�I����
			int t = ba.getSpellTime() + turn;
			addSpelling(user, ba, t);
			//�r�����R���f�B�V������ǉ�
			user.getStatus().addCondition(BattleConfig.spellingConditionName);
			//�r���������b�Z�[�W��ݒ�
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
		//���t�B�[���h�C�x���g�̏ꍇ�A�^�[�Q�b�g�͋�ŋA���Ă���

		//�^�[�Q�b�g�s�݂̔���
		//NPC�A�N�V�����Ń^�[�Q�b�g�s�݂̏ꍇ
		if (!user.isPlayer() && target.isEmpty() || (target.size() == 1 && target.get(0).equals(user))) {
			//�ړ��A�N�V�����������Ă���ꍇ�ړ����s
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
			//�ړ��A�N�V�����������Ă��Ȃ��ꍇ�͉��������I��
			return ActionResult.NO_TARGET;
		}

		if (target.isEmpty() || (target.size() == 1 && target.get(0).equals(user))) {
			//�^�[�Q�b�g�s�݂ŋ�U��
			//�t�B�[���h�C�x���g���܂�ł���ꍇ�̓^�[�Q�b�g�s�݂ł�OK
			if (!ba.hasBatt(BattleActionTargetType.FIELD)) {
				messageWindowSystem.setInfoMessage(I18N.translate("NO_TARGET"));
				messageWindowSystem.closeAfterMoveCommandWindow();
				messageWindowSystem.closeActionWindow();
				messageWindowSystem.closeTooltipWindow();
				setStage(Stage.SHOW_INFO_MSG, "execAction");
				return ActionResult.NO_TARGET;
			}
		}
		//�^�[�Q�b�g����
		List<BattleActionResult> result = ba.exec(GameSystem.getInstance(), user, target);
		updateCondition();
		//���b�Z�[�W�E�C���h�E�ݒ�
		setActionMessage(user, ba, target, result);
		//�A�j���[�V�����ݒ�
		setActionAnimation(user, ba, target, result);
		//�A�j���[�V�����҂��ɑJ��
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

	//result�Ɋ�Â����b�Z�[�W���A�N�V�����E�C���h�E�ɐݒ肷��
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

		//���@�ŁASELF�݂̂���Ȃ��̂Ƀ^�[�Q�b�gSELF�݂̂̏ꍇ�A�^�[�Q�b�g�Ȃ���\��
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
				//�t�B�[���h�̏ꍇ�A�t�B�[���h�Ɍ��ʂ�^�����|��\��
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
				//����ȊO�̏ꍇ�̓��b�Z�[�W��ݒ�
				for (int i = 0, j = 0, line = 0; i < target.size(); i++, j++) {
					if (target.get(i).equals(currentCmd.getUser())) {
						//SELF�͕\�����Ȃ�
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

	//result�Ɋ�Â��A�j���[�V������this�ɒǉ�����
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
						//�A�C�e���ǉ����Ȃ�
						break;
					default:
						throw new AssertionError();
				}
			} else {
				for (BattleCharacter c : target) {
					//SELF�͕\�����Ȃ�
					if (c.equals(user)) {
						continue;
					}
					//res�ɂ��A�j���[�V������ǉ�
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
							//�A�C�e���ǉ����Ȃ�
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
			int exp = enemies.stream().mapToInt(p -> (int) p.getStatus().getEffectedStatus().get(BattleConfig.expStatisKey).getValue()).sum();
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
					//�S����������A�S���������ꍇ�A�퓬�I��
					if (party.stream().allMatch(p -> p.hasCondition(BattleConfig.escapedConditionName))) {
						//�S��������
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
				//�v���C���[�̍s���܂��Ȃ̂ŁA�������Ȃ��B
				//�R�}���h�E�C���h�E�����珈�������s�����
				//���Ƀo�g���R�}���h���擾�����Ƃ��ANPC�Ȃ�NPC�̍s���̃X�e�[�W�ɓ���B
				break;
			case SHOW_ACTION_MESSAGE:
				//�A�N�V�����E�C���h�E��������܂ő҂�
				if (!messageWindowSystem.isVisibleActionMessage()) {
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case EXECUTING_ACTION:
				//�J�����gBATime���؂��܂ő҂�
				assert currentBAWaitTime != null : "EXECITING_ACTION buf tc is null";
				if (currentBAWaitTime.isReaching()) {
					messageWindowSystem.closeActionWindow();
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
				if (remMovePoint < currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue()) {
					return;
				}
				//�A�N�V�����𒊑I
				EnemyBattleAction eba = currentCmd.getNPCActionOf(BattleActionType.ATTACK);
				if (eba == null) {
					if (GameSystem.isDebugMode()) {
						System.out.println("enemy " + currentCmd.getUser().getStatus().getName() + " try afterMoveAttack, but dont have attackCMD");
					}
					return;
				}
				//batt=FIELD�̏ꍇ�A�������s
				if (eba.getEvents().stream().allMatch(p -> p.getBatt() == BattleActionTargetType.FIELD)) {
					//�������s
					List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser());
					updateCondition();
					currentBAWaitTime = eba.createWaitTime();
					//���b�Z�[�W�\��
					setActionMessage(currentCmd.getUser(), eba, Collections.emptyList(), result);
					//�A�j���[�V�����ǉ�
					setActionAnimation(currentCmd.getUser(), eba, Collections.emptyList(), result);
					setStage(Stage.EXECUTING_ACTION, "UPDATE");
					return;
				}
				//batt==SELF�̏ꍇ�A�������s
				if (eba.getEvents().stream().allMatch(p -> p.getBatt() == BattleActionTargetType.SELF)) {
					//�������s
					List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser());
					updateCondition();
					currentBAWaitTime = eba.createWaitTime();
					//���b�Z�[�W�\��
					setActionMessage(currentCmd.getUser(), eba, Arrays.asList(currentCmd.getUser()), result);
					//�A�j���[�V�����ǉ�
					setActionAnimation(currentCmd.getUser(), eba, Arrays.asList(currentCmd.getUser()), result);
					setStage(Stage.EXECUTING_ACTION, "UPDATE");
					return;
				}

				// �C�x���g�ΏێҕʂɃ^�[�Q�b�g��ݒ�
				List<BattleCharacter> target = getTargetOfCurrentCmd(eba);
				if (target.isEmpty()) {
					return;
				}
				//�U�����s
				List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser(), target);
				updateCondition();
				currentBAWaitTime = eba.createWaitTime();
				//���b�Z�[�W�\��
				setActionMessage(currentCmd.getUser(), eba, target, result);
				//�A�j���[�V�����ǉ�
				setActionAnimation(currentCmd.getUser(), eba, target, result);
				setStage(Stage.EXECUTING_ACTION, "UPDATE");
				break;
			case SHOW_INFO_MSG:
				if (!messageWindowSystem.isVisibleInfoMessage()) {
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case BATLE_END:
				//�������Ȃ��i���[�U����҂�
				break;
			default:
				throw new AssertionError("UNDEFINED STAGE");

		}
	}

	private List<BattleCharacter> getTargetOfCurrentCmd(EnemyBattleAction eba) {
		List<BattleCharacter> target = new ArrayList<>();
		//�G���A�͑I�����ꂽ�A�N�V�������ړ��|�C���g�̏������ق�
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
