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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GameSystemで使用されるI18Nキーです。これらがI18Nクラスにない場合、様々な処理が失敗します。
 *
 * @vesion 1.0.0 - 2023/05/20_15:45:54<br>
 * @author Shinacho<br>
 */
public class GameSystemI18NKeys {
//
//	public static void main(String[] args) {
//		for (var v : GameSystemI18NKeys.allKeysJAData()) {
//			System.out.println(v);
//		}
//	}
//
//	private static List<String> of(Class<?> c) {
//		List<String> r = new ArrayList<>();
//		try {
//			for (Field ff : List.of(c.getDeclaredFields())) {
//				r.add(ff.get(null).toString());
//			}
//		} catch (Exception ex) {
//		}
//		return r;
//	}
//
//	public static List<String> allKeysJAData() {
//		List<String> r = new ArrayList<>();
//		r.add("[GAME_SYSTEM]");
//		r.addAll(of(GameSystemI18NKeys.class));
//		r.add("[ERROR_MSG]");
//		r.addAll(of(ErrorMsg.class));
//		r.add("[COUNT_KEY]");
//		r.addAll(of(CountKey.class));
//		//関連ENUMを追加
//		r.add("[StatusKey]");
//		r.addAll(List.of(StatusKey.values()).stream().map(p -> p.toString()).toList());
//		r.add("[AttributeKey]");
//		r.addAll(List.of(AttributeKey.values()).stream().map(p -> p.toString()).toList());
//		r.add("[ConditionKey]");
//		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.toString()).toList());
//		r.add("[ItemStyle]");
//		r.addAll(List.of(ItemStyle.values()).stream().map(p -> p.toString()).toList());
//		r.add("[ItemEnchant]");
//		r.addAll(List.of(ItemEnchant.values()).stream().map(p -> p.toString()).toList());
//		r.add("[CharaAbility]");
//		r.addAll(List.of(CharaAbility.values()).stream().map(p -> p.toString()).toList());
//		r.add("[Action.ターゲットモード]");
//		r.addAll(List.of(Action.ターゲットモード.values()).stream().map(p -> p.toString()).toList());
//		r.add("[Action.死亡者ターゲティング]");
//		r.addAll(List.of(Action.死亡者ターゲティング.values()).stream().map(p -> p.toString()).toList());
//		r.add("[ActionEventType]");
//		r.addAll(List.of(ActionEventType.values()).stream().map(p -> p.toString()).toList());
//		r.add("[WeaponType]");
//		r.addAll(List.of(WeaponType.values()).stream().map(p -> p.toString()).toList());
//		r.add("[EqipSlot]");
//		r.addAll(List.of(EqipSlot.values()).stream().map(p -> p.toString()).toList());
//		r.add("[ActionType]");
//		r.addAll(List.of(ActionType.values()).stream().map(p -> p.toString()).toList());
//		r.add("[Race]");
//		r.addAll(List.of(Race.values()).stream().map(p -> p.toString()).toList());
//		r.add("[BattleResult]");
//		r.addAll(List.of(BattleResult.values()).stream().map(p -> p.toString()).toList());
//		r.add("[BattleSystem.NoTgtDesc]");
//		r.addAll(List.of(BattleSystem.NoTgtDesc.values()).stream().map(p -> p.toString()).toList());
//		//関連ENUMのDESCを追加
//		r.add("[ConditionKey.startMsg]");
//		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.getStartMsgI18NK()).toList());
//		r.add("[ConditionKey.execMsg]");
//		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.getExecMsgI18NK()).toList());
//		r.add("[ConditionKey.endMsg]");
//		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.getEndMsgI18NK()).toList());
//		r.add("[ConditionKey.desc]");
//		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.getDescI18NK()).toList());
//		r.add("[ItemStyle.desc]");
//		r.addAll(List.of(ItemStyle.values()).stream().map(p -> p.getDescI18NKey()).toList());
//		r.add("[ItemEnchant.desc]");
//		r.addAll(List.of(ItemEnchant.values()).stream().map(p -> p.getDescI18NKey()).toList());
//		r.add("[CharaAbility.desc]");
//		r.addAll(List.of(CharaAbility.values()).stream().map(p -> p.getDescI18NK()).toList());
//		r.add("[InfoWindow.Mode]");
//		r.addAll(Arrays.asList(InfoWindow.Mode.values()).stream().map(p->p.toString()).toList());
//		r = r.stream().distinct().filter(p -> !p.isEmpty()).map(p -> {
//			if (!p.contains("[")) {
//				return p + "=" + p;
//			}
//			return p;
//		}).toList();
//
//		List<String> res = new ArrayList<>();
//		for (var v : r) {
//			if (!v.startsWith("[") && v.contains("X")) {
//				String key = v.split("=")[0];
//				String value = v.split("=")[1];
//				StringBuilder sb = new StringBuilder();
//				for (int i = 0, p = 0; i < value.length(); i++) {
//					char a = value.charAt(i);
//					if (a != 'X') {
//						sb.append(a);
//					} else {
//						sb.append("!" + p);
//						p++;
//					}
//				}
//				res.add(key + "=" + sb.toString());
//			} else {
//				res.add(v);
//			}
//		}
//
//		return res;
//	}
//
//	public static void main(String... args) throws Exception {
//		List<String> data = new ArrayList<>();
//		data.addAll(get(GameSystemI18NKeys.class));
//		data.addAll(get(GameSystemI18NKeys.CountKey.class));
//		data.addAll(get(GameSystemI18NKeys.ErrorMsg.class));
//
//		data.forEach(System.out::println);
//	}
//
//	private static List<String> get(Class<?> c) throws IllegalArgumentException, IllegalAccessException {
//		Field[] f = c.getDeclaredFields();
//		List<String> res = new ArrayList<>();
//		for (var v : f) {
//			String val = v.get(null).toString();
//			if (!v.getName().equals(val)) {
//				throw new RuntimeException(v.getName());
//			}
//			res.add("\"" + val + "\"" + "," + "\"" + val(val) + "\"");
//		}
//		return res;
//	}
//
//	private static String val(String v) {
//		int i = 0;
//		StringBuilder sb = new StringBuilder();
//		for (char c : v.toCharArray()) {
//			if (c == 'X') {
//				sb.append("!" + i++);
//			} else {
//				sb.append(c);
//			}
//		}
//		return sb.toString();
//
//	}
	public static final String はい = "はい";
	public static final String いいえ = "いいえ";
	public static final String 話す = "話す";
	public static final String 調べる = "調べる";
	
