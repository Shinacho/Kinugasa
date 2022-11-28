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
import java.util.EnumMap;
import java.util.EnumSet;
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.system.AfterMoveCommandMessageWindow;
import kinugasa.game.system.BattleAction;
import kinugasa.game.system.BattleActionStorage;
import kinugasa.game.system.BattleActionTargetParameterType;
import kinugasa.game.system.BattleActionTargetType;
import kinugasa.game.system.BattleActionType;
import kinugasa.game.system.BattleCharacter;
import kinugasa.game.system.BattleCommand;
import kinugasa.game.system.BattleCommandMessageWindow;
import kinugasa.game.system.BattleFieldSystem;
import kinugasa.game.system.BattleResult;
import kinugasa.game.system.BattleResultValues;
import kinugasa.game.system.BattleSystem;
import kinugasa.game.system.BattleTargetSystem;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.MagicBattleCommand;
import kinugasa.game.ui.Text;
import kinugasa.object.FourDirection;
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
		c5 = new SoundBuilder("resource/se/���ʉ��Q�I��1.wav").builde().load();
		c6 = new SoundBuilder("resource/se/���ʉ��Q�I��2.wav").builde().load();
		turnStart = new SoundBuilder("resource/se/���ʉ��Q�o�g���^�[���J�n.wav").builde().load();
	}

	@Override
	public void dispose() {
	}

	private int stage = 0;
	private BattleSystem battleSystem;
	private Point2D.Float playerMoveInitialLocation;
	private Sound c5, c6, turnStart;
	private int remMovPoint;

	@Override
	public void update(GameTimeManager gtm) {
		battleSystem.update();
		InputState is = InputState.getInstance();

		//�ً}�E�o�{�^��
		if (is.isPressed(GamePadButton.BACK, InputType.SINGLE)) {
			battleSystem.setBattleResultValue(new BattleResultValues(BattleResult.WIN, 123, new ArrayList<>(), "FIELD"));
			BattleResultValues result = GameSystem.getInstance().battleEnd();
			System.out.println("�퓬�����I���F" + result);
			gls.changeTo("FIELD");
		}

		switch (stage) {
			case -1:
				//��Ԃ��ҋ@����INFO�̕\�����I���܂ő҂�
				if (!battleSystem.getMessageWindowSystem().isClosedInfoWindow()) {
					break;
				}
				if (battleSystem.getStage() != BattleSystem.Stage.WAITING_USER_CMD) {
					break;
				}
				stage = 5;
				break;
			case 0:
				//��Ԃ��ҋ@�ɂȂ�܂ő҂�
				if (battleSystem.getStage() != BattleSystem.Stage.WAITING_USER_CMD) {
					break;
				}
				stage = 1;
				break;
			case 1:
				//�R�}���h����
				BattleCommand bc = battleSystem.getNextCmdAndExecNPCCmd();
				if (bc.getMode() == BattleCommand.Mode.PC) {
					if (!battleSystem.isCantMove()) {
						//���@�r�������œ����Ȃ��Ȃ��ꍇ�i��������ꍇ
						turnStart.stopAndPlay();
						battleSystem.getMessageWindowSystem().getCommandWindow().resetSelect();
						BattleAction ba = battleSystem.getMessageWindowSystem().getCommandWindow().getSelected();
						battleSystem.setBattleAction(ba, ba.getAreaWithEqip(battleSystem.getCurrentCmd().getUser().getStatus()));
						battleSystem.getTargetSystem().unset();
						stage = 2;
						break;
					}
					if (bc instanceof MagicBattleCommand) {
						stage = 0;
					}
				}
				stage = 0;
				break;
			case 2:
				//�v���C���[����
				if (!battleSystem.getMessageWindowSystem().isClosedInfoWindow()) {
					break;
				}
				BattleCommandMessageWindow mw = battleSystem.getMessageWindowSystem().getCommandWindow();
				int baa = mw.isSelected("�ړ�")
						? (int) battleSystem.getCurrentCmd().getUser().getStatus().getEffectedStatus().get("MOV").getValue()
						: mw.getSelected().getBattleActionType() == BattleActionType.OTHER
						? 0
						: battleSystem.calcArea(mw.getSelected());
				battleSystem.setBattleAction(mw.getSelected(), baa);
				//�I��
				if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw.prevType();
				} else if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw.nextType();
				}
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw.prevAction();
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw.nextAction();
				}
				//�틵�}
				if (is.isPressed(GamePadButton.LB, InputType.CONTINUE)) {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(false);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(false);
				} else {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(true);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(true);
				}

				//����
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					//�^�[�Q�b�g�I���܂��͈ړ��֐ؑ�
					c5.stopAndPlay();
					battleSystem.toTargetSelectOrPCMoveMode();
					stage = 3;
				}

				break;
			case 3:
				//�^�[�Q�b�g�I���܂��͈ړ��܂��͈ړ��m�肩�𔻒�i1�񂾂����s
				BattleAction selected = battleSystem.getCurrentBA();
				if (selected.isOnlyBatpt(BattleActionTargetParameterType.MOVE)) {
					//�ړ��̏ꍇ
					playerMoveInitialLocation = battleSystem.getCurrentCmd().getUser().getSprite().getCenter();
					stage = 4;
				} else if (selected.isOnlyBatpt(BattleActionTargetParameterType.NONE)) {
					//�ړ��m��
					stage = 1;
				} else {
					//�͈͌��ʓ��ɑΏۂ����邩�m�F�A���Ȃ��ꍇ�̓X�e�[�W2�ɖ߂�
					if (!battleSystem.getTargetSystem().hasAnyTarget()) {
						battleSystem.setNoTargetMessage();
						if (battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow().isVisible()) {
							stage = 6;
						} else {
							stage = 2;
						}
					} else {
						//�G������ꍇ�̓^�[�Q�b�g�I����
//						battleSystem.getMessageWindowSystem().setActionMessage(selected.getName() + Text.getLineSep() + "  " + selected.getDesc());
						stage = 5;
					}
				}
				break;
			case 4:
				//�ړ����[�h
				assert playerMoveInitialLocation != null : "battle logic, player move initial location is null";
				BattleCharacter playerChara = battleSystem.getCurrentCmd().getUser();

				//�틵�}
				if (is.isPressed(GamePadButton.LB, InputType.CONTINUE)) {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(false);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(false);
					battleSystem.getMessageWindowSystem().getTooltipWindow().setVisible(false);
				} else {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(true);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(true);
					battleSystem.getMessageWindowSystem().getTooltipWindow().setVisible(true);
				}
				//�L�����Z��
				if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
					c5.stopAndPlay();
					battleSystem.getMessageWindowSystem().closeTooltipWindow();
					battleSystem.setRemMovPoint(0);
					playerChara.getSprite().setLocationByCenter(playerMoveInitialLocation);
					battleSystem.getMessageWindowSystem().closeActionWindow();
					playerChara.to(FourDirection.WEST);
					stage = 2;
					break;
				}
