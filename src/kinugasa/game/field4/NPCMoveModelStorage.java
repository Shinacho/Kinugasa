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
import java.util.Collections;
import java.util.stream.Collectors;
import kinugasa.object.FourDirection;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.Storage;
import kinugasa.util.Random;

/**
 * NPC�̈ړ��A���S���Y���̗B��̕ۊǏꏊ�ł��B���̃N���X�̓V���O���g���N���X�ł�.
 * �f�t�H���g�̈ړ��A���S���Y���Ƃ��āA�ȉ��̖��O�̗v�f���ǉ�����Ă��܂��B<br>
 * �ENOT_MOVE�F�ړ����܂���B
 * �EROUND_1�F�����ʒu����w��̔��a�̃^�C�����Ń����_���Ȓn�_�Ɉړ����܂��B�}�b�v���́A����NPC�ɐݒ肳�ꂽ�ړ����@�ňړ��ł���^�C���Ɍ���܂��B�ړ�������60?600�t���[���̃����_���ł��B<br>
 * �EROUND_2�F����<br>
 * �EROUND_3�F����<br>
 * �EROUND_4�F����<br>
 * �EROUND_5�F����<br>
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
			public D2Idx getNextTargetIdx(NPC n, FieldMap map) {
				return n.getInitialIdx();
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return Integer.MAX_VALUE;//MAX_VALUE��̃t���[���ł��ړ����邱�Ƃ͂Ȃ��B
			}

			@Override
			public D2Idx getMax(NPC n) {
				return n.getInitialIdx().clone();
			}

			@Override
			public D2Idx getMin(NPC n) {
				return n.getInitialIdx().clone();
			}

		});
		add(new NPCMoveModel("ROUND_1") {
			private static final int VALUE = 1;

			@Override
			public D2Idx getNextTargetIdx(NPC n, FieldMap map) {
				D2Idx tgt = null;
				for (int i = 0; i < 600; i++) {
					tgt = new D2Idx(n.getCurrentIdx());
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(2);
					} else {
						tgt.x += Random.randomAbsInt(2);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(2);
					} else {
						tgt.y += Random.randomAbsInt(2);
					}
					if (tgt.x <= 0 || tgt.y <= 0) {
						tgt = new D2Idx(n.getInitialIdx());
						continue;
					}
					if (tgt.x >= map.getBaseLayer().getDataWidth() - 1 || tgt.y >= map.getBaseLayer().getDataHeight() - 1) {
						continue;
					}
					if (!n.getVehicle().isStepOn(map.getTile(tgt).getChip())) {
						continue;
					}
					if (map.getCurrentIdx().equals(tgt)) {
						continue;
					}
					if (map.getPlayerCharacter().stream().map(v -> v.getCurrentIdx()).collect(Collectors.toList()).contains(tgt)) {
						continue;
					}
					if (tgt.x < n.getInitialIdx().x - VALUE || tgt.y < n.getInitialIdx().y - VALUE) {
						continue;
					}
					if (tgt.x > n.getInitialIdx().x + VALUE || tgt.y > n.getInitialIdx().y + VALUE) {
						continue;
					}
					assert map.getBaseLayer().include(tgt) : "NPC targetIDX is " + tgt;
					break;
				}
				return tgt == null ? n.getInitialIdx() : tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}

			@Override
			public D2Idx getMax(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x += VALUE;
				idx.y += VALUE;
				return idx;
			}

			@Override
			public D2Idx getMin(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x -= VALUE;
				idx.y -= VALUE;
				return idx;
			}

		});
		add(new NPCMoveModel("ROUND_2") {
			private static final int VALUE = 2;

			@Override
			public D2Idx getNextTargetIdx(NPC n, FieldMap map) {
				D2Idx tgt = null;
				for (int i = 0; i < 600; i++) {
					tgt = new D2Idx(n.getCurrentIdx());
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(2);
					} else {
						tgt.x += Random.randomAbsInt(2);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(2);
					} else {
						tgt.y += Random.randomAbsInt(2);
					}
					if (tgt.x <= 0 || tgt.y <= 0) {
						tgt = new D2Idx(n.getInitialIdx());
						continue;
					}
					if (tgt.x >= map.getBaseLayer().getDataWidth() - 1 || tgt.y >= map.getBaseLayer().getDataHeight() - 1) {
						continue;
					}
					if (!n.getVehicle().isStepOn(map.getTile(tgt).getChip())) {
						continue;
					}
					if (map.getCurrentIdx().equals(tgt)) {
						continue;
					}
					if (map.getPlayerCharacter().stream().map(v -> v.getCurrentIdx()).collect(Collectors.toList()).contains(tgt)) {
						continue;
					}
					if (tgt.x < n.getInitialIdx().x - VALUE || tgt.y < n.getInitialIdx().y - VALUE) {
						continue;
					}
					if (tgt.x > n.getInitialIdx().x + VALUE || tgt.y > n.getInitialIdx().y + VALUE) {
						continue;
					}
					assert map.getBaseLayer().include(tgt) : "NPC targetIDX is " + tgt;
					break;
				}
				return tgt == null ? n.getInitialIdx() : tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}

			@Override
			public D2Idx getMax(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x += VALUE;
				idx.y += VALUE;
				return idx;
			}

			@Override
			public D2Idx getMin(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x -= VALUE;
				idx.y -= VALUE;
				return idx;
			}
		});
		add(new NPCMoveModel("ROUND_3") {
			private static final int VALUE = 3;

			@Override
			public D2Idx getNextTargetIdx(NPC n, FieldMap map) {
				D2Idx tgt = null;
				for (int i = 0; i < 600; i++) {
					tgt = new D2Idx(n.getCurrentIdx());
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(2);
					} else {
						tgt.x += Random.randomAbsInt(2);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(2);
					} else {
						tgt.y += Random.randomAbsInt(2);
					}
					if (tgt.x <= 0 || tgt.y <= 0) {
						tgt = new D2Idx(n.getInitialIdx());
						continue;
					}
					if (tgt.x >= map.getBaseLayer().getDataWidth() - 1 || tgt.y >= map.getBaseLayer().getDataHeight() - 1) {
						continue;
					}
					if (!n.getVehicle().isStepOn(map.getTile(tgt).getChip())) {
						continue;
					}
					if (map.getCurrentIdx().equals(tgt)) {
						continue;
					}
					if (map.getPlayerCharacter().stream().map(v -> v.getCurrentIdx()).collect(Collectors.toList()).contains(tgt)) {
						continue;
					}
					if (tgt.x < n.getInitialIdx().x - VALUE || tgt.y < n.getInitialIdx().y - VALUE) {
						continue;
					}
					if (tgt.x > n.getInitialIdx().x + VALUE || tgt.y > n.getInitialIdx().y + VALUE) {
						continue;
					}
					assert map.getBaseLayer().include(tgt) : "NPC targetIDX is " + tgt;
					break;
				}
				return tgt == null ? n.getInitialIdx() : tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}

			@Override
			public D2Idx getMax(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x += VALUE;
				idx.y += VALUE;
				return idx;
			}

			@Override
			public D2Idx getMin(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x -= VALUE;
				idx.y -= VALUE;
				return idx;
			}
		});
		add(new NPCMoveModel("ROUND_4") {
			private static final int VALUE = 4;

			@Override
			public D2Idx getNextTargetIdx(NPC n, FieldMap map) {
				D2Idx tgt = null;
				for (int i = 0; i < 600; i++) {
					tgt = new D2Idx(n.getCurrentIdx());
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(2);
					} else {
						tgt.x += Random.randomAbsInt(2);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(2);
					} else {
						tgt.y += Random.randomAbsInt(2);
					}
					if (tgt.x <= 0 || tgt.y <= 0) {
						tgt = new D2Idx(n.getInitialIdx());
						continue;
					}
					if (tgt.x >= map.getBaseLayer().getDataWidth() - 1 || tgt.y >= map.getBaseLayer().getDataHeight() - 1) {
						continue;
					}
					if (!n.getVehicle().isStepOn(map.getTile(tgt).getChip())) {
						continue;
					}
					if (map.getCurrentIdx().equals(tgt)) {
						continue;
					}
					if (map.getPlayerCharacter().stream().map(v -> v.getCurrentIdx()).collect(Collectors.toList()).contains(tgt)) {
						continue;
					}
					if (tgt.x < n.getInitialIdx().x - VALUE || tgt.y < n.getInitialIdx().y - VALUE) {
						continue;
					}
					if (tgt.x > n.getInitialIdx().x + VALUE || tgt.y > n.getInitialIdx().y + VALUE) {
						continue;
					}
					assert map.getBaseLayer().include(tgt) : "NPC targetIDX is " + tgt;
					break;
				}
				return tgt == null ? n.getInitialIdx() : tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}

			@Override
			public D2Idx getMax(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x += VALUE;
				idx.y += VALUE;
				return idx;
			}

			@Override
			public D2Idx getMin(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x -= VALUE;
				idx.y -= VALUE;
				return idx;
			}
		});
		add(new NPCMoveModel("ROUND_5") {
			private static final int VALUE = 5;

			@Override
			public D2Idx getNextTargetIdx(NPC n, FieldMap map) {
				D2Idx tgt = null;
				for (int i = 0; i < 600; i++) {
					tgt = new D2Idx(n.getCurrentIdx());
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(2);
					} else {
						tgt.x += Random.randomAbsInt(2);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(2);
					} else {
						tgt.y += Random.randomAbsInt(2);
					}
					if (tgt.x <= 0 || tgt.y <= 0) {
						tgt = new D2Idx(n.getInitialIdx());
						continue;
					}
					if (tgt.x >= map.getBaseLayer().getDataWidth() - 1 || tgt.y >= map.getBaseLayer().getDataHeight() - 1) {
						continue;
					}
					if (!n.getVehicle().isStepOn(map.getTile(tgt).getChip())) {
						continue;
					}
					if (map.getCurrentIdx().equals(tgt)) {
						continue;
					}
					if (map.getPlayerCharacter().stream().map(v -> v.getCurrentIdx()).collect(Collectors.toList()).contains(tgt)) {
						continue;
					}
					if (tgt.x < n.getInitialIdx().x - VALUE || tgt.y < n.getInitialIdx().y - VALUE) {
						continue;
					}
					if (tgt.x > n.getInitialIdx().x + VALUE || tgt.y > n.getInitialIdx().y + VALUE) {
						continue;
					}
					assert map.getBaseLayer().include(tgt) : "NPC targetIDX is " + tgt;
					break;
				}
				return tgt == null ? n.getInitialIdx() : tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return 60 + Random.randomAbsInt(541);
			}

			@Override
			public D2Idx getMax(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x += VALUE;
				idx.y += VALUE;
				return idx;
			}

			@Override
			public D2Idx getMin(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x -= VALUE;
				idx.y -= VALUE;
				return idx;
			}
		});
		add(new NPCMoveModel("LOOK_AROUND") {
			@Override
			public D2Idx getNextTargetIdx(NPC n, FieldMap map) {
				return n.getInitialIdx();
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				int r = Random.randomAbsInt(4);
				switch (r) {
					case 0:
						n.to(FourDirection.EAST);
						break;
					case 1:
						n.to(FourDirection.WEST);
						break;
					case 2:
						n.to(FourDirection.SOUTH);
						break;
					case 3:
						n.to(FourDirection.NORTH);
						break;
				}
				return 60 + Random.randomAbsInt(541);
			}

			@Override
			public D2Idx getMin(NPC n) {
				return n.getInitialIdx();
			}

			@Override
			public D2Idx getMax(NPC n) {
				return n.getInitialIdx();
			}
		});
	}

	public static NPCMoveModel createRoundMoveModel(String name, int size, int minTime, int maxTime) {
		NPCMoveModel n = new NPCMoveModel(name) {
			private final int VALUE = size;

			@Override
			public D2Idx getNextTargetIdx(NPC n, FieldMap map) {
				D2Idx tgt = null;
				for (int i = 0; i < 600; i++) {
					tgt = new D2Idx(n.getCurrentIdx());
					if (Random.randomBool()) {
						tgt.x -= Random.randomAbsInt(2);
					} else {
						tgt.x += Random.randomAbsInt(2);
					}
					if (Random.randomBool()) {
						tgt.y -= Random.randomAbsInt(2);
					} else {
						tgt.y += Random.randomAbsInt(2);
					}
					if (tgt.x <= 0 || tgt.y <= 0) {
						tgt = new D2Idx(n.getInitialIdx());
						continue;
					}
					if (tgt.x >= map.getBaseLayer().getDataWidth() - 1 || tgt.y >= map.getBaseLayer().getDataHeight() - 1) {
						continue;
					}
					if (!n.getVehicle().isStepOn(map.getTile(tgt).getChip())) {
						continue;
					}
					if (map.getCurrentIdx().equals(tgt)) {
						continue;
					}
					if (map.getPlayerCharacter().stream().map(v -> v.getCurrentIdx()).collect(Collectors.toList()).contains(tgt)) {
						continue;
					}
					if (tgt.x < n.getInitialIdx().x - VALUE || tgt.y < n.getInitialIdx().y - VALUE) {
						continue;
					}
					if (tgt.x > n.getInitialIdx().x + VALUE || tgt.y > n.getInitialIdx().y + VALUE) {
						continue;
					}
					assert map.getBaseLayer().include(tgt) : "NPC targetIDX is " + tgt;
					break;
				}
				return tgt == null ? n.getInitialIdx() : tgt;
			}

			@Override
			public int nextMoveFrameTime(NPC n, FieldMap map) {
				return minTime + Random.randomAbsInt(maxTime + 1 - minTime);
			}

			@Override
			public D2Idx getMax(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x += VALUE;
				idx.y += VALUE;
				return idx;
			}

			@Override
			public D2Idx getMin(NPC n) {
				D2Idx idx = n.getInitialIdx();
				idx.x -= VALUE;
				idx.y -= VALUE;
				return idx;
			}
		};
		getInstance().add(n);
		return n;
	}

	public static NPCMoveModelStorage getInstance() {
		return INSTANCE;
	}

}