	public static final String X回発動する = "X回発動する";

	public static final String 効果 = "効果";
	public static final String 混乱している = "混乱している";

	public static final String 確認 = "確認";
	public static final String 本当に終了しますか = "本当に終了しますか";

	public static final String の魔術書 = "の魔術書";

	public static final String 両手持ち = "両手持ち";
	public static final String 両手持ちすると右手の効果が２倍になる = "両手持ちすると右手の効果が２倍になる";

	public static final String あとX個持てる = "あとX個持てる";
	public static final String 諦める = "諦める";
	public static final String Xを手に入れた誰が持つ = "Xを手に入れた誰が持つ";

	public static final String 対象効果 = "対象効果";
	public static final String 自身への効果 = "自身への効果";

	public static final String Xの行動 = "Xの行動";
	public static final String 範囲 = "範囲";
	public static final String 属性 = "属性";
	public static final String 基礎威力 = "基礎威力";
	public static final String 魔術利用可能 = "魔術利用可能";
	public static final String 魔術利用不可 = "魔術利用不可";
	public static final String Xの被属性 = "Xの被属性";
	public static final String Xの与属性 = "Xの与属性";
	public static final String Xの状態異常耐性 = "Xの状態異常耐性";
	public static final String 渡す = "渡す";
	public static final String 解体 = "解体";
	public static final String 捨てる = "捨てる";
	public static final String Xを = "Xを";
	public static final String Xの = "Xの";
	public static final String Xは = "Xは";
	public static final String X回復した = "X回復した";
	public static final String Xに = "Xに";
	public static final String Xのダメージ = "Xのダメージ";
	public static final String Xを誰に渡す = "Xを誰に渡す";
	public static final String の魔法が使えるようになる = "の魔法が使えるようになる";
	public static final String 価値 = "価値";
	public static final String 解体すると以下を入手する = "解体すると以下を入手する";
	public static final String ダメージ = "ダメージ";
	public static final String 回復 = "回復";
	public static final String 確率 = "確率";
	public static final String 対象 = "対象";
	public static final String この効果は特殊なもので分析ができない = "この効果は特殊なもので分析ができない";
	public static final String 値 = "値";
	public static final String Xを本当に解体する = "Xを本当に解体する";
	public static final String Xを本当にすてる = "Xを本当にすてる";
	public static final String XはXにXを渡した = "XはXにXを渡した";
	public static final String XはXを持ち替えた = "XはXを持ち替えた";
	public static final String XはXを捨てた = "XはXを捨てた";
	public static final String XはXを解体した = "XはXを解体した";
	public static final String XをX個入手した = "XをX個入手した";
	public static final String 何も持っていない = "何も持っていない";
	public static final String Xの発生中の効果 = "Xの発生中の効果";
	public static final String 使う = "使う";
	public static final String 装備 = "装備";
	public static final String XはXを使用した = "XはXを使用した";
	public static final String しかし効果がなかった = "しかし効果がなかった";
	public static final String しかしXには効果がなかった = "しかしXには効果がなかった";
	public static final String 持ち物が多すぎてXを外せない = "持ち物が多すぎてXを外せない";
	public static final String 左手に装備 = "左手に装備";
	public static final String 両手に装備 = "両手に装備";
	public static final String Xを誰に使う = "Xを誰に使う";
	public static final String Xを外した = "Xを外した";
	public static final String Xを装備した = "Xを装備した";
	public static final String Xは装備できない = "Xは装備できない";
	public static final String Xは左手に装備できない = "Xは左手に装備できない";
	public static final String Xを左手に装備した = "Xを左手に装備した";
	public static final String Xを両手持ちで装備した = "Xを両手持ちで装備した";
	public static final String 装備スロット = "装備スロット";
	public static final String 様式 = "様式";
	public static final String エンチャント = "エンチャント";
	public static final String 武器種別 = "武器種別";
	public static final String このアイテムは売ったり捨てたり解体したりできない = "このアイテムは売ったり捨てたり解体したりできない";
	public static final String ダメージ計算ステータス = "ダメージ計算ステータス";
	public static final String このアイテムは戦闘中使える = "このアイテムは戦闘中使える";
	public static final String このアイテムはフィールドで使える = "このアイテムはフィールドで使える";
	public static final String 攻撃回数 = "攻撃回数";
	public static final String このアイテムはあとX回強化できる = "このアイテムはあとX回強化できる";
	public static final String このアイテムは強化できない = "このアイテムは強化できない";
	public static final String このアイテムは解体できる = "このアイテムは解体できる";
	public static final String このアイテムは解体できない = "このアイテムは解体できない";
	public static final String 解体＿強化時の素材 = "解体＿強化時の素材";
	public static final String ステータス = "ステータス";
	public static final String 被属性 = "被属性";
	public static final String 与属性 = "与属性";
	public static final String 状態異常耐性 = "状態異常耐性";
	public static final String Xはこれ以上物を持てない = "Xはこれ以上物を持てない";
	public static final String このアイテムは捨てられない = "このアイテムは捨てられない";
	public static final String メインクエスト = "メインクエスト";
	public static final String サブクエスト = "サブクエスト";
	public static final String 後列 = "後列";
	public static final String 前列 = "前列";
	public static final String 前列後列設定 = "前列後列設定";
	public static final String 特性 = "特性";
	public static final String XはXを詠唱した = "XはXを詠唱した";
	public static final String しかしこの魔法はフィールドでは使えない = "しかしこの魔法はフィールドでは使えない";
	public static final String しかしXが足りない = "しかしXが足りない";
	public static final String 全員 = "全員";
	public static final String Xが = "Xが";
	public static final String 戦闘効果 = "戦闘効果";
	public static final String 詠唱時間 = "詠唱時間";
	public static final String ターン = "ターン";
	public static final String この魔法は戦闘中使えない = "この魔法は戦闘中使えない";
	public static final String XをXターン付与する = "XをXターン付与する";
	public static final String Xを付与する = "Xを付与する";
	public static final String Xを解除する = "Xを解除する";
	public static final String 被耐性Xを変更する = "被耐性Xを変更する";
	public static final String 与耐性Xを変更する = "与耐性Xを変更する";
	public static final String X耐性を変更する = "X耐性を変更する";
	public static final String Xを失う = "Xを失う";
	public static final String Xを入手する = "Xを入手する";
	public static final String Xを回復する = "Xを回復する";
	public static final String Xにダメージを与える = "Xにダメージを与える";
	public static final String 計算方法 = "計算方法";
	public static final String ダメージ計算 = "ダメージ計算";
	public static final String 直接作用 = "直接作用";
	public static final String 乗算 = "乗算";
	public static final String 値になる = "値になる";
	public static final String 最大値になる = "最大値になる";
	public static final String ゼロになる = "ゼロになる";
	public static final String フィールド効果 = "フィールド効果";
	public static final String この魔法はフィールドでは使えない = "この魔法はフィールドでは使えない";
	public static final String ターゲット = "ターゲット";
	public static final String ターゲット選択可否 = "ターゲット選択可否";
	public static final String 死亡者ターゲット = "死亡者ターゲット";
	public static final String 魔術 = "魔術";
	public static final String 使える魔術はない = "使える魔術はない";
	public static final String Xの装備 = "Xの装備";
	public static final String なし = "なし";
	public static final String 被耐性変化の術式 = "被耐性変化の術式";
	public static final String 与耐性変化の術式 = "与耐性変化の術式";
	public static final String 状態耐性変化の術式 = "状態耐性変化の術式";
	public static final String アイテムロストの術式 = "アイテムロストの術式";
	public static final String アイテム追加の術式 = "アイテム追加の術式";
	public static final String X回復の術式 = "X回復の術式";
	public static final String Xダメージの術式 = "Xダメージの術式";
	public static final String X回復X回の術式 = "X回復X回の術式";
	public static final String XダメージX回の術式 = "XダメージX回の術式";
	public static final String 状態付与の術式 = "状態付与の術式";
	public static final String 状態解除の術式 = "状態解除の術式";
	public static final String どうする = "どうする";
	public static final String 弓は両手で持つ必要がある = "弓は両手で持つ必要がある";
	public static final String XがX体現れた = "XがX体現れた";
	public static final String 戦闘結果 = "戦闘結果";
	public static final String 獲得経験値 = "獲得経験値";
	public static final String 獲得アイテム = "獲得アイテム";
	public static final String 獲得物資 = "獲得物資";
	public static final String Xはレベルアップできる = "Xはレベルアップできる";
	public static final String XはXの詠唱を開始した = "XはXの詠唱を開始した";
	public static final String Xは逃走した = "Xは逃走した";
	public static final String Xは逃走しようとした = "Xは逃走しようとした";
	public static final String しかし戦闘エリアの中心にいては逃げられない = "しかし戦闘エリアの中心にいては逃げられない";
	public static final String Xは防御に専念した = "Xは防御に専念した";
	public static final String Xは回避に専念した = "Xは回避に専念した";
	public static final String Xは様子をうかがっている = "Xは様子をうかがっている";
	public static final String Xは移動した = "Xは移動した";
	public static final String XのX = "XのX";
	public static final String XはXを使った = "XはXを使った";
	public static final String しかしうまくきまらなかった = "しかしうまくきまらなかった";
	public static final String しかしXは反射した = "しかしXは反射した";
	public static final String しかしXは吸収した = "しかしXは吸収した";
	public static final String Xに平均Xのダメージ = "Xに平均Xのダメージ";
	public static final String 装備中 = "装備中";
	public static final String しかしXは行動力が０のため移動できない = "しかしXは行動力が０のため移動できない";
	public static final String 装備解除 = "装備解除";
	public static final String Xは混乱している = "Xは混乱している";
	public static final String 移動 = "移動";
	public static final String 確定 = "確定";
	public static final String 防御 = "防御";
	public static final String 回避 = "回避";
	public static final String 状態 = "状態";
	public static final String 逃走 = "逃走";
	public static final String 自身の武器装備を解除して敵のドロップアイテムに追加する = "自身の武器装備を解除して敵のドロップアイテムに追加する";
	public static final String Xを召喚する = "Xを召喚する";
	public static final String 友好的存在召喚の術式 = "友好的存在召喚の術式";
	public static final String 敵対的存在召喚の術式 = "敵対的存在召喚の術式";
	public static final String 使用者のXを外し戦闘に勝利したとき入手する = "使用者のXを外し戦闘に勝利したとき入手する";
	public static final String ドロップアイテム追加の術式 = "ドロップアイテム追加の術式";
	public static final String 対象者は直ちにX回行動できる = "対象者は直ちにX回行動できる";
	public static final String 解除の術式 = "解除の術式";
	public static final String 即時行動の術式 = "即時行動の術式";
	public static final String X回 = "X回";
	public static final String 対象者はこのターンの最後にX回行動できる = "対象者はこのターンの最後にX回行動できる";
	public static final String 遅延行動の術式 = "遅延行動の術式";
	public static final String 対象者は魔法詠唱を中断する = "対象者は魔法詠唱を中断する";
	public static final String 詠唱中断の術式 = "詠唱中断の術式";
	public static final String 対象者の詠唱完了イベントをXターン移動する = "対象者の詠唱完了イベントをXターン移動する";
	public static final String 詠唱時間変更の術式 = "詠唱時間変更の術式";
	public static final String 対象のクローンを召喚する = "対象のクローンを召喚する";
	public static final String クローニングの術式 = "クローニングの術式";
	public static final String 対象者の行動順を早める = "対象者の行動順を早める";
	public static final String ヘイストの術式 = "ヘイストの術式";
	public static final String 対象者はそのターン行動できなくなる = "対象者はそのターン行動できなくなる";
	public static final String 行動阻止の術式 = "行動阻止の術式";
	public static final String そのターンの行動順を反転させる = "そのターンの行動順を反転させる";
	public static final String トリックルームの術式 = "トリックルームの術式";
	public static final String 対象者は最大Xノックバックする = "対象者は最大Xノックバックする";
	public static final String ノックバックの術式 = "ノックバックの術式";
	public static final String Xはノックバックした = "Xはノックバックした";
	public static final String 対象者は中心からXの範囲内に転送される = "対象者は中心からXの範囲内に転送される";
	public static final String 集結の術式 = "集結の術式";
	public static final String 対象者はすぐ逃げられる位置に転送される = "対象者はすぐ逃げられる位置に転送される";
	public static final String 退避の術式 = "退避の術式";
	public static final String 対象者は一番近い敵対者のそばに転送される = "対象者は一番近い敵対者のそばに転送される";
	public static final String 接近の術式 = "接近の術式";
	public static final String 術者は対象者のそばに転送される = "術者は対象者のそばに転送される";
	public static final String 術者転送の術式 = "術者転送の術式";
	public static final String 術者は対象者と位置が入れ替わる = "術者は対象者と位置が入れ替わる";
	public static final String 位置交換の術式 = "位置交換の術式";
	public static final String 術者は候補からいずれかのアイテムを手に入れる = "術者は候補からいずれかのアイテムを手に入れる";
	public static final String ランダムアイテムの術式 = "ランダムアイテムの術式";
	public static final String 戦闘が終了し逃走扱いになる = "戦闘が終了し逃走扱いになる";
	public static final String 強制逃走の術式 = "強制逃走の術式";
	public static final String Xにワープする = "Xにワープする";
	public static final String マップ間ワープの術式 = "マップ間ワープの術式";
	public static final String 現在のマップのランダムな出入り口に移動する = "現在のマップのランダムな出入り口に移動する";
	public static final String テレポートの術式 = "テレポートの術式";
	public static final String 現在のセーブデータを破壊しセーブせずにゲームを終了した場合はセーブデータをロストする = "現在のセーブデータを破壊しセーブせずにゲームを終了した場合はセーブデータをロストする";
	public static final String 現在記録抹消の術式 = "現在記録抹消の術式";
	public static final String ゲームがセーブされずに終了する = "ゲームがセーブされずに終了する";
	public static final String 次元崩壊の術式 = "次元崩壊の術式";
	public static final String 他のセーブデータを破壊する = "他のセーブデータを破壊する";
	public static final String 別次元破壊の術式 = "別次元破壊の術式";
	public static final String すべてのセーブデータを破壊する = "すべてのセーブデータを破壊する";
	public static final String 全空間破壊の術式 = "全空間破壊の術式";
	public static final String 術者から対象者へビームを照射する = "術者から対象者へビームを照射する";
	public static final String 光線の術式 = "光線の術式";
	public static final String 独自効果の術式 = "独自効果の術式";
	public static final String このダメージは選択したファイルによって変動する = "このダメージは選択したファイルによって変動する";
	public static final String このダメージは選択したファイルのサイズによって変動する = "このダメージは選択したファイルのサイズによって変動する";
	public static final String 上位者の情報の術式 = "上位者の情報の術式";
	public static final String 上位者の巨大情報の術式 = "上位者の巨大情報の術式";
	public static final String 倒した敵の数に応じて属性とダメージが決まる = "倒した敵の数に応じて属性とダメージが決まる";
	public static final String 勇者の絶望の術式 = "勇者の絶望の術式";
	public static final String ターン数が小さいほどダメージが上がる = "ターン数が小さいほどダメージが上がる";
	public static final String 速攻戦の術式 = "速攻戦の術式";
	public static final String ターン数が大きいほどダメージが上がる = "ターン数が大きいほどダメージが上がる";
	public static final String 遅滞戦術の術式 = "遅滞戦術の術式";
	public static final String 使用しているコンピュータのコアによって属性とダメージが決まる = "使用しているコンピュータのコアによって属性とダメージが決まる";
	public static final String 上位者の脳の術式 = "上位者の脳の術式";
	public static final String 持っているアイテムの重さによってダメージが決まる = "持っているアイテムの重さによってダメージが決まる";
	public static final String ヘビーボンバーの術式 = "ヘビーボンバーの術式";
	public static final String Xターン内の詠唱完了を反転させる = "Xターン内の詠唱完了を反転させる";
	public static final String 詠唱時間逆転の術式 = "詠唱時間逆転の術式";
	public static final String 上位者の情報を閲覧する = "上位者の情報を閲覧する";
	public static final String 上位者の情報閲覧の術式 = "上位者の情報閲覧の術式";
	public static final String このダメージは倒した敵の数が多いほど大きくなる = "このダメージは倒した敵の数が多いほど大きくなる";
	public static final String このダメージは倒した敵の数が少ないほど大きくなる = "このダメージは倒した敵の数が少ないほど大きくなる";
	public static final String 慈悲深き聖者の術式 = "慈悲深き聖者の術式";
	public static final String このダメージはターン数が小さいほど大きくなる = "このダメージはターン数が小さいほど大きくなる";
	public static final String このダメージはターン数が経過しているほど大きくなる = "このダメージはターン数が経過しているほど大きくなる";
	public static final String このダメージは使用しているコンピュータのコア数により変化する = "このダメージは使用しているコンピュータのコア数により変化する";
	public static final String このダメージはアイテムをたくさん持っているほど大きくなる = "このダメージはアイテムをたくさん持っているほど大きくなる";
	public static final String このダメージは自身の体力が減っているほど高くなる = "このダメージは自身の体力が減っているほど高くなる";
	public static final String このダメージは自身の魔力が減っているほど高くなる = "このダメージは自身の魔力が減っているほど高くなる";
	public static final String このダメージは自身の正気度が減っているほど高くなる = "このダメージは自身の正気度が減っているほど高くなる";
	public static final String 背水の陣の術式 = "背水の陣の術式";
	public static final String 精神限界の術式 = "精神限界の術式";
	public static final String 狂気の笑みの術式 = "狂気の笑みの術式";
	public static final String Xをこのターンの最後にX回発動する = "Xをこのターンの最後にX回発動する";
	public static final String Xをこのターンの最初にX回発動する = "Xをこのターンの最初にX回発動する";
	public static final String 高速詠唱の術式 = "高速詠唱の術式";
	public static final String 多重発動の術式 = "多重発動の術式";
	public static final String このイベントはランダムな属性とダメージになる = "このイベントはランダムな属性とダメージになる";
	public static final String ランダムシードの術式 = "ランダムシードの術式";
	public static final String 術者のX装備の攻撃回数をX増減する = "術者のX装備の攻撃回数をX増減する";
	public static final String 攻撃回数変化の術式 = "攻撃回数変化の術式";
	public static final String 術者のX装備の価値をXにする = "術者のX装備の価値をXにする";
	public static final String 装備価値変更の術式 = "装備価値変更の術式";
	public static final String 術者のX装備の価値にXを加算する = "術者のX装備の価値にXを加算する";
	public static final String 装備価値加算の術式 = "装備価値加算の術式";
	public static final String 対象の耐性が参照される = "対象の耐性が参照される";
	public static final String 戦闘に勝利したときXを入手する = "戦闘に勝利したときXを入手する";

