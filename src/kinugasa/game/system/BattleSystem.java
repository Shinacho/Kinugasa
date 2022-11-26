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
	//�\�����o�g���A�N�V�����E�A�j���[�V����
	private List<BattleActionAnimation> animation = new ArrayList<>();
	//���s���o�g���A�N�V�������琶�����ꂽ�ҋ@����
	private FrameTimeCounter currentBAWaitTime;
	//���s���o�g���A�N�V����
	private BattleAction currentBA;
	//���s���o�g���A�N�V�����̃G���A
	private int currentBAArea;
	//�s�����R�}���h
	private BattleCommand currentCmd;
	//�s�����G�l�~�[�̎c�s����
	private int enemyMovPoint;
	//PC�̍s���͈́^�U���͈͂̓_�Ő���
	private FrameTimeCounter playerAreaBlinkCounter = new FrameTimeCounter(12);
	private boolean playerAreaVisible = false;

	//update���\�b�h�X�e�[�W
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

		//���Z�b�g
		currentCmd = null;
		currentBA = null;
		currentBAWaitTime = null;
		currentBAArea = 0;
		commandsOfThisTurn.clear();
		turn = 0;
		animation.clear();
		playerAreaBlinkCounter = new FrameTimeCounter(12);
		playerAreaVisible = false;

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
		//���̃^�[���̃o�g���R�}���h���쐬
		List<BattleCharacter> list = getAllChara();
		SpeedCalcModelStorage.getInstance().getCurrent().sort(list);

		//�s�����Ƀo�g���R�}���h���i�[
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
		//�����̔z�u�̏�����
		List<PlayerCharacterSprite> partySprite = GameSystem.getInstance().getPartySprite();
		for (int i = 0; i < partySprite.size(); i++) {
			partySprite.get(i).to(partyInitialDir.get(i));
			partySprite.get(i).setLocation(partyInitialLocation.get(i));
		}
		//�G�ԍ��̏�����
		EnemyBlueprint.initEnemyNoMap();
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

	//���̃R�}���h���擾�BNPC�܂���PC�B
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
			//NPC�A�N�V���������s�A���̒��ŃX�e�[�W���ς��
			execNPCAction();
			messageWindowSystem.closeCommandWindow();
			return currentCmd;
		}
		//PC
		//�J�����g�X�V�R�}���h���e�����b�Z�[�W�E�C���h�E�ɕ\��
		messageWindowSystem.setCommand(currentCmd);
		currentBA = messageWindowSystem.getCommandWindow().getSelected();//�����I�����擾
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
		//�A�T�[�V����
		//���������J�����g�������Ă��邱��
		assert currentCmd != null : "execNPCAction but currentCmd is null";

		//�J�����g�R�}���h��NPC�ł��邱��
		if (currentCmd.getMode() == BattleCommand.Mode.PC) {
			throw new GameSystemException("execNpcAction but cmd is PC");
		}
		//���s���o�g���A�N�V�����Ƃ��̃G���A��ݒ�
		EnemyBattleAction eba = currentCmd.getNPCActionExMove();
		currentBAArea = currentCmd.getAreaWithEqip(eba.getName());
		currentBA = eba;
		if (GameSystem.isDebugMode()) {
			System.out.println("execNPCAction : selected=" + eba);
		}

		//SELF�A�N�V�����̂ݏꍇ�͑������s���ďI��
		if (eba.getTargetTypeList().stream().allMatch(p -> p == BattleActionTargetType.SELF)) {
			currentBAWaitTime = eba.createWaitTime();
			List<BattleActionResult> result = eba.exec(GameSystem.getInstance(), currentCmd.getUser());
			//���b�Z�[�W�\��
			addMessage(eba, Arrays.asList(currentCmd.getUser()), result);
			//�A�j���[�V�����ǉ�
			addAnimation(eba, Arrays.asList(currentCmd.getUser()), result);
			setStage(Stage.EXECUTING_ACTION, "execNPCAction");
			return;
		}
		// SELF�݂̂łȂ��A�N�V�����̏ꍇ�͍U���͈͓��ɑΏێ҂����邩�m�F
		enemyMovPoint = (int) currentCmd.getUser().getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue();
		List<BattleCharacter> targetList = getTargetOfCurrentCmd(eba);

		//���Ȃ��ꍇ�ŁA�s���A�N�V�����������Ă���ꍇ�͍s�������s
		if (targetList.isEmpty() && currentCmd.hasMoveAction()) {
			//�ړ����[�h�ɂ��ďI��
			//��ԋ߂��G������
			BattleCharacter nearTarget = targetSystem.nearPlayer(currentCmd.getUser().getSprite().getCenter());
			//�^�[�Q�b�g���W�ݒ�
			currentCmd.getUser().setTargetLocation(nearTarget.getSprite().getCenter(), currentBAArea);
			//���b�Z�[�W�\��
			addMoveMessage();
			//�A�j���[�V�����͂Ȃ�
			setStage(Stage.EXECUTING_MOVE, "execNPCAction");
			return;
		}
		//�^�[�Q�b�g�����Ȃ��ꍇ�ŁA�ړ��A�N�V�����������Ă��Ȃ��ꍇ�͉������Ȃ�
		if (targetList.isEmpty()) {
			setStage(Stage.EXECUTING_MOVE, "execNPCAction");
			return;
		}
		//�^�[�Q�b�g������ꍇ�A�A�N�V���������s
		currentBAWaitTime = eba.createWaitTime();
		List<BattleActionResult> result = eba.exec(GameSystem.getInstance(), currentCmd.getUser());
		//���b�Z�[�W�\��
		addMessage(eba, targetList, result);
		//�A�j���[�V�����ǉ�
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
			//�^�[�Q�b�g�ɃA�j���[�V�����̕�����z�u
			if (e.getBatt() == BattleActionTargetType.FIELD) {
				BattleActionAnimation a = e.getAnimationClone();
				switch (a.getAnimationTargetType()) {
					case BATTLE_FIELD_AREA:
						//�t�B�[���h�S�̂�ΏۂƂ���
						a.getAnimationSprite().setLocation(battleFieldSystem.getBattleFieldAllArea());
						animation.add(a);
						break;
					case TEAM_AREA:
						if (ba instanceof EnemyBattleAction) {
							//�G�̃R�}���h�Ȃ̂ŁA�����̗̈�ɐݒ�
							float x = battleFieldSystem.getEnemytArea().x;
							float y = battleFieldSystem.getEnemytArea().y;
							a.getAnimationSprite().setLocation(x, y);
							animation.add(a);
						} else {
							//�����̃R�}���h�Ȃ̂œG�̗̈�ɐݒ�
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
				//SELF�C�x���g�Ȃ̂ŁA�Ώێ҂͎������g�Ƃ���
				BattleActionAnimation a = e.getAnimationClone();
				a.getAnimationSprite().setLocation(currentCmd.getUser().getSprite());
				animation.add(a);
				continue;
			}
			// �^�[�Q�b�g����̃A�j���[�V����
			assert !target.isEmpty() : "addAnimation, but target is empty";
			BattleActionAnimation a = e.getAnimationClone();
			for (BattleCharacter bc : target) {
				BattleActionAnimation ani = a.clone();
				ani.getAnimationSprite().setLocation(bc.getSprite());
				animation.add(ani);
			}
		}
	}

	//�J�����g�̃L�������ړ������|���A�N�V�������b�Z�[�W�E�C���h�E�ɕ\��
	private void addMoveMessage() {
		String text = currentCmd.getUser().getStatus().getName();
		text += I18N.translate("ISMOVE");
		getMessageWindowSystem().setActionMessage(text);
	}

	// �A�N�V�����Ɋ�Â��s�����ʂ��A�N�V�������b�Z�[�W�E�C���h�E�ɐݒ�
	private String addMessage(BattleAction ba, List<BattleCharacter> target, List<BattleActionResult> result) {
		StringBuilder s = new StringBuilder();
		//�C�x���g�ɑ΂��鐬�ۂ��܂Ƃ߂�
		assert ba.getEvents().size() == result.size() : "action event size is mismatch";
		Map<BattleActionEvent, BattleActionResult> actionResult = new HashMap<>();
		List<BattleActionEvent> events = ba.getEvents();
		for (int i = 0; i < result.size(); i++) {
			actionResult.put(events.get(i), result.get(i));
		}

		//�s��
		s.append(currentCmd.getUser().getStatus().getName());
		s.append(I18N.translate("S"));
		s.append(I18N.translate(ba.getBattleActionType().toString()));
		s.append("�u");
		s.append(ba.getName());
		s.append("�v");
		//���s��
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

		//�_���[�W
		//N�l�����s
		int lfN = 6;
		int i = 0;
		for (BattleCharacter c : target) {
			Map<StatusKey, Integer> damage = c.getStatus().calcDamage();
			//outputLogStatusKey�ւ̃_���[�W���Ȃ��ꍇ
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
		//�ړ����[�h�܂��̓^�[�Q�b�g�I���ɓ���
		
		
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

		//�X�e�[�^�X�ύX�ɂ���Ԉُ�̕t�^
		conditionManager.setCondition(GameSystem.getInstance().getPartyStatus());
		conditionManager.setCondition(enemies.stream().map(p -> p.getStatus()).collect(Collectors.toList()));
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
					//�m������
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
		removeEndAnimation();

		switch (stage) {
			case INITIAL_MOVE:
				//�v���C���[�L�����N�^�[���ڕW�̍��W�ɋ߂Â��܂ňړ������s�A�ړI�n�ɋ߂Â�����X�e�[�W��ς���
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
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
				}
				break;
			case WAITING_USER_CMD:
				//�v���C���[�̍s���܂��Ȃ̂ŁA�������Ȃ��B
				//�R�}���h�E�C���h�E�����珈�������s�����
				//���Ƀo�g���R�}���h���擾�����Ƃ��ANPC�Ȃ�NPC�̍s���̃X�e�[�W�ɓ���B
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
				//�ړ����s
				currentCmd.getUser().moveToTgt();
				enemyMovPoint--;
				//�ړ��|�C���g���؂ꂽ�ꍇ�A�ړ��I�����ă��[�U�R�}���h�҂��Ɉڍs
				if (enemyMovPoint <= 0 || !currentCmd.getUser().isMoving()) {
					currentCmd.getUser().unsetTarget();
					setStage(Stage.WAITING_USER_CMD, "UPDATE");
					return;
				}
				//�ړ��|�C���g���؂�Ă��Ȃ��ꍇ�A�Ώۂ����邩����
				//�A�N�V�����𒊑I
				EnemyBattleAction eba = currentCmd.getNPCActionExMove();
				//batt=FIELD�̏ꍇ�A�������s
				if (eba.getEvents().stream().allMatch(p -> p.getBatt() == BattleActionTargetType.FIELD)) {
					//�������s
					List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser());
					currentBAWaitTime = eba.createWaitTime();
					//���b�Z�[�W�\��
					addMessage(eba, Collections.emptyList(), result);
					//�A�j���[�V�����ǉ�
					addAnimation(eba, Collections.emptyList(), result);
					setStage(Stage.EXECUTING_ACTION, "UPDATE");
					return;
				}
				//batt==SELF�̏ꍇ�A�������s
				if (eba.getEvents().stream().allMatch(p -> p.getBatt() == BattleActionTargetType.SELF)) {
					//�������s
					List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser());
					currentBAWaitTime = eba.createWaitTime();
					//���b�Z�[�W�\��
					addMessage(currentBA, Arrays.asList(currentCmd.getUser()), result);
					//�A�j���[�V�����ǉ�
					addAnimation(currentBA, Arrays.asList(currentCmd.getUser()), result);
					setStage(Stage.EXECUTING_ACTION, "UPDATE");
					return;
				}

				// �C�x���g�ΏێҕʂɃ^�[�Q�b�g��ݒ�
				List<BattleCharacter> target = getTargetOfCurrentCmd(eba);
				if (target.isEmpty()) {
					//�s�����悤�Ƃ������A�Ώێ҂����Ȃ��ꍇ�I��
//					if (GameSystem.isDebugMode()) {
//						System.out.println("target is empty");
//					}
					return;
				}
				//�U�����s
				List<BattleActionResult> result = eba.exec(gs, currentCmd.getUser(), target);
				currentBAWaitTime = eba.createWaitTime();
				//���b�Z�[�W�\��
				addMessage(currentBA, target, result);
				//�A�j���[�V�����ǉ�
				addAnimation(currentBA, target, result);
				setStage(Stage.EXECUTING_ACTION, "UPDATE");

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
		//�v���C���[�L�����N�^�̍s���G���A��\��
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
