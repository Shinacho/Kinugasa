/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
package kinugasa.game.system;

import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kinugasa.game.field4.BGMMode;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/21_21:39:55<br>
 * @author Shinacho<br>
 */
public class EnemySetStorage extends Storage<EnemySet> implements Nameable {

	private String name;
	private String fileName;

	public EnemySetStorage(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}

	@Override
	public String getName() {
		return name;
	}

	public EnemySet get() {
		List<EnemySet> list = asList();
		Collections.sort(list);
		Collections.reverse(list);
		for (EnemySet e : list) {
			if (Random.percent(e.getP())) {
				return e;
			}
		}
		return random();
	}

	public EnemySetStorage build() throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}
		XMLElement root = file.load().getFirst();

		for (XMLElement e : root.getElement("set")) {
			String name = e.getAttributes().get("name").getValue();
			float p = e.getAttributes().get("p").getFloatValue();
			String bgmMapName = null, bgmName = null;
			if (e.getAttributes().contains("BGM")) {
				bgmMapName = e.getAttributes().get("BGM").getValue().split("/")[0];
				bgmName = e.getAttributes().get("BGM").getValue().split("/")[1];
			}
			String winBgmMapName = null, winBgmName = null;
			if (e.getAttributes().contains("winBGM")) {
				winBgmMapName = e.getAttributes().get("winBGM").getValue().split("/")[0];
				winBgmName = e.getAttributes().get("winBGM").getValue().split("/")[1];
			}
			BGMMode bgmMode = BGMMode.NOTHING;
			if (e.hasAttribute("bgmMode")) {
				bgmMode = e.getAttributes().get("bgmMode").of(BGMMode.class);
			}

			String win = e.getAttributes().get("win").getValue();
			String lose = e.getAttributes().get("lose").getValue();
			List<EnemyBlueprint> list = new ArrayList<>();
			for (XMLElement ee : e.getElement("enemy")) {
				list.add(EnemyStorage.getInstance().getByVisibleName(ee.getAttributes().get("name").getValue()));
			}
			EnemySet es = new EnemySet(name, list, p, bgmMapName, bgmName, bgmMode, winBgmMapName, winBgmName, win, lose);
			add(es);
		}

		file.dispose();

		return this;
	}

	public void dispose() {
		clear();
	}
}