	public static final String 指定したマップの指定した座標にワープする = "指定したマップの指定した座標にワープする";
	public static final String 転送の術式 = "転送の術式";
	public static final String 対象者は行動がそのターンの最後になる = "対象者は行動がそのターンの最後になる";
	public static final String スローの術式 = "スローの術式";
	public static final String 対象者は術者からXの範囲内に転送される = "対象者は術者からXの範囲内に転送される";
	public static final String 引き寄せの術式 = "引き寄せの術式";
	public static final String 術者のX装備を解除する = "術者のX装備を解除する";
	public static final String パージの術式 = "パージの術式";
	public static final String 即時追加行動の術式 = "即時追加行動の術式";
	public static final String 遅延追加行動の術式 = "遅延追加行動の術式";
	public static final String ターゲットからXの距離内の同じチームの全員にも作用する = "ターゲットからXの距離内の同じチームの全員にも作用する";
	public static final String 放射の術式 = "放射の術式";
	public static final String ターゲットからXの距離内の全員にも作用する = "ターゲットからXの距離内の全員にも作用する";
	public static final String 解放の術式 = "解放の術式";
	public static final String ターゲットからXの距離内の同じチームのランダムな一人にも作用する = "ターゲットからXの距離内の同じチームのランダムな一人にも作用する";
	public static final String 派生の術式 = "派生の術式";
	public static final String ターゲットからXの距離内のランダムな一人にも作用する = "ターゲットからXの距離内のランダムな一人にも作用する";
	public static final String 伝搬の術式 = "伝搬の術式";
	public static final String 連鎖の術式 = "連鎖の術式";
	public static final String 伝達の術式 = "伝達の術式";
	public static final String ターゲットからXの距離内の同じチームの最も近い一人にも作用する = "ターゲットからXの距離内の同じチームの最も近い一人にも作用する";
	public static final String ターゲットからXの距離内の最も近い一人にも作用する = "ターゲットからXの距離内の最も近い一人にも作用する";
	public static final String のクローン = "のクローン";
	public static final String 対象が = "対象が";
	public static final String 使用者が = "使用者が";
	public static final String 対象の = "対象の";
	public static final String 使用者の = "使用者の";
	public static final String 耐性が参照される = "耐性が参照される";

