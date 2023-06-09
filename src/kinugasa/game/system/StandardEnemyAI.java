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
package kinugasa.game.system;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static kinugasa.game.system.TargetType.ALL;
import static kinugasa.game.system.TargetType.ONE;
import static kinugasa.game.system.TargetType.SELF;
import static kinugasa.game.system.TargetType.TEAM;
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
		public Action getNext(BattleCharacter user, List<Action> list) {
			assert user.isPlayer() == false : "ENEMY AI but user is not CPU";
			//HPが半分以下かどうか
			boolean hpIsUnderHarf = user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
					< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax() / 2;
			L1:
			if(hpIsUnderHarf){
				//回復アイテム（valueが＋でバトル利用できるアイテム）を持っているかどうか
				Item healItem = (Item) getMax(user.getStatus().getItemBag().getItems());
				if (healItem == null) {
					break L1;
				}
				//回復アイテムインスタ
				ActionTarget instTgt = BattleTargetSystem.instantTarget(user, healItem);
				if (instTgt.getTarget().isEmpty()) {
					break L1;
				}
				//自分のHPが半分以下またはインスタエリアの敵内にHPが半分以下がいる場合、そいつにアイテム使用
				//（ターゲットはBSで再計算する
				if (hpIsUnderHarf || instTgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
						< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax() / 2).count() > 0) {
					return healItem;
				}
			}

			L2:
			if(hpIsUnderHarf){
				//回復魔法（valueが＋）持っている場合でHPが低い場合自分に使う
				Action healMgk = getMax(list.stream().filter(p -> p.getType() == ActionType.MAGIC).collect(Collectors.toList()));
				if (healMgk == null) {
					break L2;
				}
				//回復魔法インスタ
				ActionTarget instTgt = BattleTargetSystem.instantTarget(user, healMgk);
				if (instTgt.getTarget().isEmpty()) {
					break L2;
				}
				//自分のHPが半分以下またはインスタエリアの敵内にHPが半分以下がいる場合、そいつにアイテム使用
				//（ターゲットはBSで再計算する
				if (hpIsUnderHarf || instTgt.getTarget().stream().filter(p -> user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getValue()
						< user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.hp).getMax() / 2).count() > 0) {
					return healMgk;
				}

			}
			//威力が最低の行動を返すが、足りない項目があって詠唱できない魔法である場合は別の行動を返す
			final int CHUUSEN_KAISU = 12;
			for (int i = 0; i < CHUUSEN_KAISU; i++) {
				Action kouho = getMin(list);
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
			BattleCharacter pc = BattleTargetSystem.nearPC(user);

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
	private static Action getMax(List<? extends Action> l) {
		//敵の人数
		int enemyNum = GameSystem.getInstance().getBattleSystem().getEnemies().size();
		//味方の人数
		int partyNum = GameSystem.getInstance().getParty().size();
		Collections.shuffle(l);
		Map<Action, Integer> result = new HashMap<>();
		for (Action a : l) {
			int sum = 0;
			for (ActionEvent e : a.getBattleEvent()) {
				switch (e.getTargetType()) {
					case ALL:
						sum += (enemyNum + partyNum) * e.getValue();
						break;
					case ONE:
						sum += (partyNum) * e.getValue();
						break;
					case RANDOM:
						sum += e.getValue();
						break;
					case SELF:
						sum += e.getValue();
						break;
					case TEAM:
						sum += (partyNum) * e.getValue();
						break;
					default:
						throw new AssertionError();
				}
			}
			if (sum < 0) {
				continue;
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
	private static Action getMin(List<? extends Action> l) {
		//敵の人数
		int enemyNum = GameSystem.getInstance().getBattleSystem().getEnemies().size();
		//味方の人数
		int partyNum = GameSystem.getInstance().getParty().size();
		Collections.shuffle(l);
		Map<Action, Integer> result = new HashMap<>();
		for (Action a : l) {
			int sum = 0;
			for (ActionEvent e : a.getBattleEvent()) {
				switch (e.getTargetType()) {
					case ALL:
						sum += (enemyNum + partyNum) * e.getValue();
						break;
					case ONE:
						sum += (partyNum) * e.getValue();
						break;
					case RANDOM:
						sum += e.getValue();
						break;
					case SELF:
						sum += e.getValue();
						break;
					case TEAM:
						sum += (partyNum) * e.getValue();
						break;
					default:
						throw new AssertionError();
				}
			}
			if (sum > 0) {
				continue;
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
