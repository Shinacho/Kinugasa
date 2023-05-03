/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
package kinugasa.game.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.field4.FieldEvent;
import kinugasa.resource.Nameable;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_13:55:40<br>
 * @author Dra211<br>
 */
public class Text implements Nameable {

	public static Text collect(List<Text> t) {
		StringBuilder sb = new StringBuilder();
		for (Text tt : t) {
			sb.append(tt.getText()).append(Text.lineSep);
		}
		return new Text(sb.substring(0, sb.length() - 1));
	}

	public static Text collect(Text... t) {
		StringBuilder sb = new StringBuilder();
		for (Text tt : t) {
			sb.append(tt.getText()).append(Text.lineSep);
		}
		return new Text(sb.substring(0, sb.length() - 1));
	}

	private static Map<String, String> replaceMap = new HashMap<>();

	public static void setReplaceMap(Map<String, String> replaceMap) {
		Text.replaceMap = replaceMap;
	}

	public static Map<String, String> getReplaceMap() {
		return replaceMap;
	}

	//
	private final String name;
	private String text;
	private TimeCounter tc = new FrameTimeCounter(0);
	private int visibleIdx = 0;
	private String nextId;
	private static int autoId = 0;
	private static String lineSep = "/";
	private List<FieldEvent> events;

	public static void setLineSep(String lineSep) {
		Text.lineSep = lineSep;
	}

	public static String getLineSep() {
		return lineSep;
	}

	public Text() {
		this("");
	}

	public Text(String text) {
		this.name = autoId++ + "";
		setText(text);
	}

	public Text(String text, TimeCounter tc) {
		this.name = autoId++ + "";
		setText(text);
		this.tc = tc;
		visibleIdx = 0;
	}

	public Text(String name, String text, TimeCounter tc, int visibleIdx) {
		this.name = name;
		setText(text);
		this.tc = tc;
		this.visibleIdx = visibleIdx;
	}

	private void setText(String t) {
		this.text = t;
		for (Map.Entry<String, String> e : replaceMap.entrySet()) {
			text = text.replaceAll(e.getKey(), e.getValue());
		}

	}

	List<FieldEvent> getEvents() {
		return events;
	}

	void setEvents(List<FieldEvent> events) {
		Collections.sort(events);
		this.events = events;
	}

	public boolean isReaching() {
		if (visibleIdx >= text.length()) {
			return true;
		}
		if (!tc.isReaching()) {
			return false;
		}
		visibleIdx++;
		return visibleIdx >= text.length();
	}

	@Override
	public String getName() {
		return name;
	}

	public final String getText() {
		return text;
	}

	public String getVisibleText() {
		return visibleIdx >= text.length() ? text : text.substring(0, visibleIdx);
	}

	public TimeCounter getTc() {
		return tc;
	}

	public int getVisibleIdx() {
		return visibleIdx;
	}

	public void allText() {
		visibleIdx = text.length();
	}

	public boolean isAllVisible() {
		return visibleIdx >= text.length();
	}

	public void setTc(FrameTimeCounter tc) {
		this.tc = tc;
	}

	public void setVisibleIdx(int visibleIdx) {
		this.visibleIdx = visibleIdx;
	}

	public String getNextId() {
		return nextId;
	}

	public boolean hasNext() {
		return nextId != null;
	}

	public void setNextId(String nextId) {
		this.nextId = nextId;
	}

	@Override
	public String toString() {
		return "Text{" + "name=" + name + ", text=" + text + ", nextId=" + nextId + '}';
	}

	public void reset() {
		tc.reset();
		visibleIdx = 0;
	}

}
