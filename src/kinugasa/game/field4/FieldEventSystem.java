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
package kinugasa.game.field4;

import kinugasa.game.system.GameSystemI18NKeys;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.LoopCall;
import kinugasa.game.NoLoopCall;
import kinugasa.game.system.EncountInfo;
import kinugasa.game.system.Flag;
import kinugasa.game.system.FlagStatus;
import kinugasa.game.system.FlagStorage;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.Item;
import kinugasa.game.system.ItemStorage;
import kinugasa.game.system.PlayerCharacter;
import kinugasa.game.system.ScriptFormatException;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.object.Drawable;
import kinugasa.object.FadeEffect;
import kinugasa.resource.Storage;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/12/12_16:33:29<br>
 * @author Shinacho<br>
 */
public class FieldEventSystem implements Drawable {

	private static final FieldEventSystem INSTANCE = new FieldEventSystem();

	private FieldEventSystem() {
	}

	public static FieldEventSystem getInstance() {
		return INSTANCE;
	}

	//
	private TextStorage textStorage;
	private Text text;
	private String nextMapName;
	private EncountInfo encountInfo;
	private boolean userOperation = true;
	private FrameTimeCounter waitTime;
	//
	private boolean executing = false;
	private FieldEvent currentEvent;
	private List<NPC> watchingPC = new ArrayList<>();
	private Node node;
	private LinkedList<FieldEvent> event = new LinkedList<>();
	private LinkedList<FieldEvent> prevEvent = new LinkedList<>();
	private boolean manual = false;
	//
	private Item item;
	private Storage<Flag> flags = new Storage<Flag>();

	public void setEvent(LinkedList<FieldEvent> event) {
		Collections.sort(event);
		prevEvent = (LinkedList<FieldEvent>) event.clone();
		this.event = event;
		if (!event.isEmpty() && event.stream().anyMatch(p -> p.getEventType() == FieldEventType.MANUAL_EVENT)) {
			manual = true;
		}
		if (event.isEmpty()) {
			manual = false;
		}
		//IFのterm設定
		if (event.stream().filter(p -> p.getEventType() == FieldEventType.IF).count()
				!= event.stream().filter(p -> p.getEventType() == FieldEventType.END_IF).count()) {
			throw new ScriptFormatException("IF - END_IF is missmatch:" + event);
		}
		int i = 0, j = 0;
		while (true) {
			for (; i < event.size() && event.get(i).getEventType() != FieldEventType.IF; i++);
			for (j = i + 1; j < event.size() && event.get(j).getEventType() != FieldEventType.END_IF; j++);
			if (i < event.size()) {
				List<EventTerm> term = event.get(i).getTerm();
				for (; i <= j; i++) {
					event.get(i).setTerm(term);
				}
			}
			if (i >= event.size()) {
				break;
			}
		}
	}

	Storage<Flag> getFlags() {
		return flags;
	}

	void setFlag(String flagName, FlagStatus v) {
		//仮ストレージにフラグを追加
		FlagStorage fs = FlagStorage.getInstance();
		if (!flags.contains(flagName)) {
			flags.add(new Flag(flagName));
		}
		flags.get(flagName).set(v);
	}

