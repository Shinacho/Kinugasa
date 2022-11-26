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

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageEditor;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.Storage;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundBuilder;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.FrameTimeCounter;

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

		//アニメーションのパース
		Map<String, Animation> animationMap = new HashMap<>();
		for (XMLElement e : root.getElement("animation")) {
			String name = e.getAttributes().get("name").getValue();
			int w = e.getAttributes().get("w").getIntValue();
			int h = e.getAttributes().get("h").getIntValue();
			BufferedImage[] images = new SpriteSheet(e.getAttributes().get("spriteSheet").getValue()).rows(0, w, h).images();
			float mg = e.getAttributes().get("mg").getFloatValue();
			images = ImageEditor.resizeAll(images, mg);
			//仮
			FrameTimeCounter tc = new FrameTimeCounter(e.getAttributes().get("tc").getIntValue());
			Animation a = new Animation(tc, images);
			a.setRepeat(false);
			animationMap.put(name, a);
		}

		//サウンドのパース
		Map<String, Sound> soundMap = new HashMap<>();
		for (XMLElement e : root.getElement("sound")) {
			String name = e.getAttributes().get("name").getValue();
			Sound s = new SoundBuilder(e.getAttributes().get("file").getValue()).builde();
			soundMap.put(name, s);
		}

		//攻撃のパース
		BattleActionType actionType = BattleActionType.ATTACK;
		for (XMLElement e : root.getElement("attack")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			BattleAction ba = new BattleAction(actionType, name, desc);
			if (e.hasAttribute("term")) {
				String[] termName = e.getAttributes().get("term").safeSplit(",");
				for (String s : termName) {
					ba.addTerm(BattleActionEventTermStorage.getInstance().get(s));
				}
			}
			if (e.hasAttribute("area")) {
				int area = e.getAttributes().get("area").getIntValue();
				ba.setArea(area);
			}
			if (e.hasAttribute("fieldUse")) {
				boolean fu = e.getAttributes().get("fieldUse").getBool();
				ba.setFieldUse(fu);
			}
			if (e.hasAttribute("spellTime")) {
				int st = e.getAttributes().get("spellTime").getIntValue();
				ba.setSpellTime(st);
			}
			if (e.hasAttribute("sound")) {
				ba.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("sort")) {
				ba.setSortKey(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("waitTime")) {
				ba.setWaitTime(new FrameTimeCounter(e.getAttributes().get("waitTime").getIntValue()));
			}
			for (XMLElement ee : e.getElement("event")) {
				BattleActionTargetType batt = BattleActionTargetType.valueOf(ee.getAttributes().get("batt").getValue());
				BattleActionTargetParameterType batpt = BattleActionTargetParameterType.valueOf(ee.getAttributes().get("batpt").getValue());
				BattleActionEvent event = new BattleActionEvent(batt, batpt);
				if (ee.hasAttribute("tgtName")) {
					event.setTargetName(ee.getAttributes().get("tgtName").getValue());
				}
				if (ee.hasAttribute("value")) {
					event.setBaseValue(ee.getAttributes().get("value").getFloatValue());
				}
				if (ee.hasAttribute("attr")) {
					event.setAttribute(AttributeKeyStorage.getInstance().get(ee.getAttributes().get("attr").getValue()));
				}
				if (ee.hasAttribute("p")) {
					event.setBaseP(ee.getAttributes().get("p").getFloatValue());
				}
				if (ee.hasAttribute("dct")) {
					event.setDamageCalcType(StatusDamageCalcType.valueOf(ee.getAttributes().get("dct").getValue()));
				}
				if(ee.hasAttribute("animation")) {
					String animationName = ee.getAttributes().get("animation").getValue();
					BattleActionAnimationTargetType animationTargetType = BattleActionAnimationTargetType.valueOf(ee.getAttributes().get("animationTgt").getValue());
					BattleActionAnimation animation = new BattleActionAnimation(new AnimationSprite(animationMap.get(animationName).clone()), animationTargetType);
					event.setAnimation(animation);
				}
				ba.addEvent(event);
			}
			add(ba);
		}

		//特殊攻撃のパース
		actionType = BattleActionType.SPECIAL_ATTACK;
		for (XMLElement e : root.getElement("spAttack")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			BattleAction ba = new BattleAction(actionType, name, desc);
			if (e.hasAttribute("term")) {
				String[] termName = e.getAttributes().get("term").safeSplit(",");
				for (String s : termName) {
					ba.addTerm(BattleActionEventTermStorage.getInstance().get(s));
				}
			}
			if (e.hasAttribute("area")) {
				int area = e.getAttributes().get("area").getIntValue();
				ba.setArea(area);
			}
			if (e.hasAttribute("fieldUse")) {
				boolean fu = e.getAttributes().get("fieldUse").getBool();
				ba.setFieldUse(fu);
			}
			if (e.hasAttribute("spellTime")) {
				int st = e.getAttributes().get("spellTime").getIntValue();
				ba.setSpellTime(st);
			}
			if (e.hasAttribute("sound")) {
				ba.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("sort")) {
				ba.setSortKey(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("waitTime")) {
				ba.setWaitTime(new FrameTimeCounter(e.getAttributes().get("waitTime").getIntValue()));
			}
			for (XMLElement ee : e.getElement("event")) {
				BattleActionTargetType batt = BattleActionTargetType.valueOf(ee.getAttributes().get("batt").getValue());
				BattleActionTargetParameterType batpt = BattleActionTargetParameterType.valueOf(ee.getAttributes().get("batpt").getValue());
				BattleActionEvent event = new BattleActionEvent(batt, batpt);
				if (ee.hasAttribute("tgtName")) {
					event.setTargetName(ee.getAttributes().get("tgtName").getValue());
				}
				if (ee.hasAttribute("value")) {
					event.setBaseValue(ee.getAttributes().get("value").getFloatValue());
				}
				if (ee.hasAttribute("attr")) {
					event.setAttribute(AttributeKeyStorage.getInstance().get(ee.getAttributes().get("attr").getValue()));
				}
				if (ee.hasAttribute("p")) {
					event.setBaseP(ee.getAttributes().get("p").getFloatValue());
				}
				if (ee.hasAttribute("dct")) {
					event.setDamageCalcType(StatusDamageCalcType.valueOf(ee.getAttributes().get("dct").getValue()));
				}
				if(ee.hasAttribute("animation")) {
					String animationName = ee.getAttributes().get("animation").getValue();
					BattleActionAnimationTargetType animationTargetType = BattleActionAnimationTargetType.valueOf(ee.getAttributes().get("animationTgt").getValue());
					BattleActionAnimation animation = new BattleActionAnimation(new AnimationSprite(animationMap.get(animationName).clone()), animationTargetType);
					event.setAnimation(animation);
				}
				ba.addEvent(event);
			}
			add(ba);
		}
		//魔法のパース
		actionType = BattleActionType.MAGIC;
		for (XMLElement e : root.getElement("magic")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			BattleAction ba = new BattleAction(actionType, name, desc);
			if (e.hasAttribute("term")) {
				String[] termName = e.getAttributes().get("term").safeSplit(",");
				for (String s : termName) {
					ba.addTerm(BattleActionEventTermStorage.getInstance().get(s));
				}
			}
			if (e.hasAttribute("area")) {
				int area = e.getAttributes().get("area").getIntValue();
				ba.setArea(area);
			}
			if (e.hasAttribute("fieldUse")) {
				boolean fu = e.getAttributes().get("fieldUse").getBool();
				ba.setFieldUse(fu);
			}
			if (e.hasAttribute("spellTime")) {
				int st = e.getAttributes().get("spellTime").getIntValue();
				ba.setSpellTime(st);
			}
			if (e.hasAttribute("sound")) {
				ba.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("sort")) {
				ba.setSortKey(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("waitTime")) {
				ba.setWaitTime(new FrameTimeCounter(e.getAttributes().get("waitTime").getIntValue()));
			}
			for (XMLElement ee : e.getElement("event")) {
				BattleActionTargetType batt = BattleActionTargetType.valueOf(ee.getAttributes().get("batt").getValue());
				BattleActionTargetParameterType batpt = BattleActionTargetParameterType.valueOf(ee.getAttributes().get("batpt").getValue());
				BattleActionEvent event = new BattleActionEvent(batt, batpt);
				if (ee.hasAttribute("tgtName")) {
					event.setTargetName(ee.getAttributes().get("tgtName").getValue());
				}
				if (ee.hasAttribute("value")) {
					event.setBaseValue(ee.getAttributes().get("value").getFloatValue());
				}
				if (ee.hasAttribute("attr")) {
					event.setAttribute(AttributeKeyStorage.getInstance().get(ee.getAttributes().get("attr").getValue()));
				}
				if (ee.hasAttribute("p")) {
					event.setBaseP(ee.getAttributes().get("p").getFloatValue());
				}
				if (ee.hasAttribute("dct")) {
					event.setDamageCalcType(StatusDamageCalcType.valueOf(ee.getAttributes().get("dct").getValue()));
				}
				if(ee.hasAttribute("animation")) {
					String animationName = ee.getAttributes().get("animation").getValue();
					BattleActionAnimationTargetType animationTargetType = BattleActionAnimationTargetType.valueOf(ee.getAttributes().get("animationTgt").getValue());
					BattleActionAnimation animation = new BattleActionAnimation(new AnimationSprite(animationMap.get(animationName).clone()), animationTargetType);
					event.setAnimation(animation);
				}
				ba.addEvent(event);
			}
			add(ba);
		}
		//その他行動のパース(ESCAPEイベント
		actionType = BattleActionType.OTHER;
		for (XMLElement e : root.getElement("other")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			BattleAction ba = new BattleAction(actionType, name, desc);
			if (e.hasAttribute("term")) {
				String[] termName = e.getAttributes().get("term").safeSplit(",");
				for (String s : termName) {
					ba.addTerm(BattleActionEventTermStorage.getInstance().get(s));
				}
			}
			if (e.hasAttribute("area")) {
				int area = e.getAttributes().get("area").getIntValue();
				ba.setArea(area);
			}
			if (e.hasAttribute("fieldUse")) {
				boolean fu = e.getAttributes().get("fieldUse").getBool();
				ba.setFieldUse(fu);
			}
			if (e.hasAttribute("spellTime")) {
				int st = e.getAttributes().get("spellTime").getIntValue();
				ba.setSpellTime(st);
			}
			if (e.hasAttribute("sound")) {
				ba.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("sort")) {
				ba.setSortKey(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("waitTime")) {
				ba.setWaitTime(new FrameTimeCounter(e.getAttributes().get("waitTime").getIntValue()));
			}
			for (XMLElement ee : e.getElement("event")) {
				BattleActionTargetType batt = BattleActionTargetType.valueOf(ee.getAttributes().get("batt").getValue());
				BattleActionTargetParameterType batpt = BattleActionTargetParameterType.valueOf(ee.getAttributes().get("batpt").getValue());
				BattleActionEvent event = new BattleActionEvent(batt, batpt);
				if (ee.hasAttribute("tgtName")) {
					event.setTargetName(ee.getAttributes().get("tgtName").getValue());
				}
				if (ee.hasAttribute("value")) {
					event.setBaseValue(ee.getAttributes().get("value").getFloatValue());
				}
				if (ee.hasAttribute("attr")) {
					event.setAttribute(AttributeKeyStorage.getInstance().get(ee.getAttributes().get("attr").getValue()));
				}
				if (ee.hasAttribute("p")) {
					event.setBaseP(ee.getAttributes().get("p").getFloatValue());
				}
				if (ee.hasAttribute("dct")) {
					event.setDamageCalcType(StatusDamageCalcType.valueOf(ee.getAttributes().get("dct").getValue()));
				}
				if(ee.hasAttribute("animation")) {
					String animationName = ee.getAttributes().get("animation").getValue();
					BattleActionAnimationTargetType animationTargetType = BattleActionAnimationTargetType.valueOf(ee.getAttributes().get("animationTgt").getValue());
					BattleActionAnimation animation = new BattleActionAnimation(new AnimationSprite(animationMap.get(animationName).clone()), animationTargetType);
					event.setAnimation(animation);
				}
				ba.addEvent(event);
			}
			add(ba);
		}

		printAll(System.out);

		file.dispose();
	}

}
