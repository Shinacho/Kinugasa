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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.object.EmptySprite;
import kinugasa.object.KVector;
import kinugasa.object.Sprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/02_16:46:35<br>
 * @author Dra211<br>
 */
public enum StandardEnemyAI implements EnemyAI {
	SIMPLE {
		@Override
		public CmdAction getNext(BattleCharacter user, List<CmdAction> list) {
			//�����_���ȍs����Ԃ�
			List<CmdAction> l = new ArrayList<>(list);
			Collections.shuffle(l);
			return l.get(0);
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

}
