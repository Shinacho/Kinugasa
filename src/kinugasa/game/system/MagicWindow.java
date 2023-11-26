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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.MessageWindowGroup;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/26_21:26:42<br>
 * @author Shinacho<br>
 */
public class MagicWindow extends BasicSprite {

	public MagicWindow(int x, int y, int w, int h) {
		super(x, y, w, h);
		main = new ScrollSelectableMessageWindow(x, y, w, h, 23, false);
		main.setLoop(true);
		x += 8;
		y += 8;
		w -= 8;
		h -= 8;
		choiceUse = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(""));
		choiceUse.setVisible(false);
		tgtSelect = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(""));
		tgtSelect.setVisible(false);
		msg = new ScrollSelectableMessageWindow(x, y, w, h, 23, false);
		msg.setVisible(false);
		group = new MessageWindowGroup(choiceUse, tgtSelect, msg.getWindow());
		updateText();
	}

	private Actor getPC() {
		return GameSystem.getInstance().getPCbyID(getSelectedPC().getId());
	}

	private Actor getPC(String id) {
		return GameSystem.getInstance().getPCbyID(id);
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
	private MessageWindow choiceUse, tgtSelect;
	private ScrollSelectableMessageWindow msg;
	private MessageWindowGroup group;

	private static final int USE = 0;
	private static final int CHECK = 1;

	public void nextSelect() {
		switch (mode) {
			case WAIT_MSG_CLOSE_TO_CU:
			case WAIT_MSG_CLOSE_TO_MUS: {
				if (msg.isVisible()) {
					msg.nextSelect();
				}
				break;
			}
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
			case WAIT_MSG_CLOSE_TO_CU:
			case WAIT_MSG_CLOSE_TO_MUS: {
				if (msg.isVisible()) {
					msg.prevSelect();
				}
				break;
			}
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
		if (getSelectedPC().getActions().stream().filter(p -> p.getType() == ActionType.魔法).toList().isEmpty()) {
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
				Choice c = new Choice(options, "IMAGIC_WINDOW_SUB",
						I18N.get(GameSystemI18NKeys.Xの, getSelectedPC().getVisibleName())
						+ I18N.get(GameSystemI18NKeys.Xを, a.getVisibleName()));
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
						if (!a.isField()) {
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getPC().getVisibleName(), a.getVisibleName()));
							sb.append(Text.getLineSep());
							sb.append(I18N.get(GameSystemI18NKeys.しかしこの魔法はフィールドでは使えない));
							msg.setText(Text.split(new Text(sb.toString())));
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//SELFのみの場合即時実行
						if (a.getMainEvents().isEmpty()) {
							//代償が支払えるかのチェック
							Action.ResourceShortage s = a.checkResource(getSelectedPC());
							if (s.is足りないステータスあり()) {
								StringBuilder sb = new StringBuilder();
								sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getPC().getVisibleName(), a.getVisibleName()));
								sb.append(Text.getLineSep());
								sb.append(I18N.get(GameSystemI18NKeys.しかしXが足りない, s.keys.stream().map(p -> p.getVisibleName()).toString()));
								msg.setText(Text.split(new Text(sb.toString())));
								group.show(msg.getWindow());
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								return;
							}
							//即時実行してサブに効果を出力
							Status tgt = getSelectedPC();
							tgt.saveBeforeDamageCalc();
							ActionResult r = a.exec(new ActionTarget(getPC(), a, List.of(getPC(tgt.getId())), true));
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getPC().getVisibleName(), a.getVisibleName()));
							sb.append(Text.getLineSep());
							if (r.is成功あり()) {
								//成功
								//効果測定
								StatusValueSet map = tgt.getDamageFromSavePoint();
								for (StatusValue e : map) {
									if (e.getValue() < 0f) {
										sb.append(I18N.get(GameSystemI18NKeys.Xの, getPC().getVisibleName()));
										sb.append(I18N.get(GameSystemI18NKeys.Xは, e.getKey().getVisibleName()));
										sb.append(I18N.get(GameSystemI18NKeys.X回復した, Math.abs(e.getValue()) + ""));
										sb.append(Text.getLineSep());
									} else if (e.getValue() > 0f) {
										sb.append(I18N.get(GameSystemI18NKeys.Xの, getPC().getVisibleName()));
										sb.append(I18N.get(GameSystemI18NKeys.Xに, e.getKey().getVisibleName()));
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
							msg.setText(Text.split(new Text(sb.toString())));
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_MUS;
							return;
						}
						//チームが入っている場合即時実行
						if (a.getTgtType() == Action.ターゲットモード.全員
								|| a.getTgtType() == Action.ターゲットモード.グループ_味方全員
								|| a.getTgtType() == Action.ターゲットモード.グループ_切替可能_初期選択味方
								|| a.getTgtType() == Action.ターゲットモード.グループ_切替可能_初期選択敵
								|| a.getTgtType() == Action.ターゲットモード.全員_自身除く
								|| a.getTgtType() == Action.ターゲットモード.グループ_味方全員_自身除く
								|| a.getTgtType() == Action.ターゲットモード.グループ_切替可能_初期選択味方_自身除く
								|| a.getTgtType() == Action.ターゲットモード.グループ_切替可能_初期選択敵_自身除く) {
							//代償が支払えるかのチェック
							Action.ResourceShortage s = a.checkResource(getSelectedPC());
							if (s.is足りないステータスあり()) {
								StringBuilder sb = new StringBuilder();
								sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getPC().getVisibleName(), a.getVisibleName()));
								sb.append(Text.getLineSep());
								sb.append(I18N.get(GameSystemI18NKeys.しかしXが足りない, s.keys.stream().map(p -> p.getVisibleName()).toList().toString()));
								msg.setText(Text.split(new Text(sb.toString())));
								group.show(msg.getWindow());
								mode = Mode.WAIT_MSG_CLOSE_TO_CU;
								return;
							}
							//即時実行してサブに効果を出力
							GameSystem.getInstance().getParty().forEach(p -> p.getStatus().saveBeforeDamageCalc());
							ActionResult r = a.exec(new ActionTarget(getPC(), a, GameSystem.getInstance().getParty(), true));
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getPC().getVisibleName(), a.getVisibleName()));
							sb.append(Text.getLineSep());
							for (var v : r.getUserEventResultAsList()) {
								sb.append(v.msgI18Nd).append(Text.getLineSep());
							}
							for (var v : r.getMainEventResultAsList()) {
								for (var vv : v.perActor.values()) {
									sb.append(vv.msgI18Nd).append(Text.getLineSep());
								}
							}
							msg.setText(Text.split(new Text(sb.toString())));
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_MUS;
							return;
						}
						//代償が支払えるかのチェック
						Action.ResourceShortage s = a.checkResource(getSelectedPC());
						if (s.is足りないステータスあり()) {
							StringBuilder sb = new StringBuilder();
							sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getPC().getVisibleName(), a.getVisibleName()));
							sb.append(Text.getLineSep());
							sb.append(I18N.get(GameSystemI18NKeys.しかしXが足りない, s.keys.stream().map(p -> p.getVisibleName()).toList().toString()));
							msg.setText(Text.split(new Text(sb.toString())));
							group.show(msg.getWindow());
							mode = Mode.WAIT_MSG_CLOSE_TO_CU;
							return;
						}
						//その他の場合はターゲット選択へ
						List<Text> option1 = new ArrayList<>();
						option1.addAll(GameSystem.getInstance().getPartyStatus().stream()
								.map(p -> new Text(getPC(p.getId()).getVisibleName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(option1, "MAGIC_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xの, a.getVisibleName())
								+ I18N.get(GameSystemI18NKeys.Xを誰に使う, getSelectedAction().getVisibleName())));
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
						sb.append(Text.getLineSep());
						sb.append("--").append(I18N.get(GameSystemI18NKeys.戦闘効果)).append(Text.getLineSep());
						if (a.isBattle()) {
							//SPELL_TIME
							sb.append("  ");
							sb.append(I18N.get(GameSystemI18NKeys.詠唱時間))
									.append(":").append(a.getCastTime()).append(I18N.get(GameSystemI18NKeys.ターン));
							sb.append(Text.getLineSep());
							//AREA
							sb.append("  ");
							sb.append(I18N.get(GameSystemI18NKeys.範囲)).append(":").append(a.getArea());
							sb.append(Text.getLineSep());

							sb.append("---");
							sb.append(I18N.get(GameSystemI18NKeys.対象効果));
							sb.append(Text.getLineSep());
							for (ActionEvent e : a.getMainEvents()) {
								sb.append("  ・").append(e.getEventType().getEventDescI18Nd(e)).append(Text.getLineSep());
							}
							sb.append("---");
							sb.append(I18N.get(GameSystemI18NKeys.自身への効果));
							sb.append(Text.getLineSep());
							for (ActionEvent e : a.getUserEvents()) {
								sb.append("  ・").append(e.getEventType().getEventDescI18Nd(e)).append(Text.getLineSep());
							}
							//ターゲティング情報
							sb.append("---").append(GameSystemI18NKeys.戦闘時ターゲット情報).append(Text.getLineSep());
							sb.append("  ").append(a.getTgtType().getVisibleName()).append(Text.getLineSep());
							sb.append("  ").append(a.getDeadTgt().getVisibleName()).append(Text.getLineSep());
						} else {
							sb.append("  ").append(I18N.get(GameSystemI18NKeys.この魔法は戦闘中使えない)).append(Text.getLineSep());
						}
						sb.append(Text.getLineSep());
						sb.append("--").append(I18N.get(GameSystemI18NKeys.フィールド効果)).append(Text.getLineSep());
						if (a.isField()) {
							sb.append("---");
							sb.append(I18N.get(GameSystemI18NKeys.対象効果));
							sb.append(Text.getLineSep());
							for (ActionEvent e : a.getMainEvents()) {
								sb.append("  ・").append(e.getEventType().getEventDescI18Nd(e)).append(Text.getLineSep());
							}
							sb.append("---");
							sb.append(I18N.get(GameSystemI18NKeys.自身への効果));
							sb.append(Text.getLineSep());
							for (ActionEvent e : a.getUserEvents()) {
								sb.append("  ・").append(e.getEventType().getEventDescI18Nd(e)).append(Text.getLineSep());
							}
						} else {
							sb.append("  ").append(I18N.get(GameSystemI18NKeys.この魔法はフィールドでは使えない)).append(Text.getLineSep());
						}
						msg.setText(Text.split(new Text(sb.toString())));
						group.show(msg.getWindow());
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
				}
				break;
			case TARGET_SELECT:
				commitUse();
				group.show(msg.getWindow());
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
		getSelectedPC().saveBeforeDamageCalc();
		//使用者とターゲットが違う場合はターゲットもDCP設定
		if (!getSelectedPC().equals(tgt)) {
			tgt.saveBeforeDamageCalc();
		}
		ActionResult r = a.exec(new ActionTarget(getPC(), a, List.of(getPC(tgt.getId())), true));
		StringBuilder sb = new StringBuilder();
		sb.append(I18N.get(GameSystemI18NKeys.XはXを詠唱した, getPC().getVisibleName(), a.getVisibleName()));
		sb.append(Text.getLineSep());
		for (var v : r.getUserEventResultAsList()) {
			sb.append(v.msgI18Nd).append(Text.getLineSep());
		}
		for (var v : r.getMainEventResultAsList()) {
			for (var vv : v.perActor.values()) {
				sb.append(vv.msgI18Nd).append(Text.getLineSep());
			}
		}
		msg.setText(Text.split(new Text(sb.toString())));
	}

	public Action getSelectedAction() {
		return GameSystem.getInstance().getPartyStatus().get(pcIdx)
				.getActions().stream().filter(p -> p.getType() == ActionType.魔法)
				.toList().get(main.getSelectedIdx() - 1);
	}

	public Status getSelectedPC() {
		return GameSystem.getInstance().getPartyStatus().get(pcIdx);
	}

	public Mode getCurrentMode() {
		return mode;
	}

	private void updateText() {
		Text line1 = new Text("<---" + I18N.get(GameSystemI18NKeys.Xの,
				getPC().getVisibleName()) + I18N.get(GameSystemI18NKeys.魔術) + "--->");

		List<Action> list = getSelectedPC().getActions().stream().filter(p -> p.getType() == ActionType.魔法).toList();
		if (list.isEmpty()) {
			Text line2 = new Text(I18N.get(GameSystemI18NKeys.使える魔術はない));
			main.setText(List.of(line1, line2));
			return;
		}
		List<Text> l = new ArrayList<>();
		l.add(line1);
		l.addAll(list.stream().map(p -> new Text(p.getVisibleName() + "／" + p.getSummary())).collect(Collectors.toList()));
		main.setText(l);
	}

	@Override
	public void update() {
		main.update();
		msg.update();
	}

	//1つ前の画面に戻る
	public boolean close() {
		msg.setText(List.of());
		//IUS表示中の場合は戻るは全消し
		if (group.getWindows().stream().allMatch(p -> !p.isVisible())) {
			mode = Mode.MAGIC_AND_USER_SELECT;
			return true;
		}
		if (msg.isVisible()) {
			mode = Mode.MAGIC_AND_USER_SELECT;
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
