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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.List;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_14:43:14<br>
 * @author Dra211<br>
 */
public class BattleActionStorage extends Storage<BattleAction> implements XMLFileSupport {

	private static final BattleActionStorage INSTANCE = new BattleActionStorage();

	private BattleActionStorage() {
	}

	public static BattleActionStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}

		XMLElement root = file.load().getFirst();
		//発動条件のパース
		for (XMLElement e : root.getElement("term")) {
			String name = e.getAttributes().get("name").getValue();
			BattleActionEventTermType type = BattleActionEventTermType.valueOf(e.getAttributes().get("baett").getValue());
			String value = e.getAttributes().get("value").getValue();
			BattleActionEventTermStorage.getInstance().add(new BattleActionEventTerm(name, type, value));
		}
		BattleActionEventTermStorage.getInstance().printAll(System.out);

		//攻撃のパース
		for (XMLElement e : root.getElement("attack")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("name").getValue();
			List<BattleActionEventTerm> term = new ArrayList<>();
			if (e.getAttributes().contains("term")) {
				String termVal = e.getAttributes().get("term").getValue();
				if (termVal.contains(",")) {
					for (String s : termVal.split(",")) {
						term.add(BattleActionEventTermStorage.getInstance().get(s));
					}
				} else {
					term.add(BattleActionEventTermStorage.getInstance().get(termVal));
				}
			}
			List<BattleActionEvent> events = new ArrayList<>();
			for (XMLElement ee : e.getElement("event")) {
				BattleActionTargetType batt = BattleActionTargetType.valueOf(ee.getAttributes().get("batt").getValue());
				BattleActionTargetParameterType batpt = BattleActionTargetParameterType.valueOf(ee.getAttributes().get("batpt").getValue());
				String targetName = ee.getAttributes().get("tgtName").getValue();
				AttributeKey attrKey = ee.getAttributes().contains("attr")
						? AttributeKeyStorage.getInstance().get(ee.getAttributes().get("attr").getValue())
						: null;
				float value = ee.getAttributes().get("value").getFloatValue();
				float p = ee.getAttributes().get("p").getFloatValue();
				DamageCalcType dct = DamageCalcType.valueOf(ee.getAttributes().get("dct").getValue());
				events.add(new BattleActionEvent(batt, batpt, targetName, value, attrKey, p, dct));
			}
			getInstance().add(new BattleAction(name, desc, events, term));

		}

		//特殊攻撃のパース
		for (XMLElement e : root.getElement("spAttack")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("name").getValue();
			List<BattleActionEventTerm> term = new ArrayList<>();
			if (e.getAttributes().contains("term")) {
				String termVal = e.getAttributes().get("term").getValue();
				if (termVal.contains(",")) {
					for (String s : termVal.split(",")) {
						term.add(BattleActionEventTermStorage.getInstance().get(s));
					}
				} else {
					term.add(BattleActionEventTermStorage.getInstance().get(termVal));
				}
			}
			List<BattleActionEvent> events = new ArrayList<>();
			for (XMLElement ee : e.getElement("event")) {
				BattleActionTargetType batt = BattleActionTargetType.valueOf(ee.getAttributes().get("batt").getValue());
				BattleActionTargetParameterType batpt = BattleActionTargetParameterType.valueOf(ee.getAttributes().get("batpt").getValue());
				String targetName = ee.getAttributes().get("tgtName").getValue();
				AttributeKey attrKey = ee.getAttributes().contains("attr")
						? AttributeKeyStorage.getInstance().get(ee.getAttributes().get("attr").getValue())
						: null;
				float value = ee.getAttributes().get("value").getFloatValue();
				float p = ee.getAttributes().get("p").getFloatValue();
				DamageCalcType dct = DamageCalcType.valueOf(ee.getAttributes().get("dct").getValue());
				events.add(new BattleActionEvent(batt, batpt, targetName, value, attrKey, p, dct));
			}
			getInstance().add(new BattleAction(name, desc, events, term));

		}
		//魔法のパース
		for (XMLElement e : root.getElement("magic")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("name").getValue();
			List<BattleActionEventTerm> term = new ArrayList<>();
			if (e.getAttributes().contains("term")) {
				String termVal = e.getAttributes().get("term").getValue();
				if (termVal.contains(",")) {
					for (String s : termVal.split(",")) {
						term.add(BattleActionEventTermStorage.getInstance().get(s));
					}
				} else {
					term.add(BattleActionEventTermStorage.getInstance().get(termVal));
				}
			}
			List<BattleActionEvent> events = new ArrayList<>();
			for (XMLElement ee : e.getElement("event")) {
				BattleActionTargetType batt = BattleActionTargetType.valueOf(ee.getAttributes().get("batt").getValue());
				BattleActionTargetParameterType batpt = BattleActionTargetParameterType.valueOf(ee.getAttributes().get("batpt").getValue());
				String targetName = ee.getAttributes().get("tgtName").getValue();
				AttributeKey attrKey = ee.getAttributes().contains("attr")
						? AttributeKeyStorage.getInstance().get(ee.getAttributes().get("attr").getValue())
						: null;
				float value = ee.getAttributes().get("value").getFloatValue();
				float p = ee.getAttributes().get("p").getFloatValue();
				DamageCalcType dct = DamageCalcType.valueOf(ee.getAttributes().get("dct").getValue());
				events.add(new BattleActionEvent(batt, batpt, targetName, value, attrKey, p, dct));
			}
			getInstance().add(new BattleAction(name, desc, events, term));

		}
		printAll(System.out);
		
		file.dispose();
	}

}
