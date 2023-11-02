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
 * ESSはファイル内に複数または単一の敵出現セットを持ち、それらのうちどれかを返す機能を持っています。 敵出現セットには敵の他、BGMや出現率があります。
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

	// Pに基づいてエネミーセットストレージ内のエネミーセットを１つ選んで返す。
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

	// fileNameをロード
	public EnemySetStorage build() throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}
		XMLElement root = file.load().getFirst();

		for (XMLElement e : root.getElement("set")) {
			String id = e.getAttributes().get("name").getValue();
			float p = e.getAttributes().get("p").getFloatValue();
			String bgmName = null;
			if (e.getAttributes().contains("BGM")) {
				bgmName = e.getAttributes().get("BGM").getValue();
			}
			String winBgmName = null;
			if (e.getAttributes().contains("winBGM")) {
				winBgmName = e.getAttributes().get("winBGM").getValue();
			}
			BGMMode bgmMode = BGMMode.NOTHING;
			if (e.hasAttribute("bgmMode")) {
				bgmMode = e.getAttributes().get("bgmMode").of(BGMMode.class);
			}

			String win = e.getAttributes().get("win").getValue();
			String lose = e.getAttributes().get("lose").getValue();
			List<EnemyBlueprint> list = new ArrayList<>();
			for (XMLElement ee : e.getElement("enemy")) {
				list.add(new EnemyBlueprint(ee.getAttributes().get("fileName").getValue()));
			}
			EnemySet es = new EnemySet(id, list, p, bgmName, bgmMode, winBgmName, win, lose);
			add(es);
		}

		file.dispose();

		return this;
	}

	public void dispose() {
		clear();
	}
}
