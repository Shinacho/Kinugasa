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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import kinugasa.game.GameLog;
import kinugasa.game.field4.FieldEventParser;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_13:56:40<br>
 * @author Dra211<br>
 */
public class TextStorage extends Storage<Text> implements Nameable {

	private String name;
	private XMLFile data;

	public TextStorage() {
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
			String value = e.getValue();
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
			if (!contains(t)) {
				add(t);
			}

		}
		for (XMLElement e : root.getElement("choice")) {
			if (e.getAttributes().contains("next")) {
				throw new IllegalXMLFormatException("Choice cannot have Next : " + e);
			}
			String id = e.getAttributes().get("id").getValue();
			String value = e.getAttributes().get("text").getValue();
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
			if (!contains(c)) {
				add(c);
			}
		}
		data.dispose();

		GameLog.printIfUsing(Level.ALL, getAll().toString());
		printAll(System.out);

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
