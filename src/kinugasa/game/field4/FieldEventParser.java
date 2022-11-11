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

import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;

/**
 *
 * @vesion 1.0.0 - 2022/11/11_10:05:39<br>
 * @author Dra211<br>
 */
public class FieldEventParser {

	private String name;
	private D2Idx idx;
	private XMLFile scriptData;

	public FieldEventParser(String name, D2Idx idx, XMLFile scriptData) {
		this.name = name;
		this.idx = idx;
		this.scriptData = scriptData;
	}

	public FieldEvent parse() {
		XMLElement e = scriptData.load().getFirst();
		FieldEvent result = null;

		if (e.hasElement("playSound")) {
			for (XMLElement ee : e.getElement("playSound")) {
				String mapName = ee.getAttributes().get("mapName").getValue();
				String soundName = ee.getAttributes().get("soundName").getValue();
				result = new SoundPlay(SoundStorage.getInstance().get(mapName).get(soundName), scriptData.getName(), idx);
			}
		}

		scriptData.dispose();
		if (result != null) {
			return result;
		}

		throw new InternalError("undefined script " + scriptData.getName());

	}
}

class SoundPlay extends FieldEvent {

	private Sound sound;

	public SoundPlay(Sound sound, String name, D2Idx location) {
		super(name, location);
		this.sound = sound;
	}

	@Override
	public void exec(FieldMap m) {
		sound.load().stopAndPlay();
	}

}
