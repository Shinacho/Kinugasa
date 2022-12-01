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

import java.util.List;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import static kinugasa.game.system.BattleConfig.messageWindowY;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.Drawable;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/24_21:58:35<br>
 * @author Dra211<br>
 */
public class BattleMessageWindowSystem implements Drawable {

	private static final BattleMessageWindowSystem INSTANCE = new BattleMessageWindowSystem();

	private BattleMessageWindowSystem() {

	}

	static BattleMessageWindowSystem getInstance() {
		return INSTANCE;
	}

	//�R�}���h��\�����郁�b�Z�[�W�E�C���h�E
	private BattleCommandMessageWindow commandWindow;
	//�s����\�����郁�b�Z�[�W�E�C���h�E
	private MessageWindow actionWindow;
	private FrameTimeCounter actionWindowVisibleTC;
	//�s���ł��Ȃ��Ȃǂ̌x�����b�Z�[�W���o���E�C���h�E
	private MessageWindow infoWindow;
	//�U���\�Ƃ��̏������o���E�C���h�E
	private MessageWindow tooltipWindow;
	//INFO�̕\�����Ԃ̃}�X�^
	private int infoWindowVisibleTime = 60;
	//INFO�̕\�����ԃJ�E���^
	private FrameTimeCounter infoWindowVisibleTC;
	//�X�e�[�^�X�y�C��
	private StatusWindows statusWindows;
	//�ړ���s���p�̃E�C���h�E
	private AfterMoveActionMessageWindow afterMoveCommandWindow;
	//�A�C�e���E�C���h�E
	private ItemWindow itemWindow;