	public static final String このターン対象者が未行動なら対象者はこの行動のすぐあとに行動できる = "このターン対象者が未行動なら対象者はこの行動のすぐあとに行動できる";
	public static final String このターン対象者が未行動なら対象者はこのターンの最後に行動できる = "このターン対象者が未行動なら対象者はこのターンの最後に行動できる";
	public static final String 全員にXの正気度ダメージを与える = "全員にXの正気度ダメージを与える";
	public static final String 正気度ダメージの術式 = "正気度ダメージの術式";
	public static final String 対象を即死Xさせる = "対象を即死Xさせる";
	public static final String 即死の術式 = "即死の術式";
	public static final String 特定のキャラを即死Xさせる = "特定のキャラを即死Xさせる";
	public static final String 特定のキャラに正気度ダメージを与える = "特定のキャラに正気度ダメージを与える";
	public static final String 特定人物即死の術式 = "特定人物即死の術式";
	public static final String 特定人物正気度ダメージの術式 = "特定人物正気度ダメージの術式";
	public static final String このイベントは処理の都合で入っているようだ = "このイベントは処理の都合で入っているようだ";
	public static final String ダミーの術式＿成功 = "ダミーの術式＿成功";
	public static final String ダミーの術式＿失敗 = "ダミーの術式＿失敗";
	public static final String ドロップマテリアル追加の術式 = "ドロップマテリアル追加の術式";
	public static final String ダミーの術式＿メッセージ表示 = "ダミーの術式＿メッセージ表示";
	public static final String 軽い = "軽い";
	public static final String とてつもなく重い = "とてつもなく重い";
	public static final String 非常に重い = "非常に重い";
	public static final String かなり重い = "かなり重い";
	public static final String 重い = "重い";
	public static final String このダメージは自身の体力が最大値に近いほど高くなる = "このダメージは自身の体力が最大値に近いほど高くなる";
	public static final String 体力の余裕の術式 = "体力の余裕の術式";
	public static final String このダメージは自身の魔力が最大値に近いほど高くなる = "このダメージは自身の魔力が最大値に近いほど高くなる";
	public static final String 魔力の余裕の術式 = "魔力の余裕の術式";
	public static final String このダメージは自身の正気度が最大値に近いほど高くなる = "このダメージは自身の正気度が最大値に近いほど高くなる";
	public static final String 精神的余裕の術式 = "精神的余裕の術式";
	public static final String 最大Xの正気度ダメージを与える = "最大Xの正気度ダメージを与える";
	public static final String XはXしたので行動は中断された = "XはXしたので行動は中断された";
	public static final String Xになった = "Xになった";
	public static final String 与耐性Xが = "与耐性Xが";
	public static final String 被耐性Xが = "被耐性Xが";
	public static final String 状態異常耐性Xが = "状態異常耐性Xが";
	public static final String Xの持ち物にXが追加された = "Xの持ち物にXが追加された";
	public static final String XはXを失った = "XはXを失った";
	public static final String XはXを入手した = "XはXを入手した";
	public static final String Xは追加で行動できるようになった = "Xは追加で行動できるようになった";
	public static final String Xは次に行動できるようになった = "Xは次に行動できるようになった";
	public static final String Xは最後に行動できるようになった = "Xは最後に行動できるようになった";
	public static final String Xは詠唱が中断された = "Xは詠唱が中断された";
	public static final String Xの魔法詠唱はXターン移動した = "Xの魔法詠唱はXターン移動した";
	public static final String 分身Xが現れた = "分身Xが現れた";
	public static final String Xの行動は中断された = "Xの行動は中断された";
	public static final String このターン行動順は反転した = "このターン行動順は反転した";
	public static final String Xは転送された = "Xは転送された";
	public static final String Xが召喚された = "Xが召喚された";
	public static final String Xターン内の魔法詠唱は反転された = "Xターン内の魔法詠唱は反転された";
	public static final String XはXの正気度ダメージを受けた = "XはXの正気度ダメージを受けた";
	public static final String XはXをX回発動する準備をした = "XはXをX回発動する準備をした";
	public static final String XのXは攻撃回数が変動した = "XのXは攻撃回数が変動した";
	public static final String XのXは価値が変動した = "XのXは価値が変動した";
	public static final String XはX装備を外した = "XはX装備を外した";
	public static final String 基礎値 = "基礎値";
	public static final String XはすでにXがかかっている = "XはすでにXがかかっている";
	public static final String 効果範囲内にターゲットがいない = "効果範囲内にターゲットがいない";
	public static final String しかしXには当たらなかった = "しかしXには当たらなかった";
	public static final String Xは回避した = "Xは回避した";
	public static final String Xはブロックした = "Xはブロックした";
	public static final String Xは反射した = "Xは反射した";
	public static final String Xは吸収した = "Xは吸収した";
	public static final String クリティカルヒットした = "クリティカルヒットした";
	public static final String 脚本Xを実行する = "脚本Xを実行する";
	public static final String 脚本実行の術式 = "脚本実行の術式";
	public static final String 脚本Xが存在しない = "脚本Xが存在しない";
	public static final String 脚本Xは誤っている = "脚本Xは誤っている";
	public static final String 脚本が実行された = "脚本が実行された";
	public static final String Xの正気度ダメージを受けた = "Xの正気度ダメージを受けた";
	public static final String しかし誰も正気度ダメージを受けなかった = "しかし誰も正気度ダメージを受けなかった";
	public static final String XはXを誰かに渡す気はないようだ = "XはXを誰かに渡す気はないようだ";
	public static final String このアイテムは誰かに渡したり装備解除したりできない = "このアイテムは誰かに渡したり装備解除したりできない";
	public static final String XはXしなかった = "XはXしなかった";
	public static final String XはXを実行できない = "XはXを実行できない";
	public static final String 時 = "時";
	public static final String Xには何も装備していない = "Xには何も装備していない";
	public static final String Xは両手には装備できない = "Xは両手には装備できない";
	public static final String 両手持ち可能 = "両手持ち可能";

