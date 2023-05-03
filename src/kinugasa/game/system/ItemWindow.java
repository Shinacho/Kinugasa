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

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
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
 * @author Dra211<br>
 */
public class ItemWindow extends BasicSprite {

	private List<Status> list;
	private MessageWindow main;
	private MessageWindow choiceUse, dropConfirm, tgtSelect, msg;//msgはボタン操作で即閉じる
	private MessageWindowGroup group;

	public ItemWindow(float x, float y, float w, float h) {
		list = GameSystem.getInstance().getPartyStatus();
		main = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		x += 8;
		y += 8;
		w -= 8;
		h -= 8;
		choiceUse = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		choiceUse.setVisible(false);
		dropConfirm = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		dropConfirm.setVisible(false);
		tgtSelect = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		tgtSelect.setVisible(false);
		msg = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel());
		msg.setVisible(false);

		group = new MessageWindowGroup(choiceUse, dropConfirm, tgtSelect, msg);
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
		return list.get(pcIdx);
	}

	public Item getSelectedItem() {
		return getSelectedPC().getItemBag().get(mainSelect);
	}

	private static final int USE = 0;
	private static final int EQIP = 1;
	private static final int PASS = 2;
	private static final int CHECK = 3;
	private static final int DROP = 4;

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
				options.add(new Text(I18N.translate("USE")));
				options.add(new Text(I18N.translate("EQIP")));
				options.add(new Text(I18N.translate("PASS")));
				options.add(new Text(I18N.translate("CHECK")));
				options.add(new Text(I18N.translate("DROP")));
				Choice c = new Choice(options, "ITEM_WINDOW_SUB", getSelectedItem().getName() + I18N.translate("OF"));
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
						if (i.getFieldEvent() == null || i.getFieldEvent().isEmpty()) {
							//使っても効果がないよ
							StringBuilder sb = new StringBuilder();
							sb.append(getSelectedPC().getName());
							sb.append(I18N.translate("IS"));
							sb.append(i.getName());
							sb.append(I18N.translate("USE_ITEM"));
							sb.append(Text.getLineSep());
							sb.append(I18N.translate("BUT"));
							sb.append(I18N.translate("NO_EFFECT"));
							msg.setText(sb.toString());
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//敵対象のアイテムの場合使用できない（このようなアイテムは存在しないはずだが念のため）
						if (i.fieldEventIsOnly(TargetType.ONE_ENEMY) || i.fieldEventIsOnly(TargetType.RANDOM_ONE_ENEMY) || i.fieldEventIsOnly(TargetType.RANDOM_ONE_ENEMY)) {
							//使っても効果がないよ
							StringBuilder sb = new StringBuilder();
							sb.append(getSelectedPC().getName());
							sb.append(I18N.translate("IS"));
							sb.append(i.getName());
							sb.append(I18N.translate("USE_ITEM"));
							sb.append(Text.getLineSep());
							sb.append(I18N.translate("BUT"));
							sb.append(I18N.translate("NO_EFFECT"));
							msg.setText(sb.toString());
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//ターゲット確認
						//SELFのみの場合即時実行
						//ターゲットタイプランダムの場合は即時実行
						//チームが入っている場合即時実行
						if (i.fieldEventIsOnly(TargetType.SELF) || i.fieldEventIsOnly(TargetType.RANDOM_ONE) || i.fieldEventIsOnly(TargetType.RANDOM_ONE_PARTY)
								||i.fieldEventIsOnly(TargetType.TEAM_PARTY)) {
							//即時実行してサブに効果を出力
							Status tgt = getSelectedPC();
							tgt.setDamageCalcPoint();
							ActionResult r = i.exec(ActionTarget.instantTarget(getSelectedPC(), i).setInField(true));
							StringBuilder sb = new StringBuilder();
							sb.append(tgt.getName()).append(I18N.translate("IS")).append(i.getName()).append(I18N.translate("USE_ITEM"));
							sb.append(Text.getLineSep());
							if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
								//成功
								//効果測定
								Map<StatusKey, Integer> map = tgt.calcDamage();
								for (Map.Entry<StatusKey, Integer> e : map.entrySet()) {
									if (e.getValue() > 0) {
										sb.append(tgt.getName()).append(I18N.translate("IS")).append(e.getValue()).append(I18N.translate("HEALDAMAGE"));
									} else {
										sb.append(tgt.getName()).append(I18N.translate("IS")).append(e.getValue()).append(I18N.translate("DAMAGE"));
									}
								}
							} else {
								//失敗
								sb.append(I18N.translate("BUT"));
								sb.append(I18N.translate("NO_EFFECT"));
							}
							msg.setText(sb.toString());
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
							return;
						}
						//その他の場合はターゲット選択へ
						List<Text> options3 = new ArrayList<>();
						options3.addAll(list.stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(options3, "ITEM_WINDOW_SUB", i.getName() + I18N.translate("WHO_DO_USE")));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case EQIP:
						//装備できるアイテムかどうかで分岐
						if (getSelectedPC().getEqipment().values().contains(i)) {
							//すでに装備している時は外す
							getSelectedPC().getEqipment().put(i.getEqipmentSlot(), null);
							msg.setText(i.getName() + I18N.translate("REMOVE_EQUP"));
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else if (getSelectedPC().canEqip(i)) {
							//装備する
							getSelectedPC().addEqip(i);
							msg.setText(i.getName() + I18N.translate("IS_EQIP"));
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							//装備できない
							msg.setText(i.getName() + I18N.translate("NOT_EQIP"));
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						}
						break;
					case PASS:
						//パスターゲットに移動
						List<Text> options2 = new ArrayList<>();
						options2.addAll(list.stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(options2, "ITEM_WINDOW_SUB", i.getName() + I18N.translate("WHO_DO_PASS")));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case CHECK:
						//CHECKモードでは価値、キーアイテム属性、スロット、攻撃力、DCSを表示すること！
						//アイテムの詳細をサブに表示
						StringBuilder sb = new StringBuilder();
						sb.append(i.getName()).append(Text.getLineSep());

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
						sb.append(" ").append(I18N.translate("VALUE")).append(":").append(i.getValue());
						sb.append(Text.getLineSep());
						//装備スロット
						sb.append(" ").append(I18N.translate("SLOT")).append(":").append(i.getEqipmentSlot() != null
								? i.getEqipmentSlot().getName()
								: I18N.translate("NONE"));
						sb.append(Text.getLineSep());
						//WMT
						if (i.getWeaponMagicType() != null) {
							sb.append(" ").append(I18N.translate("WMT")).append(":").append(i.getWeaponMagicType().getName());
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
							sb.append(" ").append(I18N.translate("AREA")).append(":").append(area);
							sb.append(Text.getLineSep());
						}
						//キーアイテム属性
						if (!i.canSale()) {
							sb.append(" ").append(I18N.translate("CANT_SALE"));
							sb.append(Text.getLineSep());
						}
						//DCS
						if (i.getDamageCalcStatusKey() != null && !i.getDamageCalcStatusKey().isEmpty()) {
							String dcs = "";
							for (StatusKey s : i.getDamageCalcStatusKey()) {
								dcs += s.getDesc() + ",";
							}
							dcs = dcs.substring(0, dcs.length() - 1);
							sb.append(" ").append(I18N.translate("DCS")).append(":").append(dcs);
							sb.append(Text.getLineSep());
						}
						//戦闘中アクション
						if (i.getBattleEvent() != null && !i.getBattleEvent().isEmpty()) {
							sb.append(" ").append(I18N.translate("CAN_USE_BATTLE"));
							sb.append(Text.getLineSep());
						}
						//フィールドアクション
						if (i.getFieldEvent() != null && !i.getFieldEvent().isEmpty()) {
							sb.append(" ").append(I18N.translate("CAN_USE_FIELD"));
							sb.append(Text.getLineSep());
						}
						if (i.isEqipItem()) {
							//攻撃回数
							if (i.getActionCount() > 1) {
								sb.append(" ").append(I18N.translate("ACTION_COUNT").replaceAll("n", i.getActionCount() + ""));
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
								sb.append(I18N.translate("CAN_UPGRADE").replaceAll("n", i.getUpgradeMaterials().size() + ""));
								sb.append(Text.getLineSep());
							} else {
								sb.append(" ");
								sb.append(I18N.translate("THIS_ITEM_CANT_UPGRADE"));
								sb.append(Text.getLineSep());
							}
							//解体
							if (!i.getDissasseMaterials().isEmpty()) {
								sb.append(" ");
								sb.append(I18N.translate("IF_DISASSEMBLY_GET"));
								sb.append(Text.getLineSep());
								for (Map.Entry<Material, Integer> e : i.getDissasseMaterials().entrySet()) {
									sb.append("   ");
									sb.append(e.getKey().getName()).append(":").append(e.getValue());
									sb.append(Text.getLineSep());
								}
								sb.append(Text.getLineSep());
							} else {
								sb.append(" ");
								sb.append(I18N.translate("THIS_ITEM_CANT_DISASSE"));
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
							msg.setText(I18N.translate("CANT_SALE"));
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							List<Text> options4 = new ArrayList<>();
							options4.add(new Text(I18N.translate("NO")));
							options4.add(new Text(I18N.translate("YES")));
							dropConfirm.reset();
							dropConfirm.setText(new Choice(options4, "DROP_CONFIRM", i.getName() + I18N.translate("REALLY_DROP")));
							dropConfirm.allText();
							group.show(dropConfirm);
							mode = Mode.DROP_CONFIRM;
						}
						break;
				}
				break;
			case TARGET_SELECT:
				//tgtウインドウから選択された対象者をもとにUSEまたはPASSを実行
				//use or pass
				assert choiceUse.getSelect() == USE || choiceUse.getSelect() == PASS : "ITEMWINDOW : choice user select is missmatch";
				if (choiceUse.getSelect() == USE) {
					commitUse();
					group.show(msg);
					mode = Mode.WAIT_MSG_CLOSE_TO_IUS;//useしたら消えている可能性があるためIUS
				}
				if (choiceUse.getSelect() == PASS) {
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
		ActionResult r = i.exec(ActionTarget.instantTarget(getSelectedPC(), i, tgt).setInField(true));
		StringBuilder sb = new StringBuilder();
		sb.append(tgt.getName()).append(I18N.translate("IS")).append(i.getName()).append(I18N.translate("USE_ITEM"));
		sb.append(Text.getLineSep());
		if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
			//成功
			//効果測定
			Map<StatusKey, Integer> map = tgt.calcDamage();
			for (Map.Entry<StatusKey, Integer> e : map.entrySet()) {
				if (e.getValue() > 0) {
					sb.append(tgt.getName()).append(I18N.translate("IS")).append(e.getValue()).append(I18N.translate("HEALDAMAGE"));
					sb.append(Text.getLineSep());
				} else if (e.getValue() < 0) {
					sb.append(tgt.getName()).append(I18N.translate("IS")).append(e.getValue()).append(I18N.translate("DAMAGE"));
					sb.append(Text.getLineSep());
				} else {
					//==0
					sb.append(I18N.translate("BUT")).append(I18N.translate("NO_EFFECT"));
					sb.append(Text.getLineSep());
				}
				sb.append(Text.getLineSep());
			}
			if (map.isEmpty()) {
				sb.append(I18N.translate("BUT")).append(I18N.translate("NO_EFFECT"));
				sb.append(Text.getLineSep());
			}
			//DROP_ITEMイベントの判定
			for (ActionEvent e : i.getFieldEvent()) {
				if (e.getParameterType() == ParameterType.ITEM_LOST) {
					if (e.getP() >= 1f || Random.percent(e.getP())) {
						tgt.getItemBag().drop(i);
						sb.append(i.getName()).append(I18N.translate("ITEM_DROP"));
						sb.append(Text.getLineSep());
					}
				}
			}
		} else {
			//失敗
			sb.append(I18N.translate("BUT"));
			sb.append(I18N.translate("NO_EFFECT"));
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
			sb.append(item.getName()).append(Text.getLineSep());
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
			msg.setText(getSelectedPC().getName() + I18N.translate("IS")
					+ tgt.getName() + I18N.translate("TO") + i.getName() + I18N.translate("PASSED"));
			mainSelect = 0;
		} else {
			msg.setText(getSelectedPC().getName() + I18N.translate("IS") + i.getName() + I18N.translate("RESET_ITEM"));
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
			sb.append(item.getName()).append(Text.getLineSep());
			j++;
		}
		main.setText(sb.toString());
		main.allText();
		main.setVisible(true);
	}

	private void commitDrop() {
		dropConfirm.close();
		Item i = getSelectedItem();
		//1個しか持っていなかったら装備を外す
		if (getSelectedPC().isEqip(i.getName()) && getSelectedPC().getItemBag().getItems().stream().filter(p -> p.equals(i)).count() == 1) {
			getSelectedPC().removeEqip(i);
		}
		getSelectedPC().getItemBag().drop(i);
		msg.setText(getSelectedPC().getName() + I18N.translate("IS") + i.getName() + I18N.translate("WAS_DROP"));
		msg.allText();
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
				sb.append("  ").append(I18N.translate("NOTHING_ITEM"));
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
					sb.append(i.getName()).append(Text.getLineSep());
					j++;
				}
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
		msg.draw(g);
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append(sb.length() > 0 ) ;
		System.out.println(sb);
	}
}



