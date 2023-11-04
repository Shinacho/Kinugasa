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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kinugasa.game.NotNull;
import kinugasa.game.Nullable;
import kinugasa.object.EmptySprite;
import kinugasa.object.KVector;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/12/02_16:46:35<br>
 * @author Shinacho<br>
 */
public enum EnemyAIImpl implements EnemyAI {
	/**
	 * ダメージロールはHPが高い敵に強力な攻撃を続けます
	 */
	アタッカー {
		@Override
		public ActionTarget getNextAction(Enemy user) {
			//自己ヒール
			Action a = getHPを自己回復できるアクション(user);
			if (isそろそろHP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getMPを自己回復できるアクション(user);
			if (isそろそろMP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getSANを自己回復できるアクション(user);
			if (isそろそろSAN回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}

			//自分にかけられるバフがあれば実施
			Action buf = get自分にかけられるバフでまだかかっていないもの(user);
			if (buf != null) {
				return new ActionTarget(user, buf, List.of(user), false);
			}

			//攻撃力が高いアクションを取得
			for (Action ac : get最大威力攻撃順(user)) {
				//ターゲットが射程内にいればそれを実施
				Actor tgt = getTgt(user);
				if (is射程内(user, a, tgt)) {
					if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
						return new ActionTarget(user, buf, List.of(tgt), false);
					}
				}
			}

			//最大威力アクションを適用できる場所まで移動するよう指示
			return new ActionTarget(user, EnemyAIImpl.移動アクション, null, false);
		}

		@Override
		Actor getTgt(Enemy user) {
			if (isそろそろHP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろMP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろSAN回復したほうがいい(user) && getSANを自己回復できるアクション(user) != null) {
				return user;
			}
			return get一番体力が高いPC();
		}

		@Override
		public Point2D.Float targetLocation(Enemy user, Action a) {
			return getターゲットに届く位置(user, user.getStatus().getEffectedArea(a), this);
		}
	},
	/**
	 * ヒーラーロールは味方の近くで待機してHPを回復します。
	 */
	ヒーラー {
		@Override
		public ActionTarget getNextAction(Enemy user) {
			//自己ヒール
			Action a = getHPを自己回復できるアクション(user);
			if (isそろそろHP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getMPを自己回復できるアクション(user);
			if (isそろそろMP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getSANを自己回復できるアクション(user);
			if (isそろそろSAN回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}

			//自分にかけられるバフがあれば実施
			Action buf = get自分にかけられるバフでまだかかっていないもの(user);
			if (buf != null) {
				return new ActionTarget(user, buf, List.of(user), false);
			}

			Actor tgt = getTgt(user);
			Action 回復 = getターゲットにかけられるバフでまだかかっていないもの(user, (Enemy) tgt);
			if (回復 != null) {
				if (!回復.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, 回復, List.of(tgt), false);
				}
			}
			for (Action aa : get最大威力回復順(user)) {
				if (is射程内(user, aa, tgt)) {
					if (!aa.checkResource(user.getStatus()).is足りないステータスあり()) {
						return new ActionTarget(user, aa, List.of(tgt), false);
					}
				}
			}
			return new ActionTarget(user, EnemyAIImpl.移動アクション, null, false);
		}

		@Override
		Actor getTgt(Enemy user) {
			if (isそろそろHP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろMP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろSAN回復したほうがいい(user) && getSANを自己回復できるアクション(user) != null) {
				return user;
			}
			return get自分以外で一番近いEnemy(user);
		}

		@Override
		public Point2D.Float targetLocation(Enemy user, Action a) {
			return getターゲットに届く位置(user, user.getStatus().getEffectedArea(a), this);
		}
	},
	/**
	 * タンクは近い敵に近接攻撃を仕掛けに行きます
	 */
	タンク {
		@Override
		public ActionTarget getNextAction(Enemy user) {
			//自己ヒール
			Action a = getHPを自己回復できるアクション(user);
			if (isそろそろHP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getMPを自己回復できるアクション(user);
			if (isそろそろMP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getSANを自己回復できるアクション(user);
			if (isそろそろSAN回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}

			//自分にかけられるバフがあれば実施
			Action buf = get自分にかけられるバフでまだかかっていないもの(user);
			if (buf != null) {
				return new ActionTarget(user, buf, List.of(user), false);
			}

			//攻撃力が高いアクションを取得
			for (Action aa : get最大威力攻撃順(user)) {
				Actor tgt = getTgt(user);
				//ターゲットが射程内にいるか確認
				if (is射程内(user, a, tgt)) {
					if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
						if (Random.percent(0.5f)) {
							return new ActionTarget(user, EnemyAIImpl.防御アクション, List.of(user), false);
						} else {
							return new ActionTarget(user, a, List.of(tgt), false);
						}
					}
				}
			}
			//最大威力アクションを適用できる場所まで移動するよう指示
			return new ActionTarget(user, EnemyAIImpl.移動アクション, null, false);
		}

		@Override
		Actor getTgt(Enemy user) {
			if (isそろそろHP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろMP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろSAN回復したほうがいい(user) && getSANを自己回復できるアクション(user) != null) {
				return user;
			}
			return get一番体力が高いPC();
		}

		@Override
		public Point2D.Float targetLocation(Enemy user, Action a) {
			return getターゲットに届く位置(user, user.getStatus().getEffectedArea(a), this);
		}
	},
	/**
	 * 雑魚処理は一番弱い敵を狙う傾向があり、主に範囲攻撃をします
	 */
	雑魚処理 {
		@Override
		public ActionTarget getNextAction(Enemy user) {
			//自己ヒール
			Action a = getHPを自己回復できるアクション(user);
			if (isそろそろHP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getMPを自己回復できるアクション(user);
			if (isそろそろMP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getSANを自己回復できるアクション(user);
			if (isそろそろSAN回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}

			//自分にかけられるバフがあれば実施
			Action buf = get自分にかけられるバフでまだかかっていないもの(user);
			if (buf != null) {
				return new ActionTarget(user, buf, List.of(user), false);
			}
			Actor tgt = getTgt(user);
			for (Action aa : get対象者が多い順(user)) {
				if (is射程内(user, aa, tgt)) {
					if (!aa.checkResource(user.getStatus()).is足りないステータスあり()) {
						return new ActionTarget(user, aa, List.of(tgt), false);
					}
				}
			}
			//最大威力アクションを適用できる場所まで移動するよう指示
			return new ActionTarget(user, EnemyAIImpl.移動アクション, null, false);
		}

		@Override
		Actor getTgt(Enemy user) {
			if (isそろそろHP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろMP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろSAN回復したほうがいい(user) && getSANを自己回復できるアクション(user) != null) {
				return user;
			}
			return get一番体力が低いPC();
		}

		@Override
		public Point2D.Float targetLocation(Enemy user, Action a) {
			return getターゲットに届く位置(user, user.getStatus().getEffectedArea(a), this);
		}
	},
	/**
	 * 待ち伏せはあまり積極的に移動せず、射程に入った敵を攻撃します
	 */
	待ち伏せ {
		@Override
		public ActionTarget getNextAction(Enemy user) {
			//自己ヒール
			Action a = getHPを自己回復できるアクション(user);
			if (isそろそろHP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getMPを自己回復できるアクション(user);
			if (isそろそろMP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getSANを自己回復できるアクション(user);
			if (isそろそろSAN回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}

			//自分にかけられるバフがあれば実施
			Action buf = get自分にかけられるバフでまだかかっていないもの(user);
			if (buf != null) {
				return new ActionTarget(user, buf, List.of(user), false);
			}

			Actor tgt = getTgt(user);
			for (Action aa : get最大威力攻撃順(user)) {
				if (is射程内(user, aa, tgt)) {
					if (!aa.checkResource(user.getStatus()).is足りないステータスあり()) {
						return new ActionTarget(user, aa, List.of(tgt), false);
					}
				}
			}
			return new ActionTarget(user, EnemyAIImpl.防御アクション, null, false);
		}

		@Override
		Actor getTgt(Enemy user) {
			if (isそろそろHP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろMP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろSAN回復したほうがいい(user) && getSANを自己回復できるアクション(user) != null) {
				return user;
			}
			return get一番体力が低いPC();
		}

		@Override
		public Point2D.Float targetLocation(Enemy user, Action a) {
			return user.getSprite().getCenter();
		}
	},
	/**
	 * 支援はあまり攻撃せず、持っているバフデバフや状態異常付与を撒きます
	 */
	支援 {
		@Override
		public ActionTarget getNextAction(Enemy user) {
			//自己ヒール
			Action a = getHPを自己回復できるアクション(user);
			if (isそろそろHP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getMPを自己回復できるアクション(user);
			if (isそろそろMP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getSANを自己回復できるアクション(user);
			if (isそろそろSAN回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}

			//自分にかけられるバフがあれば実施
			Action buf = get自分にかけられるバフでまだかかっていないもの(user);
			if (buf != null) {
				return new ActionTarget(user, buf, List.of(user), false);
			}

			//ターゲットにかけられるバフがあればそれを実施
			Actor t = getTgt(user);
			Action 回復 = getターゲットにかけられるバフでまだかかっていないもの(user, (Enemy) t);
			if (回復 != null) {
				if (!回復.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, 回復, List.of(t), false);
				}
			}
			List<Actor> tgt = new ArrayList<>(GameSystem.getInstance().getParty());
			Collections.shuffle(tgt);
			for (Actor pc : tgt) {
				a = getPCにかけられるデバフ(user, pc);
				if (a != null) {
					if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
						return new ActionTarget(user, a, List.of(pc), false);
					}
				}
			}
			return new ActionTarget(user, EnemyAIImpl.防御アクション, null, false);

		}

		@Override
		Actor getTgt(Enemy user) {
			if (isそろそろHP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろMP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろSAN回復したほうがいい(user) && getSANを自己回復できるアクション(user) != null) {
				return user;
			}
			return get自分以外で一番体力が低いEnemy(user);
		}

		@Override
		public Point2D.Float targetLocation(Enemy user, Action a) {
			return getターゲットに届く位置(user, user.getStatus().getEffectedArea(a), this);
		}
	},
	/**
	 * 生存優先は敵の射程外に移動し続けます。もし遠距離攻撃を持っていたら攻撃します。 接近された場合回避に専念を選択することがあります。
	 */
	生存優先 {
		@Override
		public ActionTarget getNextAction(Enemy user) {
			//自己ヒール
			Action a = getHPを自己回復できるアクション(user);
			if (isそろそろHP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getMPを自己回復できるアクション(user);
			if (isそろそろMP回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}
			a = getSANを自己回復できるアクション(user);
			if (isそろそろSAN回復したほうがいい(user) && a != null) {
				if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, a, List.of(user), false);
				}
			}

			//自分にかけられるバフがあれば実施
			Action buf = get自分にかけられるバフでまだかかっていないもの(user);
			if (buf != null) {
				return new ActionTarget(user, buf, List.of(user), false);
			}

			//ターゲットにかけられるバフがあればそれを実施
			Actor t = getTgt(user);
			Action 回復 = getターゲットにかけられるバフでまだかかっていないもの(user, (Enemy) t);
			if (回復 != null) {
				if (!回復.checkResource(user.getStatus()).is足りないステータスあり()) {
					return new ActionTarget(user, 回復, List.of(t), false);
				}
			}
			List<Actor> tgt = new ArrayList<>(GameSystem.getInstance().getParty());
			Collections.shuffle(tgt);
			for (Actor pc : tgt) {
				a = getPCにかけられるデバフ(user, pc);
				if (a != null) {
					if (!a.checkResource(user.getStatus()).is足りないステータスあり()) {
						return new ActionTarget(user, a, List.of(pc), false);
					}
				}
			}
			return new ActionTarget(user, EnemyAIImpl.回避アクション, null, false);

		}

		@Override
		Actor getTgt(Enemy user) {
			if (isそろそろHP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろMP回復したほうがいい(user) && getHPを自己回復できるアクション(user) != null) {
				return user;
			}
			if (isそろそろSAN回復したほうがいい(user) && getSANを自己回復できるアクション(user) != null) {
				return user;
			}
			return get自分以外で一番体力が低いEnemy(user);
		}

		@Override
		public Point2D.Float targetLocation(Enemy user, Action a) {
			return getPCの射程外の位置(user, user.getStatus().getEffectedArea(a), this);
		}
	};

	private static Action 移動アクション = ActionStorage.getInstance().actionOf(BattleConfig.ActionID.移動);
	private static Action 防御アクション = ActionStorage.getInstance().actionOf(BattleConfig.ActionID.防御);
	private static Action 回避アクション = ActionStorage.getInstance().actionOf(BattleConfig.ActionID.回避);

	@Nullable
	static Action getHPを自己回復できるアクション(Enemy e) {
		for (Action a : e.getStatus().getActions()) {
			if (a.getAllEvents()
					.stream()
					.anyMatch(p -> p.getEventType() == ActionEventType.ステータス回復
					&& p.getTgtStatusKey() == StatusKey.体力)) {
				return a;
			}
		}
		return null;
	}

	static boolean isそろそろHP回復したほうがいい(Enemy e) {
		return e.getStatus().getEffectedStatus().get(StatusKey.体力).get割合() < 0.6f;
	}

	@Nullable
	static Action getMPを自己回復できるアクション(Enemy e) {
		for (Action a : e.getStatus().getActions()) {
			if (a.getAllEvents()
					.stream()
					.anyMatch(p -> p.getEventType() == ActionEventType.ステータス回復
					&& p.getTgtStatusKey() == StatusKey.魔力)) {
				return a;
			}
		}
		return null;
	}

	static boolean isそろそろMP回復したほうがいい(Enemy e) {
		return e.getStatus().getEffectedStatus().get(StatusKey.魔力).get割合() < 0.6f;
	}

	@Nullable
	static Action getSANを自己回復できるアクション(Enemy e) {
		for (Action a : e.getStatus().getActions()) {
			if (a.getAllEvents()
					.stream()
					.anyMatch(p -> p.getEventType() == ActionEventType.ステータス回復
					&& p.getTgtStatusKey() == StatusKey.正気度)) {
				return a;
			}
		}
		return null;
	}

	static boolean isそろそろSAN回復したほうがいい(Enemy e) {
		return e.getStatus().getEffectedStatus().get(StatusKey.正気度).get割合() < 0.6f;
	}

	static Point2D.Float getPCの射程外の位置(Enemy e, float area, EnemyAIImpl ai) {
		//eからターゲットへの方向と逆に移動。
		Point2D.Float tgtLocation = ai.getTgt(e).getSprite().getCenter();
		EmptySprite s = new EmptySprite(e.getSprite().getLocation(), new Dimension(2, 2));
		s.setVector(new KVector(e.getSprite().getCenter(), tgtLocation).reverse());
		s.getVector().setSpeed(2f);
		while (true) {
			float d = (float) s.getCenter().distance(tgtLocation);
			if (d > area) {
				return s.getCenter();
			}
			s.move();
		}
	}

	static Point2D.Float getターゲットに届く位置(Enemy e, float distance, EnemyAIImpl ai) {
		Actor tgt = ai.getTgt(e);
		Point2D.Float tgtLocation = tgt.getSprite().getCenter();
		if (tgt.equals(e)) {
			return tgtLocation;
		}
		EmptySprite s = new EmptySprite(e.getSprite().getLocation(), new Dimension(2, 2));
		s.setVector(new KVector(e.getSprite().getCenter(), tgtLocation));
		s.getVector().setSpeed(2f);
		while (true) {
			if (s.getCenter().distance(tgtLocation) < distance) {
				return s.getCenter();
			}
			s.move();
		}

	}

	static boolean is射程内(Enemy e, Action a, Actor tgt) {
		return e.getStatus().getEffectedArea(a) < (e.getSprite().getCenter().distance(tgt.getSprite().getCenter()));
	}

	abstract Actor getTgt(Enemy user);

	static boolean is射程内にターゲットあり(Enemy e, Action a) {
		if (is敵に向けて実行するアクションか(a)) {
			return !BattleTargetSystem.getInstance().allPartyOf(e.getSprite().getCenter(),
					e.getStatus().getEffectedArea(a)).isEmpty();
		} else {
			return !BattleTargetSystem.getInstance().allEnemyOf(e.getSprite().getCenter(),
					e.getStatus().getEffectedArea(a)).isEmpty();
		}
	}

	static boolean is敵に向けて実行するアクションか(Action a) {
		int r = 0;
		for (ActionEvent e : a.getMainEvents()) {
			r += e.getValue() * 100;
		}
		return r < 0;
	}

	static List<Action> get最大威力攻撃順(Enemy e) {
		class SortAction implements Comparable<SortAction> {

			final Action a;

			SortAction(Action a) {
				this.a = a;
			}

			int getEffect() {
				int r = 0;
				for (ActionEvent e : a.getMainEvents()) {
					r += Math.abs(e.getValue() * 100);
				}
				return r;
			}

			@Override
			public int compareTo(SortAction o) {
				return getEffect() - o.getEffect();
			}
		}

		return e.getStatus().getActions().stream().map(p -> new SortAction(p)).sorted().map(p -> p.a).toList();

	}

	static List<Action> get対象者が多い順(Enemy e) {
		class SortAction implements Comparable<SortAction> {

			final Action a;

			SortAction(Action a) {
				this.a = a;
			}

			int getEffect() {
				int r = 0;
				if (a.getTgtType().toString().contains("グループ")) {
					if (a.getTgtType().toString().contains("敵")) {
						r += GameSystem.getInstance().getParty().size();
					} else {
						r += BattleSystem.getInstance().getEnemies().size();
					}
				} else if (a.getTgtType().toString().contains("全員")) {
					r += GameSystem.getInstance().getParty().size();
					r += BattleSystem.getInstance().getEnemies().size();
				} else {
					r += 1;
				}
				return r;
			}

			@Override
			public int compareTo(SortAction o) {
				return o.getEffect() - getEffect();
			}
		}

		return e.getStatus()
				.getActions()
				.stream()
				.map(p -> new SortAction(p))
				.sorted()
				.map(p -> p.a)
				.toList();
	}

	static List<Action> get最大威力回復順(Enemy e) {
		class SortAction implements Comparable<SortAction> {

			final Action a;

			SortAction(Action a) {
				this.a = a;
			}

			int getEffect() {
				int r = 0;
				for (ActionEvent e : a.getMainEvents()) {
					r += Math.abs(e.getValue() * 100);
				}
				return r;
			}

			@Override
			public int compareTo(SortAction o) {
				return o.getEffect() - getEffect();
			}
		}

		return e.getStatus()
				.getActions()
				.stream()
				.map(p -> new SortAction(p))
				.sorted()
				.map(p -> p.a)
				.toList();
	}

	@Nullable
	static Action getPCにかけられるデバフ(Enemy e, Actor pc) {
		List<Action> list = e.getStatus().getActions().asList();
		Collections.shuffle(list);
		for (Action a : list) {
			for (ActionEvent ev : a.getAllEvents()) {
				if (ev.getEventType() == ActionEventType.状態異常付与) {
					if (ev.getTgtConditionKey().isデバフ()
							&& !pc.getStatus().hasCondition(ev.getTgtConditionKey())) {
						if (!a.checkResource(e.getStatus()).is足りないステータスあり()) {
							return a;
						}
					}
				}
			}
		}
		return null;
	}

	//自分指定可能
	@Nullable
	static Action getターゲットにかけられるバフでまだかかっていないもの(Enemy e, Enemy tgt) {
		List<Action> list = e.getStatus().getActions().asList();
		Collections.shuffle(list);
		for (Action a : list) {
			for (ActionEvent ev : a.getAllEvents()) {
				if (ev.getEventType() == ActionEventType.状態異常付与) {
					if (ev.getTgtConditionKey().isバフ() && !tgt.getStatus().hasCondition(ev.getTgtConditionKey())) {

						if (!a.checkResource(e.getStatus()).is足りないステータスあり()) {
							return a;
						}
					}
				}
			}
		}
		return null;
	}

	@Nullable
	static Action get自分にかけられるバフでまだかかっていないもの(Enemy e) {
		List<Action> list = e.getStatus().getActions().asList();
		Collections.shuffle(list);
		for (Action a : list) {
			for (ActionEvent ev : a.getAllEvents()) {
				if (ev.getEventType() == ActionEventType.状態異常付与) {
					if (ev.getTgtConditionKey().isバフ() && !e.getStatus().hasCondition(ev.getTgtConditionKey())) {
						if (!a.checkResource(e.getStatus()).is足りないステータスあり()) {
							return a;
						}
					}
				}
			}
		}
		return null;
	}

	static Actor get一番近いPC(Enemy e) {
		float distance = Float.MAX_VALUE;
		Actor res = null;
		for (Actor ee : GameSystem.getInstance().getParty()) {
			for (Action a : ee.getStatus().getActions()) {
				float d = ee.getStatus().getEffectedArea(a);
				if (d < distance) {
					distance = d;
					res = ee;
				}
			}
		}
		return res;
	}

	static Actor get一番体力が低いPC() {
		return GameSystem.getInstance().getParty()
				.stream()
				.max((p1, p2)
						-> (int) (p2.getStatus().getEffectedStatus().get(StatusKey.体力).getValue()
				- p1.getStatus().getEffectedStatus().get(StatusKey.体力).getValue())).get();
	}

	static Actor get一番体力が高いPC() {
		return GameSystem.getInstance().getParty()
				.stream()
				.max((p1, p2)
						-> (int) (p1.getStatus().getEffectedStatus().get(StatusKey.体力).getValue()
				- p2.getStatus().getEffectedStatus().get(StatusKey.体力).getValue())).get();

	}

	@NotNull
	static Actor get自分以外で一番近いEnemy(Enemy e) {
		float distance = Float.MAX_VALUE;
		Enemy res = null;
		for (Enemy ee : BattleSystem.getInstance().getEnemies()) {
			if (!ee.equals(e)) {
				float d = (float) ee.getSprite().getCenter().distance(e.getSprite().getCenter());
				if (d < distance) {
					distance = d;
					res = ee;
				}
			}
		}
		assert res != null : "EAI res is null";
		return res;
	}

	@NotNull
	static Actor get自分以外で一番体力が低いEnemy(Enemy e) {
		Enemy res = null;
		for (Enemy ee : BattleSystem.getInstance().getEnemies()) {
			if (!ee.equals(e)) {
				if (res == null) {
					res = ee;
				}
				int min = (int) res.getStatus().getEffectedStatus().get(StatusKey.体力).getValue();
				int me = (int) ee.getStatus().getEffectedStatus().get(StatusKey.体力).getValue();
				if (me < min) {
					res = ee;
				}
			}
		}
		assert res != null : "EAI res is null";
		return res;
	}

	@Override
	public String getName() {
		return toString();
	}

}
