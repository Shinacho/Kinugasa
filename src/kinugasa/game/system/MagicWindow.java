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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/12/26_21:26:42<br>
 * @author Dra211<br>
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
		CmdAction a = getSelectedAction();
		switch (mode) {
			case MAGIC_AND_USER_SELECT:
				List<Text> options = new ArrayList<>();
				options.add(new Text(I18N.translate("USE")));
				options.add(new Text(I18N.translate("CHECK")));
				Choice c = new Choice(options, "IMAGIC_WINDOW_SUB", a.getName() + I18N.translate("OF"));
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
							sb.append(getSelectedPC().getName());
							sb.append(I18N.translate("IS"));
							sb.append(a.getName());
							sb.append(I18N.translate("USE_ITEM"));
							sb.append(Text.getLineSep());
							sb.append(I18N.translate("BUT"));
							sb.append(I18N.translate("CANT_USE_FIELD"));
							msg.setText(sb.toString());
							msg.allText();
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//SELFのみの場合即時実行
						if (a.fieldEventIsOnly(TargetType.SELF)) {
							//代償が支払えるかのチェック
							Map<StatusKey, Integer> selfDamage = a.selfBattleDirectDamage();
							StatusValueSet vs = getSelectedPC().simulateDamage(selfDamage);
							if (vs.hasZero()) {
								StringBuilder sb = new StringBuilder();
								sb.append(getSelectedPC().getName()).append(I18N.translate("IS")).append(a.getName()).append(I18N.translate("USE_MAGIC"));
								sb.append(Text.getLineSep());
								sb.append(I18N.translate("BUT"));
								sb.append(vs.stream().map(p -> p.getName()).collect(Collectors.toList())).append(I18N.translate("SHORTAGE"));
								msg.setText(sb.toString());
								msg.allText();
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								return;
							}
							//即時実行してサブに効果を出力
							Status tgt = getSelectedPC();
							tgt.setDamageCalcPoint();
							ActionResult r = a.exec(ActionTarget.instantTarget(getSelectedPC(), a, tgt).setInField(true).setSelfTarget(true));
							StringBuilder sb = new StringBuilder();
							sb.append(tgt.getName()).append(I18N.translate("IS")).append(a.getName()).append(I18N.translate("USE_MAGIC"));
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
							mode = Mode.WAIT_MSG_CLOSE_TO_MUS;
							return;
						}
						//ターゲットタイプランダムの場合は即時実行
						if (a.getFieldEvent().stream().anyMatch(p -> p.getTargetType() == TargetType.RANDOM_ONE_PARTY)) {
							//代償が支払えるかのチェック
							Map<StatusKey, Integer> selfDamage = a.selfBattleDirectDamage();
							StatusValueSet vs = getSelectedPC().simulateDamage(selfDamage);
							if (vs.hasZero()) {
								StringBuilder sb = new StringBuilder();
								sb.append(getSelectedPC().getName()).append(I18N.translate("IS")).append(a.getName()).append(I18N.translate("USE_MAGIC"));
								sb.append(Text.getLineSep());
								sb.append(I18N.translate("BUT"));
								sb.append(vs.stream().map(p -> p.getName()).collect(Collectors.toList())).append(I18N.translate("SHORTAGE"));
								msg.setText(sb.toString());
								msg.allText();
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								return;
							}
							//即時実行してサブに効果を出力
							//ターゲットを決定

							Status tgt = Random.random(GameSystem.getInstance().getPartyStatus());
							tgt.setDamageCalcPoint();
							ActionResult r = a.exec(ActionTarget.instantTarget(getSelectedPC(), a, tgt).setInField(true));
							StringBuilder sb = new StringBuilder();
							sb.append(tgt.getName()).append(I18N.translate("IS")).append(a.getName()).append(I18N.translate("USE_MAGIC"));
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
							mode = Mode.WAIT_MSG_CLOSE_TO_MUS;
							return;
						}
						//チームが入っている場合即時実行
						if (a.getFieldEvent().stream().anyMatch(p -> p.getTargetType() == TargetType.TEAM_PARTY)) {
							//代償が支払えるかのチェック
							Map<StatusKey, Integer> selfDamage = a.selfBattleDirectDamage();
							StatusValueSet vs = getSelectedPC().simulateDamage(selfDamage);
							if (vs.hasZero()) {
								StringBuilder sb = new StringBuilder();
								sb.append(getSelectedPC().getName()).append(I18N.translate("IS")).append(a.getName()).append(I18N.translate("USE_MAGIC"));
								sb.append(Text.getLineSep());
								sb.append(I18N.translate("BUT"));
								sb.append(vs.stream().map(p -> p.getName()).collect(Collectors.toList())).append(I18N.translate("SHORTAGE"));
								msg.setText(sb.toString());
								msg.allText();
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								return;
							}
							//即時実行してサブに効果を出力
							List<Status> tgt = GameSystem.getInstance().getPartyStatus();
							tgt.forEach(p -> p.setDamageCalcPoint());
							ActionResult r = a.exec(ActionTarget.instantTarget(getSelectedPC(), a, tgt).setInField(true));
							StringBuilder sb = new StringBuilder();
							for (Status s : tgt) {
								sb.append(s.getName()).append(I18N.translate("IS")).append(a.getName()).append(I18N.translate("USE_MAGIC"));
								sb.append(Text.getLineSep());
								if (r.getResultType().stream().flatMap(p -> p.stream()).allMatch(p -> p == ActionResultType.SUCCESS)) {
									//成功
									//効果測定
									Map<StatusKey, Integer> map = s.calcDamage();
									for (Map.Entry<StatusKey, Integer> e : map.entrySet()) {
										if (e.getValue() > 0) {
											sb.append(s.getName()).append(I18N.translate("IS")).append(e.getValue()).append(I18N.translate("HEALDAMAGE"));
										} else {
											sb.append(s.getName()).append(I18N.translate("IS")).append(e.getValue()).append(I18N.translate("DAMAGE"));
										}
									}
								} else {
									//失敗
									sb.append(I18N.translate("BUT"));
									sb.append(I18N.translate("NO_EFFECT"));
								}
							}
							msg.setText(sb.toString());
							group.show(msg);
							mode = Mode.WAIT_MSG_CLOSE_TO_MUS;
							return;
						}
						//代償が支払えるかのチェック
						Map<StatusKey, Integer> selfDamage = a.selfBattleDirectDamage();
						StatusValueSet vs = getSelectedPC().simulateDamage(selfDamage);
						if (vs.hasZero()) {
							StringBuilder sb = new StringBuilder();
							sb.append(getSelectedPC().getName()).append(I18N.translate("IS")).append(a.getName()).append(I18N.translate("USE_MAGIC"));
							sb.append(Text.getLineSep());
							sb.append(I18N.translate("BUT"));
							sb.append(vs.stream().map(p -> p.getName()).collect(Collectors.toList())).append(I18N.translate("SHORTAGE"));
							msg.setText(sb.toString());
							msg.allText();
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//その他の場合はターゲット選択へ
						List<Text> option1 = new ArrayList<>();
						option1.addAll(GameSystem.getInstance().getPartyStatus().stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(option1, "MAGIC_WINDOW_SUB", a.getName() + I18N.translate("WHO_DO_USE")));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case CHECK:
						StringBuilder sb = new StringBuilder();
						sb.append(a.getName()).append(Text.getLineSep());
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
						sb.append("--").append(I18N.translate("BATTLE_ACTION")).append(Text.getLineSep());
						//SPELL_TIME
						sb.append("  ");
						sb.append(I18N.translate("SPELLTIME")).append(":").append(a.getSpellTime()).append(I18N.translate("TURN"));
						sb.append(Text.getLineSep());
						//AREA
						sb.append("  ");
						sb.append(I18N.translate("AREA")).append(":").append(a.getArea());
						sb.append(Text.getLineSep());
						if (a.isBattleUse()) {
							for (ActionEvent e : a.getBattleEvent()) {
								sb.append("  ");
								switch (e.getParameterType()) {
									case ADD_CONDITION:
										sb.append(I18N.translate("ADD_CONDITION").replaceAll("c", ConditionValueStorage.getInstance().get(e.getTgtName()).getKey().getDesc()));
										break;
									case ATTR_IN:
										sb.append(I18N.translate("CHANGE_ATTR"));
										sb.append(AttributeKeyStorage.getInstance().get(e.getTgtName()));
										sb.append(",");
										//dct
										switch (e.getDamageCalcType()) {
											case DIRECT:
												sb.append(I18N.translate("DCT_DIRECT"));
												break;
											case PERCENT_OF_MAX:
												sb.append(I18N.translate("DCT_PERCENT_OF_MAX"));
												break;
											case PERCENT_OF_NOW:
												sb.append(I18N.translate("DCT_PERCENT_OF_NOW"));
												break;
											case USE_DAMAGE_CALC:
												sb.append(I18N.translate("DCT_USE_DAMAGE_CALC"));
												break;
										}
										break;
									case ITEM_ADD:
										sb.append(I18N.translate("ITEM_ADD").replaceAll("i", e.getTgtName()));
										break;
									case ITEM_LOST:
										sb.append(I18N.translate("ITEM_LOST").replaceAll("i", e.getTgtName()));
										break;
									case NONE:
										break;
									case REMOVE_CONDITION:
										sb.append(I18N.translate("REMOVE_CONDITION").replaceAll("c", e.getTgtName()));
										break;
									case STATUS:
										sb.append(I18N.translate("DAMAGE"));
										sb.append(StatusKeyStorage.getInstance().get(e.getTgtName()).getName());
										sb.append(",");
										//dct
										switch (e.getDamageCalcType()) {
											case DIRECT:
												sb.append(I18N.translate("DCT_DIRECT"));
												break;
											case PERCENT_OF_MAX:
												sb.append(I18N.translate("DCT_PERCENT_OF_MAX"));
												break;
											case PERCENT_OF_NOW:
												sb.append(I18N.translate("DCT_PERCENT_OF_NOW"));
												break;
											case USE_DAMAGE_CALC:
												sb.append(I18N.translate("DCT_USE_DAMAGE_CALC"));
												break;
										}
										break;
								}
								sb.append(",");
								sb.append(I18N.translate("ACTION_EFFECT")).append(":").append(Math.abs((int) e.getValue()));
								sb.append(",");
								sb.append(I18N.translate("P")).append(":").append((int) (e.getP() * 100)).append("%");
								sb.append(Text.getLineSep());
							}
						} else {
							sb.append("  ").append(I18N.translate("CANT_USE_BATTLE")).append(Text.getLineSep());
						}
						sb.append("--").append(I18N.translate("FIELD_ACTION")).append(Text.getLineSep());
						if (a.isFieldUse()) {
							for (ActionEvent e : a.getFieldEvent()) {
								sb.append("  ");
								switch (e.getParameterType()) {
									case ADD_CONDITION:
										sb.append(I18N.translate("ADD_CONDITION").replaceAll("c", e.getTgtName()));
										break;
									case ATTR_IN:
										sb.append(I18N.translate("CHANGE_ATTR"));
										sb.append(AttributeKeyStorage.getInstance().get(e.getTgtName()));
										sb.append(",");
										//dct
										switch (e.getDamageCalcType()) {
											case DIRECT:
												sb.append(I18N.translate("DCT_DIRECT"));
												break;
											case PERCENT_OF_MAX:
												sb.append(I18N.translate("DCT_PERCENT_OF_MAX"));
												break;
											case PERCENT_OF_NOW:
												sb.append(I18N.translate("DCT_PERCENT_OF_NOW"));
												break;
											case USE_DAMAGE_CALC:
												sb.append(I18N.translate("DCT_USE_DAMAGE_CALC"));
												break;
										}
										break;
									case ITEM_ADD:
										sb.append(I18N.translate("ITEM_ADD").replaceAll("i", e.getTgtName()));
										break;
									case ITEM_LOST:
										sb.append(I18N.translate("ITEM_LOST").replaceAll("i", e.getTgtName()));
										break;
									case NONE:
										break;
									case REMOVE_CONDITION:
										sb.append(I18N.translate("REMOVE_CONDITION").replaceAll("c", e.getTgtName()));
										break;
									case STATUS:
										sb.append(I18N.translate("DAMAGE"));
										sb.append(StatusKeyStorage.getInstance().get(e.getTgtName()).getName());
										sb.append(",");
										//dct
										switch (e.getDamageCalcType()) {
											case DIRECT:
												sb.append(I18N.translate("DCT_DIRECT"));
												break;
											case PERCENT_OF_MAX:
												sb.append(I18N.translate("DCT_PERCENT_OF_MAX"));
												break;
											case PERCENT_OF_NOW:
												sb.append(I18N.translate("DCT_PERCENT_OF_NOW"));
												break;
											case USE_DAMAGE_CALC:
												sb.append(I18N.translate("DCT_USE_DAMAGE_CALC"));
												break;
										}
										break;
								}
								sb.append(",");
								sb.append(I18N.translate("ACTION_EFFECT")).append(":").append(Math.abs((int) e.getValue()));
								sb.append(",");
								sb.append(I18N.translate("P")).append(":").append((int) (e.getP() * 100)).append("%");
								sb.append(Text.getLineSep());
							}
						} else {
							sb.append("  ").append(I18N.translate("CANT_USE_FIELD")).append(Text.getLineSep());
						}
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
				mode = Mode.CHOICE_USE;
				break;
			case WAIT_MSG_CLOSE_TO_MUS:
				group.closeAll();
				mode = Mode.MAGIC_AND_USER_SELECT;
				break;
		}

	}

	private void commitUse() {
		CmdAction a = getSelectedAction();
		Status tgt = GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect());
		tgt.setDamageCalcPoint();
		ActionResult r = a.exec(ActionTarget.instantTarget(getSelectedPC(), a, tgt).setInField(true));
		StringBuilder sb = new StringBuilder();
		sb.append(tgt.getName()).append(I18N.translate("IS")).append(a.getName()).append(I18N.translate("USE_MAGIC"));
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
//			//DROP_ITEMイベントの判定
//			for (ActionEvent e : i.getFieldEvent()) {
//				if (e.getParameterType() == ParameterType.ITEM_LOST) {
//					if (e.getP() >= 1f || Random.percent(e.getP())) {
//						tgt.getItemBag().drop(i);
//						sb.append(i.getName()).append(I18N.translate("ITEM_DROP"));
//						sb.append(Text.getLineSep());
//					}
//				}
//			}
		} else {
			//失敗
			sb.append(I18N.translate("BUT"));
			sb.append(I18N.translate("NO_EFFECT"));
			sb.append(Text.getLineSep());
		}

		msg.setText(sb.toString());
		msg.allText();
		group.show(msg);
		updateText();
	}

	public CmdAction getSelectedAction() {
		return GameSystem.getInstance().getPartyStatus().get(pcIdx).getActions(ActionType.MAGIC).get(main.getSelectedIdx() - 1);
	}

	public Status getSelectedPC() {
		return GameSystem.getInstance().getPartyStatus().get(pcIdx);
	}

	public Mode getCurrentMode() {
		return mode;
	}

	private void updateText() {
		Text line1 = new Text("<---" + getSelectedPC().getName() + I18N.translate("S") + I18N.translate("MAGIC") + "--->");

		List<CmdAction> list = getSelectedPC().getActions(ActionType.MAGIC);
		if (list.isEmpty()) {
			Text line2 = new Text(I18N.translate("NOTHING_MAGIC"));
			main.setText(List.of(line1, line2));
			return;
		}
		List<Text> l = new ArrayList<>();
		l.add(line1);
		l.addAll(list.stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
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
