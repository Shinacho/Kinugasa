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
package kinugasa.game.field4;

import java.awt.Point;
import kinugasa.resource.Storage;
import kinugasa.util.Random;

/**
 * NPC�̈ړ��A���S���Y���̗B��̕ۊǏꏊ�ł��B���̃N���X�̓V���O���g���N���X�ł�.
 * �f�t�H���g�̈ړ��A���S���Y���Ƃ��āA�ȉ��̖��O�̗v�f���ǉ�����Ă��܂��B<br>
 * �ENOT_MOVE�F�ړ����܂���B
 * �EROUND_1�F�����ʒu����w��̔��a�̃^�C�����Ń����_���Ȓn�_�Ɉړ����܂��B�}�b�v���́A����NPC�ɐݒ肳�ꂽ�ړ����@�ňړ��ł���^�C���Ɍ���܂��B�ړ�������60?600�t���[���̃����_���ł��B<br>
 * �EROUND_3�F����<br>
 * �EROUND_5�F����<br>
 * �EROUND_7�F����<br>
 * �EROUND_9�F����<br>
 * ����ȊO�́A�Ⴆ��2�_�Ԃ��s�����藈���肷��A���S���Y����A
 * ����̏ꏊ�Ɉړ�����A���S���Y���́A��{�I�Ƀ}�b�v�`��Ɉˑ����邽�߁A�Ǝ��ɍ쐬����K�v������܂��B<br>
 *
 * @vesion 1.0.0 - 2022/11/08_19:50:30<br>
 * @author Dra211<br>
 */
public class NPCMoveModelStorage extends Storage<NPCMoveModel> {

	private static final NPCMoveModelStorage INSTANCE = new NPCMoveModelStorage();

	private NPCMoveModelStorage() {
		add(new NPCMoveModel("NOT_MOVE") {
			@Override
			public Point getNextTargetLocationOnMap(NPC n, FieldMap map) {
				return n.getInitialLocationOnMap();
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return Integer.MAX_VALUE;//MAX_VALUE��̃t���[���ł��ړ����邱�Ƃ͂Ȃ��B
			}
		});
		add(new NPCMoveModel("ROUND_1") {
			private static final int VALUE = 1;

			@Override
			public Point getNextTargetLocationOnMap(NPC n, FieldMap map) {
				Point tgt = new Point((Point) n.getInitialLocationOnMap().clone());
				do {
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(VALUE);
					} else {
						tgt.x += Random.randomAbsInt(VALUE);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(VALUE);
					} else {
						tgt.y += Random.randomAbsInt(VALUE);
					}
				} while (n.getVehicle().isStepOn(map.getTile(new D2Idx(tgt.x, tgt.y)).getChip()));
				return tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}
		});
		add(new NPCMoveModel("ROUND_3") {
			private static final int VALUE = 3;

			@Override
			public Point getNextTargetLocationOnMap(NPC n, FieldMap map) {
				Point tgt = new Point((Point) n.getInitialLocationOnMap().clone());
				do {
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(VALUE);
					} else {
						tgt.x += Random.randomAbsInt(VALUE);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(VALUE);
					} else {
						tgt.y += Random.randomAbsInt(VALUE);
					}
				} while (n.getVehicle().isStepOn(map.getTile(new D2Idx(tgt.x, tgt.y)).getChip()));
				return tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}
		});
		add(new NPCMoveModel("ROUND_5") {
			private static final int VALUE = 5;

			@Override
			public Point getNextTargetLocationOnMap(NPC n, FieldMap map) {
				Point tgt = new Point((Point) n.getInitialLocationOnMap().clone());
				do {
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(VALUE);
					} else {
						tgt.x += Random.randomAbsInt(VALUE);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(VALUE);
					} else {
						tgt.y += Random.randomAbsInt(VALUE);
					}
				} while (n.getVehicle().isStepOn(map.getTile(new D2Idx(tgt.x, tgt.y)).getChip()));
				return tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}
		});
		add(new NPCMoveModel("ROUND_7") {
			private static final int VALUE = 7;

			@Override
			public Point getNextTargetLocationOnMap(NPC n, FieldMap map) {
				Point tgt = new Point((Point) n.getInitialLocationOnMap().clone());
				do {
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(VALUE);
					} else {
						tgt.x += Random.randomAbsInt(VALUE);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(VALUE);
					} else {
						tgt.y += Random.randomAbsInt(VALUE);
					}
				} while (n.getVehicle().isStepOn(map.getTile(new D2Idx(tgt.x, tgt.y)).getChip()));
				return tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}
		});
		add(new NPCMoveModel("ROUND_9") {
			private static final int VALUE = 9;

			@Override
			public Point getNextTargetLocationOnMap(NPC n, FieldMap map) {
				Point tgt = new Point((Point) n.getInitialLocationOnMap().clone());
				do {
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(VALUE);
					} else {
						tgt.x += Random.randomAbsInt(VALUE);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(VALUE);
					} else {
						tgt.y += Random.randomAbsInt(VALUE);
					}
				} while (n.getVehicle().isStepOn(map.getTile(new D2Idx(tgt.x, tgt.y)).getChip()));
				return tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}
		});

	}

	public static NPCMoveModelStorage getInstance() {
		return INSTANCE;
	}

}
