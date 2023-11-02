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
package kinugasa.game.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.Nullable;
import kinugasa.game.field4.FieldEvent;
import kinugasa.resource.Nameable;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_13:55:40<br>
 * @author Shinacho<br>
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

	public static List<Text> split(Text t) {
		List<Text> r = new ArrayList<>();
		for (String v : t.getText().split(lineSep)) {
			r.add(new Text(v));
		}
		return r;
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
	private BufferedImage image;

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
		this.name = "TEXT_" + autoId++;
		setText(text);
	}

	public Text(String text, TimeCounter tc) {
		this.name = "TEXT_" + autoId++;
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

	public boolean hasImage() {
		return image != null;
	}

	@Nullable
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
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
		return "Text{" + "name=" + name + ", nextId=" + nextId + '}';
	}

	public void reset() {
		tc.reset();
		visibleIdx = 0;
	}

}