	public static final String 無 = "無";
	public static final String 意識を失うとこの効果はなくなる = "意識を失うとこの効果はなくなる";
	//
	public static final String 難易度 = "難易度";
	public static final String 難易度を変更するには = "難易度を変更するには";

	public static final String 条件 = "条件";

	public static final String 統計情報XにXを加算する = "統計情報XにXを加算する";
	public static final String 統計X改竄の術式 = "統計X改竄の術式";
	public static final String 統計情報XはXになった = "統計情報XはXになった";
	public static final String 統計情報を完全にリセットする = "統計情報を完全にリセットする";
	public static final String 改竄の術式 = "改竄の術式";
	public static final String 統計情報がリセットされた = "統計情報がリセットされた";
	public static final String 現在のフィールドマップ情報を閲覧する = "現在のフィールドマップ情報を閲覧する";
	public static final String 位置の術式 = "位置の術式";
	public static final String 難易度をXに変更する = "難易度をXに変更する";
	public static final String 世界設定変更の術式 = "世界設定変更の術式";
	public static final String 難易度がXになった = "難易度がXになった";
	public static final String 難易度を選択して変更する = "難易度を選択して変更する";
	public static final String 世界設定再選択の術式 = "世界設定再選択の術式";
	public static final String デバッグモードを切り替える = "デバッグモードを切り替える";
	public static final String 世界の裏側の術式 = "世界の裏側の術式";
	public static final String デバッグモードがXになった = "デバッグモードがXになった";

