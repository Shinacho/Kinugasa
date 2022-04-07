/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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
package kinugasa.game.rpgui;

import java.util.Arrays;
import kinugasa.resource.InputStatus;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.util.TimeCounter;

/**
 * テキストは、メッセージウインドウに表示するひとつのテキストメッセージを表します.
 * <br>
 *
 * @version 1.0.0 - 2015/06/18<br>
 * @author Dra<br>
 * <br>
 */
public class Text implements Nameable, Comparable<Text> {

	private static int instanceCount = 0;
	private final String id;
	private Sound sound;
	private TimeCounter timeCounter;
	private String message;
	private String[] nextId;

	private String[] splitResult = null;
	private int prevSplitLength = -1;
	private int visibleTextLength;

	public Text() {
		this(Integer.toString(instanceCount++), null, TimeCounter.TRUE, "");
	}

	public Text(String message, TimeCounter tc) {
		this(Integer.toString(instanceCount++), null, tc, message);
	}

	public Text(String id, Sound sound, TimeCounter timeCounter, String message, String... nextId) {
		this.id = id;
		this.sound = sound;
		this.timeCounter = timeCounter;
		this.message = message;
		this.nextId = nextId;
	}

	public Text(String id, Sound sound, TimeCounter timeCounter, String message) {
		this.id = id;
		this.sound = sound;
		this.timeCounter = timeCounter;
		this.message = message;
		this.nextId = null;
	}

	public Text(String id, String message, TimeCounter timeCounter, String... nextId) {
		this.id = id;
		this.sound = null;
		this.timeCounter = timeCounter;
		this.message = message;
		this.nextId = nextId;
	}

	public Text(String id, String message, TimeCounter timeCounter) {
		this.id = id;
		this.sound = null;
		this.timeCounter = timeCounter;
		this.message = message;
		this.nextId = null;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public int compareTo(Text o) {
		return this.id.compareTo(o.id);
	}

	public Sound getSound() {
		return sound;
	}

	public String getMessage() {
		return message;
	}

	public String[] getNextId() {
		return nextId;
	}

	public boolean hasNext() {
		return nextId != null;
	}

	public int getNextIdNum() {
		return nextId.length;
	}

	public boolean hasSound() {
		return sound != null;
	}

	public TimeCounter getTimeCounter() {
		return timeCounter;
	}

	public void setSound(Sound sound) {
		this.sound = sound;
	}

	public void setTimeCounter(TimeCounter timeCounter) {
		this.timeCounter = timeCounter;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setNextId(String[] nextId) {
		this.nextId = nextId;
	}

	private static String BR = "\n";

	public static String getBR() {
		return BR;
	}

	public static void setBR(String BR) {
		Text.BR = BR;
	}

	public String[] split(int length) {
		if (prevSplitLength != visibleTextLength) {
			StringBuilder val = new StringBuilder(message.substring(0, visibleTextLength));
			for (int i = length; i < val.length(); i += length) {
				val.insert(i, BR);
			}
			prevSplitLength = visibleTextLength;
			return splitResult = val.toString().split(BR);
		}
		return splitResult;
	}

	public String[] split(String val) {
		return message.substring(0, visibleTextLength).split(val);
	}

	public boolean isReaching() {
		return timeCounter.isReaching();
	}

	public void nextChar() {
		if (isVisibleAllChar()) {
			return;
		}
		if (sound != null) {
			if (!(sound.getStatus() == InputStatus.LOADED)) {
				sound.load();
			}
			sound.again();
		}
		visibleTextLength++;
	}

	public void prevChar() {
		visibleTextLength -= visibleTextLength == 0 ? 0 : 1;
	}

	public void allChar() {
		visibleTextLength = message.length();
	}

	public Text reset() {
		timeCounter.reset();
		visibleTextLength = 0;
		return this;
	}

	public boolean isVisibleAllChar() {
		return visibleTextLength == message.length();
	}

	public void setVisibleTextLength(int visibleTextLength) {
		this.visibleTextLength = visibleTextLength;
	}

	public int getVisibleTextLength() {
		return visibleTextLength;
	}

	public String getVisibleText() {
		return message.substring(0, visibleTextLength);
	}

	public boolean isReachingNextCher() {
		if (isReaching()) {
			nextChar();
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Text{" + "id=" + id + ", sound=" + sound + ", message=" + message + ", nextId=" + Arrays.toString(nextId) + '}';
	}
}
