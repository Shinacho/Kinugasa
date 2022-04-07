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
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_13:56:40<br>
 * @author Dra211<br>
 */
public class TextStorage extends Storage<Text> implements XMLFileSupport {

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.getFile().exists()) {
			throw new FileNotFoundException(file + " is not found");
		}

		XMLElement root = file.load().getFirst();
		String lineSep = root.getAttributes().get("lSep").getValue();
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
			add(t);

		}
		GameLog.printIfUsing(Level.ALL, getAll().toString());
		printAll(System.out);
	}

	public MessageWindow createWindow(float x, float y, float w, float h, String id) throws NameNotFoundException {
		return new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(), this, get(id));
	}

	public MessageWindow createWindow(float x, float y, float w, float h, Text t) {
		return new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(), this, t);
	}
}