	public static final String 対象の特性をXに変える = "対象の特性をXに変える";
	public static final String 特性変更の術式 = "特性変更の術式";
	public static final String XはXになった = "XはXになった";
	public static final String 対象の異名をXに変える = "対象の異名をXに変える";
	public static final String 異名変更の術式 ="異名変更の術式";
	
	public static class CountKey {

		public static final String BGM再生回数 = "BGM再生回数";
		public static final String 倒した敵の数 = "倒した敵の数";
		public static final String エリーが倒した敵の数 = "エリーが倒した敵の数";
		public static final String スペードが倒した敵の数 = "スペードが倒した敵の数";
		public static final String 戦闘回数 = "戦闘回数";
		public static final String 全滅回数 = "全滅回数";
		public static final String 勝利回数 = "勝利回数";
		public static final String 物理攻撃した回数 = "物理攻撃した回数";
		public static final String 魔法攻撃した回数 = "魔法攻撃した回数";
		public static final String 合計与ダメージ = "合計与ダメージ";
		public static final String 最大与ダメージ = "最大与ダメージ";
		public static final String 合計被ダメージ = "合計被ダメージ";
		public static final String 最大被ダメージ = "最大被ダメージ";
		public static final String 最大魔力 = "最大魔力";
		public static final String 解脱回数 = "解脱回数";
		public static final String いい宿に泊まった回数 = "いい宿に泊まった回数";
		public static final String 宿泊日数 = "宿泊日数";
		public static final String セーブ回数 = "セーブ回数";
		public static final String セーブデータ破壊回数 = "セーブデータ破壊回数";
		public static final String ゲーム起動回数 = "ゲーム起動回数";
		public static final String ゲームオーバー回数 = "ゲームオーバー回数";
		public static final String エリーの毒舌回数 = "エリーの毒舌回数";
		public static final String スペードのやれやれ回数 = "スペードのやれやれ回数";
		public static final String スペードが吸ったタバコの本数 = "スペードが吸ったタバコの本数";
		public static final String 飲んだお酒の数 = "飲んだお酒の数";
		public static final String 食べた豆の数 = "食べた豆の数";
		public static final String 食べた芋の数 = "食べた芋の数";
		public static final String 食べた魚の数 = "食べた魚の数";
		public static final String 食べた蕎麦の数 = "食べた蕎麦の数";
		public static final String フグを食べて死んだ回数 = "フグを食べて死んだ回数";
		public static final String フグを食べて平気だった回数 = "フグを食べて平気だった回数";
		public static final String エンディングに至った回数 = "エンディングに至った回数";
		public static final String 飲んだアルケミコーラの数 = "飲んだアルケミコーラの数";
		public static final String 入手した最強のアルケミコーラの番号 = "入手した最強のアルケミコーラの番号";
		public static final String ラロヘスキーが動物と友達になった回数 = "ラロヘスキーが動物と友達になった回数";
		public static final String ニーナが死んだ回数 = "ニーナが死んだ回数";
		public static final String コペレードが泥酔した回数 = "コペレードが泥酔した回数";
		public static final String リングロードに回復された回数 = "リングロードに回復された回数";
		public static final String リングロードが失った髪の合計 = "リングロードが失った髪の合計";
		public static final String TRUEエンド回数 = "TRUEエンド回数";

	}

