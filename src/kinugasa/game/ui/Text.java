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
import kinugasa.game.I18N;
import kinugasa.game.Nullable;
import kinugasa.game.field4.FieldEvent;
import kinugasa.game.system.GameSystemException;
import kinugasa.resource.Nameable;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_13:55:40<br>
 * @author Shinacho<br>
 */
public sealed class Text implements Nameable permits Choice {

	public static Text empty() {
		return noI18N("");
	}

	public static Text collect(List<Text> t) {
		StringBuilder sb = new StringBuilder();
		for (Text tt : t) {
			sb.append(tt.getText()).append(Text.lineSep);
		}
		return Text.noI18N(sb.substring(0, sb.length() - 1));
	}

	public static Text collect(Text... t) {
		StringBuilder sb = new StringBuilder();
		for (Text tt : t) {
			sb.append(tt.getText()).append(Text.lineSep);
		}
		return Text.noI18N(sb.substring(0, sb.length() - 1));
	}

	public static List<Text> split(Text t) {
		List<Text> r = new ArrayList<>();
		for (String v : t.getText().split(lineSep)) {
			r.add(Text.noI18N(v));
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
	protected String name;
	protected String text;
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
		name = "TEXT_" + autoId++;
		text = "";
	}

	public static Text noI18N(String text) {
		if (text == null) {
			throw new GameSystemException("text is null, use Text.empty");
		}
		Text t = new Text();
		t.text = text;
		return t;
	}

	public static Text noI18N(String id, String text) {
		if (text == null) {
			throw new GameSystemException("text is null, use Text.empty");
		}
		Text t = new Text();
		t.name = id;
		t.text = text;
		return t;
	}

	public Text(String text) {
		this.name = "TEXT_" + autoId++;
		if (text == null) {
			throw new GameSystemException("text is null, use Text.empty");
		}
		setText(text);
	}

	public Text(String text, TimeCounter tc) {
		this.name = "TEXT_" + autoId++;
		if (text == null) {
			throw new GameSystemException("text is null, use Text.empty");
		}
		setText(text);
		this.tc = tc;
		visibleIdx = 0;
	}

	public Text(String name, TimeCounter tc, int visibleIdx) {
		this.name = name;
		this.tc = tc;
		this.visibleIdx = visibleIdx;
	}

	public Text(String name, String text, TimeCounter tc, int visibleIdx) {
		this.name = name;
		if (text == null) {
			throw new GameSystemException("text is null, use Text.empty");
		}
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

	public void setTextNoI18N(String t) {
		if (t == null) {
			throw new GameSystemException("text is null, use Text.empty");
		}
		this.text = t;
		for (Map.Entry<String, String> e : replaceMap.entrySet()) {
			text = text.replaceAll(e.getKey(), e.getValue());
		}
	}

	private void setText(String t) {
		if (t == null) {
			throw new GameSystemException("text is null, use Text.empty");
		}
		this.text = I18N.get(t);
		for (Map.Entry<String, String> e : replaceMap.entrySet()) {
			text = text.replaceAll(e.getKey(), e.getValue());
		}
	}

	List<FieldEvent> getEvents() {
		return events;
	}

	public void setEvents(List<FieldEvent> events) {
		Collections.sort(events);
		this.events = events;
	}

	public boolean isReaching() {
		if (text == null || text.isEmpty()) {
			return true;
		}
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
		if (text == null || text.isEmpty()) {
			return "";
		}
		return text;
	}

	public String getVisibleText() {
		if (text == null || text.isEmpty()) {
			return "";
		}
		return visibleIdx >= text.length() ? text : text.substring(0, visibleIdx);
	}

	public TimeCounter getTc() {
		return tc;
	}

	public int getVisibleIdx() {
		return visibleIdx;
	}

	public void allText() {
		if (text != null) {
			visibleIdx = text.length();
		}
	}

	public boolean isAllVisible() {
		if (text == null || text.isEmpty()) {
			return true;
		}
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
