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

/**
 *
 * @vesion 1.0.0 - 2022/12/25_9:00:48<br>
 * @author Shinacho<br>
 */
public class BookWindow extends BasicSprite {

	private List<Status> list;
	private MessageWindow main;
	private MessageWindow choiceUse, dropConfirm, tgtSelect, dissassemblyComfirm, msg;//msg�̓{�^������ő�����
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
		 * �ǂ̃A�C�e���ɂ��邩��I�𒆁B
		 */
		BOOK_AND_USER_SELECT,
		/**
		 * �A�C�e���g�p���e��I�𒆁B
		 */
		CHOICE_USE,
		/**
		 * MSG�\�����A�I���҂��B�I��������ITEM�QAND�QUSER�QSELECT�ɓ���B
		 */
		WAIT_MSG_CLOSE_TO_IUS,
		WAIT_MSG_CLOSE_TO_CU,
		/**
		 * �n���Ώۂ�I�𒆁B
		 */
		TARGET_SELECT,
		/**
		 * ���p���̉�́B��̂ł���̂̓y�[�W�����閂�p�������B
		 */
		DISASSEMBLY_CONFIRM,
		/**
		 * �̂ĂĂ��悢���m�F���B
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
				//�����Ȃ�
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
				//�����Ȃ�
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
				//�����Ȃ�
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
				//�����Ȃ�
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
				options.add(new Text(I18N.translate("PASS")));
				options.add(new Text(I18N.translate("CHECK")));
				options.add(new Text(I18N.translate("DISASSEMBLY")));
				options.add(new Text(I18N.translate("DROP")));
				Choice c = new Choice(options, "BOOK_WINDOW_SUB", getSelectedBook().getName() + I18N.translate("OF"));
				choiceUse.setText(c);
				choiceUse.allText();
				choiceUse.setSelect(0);
				group.show(choiceUse);
				mode = Mode.CHOICE_USE;
				break;
			case CHOICE_USE:
				//�I�΂ꂽ�I�����ɂ�蕪��
				switch (choiceUse.getSelect()) {
					case PASS:
						//�p�X�^�[�Q�b�g�Ɉړ�
						List<Text> options2 = new ArrayList<>();
						options2.addAll(list.stream().map(p -> new Text(p.getName())).collect(Collectors.toList()));
						tgtSelect.setText(new Choice(options2, "BOOK_WINDOW_SUB", b.getName() + I18N.translate("WHO_DO_PASS")));
						tgtSelect.allText();
						group.show(tgtSelect);
						mode = Mode.TARGET_SELECT;
						break;
					case CHECK:
						//CHECK���[�h�ł͉��l�A�L�[�A�C�e�������A�X���b�g�A�U���́ADCS��\�����邱�ƁI
						//�A�C�e���̏ڍׂ��T�u�ɕ\��
						StringBuilder sb = new StringBuilder();
						sb.append(b.getName()).append(Text.getLineSep());

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
						//���l
						sb.append(" ").append(I18N.translate("VALUE")).append(":").append(b.getValue());
						sb.append(Text.getLineSep());
						//��̑f��
						sb.append(I18N.translate("IF_DISASSEMBLY_GET")).append(Text.getLineSep());
						for (BookPage p : b.getPages()) {
							sb.append("   ").append(p.getDesc()).append(Text.getLineSep());
						}

						msg.setText(sb.toString());
						msg.allText();
						group.show(msg);
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
						break;
					case DISASSEMBLY:
						//��̊m�F�E�C���h�E��L����
						List<Text> options4 = new ArrayList<>();
						options4.add(new Text(I18N.translate("NO")));
						options4.add(new Text(I18N.translate("YES")));
						dissassemblyComfirm.reset();
						dissassemblyComfirm.setText(new Choice(options4, "DISSASSE_CONFIRM", b.getName() + I18N.translate("REALLY_DISASSEMBLY")));
						dissassemblyComfirm.allText();
						group.show(dissassemblyComfirm);
						mode = Mode.DISASSEMBLY_CONFIRM;
						break;
					case DROP:
						//drop�m�F�E�C���h�E��L����
						List<Text> options5 = new ArrayList<>();
						options5.add(new Text(I18N.translate("NO")));
						options5.add(new Text(I18N.translate("YES")));
						dropConfirm.reset();
						dropConfirm.setText(new Choice(options5, "DROP_CONFIRM", b.getName() + I18N.translate("REALLY_DROP")));
						dropConfirm.allText();
						group.show(dropConfirm);
						mode = Mode.DROP_CONFIRM;
						break;
				}
				break;
			case TARGET_SELECT:
				//tgt�E�C���h�E����I�����ꂽ�Ώێ҂����Ƃ�USE�܂���PASS�����s
				//use or pass
				assert choiceUse.getSelect() == PASS : "BOOKWINDOW : choice user select is missmatch";
				if (choiceUse.getSelect() == PASS) {
					int size = getSelectedPC().getBookBag().size();
					commitPass();
					boolean self = size == getSelectedPC().getBookBag().size();
					group.show(msg);
					//�������g�ɓn�����ꍇCU�ցA�����łȂ��ꍇ��IUS�ɖ߂�
					if (self) {
						mode = Mode.WAIT_MSG_CLOSE_TO_CU;
					} else {
						mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
					}
				}
				break;
			case DROP_CONFIRM:
				//drop�m�F�E�C���h�E�̑I�����ɂ�蕪��
				switch (dropConfirm.getSelect()) {
					case 0:
						//������
						//�p�r�I���ɖ߂�
						group.show(choiceUse);
						mode = Mode.CHOICE_USE;
						break;
					case 1:
						//�͂�
						//drop���ăA�C�e���I���ɖ߂�
						commitDrop();
						group.show(msg);
						mode = Mode.WAIT_MSG_CLOSE_TO_IUS;
						break;
				}
				break;
			case DISASSEMBLY_CONFIRM:
				//��̊m�F�E�C���h�E�̑I�����ɂ�蕪��
				switch (dissassemblyComfirm.getSelect()) {
					case 0:
						//������
						//�p�r�I���ɖ߂�
						group.show(choiceUse);
						mode = Mode.CHOICE_USE;
						break;
					case 1:
						//�͂�
						//drop���ăA�C�e���I���ɖ߂�
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
			msg.setText(getSelectedPC().getName() + I18N.translate("IS")
					+ tgt.getName() + I18N.translate("TO") + i.getName() + I18N.translate("PASSED"));
			mainSelect = 0;
		} else {
			msg.setText(getSelectedPC().getName() + I18N.translate("IS") + i.getName() + I18N.translate("RESET_ITEM"));
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
			sb.append(b.getName()).append(Text.getLineSep());
			j++;
		}
		main.setText(sb.toString());
		main.allText();
		main.setVisible(true);
	}

	private void commitDrop() {
		dropConfirm.close();
		Book i = getSelectedBook();
		//1���������Ă��Ȃ������瑕�����O��
		getSelectedPC().getBookBag().drop(i);
		msg.setText(getSelectedPC().getName() + I18N.translate("IS") + i.getName() + I18N.translate("WAS_DROP"));
		msg.allText();
		group.show(msg);
		mainSelect = 0;
	}

	private void commitDissasse() {
		Book i = getSelectedBook();
		List<BookPage> pages = i.getPages();
		getSelectedPC().getBookBag().drop(i);
		GameSystem.getInstance().getBookPageBag().addAll(pages);
		StringBuilder s = new StringBuilder();
		s.append(getSelectedPC().getName()).append(I18N.translate("IS")).append(i.getName()).append(I18N.translate("WAS_DISASSEMBLY"));
		s.append(Text.getLineSep());
		Map<String, Long> count = pages.stream().collect(Collectors.groupingBy(BookPage::getDesc, Collectors.counting()));
		for (Map.Entry<String, Long> e : count.entrySet()) {
			s.append(e.getKey()).append(I18N.translate("OF")).append(e.getValue()).append(I18N.translate("GET_PAGE"));
			s.append(Text.getLineSep());
		}
		msg.setText(s.toString());
		msg.allText();
		group.show(msg);
		mainSelect = 0;
	}

	public boolean close() {
		//IUS�\�����̏ꍇ�͖߂�͑S����
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
		//���C���E�C���h�E�̓��e�X�V
		if (mode == Mode.BOOK_AND_USER_SELECT) {
			BookBag ib = getSelectedPC().getBookBag();
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
				for (Book i : ib) {
					if (j == mainSelect) {
						sb.append("  >");
					} else {
						sb.append("   ");
					}
					sb.append("    ");
					sb.append(i.getName()).append(Text.getLineSep());
					j++;
				}
				main.setText(sb.toString());
				main.allText();
				main.setVisible(true);
			}
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
