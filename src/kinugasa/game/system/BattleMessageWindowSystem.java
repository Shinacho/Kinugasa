/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import static kinugasa.game.system.BattleConfig.messageWindowY;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.Drawable;

/**
 *
 * @vesion 1.0.0 - 2023/05/09_21:21:01<br>
 * @author Shinacho<br>
 */
public class BattleMessageWindowSystem implements Drawable {

	private BattleMessageWindowSystem() {
	}

	private static final BattleMessageWindowSystem INSTANCE = new BattleMessageWindowSystem();

	public static BattleMessageWindowSystem getInstance() {
		return INSTANCE;
	}
	//�X�e�[�^�X�i�㕔�j�y�C��
	private BattleStatusWindows statusW;
	//�R�}���h�E�C���h�E�{��
	private BattleCommandMessageWindow cmdW;
	//�^�[�Q�b�g���\���E�C���h�E(7)
	private ScrollSelectableMessageWindow tgtW;
	//�A�C�e��ChoiceUse(Choice
	private MessageWindow itemChoiceUseW;
	//�A�C�e���R�~�b�g���ʕ\��
	private MessageWindow itemCommitResultW;
	//�ړ���R�}���h�E�C���h�E
	private AfterMoveActionMessageWindow afterMoveW;
	//�A�N�V�������U���g�E�C���h�E
	private MessageWindow actionResultW;
	//�ėpINFO�͕ʂ̃E�C���h�E�Ƌ����ł���
	private MessageWindow infoW;
	//�X�e�[�^�X�ڍ׃E�C���h�E
	private PCStatusWindow statusDescW;
	private int statusDescWPage = 0;
	//�A�C�e���ڍ׃E�C���h�E
	private MessageWindow itemDescW;
	//�o�g�����U���g
	private MessageWindow battleResultW;

	void init() {
		List<Status> statusList = GameSystem.getInstance().getPartyStatus();
		float w = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() - 6;
		float h = (float) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 3.66f);
		cmdW = new BattleCommandMessageWindow(3, (int) messageWindowY, (int) w, (int) h);
		afterMoveW = new AfterMoveActionMessageWindow(3, (int) messageWindowY, (int) w, (int) h);
		tgtW = new ScrollSelectableMessageWindow(3, (int) messageWindowY, (int) w, (int) h, 7, false);
		infoW = new MessageWindow(480, messageWindowY, w - 480, h, new SimpleMessageWindowModel().setNextIcon(""));
		statusW = new BattleStatusWindows(statusList);
		itemChoiceUseW = new MessageWindow(3, messageWindowY, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		itemCommitResultW = new MessageWindow(3, messageWindowY, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		actionResultW = new MessageWindow(3, messageWindowY, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		statusDescW = new StatusDescWindow(
				24 + 8,
				24 + 8,
				(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
				(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 48 - 32),
				GameSystem.getInstance().getPartyStatus()
		);
		itemDescW = new MessageWindow(
				24 + 8,
				24 + 8,
				(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
				(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 48 - 32)
		);
		battleResultW = new MessageWindow(
				24 + 8,
				24 + 8,
				(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
				(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 48 - 32)
		);
		statusDescWPage = 0;

		setVisible(
				StatusVisible.ON,
				Mode.ACTION,
				InfoVisible.OFF);

	}

	void statusDescWindowNextPage() {
		statusDescWPage++;
		if (statusDescWPage >= 5) {
			statusDescWPage = 0;
		}
		switch (statusDescWPage) {
			case 0:
				statusDescW = new StatusDescWindow(
						24 + 8,
						24 + 8,
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 48 - 32),
						GameSystem.getInstance().getPartyStatus()
				);
				break;
			case 1:
				statusDescW = new AttrDescWindow(
						24 + 8,
						24 + 8,
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 48 - 32),
						GameSystem.getInstance().getPartyStatus()
				);

			case 2:
				statusDescW = new EqipItemWindow(
						24 + 8,
						24 + 8,
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 48 - 32),
						GameSystem.getInstance().getPartyStatus()
				);

				break;
			case 3:
				statusDescW = new ActionDescWindow(
						24 + 8,
						24 + 8,
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 48 - 32),
						GameSystem.getInstance().getPartyStatus()
				);
				break;
			case 4:
				statusDescW = new ConditionDescWindow(
						24 + 8,
						24 + 8,
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 48 - 32),
						GameSystem.getInstance().getPartyStatus()
				);
				break;
			default:
				throw new AssertionError();
		}
	}