	public void commitFlags() {
		FlagStorage.getInstance().update(flags);
		//フラグ自体の更新
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("tmp flags commit is done.");
		}
		clearTmpFlags();
	}

	public void clearTmpFlags() {
		flags.clear();
	}

	public void reset() {
		reset = true;
		setEvent(prevEvent);
	}

	void setItem(String item) {
		Item i = ItemStorage.getInstance().get(item);
		if (i == null) {
			throw new ScriptFormatException("item is null : " + item);
		}
		this.item = i;
	}

	public boolean hasItem() {
		return item != null;
	}

	public Item getItem() {
		return item == null ? null : item.clone();
	}

	//誰が持つ？ウインドウの表示
	public MessageWindow showItemGetMessageWindow() {
		float buffer = 24;
		float x = buffer;
		float y = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 2 + buffer * 2;
		float w = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() - (buffer * 2);
		float h = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 3;
		List<Text> options = new ArrayList<>();
		for (PlayerCharacter pc : GameSystem.getInstance().getParty()) {
			options.add(new Text(pc.getName() + " / " + I18N.get(GameSystemI18NKeys.あとX個持てる, pc.getStatus().getItemBag().remainingSize() + "")));
		}
		options.add(new Text(I18N.get(GameSystemI18NKeys.諦める)));

		Choice c = new Choice(options, "",
				I18N.get(GameSystemI18NKeys.Xを手に入れた誰が持つ, item.getVisibleName()), new FrameTimeCounter(0), 0);
		c.allText();

		MessageWindow mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(), null, c);
		FieldMap.getCurrentInstance().setMW(mw);
		return mw;
	}

	public boolean hasManualEvent() {
		if (event == null || event.isEmpty() || reset) {
			return false;
		}
		return event.stream().anyMatch(p -> p.getEventType() == FieldEventType.MANUAL_EVENT);
	}

	void setUserOperation(boolean userOperation) {
		this.userOperation = userOperation;
	}

	void setNode(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

	public boolean isUserOperation() {
		return userOperation;
	}

	public boolean hasEvent() {
		if (reset) {
			reset = false;
			return false;
		}
		return !event.isEmpty();
	}

	public boolean isLastEvent() {
		return event.isEmpty();
	}

	public UserOperationRequire manualExec() {
		return exec();
	}

	public boolean isManual() {
		return manual;
	}

	public void endEvent() {
		event.clear();
		item = null;
		executing = false;
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("--------ManualEndEvent");
		}
	}

	private boolean reset = false;

	@NoLoopCall
	public UserOperationRequire exec() {
		item = null;
		currentEvent = event.getFirst();
		event.removeFirst();
//		System.out.print("start event : " + currentEvent);
		GameLog.print("start event : " + currentEvent);
		if (currentEvent.getEventType() == FieldEventType.IF) {
			return UserOperationRequire.CONTINUE;
		}
		if (currentEvent.getEventType() == FieldEventType.END_IF) {
			return UserOperationRequire.CONTINUE;
		}
		if (currentEvent.getEventType() == FieldEventType.COMMIT_FLG) {
			commitFlags();
			return UserOperationRequire.CONTINUE;
		}
		if (currentEvent.getEventType() == FieldEventType.END) {
			if (currentEvent.getTerm() != null && currentEvent.getTerm().stream().allMatch(p -> p.canDoThis(GameSystem.getInstance().getPartyStatus(), currentEvent))) {
				endEvent();
				return UserOperationRequire.CONTINUE;
			}
		}
		if (currentEvent.getEventType() == FieldEventType.END_AND_RESET_EVENT) {
			if (currentEvent.getTerm() != null && currentEvent.getTerm().stream().allMatch(p -> p.canDoThis(GameSystem.getInstance().getPartyStatus(), currentEvent))) {
				reset = true;
				endEvent();
				reset();
				return UserOperationRequire.CONTINUE;
			}
		}
		UserOperationRequire res = currentEvent.exec(FieldMap.getCurrentInstance());
		if (res == UserOperationRequire.WAIT_FOR_EVENT) {
			executing = true;
		}
		if (currentEvent.getEventType() == FieldEventType.FADE_IN) {
			blackout = false;
		}
		return res;
	}

	List<NPC> getWatchingPC() {
		return watchingPC;
	}

	@Deprecated
	public boolean isExecuting() {
		return executing;
	}

	@LoopCall
	public void update() {
		//イベント実行中の更新
		if (!executing) {
			return;
		}
		if (currentEvent == null) {
			return;
		}
		switch (currentEvent.getEventType()) {
			case MOVE_CAMERA_2:
			case MOVE_CAMERA_4:
			case MOVE_CAMERA_6:
			case MOVE_CAMERA_8:
				if (!FieldMapCamera.getInstance().hasTarget()) {
					executing = false;
				}
				break;
			case WAIT:
				if (waitTime.isReaching()) {
					waitTime = null;
					executing = false;
				}
				break;
			case NPC_MOVE_AND_WAIT_THAT:
				boolean allEnd = watchingPC.stream().allMatch(p -> p.isMoveStop());
				if (allEnd) {
					executing = false;
					watchingPC.clear();
				}
				break;
			case FADE_IN:
				if (effect.isEnded()) {
					executing = false;
				}
				break;
			case FADE_OUT:
				if (effect.isEnded()) {
					executing = false;
					blackout = true;
				}
				break;
			default:
				break;
		}

		//カメラムーブの完了
	}

	public MessageWindow showMessageWindow() {
		float buffer = 24;
		float x = buffer;
		float y = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 2 + buffer * 2;
		float w = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() - (buffer * 2);
		float h = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 3;
		text.reset();

		MessageWindow mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(), textStorage, text);
		FieldMap.getCurrentInstance().setMW(mw);
		return mw;
	}

	void setWaitTime(FrameTimeCounter waitTime) {
		this.waitTime = waitTime;
	}

	public boolean waitTimeEnded() {
		return waitTime == null;
	}

	void setNextMapName(String nextMapName) {
		this.nextMapName = nextMapName;
	}

	public String getNextMapName() {
		return nextMapName;
	}

	void setEncountInfo(EncountInfo encountInfo) {
		this.encountInfo = encountInfo;
	}

	public EncountInfo getEncountInfo() {
		return encountInfo;
	}

	void setText(Text text) {
		this.text = text;
	}

	void setTextStorage(TextStorage textStorage) {
		this.textStorage = textStorage;
	}

	public Text getText() {
		return text;
	}

	public TextStorage getTextStorage() {
		return textStorage;
	}

	private FadeEffect effect;
	private boolean blackout;

	void setEffect(FadeEffect effect) {
		this.effect = effect;
		executing = true;
	}

	void setBlackout(boolean blackout) {
		this.blackout = blackout;
		executing = true;
	}

	public boolean isBlackout() {
		return blackout;
	}

	public FadeEffect getEffect() {
		return effect;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (effect != null) {
			effect.draw(g);
		}
		if (blackout) {
			g.setColor(Color.BLACK);
			int w = GameOption.getInstance().getWindowSize().width;
			int h = GameOption.getInstance().getWindowSize().height;
			g.fillRect(0, 0, w, h);
		}
	}

}
