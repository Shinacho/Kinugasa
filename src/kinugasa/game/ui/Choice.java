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
package kinugasa.game.ui;

import java.util.List;
import kinugasa.resource.NameNotFoundException;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_9:33:24<br>
 * @author Dra211<br>
 */
public class Choice extends Text {

	private List<Text> options;

	public Choice(List<Text> options, String name, String text) {
		super(name, text, TimeCounter.TRUE, Integer.MAX_VALUE);
		this.options = options;
	}

	public Choice(List<Text> options, String name, String text, FrameTimeCounter tc, int visibleIdx) {
		super(name, text, tc, visibleIdx);
		this.options = options;
	}

	public List<Text> getOptions() {
		return options;
	}

	public Text getOption(String name) throws NameNotFoundException {
		for (Text t : options) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		throw new NameNotFoundException(name + " is not found");
	}

}
