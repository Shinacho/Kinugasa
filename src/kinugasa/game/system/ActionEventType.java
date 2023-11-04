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

import kinugasa.game.I18N;
import kinugasa.game.ui.Text;

/**
 *
 * @vesion 1.0.0 - 2023/11/04_12:34:27<br>
 * @author Shinacho<br>
 */
public enum ActionEventType {
	ステータス攻撃 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			switch (event.getCalcMode()) {
				case DC: {
					sb.append("  ").append(I18N.get(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる));
					break;
				}
				default: {
					break;
				}
			}
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xダメージの術式, event.getTgtStatusKey().getVisibleName()));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			if (event.getCalcMode() == ActionEvent.CalcMode.DC) {
				sb.append("[");
				sb.append(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる);
				sb.append("]");
			}
			return sb.toString();
		}
	},
	ステータス回復 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXを回復する, (int) (event.getP() * 100) + "%", event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			switch (event.getCalcMode()) {
				case DC: {
					sb.append("  ").append(I18N.get(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる));
					break;
				}
				default: {
					break;
				}
			}
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.X回復の術式, event.getTgtStatusKey().getVisibleName()));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			if (event.getCalcMode() == ActionEvent.CalcMode.DC) {
				sb.append("[");
				sb.append(GameSystemI18NKeys.この値は基礎値でありダメージ計算が行われる);
				sb.append("]");
			}
			return sb.toString();
		}
	},
	ATTR_IN {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.被耐性XをXの確率でX変更する, event.getTgtAttrIn().getVisibleName(), (int) (event.getP() * 100) + "%", event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.被属性変化の術式));
			sb.append(":");
			sb.append(event.getTgtAttrIn().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue() * 100));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	ATTR_OUT {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.与耐性XをXの確率でX変更する, event.getTgtAttrOut().getVisibleName(), (int) (event.getP() * 100) + "%", event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.与属性変化の術式));
			sb.append(":");
			sb.append(event.getTgtAttrOut().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue() * 100));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	CND_REGIST {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常Xの耐性をXの確率でX変更する, event.getTgtCndRegist().getVisibleName(), (int) (event.getP() * 100) + "%", event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常耐性変化の術式));
			sb.append(":");
			sb.append(event.getTgtCndRegist().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue() * 100));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	状態異常付与 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常XをXの確率で追加する, event.getTgtConditionKey().getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常付与の術式));
			sb.append(":");
			sb.append(event.getTgtConditionKey().getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	状態異常解除 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常XをXの確率で解除する, event.getTgtConditionKey().getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.状態異常解除の術式));
			sb.append(":");
			sb.append(event.getTgtConditionKey().getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	アイテム追加 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.XをXの確率で入手する, i.getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.アイテム追加の術式));
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(":").append(i.getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	アイテムロスト {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.XをXの確率で失う, i.getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.アイテムロストの術式));
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(":").append(i.getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	ドロップアイテム追加 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Item i = ActionStorage.getInstance().itemOf(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.戦闘に勝利したときXをXの確率で入手する, i.getVisibleName(), (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ドロップアイテム追加の術式));
			sb.append(":");
			sb.append(ActionStorage.getInstance().itemOf(event.getTgtID()).getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	ユーザの武器をドロップしてドロップアイテムに追加 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で自身の武器装備を解除して敵のドロップアイテムに追加する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.武器投擲の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTの行動をVALUE回数この直後に追加 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は直ちにX回行動できる, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.即時行動の術式));
			sb.append(":").append(I18N.get(GameSystemI18NKeys.X回, ((int) event.getValue()) + ""));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTの行動をVALUE回数ターン最後に追加 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者はこのターンの最後にX回行動できる, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.遅延行動の術式));
			sb.append(":").append(I18N.get(GameSystemI18NKeys.X回, ((int) event.getValue()) + ""));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTの魔法詠唱を中断 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は魔法詠唱を中断する, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.詠唱中断の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTの魔法詠唱完了をVALUEターン分ずらす {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			String v = event.getValue() < 0 ? "-" : "+";
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者の詠唱完了イベントをXターン移動する, (int) (event.getP() * 100) + "%", v + (int) event.getValue()));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.詠唱時間変更の術式));
			sb.append(":").append((int) event.getValue()).append(I18N.get(GameSystemI18NKeys.ターン));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	USERのクローンをパーティーまたはENEMYに追加 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で自身のクローンを召喚する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.術者クローニングの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	このターンのTGTの行動をこの次にする {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者の行動順を早める, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ヘイストの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	このターンのTGTの行動を破棄 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者はそのターン行動できなくなる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.行動阻止の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	このターンのTGTの行動を最後にする {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は行動がそのターンの最後になる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.スローの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	このターンの行動順を反転させる {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でそのターンの行動順を反転させる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.トリックルームの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	ノックバック＿弱 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は最大48ノックバックする, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.弱ノックバックの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	ノックバック＿中 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は最大116ノックバックする, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.中ノックバックの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	ノックバック＿強 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は最大255ノックバックする, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.強ノックバックの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	中心位置からVALUEの場所に転送 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は中心からXの範囲内に転送される, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.集結の術式));
			sb.append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTを術者の近くに転送 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は術者からXの範囲内に転送される, (int) (event.getP() * 100) + "%", (int) event.getValue() + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.引き寄せの術式));
			sb.append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTを逃げられる位置に転送 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者はすぐ逃げられる位置に転送される, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.退避の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTを一番近い敵対者の至近距離に転送 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で対象者は一番近い敵対者のそばに転送される, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.接近の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	USERをTGTの至近距離に転送 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者は対象者のそばに転送される, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.術者転送の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	USERとTGTの位置を交換 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者は対象者と位置が入れ替わる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.位置交換の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTIDのCSVにあるアイテムのいずれかをUSERに追加 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者は特定のアイテムを手に入れる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ランダムアイテムの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	逃走で戦闘終了 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で戦闘が終了し逃走扱いになる, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.強制逃走の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	TGTIDのマップIDのランダムな出口ノードに転送 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXにワープする, (int) (event.getP() * 100) + "%", I18N.get(event.getTgtID())));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.マップ間ワープの術式));
			sb.append(":").append(I18N.get(event.getTgtID()));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	カレントマップのランダムな出口ノードに転送 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で現在のマップのランダムな出入り口に移動する, (int) (event.getP() * 100) + "%", I18N.get(event.getTgtID())));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.テレポートの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	召喚 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Actor ac = new Actor(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXを召喚する, (int) (event.getP() * 100) + "%", ac.getVisibleName()));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.召喚の術式));
			Actor ac = new Actor(event.getTgtID());
			sb.append(":").append(ac.getVisibleName());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	カレントセーブデータロスト {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で現在のセーブデータを破壊しセーブせずにゲームを終了した場合はセーブデータをロストする, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.現在記録抹消の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	クラッシュの術式 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でゲームがセーブされずに終了する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.次元崩壊の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	カレント以外のセーブデータを１つロスト {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で他のセーブデータを破壊する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.別次元破壊の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	セーブデータ全ロスト {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率ですべてのセーブデータを破壊する, (int) (event.getP() * 100) + "%"));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.全空間破壊の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	独自効果 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.この効果は特殊なもので分析ができない);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.この効果は特殊なもので分析ができない);
		}
	},
	ビームエフェクト {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.このイベントがあると術者から対象者へビームを発射するアニメーションが追加される);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.このイベントがあると術者から対象者へビームを発射するアニメーションが追加される);
		}
	},
	DC_ファイル選択からのハッシュ {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.起動するとファイル選択が開き選んだファイルに応じて属性とダメージが決まる);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.上位者の情報の術式);
		}
	},
	DC_ファイル選択からのサイズ {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.起動するとファイル選択が開き選んだファイルのサイズに応じて属性とダメージが決まる);
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.上位者の巨大情報の術式);
		}
	},
	DC_倒した敵の数 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは倒した敵の数が多いほど大きくなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.勇者の絶望の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは倒した敵の数が多いほど大きくなる);
			return sb.toString();
		}
	},
	DC_ターン数が小さい {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージはターン数が小さいほど大きくなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.速攻戦の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージはターン数が小さいほど大きくなる);
			return sb.toString();
		}
	},
	DC_ターン数が大きい {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージはターン数が経過しているほど大きくなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.遅滞戦術の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージはターン数が経過しているほど大きくなる);
			return sb.toString();
		}
	},
	DC_CPUのコア数 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは使用しているコンピュータのコア数により変化する));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.上位者の脳の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは使用しているコンピュータのコア数により変化する);
			return sb.toString();
		}
	},
	DC_USERの持っているアイテムの重さ {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージはアイテムをたくさん持っているほど大きくなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ヘビーボンバーの術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージはアイテムをたくさん持っているほど大きくなる);
			return sb.toString();
		}
	},
	詠唱完了イベントをVALUEターン内で反転 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXターン内の詠唱完了を反転させる, (int) (event.getP() * 100) + "%", ((int) event.getValue()) + ""));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.詠唱時間逆転の術式));
			sb.append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	WEBサイト起動 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.上位者の情報Xを閲覧する, event.getTgtID());
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			return I18N.get(GameSystemI18NKeys.上位者の情報閲覧の術式);
		}
	},
	DC_減っている体力 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の体力が減っているほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.背水の陣の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の体力が減っているほど高くなる);
			return sb.toString();
		}
	},
	DC_減っている魔力 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の魔力が減っているほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.精神限界の術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の魔力が減っているほど高くなる);
			return sb.toString();
		}
	},
	DC_減っている正気度 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でX属性のダメージをXに与える, (int) (event.getP() * 100) + "%", event.getAtkAttr().toString(), event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			sb.append("  ").append(I18N.get(GameSystemI18NKeys.このダメージは自身の正気度が減っているほど高くなる));
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.狂気の笑みの術式));
			sb.append(":");
			sb.append(event.getAtkAttr().getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			sb.append(GameSystemI18NKeys.このダメージは自身の正気度が減っているほど高くなる);
			return sb.toString();
		}
	},
	USERによる指定IDの魔法の詠唱完了をこのターンの最後にVALUE回数追加 {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			Action a = ActionStorage.getInstance().actionOf(event.getTgtID());
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でXをこのターンの最後にX回発動する, (int) (event.getP() * 100) + "%", a.getVisibleName(), ((int) event.getValue()) + ""));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.高速詠唱の術式));
			sb.append(":");
			Action a = ActionStorage.getInstance().actionOf(event.getTgtID());
			sb.append(a.getVisibleName());
			sb.append(event.getValue() < 0 ? "-" : "+");
			sb.append((int) (event.getValue()));
			sb.append("%(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	DC_ランダム属性のランダムダメージ {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率でランダムな属性のランダムなダメージをXに与える, (int) (event.getP() * 100) + "%", event.getTgtStatusKey().getVisibleName()));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.ランダムシードの術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	USERの指定スロットの装備品の攻撃回数をVALUE上げる {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者のX装備の攻撃回数をX上げる, (int) (event.getP() * 100) + "%", EqipSlot.valueOf(event.getTgtID()).getVisibleName(), ((int) event.getValue()) + ""));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.攻撃回数増加の術式));
			sb.append(":").append(EqipSlot.valueOf(event.getTgtID()).getVisibleName()).append(":").append((int) event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	USERの指定スロットの装備品の価値をVALUE倍にする {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.Xの確率で術者のX装備の価値をX倍にする, (int) (event.getP() * 100) + "%", EqipSlot.valueOf(event.getTgtID()).getVisibleName(), (event.getValue()) + ""));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.装備価値変更の術式));
			sb.append(":").append(EqipSlot.valueOf(event.getTgtID()).getVisibleName()).append(":").append(event.getValue());
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	},
	マップIDと座標を入力させて移動する {
		@Override
		public String getEventDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.指定したマップの指定した座標にワープする));
			sb.append(Text.getLineSep());
			return sb.toString();
		}

		@Override
		public String getPageDescI18Nd(ActionEvent event) {
			StringBuilder sb = new StringBuilder();
			sb.append(I18N.get(GameSystemI18NKeys.転送の術式));
			sb.append("(");
			sb.append(GameSystemI18NKeys.確率);
			sb.append((int) (event.getP() * 100)).append("%");
			sb.append(")");
			return sb.toString();
		}
	};

	public String getVisibleName() {
		return I18N.get(toString());
	}

	public abstract String getEventDescI18Nd(ActionEvent e);

	public abstract String getPageDescI18Nd(ActionEvent e);

}
