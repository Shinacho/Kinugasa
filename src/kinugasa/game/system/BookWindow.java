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
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.MessageWindowGroup;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_9:00:48<br>
 * @author Shinacho<br>
 */
public class BookWindow extends BasicSprite {

	private List<Status> list;
	private MessageWindow main;
	private MessageWindow choiceUse, dropConfirm, tgtSelect, dissassemblyComfirm, msg;//msgはボタン操作で即閉じる
	private MessageWindowGroup group;

	public BookWindow(float x, float y, float w, float h) {
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
		dissassemblyComfirm = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		dissassemblyComfirm.setVisible(false);
		tgtSelect = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		tgtSelect.setVisible(false);
		msg = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel());
		msg.setVisible(false);

		group = new MessageWindowGroup(choiceUse, dropConfirm, dissassemblyComfirm, tgtSelect, msg);
		mainSelect = 0;
		update();
	}

	public enum Mode {
		/**
		 * どのアイテムにするかを選択中。
		 */
		BOOK_AND_USER_SELECT,
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
		 * 渡す対象を選択中。
		 */
		TARGET_SELECT,
		/**
		 * 魔術書の解体。解体できるのはページがある魔術書だけ。
		 */
		DISASSEMBLY_CONFIRM,
		/**
		 * 捨ててもよいか確認中。
		 */
		DROP_CONFIRM,
	}
	private Mode mode = Mode.BOOK_AND_USER_SELECT;
	private int pcIdx;
	private int mainSelect = 0;

	public void nextSelect() {
		switch (mode) {
			case BOOK_AND_USER_SELECT:
				mainSelect++;
				if (mainSelect >= getSelectedPC().getBookBag().size()) {
					mainSelect = 0;
				}
				return;
			case CHOICE_USE:
				choiceUse.nextSelect();
				return;
			case DROP_CONFIRM:
				dropConfirm.nextSelect();
				return;
			case DISASSEMBLY_CONFIRM:
				dissassemblyComfirm.nextSelect();
				break;
			case TARGET_SELECT:
			case WAIT_MSG_CLOSE_TO_IUS:
			case WAIT_MSG_CLOSE_TO_CU:
				//処理なし
				return;
		}
	}

	public void prevSelect() {
		switch (mode) {
			case BOOK_AND_USER_SELECT:
				mainSelect--;
				if (mainSelect < 0) {
					mainSelect = getSelectedPC().getBookBag().size() - 1;
				}
				return;
			case CHOICE_USE:
				choiceUse.prevSelect();
				return;
			case DROP_CONFIRM:
				dropConfirm.prevSelect();
				return;
			case DISASSEMBLY_CONFIRM:
				dissassemblyComfirm.prevSelect();
				break;
			case TARGET_SELECT:
			case WAIT_MSG_CLOSE_TO_IUS:
			case WAIT_MSG_CLOSE_TO_CU:
				//処理なし
				return;
		}
	}

	public void nextPC() {
		switch (mode) {
			case BOOK_AND_USER_SELECT:
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
			case DISASSEMBLY_CONFIRM:
				//処理なし
				return;
			case TARGET_SELECT:
				tgtSelect.nextSelect();
				return;
		}
	}

	public void prevPC() {
		switch (mode) {
			case BOOK_AND_USER_SELECT:
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
			case DISASSEMBLY_CONFIRM:
				//処理なし
				return;
			case TARGET_SELECT:
				tgtSelect.prevSelect();
				return;
		}
	}

	public BookWindow.Mode currentMode() {
		return mode;
	}

	public Status getSelectedPC() {
		return list.get(pcIdx);
	}

	public Book getSelectedBook() {
		return getSelectedPC().getBookBag().getBooks().get(mainSelect);
	}
	private static final int PASS = 0;
	private static final int CHECK = 1;
	private static final int DISASSEMBLY = 2;
	private static final int DROP = 3;

	public void select() {
		if (getSelectedPC().getBookBag().isEmpty()) {
			group.closeAll();
			mode = Mode.BOOK_AND_USER_SELECT;
			return;
		}
		Book b = getSelectedBook();
		switch (mode) {
			case BOOK_AND_USER_SELECT:
				List<Text> options = new ArrayList<>();
				options.add(new Text(I18N.get(GameSystemI18NKeys.渡す)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.調べる)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.解体)));
				options.add(new Text(I18N.get(GameSystemI18NKeys.捨てる)));
				Choice c = new Choice(options, "BOOK_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xを, getSelectedBook().getVisibleName()));
				choiceUse.setText(c);
				choiceUse.allText();
				choiceUse.setSelect(0);
				group.show(choiceUse);
				mode = Mode.CHOICE_USE;
				break;
			case CHOICE_USE:
				//選ばれた選択肢により分岐
				switch (choiceUse.getSelect()) {
					case PASS:
						//パスターゲットに移動
						List<Text> options2 = new ArrayList<>();
						options2.addAll(list.stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(options2, "BOOK_WINDOW_SUB", I18N.get(GameSystemI18NKeys.Xの, getSelectedPC().getName()) + I18N.get(GameSystemI18NKeys.Xを誰に渡す, b.getVisibleName())));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case CHECK:
						//CHECKモードでは価値、キーアイテム属性、スロット、攻撃力、DCSを表示すること！
						//アイテムの詳細をサブに表示
						StringBuilder sb = new StringBuilder();
						sb.append(b.getVisibleName()).append(Text.getLineSep());

						//DESC
						String desc = b.getDesc();
						if (desc.contains(Text.getLineSep())) {
							String[] sv = desc.split(Text.getLineSep());
							for (String v : sv) {
								sb.append(" ").append(v);
								sb.append(Text.getLineSep());
							}
						} else {
							sb.append(" ").append(b.getDesc());
							sb.append(Text.getLineSep());
						}
						//価値
						sb.append(" ").append(I18N.get(GameSystemI18NKeys.価値)).append(":").append(b.getValue());
						sb.append(Text.getLineSep());
						//解体素材
						sb.append(I18N.get(GameSystemI18NKeys.解体すると以下を入手する)).append(Text.getLineSep());
						for (Page p : b.getPage()) {
							sb.append("   ").append(p.getEvent().getDesc()).append(Text.getLineSep());
						}

						msg.setText(sb.toString());
						msg.allText();
						group.show(msg);
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						break;
					case DISASSEMBLY:
						//解体確認ウインドウを有効化
						List<Text> options4 = new ArrayList<>();
						options4.add(new Text(I18N.get(GameSystemI18NKeys.いいえ)));
						options4.add(new Text(I18N.get(GameSystemI18NKeys.はい)));
						dissassemblyComfirm.reset();
						dissassemblyComfirm.setText(new Choice(options4, "DISSASSE_CONFIRM", I18N.get(GameSystemI18NKeys.Xの, getSelectedPC().getName())
								+ I18N.get(GameSystemI18NKeys.Xを本当に解体する, b.getVisibleName())));
						dissassemblyComfirm.allText();
						group.show(dissassemblyComfirm);
						mode = Mode.DISASSEMBLY_CONFIRM;
						break;
					case DROP:
						//drop確認ウインドウを有効化
						List<Text> options5 = new ArrayList<>();
						options5.add(new Text(I18N.get(GameSystemI18NKeys.いいえ)));
						options5.add(new Text(I18N.get(GameSystemI18NKeys.はい)));
						dropConfirm.reset();
						dropConfirm.setText(new Choice(options5, "DROP_CONFIRM", I18N.get(GameSystemI18NKeys.Xの, getSelectedPC().getName()) + I18N.get(GameSystemI18NKeys.Xを本当にすてる, b.getVisibleName())));
						dropConfirm.allText();
						group.show(dropConfirm);
						mode = Mode.DROP_CONFIRM;
						break;
				}
				break;
			case TARGET_SELECT:
				//tgtウインドウから選択された対象者をもとにUSEまたはPASSを実行
				//use or pass
				assert choiceUse.getSelect() == PASS : "BOOKWINDOW : choice user select is missmatch";
				if (choiceUse.getSelect() == PASS) {
					int size = getSelectedPC().getBookBag().size();
					commitPass();
					boolean self = size == getSelectedPC().getBookBag().size();
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
			case DISASSEMBLY_CONFIRM:
				//解体確認ウインドウの選択肢により分岐
				switch (dissassemblyComfirm.getSelect()) {
					case 0:
						//いいえ
						//用途選択に戻る
						group.show(choiceUse);
						mode = Mode.CHOICE_USE;
						break;
					case 1:
						//はい
						//dropしてアイテム選択に戻る
						commitDissasse();
						group.show(msg);
						mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
						break;
				}
				break;
			case WAIT_MSG_CLOSE_TO_IUS:
				group.closeAll();
				mode = Mode.BOOK_AND_USER_SELECT;
				break;
			case WAIT_MSG_CLOSE_TO_CU:
				group.show(choiceUse);
				mode = Mode.CHOICE_USE;
				break;
		}
	}

	private void commitPass() {
		Status tgt = GameSystem.getInstance().getPartyStatus().get(tgtSelect.getSelect());
		Book i = getSelectedBook();
		getSelectedPC().passBook(tgt, i);
		if (!getSelectedPC().equals(tgt)) {
			String t = I18N.get(GameSystemI18NKeys.XはXにXを渡した, getSelectedPC().getName(), tgt.getName(), i.getVisibleName());
			GameSystem.getInstance().getPartyStatus().forEach(p -> p.updateAction());
			getSelectedPC().updateAction();
			msg.setText(t);
			mainSelect = 0;
		} else {
			String t = I18N.get(GameSystemI18NKeys.XはXを持ち替えた, getSelectedPC().getName(), i.getVisibleName());
			msg.setText(t);
			mainSelect = getSelectedPC().getBookBag().size() - 1;
		}
		msg.allText();
		group.show(msg);

		StringBuilder sb = new StringBuilder();
		BookBag ib = getSelectedPC().getBookBag();
		sb = new StringBuilder();
		sb.append("<---");
		sb.append(getSelectedPC().getName());
		sb.append("--->");
		sb.append(Text.getLineSep());
		int j = 0;
		for (Book b : ib) {
			if (j == main.getSelect()) {
				sb.append("  >");
			} else {
				sb.append("   ");
			}
			sb.append("    ");
			sb.append(b.getVisibleName()).append(Text.getLineSep());
			j++;
		}
		if (getSelectedPC().getEffectedStatus().get(Status.canMagicStatusName).getValue() != (Float.parseFloat(Status.canMagicStatusValue))) {
			List<Action> removeList = getSelectedPC().getActions(ActionType.MAGIC);
			getSelectedPC().getActions().removeAll(removeList);
		}
		main.setText(sb.toString());
		main.allText();
		main.setVisible(true);
	}

	private void commitDrop() {
		dropConfirm.close();
		Book i = getSelectedBook();
		//1個しか持っていなかったら装備を外す
		getSelectedPC().getBookBag().drop(i);
		msg.setText(I18N.get(GameSystemI18NKeys.XはXを捨てた, getSelectedPC().getName(), i.getVisibleName()));
		msg.allText();
		getSelectedPC().updateAction();
		group.show(msg);
		mainSelect = 0;
	}

	private void commitDissasse() {
		Book i = getSelectedBook();
		List<Page> pages = i.getPage();
		getSelectedPC().getBookBag().drop(i);
		GameSystem.getInstance().getPageBag().addAll(pages);
		StringBuilder s = new StringBuilder();
		s.append(I18N.get(GameSystemI18NKeys.XはXを解体した, getSelectedPC().getName(), i.getVisibleName()));
		s.append(Text.getLineSep());
		Map<String, Long> count = pages.stream().map(p -> p.getEvent()).collect(Collectors.groupingBy(ActionEvent::getDesc, Collectors.counting()));
		for (Map.Entry<String, Long> e : count.entrySet()) {
			s.append(I18N.get(GameSystemI18NKeys.XをX個入手した, e.getKey(), e.getValue() + ""));
			s.append(Text.getLineSep());
		}
		msg.setText(s.toString());
		msg.allText();
		getSelectedPC().updateAction();
		group.show(msg);
		mainSelect = 0;
	}

	public boolean close() {
		//IUS表示中の場合は戻るは全消し
		if (group.getWindows().stream().allMatch(p -> !p.isVisible())) {
			mode = Mode.BOOK_AND_USER_SELECT;
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
		if (dissassemblyComfirm.isVisible()) {
			mode = Mode.CHOICE_USE;
			group.show(choiceUse);
			return false;
		}
		if (choiceUse.isVisible()) {
			mode = Mode.BOOK_AND_USER_SELECT;
			group.closeAll();
			return false;
		}
		group.closeAll();
		mode = Mode.BOOK_AND_USER_SELECT;
		return false;
	}

	@Override
	public void update() {
		//メインウインドウの内容更新
		if (mode == Mode.BOOK_AND_USER_SELECT) {
			BookBag ib = getSelectedPC().getBookBag();
			StringBuilder sb = new StringBuilder();
			sb.append("<---");
			sb.append(getSelectedPC().getName());
			sb.append("--->");
			sb.append(Text.getLineSep());
			if (ib.isEmpty()) {
				sb.append("  ").append(I18N.get(GameSystemI18NKeys.何も持っていない));
			} else {
				int j = 0;
				for (Book i : ib) {
					if (j == mainSelect) {
						sb.append("  >");
					} else {
						sb.append("   ");
					}
					sb.append("    ");
					sb.append(i.getVisibleName()).append(Text.getLineSep());
					j++;
				}
			}
			sb.append(Text.getLineSep());
			sb.append(Text.getLineSep());
			sb.append(I18N.get(GameSystemI18NKeys.あとX個持てる, getSelectedPC().getBookBag().sizeAt() + ""));
			main.setText(sb.toString());
			main.allText();
			main.setVisible(true);
		}
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
		dissassemblyComfirm.draw(g);
		msg.draw(g);
	}

}
