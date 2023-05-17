/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.object.EmptySprite;
import kinugasa.object.KVector;

/**
 *
 * @vesion 1.0.0 - 2022/12/02_16:46:35<br>
 * @author Shinacho<br>
 */
public enum StandardEnemyAI implements EnemyAI {
	SIMPLE {
		@Override
		public CmdAction getNext(BattleCharacter user, List<CmdAction> list) {
			assert user.isPlayer() == false : "ENEMY AI but user is not CPU";
			//HP�������ȉ����ǂ���
			boolean hpIsUnderHarf = user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
					< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax();
			L1:
			{
				//�񕜃A�C�e���ivalue���{�Ńo�g�����[�X�ł���A�C�e���j�������Ă��邩�ǂ���
				//�񕜃A�C�e��
				Item healItem = (Item) getMax(user.getStatus().getItemBag().getItems());
				if (healItem == null) {
					break L1;
				}
				//�񕜃A�C�e���C���X�^
				ActionTarget instTgt = BattleTargetSystem.instantTarget(user, healItem);
				//������HP�������ȉ��܂��̓C���X�^�G���A�̓G����HP�������ȉ�������ꍇ�A�����ɃA�C�e���g�p
				if (hpIsUnderHarf || instTgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
						< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax()).count() > 0) {
					return healItem;
				}
			}

			//�񕜖��@�ivalue���{�j�����Ă���ꍇ��HP���Ⴂ�ꍇ�����Ɏg��
			//�񕜃A�C�e���������Ă���ꍇ��HP���Ⴂ�ꍇ�����Ɏg��
			L2:
			{
				CmdAction healMgk = getMax(list.stream().filter(p -> p.getType() == ActionType.MAGIC).collect(Collectors.toList()));
				//�񕜖��@�C���X�^
				if (healMgk == null) {
					break L2;
				}
				//�񕜃A�C�e���C���X�^
				ActionTarget instTgt = BattleTargetSystem.instantTarget(user, healMgk);
				//������HP�������ȉ��܂��̓C���X�^�G���A�̓G����HP�������ȉ�������ꍇ�A�����ɃA�C�e���g�p
				if (hpIsUnderHarf || instTgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
						< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax()).count() > 0) {
					return healMgk;
				}

			}
			//�З͂��Œ�̍s����Ԃ����A����Ȃ����ڂ������ĉr���ł��Ȃ����@�ł���ꍇ�͕ʂ̍s����Ԃ�
			//�����_���ȍs����Ԃ�

			final int CHUUSEN_KAISU = 12;
			for (int i = 0; i < CHUUSEN_KAISU; i++) {
				CmdAction kouho = getMin(list);
				Map<StatusKey, Integer> damage = kouho.selfBattleDirectDamage();
				//�_���[�W�����Z
				StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
				//�_���[�W�������āA-�̍��ڂ�����ꍇ�A�Ή����x�����Ȃ����ߋ�U��
				//���̖��@�̏���ڂ��擾
				if (!damage.isEmpty() && simulateDamage.hasMinus()) {
					continue;
				}
				return kouho;
			}
			//�����_���ȍs����Ԃ�
			Collections.shuffle(list);
			return list.get(0);
		}

		@Override
		public Point2D.Float targetLocation(BattleCharacter user) {
			//�ł��߂�PC��Ԃ�
			//����������Q��������ꍇ�͏�Q�����悯��R�[�X�ŏ�Q���܂ł̈ʒu��Ԃ�

			//�ł��߂�PC������
			BattleCharacter pc = BattleTargetSystem.nearPCs(user);

			//���݂̏�Q�����X�g���擾
			List<BattleFieldObstacle> oList = GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getObstacle();

			//user����pc�܂ł̒�����ɏ�Q�������邩����
			EmptySprite s = new EmptySprite(user.getSprite().getCenter(), new Dimension(2, 2));
			s.setX(s.getX() - 1);
			s.setY(s.getY() - 1);
			KVector v = new KVector();
			v.setAngle(user.getCenter(), pc.getCenter());
			v.setSpeed(1);
			s.setVector(v);

			while (true) {
				Point2D.Float next = s.simulateMove();
				//PC�ɏՓˁE�E�EPC�̈ʒu��Ԃ�
				if (pc.getSprite().contains(next)) {
					return pc.getCenter();
				}
				//�G���A����o���E�E�E�ړ��ł���Ƃ���܂ł̍��W��Ԃ�
				//�^�[�Q�b�g���G���A�O�ɂ���ꍇ�ɔ������邪�A���ʔ������Ȃ��B
				if (!GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getBattleFieldAllArea().contains(next)) {
					return s.getCenter();
				}
				//��Q���ڐG����
				for (int j = 0; j < oList.size(); j++) {
					BattleFieldObstacle o = oList.get(j);
					//��Q��o�ɏՓ�
					if (o.hit(s)) {
						//�����p�x��ݒ�
						float d = (float) Point2D.Float.distance(user.getCenter().x, user.getCenter().y, o.getCenterX(), o.getCenterY());
						//���݂̊p�x�{�[i�ŁA����d���q�b�g���Ȃ��Ȃ�p�x���v�Z����
						float ang1 = v.getAngle();
						float ang2 = ang1;
						boolean sw = true;//�{���邩-���邩
						for (int i = 0;; i++) {
							if (sw) {
								ang1 = ang2 + i;
							} else {
								ang1 = ang2 - i;
							}

							//+=i�����p�x���Z�o
							KVector kv = new KVector();
							kv.setAngle(ang1);
							kv.setSpeed(d);

							//�p�x����̃X�v���C�g�ɐݒ�A������d��1�񓮂���
							EmptySprite es = new EmptySprite(user.getSprite().getX(), user.getSprite().getY(), 2, 2);
							es.setVector(kv);
							es.move();

							//�q�b�g���Ă��Ȃ���΂��̍��W��Ԃ�
							if (!o.hit(es)) {
								return es.getCenter();
							}
							sw = !sw;
						}
					}
				}
				//���Ȃ����߈ړ����R�~�b�g
				s.move();
			}

		}

	};

	static {
		EnemyAIStorage.getInstance().addAll(StandardEnemyAI.values());
	}

	@Override
	public String getName() {
		return toString();
	}

	//l����TT���p�[�e�B�[��value���ő�̂��̂�Ԃ��i�{
	//��������ꍇ�̓����_���Ȃ��̂�Ԃ�
	//�Ȃ��ꍇ��null��Ԃ�
	private static CmdAction getMax(List<? extends CmdAction> l) {
		//�G�̐l��
		int enemyNum = GameSystem.getInstance().getBattleSystem().getEnemies().size();
		//�����̐l��
		int partyNum = GameSystem.getInstance().getParty().size();
		Collections.shuffle(l);
		Map<CmdAction, Integer> result = new HashMap<>();
		for (CmdAction a : l) {
			int sum = 0;
			for (ActionEvent e : a.getBattleEvent()) {
				switch (e.getTargetType()) {
					case ALL:
						sum += (enemyNum + partyNum) * e.getValue();
						break;
					case FIELD:
						break;
					case ONE_ENEMY:
						sum += (partyNum) * e.getValue();
						break;
					case ONE_PARTY:
						sum += (enemyNum) * e.getValue();
						break;
					case RANDOM_ONE:
						sum += e.getValue();
						break;
					case RANDOM_ONE_ENEMY:
						sum += e.getValue();
						break;
					case RANDOM_ONE_PARTY:
						sum += e.getValue();
						break;
					case SELF:
						sum += e.getValue();
						break;
					case TEAM_ENEMY:
						sum += (partyNum) * e.getValue();
						break;
					case TEAM_PARTY:
						sum += (enemyNum) * e.getValue();
						break;
					default:
						throw new AssertionError();
				}
			}
			result.put(a, sum);
		}
		if (result.isEmpty()) {
			return null;
		}
		return result.entrySet().stream().sorted((p1, p2) -> {
			return p2.getValue() - p1.getValue();
		}
		).map(p -> p.getKey()).collect(Collectors.toList()).get(0);

	}

	//l����TT���G�l�~�[��value���Œ�̂��̂�Ԃ��i�[
	//��������ꍇ�̓����_���Ȃ��̂�Ԃ�
	//�Ȃ��ꍇ��null��Ԃ�
	private static CmdAction getMin(List<? extends CmdAction> l) {
		//�G�̐l��
		int enemyNum = GameSystem.getInstance().getBattleSystem().getEnemies().size();
		//�����̐l��
		int partyNum = GameSystem.getInstance().getParty().size();
		Collections.shuffle(l);
		Map<CmdAction, Integer> result = new HashMap<>();
		for (CmdAction a : l) {
			int sum = 0;
			for (ActionEvent e : a.getBattleEvent()) {
				switch (e.getTargetType()) {
					case ALL:
						sum += (enemyNum + partyNum) * e.getValue();
						break;
					case FIELD:
						break;
					case ONE_ENEMY:
						sum += (partyNum) * e.getValue();
						break;
					case ONE_PARTY:
						sum += (enemyNum) * e.getValue();
						break;
					case RANDOM_ONE:
						sum += e.getValue();
						break;
					case RANDOM_ONE_ENEMY:
						sum += e.getValue();
						break;
					case RANDOM_ONE_PARTY:
						sum += e.getValue();
						break;
					case SELF:
						sum += e.getValue();
						break;
					case TEAM_ENEMY:
						sum += (partyNum) * e.getValue();
						break;
					case TEAM_PARTY:
						sum += (enemyNum) * e.getValue();
						break;
					default:
						throw new AssertionError();
				}
			}
			result.put(a, sum);
		}
		if (result.isEmpty()) {
			return null;
		}
		return result.entrySet().stream().sorted((p1, p2) -> {
			return p1.getValue() - p2.getValue();
		}
		).map(p -> p.getKey()).collect(Collectors.toList()).get(0);
	}
}