	void init(List<Status> statusList) {
		float w = GameOption.getInstance().getWindowSize().width - 6;
		float h = (float) (GameOption.getInstance().getWindowSize().height / 3.66f);
		commandWindow = new BattleCommandMessageWindow(3, messageWindowY, w, h);
		afterMoveCommandWindow = new AfterMoveActionMessageWindow(3, messageWindowY, w, h);
		itemWindow = new ItemWindow(3, messageWindowY, w, h);
		w = GameOption.getInstance().getWindowSize().width - 6;
		h = (float) (GameOption.getInstance().getWindowSize().height / 3.66f);
		actionWindow = new MessageWindow(3, messageWindowY, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		w = GameOption.getInstance().getWindowSize().width - 6;
		h = (float) (GameOption.getInstance().getWindowSize().height / 3.66f);
		infoWindow = new MessageWindow(48, messageWindowY, w - 48 * 2, h, new SimpleMessageWindowModel().setNextIcon(""));
		w = GameOption.getInstance().getWindowSize().width - 6 - 48 * 2;
		h = (float) (GameOption.getInstance().getWindowSize().height / 3.66f);
		tooltipWindow = new MessageWindow(48 * 4, messageWindowY, w - 48 * 2, h, new SimpleMessageWindowModel().setNextIcon(""));

		statusWindows = new StatusWindows(statusList);
		statusWindows.setVisible(true);
		commandWindow.setVisible(false);
		afterMoveCommandWindow.setVisible(false);
		infoWindow.setVisible(false);
		actionWindow.setVisible(true);
		tooltipWindow.setVisible(false);
		itemWindow.setVisible(false);

	}

	public ItemWindow getItemWindow() {
		return itemWindow;
	}

	public void closeItemWindow() {
		itemWindow.clearText();;
		itemWindow.setVisible(false);
	}

	void setAfterMoveCommand(List<BattleAction> list) {
		afterMoveCommandWindow.setActions(list);
		afterMoveCommandWindow.allText();
		afterMoveCommandWindow.setVisible(true);
	}

	void setActionMessage(String msg, int actionTime) {
		actionWindowVisibleTC = new FrameTimeCounter(actionTime);
		actionWindow.setText(new Text(msg.replaceAll("\\r\\n", Text.getLineSep())));
		actionWindow.setVisible(true);
		actionWindow.allText();
	}

	void setInfoMessage(String msg) {
		infoWindowVisibleTC = new FrameTimeCounter(infoWindowVisibleTime);
		infoWindow.setText(new Text(msg.replaceAll("\\r\\n", Text.getLineSep())));
		infoWindow.allText();
		infoWindow.setVisible(true);
	}

	void setTolltipMessage(String text) {
		tooltipWindow.setText(new Text(text));
		tooltipWindow.setVisible(true);
		tooltipWindow.allText();
	}

	boolean isVisibleCommand() {
		return commandWindow.isVisible();
	}

	boolean isVisibleAfterMoveCommand() {
		return afterMoveCommandWindow.isVisible();
	}

	boolean isVisibleActionMessage() {
		return actionWindow.isVisible();
	}

	boolean isVisibleInfoMessage() {
		return infoWindow.isVisible();
	}

	boolean isVisibleTolltipMessage() {
		return tooltipWindow.isVisible();
	}

	public void closeCommandWindow() {
		commandWindow.clearText();
		commandWindow.setVisible(false);
	}

	public void closeActionWindow() {
		actionWindow.clearText();
		actionWindow.setVisible(false);
		actionWindowVisibleTC = null;
	}

	public void closeInfoWindow() {
		infoWindow.clearText();
		infoWindow.setVisible(false);
		infoWindowVisibleTC = null;
	}

	public void closeTooltipWindow() {
		tooltipWindow.clearText();
		tooltipWindow.setVisible(false);
	}

	public void closeStatusWindow() {
		statusWindows.setVisible(false);
	}

	public void closeAfterMoveCommandWindow() {
		afterMoveCommandWindow.clearText();
		afterMoveCommandWindow.setVisible(false);
	}

	void update() {
		if (closeAll) {
			return;
		}
		//info�E�C���h�E�̕\�����Ԕ���
		if (infoWindowVisibleTC != null) {
			//info�\����
			infoWindow.update();
			if (infoWindowVisibleTC.isReaching()) {
				//�\���I��
				closeInfoWindow();
			}
		}
		if (actionWindowVisibleTC != null) {
			//action�\����
			actionWindow.update();
			if (actionWindowVisibleTC.isReaching()) {
				//�\���I��
				closeActionWindow();
			}
		}

		//�R�}���h�E�C���h�E�̏���
		//���e�̕ύX��BattleSystem������s�����
		commandWindow.update();

		//�X�e�[�^�X�E�C���h�E�̏���
		//���e�͎����I�ɕύX�����
		statusWindows.update();

		//�c�[���`�b�v�E�C���h�E�̍X�V
		tooltipWindow.update();

		afterMoveCommandWindow.update();

		itemWindow.update();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (closeAll) {
			return;
		}
		statusWindows.draw(g);
		commandWindow.draw(g);
		afterMoveCommandWindow.draw(g);
		actionWindow.draw(g);
		infoWindow.draw(g);
		tooltipWindow.draw(g);
		itemWindow.draw(g);
	}

	public void setInfoWindowVisibleTime(int infoWindowVisibleTime) {
		this.infoWindowVisibleTime = infoWindowVisibleTime;
	}

	public BattleCommandMessageWindow getCommandWindow() {
		return commandWindow;
	}

	MessageWindow getActionWindow() {
		return actionWindow;
	}

	MessageWindow getInfoWindow() {
		return infoWindow;
	}

	MessageWindow getTooltipWindow() {
		return tooltipWindow;
	}

	StatusWindows getStatusWindows() {
		return statusWindows;
	}

	public AfterMoveActionMessageWindow getAfterMoveCommandWindow() {
		return afterMoveCommandWindow;
	}

	//�ꎞ�I�ɂ��ׂẴE�C���h�E���N���[�Y����B
	private boolean closeAll = false;

	public void closeAll() {
		closeAll = true;
	}

	//�ꎞ�I�ɃN���[�Y�����E�C���h�E���I�[�v������
	public void reOpenAll() {
		closeAll = false;
	}

	public void switchVisible() {
		closeAll = !closeAll;
	}

}
