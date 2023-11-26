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
package kinugasa.game.field4;

import kinugasa.game.system.NPCSprite;
import kinugasa.game.system.GameSystemI18NKeys;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.LoopCall;
import kinugasa.game.NoLoopCall;
import kinugasa.game.system.ActionStorage;
import kinugasa.game.system.Actor;
import kinugasa.game.system.EncountInfo;
import kinugasa.game.system.Flag;
import kinugasa.game.system.FlagStatus;
import kinugasa.game.system.FlagStorage;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.Item;
import kinugasa.game.system.ScriptFormatException;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.object.Drawable;
import kinugasa.object.FadeEffect;
import kinugasa.resource.Storage;
import kinugasa.resource.text.TextFile;
import kinugasa.resource.text.XMLFile;
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
	private List<NPCSprite> watchingPC = new ArrayList<>();
	private Node node;
	private LinkedList<FieldEvent> event = new LinkedList<>();
	private LinkedList<FieldEvent> prevEvent = new LinkedList<>();
	private boolean manual = false;
	//
	private Item item;
	private Storage<Flag> flags = new Storage<Flag>();

	public D2Idx getPrevEventLocation() {
		return prevEvent == null || prevEvent.isEmpty() ? null : prevEvent.get(0).getLocation();
	}

	public LinkedList<FieldEvent> getCurrentEvents() {
		return event;
	}

	public FieldEvent getCurrentEvent() {
		return currentEvent;
	}

	public LinkedList<FieldEvent> getPrevEvent() {
		return prevEvent;
	}

	public void setEvent(LinkedList<FieldEvent> event) {
		Collections.sort(event);
		prevEvent = (LinkedList<FieldEvent>) event.clone();
		this.event = event;
		if (!event.isEmpty()
				&& event.stream().anyMatch(p -> p.getEventType().toString().startsWith("MANUAL_EVENT"))) {
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
		setEvent((LinkedList<FieldEvent>) prevEvent.clone());
	}

	void setItem(String itemID) {
		Item i = ActionStorage.getInstance().itemOf(itemID);
		this.item = i;
	}

	public boolean hasItem() {
		return item != null;
	}

	public Item getItem() {
		return item;
	}

	//誰が持つ？ウインドウの表示
	public MessageWindow showItemGetMessageWindow() {
		float buffer = 24;
		float x = buffer;
		float y = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 2 + buffer * 2;
		float w = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() - (buffer * 2);
		float h = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 3;
		List<Text> options = new ArrayList<>();
		for (Actor pc : GameSystem.getInstance().getParty()) {
			options.add(new Text(pc.getVisibleName() + " / " + I18N.get(GameSystemI18NKeys.あとX個持てる, pc.getStatus().getItemBag().remainingSize() + "")));
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

	public boolean prevEventIsDead() {
		return prevEvent.stream().allMatch(p -> p.isDisposeWhenExec());
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
		if (event.isEmpty()) {
			return UserOperationRequire.END;
		}
		item = null;
		currentEvent = event.getFirst();
		event.removeFirst();
		if (currentEvent.getEventType() == FieldEventType.END) {
			event.clear();
			return UserOperationRequire.END;
		}
		if (GameSystem.isDebugMode()) {
			GameLog.print("start event : " + currentEvent);
		}
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
			if (currentEvent.getTerm() != null
					&& currentEvent.getTerm().stream().allMatch(p -> p.canDoThis(GameSystem.getInstance().getParty(), currentEvent))) {
				endEvent();
				return UserOperationRequire.CONTINUE;
			}
		}
		if (currentEvent.getEventType() == FieldEventType.END_AND_RESET_EVENT) {
			if (currentEvent.getTerm() != null
					&& currentEvent.getTerm().stream().allMatch(p -> p.canDoThis(GameSystem.getInstance().getParty(), currentEvent))) {
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

	List<NPCSprite> getWatchingPC() {
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

	public static void checkAllScripts(File dir) throws ScriptFormatException {
		GameLog.print("----------FIELD_EVENT_CHECK start --------------------");
		check(dir);
		GameLog.print("----------FIELD_EVENT_CHECK end --------------------");
	}

	private static void check(File f) {
		if (f.isDirectory()) {
			Arrays.stream(f.listFiles()).forEach(p -> check(p));
		}
		if (!f.getName().toLowerCase().endsWith(".xml")) {
			return;
		}
		//FILE
		new FieldEventParser("CHECK_EVENT", new D2Idx(0, 0), new XMLFile(f)).parse();
		GameLog.print("> " + f.getName() + " OK");
	}

}
