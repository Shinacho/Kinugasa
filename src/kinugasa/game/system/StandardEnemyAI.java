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
			//ランダムな行動を返す
			List<CmdAction> l = new ArrayList<>(list);
			Collections.shuffle(l);
			return l.get(0);
		}

		@Override
		public Point2D.Float targetLocation(BattleCharacter user) {
			//最も近いPCを返す
			//ただし足障害物がある場合は障害物をよけるコースで障害物までの位置を返す

			//最も近いPCを検索
			BattleCharacter pc = BattleTargetSystem.nearPCs(user);

			//現在の障害物リストを取得
			List<BattleFieldObstacle> oList = GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getObstacle();

			//userからpcまでの直線状に障害物があるか検査
			EmptySprite s = new EmptySprite(user.getSprite().getCenter(), new Dimension(2, 2));
			KVector v = new KVector();
			v.setAngle(user.getCenter(), pc.getCenter());
			v.setSpeed(1);
			s.setVector(v);

			while (true) {
				Point2D.Float next = s.simulateMove();
				//PCに衝突・・・PCの位置を返す
				if (pc.getSprite().contains(next)) {
					return pc.getCenter();
				}
				//エリアから出た・・・移動できるところまでの座標を返す
				//ターゲットがエリア外にいる場合に発生するが、普通発生しない。
				if (!GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getBattleFieldAllArea().contains(next)) {
					return s.getCenter();
				}
				//障害物接触判定
				for (int j = 0; j < oList.size(); j++) {
					BattleFieldObstacle o = oList.get(j);
					//障害物oに衝突
					if (o.hit(s)) {
						//避け角度を設定
						float d = (float) Point2D.Float.distance(user.getCenter().x, user.getCenter().y, o.getCenterX(), o.getCenterY());
						//現在の角度＋ーiで、距離dがヒットしなくなる角度を計算する
						float ang1 = v.getAngle();
						float ang2 = ang1;
						boolean sw = true;//＋するか-するか
						for (int i = 0;; i++) {
							if (sw) {
								ang1 = ang2 + i;
							} else {
								ang1 = ang2 - i;
							}

							//+=iした角度を算出
							KVector kv = new KVector();
							kv.setAngle(ang1);
							kv.setSpeed(d);

							//角度を空のスプライトに設定、距離はdで1回動かす
							EmptySprite es = new EmptySprite(user.getSprite().getX(), user.getSprite().getY(), 2, 2);
							es.setVector(kv);
							es.move();

							//ヒットしていなければその座標を返す
							if (!o.hit(es)) {
								return es.getCenter();
							}
							sw = !sw;
						}
					}
				}
				//問題ないため移動をコミット
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