	public static class ErrorMsg {

		public static final String IDが入ってません = "IDが入ってません";
		public static final String 名前が入ってません = "名前が入ってません";
		public static final String アクションタイプが入ってません = "アクションタイプが入ってません";
		public static final String アクションタイプが攻撃または魔法ですがイベントが入ってません = "アクションタイプが攻撃または魔法ですがイベントが入ってません";
		public static final String イベントがありますがターゲット選択情報が空です = "イベントがありますがターゲット選択情報が空です";
		public static final String DESCが入っていません = "DESCが入っていません";
		public static final String イベントがありますがAREAが０です = "イベントがありますがAREAが０です";
		public static final String イベントがありますが死亡者ターゲット可否が空です = "イベントがありますが死亡者ターゲット可否が空です";
		public static final String イベントがありますがFIELDとBATTLEがOFFです = "イベントがありますがFIELDとBATTLEがOFFです";
		public static final String イベントのタイプが空です = "イベントのタイプが空です";
		public static final String イベントの発生確率が０です = "イベントの発生確率が０です";
		public static final String イベントTermが重複しています = "イベントTermが重複しています";
		public static final String このイベントにはEQIPSLOTが必要です = "このイベントにはEQIPSLOTが必要です";
		public static final String このイベントにはVALUEが必要です = "このイベントにはVALUEが必要です";
		public static final String このイベントにはTgtStatusKeyが必要です = "このイベントにはTgtStatusKeyが必要です";
		public static final String このイベントにはCALC_MODEが必要です = "このイベントにはCALC_MODEが必要です";
		public static final String このイベントにはATK_ATTRが必要です = "このイベントにはATK_ATTRが必要です";
		public static final String このイベントにはATTRINが必要です = "このイベントにはATTRINが必要です";
		public static final String このイベントにはATTROUTが必要です = "このイベントにはATTROUTが必要です";
		public static final String このイベントにはCNDREGISTが必要です = "このイベントにはCNDREGISTが必要です";
		public static final String このイベントにはTGTCNDKEYが必要です = "このイベントにはTGTCNDKEYが必要です";
		public static final String このイベントにはCNDTIMEが必要です = "このイベントにはCNDTIMEが必要です";
		public static final String このイベントにはTGTIDが必要です = "このイベントにはTGTIDが必要です";
		public static final String TGTIDがアイテムIDではありません = "TGTIDがアイテムIDではありません";
		public static final String TGTIDがマテリアルIDではありません = "TGTIDがマテリアルIDではありません";
		public static final String このイベントが武器に紐づいていないため装備解除できません = "このイベントが武器に紐づいていないため装備解除できません";
		public static final String 装備解除しようとしましたがユーザはすでに装備していません = "装備解除しようとしましたがユーザはすでに装備していません";
		public static final String BSの魔法詠唱中リストとACの詠唱中状態の整合性が取れていない = "BSの魔法詠唱中リストとACの詠唱中状態の整合性が取れていない";
		public static final String TGTIDがI18NにありませんおそらくマップIDではありません = "TGTIDがI18NにありませんおそらくマップIDではありません";
		public static final String TGTIDが誤っています = "TGTIDが誤っています";
		public static final String 独自効果イベントがオーバーライドされていません = "独自効果イベントがオーバーライドされていません";
		public static final String TGTIDがSLOTではありません = "TGTIDがSLOTではありません";
		public static final String このイベントのTGTCNDKEYは解脱損壊気絶のいずれかである必要があります = "このイベントのTGTCNDKEYは解脱損壊気絶のいずれかである必要があります";
		public static final String 連鎖イベントは１種類しか設置できません = "連鎖イベントは１種類しか設置できません";

