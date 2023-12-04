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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import kinugasa.game.GameLog;
import static kinugasa.game.I18N.get;
import kinugasa.game.field4.FieldEventParser;
import kinugasa.game.system.GameSystem;
import kinugasa.graphics.ImageUtil;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.Storage;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_13:56:40<br>
 * @author Shinacho<br>
 */
public class TextStorage extends Storage<Text> implements Nameable {

	private String name;
	private XMLFile data;

	public TextStorage(String name) {
		this.name = name;
	}

	public TextStorage(String name, XMLFile data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public String getName() {
		return name;
	}

	public TextStorage build() throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		if (!data.getFile().exists()) {
			throw new FileNotFoundException(data + " is not found");
		}

		XMLElement root = data.load().getFirst();
		for (XMLElement e : root.getElement("text")) {
			String id = e.getAttributes().get("id").getValue();
			String value = e.getValue().replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll(Text.getLineSep(), "").trim();
			String tcs = e.getAttributes().get("tc").getValue();

			List<Integer> tcvalues;
			if (tcs.contains(",")) {
				tcvalues = Arrays.stream(tcs.split(",")).map((s) -> Integer.parseInt(s)).collect(Collectors.toList());
			} else {
				tcvalues = Arrays.asList(Integer.parseInt(tcs));
			}
			FrameTimeCounter ftc = new FrameTimeCounter(tcvalues);
			Text t = new Text(id, value, ftc, 0);
			t.setVisibleIdx(0);
			if (e.getAttributes().contains("next")) {
				t.setNextId(e.getAttributes().get("next").getValue());
			}
			if (e.hasAttribute("eventScript")) {
				t.setEvents(FieldEventParser.parse(id, e.getAttributes().get("eventScript").getValue()));
			}
			if (e.hasAttribute("image")) {
				t.setImage(ImageUtil.load(e.getAttributes().get("image").getValue()));
			}
			if (!contains(t)) {
				add(t);
			}

		}
		for (XMLElement e : root.getElement("choice")) {
			if (e.getAttributes().contains("next")) {
				throw new IllegalXMLFormatException("Choice cannot have Next : " + e);
			}
			String id = e.getAttributes().get("id").getValue();
			String value = e.getAttributes().get("text").getValue().replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll(Text.getLineSep(), "").trim();
			String tcs = e.getAttributes().get("tc").getValue();
			List<Text> options = new ArrayList<>();
			for (XMLElement option : e.getElement("option")) {
				String oid = option.getAttributes().get("id").getValue();
				String ov = option.getValue();
				Text t = new Text(oid, ov, TimeCounter.TRUE, Integer.MAX_VALUE);
				if (option.getAttributes().contains("next")) {
					t.setNextId(option.getAttributes().get("next").getValue());
				}
				options.add(t);
			}
			List<Integer> tcvalues;
			if (tcs.contains(",")) {
				tcvalues = Arrays.stream(tcs.split(",")).map((s) -> Integer.parseInt(s)).collect(Collectors.toList());
			} else {
				tcvalues = Arrays.asList(Integer.parseInt(tcs));
			}
			FrameTimeCounter ftc = new FrameTimeCounter(tcvalues);
			Choice c = new Choice(options, id, value, ftc, 0);
			if (e.hasAttribute("image")) {
				c.setImage(ImageUtil.load(e.getAttributes().get("image").getValue()));
			}
			if (!contains(c)) {
				add(c);
			}
		}
		data.dispose();

		if (GameSystem.isDebugMode()) {
			GameLog.print(getAll());
		}

		return this;
	}

	public void dispose() {
		clear();
	}

	public MessageWindow createWindow(float x, float y, float w, float h, String id) throws NameNotFoundException {
		return new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(), this, get(id));
	}

	public MessageWindow createWindow(float x, float y, float w, float h, Text t) {
		return new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(), this, t);
	}

	public void resetAll() {
		for (Text t : this) {
			t.reset();
		}
	}
}