	void setItemDesc(Item i) {

	}

	void statusDescWindowNextSelect() {
		switch (statusDescWPage) {
			case 0:
			case 2:
				break;
			case 1:
			case 3:
			case 4:
				statusDescW.next();
				break;
		}
	}

	void statusDescWindowPrevSelect() {
		switch (statusDescWPage) {
			case 0:
			case 2:
				break;
			case 1:
			case 3:
			case 4:
				statusDescW.prev();
				break;
		}
	}

	void saveVisible() {
		prevSv = sv;
		prevMode = mode;
		prevIv = iv;
	}

	void setVisibleFromSave() {
		setVisible(prevSv, prevMode, prevIv);
	}

	enum Mode {
		TGT_SELECT,
		CMD_SELECT,
		ITEM_USE_SELECT,
		ITEM_COMMIT,
		AFTER_MOVE,
		ACTION,
		NOTHING,
		SHOW_STATUS_DESC,
		SHOW_ITEM_DESC,
		BATTLE_RESULT,
	}

	enum StatusVisible {
		ON,
		OFF
	}

	enum InfoVisible {
		ON,
		OFF
	}
	private Mode mode, prevMode;
	private StatusVisible sv, prevSv;
	private InfoVisible iv, prevIv;

	void setStatusDescPCIDX(int i) {
		statusDescW.setPcIdx(i);
	}

	void setVisible(Mode m) {
		setVisible(StatusVisible.ON, m, InfoVisible.OFF);
	}

	void setVisible(StatusVisible sv, Mode m) {
		setVisible(sv, m, InfoVisible.OFF);
	}

	void setVisible(StatusVisible sv, Mode m, InfoVisible iv) {
		this.sv = sv;
		this.mode = m;
		this.iv = iv;
		statusW.setVisible(sv == StatusVisible.ON);
		infoW.setVisible(iv == InfoVisible.ON);
		cmdW.setVisible(false);
		tgtW.setVisible(false);
		afterMoveW.setVisible(false);
		itemChoiceUseW.setVisible(false);
		itemCommitResultW.setVisible(false);
		actionResultW.setVisible(false);
		statusDescW.setVisible(false);
		itemDescW.setVisible(false);
		battleResultW.setVisible(false);
		switch (m) {
			case NOTHING:
				break;
			case CMD_SELECT:
				cmdW.setVisible(true);
				break;
			case TGT_SELECT:
				tgtW.setVisible(true);
				break;
			case AFTER_MOVE:
				afterMoveW.setVisible(true);
				break;
			case ITEM_USE_SELECT:
				itemChoiceUseW.setVisible(true);
				break;
			case ITEM_COMMIT:
				itemCommitResultW.setVisible(true);
				break;
			case ACTION:
				actionResultW.setVisible(true);
				break;
			case SHOW_STATUS_DESC:
				statusDescW.setVisible(true);
				break;
			case SHOW_ITEM_DESC:
				itemDescW.setVisible(true);
				break;
			case BATTLE_RESULT:
				battleResultW.setVisible(true);
			default:
				throw new AssertionError();
		}
	}

	MessageWindow getActionResultW() {
		return actionResultW;
	}

	BattleCommandMessageWindow getCmdW() {
		return cmdW;
	}

	ScrollSelectableMessageWindow getTgtW() {
		return tgtW;
	}

	MessageWindow getItemChoiceUseW() {
		return itemChoiceUseW;
	}

