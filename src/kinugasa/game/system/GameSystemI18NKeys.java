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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GameSystemで使用されるI18Nキーです。これらがI18Nクラスにない場合、様々な処理が失敗します。
 *
 * @vesion 1.0.0 - 2023/05/20_15:45:54<br>
 * @author Shinacho<br>
 */
public class GameSystemI18NKeys {

	public static void main(String[] args) {
		for (var v : GameSystemI18NKeys.allKeysJAData()) {
			System.out.println(v);
		}
	}

	private static List<String> of(Class<?> c) {
		List<String> r = new ArrayList<>();
		try {
			for (Field ff : List.of(c.getDeclaredFields())) {
				r.add(ff.get(null).toString());
			}
		} catch (Exception ex) {
		}
		return r;
	}

	public static List<String> allKeysJAData() {
		List<String> r = new ArrayList<>();
		r.add("[GAME_SYSTEM]");
		r.addAll(of(GameSystemI18NKeys.class));
		r.add("[ERROR_MSG]");
		r.addAll(of(ErrorMsg.class));
		r.add("[COUNT_KEY]");
		r.addAll(of(CountKey.class));
		//関連ENUMを追加
		r.add("[StatusKey]");
		r.addAll(List.of(StatusKey.values()).stream().map(p -> p.toString()).toList());
		r.add("[AttributeKey]");
		r.addAll(List.of(AttributeKey.values()).stream().map(p -> p.toString()).toList());
		r.add("[ConditionKey]");
		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.toString()).toList());
		r.add("[ItemStyle]");
		r.addAll(List.of(ItemStyle.values()).stream().map(p -> p.toString()).toList());
		r.add("[ItemEnchant]");
		r.addAll(List.of(ItemEnchant.values()).stream().map(p -> p.toString()).toList());
		r.add("[CharaAbility]");
		r.addAll(List.of(CharaAbility.values()).stream().map(p -> p.toString()).toList());
		r.add("[Action.ターゲットモード]");
		r.addAll(List.of(Action.ターゲットモード.values()).stream().map(p -> p.toString()).toList());
		r.add("[Action.死亡者ターゲティング]");
		r.addAll(List.of(Action.死亡者ターゲティング.values()).stream().map(p -> p.toString()).toList());
		r.add("[ActionEventType]");
		r.addAll(List.of(ActionEventType.values()).stream().map(p -> p.toString()).toList());
		r.add("[WeaponType]");
		r.addAll(List.of(WeaponType.values()).stream().map(p -> p.toString()).toList());
		r.add("[EqipSlot]");
		r.addAll(List.of(EqipSlot.values()).stream().map(p -> p.toString()).toList());
		r.add("[ActionType]");
		r.addAll(List.of(ActionType.values()).stream().map(p -> p.toString()).toList());
		r.add("[Race]");
		r.addAll(List.of(Race.values()).stream().map(p -> p.toString()).toList());
		r.add("[BattleResult]");
		r.addAll(List.of(BattleResult.values()).stream().map(p -> p.toString()).toList());
		r.add("[BattleSystem.NoTgtDesc]");
		r.addAll(List.of(BattleSystem.NoTgtDesc.values()).stream().map(p -> p.toString()).toList());
		//関連ENUMのDESCを追加
		r.add("[ConditionKey.startMsg]");
		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.getStartMsgI18NK()).toList());
		r.add("[ConditionKey.execMsg]");
		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.getExecMsgI18NK()).toList());
		r.add("[ConditionKey.endMsg]");
		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.getEndMsgI18NK()).toList());
		r.add("[ConditionKey.desc]");
		r.addAll(List.of(ConditionKey.values()).stream().map(p -> p.getDescI18NK()).toList());
		r.add("[ItemStyle.desc]");
		r.addAll(List.of(ItemStyle.values()).stream().map(p -> p.getDescI18NKey()).toList());
		r.add("[ItemEnchant.desc]");
		r.addAll(List.of(ItemEnchant.values()).stream().map(p -> p.getDescI18NKey()).toList());
		r.add("[CharaAbility.desc]");
		r.addAll(List.of(CharaAbility.values()).stream().map(p -> p.getDescI18NK()).toList());

		r = r.stream().distinct().filter(p -> !p.isEmpty()).map(p -> {
			if (!p.contains("[")) {
				return p + "=" + p;
			}
			return p;
		}).toList();

		List<String> res = new ArrayList<>();
		for (var v : r) {
			if (!v.startsWith("[") && v.contains("X")) {
				String key = v.split("=")[0];
				String value = v.split("=")[1];
				StringBuilder sb = new StringBuilder();
				for (int i = 0, p = 0; i < value.length(); i++) {
					char a = value.charAt(i);
					if (a != 'X') {
						sb.append(a);
					} else {
						sb.append("!" + p);
						p++;
					}
				}
				res.add(key + "=" + sb.toString());
			} else {
				res.add(v);
			}
		}

		return res;
	}
	public static final String はい = "はい";
	public static final String いいえ = "いいえ";
	public static final String 話す = "話す";
	public static final String 調べる = "調べる";

	public static final String 混乱している = "混乱している";

	public static final String 確認 = "確認";
	public static final String 本当に終了しますか = "本当に終了しますか";

	public static final String の本 = "の本";

	public static final String 両手持ち = "両手持ち";
	public static final String 両手持ちすると右手の効果が２倍になる = "両手持ちにすると右手の効果が２倍になる";

	public static final String あとX個持てる = "あとX個持てる";
	public static final String 諦める = "諦める";
	public static final String Xを手に入れた誰が持つ = "Xを手に入れた誰が持つ";

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
	public static final String 左手に装備 = "左手持ちで装備";
	public static final String 両手持ちで装備 = "両手に装備";
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
	public static final String 状態異常XをXの確率で追加する = "状態異常XをXの確率で追加する";
	public static final String 状態異常XをXの確率で解除する = "状態異常XをXの確率で解除する";
	public static final String 被耐性XをXの確率でX変更する = "被耐性XをXの確率でX変更する";
	public static final String 与耐性XをXの確率でX変更する = "与耐性XをXの確率でX変更する";
	public static final String 状態異常Xの耐性をXの確率でX変更する = "状態異常Xの耐性をXの確率でX変更する";
	public static final String XをXの確率で失う = "アイテムXをXの確率で失う";
	public static final String XをXの確率で入手する = "アイテムXをXの確率で入手る";
	public static final String Xの確率でXを回復する = "Xの確率でXを回復する";
	public static final String Xの確率でX属性のダメージをXに与える = "Xの確率でX属性のダメージをXに与える";
	public static final String 計算方法 = "計算方法";
	public static final String ダメージ計算 = "ダメージ計算";
	public static final String 直接作用 = "直接作用";
	public static final String 乗算 = "乗算";
	public static final String 値になる = "値になる";
	public static final String 最大値になる = "最大値になる";
	public static final String ゼロになる = "ゼロになる";
	public static final String フィールド効果 = "フィールド効果";
	public static final String この魔法はフィールドでは使えない = "この魔法はフィールドでは使えない";
	public static final String 戦闘時ターゲット情報 = "戦闘時ターゲット情報";
	public static final String ターゲット = "ターゲット";
	public static final String ターゲット選択可否 = "ターゲット選択可否";
	public static final String 死亡者ターゲット = "死亡者ターゲット";
	public static final String 魔術 = "魔術";
	public static final String 使える魔術はない = "使える魔術はない";
	public static final String Xの装備 = "Xの装備";
	public static final String なし = "なし";
	public static final String 被属性変化の術式 = "被属性変化の術式";
	public static final String 与属性変化の術式 = "与属性変化の術式";
	public static final String 状態異常耐性変化の術式 = "状態異常耐性変化の術式";
	public static final String アイテムロストの術式 = "アイテムロストの術式";
	public static final String アイテム追加の術式 = "アイテム追加の術式";
	public static final String X回復の術式 = "X回復の術式";
	public static final String Xダメージの術式 = "Xダメージの術式";
	public static final String 状態異常付与の術式 = "状態異常付与の術式";
	public static final String 状態異常解除の術式 = "状態異常解除の術式";
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
	public static final String Xの確率で自身の武器装備を解除して敵のドロップアイテムに追加する = "Xの確率で自身の武器装備を解除して敵のドロップアイテムに追加する";
	public static final String Xの確率でXを召喚する = "Xの確率でXを召喚する";
	public static final String 友好的存在召喚の術式 = "友好的存在召喚の術式";
	public static final String 敵対的存在召喚の術式 = "敵対的存在召喚の術式";
	public static final String 戦闘に勝利したときXをXの確率で入手する = "戦闘に勝利したときXを入手する";
	public static final String ドロップアイテム追加の術式 = "ドロップアイテム追加の術式";
	public static final String Xの確率で対象者は直ちにX回行動できる = "Xの確率で対象者は直ちにX回行動できる";
	public static final String 武器投擲の術式 = "武器投擲の術式";
	public static final String 即時行動の術式 = "即時行動の術式";
	public static final String X回 = "X回";
	public static final String Xの確率で対象者はこのターンの最後にX回行動できる = "Xの確率で対象者はこのターンの最後にX回行動できる";
	public static final String 遅延行動の術式 = "遅延行動の術式";
	public static final String Xの確率で対象者は魔法詠唱を中断する = "Xの確率で対象者は魔法詠唱を中断する";
	public static final String 詠唱中断の術式 = "詠唱中断の術式";
	public static final String Xの確率で対象者の詠唱完了イベントをXターン移動する = "Xの確率で対象者の詠唱完了イベントをXターン移動する";
	public static final String 詠唱時間変更の術式 = "詠唱時間変更の術式";
	public static final String Xの確率で自身のクローンを召喚する = "Xの確率で自身のクローンを召喚する";
	public static final String 術者クローニングの術式 = "術者クローニングの術式";
	public static final String Xの確率で対象者の行動順を早める = "Xの確率で対象者の行動順を早める";
	public static final String ヘイストの術式 = "ヘイストの術式";
	public static final String Xの確率で対象者はそのターン行動できなくなる = "Xの確率で対象者はそのターン行動できなくなる";
	public static final String 行動阻止の術式 = "行動阻止の術式";
	public static final String Xの確率でそのターンの行動順を反転させる = "Xの確率でそのターンの行動順を反転させる";
	public static final String トリックルームの術式 = "トリックルームの術式";
	public static final String Xの確率で対象者は最大48ノックバックする = "Xの確率で対象者は最大48ノックバックする";
	public static final String 弱ノックバックの術式 = "弱ノックバックの術式";
	public static final String Xの確率で対象者は最大116ノックバックする = "Xの確率で対象者は最大116ノックバックする";
	public static final String 中ノックバックの術式 = "中ノックバックの術式";
	public static final String Xの確率で対象者は最大255ノックバックする = "Xの確率で対象者は最大255ノックバックする";
	public static final String 強ノックバックの術式 = "強ノックバックの術式";
	public static final String Xの確率で対象者は中心からXの範囲内に転送される = "Xの確率で対象者は中心からXの範囲内に転送される";
	public static final String 集結の術式 = "集結の術式";
	public static final String Xの確率で対象者はすぐ逃げられる位置に転送される = "Xの確率で対象者はすぐ逃げられる位置に転送される";
	public static final String 退避の術式 = "退避の術式";
	public static final String Xの確率で対象者は一番近い敵対者のそばに転送される = "Xの確率で対象者は一番近い敵対者のそばに転送される";
	public static final String 接近の術式 = "接近の術式";
	public static final String Xの確率で術者は対象者のそばに転送される = "Xの確率で術者は対象者のそばに転送される";
	public static final String 術者転送の術式 = "術者転送の術式";
	public static final String Xの確率で術者は対象者と位置が入れ替わる = "Xの確率で術者は対象者と位置が入れ替わる";
	public static final String 位置交換の術式 = "位置交換の術式";
	public static final String Xの確率で術者は特定のアイテムを手に入れる = "Xの確率で術者は特定のアイテムを手に入れる";
	public static final String ランダムアイテムの術式 = "ランダムアイテムの術式";
	public static final String Xの確率で戦闘が終了し逃走扱いになる = "Xの確率で戦闘が終了し逃走扱いになる";
	public static final String 強制逃走の術式 = "強制逃走の術式";
	public static final String Xの確率でXにワープする = "Xの確率でXにワープする";
	public static final String マップ間ワープの術式 = "マップ間ワープの術式";
	public static final String Xの確率で現在のマップのランダムな出入り口に移動する = "Xの確率で現在のマップのランダムな出入り口に移動する";
	public static final String テレポートの術式 = "テレポートの術式";
	public static final String Xの確率で現在のセーブデータを破壊しセーブせずにゲームを終了した場合はセーブデータをロストする = "Xの確率で現在のセーブデータを破壊しセーブせずにゲームを終了した場合はセーブデータをロストする";
	public static final String 現在記録抹消の術式 = "現在記録抹消の術式";
	public static final String Xの確率でゲームがセーブされずに終了する = "Xの確率でゲームがセーブされずに終了する";
	public static final String 次元崩壊の術式 = "次元崩壊の術式";
	public static final String Xの確率で他のセーブデータを破壊する = "Xの確率で他のセーブデータを破壊する";
	public static final String 別次元破壊の術式 = "別次元破壊の術式";
	public static final String Xの確率ですべてのセーブデータを破壊する = "Xの確率ですべてのセーブデータを破壊する";
	public static final String 全空間破壊の術式 = "全空間破壊の術式";
	public static final String このイベントがあると術者から対象者へビームを発射するアニメーションが追加される = "このイベントがあると術者から対象者へビームを発射するアニメーションが追加される";
	public static final String 起動するとファイル選択が開き選んだファイルに応じて属性とダメージが決まる = "起動するとファイル選択が開き選んだファイルに応じて属性とダメージが決まる";
	public static final String 上位者の情報の術式 = "上位者の情報の術式";
	public static final String 起動するとファイル選択が開き選んだファイルのサイズに応じて属性とダメージが決まる = "起動するとファイル選択が開き選んだファイルのサイズに応じて属性とダメージが決まる";
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
	public static final String Xの確率でXターン内の詠唱完了を反転させる = "Xの確率でXターン内の詠唱完了を反転させる";
	public static final String 詠唱時間逆転の術式 = "詠唱時間逆転の術式";
	public static final String 上位者の情報を閲覧する = "上位者の情報を閲覧する";
	public static final String 上位者の情報閲覧の術式 = "上位者の情報閲覧の術式";
	public static final String このダメージは倒した敵の数が多いほど大きくなる = "このダメージは倒した敵の数が多いほど大きくなる";
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
	public static final String Xの確率でXをこのターンの最後にX回発動する = "Xの確率でXをこのターンの最後にX回発動する";
	public static final String 高速詠唱の術式 = "高速詠唱の術式";
	public static final String Xの確率でランダムな属性のランダムなダメージをXに与える = "Xの確率でランダムな属性のランダムなダメージをXに与える";
	public static final String ランダムシードの術式 = "ランダムシードの術式";
	public static final String Xの確率で術者のX装備の攻撃回数をX上げる = "Xの確率で術者のX装備の攻撃回数をX上げる";
	public static final String 攻撃回数増加の術式 = "攻撃回数増加の術式";
	public static final String Xの確率で術者のX装備の価値をX倍にする = "Xの確率で術者のX装備の価値をX倍にする";
	public static final String 装備価値変更の術式 = "装備価値変更の術式";
	public static final String 指定したマップの指定した座標にワープする = "指定したマップの指定した座標にワープする";
	public static final String 転送の術式 = "転送の術式";
	public static final String Xの確率で対象者は行動がそのターンの最後になる = "Xの確率で対象者は行動がそのターンの最後になる";
	public static final String スローの術式 = "スローの術式";
	public static final String Xの確率で対象者は術者からXの範囲内に転送される = "Xの確率で対象者は術者からXの範囲内に転送される";
	public static final String 引き寄せの術式 = "引き寄せの術式";
	public static final String Xの確率で術者のX装備を解除する = "Xの確率で術者のX装備を解除する";
	public static final String パージの術式 = "パージの術式";
	public static final String 即時追加行動の術式 = "即時追加行動の術式";
	public static final String 遅延追加行動の術式 = "遅延追加行動の術式";
	public static final String Xの確率でこのアクションはターゲットからXの距離内の同じチームの全員にも作用する = "Xの確率でこのアクションはターゲットからXの距離内の同じチームの全員にも作用する";
	public static final String 放射の術式 = "放射の術式";
	public static final String Xの確率でこのアクションはターゲットからXの距離内の全員にも作用する = "Xの確率でこのアクションはターゲットからXの距離内の全員にも作用する";
	public static final String 全体放射の術式 = "全体放射の術式";
	public static final String Xの確率でこのアクションはターゲットからXの距離内の同じチームの一人にも作用する = "Xの確率でこのアクションはターゲットからXの距離内の同じチームの一人にも作用する";
	public static final String 派生の術式 = "派生の術式";
	public static final String Xの確率でこのアクションはターゲットからXの距離内の一人にも作用する = "Xの確率でこのアクションはターゲットからXの距離内の一人にも作用する";
	public static final String 伝搬の術式 = "伝搬の術式";
	public static final String このターン対象者が未行動ならXの確率で対象者はこの行動のすぐあとに行動できる = "このターン対象者が未行動ならXの確率で対象者はこの行動のすぐあとに行動できる";
	public static final String このターン対象者が未行動ならXの確率で対象者はこのターンの最後に行動できる = "このターン対象者が未行動ならXの確率で対象者はこのターンの最後に行動できる";
	public static final String Xの確率で全員にXの正気度ダメージを与える = "Xの確率で全員にXの正気度ダメージを与える";
	public static final String 正気度ダメージの術式 = "正気度ダメージの術式";
	public static final String Xの確率で対象を即死させる = "Xの確率で対象を即死させる";
	public static final String 即死の術式 = "即死の術式";
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
	public static final String Xの確率で最大Xの正気度ダメージを与える = "Xの確率で最大Xの正気度ダメージを与える";
	public static final String XはXしたので行動は中断された = "XはXしたので行動は中断された";
	public static final String Xになった = "Xになった";
	public static final String 与属性Xが = "与属性Xが";
	public static final String 被属性Xが = "被属性Xが";
	public static final String 状態異常耐性Xが = "状態異常耐性Xが";
	
	
	
	
	public static class CountKey {

		public static final String BGM再生回数 = "BGM再生回数";
		public static final String 倒した敵の数 = "倒した敵の数";
		public static final String エリーが倒した敵の数 = "エリーが倒した敵の数";
		public static final String ハートが倒した敵の数 = "ハートが倒した敵の数";
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
		public static final String ハートのやれやれ回数 = "ハートのやれやれ回数";
		public static final String ハートが吸ったタバコの本数 = "ハートが吸ったタバコの本数";
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
		public static final String コペレードに酒を飲ませた回数 = "コペレードに酒を飲ませた回数";
		public static final String リングロードに回復された回数 = "リングロードに回復された回数";
		public static final String リングロードが失った髪の合計 = "リングロードが失った髪の合計";
		public static final String Aエンド回数 = "Aエンド回数";
		public static final String Bエンド回数 = "Bエンド回数";

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
	}
}
