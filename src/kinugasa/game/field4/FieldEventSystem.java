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
package kinugasa.game.field4;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LoopCall;
import kinugasa.game.NoLoopCall;
import kinugasa.game.system.EncountInfo;
import kinugasa.game.system.GameSystem;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.object.Drawable;
import kinugasa.object.FadeEffect;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/12/12_16:33:29<br>
 * @author Dra211<br>
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

	void setEvent(LinkedList<FieldEvent> event) {
		this.event = event;
		if (GameSystem.isDebugMode()) {
			for (FieldEvent e : event) {
				System.out.println(e);
			}
		}
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
		return !event.isEmpty();
	}

	@NoLoopCall
	public UserOperationRequire exec() {
		currentEvent = event.getFirst();
		event.removeFirst();
		if (GameSystem.isDebugMode()) {
			System.out.println("start event : " + currentEvent);
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
		float y = GameOption.getInstance().getWindowSize().height / 2 + buffer;
		float w = GameOption.getInstance().getWindowSize().width - (buffer * 2);
		float h = GameOption.getInstance().getWindowSize().height / 3;
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