	MessageWindow getItemCommitResultW() {
		return itemCommitResultW;
	}

	BattleStatusWindows getStatusW() {
		return statusW;
	}

	AfterMoveActionMessageWindow getAfterMoveW() {
		return afterMoveW;
	}

	PCStatusWindow getStatusDescW() {
		return statusDescW;
	}

	MessageWindow getInfoW() {
		return infoW;
	}

	MessageWindow getBattleResultW() {
		return battleResultW;
	}

	MessageWindow getItemDescW() {
		return itemDescW;
	}

	int getStatusDescWPage() {
		return statusDescWPage;
	}
	public static final int ITEM_CHOICE_USE_CHECK = 0;
	public static final int ITEM_CHOICE_USE_USE = 1;
	public static final int ITEM_CHOICE_USE_EQIP = 2;
	public static final int ITEM_CHOICE_USE_PASS = 3;
	public static final int ITEM_CHOICE_USE_THROW = 4;
	private int itemChoiceUseSelected = -1;

	void openItemChoiceUse() {
		if (!(cmdW.getSelectedCmd() instanceof Item)) {
			throw new GameSystemException("requested open item choice use window, but selected action is not item");
		}
		Item i = (Item) cmdW.getSelectedCmd();
		List<Text> options = new ArrayList<>();
		options.add(new Text(I18N.translate("CHECK")));
		options.add(new Text(I18N.translate("USE")));
		options.add(new Text(I18N.translate("EQIP")));
		options.add(new Text(I18N.translate("PASS")));
		options.add(new Text(I18N.translate("THROW")));
		itemChoiceUseW.setText(new Choice(options, "BATTLE_MW_SYSTEM_IUC", i.getName() + I18N.translate("OF")));
		setVisible(StatusVisible.ON, mode.ITEM_USE_SELECT, InfoVisible.OFF);
	}

	void itemChoiceUseNextSelect() {
		if (mode != Mode.ITEM_USE_SELECT) {
			throw new GameSystemException("item choice use select, but mode is not item choice use");
		}
		itemChoiceUseW.nextSelect();
	}

	void itemChoiceUsePrevSelect() {
		if (mode != Mode.ITEM_USE_SELECT) {
			throw new GameSystemException("item choice use select, but mode is not item choice use");
		}
		itemChoiceUseW.prevSelect();
	}

	int itemChoiceUseCommit() {
		if (mode != Mode.ITEM_USE_SELECT) {
			throw new GameSystemException("item choice use select, but mode is not item choice use");
		}
		return itemChoiceUseSelected = itemChoiceUseW.getSelect();
	}

	void update() {
		if (statusW.isVisible()) {
			statusW.update();
		}
		//
		if (cmdW.isVisible()) {
			cmdW.update();
		}
		//
		if (afterMoveW.isVisible()) {
			afterMoveW.update();
		}
		if (actionResultW.isVisible()) {
			actionResultW.update();
		}
		if (tgtW.isVisible()) {
			tgtW.update();
		}
		//
		if (itemChoiceUseW.isVisible()) {
			itemChoiceUseW.update();
		}
		if (itemCommitResultW.isVisible()) {
			itemCommitResultW.update();
		}
		//
		if (infoW.isVisible()) {
			infoW.update();
		}
		if (statusDescW.isVisible()) {
			statusDescW.update();
		}
		if (itemDescW.isVisible()) {
			itemDescW.update();
		}
		if (battleResultW.isVisible()) {
			battleResultW.update();
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		statusW.draw(g);
		//
		cmdW.draw(g);
		//
		afterMoveW.draw(g);
		actionResultW.draw(g);
		tgtW.draw(g);
		//
		itemChoiceUseW.draw(g);
		itemCommitResultW.draw(g);
		//
		infoW.draw(g);
		statusDescW.draw(g);
		itemDescW.draw(g);
		//
		battleResultW.draw(g);
	}

}
