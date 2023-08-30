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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.MessageWindowGroup;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/12/22_16:01:21<br>
 * @author Shinacho<br>
 */
public class ItemWindow extends BasicSprite {

	private List<Actor> list;
	private MessageWindow main;
	private MessageWindow choiceUse, dropConfirm, disasseConfirm, tgtSelect, msg;//msgはボタン操作で即閉じる
	private MessageWindowGroup group;

	public ItemWindow(float x, float y, float w, float h) {
		list = GameSystem.getInstance().getParty().stream().collect(Collectors.toList());
		main = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		x += 8;
		y += 8;
		w -= 8;
		h -= 8;
		choiceUse = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		choiceUse.setVisible(false);
		dropConfirm = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		dropConfirm.setVisible(false);
		disasseConfirm = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		disasseConfirm.setVisible(false);
		tgtSelect = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		tgtSelect.setVisible(false);
		msg = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel());
		msg.setVisible(false);

		group = new MessageWindowGroup(choiceUse, dropConfirm, tgtSelect, msg, disasseConfirm);
		mainSelect = 0;
		update();
	}

	public enum Mode {
		/**
		 * どのアイテムにするかを選択中。
		 */
		ITEM_AND_USER_SELECT,
		/**
		 * アイテム使用内容を選択中。
		 */
		CHOICE_USE,
		/**
		 * MSG表示し、終了待ち。終了したらITEM＿AND＿USER＿SELECTに入る。
		 */
		WAIT_MSG_CLOSE_TO_IUS,
		WAIT_MSG_CLOSE_TO_CU,
		/**
		 * 使う・渡す対象を選択中。
		 */
		TARGET_SELECT,
		/**
		 * 捨ててもよいか確認中。
		 */
		DROP_CONFIRM,
		/**
		 * 解体してもよいか確認中。
		 */
		DISASSE_CONFIRM,
	}
	private Mode mode = Mode.ITEM_AND_USER_SELECT;
	private int pcIdx;
	private int mainSelect = 0;

	public void nextSelect() {
		switch (mode) {
			case ITEM_AND_USER_SELECT:
				mainSelect++;
				if (mainSelect >= getSelectedPC().getItemBag().size()) {
					mainSelect = 0;
				}
				return;
			case CHOICE_USE:
				choiceUse.nextSelect();
				return;
			case DROP_CONFIRM:
				dropConfirm.nextSelect();
				return;
			case DISASSE_CONFIRM:
				disasseConfirm.nextSelect();
				return;
			case TARGET_SELECT:
			case WAIT_MSG_CLOSE_TO_IUS:
			case WAIT_MSG_CLOSE_TO_CU:
				//処理なし
				return;
		}
	}

	public void prevSelect() {
		switch (mode) {
			case ITEM_AND_USER_SELECT:
				mainSelect--;
				if (mainSelect < 0) {
					mainSelect = getSelectedPC().getItemBag().size() - 1;
				}
				return;
			case CHOICE_USE:
				choiceUse.prevSelect();
				return;
			case DROP_CONFIRM:
				dropConfirm.prevSelect();
				return;
			case DISASSE_CONFIRM:
				disasseConfirm.prevSelect();
				return;
			case TARGET_SELECT:
			case WAIT_MSG_CLOSE_TO_IUS:
			case WAIT_MSG_CLOSE_TO_CU:
				//処理なし
				return;
		}
	}

	public void nextPC() {
		switch (mode) {
			case ITEM_AND_USER_SELECT:
				mainSelect = 0;
				pcIdx++;
				if (pcIdx >= list.size()) {
					pcIdx = 0;
				}
				return;
			case CHOICE_USE:
			case DROP_CONFIRM:
			case DISASSE_CONFIRM:
			case WAIT_MSG_CLOSE_TO_IUS:
			case WAIT_MSG_CLOSE_TO_CU:
				//処理なし
				return;
			case TARGET_SELECT:
				tgtSelect.nextSelect();
				return;
		}
	}

	public void prevPC() {
		switch (mode) {
			case ITEM_AND_USER_SELECT:
				mainSelect = 0;
				pcIdx--;
				if (pcIdx < 0) {
					pcIdx = list.size() - 1;
				}
				return;
			case CHOICE_USE:
			case DROP_CONFIRM:
			case DISASSE_CONFIRM:
			case WAIT_MSG_CLOSE_TO_IUS:
			case WAIT_MSG_CLOSE_TO_CU:
				//処理なし
				return;
			case TARGET_SELECT:
				tgtSelect.prevSelect();
				return;
		}
	}

	public Mode currentMode() {
		return mode;
	}

	public Status getSelectedPC() {
		return list.get(pcIdx).getStatus();
	}

	public Item getSelectedItem() {
		return getSelectedPC().getItemBag().get(mainSelect);
	}

	private static final int CHECK = 0;
	private static final int USE = 1;
	private static final int EQIP = 2;
	private static final int PASS = 3;
	private static final int DISASSEMBLY = 4;
	private static final int DROP = 5;

	public void select() {
		if (getSelectedPC().getItemBag().isEmpty()) {
			group.closeAll();
			mode = Mode.ITEM_AND_USER_SELECT;
			return;
		}
		Item i = getSelectedItem();
		switch (mode) {
			case ITEM_AND_USER_SELECT:
				List<Text> options = new ArrayList<>();
				options.add(new Text(I18N.get(GameSystemI18NKeys.調べる)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.使う)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.装備)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.渡す)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.解体)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.捨てる)));
				Choice c = new Choice(options, "ITEM_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xを, getSelectedItem().getVisibleName()));
				choiceUse.setText(c);
				choiceUse.allText();
				choiceUse.setSelect(0);
				group.show(choiceUse);
				mode = Mode.CHOICE_USE;
				break;
			case CHOICE_USE:
				//選ばれた選択肢により分岐
				switch (choiceUse.getSelect()) {
					case USE:
						//アイテムがフィールドで使えるなら対象者選択へ
						if (!i.isFieldUse()) {
							//フィールドでは使えません
							msg.setText(I18N.get(GameSystemI18NKeys.XはXを使用した, getSelectedPC().getName(), i.getVisibleName())
									+ Text.getLineSep()
									+ I18N.get(GameSystemI18NKeys.しかし効果がなかった));
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//ターゲット確認
						//SELFのみの場合即時実行
						//ターゲットタイプランダムの場合は即時実行

						if (i.fieldEventIsOnly(TargetType.SELF) || i.fieldEventIsOnly(TargetType.RANDOM)) {
							//即時実行してサブに効果を出力
							Status tgt = getSelectedPC();
							tgt.setDamageCalcPoint();
							ActionTarget t;
							ActionResult r = i.exec(t = ActionTarget.instantTarget(getSelectedPC(), i).setInField(true));
							ConditionManager.getInstance().setCondition(t.getTarget());

							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを使用した, getSelectedPC().getName(), i.getVisibleName()));
							sb.append(Text.getLineSep());
							if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
								//成功
								//効果測定
								Map<StatusKey, Float> map = tgt.calcDamage();
								for (Map.Entry<StatusKey, Float> e : map.entrySet()) {
									if (e.getValue() > 0) {
										sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()))
												.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getDesc()))
												.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
									} else {
										sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()))
												.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getDesc()))
												.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""));
									}
								}
							} else {
								//失敗
								sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
							}
							msg.setText(sb.toString());
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
							return;
						}
						//チームが入っている場合即時実行
						if (i.fieldEventIsOnly(TargetType.TEAM)) {
							StringBuilder sb = new StringBuilder();
							//即時実行してサブに効果を出力
							sb.append(I18N.get(GameSystemI18NKeys.XはXを使用した, getSelectedPC().getName(), i.getVisibleName()));
							sb.append(Text.getLineSep());
							for (Status s : GameSystem.getInstance().getPartyStatus()) {
								s.setDamageCalcPoint();
								ActionTarget tgt;
								ActionResult r = i.exec(tgt = ActionTarget.instantTarget(getSelectedPC(), i).setInField(true));
								ConditionManager.getInstance().setCondition(tgt.getTarget());
								if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {

									//成功
									//効果測定・・・全行表示できると思うので、そうする
									Map<StatusKey, Float> map = s.calcDamage();
									for (Map.Entry<StatusKey, Float> e : map.entrySet()) {
										if (e.getValue() > 0) {
											sb.append(I18N.get(GameSystemI18NKeys.Xの, s.getName()))
													.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getDesc()))
													.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""))
													.append(Text.getLineSep());
										} else {
											sb.append(I18N.get(GameSystemI18NKeys.Xの, s.getName()))
													.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getDesc()))
													.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""))
													.append(Text.getLineSep());
										}
									}
								} else {
									//失敗
									sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
								}
							}
							msg.setText(sb.toString());
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
							return;
						}
						//その他の場合はターゲット選択へ
						List<Text> options3 = new ArrayList<>();
						options3.addAll(list.stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(options3, "ITEM_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xを誰に使う, i.getVisibleName())));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case EQIP:
						//装備できるアイテムかどうかで分岐
						if (getSelectedPC().getEqipment().values().contains(i)) {
							//すでに装備している時は外す
							//バッグに分類されるアイテムかつアイテム数がもともと持てる数を上回る場合外せない
							if (ItemStorage.bagItems.containsKey(i.getName())) {
								//もともとのサイズ
								int itemBagDefaultMax = getSelectedPC().getRace().getItemBagSize();
								//現在の持ってる数
								int currentSize = getSelectedPC().getItemBag().size();
								//現在のサイズがもともともサイズより大きい場合は外せない
								if (currentSize > itemBagDefaultMax) {
									//外せない
									msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
									msg.allText();
									group.show(msg);
									mode = Mode.WAIT_MSG_CLOSE_TO_CU;
									break;
								}
							}
							getSelectedPC().getEqipment().put(i.getEqipmentSlot(), null);
							getSelectedPC().updateAction();
							//アイテム所持数の再計算
							getSelectedPC().updateItemBagSize();
							msg.setText(I18N.get(GameSystemI18NKeys.Xを外した, i.getVisibleName()));
							msg.allText();
							GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction());
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else if (getSelectedPC().canEqip(i)) {
							//装備する
							getSelectedPC().addEqip(i);
							getSelectedPC().updateAction();
							//アイテム所持数の再計算
							getSelectedPC().updateItemBagSize();
							msg.setText(I18N.get(GameSystemI18NKeys.Xを装備した, i.getVisibleName()));
							msg.allText();
							GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction());
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							//装備できない
							msg.setText(I18N.get(GameSystemI18NKeys.Xは装備できない, i.getVisibleName()));
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						}
						break;
					case PASS:
						//パスターゲットに移動
						List<Text> options2 = new ArrayList<>();
						options2.addAll(list.stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(options2, "ITEM_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xを誰に渡す, i.getVisibleName())));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case CHECK:
						//CHECKモードでは価値、キーアイテム属性、スロット、攻撃力、DCSを表示すること！
						//アイテムの詳細をサブに表示
						StringBuilder sb = new StringBuilder();
						sb.append(i.getVisibleName()).append(Text.getLineSep());

						//DESC
						String desc = i.getDesc();
						if (desc.contains(Text.getLineSep())) {
							String[] sv = desc.split(Text.getLineSep());
							for (String v : sv) {
								sb.append(" ").append(v);
								sb.append(Text.getLineSep());
							}
						} else {
							sb.append(" ").append(i.getDesc());
							sb.append(Text.getLineSep());
						}
						//価値
						sb.append(" ").append(I18N.get(GameSystemI18NKeys.価値))
								.append(":").append(i.getValue());
						sb.append(Text.getLineSep());
						//装備スロット
						sb.append(" ").append(I18N.get(GameSystemI18NKeys.装備スロット))
								.append(":").append(i.getEqipmentSlot() != null
								? i.getEqipmentSlot().getName()
								: I18N.get(GameSystemI18NKeys.なし));
						sb.append(Text.getLineSep());
						//WMT
						if (i.getWeaponMagicType() != null) {
							sb.append(" ").append(I18N.get(GameSystemI18NKeys.武器種別))
									.append(":").append(i.getWeaponMagicType().getVisibleName());
							sb.append(Text.getLineSep());
						}
						//area
						int area = 0;
						if (i.isEqipItem()) {
							//範囲表示するのは武器だけ
							if (i.getWeaponMagicType() != null) {
								area = i.getArea();
							}
						} else {
							if (i.getBattleEvent() != null && !i.getBattleEvent().isEmpty()) {
								area = (int) (getSelectedPC().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
							}
						}
						if (area != 0) {
							sb.append(" ").append(I18N.get(GameSystemI18NKeys.範囲)).append(":").append(area);
							sb.append(Text.getLineSep());
						}
						//キーアイテム属性
						if (!i.canSale()) {
							sb.append(" ").append(I18N.get(GameSystemI18NKeys.このアイテムは売ったり捨てたり解体したりできない));
							sb.append(Text.getLineSep());
						}
						//DCS
						if (i.getDamageCalcStatusKey() != null && !i.getDamageCalcStatusKey().isEmpty()) {
							String dcs = "";
							for (StatusKey s : i.getDamageCalcStatusKey()) {
								dcs += s.getDesc() + ",";
							}
							dcs = dcs.substring(0, dcs.length() - 1);
							sb.append(" ").append(I18N.get(GameSystemI18NKeys.ダメージ計算方式)).append(":").append(dcs);
							sb.append(Text.getLineSep());
						}
						//戦闘中アクション
						if (i.getBattleEvent() != null && !i.getBattleEvent().isEmpty()) {
							sb.append(" ").append(I18N.get(GameSystemI18NKeys.このアイテムは戦闘中使える));
							sb.append(Text.getLineSep());
						}
						//フィールドアクション
						if (i.getFieldEvent() != null && !i.getFieldEvent().isEmpty()) {
							sb.append(" ").append(I18N.get(GameSystemI18NKeys.このアイテムはフィールドで使える));
							sb.append(Text.getLineSep());
						}
						if (i.isEqipItem()) {
							//攻撃回数
							if (i.getActionCount() > 1) {
								sb.append(" ").append(I18N.get(GameSystemI18NKeys.攻撃回数)).append(":").append(i.getActionCount() + "");
								sb.append(Text.getLineSep());
							}
							//eqStatus
							if (i.getEqStatus() != null && !i.getEqStatus().isEmpty()) {
								for (StatusValue s : i.getEqStatus()) {
									if (StatusDescWindow.getUnvisibleStatusList().contains(s.getName())) {
										continue;
									}
									String v;
									if (s.getKey().getMax() <= 1f) {
										v = (float) (s.getValue() * 100) + "%";//1%単位
									} else {
										v = (int) s.getValue() + "";
									}
									if (!v.startsWith("0")) {
										sb.append(" ");
										sb.append(s.getKey().getDesc()).append(":").append(v);
										sb.append(Text.getLineSep());
									}
								}
							}
							//eqAttr
							if (i.getEqAttr() != null && !i.getEqAttr().isEmpty()) {
								for (AttributeValue a : i.getEqAttr()) {
									String v = (float) (a.getValue() * 100) + "%";
									if (!v.startsWith("0")) {
										sb.append(" ");
										sb.append(a.getKey().getDesc()).append(":").append(v);
										sb.append(Text.getLineSep());
									}
								}
							}
							//強化
							if (i.canUpgrade()) {
								sb.append(" ");
								sb.append(I18N.get(GameSystemI18NKeys.このアイテムはあとX回強化できる, i.getUpgradeMaterials().size() + ""));
								sb.append(Text.getLineSep());
							} else {
								sb.append(" ");
								sb.append(I18N.get(GameSystemI18NKeys.このアイテムは強化できない));
								sb.append(Text.getLineSep());
							}
							//解体
							if (!i.getDisasseMaterials().isEmpty()) {
								sb.append(" ");
								sb.append(I18N.get(GameSystemI18NKeys.解体すると以下を入手する));
								sb.append(Text.getLineSep());
								for (Map.Entry<Material, Integer> e : i.getDisasseMaterials().entrySet()) {
									sb.append("   ");
									sb.append(e.getKey().getName()).append(":").append(e.getValue());
									sb.append(Text.getLineSep());
								}
								sb.append(Text.getLineSep());
							} else {
								sb.append(" ");
								sb.append(I18N.get(GameSystemI18NKeys.このアイテムは解体できない));
							}
						}
						msg.setText(sb.toString());
						msg.allText();
						group.show(msg);
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						break;
					case DROP:
						//drop確認ウインドウを有効化
						if (!i.canSale()) {
							msg.setText(I18N.get(GameSystemI18NKeys.このアイテムは売ったり捨てたり解体したりできない));
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							List<Text> options4 = new ArrayList<>();
							options4.add(new Text(I18N.get(GameSystemI18NKeys.いいえ)));
							options4.add(new Text(I18N.get(GameSystemI18NKeys.はい)));
							dropConfirm.reset();
							dropConfirm.setText(new Choice(options4, "DROP_CONFIRM", I18N.get(GameSystemI18NKeys.Xを本当にすてる, i.getVisibleName())));
							dropConfirm.allText();
							group.show(dropConfirm);
							mode = Mode.DROP_CONFIRM;
						}
						break;
					case DISASSEMBLY:
						//解体できるアイテムか判定
						if (!i.canSale() || !i.canDisasse()) {
							msg.setText(I18N.get(GameSystemI18NKeys.このアイテムは売ったり捨てたり解体したりできない));
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							List<Text> options5 = new ArrayList<>();
							options5.add(new Text(I18N.get(GameSystemI18NKeys.いいえ)));
							options5.add(new Text(I18N.get(GameSystemI18NKeys.はい)));
							disasseConfirm.reset();
							disasseConfirm.setText(new Choice(options5, "DISASSE_CONFIRM", I18N.get(GameSystemI18NKeys.Xを本当に解体する, i.getVisibleName())));
							disasseConfirm.allText();
							group.show(disasseConfirm);
							mode = Mode.DISASSE_CONFIRM;
						}
						break;
					default:
						throw new AssertionError("undefined choiceUser case");
				}
				break;

			case TARGET_SELECT:
				//tgtウインドウから選択された対象者をもとにUSEまたはPASSを実行
				//use or watasu
				assert choiceUse.getSelect() == USE || choiceUse.getSelect() == PASS : "ITEMWINDOW : choice user select is missmatch";
				if (choiceUse.getSelect() == USE) {
					commitUse();
					group.show(msg);
					mode = Mode.WAIT_MSG_CLOSE_TO_IUS;//useしたら消えている可能性があるためIUS
					break;
				}
				if (choiceUse.getSelect() == PASS) {
					//パスタの相手がこれ以上物を持てない場合失敗
					//TODO:交換機能が必要
					if (!GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect()).getItemBag().canAdd()) {
						String m = I18N.get(GameSystemI18NKeys.Xはこれ以上物を持てない, GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect()).getName());
						this.msg.setText(m);
						this.msg.allText();
						group.show(msg);
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						break;
					}
					int itemBagSize = getSelectedPC().getItemBag().size();
					commitPass();
					boolean self = itemBagSize == getSelectedPC().getItemBag().size();
					group.show(msg);
					//自分自身に渡した場合CUへ、そうでない場合はIUSに戻る
					if (self) {
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
					} else {
						mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
					}
				}
				break;
			case DROP_CONFIRM:
				//drop確認ウインドウの選択肢により分岐
				switch (dropConfirm.getSelect()) {
					case 0:
						//いいえ
						//用途選択に戻る
						group.show(choiceUse);
						mode = Mode.CHOICE_USE;
						break;
					case 1:
						//はい
						//dropしてアイテム選択に戻る
						commitDrop();
						group.show(msg);
						mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
						break;
				}
				break;
			case DISASSE_CONFIRM:
				//解体確認ウインドウの選択肢により分岐
				switch (disasseConfirm.getSelect()) {
					case 0:
						//いいえ
						//用途選択に戻る
						group.show(choiceUse);
						mode = Mode.CHOICE_USE;
						break;
					case 1:
						//はい
						commitDisasse();
						group.show(msg);
						mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
						break;
				}
				break;
			case WAIT_MSG_CLOSE_TO_IUS:
				//dropの場合はIUSに戻る
				group.closeAll();
				mode = Mode.ITEM_AND_USER_SELECT;
				break;
			case WAIT_MSG_CLOSE_TO_CU:
				group.show(choiceUse);
				mode = Mode.CHOICE_USE;
				break;
		}
	}

	private void commitUse() {
		Item i = getSelectedItem();
		Status tgt = GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect());
		getSelectedPC().passItem(tgt, i);
		tgt.setDamageCalcPoint();
		ActionTarget t;
		ActionResult r = i.exec(t = ActionTarget.instantTarget(getSelectedPC(), i, tgt).setInField(true));
		ConditionManager.getInstance().setCondition(t.getTarget());
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.get(GameSystemI18NKeys.XはXを使用した, getSelectedPC().getName(), i.getVisibleName()));
		sb.append(Text.getLineSep());
		if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
			//成功
			//効果測定
			Map<StatusKey, Float> map = tgt.calcDamage();
			for (Map.Entry<StatusKey, Float> e : map.entrySet()) {
				if (e.getValue() < 0) {
					sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()))
							.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getDesc()))
							.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
					sb.append(Text.getLineSep());
				} else if (e.getValue() > 0) {
					sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()))
							.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getDesc()))
							.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""));
					sb.append(Text.getLineSep());
				} else {
					//==0
					sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
					sb.append(Text.getLineSep());
				}
				sb.append(Text.getLineSep());
			}
			if (map.isEmpty()) {
				sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
				sb.append(Text.getLineSep());
			}
			//DROP_ITEMイベントの判定
			for (ActionEvent e : i.getFieldEvent()) {
				if (e.getParameterType() == ParameterType.ITEM_LOST) {
					if (e.getP() >= 1f || Random.percent(e.getP())) {
						tgt.getItemBag().drop(i);
						sb.append(I18N.get(GameSystemI18NKeys.Xを失った, i.getVisibleName()));
						sb.append(Text.getLineSep());
					}
				}
				if (e.getParameterType() == ParameterType.ITEM_ADD) {
					if (e.getP() >= 1f || Random.percent(e.getP())) {
						tgt.getItemBag().add(ItemStorage.getInstance().get(e.getTgtName()));
						sb.append(I18N.get(GameSystemI18NKeys.XをX個入手した, i.getVisibleName(), "1"));
						sb.append(Text.getLineSep());
					}
				}
			}
		} else {
			//失敗
			sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
			sb.append(Text.getLineSep());
		}

		msg.setText(sb.toString());
		msg.allText();
		group.show(msg);

		ItemBag ib = getSelectedPC().getItemBag();
		sb = new StringBuilder();
		sb.append("<---");
		sb.append(getSelectedPC().getName());
		sb.append("--->");
		sb.append(Text.getLineSep());
		int j = 0;
		for (Item item : ib) {
			if (j == main.getSelect()) {
				sb.append("  >");
			} else {
				sb.append("   ");
			}
			if (getSelectedPC().getEqipment().values() != null
					&& !getSelectedPC().getEqipment().values().isEmpty()
					&& getSelectedPC().getEqipment().values().contains(i)) {
				sb.append(" (E)");
			} else {
				sb.append("    ");
			}
			sb.append(item.getVisibleName()).append(Text.getLineSep());
			j++;
		}
		main.setText(sb.toString());
		main.allText();
		main.setVisible(true);
		mainSelect = 0;

	}

	private void commitPass() {
		Status tgt = GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect());
		Item i = getSelectedItem();
		getSelectedPC().passItem(tgt, i);
		if (!getSelectedPC().equals(tgt)) {
			String t = I18N.get(GameSystemI18NKeys.XはXにXを渡した, getSelectedPC().getName(), tgt.getName(), i.getVisibleName());
			msg.setText(t);
			mainSelect = 0;
			getSelectedPC().updateAction();
		} else {
			String t = I18N.get(GameSystemI18NKeys.XはXを持ち替えた, getSelectedPC().getName(), i.getVisibleName());
			msg.setText(t);
			mainSelect = getSelectedPC().getItemBag().size() - 1;
		}
		msg.allText();
		group.show(msg);

		StringBuilder sb = new StringBuilder();
		ItemBag ib = getSelectedPC().getItemBag();
		sb = new StringBuilder();
		sb.append("<---");
		sb.append(getSelectedPC().getName());
		sb.append("--->");
		sb.append(Text.getLineSep());
		int j = 0;
		for (Item item : ib) {
			if (j == main.getSelect()) {
				sb.append("  >");
			} else {
				sb.append("   ");
			}
			if (getSelectedPC().getEqipment().values() != null
					&& !getSelectedPC().getEqipment().values().isEmpty()
					&& getSelectedPC().getEqipment().values().contains(i)) {
				sb.append(" (E)");
			} else {
				sb.append("    ");
			}
			sb.append(item.getVisibleName()).append(Text.getLineSep());
			j++;
		}
		main.setText(sb.toString());
		main.allText();
		main.setVisible(true);
	}

	private void commitDrop() {
		dropConfirm.close();
		Item i = getSelectedItem();
		assert i.canSale() : "item is cant disassembly : " + i;
		//1個しか持っていなかったら装備を外す
		if (getSelectedPC().isEqip(i.getVisibleName()) && getSelectedPC().getItemBag().getItems().stream().filter(p -> p.equals(i)).count() == 1) {
			getSelectedPC().removeEqip(i);
		}
		getSelectedPC().getItemBag().drop(i);
		//アイテム追加
		for (ActionEvent e : i.getFieldEvent()) {
			if (e.getParameterType() == ParameterType.ITEM_ADD) {
				getSelectedPC().getItemBag().add(ItemStorage.getInstance().get(e.getTgtName()));
			}
		}
		msg.setText(I18N.get(GameSystemI18NKeys.XはXを捨てた, getSelectedPC().getName(), i.getVisibleName()));
		msg.allText();
		getSelectedPC().updateAction();
		group.show(msg);
		mainSelect = 0;
	}

	private void commitDisasse() {
		disasseConfirm.close();
		Item i = getSelectedItem();
		assert i.canDisasse() : "item is cant disassembly : " + i;
		assert i.canSale() : "item is cant disassembly : " + i;
		//1個しか持っていなかったら装備を外す
		if (getSelectedPC().isEqip(i.getVisibleName()) && getSelectedPC().getItemBag().getItems().stream().filter(p -> p.equals(i)).count() == 1) {
			getSelectedPC().removeEqip(i);
		}
		getSelectedPC().getItemBag().drop(i);
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Material, Integer> e : i.getDisasseMaterials().entrySet()) {
			sb.append(" ");
			sb.append(I18N.get(GameSystemI18NKeys.XをX個入手した, e.getKey().getName(), e.getValue() + ""));
			sb.append(Text.getLineSep());
			for (int j = 0; j < e.getValue(); j++) {
				GameSystem.getInstance().getMaterialBag().add(e.getKey());
			}
		}
		msg.setText(I18N.get(GameSystemI18NKeys.XはXを解体した, getSelectedPC().getName(), i.getVisibleName()));
		msg.allText();
		getSelectedPC().updateAction();
		group.show(msg);
		mainSelect = 0;
	}

	@Override
	public void update() {
		//メインウインドウの内容更新
		if (mode == Mode.ITEM_AND_USER_SELECT) {
			ItemBag ib = getSelectedPC().getItemBag();
			StringBuilder sb = new StringBuilder();
			sb.append("<---");
			sb.append(getSelectedPC().getName());
			sb.append("--->");
			sb.append(Text.getLineSep());
			if (ib.isEmpty()) {
				sb.append("  ").append(I18N.get(GameSystemI18NKeys.何も持っていない));
				main.setText(sb.toString());
				main.allText();
				main.setVisible(true);
			} else {
				int j = 0;
				Set<Item> eqip = new HashSet<>();
				for (Item i : ib) {
					if (j == mainSelect) {
						sb.append("  >");
					} else {
						sb.append("   ");
					}
					if (getSelectedPC().getEqipment().values() != null
							&& !getSelectedPC().getEqipment().values().isEmpty()
							&& getSelectedPC().getEqipment().values().contains(i)
							&& !eqip.contains(i)) {
						sb.append(" (E)");
						eqip.add(i);
					} else {
						sb.append("    ");
					}
					sb.append(i.getVisibleName()).append(Text.getLineSep());
					j++;
				}
				sb.append(Text.getLineSep());
				sb.append(Text.getLineSep());
				sb.append(I18N.get(GameSystemI18NKeys.あとX個持てる, getSelectedPC().getItemBag().remainingSize() + ""));
				main.setText(sb.toString());
				main.allText();
				main.setVisible(true);
			}
		}
	}

	//1つ前の画面に戻る
	public boolean close() {
		//IUS表示中の場合は戻るは全消し
		if (group.getWindows().stream().allMatch(p -> !p.isVisible())) {
			mode = Mode.ITEM_AND_USER_SELECT;
			return true;
		}
		if (msg.isVisible()) {
			mode = Mode.CHOICE_USE;
			group.show(choiceUse);
			return false;
		}
		if (tgtSelect.isVisible()) {
			mode = Mode.CHOICE_USE;
			group.show(choiceUse);
			return false;
		}
		if (dropConfirm.isVisible()) {
			mode = Mode.CHOICE_USE;
			group.show(choiceUse);
			return false;
		}
		if (disasseConfirm.isVisible()) {
			mode = Mode.CHOICE_USE;
			group.show(choiceUse);
			return false;
		}
		if (choiceUse.isVisible()) {
			mode = Mode.ITEM_AND_USER_SELECT;
			group.closeAll();
			return false;
		}
		group.closeAll();
		mode = Mode.ITEM_AND_USER_SELECT;
		return false;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		main.draw(g);
		choiceUse.draw(g);
		tgtSelect.draw(g);
		dropConfirm.draw(g);
		disasseConfirm.draw(g);
		msg.draw(g);
	}

}
