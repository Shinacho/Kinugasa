/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.game.field4;

import java.awt.Point;
import java.util.Collections;
import java.util.stream.Collectors;
import kinugasa.game.system.GameSystemException;
import kinugasa.object.FourDirection;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.Storage;
import kinugasa.util.Random;

/**
 * NPCの移動アルゴリズムの唯一の保管場所です。このクラスはシングルトンクラスです.
 * デフォルトの移動アルゴリズムとして、以下の名前の要素が追加されています。<br>
 * ・NOT_MOVE：移動しません。
 * ・ROUND_1：初期位置から指定の半径のタイル数でランダムな地点に移動します。マップ内の、かつNPCに設定された移動方法で移動できるタイルに限ります。移動周期は60?600フレームのランダムです。<br>
 * ・ROUND_2：同上<br>
 * ・ROUND_3：同上<br>
 * ・ROUND_4：同上<br>
 * ・ROUND_5：同上<br>
 * これ以外の、例えば2点間を行ったり来たりするアルゴリズムや、
 * 特定の場所に移動するアルゴリズムは、基本的にマップ形状に依存するため、独自に作成する必要があります。<br>
 *
 * @vesion 1.0.0 - 2022/11/08_19:50:30<br>
 * @author Shinacho<br>
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
				return Integer.MAX_VALUE;//MAX_VALUE後のフレームでも移動することはない。
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
					if (n.getVehicle() == null) {
						throw new GameSystemException("NPCs vehicle is null:" + n);
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
