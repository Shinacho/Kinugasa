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
 * NPCの移動アルゴリズムの唯一の保管場所です。このクラスはシングルトンクラスです.
 * デフォルトの移動アルゴリズムとして、以下の名前の要素が追加されています。<br>
 * ・NOT_MOVE：移動しません。
 * ・ROUND_1：初期位置から指定の半径のタイル数でランダムな地点に移動します。マップ内の、かつNPCに設定された移動方法で移動できるタイルに限ります。移動周期は60?600フレームのランダムです。<br>
 * ・ROUND_3：同上<br>
 * ・ROUND_5：同上<br>
 * ・ROUND_7：同上<br>
 * ・ROUND_9：同上<br>
 * これ以外の、例えば2点間を行ったり来たりするアルゴリズムや、
 * 特定の場所に移動するアルゴリズムは、基本的にマップ形状に依存するため、独自に作成する必要があります。<br>
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
				return Integer.MAX_VALUE;//MAX_VALUE後のフレームでも移動することはない。
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