		public static final String TGTIDが難易度ではありません = "TGTIDが難易度ではありません";
		public static final String 装備解除しようとしましたがユーザはすでに装備していませんTERMとの整合性を要確認 = "装備解除しようとしましたがユーザはすでに装備していませんTERMとの整合性を要確認";
		public static final String 武器ですが攻撃回数が０です = "武器ですが攻撃回数が０です";
		public static final String 装備品ですがスタイルが入っていません = "装備品ですがスタイルが入っていません";
		public static final String 武器ですがDCSが入っていません = "武器ですがDCSが入っていません";
		public static final String 武器ですがAREAが０です = "武器ですがAREAが０です";
		public static final String 売れますが価格が０です = "売れますが価格が０です";
		public static final String 装備品ですが装備効果が入っていません = "装備品ですが装備効果が入っていません";
		public static final String 装備効果のステータスが大きすぎます = "装備効果のステータスが大きすぎます";
		public static final String 装備効果のステータス最大値が大きすぎます = "装備効果のステータス最大値が大きすぎます";
		public static final String スロットと武器タイプの整合性がとれていません = "スロットと武器タイプの整合性がとれていません";

		public static final String このイベントにはREGISTがあるTGTCNDKEYが必要です = "このイベントにはREGISTがあるTGTCNDKEYが必要です";
		public static final String 最初のイベントに不適切なEvent起動条件が設定されています = "最初のイベントに不適切なEvent起動条件が設定されています";

	}
}
