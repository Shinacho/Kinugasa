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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import static kinugasa.game.system.ParameterType.ITEM_LOST;
import static kinugasa.game.system.ParameterType.STATUS;
import static kinugasa.game.system.DamageCalcType.PERCENT_OF_MAX;
import static kinugasa.game.system.DamageCalcType.PERCENT_OF_NOW;
import static kinugasa.game.system.DamageCalcType.USE_DAMAGE_CALC;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.MessageWindowGroup;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/12/26_21:26:42<br>
 * @author Shinacho<br>
 */
public class MagicWindow extends BasicSprite {

	public MagicWindow(int x, int y, int w, int h) {
		super(x, y, w, h);
		main = new ScrollSelectableMessageWindow(x, y, w, h, 20, false);
		main.setLoop(true);
		x += 8;
		y += 8;
		w -= 8;
		h -= 8;
		choiceUse = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(""));
		choiceUse.setVisible(false);
		tgtSelect = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(""));
		tgtSelect.setVisible(false);
		msg = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(""));
		msg.setVisible(false);
		group = new MessageWindowGroup(choiceUse, tgtSelect, msg);
		updateText();
	}

	public enum Mode {
		MAGIC_AND_USER_SELECT,
		CHOICE_USE,
		WAIT_MSG_CLOSE_TO_MUS,
		WAIT_MSG_CLOSE_TO_CU,
		TARGET_SELECT,
	}
	private Mode mode = Mode.MAGIC_AND_USER_SELECT;
	private int pcIdx = 0;
	private ScrollSelectableMessageWindow main;
	private MessageWindow choiceUse, tgtSelect, msg;
	private MessageWindowGroup group;

	private static final int USE = 0;
	private static final int CHECK = 1;

	public void nextSelect() {
		switch (mode) {
			case MAGIC_AND_USER_SELECT:
				main.nextSelect();
				break;
			case CHOICE_USE:
				choiceUse.nextSelect();
				break;
			default:
				break;
		}
		update();
	}

	public void prevSelect() {
		switch (mode) {
			case MAGIC_AND_USER_SELECT:
				main.prevSelect();
				break;
			case CHOICE_USE:
				choiceUse.prevSelect();
				break;
			default:
				break;
		}
		update();
	}

	public void nextPC() {
		switch (mode) {
			case MAGIC_AND_USER_SELECT:
				pcIdx++;
				if (pcIdx >= GameSystem.getInstance().getParty().size()) {
					pcIdx = 0;
				}
				main.reset();
				updateText();
				break;
			case TARGET_SELECT:
				tgtSelect.nextSelect();
				break;
			case CHOICE_USE:
			case WAIT_MSG_CLOSE_TO_CU:
			case WAIT_MSG_CLOSE_TO_MUS:
				//処理なし
				break;
		}
	}

	public void prevPC() {
		switch (mode) {
			case MAGIC_AND_USER_SELECT:
				pcIdx--;
				if (pcIdx < 0) {
					pcIdx = GameSystem.getInstance().getParty().size() - 1;
				}
				main.reset();
				updateText();
				break;
			case TARGET_SELECT:
				tgtSelect.prevSelect();
				break;
			case CHOICE_USE:
			case WAIT_MSG_CLOSE_TO_CU:
			case WAIT_MSG_CLOSE_TO_MUS:
				//処理なし
				break;
		}
	}

	public void select() {
		if (getSelectedPC().getActions(ActionType.MAGIC).isEmpty()) {
			group.closeAll();
			mode = Mode.MAGIC_AND_USER_SELECT;
			return;
		}
		Action a = getSelectedAction();
		switch (mode) {
			case MAGIC_AND_USER_SELECT:
				List<Text> options = new ArrayList<>();
				options.add(new Text(I18N.get(GameSystemI18NKeys.使う)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.調べる)));
				Choice c = new Choice(options, "IMAGIC_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xを, a.getVisibleName()));
				choiceUse.setText(c);
				choiceUse.allText();
				choiceUse.setSelect(0);
				group.show(choiceUse);
				mode = Mode.CHOICE_USE;
				break;
			case CHOICE_USE:
				switch (choiceUse.getSelect()) {
					case USE:
						//フィールドでは使えない場合
						if (!a.isFieldUse()) {
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
							sb.append(Text.getLineSep());
							sb.append(I18N.get(GameSystemI18NKeys.しかしこの魔法はフィールドでは使えない));
							msg.setText(sb.toString());
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//SELFのみの場合即時実行
						if (a.fieldEventIsOnly(TargetType.SELF)) {
							//代償が支払えるかのチェック
							Map<StatusKey, Integer> selfDamage = a.selfFieldDirectDamage();
							StatusValueSet vs = getSelectedPC().simulateDamage(selfDamage);
							if (vs.hasMinus()) {
								StringBuilder sb = new StringBuilder();
								sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
								sb.append(Text.getLineSep());
								sb.append(I18N.get(GameSystemI18NKeys.しかしXが足りない, vs.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList()).toString()));
								msg.setText(sb.toString());
								msg.allText();
								group.show(msg);
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								return;
							}
							//即時実行してサブに効果を出力
							Status tgt = getSelectedPC();
							tgt.setDamageCalcPoint();
							ActionResult r = a.exec(ActionTarget.instantTarget(getSelectedPC(), a, tgt).setInField(true).setSelfTarget(true));
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
							sb.append(Text.getLineSep());
							if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
								//成功
								//効果測定
								Map<StatusKey, Float> map = tgt.calcDamage();
								for (Map.Entry<StatusKey, Float> e : map.entrySet()) {
									if (e.getValue() < 0f) {
										sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()));
										sb.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getDesc()));
										sb.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
										sb.append(Text.getLineSep());
									} else if (e.getValue() > 0f) {
										sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()));
										sb.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getDesc()));
										sb.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""));
										sb.append(Text.getLineSep());
									} else {
										//==0
										sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
										sb.append(Text.getLineSep());
									}
								}
							} else {
								//失敗
								sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
							}
							msg.setText(sb.toString());
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_MUS;
							return;
						}
						//ターゲットタイプランダムの場合は即時実行
						if (a.getFieldEvent().stream().anyMatch(p -> p.getTargetType() == TargetType.RANDOM)) {
							//代償が支払えるかのチェック
							Map<StatusKey, Integer> selfDamage = a.selfFieldDirectDamage();
							StatusValueSet vs = getSelectedPC().simulateDamage(selfDamage);
							if (vs.hasMinus()) {
								StringBuilder sb = new StringBuilder();
								sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
								sb.append(Text.getLineSep());
								sb.append(I18N.get(GameSystemI18NKeys.しかしXが足りない, vs.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList()).toString()));
								msg.setText(sb.toString());
								msg.allText();
								group.show(msg);
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								return;
							}
							//即時実行してサブに効果を出力
							//ターゲットを決定

							Status tgt = Random.randomChoice(GameSystem.getInstance().getPartyStatus());
							tgt.setDamageCalcPoint();
							ActionResult r = a.exec(ActionTarget.instantTarget(getSelectedPC(), a, tgt).setInField(true));
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
							sb.append(Text.getLineSep());
							if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
								//成功
								//効果測定
								Map<StatusKey, Float> map = tgt.calcDamage();
								for (Map.Entry<StatusKey, Float> e : map.entrySet()) {
									if (e.getValue() < 0f) {
										sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()));
										sb.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getDesc()));
										sb.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
										sb.append(Text.getLineSep());
									} else if (e.getValue() > 0f) {
										sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()));
										sb.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getDesc()));
										sb.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""));
										sb.append(Text.getLineSep());
									} else {
										//==0
										sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
										sb.append(Text.getLineSep());
									}
								}
							} else {
								//失敗
								sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
							}
							msg.setText(sb.toString());
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_MUS;
							return;
						}
						//チームが入っている場合即時実行
						if (a.getFieldEvent().stream().anyMatch(p -> p.getTargetType() == TargetType.TEAM)) {
							//代償が支払えるかのチェック
							Map<StatusKey, Integer> selfDamage = a.selfFieldDirectDamage();
							StatusValueSet vs = getSelectedPC().simulateDamage(selfDamage);
							if (vs.hasMinus()) {
								StringBuilder sb = new StringBuilder();
								sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
								sb.append(Text.getLineSep());
								sb.append(I18N.get(GameSystemI18NKeys.しかしXが足りない, vs.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList()).toString()));
								msg.setText(sb.toString());
								msg.allText();
								group.show(msg);
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								return;
							}
							//即時実行してサブに効果を出力
							List<Status> tgt = GameSystem.getInstance().getPartyStatus();
							tgt.forEach(p -> p.setDamageCalcPoint());
							ActionResult r = a.exec(ActionTarget.instantTarget(getSelectedPC(), a, tgt).setInField(true));
							StringBuilder sb = new StringBuilder();
							for (Status s : tgt) {
								sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
								sb.append(Text.getLineSep());
								if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
									//成功
									//効果測定
									Map<StatusKey, Float> map = s.calcDamage();
									for (Map.Entry<StatusKey, Float> e : map.entrySet()) {
										if (e.getValue() < 0f) {
											sb.append(I18N.get(GameSystemI18NKeys.Xの, I18N.get(GameSystemI18NKeys.全員)));
											sb.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getDesc()));
											sb.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
											sb.append(Text.getLineSep());
										} else if (e.getValue() > 0f) {
											sb.append(I18N.get(GameSystemI18NKeys.Xの, I18N.get(GameSystemI18NKeys.全員)));
											sb.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getDesc()));
											sb.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""));
											sb.append(Text.getLineSep());
										} else {
											//==0
											sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
											sb.append(Text.getLineSep());
										}
									}
								} else {
									//失敗
									sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
								}
							}
							msg.setText(sb.toString());
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_MUS;
							return;
						}
						//代償が支払えるかのチェック
						Map<StatusKey, Integer> selfDamage = a.selfFieldDirectDamage();
						StatusValueSet vs = getSelectedPC().simulateDamage(selfDamage);
						if (vs.hasMinus()) {
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
							sb.append(Text.getLineSep());
							sb.append(I18N.get(GameSystemI18NKeys.しかしXが足りない, vs.stream().filter(p -> p.getValue() < 0).map(p -> StatusKeyStorage.getInstance().get(p.getName()).getDesc()).collect(Collectors.toList()).toString()));
							msg.setText(sb.toString());
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//その他の場合はターゲット選択へ
						List<Text> option1 = new ArrayList<>();
						option1.addAll(GameSystem.getInstance().getPartyStatus().stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(option1, "MAGIC_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xの, a.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.Xを誰に使う, getSelectedAction().getName())));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case CHECK:
						StringBuilder sb = new StringBuilder();
						sb.append(a.getVisibleName()).append(Text.getLineSep());
						//DESC
						String desc = a.getDesc();
						if (desc.contains(Text.getLineSep())) {
							String[] sv = desc.split(Text.getLineSep());
							for (String v : sv) {
								sb.append(" ").append(v);
								sb.append(Text.getLineSep());
							}
						} else {
							sb.append(" ").append(a.getDesc());
							sb.append(Text.getLineSep());
						}
						//イベント詳細
						sb.append("--").append(I18N.get(GameSystemI18NKeys.戦闘効果)).append(Text.getLineSep());
						if (a.isBattleUse()) {
							//SPELL_TIME
							sb.append("  ");
							sb.append(I18N.get(GameSystemI18NKeys.詠唱時間)).append(":").append(a.getSpellTime()).append(I18N.get(GameSystemI18NKeys.ターン));
							sb.append(Text.getLineSep());
							//AREA
							sb.append("  ");
							sb.append(I18N.get(GameSystemI18NKeys.範囲)).append(":").append(a.getArea());
							sb.append(Text.getLineSep());
						}
						if (a.isBattleUse()) {
							for (ActionEvent e : a.getBattleEvent()) {
								sb.append("  ");
								if (e instanceof CustomActionEvent) {
									sb.append(I18N.get(GameSystemI18NKeys.独自効果));
									continue;
								}
								switch (e.getParameterType()) {
									case ADD_CONDITION:
										sb.append(I18N.get(GameSystemI18NKeys.状態異常Xを追加する, ConditionStorage.getInstance().get(e.getTgtName()).getKey().getDesc()));
										break;
									case ATTR_IN:
										sb.append(I18N.get(GameSystemI18NKeys.Xの有効度を変更する, AttributeKeyStorage.getInstance().get(e.getTgtName()).getDesc()));
										sb.append(",");
										//dct
										switch (e.getDamageCalcType()) {
											case DIRECT:
												sb.append(I18N.get(GameSystemI18NKeys.直接作用));
												break;
											case PERCENT_OF_MAX:
												sb.append(I18N.get(GameSystemI18NKeys.最大値の割合));
												break;
											case PERCENT_OF_NOW:
												sb.append(I18N.get(GameSystemI18NKeys.現在値の割合));
												break;
											case USE_DAMAGE_CALC:
												sb.append(I18N.get(GameSystemI18NKeys.標準ダメージ計算));
												break;
										}
										break;
									case ITEM_ADD:
										sb.append(I18N.get(GameSystemI18NKeys.アイテムXを追加する, e.getTgtName()));
										break;
									case ITEM_LOST:
										sb.append(I18N.get(GameSystemI18NKeys.アイテムXを破棄する, e.getTgtName()));
										break;
									case NONE:
										break;
									case REMOVE_CONDITION:
										sb.append(I18N.get(GameSystemI18NKeys.状態異常Xを回復する, e.getTgtName()));
										break;
									case STATUS:
										if (e.getTargetType() == TargetType.SELF) {
											sb.append(I18N.get(GameSystemI18NKeys.術者));
										} else {
											sb.append(I18N.get(GameSystemI18NKeys.対象));
										}
										sb.append(I18N.get(GameSystemI18NKeys.ダメージ)).append(":");
										sb.append(StatusKeyStorage.getInstance().get(e.getTgtName()).getDesc());
										sb.append(",");
										//dct
										switch (e.getDamageCalcType()) {
											case DIRECT:
												sb.append(I18N.get(GameSystemI18NKeys.直接作用));
												break;
											case PERCENT_OF_MAX:
												sb.append(I18N.get(GameSystemI18NKeys.最大値の割合));
												break;
											case PERCENT_OF_NOW:
												sb.append(I18N.get(GameSystemI18NKeys.現在値の割合));
												break;
											case USE_DAMAGE_CALC:
												sb.append(I18N.get(GameSystemI18NKeys.標準ダメージ計算));
												break;
										}
										break;
								}
								sb.append(",");
								if (e.getParameterType() == ParameterType.STATUS) {
									sb.append(I18N.get(GameSystemI18NKeys.基礎威力)).append(":").append(Math.abs((int) e.getValue()));
								}
								sb.append(",");
								sb.append(I18N.get(GameSystemI18NKeys.確率)).append(":").append((int) (e.getP() * 100)).append("%");
								if (e.getAttr() != null) {
									sb.append(",");
									sb.append(I18N.get(GameSystemI18NKeys.属性)).append(":").append(e.getAttr().getDesc()).append(Text.getLineSep());
								}
							}
						} else {
							sb.append("  ").append(I18N.get(GameSystemI18NKeys.この魔法は戦闘中使えない)).append(Text.getLineSep());
						}
						sb.append("--").append(I18N.get(GameSystemI18NKeys.フィールド効果)).append(Text.getLineSep());
						if (a.isFieldUse()) {
							for (ActionEvent e : a.getFieldEvent()) {
								sb.append("  ");
								if (e instanceof CustomActionEvent) {
									sb.append(I18N.get(GameSystemI18NKeys.独自効果));
									continue;
								}
								switch (e.getParameterType()) {
									case ADD_CONDITION:
										sb.append(I18N.get(GameSystemI18NKeys.状態異常Xを追加する, e.getTgtName()));
										break;
									case ATTR_IN:
										sb.append(I18N.get(GameSystemI18NKeys.Xの有効度を変更する, AttributeKeyStorage.getInstance().get(e.getTgtName()).getDesc()));
										sb.append(",");
										//dct
										switch (e.getDamageCalcType()) {
											case DIRECT:
												sb.append(I18N.get(GameSystemI18NKeys.直接作用));
												break;
											case PERCENT_OF_MAX:
												sb.append(I18N.get(GameSystemI18NKeys.最大値の割合));
												break;
											case PERCENT_OF_NOW:
												sb.append(I18N.get(GameSystemI18NKeys.現在値の割合));
												break;
											case USE_DAMAGE_CALC:
												sb.append(I18N.get(GameSystemI18NKeys.標準ダメージ計算));
												break;
										}
										break;
									case ITEM_ADD:
										sb.append(I18N.get(GameSystemI18NKeys.アイテムXを追加する, e.getTgtName()));
										break;
									case ITEM_LOST:
										sb.append(I18N.get(GameSystemI18NKeys.アイテムXを破棄する, e.getTgtName()));
										break;
									case NONE:
										break;
									case REMOVE_CONDITION:
										sb.append(I18N.get(GameSystemI18NKeys.状態異常Xを回復する, e.getTgtName()));
										break;
									case STATUS:
										sb.append(I18N.get(GameSystemI18NKeys.ダメージ)).append(":");
										sb.append(StatusKeyStorage.getInstance().get(e.getTgtName()).getDesc());
										sb.append(",");
										//dct
										switch (e.getDamageCalcType()) {
											case DIRECT:
												sb.append(I18N.get(GameSystemI18NKeys.直接作用));
												break;
											case PERCENT_OF_MAX:
												sb.append(I18N.get(GameSystemI18NKeys.最大値の割合));
												break;
											case PERCENT_OF_NOW:
												sb.append(I18N.get(GameSystemI18NKeys.現在値の割合));
												break;
											case USE_DAMAGE_CALC:
												sb.append(I18N.get(GameSystemI18NKeys.標準ダメージ計算));
												break;
										}
										break;
								}
								sb.append(",");
								if (e.getParameterType() == ParameterType.STATUS) {
									sb.append(I18N.get(GameSystemI18NKeys.基礎威力)).append(":").append(Math.abs((int) e.getValue()));
								}
								sb.append(",");
								sb.append(I18N.get(GameSystemI18NKeys.確率)).append(":").append((int) (e.getP() * 100)).append("%");
								if (e.getAttr() != null) {
									sb.append(",");
									sb.append(I18N.get(GameSystemI18NKeys.属性)).append(":").append(e.getAttr().getDesc()).append(Text.getLineSep());
								}
							}
						} else {
							sb.append("  ").append(I18N.get(GameSystemI18NKeys.この魔法はフィールドでは使えない)).append(Text.getLineSep());
						}
						//ターゲティング情報
						sb.append("--").append(GameSystemI18NKeys.戦闘時ターゲット情報).append(Text.getLineSep());
						String s = a.getTargetOption().getSelectType() == TargetOption.SelectType.IN_AREA
								? I18N.get(GameSystemI18NKeys.全体)
								: I18N.get(GameSystemI18NKeys.単体);
						sb.append("  ").append(I18N.get(GameSystemI18NKeys.効果対象)).append(":").append(s).append(Text.getLineSep());
						s = a.getTargetOption().getIff() == TargetOption.IFF.ON
								? I18N.get(GameSystemI18NKeys.有効)
								: I18N.get(GameSystemI18NKeys.無効);
						sb.append("  ").append(I18N.get(GameSystemI18NKeys.敵味方識別)).append(":").append(s).append(Text.getLineSep());
						s = a.getTargetOption().getSwitchTeam() == TargetOption.SwitchTeam.OK
								? I18N.get(GameSystemI18NKeys.有効)
								: I18N.get(GameSystemI18NKeys.無効);
						sb.append("  ").append(I18N.get(GameSystemI18NKeys.敵味方切替)).append(":").append(s).append(Text.getLineSep());
						s = a.getTargetOption().getTargeting() == TargetOption.Targeting.ENABLE
								? I18N.get(GameSystemI18NKeys.有効)
								: I18N.get(GameSystemI18NKeys.無効);
						sb.append("  ").append(I18N.get(GameSystemI18NKeys.標的選択)).append(":").append(s).append(Text.getLineSep());

						msg.setText(sb.toString());
						msg.allText();
						group.show(msg);
				}
				break;
			case TARGET_SELECT:
				commitUse();
				group.show(msg);
				mode = Mode.WAIT_MSG_CLOSE_TO_CU;
				break;
			case WAIT_MSG_CLOSE_TO_CU:
				group.closeAll();
				group.show(choiceUse);
				mode = Mode.CHOICE_USE;
				break;
			case WAIT_MSG_CLOSE_TO_MUS:
				group.closeAll();
				mode = Mode.MAGIC_AND_USER_SELECT;
				break;
		}

	}

	private void commitUse() {
		Action a = getSelectedAction();
		Status tgt = GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect());

		//使用者のMP計算のためDCP設定
		getSelectedPC().setDamageCalcPoint();
		//使用者とターゲットが違う場合はターゲットもDCP設定
		if (!getSelectedPC().equals(tgt)) {
			tgt.setDamageCalcPoint();
		}
		ActionResult r = a.exec(ActionTarget.instantTarget(getSelectedPC(), a, tgt).setInField(true));
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getSelectedPC().getName(), a.getVisibleName()));
		sb.append(Text.getLineSep());
		if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
			//成功
			//ターゲットへの効果測定
			Map<StatusKey, Float> map = getSelectedPC().calcDamage();
			for (Map.Entry<StatusKey, Float> e : map.entrySet()) {
				if (e.getValue() < 0f) {
					sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()));
					sb.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getDesc()));
					sb.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
					sb.append(Text.getLineSep());
				} else if (e.getValue() > 0f) {
					sb.append(I18N.get(GameSystemI18NKeys.Xの, tgt.getName()));
					sb.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getDesc()));
					sb.append(I18N.get(GameSystemI18NKeys.Xのダメージ, Math.abs(e.getValue()) + ""));
					sb.append(Text.getLineSep());
				} else {
					//==0
					sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
					sb.append(Text.getLineSep());
				}
			}
			//SELFへのダメージ
			//TODO:

			if (map.isEmpty()) {
				sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
				sb.append(Text.getLineSep());
			}
		} else {
			//失敗
			sb.append(I18N.get(GameSystemI18NKeys.しかし効果がなかった));
			sb.append(Text.getLineSep());
		}

		msg.setText(sb.toString());
		msg.allText();
		group.show(msg);
		updateText();
	}

	public Action getSelectedAction() {
		return GameSystem.getInstance().getPartyStatus().get(pcIdx).getActions(ActionType.MAGIC).get(main.getSelectedIdx() - 1);
	}

	public Status getSelectedPC() {
		return GameSystem.getInstance().getPartyStatus().get(pcIdx);
	}

	public Mode getCurrentMode() {
		return mode;
	}

	private void updateText() {
		Text line1 = new Text("<---" + I18N.get(GameSystemI18NKeys.Xの, getSelectedPC().getName()) + I18N.get(GameSystemI18NKeys.魔術) + "--->");

		List<Action> list = getSelectedPC().getActions(ActionType.MAGIC);
		if (list.isEmpty()) {
			Text line2 = new Text(I18N.get(GameSystemI18NKeys.使える魔術はない));
			main.setText(List.of(line1, line2));
			return;
		}
		List<Text> l = new ArrayList<>();
		l.add(line1);
		l.addAll(list.stream().map(p -> new Text(p.getVisibleName())).collect(Collectors.toList()));
		main.setText(l);
	}

	@Override
	public void update() {
		main.update();
	}

	//1つ前の画面に戻る
	public boolean close() {
		//IUS表示中の場合は戻るは全消し
		if (group.getWindows().stream().allMatch(p -> !p.isVisible())) {
			mode = Mode.MAGIC_AND_USER_SELECT;
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
		if (choiceUse.isVisible()) {
			mode = Mode.MAGIC_AND_USER_SELECT;
			group.closeAll();
			return false;
		}
		group.closeAll();
		mode = Mode.MAGIC_AND_USER_SELECT;
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
		msg.draw(g);
	}

}
