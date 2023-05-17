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
			//HPが半分以下かどうか
			boolean hpIsUnderHarf = user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
					< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax();
			L1:
			{
				//回復アイテム（valueが＋でバトルユースできるアイテム）を持っているかどうか
				//回復アイテム
				Item healItem = (Item) getMax(user.getStatus().getItemBag().getItems());
				if (healItem == null) {
					break L1;
				}
				//回復アイテムインスタ
				ActionTarget instTgt = BattleTargetSystem.instantTarget(user, healItem);
				//自分のHPが半分以下またはインスタエリアの敵内にHPが半分以下がいる場合、そいつにアイテム使用
				if (hpIsUnderHarf || instTgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
						< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax()).count() > 0) {
					return healItem;
				}
			}

			//回復魔法（valueが＋）持っている場合でHPが低い場合自分に使う
			//回復アイテムを持っている場合でHPが低い場合自分に使う
			L2:
			{
				CmdAction healMgk = getMax(list.stream().filter(p -> p.getType() == ActionType.MAGIC).collect(Collectors.toList()));
				//回復魔法インスタ
				if (healMgk == null) {
					break L2;
				}
				//回復アイテムインスタ
				ActionTarget instTgt = BattleTargetSystem.instantTarget(user, healMgk);
				//自分のHPが半分以下またはインスタエリアの敵内にHPが半分以下がいる場合、そいつにアイテム使用
				if (hpIsUnderHarf || instTgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
						< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax()).count() > 0) {
					return healMgk;
				}

			}
			//威力が最低の行動を返すが、足りない項目があって詠唱できない魔法である場合は別の行動を返す
			//ランダムな行動を返す

			final int CHUUSEN_KAISU = 12;
			for (int i = 0; i < CHUUSEN_KAISU; i++) {
				CmdAction kouho = getMin(list);
				Map<StatusKey, Integer> damage = kouho.selfBattleDirectDamage();
				//ダメージを合算
				StatusValueSet simulateDamage = user.getStatus().simulateDamage(damage);
				//ダメージがあって、-の項目がある場合、対価を支払えないため空振り
				//この魔法の消費項目を取得
				if (!damage.isEmpty() && simulateDamage.hasMinus()) {
					continue;
				}
				return kouho;
			}
			//ランダムな行動を返す
			Collections.shuffle(list);
			return list.get(0);
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
			s.setX(s.getX() - 1);
			s.setY(s.getY() - 1);
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

	//lからTTがパーティーでvalueが最大のものを返す（＋
	//複数ある場合はランダムなものを返す
	//ない場合はnullを返す
	private static CmdAction getMax(List<? extends CmdAction> l) {
		//敵の人数
		int enemyNum = GameSystem.getInstance().getBattleSystem().getEnemies().size();
		//味方の人数
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

	//lからTTがエネミーでvalueが最低のものを返す（ー
	//複数ある場合はランダムなものを返す
	//ない場合はnullを返す
	private static CmdAction getMin(List<? extends CmdAction> l) {
		//敵の人数
		int enemyNum = GameSystem.getInstance().getBattleSystem().getEnemies().size();
		//味方の人数
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
