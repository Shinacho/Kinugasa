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
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;
import kinugasa.util.StringUtil;

/**
 *
 * @vesion 1.0.0 - 2022/12/22_16:01:21<br>
 * @author Shinacho<br>
 */
public class ItemWindow extends BasicSprite {

	private List<Actor> list;
	private MessageWindow main;
	private MessageWindow choiceUse, dropConfirm, disasseConfirm, tgtSelect;
	private ScrollSelectableMessageWindow msg;
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
		msg = new ScrollSelectableMessageWindow((int) x, (int) y, (int) w, (int) h, 23);
		msg.setVisible(false);

		group = new MessageWindowGroup(choiceUse, dropConfirm, tgtSelect, msg.getWindow(), disasseConfirm);
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
				if (msg.isVisible()) {
					msg.nextSelect();
				}
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
				if (msg.isVisible()) {
					msg.prevSelect();
				}
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
	private static final int EQIP_LEFT = 3;
	private static final int EQIP_TWO_HAND = 4;
	private static final int PASS = 5;
	private static final int DISASSEMBLY = 6;
	private static final int DROP = 7;

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
				options.add(Text.noI18N(I18N.get(GameSystemI18NKeys.調べる)));
				options.add(Text.noI18N(I18N.get(GameSystemI18NKeys.使う)));
				options.add(Text.noI18N(I18N.get(GameSystemI18NKeys.装備)));
				options.add(Text.noI18N(I18N.get(GameSystemI18NKeys.左手に装備)));
				options.add(Text.noI18N(I18N.get(GameSystemI18NKeys.両手持ちで装備)));
				options.add(Text.noI18N(I18N.get(GameSystemI18NKeys.渡す)));
				options.add(Text.noI18N(I18N.get(GameSystemI18NKeys.解体)));
				options.add(Text.noI18N(I18N.get(GameSystemI18NKeys.捨てる)));
				Choice c = new Choice(options, "ITEM_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xを,
						getSelectedItem().getVisibleName()));
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
						if (!i.isField()) {
							//フィールドでは使えません
							msg.setText(I18N.get(GameSystemI18NKeys.XはXを使用した, getSelectedPC().getVisibleName(), i.getVisibleName())
									+ Text.getLineSep()
									+ I18N.get(GameSystemI18NKeys.しかし効果がなかった));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//ターゲット確認
						//SELFのみの場合即時実行
						if (i.getMainEvents().isEmpty()) {
							//即時実行してサブに効果を出力
							Status tgt = getSelectedPC();
							tgt.saveBeforeDamageCalc();
							ActionTarget t = new ActionTarget(
									GameSystem.getInstance().getPCbyID(getSelectedPC().getId()),
									i,
									List.of(GameSystem.getInstance().getPCbyID(getSelectedPC().getId())),
									true);
							ActionResult r = i.exec(t);
							//ActionEventでaddWhen0Condition実行済み
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを使用した,
									GameSystem.getInstance().getPCbyID(getSelectedPC().getId()).getVisibleName(),
									i.getVisibleName()));
							sb.append(Text.getLineSep());
							if (r.is成功あり()) {
								//成功
								//効果測定
								StatusValueSet vs = tgt.getDamageFromSavePoint();
								for (StatusValue v : vs) {
									if (v.getValue() > 0) {
										sb.append(I18N.get(GameSystemI18NKeys.Xの,
												GameSystem.getInstance().getPCbyID(getSelectedPC().getId()).getVisibleName()))
												.append(I18N.get(GameSystemI18NKeys.Xは, v.getKey().getVisibleName()))
												.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs((int) v.getValue()) + ""));
									} else {
										sb.append(I18N.get(GameSystemI18NKeys.Xの,
												GameSystem.getInstance().getPCbyID(getSelectedPC().getId()).getVisibleName()))
												.append(I18N.get(GameSystemI18NKeys.Xに, v.getKey().getVisibleName()))
												.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs((int) v.getValue()) + ""));
									}
								}
							} else {
								//失敗
								sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
							}
							msg.setText(sb.toString());
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
							return;
						}
						//チームが入っている場合即時実行
						//USERリザルトは表示しないでいい
						if (i.getTgtType() == Action.ターゲットモード.全員
								|| i.getTgtType() == Action.ターゲットモード.グループ_味方全員
								|| i.getTgtType() == Action.ターゲットモード.グループ_切替可能_初期選択味方
								|| i.getTgtType() == Action.ターゲットモード.グループ_切替可能_初期選択敵
								|| i.getTgtType() == Action.ターゲットモード.全員_自身除く
								|| i.getTgtType() == Action.ターゲットモード.グループ_味方全員_自身除く
								|| i.getTgtType() == Action.ターゲットモード.グループ_切替可能_初期選択味方_自身除く
								|| i.getTgtType() == Action.ターゲットモード.グループ_切替可能_初期選択敵_自身除く) {
							StringBuilder sb = new StringBuilder();
							//即時実行してサブに効果を出力
							sb.append(I18N.get(GameSystemI18NKeys.XはXを使用した,
									GameSystem.getInstance().getPCbyID(getSelectedPC().getId()).getVisibleName(),
									i.getVisibleName()));
							sb.append(Text.getLineSep());
							GameSystem.getInstance().getParty().forEach(p -> p.getStatus().saveBeforeDamageCalc());
							ActionTarget t = new ActionTarget(
									GameSystem.getInstance().getPCbyID(getSelectedPC().getId()),
									i,
									GameSystem.getInstance().getParty(),
									true);
							ActionResult r = i.exec(t);
							for (ActionResult.PerEvent p : r.getMainEventResultAsList()) {
								for (Map.Entry<Actor, ActionResult.EventActorResult> e : p.perActor.entrySet()) {
									StatusValueSet vs = e.getKey().getStatus().getDamageFromSavePoint();
									if (vs.isEmpty() || vs.stream().allMatch(q -> q.getValue() == 0)) {
										sb.append(I18N.get(GameSystemI18NKeys.しかしXには効果がなかった, e.getKey().getVisibleName()));
										sb.append(Text.getLineSep());
									} else {
										for (StatusValue v : vs) {
											if (v.getValue() > 0) {
												sb.append(I18N.get(GameSystemI18NKeys.Xの, e.getKey().getVisibleName()))
														.append(I18N.get(GameSystemI18NKeys.Xは, v.getKey().getVisibleName()))
														.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs((int) v.getValue()) + ""));
											} else if (v.getValue() < 0) {
												sb.append(I18N.get(GameSystemI18NKeys.Xの, e.getKey().getVisibleName()))
														.append(I18N.get(GameSystemI18NKeys.Xに, v.getKey().getVisibleName()))
														.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs((int) v.getValue()) + ""));
											}
											sb.append(Text.getLineSep());
										}
									}
								}
							}
							msg.setText(sb.toString());
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
							return;
						}
						//その他の場合はターゲット選択へ
						List<Text> options3 = new ArrayList<>();
						options3.addAll(list.stream().map(p -> Text.noI18N(p.getVisibleName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(options3, "ITEM_WINDOW_SUB",
								I18N.get(GameSystemI18NKeys.Xを誰に使う, i.getVisibleName())));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case EQIP:
						//装備解除不能属性がある場合は外せない
						if (!i.canUnEqip()) {
							//外せない
							msg.setText(I18N.get(GameSystemI18NKeys.XはXを誰かに渡す気はないようだ, getSelectedPC().getVisibleName(), i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							break;
						}
						//装備できるアイテムかどうかで分岐
						if (getSelectedPC().getEqip().values().contains(i)) {
							//すでに装備している時は外す
							//バッグに分類されるアイテムかつアイテム数がもともと持てる数を上回る場合外せない
							if (ActionStorage.isItemBagItem(i.getId())) {
								//もともとのサイズ
								int itemBagDefaultMax = getSelectedPC().getRace().getItemBagSize();
								//現在の持ってる数
								int currentSize = getSelectedPC().getItemBag().size();
								//現在のサイズがもともともサイズより大きい場合は外せない
								if (currentSize > itemBagDefaultMax) {
									//外せない
									msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
									msg.allText();
									group.show(msg.getWindow());
									mode = Mode.WAIT_MSG_CLOSE_TO_CU;
									break;
								}
							}
							if (ActionStorage.isBookBagItem(i.getId())) {
								//もともとのサイズ
								int itemBagDefaultMax = getSelectedPC().getRace().getBookBagSize();
								//現在の持ってる数
								int currentSize = getSelectedPC().getBookBag().size();
								//現在のサイズがもともともサイズより大きい場合は外せない
								if (currentSize > itemBagDefaultMax) {
									//外せない
									msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
									msg.allText();
									group.show(msg.getWindow());
									mode = Mode.WAIT_MSG_CLOSE_TO_CU;
									break;
								}
							}
							//外すスロットを設定
							//iが弓の場合、左手を外す（同時に右手も外す
							if (i.getWeaponType() != null) {
								if (i.getWeaponType() == WeaponType.弓) {
									getSelectedPC().unEqip(EqipSlot.右手);
									getSelectedPC().unEqip(EqipSlot.左手);
								} else {
									//このアイテムを左手に装備している場合は左てを、そうでなければアイテムのスロットを利用
									EqipSlot tgtSlot
											= i.equals(getSelectedPC().getEqip().get(EqipSlot.左手))
											? EqipSlot.左手 : i.getSlot();
									getSelectedPC().unEqip(tgtSlot);
									//右手を外した場合で左手が両手持ちの場合は左手も外す
									if (tgtSlot == EqipSlot.右手) {
										if (ActionStorage.getInstance().両手持ち.equals(getSelectedPC().getEqip().get(EqipSlot.左手))) {
											getSelectedPC().unEqip(EqipSlot.左手);
										}
									}
								}
							} else {
								getSelectedPC().unEqip(i.getSlot());
							}

							getSelectedPC().updateAction();
							String cnd = getSelectedPC().addWhen0Condition();
							//アイテム所持数の再計算
							getSelectedPC().updateBagSize();
							if (cnd == null) {
								msg.setText(I18N.get(GameSystemI18NKeys.Xを外した, i.getVisibleName()));
							} else {
								msg.setText(I18N.get(GameSystemI18NKeys.Xを外した, i.getVisibleName())
										+ Text.getLineSep() + cnd);
							}
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else if (i.canEqip(GameSystem.getInstance().getPCbyID(getSelectedPC().getId()))) {
							//左手に装備済みだった場合外す
							if (getSelectedPC().getEqip().containsKey(EqipSlot.左手)
									&& getSelectedPC().getEqip().get(EqipSlot.左手) != null
									&& getSelectedPC().getEqip().get(EqipSlot.左手).equals(i)) {
								getSelectedPC().getEqip().put(EqipSlot.左手, null);
							}
							//装備する
							boolean ryoute = false;
							if (i.getWeaponType() != null) {
								//両手持ち武器の場合は左手を強制的に両手持ちにする
								if (i.getWeaponType() == WeaponType.弓) {
									getSelectedPC().eqip(EqipSlot.右手, ActionStorage.getInstance().両手持ち_弓);
									getSelectedPC().eqip(EqipSlot.左手, i);
									ryoute = true;
								} else if (Set.of(WeaponType.大剣, WeaponType.大杖, WeaponType.銃, WeaponType.弩, WeaponType.薙刀)
										.contains(i.getWeaponType())) {
									getSelectedPC().eqip(EqipSlot.右手, i);
									getSelectedPC().eqipLeftHand(ActionStorage.getInstance().両手持ち);
									ryoute = true;
								} else {
									getSelectedPC().eqip(i);
								}
							} else {
								getSelectedPC().eqip(i);
							}
							getSelectedPC().updateAction();
							String cnd = getSelectedPC().addWhen0Condition();
							//アイテム所持数の再計算
							getSelectedPC().updateBagSize();
							if (cnd == null) {
								if (ryoute) {
									msg.setText(I18N.get(GameSystemI18NKeys.Xを両手持ちで装備した, i.getVisibleName()));
								} else {
									msg.setText(I18N.get(GameSystemI18NKeys.Xを装備した, i.getVisibleName()));
								}
							} else {
								if (ryoute) {
									msg.setText(I18N.get(GameSystemI18NKeys.Xを両手持ちで装備した, i.getVisibleName())
											+ Text.getLineSep() + cnd);
								} else {
									msg.setText(I18N.get(GameSystemI18NKeys.Xを装備した, i.getVisibleName())
											+ Text.getLineSep() + cnd);
								}
							}
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							//装備できない
							msg.setText(I18N.get(GameSystemI18NKeys.Xは装備できない, i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						}
						break;
					case EQIP_LEFT: {
						//装備解除不能属性がある場合は外せない
						if (!i.canUnEqip()) {
							//外せない
							msg.setText(I18N.get(GameSystemI18NKeys.XはXを誰かに渡す気はないようだ, getSelectedPC().getVisibleName(), i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							break;
						}
						//武器じゃなかったら装備できないを表示
						if (!i.isWeapon()) {
							msg.setText(I18N.get(GameSystemI18NKeys.Xは左手に装備できない, i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//両手持ち武器の場合は左手に装備できない
						if (Set.of(WeaponType.大剣, WeaponType.大杖, WeaponType.銃, WeaponType.弩, WeaponType.薙刀)
								.contains(i.getWeaponType())) {
							msg.setText(I18N.get(GameSystemI18NKeys.Xは左手に装備できない, i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						if (i.canEqip(GameSystem.getInstance().getPCbyID(getSelectedPC().getId()))) {
							//右手に装備済みだった場合外す
							if (getSelectedPC().getEqip().containsKey(EqipSlot.右手)
									&& getSelectedPC().getEqip().get(EqipSlot.右手) != null
									&& getSelectedPC().getEqip().get(EqipSlot.右手).equals(i)) {
								getSelectedPC().getEqip().put(EqipSlot.右手, null);
							}
							//装備する
							getSelectedPC().eqip(EqipSlot.左手, i);
							//弓の場合は右手を両手持ちにする
							if (i.getWeaponType() == WeaponType.弓) {
								getSelectedPC().eqip(EqipSlot.右手, ActionStorage.getInstance().両手持ち_弓);
							}
							getSelectedPC().updateAction();
							String cnd = getSelectedPC().addWhen0Condition();
							//アイテム所持数の再計算
							getSelectedPC().updateBagSize();
							if (cnd == null) {
								msg.setText(I18N.get(GameSystemI18NKeys.Xを左手に装備した, i.getVisibleName()));
							} else {
								msg.setText(I18N.get(GameSystemI18NKeys.Xを左手に装備した, i.getVisibleName())
										+ Text.getLineSep() + cnd);
							}
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							//装備できない
							msg.setText(I18N.get(GameSystemI18NKeys.Xは左手に装備できない, i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						}
						break;
					}
					case EQIP_TWO_HAND: {
						//装備解除不能属性がある場合は外せない
						if (!i.canUnEqip()) {
							//外せない
							msg.setText(I18N.get(GameSystemI18NKeys.XはXを誰かに渡す気はないようだ, getSelectedPC().getVisibleName(), i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							break;
						}
						//武器じゃなかったら装備できないを表示
						if (!i.isWeapon()) {
							msg.setText(I18N.get(GameSystemI18NKeys.Xは両手には装備できない, i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						if (i.canEqip(GameSystem.getInstance().getPCbyID(getSelectedPC().getId()))) {
							//装備する
							//弓の場合は右手を両手持ちにする
							if (i.getWeaponType() == WeaponType.弓) {
								getSelectedPC().eqip(EqipSlot.右手, ActionStorage.getInstance().両手持ち_弓);
							} else {
								getSelectedPC().eqip(EqipSlot.右手, i);
								//左手に両手持ちを装備
								getSelectedPC().eqipLeftHand(ActionStorage.getInstance().両手持ち);
							}
							getSelectedPC().updateAction();
							String cnd = getSelectedPC().addWhen0Condition();
							//アイテム所持数の再計算
							getSelectedPC().updateBagSize();
							if (cnd == null) {
								msg.setText(I18N.get(GameSystemI18NKeys.Xを両手持ちで装備した, i.getVisibleName()));
							} else {
								msg.setText(I18N.get(GameSystemI18NKeys.Xを両手持ちで装備した, i.getVisibleName())
										+ Text.getLineSep() + cnd);
							}
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							//装備できない
							msg.setText(I18N.get(GameSystemI18NKeys.Xは両手には装備できない, i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						}
						break;
					}
					case PASS:
						//装備解除不能属性がある場合は外せない
						if (!i.canUnEqip()) {
							//外せない
							msg.setText(I18N.get(GameSystemI18NKeys.XはXを誰かに渡す気はないようだ, getSelectedPC().getVisibleName(), i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							break;
						}
						//すでに装備している時は外す
						//バッグに分類されるアイテムかつアイテム数がもともと持てる数を上回る場合外せない
						if (ActionStorage.isItemBagItem(i.getId())) {
							//もともとのサイズ
							int itemBagDefaultMax = getSelectedPC().getRace().getItemBagSize();
							//現在の持ってる数
							int currentSize = getSelectedPC().getItemBag().size();
							//現在のサイズがもともともサイズより大きい場合は外せない
							if (currentSize > itemBagDefaultMax) {
								//外せない
								msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
								msg.allText();
								group.show(msg.getWindow());
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								break;
							}
						}
						if (ActionStorage.isBookBagItem(i.getId())) {
							//もともとのサイズ
							int itemBagDefaultMax = getSelectedPC().getRace().getBookBagSize();
							//現在の持ってる数
							int currentSize = getSelectedPC().getBookBag().size();
							//現在のサイズがもともともサイズより大きい場合は外せない
							if (currentSize > itemBagDefaultMax) {
								//外せない
								msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
								msg.allText();
								group.show(msg.getWindow());
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								break;
							}
						}
						//パスターゲットに移動
						List<Text> options2 = new ArrayList<>();
						options2.addAll(list.stream().map(p -> Text.noI18N(p.getVisibleName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(options2, "ITEM_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xを誰に渡す,
								i.getVisibleName())));
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
						sb.append("----").append(Text.getLineSep());
						//装備品の場合、スタイルとエンチャを表示
						if (i.getSlot() != null) {
							//スタイル
							if (i.getStyle() != null) {
								sb.append(I18N.get(GameSystemI18NKeys.様式))
										.append(":")
										.append(i.getStyle().getVisibleName())
										.append("(")
										.append(i.getStyle().descI18N())
										.append(")")
										.append(Text.getLineSep());
							}
							//エンチャント
							if (i.getEnchant() != null) {
								sb.append(I18N.get(GameSystemI18NKeys.エンチャント))
										.append(":")
										.append(i.getEnchant().getVisibleName())
										.append("(")
										.append(i.getEnchant().descI18N())
										.append(")")
										.append(Text.getLineSep());
							}
						}
						//価値
						if (i.canSale() && i.canUnEqip()) {
							sb.append(I18N.get(GameSystemI18NKeys.価値))
									.append(":").append(i.getPrice());
							sb.append(Text.getLineSep());
						}
						//装備スロット
						if (i.getSlot() != null) {
							if (i.getWeaponType() != null && i.getWeaponType() == WeaponType.弓) {
								sb.append(I18N.get(GameSystemI18NKeys.装備スロット))
										.append(":").append(EqipSlot.左手.getVisibleName());
								sb.append("(").append(I18N.get(GameSystemI18NKeys.両手持ち)).append(")");
							} else if (i.getWeaponType() != null && Set.of(WeaponType.大剣, WeaponType.大杖, WeaponType.銃, WeaponType.弩, WeaponType.薙刀)
									.contains(i.getWeaponType())) {
								sb.append(I18N.get(GameSystemI18NKeys.装備スロット))
										.append(":").append(EqipSlot.右手.getVisibleName());
								sb.append("(").append(I18N.get(GameSystemI18NKeys.両手持ち)).append(")");
							} else if (i.getWeaponType() != null) {
								sb.append(I18N.get(GameSystemI18NKeys.装備スロット))
										.append(":").append(i.getSlot().getVisibleName());
								sb.append("(").append(I18N.get(GameSystemI18NKeys.両手持ち可能)).append(")");
							} else {
								sb.append(I18N.get(GameSystemI18NKeys.装備スロット))
										.append(":").append(i.getSlot().getVisibleName());
							}
							sb.append(Text.getLineSep());
						}
						//WMT
						if (i.getWeaponType() != null) {
							sb.append(I18N.get(GameSystemI18NKeys.武器種別))
									.append(":").append(i.getWeaponType().getVisibleName());
							sb.append(Text.getLineSep());
						}
						//area
						int area = 0;
						if (i.isWeapon()) {
							//範囲表示するのは武器だけ
							area = i.getArea();
						}
						if (area != 0) {
							sb.append(I18N.get(GameSystemI18NKeys.範囲)).append(":").append(area);
							sb.append(Text.getLineSep());
						}
						//キーアイテム属性
						if (!i.canSale()) {
							sb.append(I18N.get(GameSystemI18NKeys.このアイテムは売ったり捨てたり解体したりできない));
							sb.append(Text.getLineSep());
						}
						if (!i.canUnEqip()) {
							sb.append(I18N.get(GameSystemI18NKeys.このアイテムは誰かに渡したり装備解除したりできない));
							sb.append(Text.getLineSep());
						}
						//DCS
						if (i.getDcs() != null) {
							String dcs = i.getDcs().getVisibleName();
							sb.append(I18N.get(GameSystemI18NKeys.ダメージ計算ステータス)).append(":").append(dcs);
							sb.append(Text.getLineSep());
						}
						//戦闘中アクション
						if (i.isBattle()) {
							sb.append(I18N.get(GameSystemI18NKeys.このアイテムは戦闘中使える));
							sb.append(Text.getLineSep());
						}
						//フィールドアクション
						if (i.isField()) {
							sb.append(I18N.get(GameSystemI18NKeys.このアイテムはフィールドで使える));
							sb.append(Text.getLineSep());
						}
						if (i.getSlot() != null) {
							//攻撃回数
							if (i.isWeapon() && i.getEffectedATKCount() > 1) {
								sb.append(I18N.get(GameSystemI18NKeys.攻撃回数)).append(":")
										.append(i.getEffectedATKCount() + "");
								sb.append(Text.getLineSep());
							}
							//eqStatus
							if (i.getEffectedStatus() != null && !i.getEffectedStatus().isEmpty()) {
								sb.append(I18N.get(GameSystemI18NKeys.ステータス));
								sb.append(Text.getLineSep());
								for (StatusValue s : i.getEffectedStatus()) {
									if (!s.getKey().isVisible()) {
										continue;
									}
									String v;
									if (s.getKey().isPercent()) {
										v = (float) (s.getValue() * 100) + "%";//1%単位
									} else {
										v = (int) s.getValue() + "";
									}
									if (!v.startsWith("0")) {
										sb.append(" ");
										sb.append(s.getKey().getVisibleName()).append(":").append(v);
										sb.append(Text.getLineSep());
									}
								}
							}
							//eqAttr
							if (i.getEffectedAttrIn() != null && !i.getEffectedAttrIn().isEmpty()) {
								sb.append(I18N.get(GameSystemI18NKeys.被属性));
								sb.append(Text.getLineSep());
								for (AttributeValue a : i.getEffectedAttrIn()) {
									String v = (float) (a.getValue() * 100) + "%";
									if (!v.startsWith("0")) {
										sb.append(" ");
										sb.append(a.getKey().getVisibleName()).append(":").append(v);
										sb.append(Text.getLineSep());
									}
								}
							}
							if (i.getEffectedAttrOut() != null && !i.getEffectedAttrOut().isEmpty()) {
								sb.append(I18N.get(GameSystemI18NKeys.与属性));
								sb.append(Text.getLineSep());
								for (AttributeValue a : i.getEffectedAttrOut()) {
									String v = (float) (a.getValue() * 100) + "%";
									if (!v.startsWith("0")) {
										sb.append(" ");
										sb.append(a.getKey().getVisibleName()).append(":").append(v);
										sb.append(Text.getLineSep());
									}
								}
							}
							if (i.getEffectedConditionRegist() != null && i.getEffectedConditionRegist().values().stream().anyMatch(p -> p != 0f)) {
								sb.append(I18N.get(GameSystemI18NKeys.状態異常耐性));
								sb.append(Text.getLineSep());
								for (Map.Entry<ConditionKey, Float> e : i.getEffectedConditionRegist().entrySet()) {
									String v = (float) (e.getValue() * 100) + "%";
									if (!v.startsWith("0")) {
										sb.append(" ");
										sb.append(e.getKey().getVisibleName()).append(":").append(v);
										sb.append(Text.getLineSep());
									}
								}
							}
							//強化
							if (i.canUpgrade()) {
								sb.append(I18N.get(GameSystemI18NKeys.このアイテムはあとX回強化できる,
										i.get残り強化回数() + ""));
								sb.append(Text.getLineSep());
							} else {
								sb.append(I18N.get(GameSystemI18NKeys.このアイテムは強化できない));
								sb.append(Text.getLineSep());
							}
							//解体
							if (i.canDisasse()) {
								sb.append(I18N.get(GameSystemI18NKeys.このアイテムは解体できる));
								sb.append(Text.getLineSep());
							} else {
								sb.append(I18N.get(GameSystemI18NKeys.このアイテムは解体できない));
								sb.append(Text.getLineSep());
							}
							if (i.canUpgrade() || i.canDisasse()) {
								sb.append(I18N.get(GameSystemI18NKeys.解体＿強化時の素材));
								sb.append(Text.getLineSep());
								for (Map.Entry<Material, Integer> e : i.getEffectedMaterials().entrySet()) {
									sb.append(" ");
									sb.append(e.getKey().getVisibleName()).append(":").append(e.getValue());
									sb.append(Text.getLineSep());
								}
							}
							sb.append(Text.getLineSep());

						}
						msg.setText(sb.toString());
						msg.allText();
						group.show(msg.getWindow());
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						break;
					case DROP:
						//バッグに分類されるアイテムかつアイテム数がもともと持てる数を上回る場合外せない
						if (ActionStorage.isItemBagItem(i.getId())) {
							//もともとのサイズ
							int itemBagDefaultMax = getSelectedPC().getRace().getItemBagSize();
							//現在の持ってる数
							int currentSize = getSelectedPC().getItemBag().size();
							//現在のサイズがもともともサイズより大きい場合は外せない
							if (currentSize > itemBagDefaultMax) {
								//外せない
								msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
								msg.allText();
								group.show(msg.getWindow());
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								break;
							}
						}
						if (ActionStorage.isBookBagItem(i.getId())) {
							//もともとのサイズ
							int itemBagDefaultMax = getSelectedPC().getRace().getBookBagSize();
							//現在の持ってる数
							int currentSize = getSelectedPC().getBookBag().size();
							//現在のサイズがもともともサイズより大きい場合は外せない
							if (currentSize > itemBagDefaultMax) {
								//外せない
								msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
								msg.allText();
								group.show(msg.getWindow());
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								break;
							}
						}
						//装備解除不能属性がある場合は外せない
						if (!i.canUnEqip()) {
							//外せない
							msg.setText(I18N.get(GameSystemI18NKeys.XはXを誰かに渡す気はないようだ, getSelectedPC().getVisibleName(), i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							break;
						}
						//drop確認ウインドウを有効化
						if (!i.canSale()) {
							msg.setText(I18N.get(GameSystemI18NKeys.このアイテムは捨てられない));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							List<Text> options4 = new ArrayList<>();
							options4.add(Text.noI18N(I18N.get(GameSystemI18NKeys.いいえ)));
							options4.add(Text.noI18N(I18N.get(GameSystemI18NKeys.はい)));
							dropConfirm.reset();
							dropConfirm.setText(new Choice(options4, "DROP_CONFIRM", I18N.get(GameSystemI18NKeys.Xを本当にすてる, i.getVisibleName())));
							dropConfirm.allText();
							group.show(dropConfirm);
							mode = Mode.DROP_CONFIRM;
						}
						break;
					case DISASSEMBLY:
						//バッグに分類されるアイテムかつアイテム数がもともと持てる数を上回る場合外せない
						if (ActionStorage.isItemBagItem(i.getId())) {
							//もともとのサイズ
							int itemBagDefaultMax = getSelectedPC().getRace().getItemBagSize();
							//現在の持ってる数
							int currentSize = getSelectedPC().getItemBag().size();
							//現在のサイズがもともともサイズより大きい場合は外せない
							if (currentSize > itemBagDefaultMax) {
								//外せない
								msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
								msg.allText();
								group.show(msg.getWindow());
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								break;
							}
						}
						if (ActionStorage.isBookBagItem(i.getId())) {
							//もともとのサイズ
							int itemBagDefaultMax = getSelectedPC().getRace().getBookBagSize();
							//現在の持ってる数
							int currentSize = getSelectedPC().getBookBag().size();
							//現在のサイズがもともともサイズより大きい場合は外せない
							if (currentSize > itemBagDefaultMax) {
								//外せない
								msg.setText(I18N.get(GameSystemI18NKeys.持ち物が多すぎてXを外せない, i.getVisibleName()));
								msg.allText();
								group.show(msg.getWindow());
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								break;
							}
						}
						//装備解除不能属性がある場合は外せない
						if (!i.canUnEqip()) {
							//外せない
							msg.setText(I18N.get(GameSystemI18NKeys.XはXを誰かに渡す気はないようだ, getSelectedPC().getVisibleName(), i.getVisibleName()));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							break;
						}
						//解体できるアイテムか判定
						if (!i.canSale() || !i.canDisasse()) {
							msg.setText(I18N.get(GameSystemI18NKeys.このアイテムは解体できない));
							msg.allText();
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						} else {
							List<Text> options5 = new ArrayList<>();
							options5.add(Text.noI18N(I18N.get(GameSystemI18NKeys.いいえ)));
							options5.add(Text.noI18N(I18N.get(GameSystemI18NKeys.はい)));
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
					group.show(msg.getWindow());
					mode = Mode.WAIT_MSG_CLOSE_TO_IUS;//useしたら消えている可能性があるためIUS
					break;
				}
				if (choiceUse.getSelect() == PASS) {
					//パスタの相手がこれ以上物を持てない場合失敗
					//TODO:交換機能が必要
					if (!GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect()).getItemBag().canAdd()) {
						String m = I18N.get(GameSystemI18NKeys.Xはこれ以上物を持てない,
								GameSystem.getInstance().getParty().get(tgtSelect.getSelect()).getVisibleName()
						);
						this.msg.setText(m);
						this.msg.allText();
						group.show(msg.getWindow());
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						break;
					}
					int itemBagSize = getSelectedPC().getItemBag().size();
					commitPass();
					boolean self = itemBagSize == getSelectedPC().getItemBag().size();
					group.show(msg.getWindow());
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
						group.show(msg.getWindow());
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
						group.show(msg.getWindow());
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
		getSelectedPC().pass(i, tgt);
		tgt.saveBeforeDamageCalc();
		ActionTarget t;
		ActionResult r = i.exec(new ActionTarget(
				GameSystem.getInstance().getPCbyID(getSelectedPC().getId()),
				i,
				List.of(GameSystem.getInstance().getPCbyID(tgt.getId())), true));
		getSelectedPC().updateAction();
		tgt.updateAction();
		tgt.addWhen0Condition();
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.get(GameSystemI18NKeys.XはXを使用した,
				GameSystem.getInstance().getPCbyID(getSelectedPC().getId()).getVisibleName(),
				i.getVisibleName()));
		sb.append(Text.getLineSep());
		if (r.is成功あり()) {
			//成功
			//効果測定
			StatusValueSet vs = tgt.getDamageFromSavePoint();
			if (vs.isEmpty()) {
				sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
				sb.append(Text.getLineSep());
			} else {
				for (StatusValue e : vs) {
					if (e.getValue() < 0) {
						sb.append(I18N.get(GameSystemI18NKeys.Xの,
								GameSystem.getInstance().getPCbyID(tgt.getId()).getVisibleName()))
								.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getVisibleName()))
								.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
						sb.append(Text.getLineSep());
					} else if (e.getValue() > 0) {
						sb.append(I18N.get(GameSystemI18NKeys.Xの,
								GameSystem.getInstance().getPCbyID(tgt.getId()).getVisibleName()))
								.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getVisibleName()))
								.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""));
						sb.append(Text.getLineSep());
					} else {
						//==0
						sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
						sb.append(Text.getLineSep());
					}
					sb.append(Text.getLineSep());
				}
			}
		} else {
			//失敗
			sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
			sb.append(Text.getLineSep());
		}

		msg.setText(sb.toString());
		msg.allText();
		group.show(msg.getWindow());

		main.setText(getItemListText(getSelectedPC()));
		main.allText();
		main.setVisible(true);
		mainSelect = 0;

	}

	private EqipSlot getEqipedSlot(Status s, Item i) {
		for (var v : s.getEqip().entrySet()) {
			if (v.getValue() != null) {
				if (v.getValue().equals(i)) {
					return v.getKey();
				}
			}
		}
		return null;
	}

	private void commitPass() {
		Status tgt = GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect());
		Item i = getSelectedItem();
		getSelectedPC().pass(i, tgt);
		if (!getSelectedPC().equals(tgt)) {
			String t = I18N.get(GameSystemI18NKeys.XはXにXを渡した, getSelectedPC().getVisibleName(), tgt.getVisibleName(), i.getVisibleName());
			msg.setText(t);
			mainSelect = 0;
			getSelectedPC().updateAction();
		} else {
			String t = I18N.get(GameSystemI18NKeys.XはXを持ち替えた, getSelectedPC().getVisibleName(), i.getVisibleName());
			msg.setText(t);
			mainSelect = getSelectedPC().getItemBag().size() - 1;
		}
		msg.allText();
		group.show(msg.getWindow());

		main.setText(getItemListText(getSelectedPC()));
		main.allText();
		main.setVisible(true);
	}

	private String getItemListText(Status s) {
		StringBuilder sb = new StringBuilder();
		sb.append("<---");
		sb.append(s.getVisibleName());
		sb.append("--->");
		sb.append(Text.getLineSep());
		int j = 0;
		boolean ryote = true;
		Set<Item> eqip = new HashSet<>();
		for (Item item : s.getItemBag()) {
			if (j == mainSelect) {
				sb.append(">・");
			} else {
				sb.append(" ・");
			}
			if (getSelectedPC().getEqip().values() != null
					&& !getSelectedPC().getEqip().values().isEmpty()
					&& getSelectedPC().getEqip().values().contains(item)
					&& !eqip.contains(item)) {
				if (item.isWeapon()) {
					eqip.add(item);
					sb.append("(E:").append(getEqipedSlot(getSelectedPC(), item).getVisibleName()).append(")");
					sb.append(item.getVisibleName()).append(Text.getLineSep());
					if (ryote) {
						if (s.getEqip().values().contains(ActionStorage.getInstance().両手持ち)) {
							sb.append(" ・(E:").append(EqipSlot.左手.getVisibleName()).append(")").append(ActionStorage.getInstance().両手持ち.getVisibleName());
							sb.append(Text.getLineSep());
						}
						if (s.getEqip().values().contains(ActionStorage.getInstance().両手持ち_弓)) {
							sb.append(" ・(E:").append(EqipSlot.右手.getVisibleName()).append(")").append(ActionStorage.getInstance().両手持ち_弓.getVisibleName());
							sb.append(Text.getLineSep());
						}
						ryote = false;
					}
				} else {
					eqip.add(item);
					sb.append("(E:").append(getEqipedSlot(getSelectedPC(), item).getVisibleName()).append(")");
					sb.append(item.getVisibleName()).append(Text.getLineSep());
				}
			} else {
				sb.append(item.getVisibleName()).append(Text.getLineSep());
			}
			j++;
		}
		sb.append(Text.getLineSep());
		sb.append(Text.getLineSep());
		sb.append(I18N.get(GameSystemI18NKeys.あとX個持てる, getSelectedPC().getItemBag().remainingSize() + ""));
		sb.append(Text.getLineSep());
		for (var v : getSelectedPC().getEqip().entrySet()) {
			if (v.getValue() == null) {
				sb.append("・").append(I18N.get(GameSystemI18NKeys.Xには何も装備していない, v.getKey().getVisibleName()));
				sb.append(Text.getLineSep());
			}
		}
		return sb.toString();
	}

	private void commitDrop() {
		dropConfirm.close();
		Item i = getSelectedItem();
		assert i.canSale() : "item is cant dispose : " + i;
		//もしこのアイテムを装備していたら、外す。外すのは1スロットだけ。
		//（もし両手に持っている場合、左手を外す
		if (i.isWeapon()) {
			//iを右手に装備中
			if (i.equals(getSelectedPC().getEqip().get(EqipSlot.右手))) {
				//両手持ちの場合、左手を外す
				if (ActionStorage.getInstance().両手持ち.equals(getSelectedPC().getEqip().get(EqipSlot.左手))) {
					getSelectedPC().unEqip(EqipSlot.左手);
				}
				//右手の装備iを外す
				getSelectedPC().unEqip(EqipSlot.右手);
			}
			//iを左手に装備中
			if (i.equals(getSelectedPC().getEqip().get(EqipSlot.左手))) {
				//両手持ちの場合
				if (ActionStorage.getInstance().両手持ち_弓.equals(getSelectedPC().getEqip().get(EqipSlot.右手))) {
					getSelectedPC().unEqip(EqipSlot.右手);
				}
				//左手の装備iを外す
				getSelectedPC().unEqip(EqipSlot.右手);
			}
		} else {
			//防具類。そのまま外す
			getSelectedPC().unEqip(i.getSlot());
		}
		getSelectedPC().getItemBag().drop(i);
		msg.setText(I18N.get(GameSystemI18NKeys.XはXを捨てた,
				GameSystem.getInstance().getPCbyID(getSelectedPC().getId()).getVisibleName(),
				i.getVisibleName()));
		msg.allText();
		getSelectedPC().updateAction();
		group.show(msg.getWindow());
		mainSelect = 0;
	}

	private void commitDisasse() {
		disasseConfirm.close();
		Item i = getSelectedItem();
		assert i.canDisasse() : "item is cant disassembly : " + i;
		assert i.canSale() : "item is cant disassembly : " + i;
		if (i.isWeapon()) {
			//iを右手に装備中
			if (i.equals(getSelectedPC().getEqip().get(EqipSlot.右手))) {
				//両手持ちの場合
				if (ActionStorage.getInstance().両手持ち.equals(getSelectedPC().getEqip().get(EqipSlot.左手))) {
					getSelectedPC().unEqip(EqipSlot.左手);
				}
				//右手の装備iを外す
				getSelectedPC().unEqip(EqipSlot.右手);
			}
			//iを左手に装備中
			if (i.equals(getSelectedPC().getEqip().get(EqipSlot.左手))) {
				//両手持ちの場合
				if (ActionStorage.getInstance().両手持ち_弓.equals(getSelectedPC().getEqip().get(EqipSlot.右手))) {
					getSelectedPC().unEqip(EqipSlot.右手);
				}
				//左手の装備iを外す
				getSelectedPC().unEqip(EqipSlot.右手);
			}
		} else {
			//防具類。そのまま外す
			getSelectedPC().unEqip(i.getSlot());
		}
		getSelectedPC().getItemBag().drop(i);
		StringBuilder sb = new StringBuilder();
		sb.append(Text.getLineSep());
		for (Map.Entry<Material, Integer> e : i.getEffectedMaterials().entrySet()) {
			sb.append(" ");
			sb.append(I18N.get(GameSystemI18NKeys.XをX個入手した, e.getKey().getVisibleName(), e.getValue() + ""));
			sb.append(Text.getLineSep());
			for (int j = 0; j < e.getValue(); j++) {
				GameSystem.getInstance().getMaterialBag().add(e.getKey());
			}
		}
		msg.setText(I18N.get(GameSystemI18NKeys.XはXを解体した,
				GameSystem.getInstance().getPCbyID(getSelectedPC().getId()).getVisibleName(),
				i.getVisibleName()) + sb.toString());
		msg.allText();
		getSelectedPC().updateAction();
		group.show(msg.getWindow());
		mainSelect = 0;
	}

	@Override
	public void update() {
		//メインウインドウの内容更新
		if (mode == Mode.ITEM_AND_USER_SELECT) {
			main.setText(Text.noI18N(getItemListText(getSelectedPC())));
			main.allText();
			main.setVisible(true);
			main.update();
		}
		msg.update();
	}

	//1つ前の画面に戻る
	public boolean close() {
		msg.setText(List.of());
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
