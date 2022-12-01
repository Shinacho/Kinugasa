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

	//コマンドを表示するメッセージウインドウ
	private BattleCommandMessageWindow commandWindow;
	//行動を表示するメッセージウインドウ
	private MessageWindow actionWindow;
	private FrameTimeCounter actionWindowVisibleTC;
	//行動できないなどの警告メッセージを出すウインドウ
	private MessageWindow infoWindow;
	//攻撃可能とかの小情報を出すウインドウ
	private MessageWindow tooltipWindow;
	//INFOの表示時間のマスタ
	private int infoWindowVisibleTime = 60;
	//INFOの表示時間カウンタ
	private FrameTimeCounter infoWindowVisibleTC;
	//ステータスペイン
	private StatusWindows statusWindows;
	//移動後行動用のウインドウ
	private AfterMoveActionMessageWindow afterMoveCommandWindow;
	//アイテムウインドウ
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
		//infoウインドウの表示時間判定
		if (infoWindowVisibleTC != null) {
			//info表示中
			infoWindow.update();
			if (infoWindowVisibleTC.isReaching()) {
				//表示終了
				closeInfoWindow();
			}
		}
		if (actionWindowVisibleTC != null) {
			//action表示中
			actionWindow.update();
			if (actionWindowVisibleTC.isReaching()) {
				//表示終了
				closeActionWindow();
			}
		}

		//コマンドウインドウの処理
		//内容の変更はBattleSystemから実行される
		commandWindow.update();

		//ステータスウインドウの処理
		//内容は自動的に変更される
		statusWindows.update();

		//ツールチップウインドウの更新
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

	//一時的にすべてのウインドウをクローズする。
	private boolean closeAll = false;

	public void closeAll() {
		closeAll = true;
	}

	//一時的にクローズしたウインドウをオープンする
	public void reOpenAll() {
		closeAll = false;
	}

	public void switchVisible() {
		closeAll = !closeAll;
	}

}
