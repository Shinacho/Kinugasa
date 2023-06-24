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
package kinugasa.game.field4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.system.ScriptFormatException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Storage;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;

/**
 *
 * @vesion 1.0.0 - 2022/11/11_10:05:39<br>
 * @author Shinacho<br>
 */
public class FieldEventParser {

	private String name;//イベントの名前
	private D2Idx idx;//イベントの位置
	private XMLFile scriptData;//スクリプトデータ

	public FieldEventParser(String name, D2Idx idx, XMLFile scriptData) {
		this.name = name;
		this.idx = idx;
		this.scriptData = scriptData;
	}

	public static List<FieldEvent> parse(String id, String fileName) {
		return new FieldEventParser(id, null, new XMLFile(fileName)).parse();
	}

	public List<FieldEvent> parse() {
		if (!scriptData.exists()) {
			throw new FileNotFoundException(scriptData.getFile());
		}
		XMLElement root = scriptData.load().getFirst();
		List<FieldEvent> result = new ArrayList<>();

		//Termのパース
		Storage<EventTerm> term = new Storage<>();
		for (XMLElement e : root.getElement("term")) {
			String name = e.getAttributes().get("name").getValue();
			EventTermType ett = e.getAttributes().get("ett").of(EventTermType.class);
			String storageName = e.getAttributes().get("stName").getValue();
			String tgtName = e.getAttributes().get("tgtName").getValue();
			String value = e.getAttributes().get("value").getValue();
			EventTerm t = new EventTerm(name, ett, storageName, tgtName, value);
			term.add(t);
		}

		//eventのパース
		int i = 0;
		boolean undead = root.hasAttribute("undead");
		for (XMLElement e : root.getElement("event")) {
			int pri = e.getAttributes().get("order").getIntValue();
			if (!Arrays.asList(FieldEventType.values()).stream().map(p -> p.toString()).collect(Collectors.toList()).contains(e.getAttributes().get("fet").getValue())) {
				throw new ScriptFormatException("fet is not found" + this + " / " + e.getAttributes().get("fet"));
			}
			FieldEventType fet = e.getAttributes().get("fet").of(FieldEventType.class);
			String storageName = null;
			if (e.hasAttribute("stName")) {
				storageName = e.getAttributes().get("stName").getValue();
			}
			String tgtName = null;
			if (e.hasAttribute("tgtName")) {
				tgtName = e.getAttributes().get("tgtName").getValue();
			}

			String value = null;
			if (e.hasAttribute("value")) {
				value = e.getAttributes().get("value").getValue();
			}

			String[] terms = new String[]{};
			if (e.hasAttribute("term")) {
				terms = e.getAttributes().get("term").safeSplit(",");
			}
			List<EventTerm> t = new ArrayList<>();
			if (terms.length != 0) {
				if (!Arrays.stream(terms).allMatch(p -> "".equals(p))) {
					try {
						t = term.getAll(terms);
					} catch (NameNotFoundException ex) {
						kinugasa.game.GameLog.print("UNDEFINED TERM NAME:" + terms);
						throw ex;
					}
				}
			}
			FieldEvent ee = new FieldEvent(name + "_" + i++, pri, idx, t, fet, storageName, tgtName, value);
			if (e.hasAttribute("undead") || undead) {
				ee.setDisposeWhenExec(false);
			}
			result.add(ee);
		}
		scriptData.dispose();
		Collections.sort(result);
		return result;
	}

	@Override
	public String toString() {
		return "FieldEventParser{" + "name=" + name + ", idx=" + idx + ", scriptData=" + scriptData + '}';
	}

}