//				//�m��
//				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
//					c5.stopAndPlay();
//					battleSystem.getMessageWindowSystem().closeTooltipWindow();
//					battleSystem.submitPlayerMove(true);
//					playerChara.to(FourDirection.WEST);
//					stage = 1;
//					break;
//				}

				//�ړ���U���̔���
				remMovPoint = (int) (playerChara.getStatus().getEffectedStatus().get("MOV").getValue()
						- playerMoveInitialLocation.distance(battleSystem.getCurrentCmd().getUser().getSprite().getCenter()));
				battleSystem.setRemMovPoint(Math.min(remMovPoint, battleSystem.getCurrentBAArea()));
				if (remMovPoint > 0) {
					battleSystem.getMessageWindowSystem().setTolltipMessage("�ʏ�U���\");
				} else {
					battleSystem.getMessageWindowSystem().setTolltipMessage("�ʏ�U���s��");
				}
				if (is.isPressed(GamePadButton.A, InputType.SINGLE) && remMovPoint != 0) {
					//�ړ���U���I���֑J��
					battleSystem.getMessageWindowSystem().closeTooltipWindow();
					battleSystem.getMessageWindowSystem().closeCommandWindow();
					battleSystem.submitPlayerMove();
					battleSystem.afterMoveAttackMode("�m��", remMovPoint);
					battleSystem.getCurrentCmd().getUser().to(FourDirection.WEST);
					stage = 6;
					break;
				}
				//�V�~�����[�g���[�u�N�����Ď��t���[���̈ʒu�擾
				KVector v = new KVector(is.getGamePadState().sticks.LEFT.getLocation(VehicleStorage.getInstance().get("WALK").getSpeed()));
				if (v.getSpeed() <= 0) {
					break;
				}
				Point2D.Float nextFrameLocation = playerChara.getSprite().simulateMoveCenterLocation(v);
				//�̈攻��
				if (!battleSystem.getBattleFieldSystem().getBattleFieldAllArea().contains(nextFrameLocation)) {
					break;
				}
				//��Q������
				if (battleSystem.getBattleFieldSystem().hitObstacle(nextFrameLocation)) {
					break;
				}
				//��������
				if (playerChara.getStatus().getEffectedStatus().get("MOV").getValue() <= playerMoveInitialLocation.distance(nextFrameLocation)) {
					break;
				}
				//�ړ����s
				playerChara.getSprite().setVector(v);
				playerChara.getSprite().move();
				playerChara.to(playerChara.getSprite().getVector().round());

				break;
			case 5:
				//�^�[�Q�b�g�I�����[�h
				//�^�[�Q�b�g�I���V�X�e���́AtoTargetSelectOrPCMoveMode�����s�������_�ŁA�I�������A�N�V�������ݒ肳��Ă���
				//�^�[�Q�b�g�I���ł���̂́AONE_ENEMY/ONE_PARTY�C�x���g�������Ă���ꍇ�̂�
				BattleTargetSystem targetSystem = battleSystem.getTargetSystem();
				if (targetSystem.contains(BattleActionTargetType.ONE_ENEMY) || targetSystem.contains(BattleActionTargetType.ONE_PARTY)) {
					//�^�[�Q�b�g�I��
					if (is.isPressed(GamePadButton.POV_LEFT, InputType.SINGLE)) {
						c6.stopAndPlay();
						targetSystem.prev();
					} else if (is.isPressed(GamePadButton.POV_RIGHT, InputType.SINGLE)) {
						c6.stopAndPlay();
						targetSystem.next();
					}
				}

				//�틵�}
				if (is.isPressed(GamePadButton.LB, InputType.CONTINUE)) {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(false);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(false);
				} else {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(true);
					battleSystem.getMessageWindowSystem().getCommandWindow().setVisible(true);
				}

				//�L�����Z��
				if (is.isPressed(GamePadButton.B, InputType.SINGLE)) {
					c5.stopAndPlay();
					battleSystem.updatePlayerLocation();
					battleSystem.getMessageWindowSystem().closeActionWindow();
					stage = 2;
					break;
				}
				//����
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					c5.stopAndPlay();
					battleSystem.execPCAction();
					battleSystem.getMessageWindowSystem().getActionWindow().setVisible(true);
					stage = 0;
					break;
				}
				break;
			case 6:
				//�ړ���U���I��

				//�R�}���h�I��
				AfterMoveCommandMessageWindow mw2 = battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow();
				if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw2.prevAction();
					if (mw2.getSelected().getName().equals("�m��")) {
						battleSystem.setBattleAction(mw2.getSelected(), 0);
					} else {
						int area = Math.min(mw2.getSelected().getAreaWithEqip(battleSystem.getCurrentCmd().getUser().getStatus()), remMovPoint);
						battleSystem.setBattleAction(mw2.getSelected(), area);
					}
				} else if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
					c6.stopAndPlay();
					mw2.nextAction();
					if (mw2.getSelected().getName().equals("�m��")) {
						battleSystem.setBattleAction(mw2.getSelected(), 0);
					} else {
						int area = Math.min(mw2.getSelected().getAreaWithEqip(battleSystem.getCurrentCmd().getUser().getStatus()), remMovPoint);
						battleSystem.setBattleAction(mw2.getSelected(), area);
					}
				}
				//�틵�}
				if (is.isPressed(GamePadButton.LB, InputType.CONTINUE)) {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(false);
					battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow().setVisible(false);
				} else {
					battleSystem.getMessageWindowSystem().getStatusWindows().setVisible(true);
					battleSystem.getMessageWindowSystem().getAfterMoveCommandWindow().setVisible(true);
				}

				//����
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					//�^�[�Q�b�g�I���܂��͎��̃L�����̈ړ���
					c5.stopAndPlay();
					battleSystem.toTargetSelectOrPCMoveEnd();
					stage = 3;
				}
				break;

			default:
				throw new AssertionError("undefined battle logic stage");
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		battleSystem.draw(g);
	}

}
