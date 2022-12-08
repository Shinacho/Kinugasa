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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.GameOption;
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
public class ActionStorage extends Storage<CmdAction> implements XMLFileSupport {

	private static final ActionStorage INSTANCE = new ActionStorage();

	private ActionStorage() {
	}

	public static ActionStorage getInstance() {
		return INSTANCE;
	}
	Map<String, Animation> animationMap = new HashMap<>();

	private ActionEvent parseEvent(XMLElement e) {
		TargetType tt = e.getAttributes().get("tt").of(TargetType.class);
		ParameterType pt = e.getAttributes().get("pt").of(ParameterType.class);
		ActionEvent event = new ActionEvent(tt, pt);
		if (e.hasAttribute("p")) {
			event.setP(e.getAttributes().get("p").getFloatValue());
		}
		if (e.hasAttribute("tgtName")) {
			event.setTgtName(e.getAttributes().get("tgtName").getValue());
		}
		if (e.hasAttribute("attr")) {
			event.setAttr(AttributeKeyStorage.getInstance().get(e.getAttributes().get("attr").getValue()));
		}
		if (e.hasAttribute("value")) {
			event.setValue(e.getAttributes().get("value").getFloatValue());
		}
		if (e.hasAttribute("spread")) {
			event.setSpread(e.getAttributes().get("spread").getFloatValue());
		}
		if (e.hasAttribute("dct")) {
			event.setDamageCalcType(e.getAttributes().get("dct").of(StatusDamageCalcType.class));
		}
		if (e.hasAttribute("animation")) {
			event.setAnimation(animationMap.get(e.getAttributes().get("animation").getValue()));
		}
		return event;
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}

		XMLElement root = file.load().getFirst();
		//���������̃p�[�X
		for (XMLElement e : root.getElement("term")) {
			String name = e.getAttributes().get("name").getValue();
			TermType type = TermType.valueOf(e.getAttributes().get("tt").getValue());
			String value = null;
			if (e.hasAttribute("value")) {
				value = e.getAttributes().get("value").getValue();
			}
			ActionTermStorage.getInstance().add(new ActionTerm(name, type, value));
		}
		ActionTermStorage.getInstance().printAll(System.out);

		//�A�j���[�V�����̃p�[�X
		for (XMLElement e : root.getElement("animation")) {
			String name = e.getAttributes().get("name").getValue();
			int w = e.getAttributes().get("w").getIntValue();
			int h = e.getAttributes().get("h").getIntValue();
			BufferedImage[] images = new SpriteSheet(e.getAttributes().get("spriteSheet").getValue()).rows(0, w, h).images();
			float mg = e.getAttributes().get("mg").getFloatValue();
			images = ImageEditor.resizeAll(images, mg * GameOption.getInstance().getDrawSize());
			FrameTimeCounter tc = new FrameTimeCounter(Arrays.stream(e.getAttributes().get("tc").safeSplit(",")).mapToInt(p -> Integer.valueOf(p)).toArray());
			Animation a = new Animation(tc, images);
			a.setRepeat(false);
			animationMap.put(name, a);
		}
		System.out.println(animationMap);

		//�T�E���h�̃p�[�X
		Map<String, Sound> soundMap = new HashMap<>();
		for (XMLElement e : root.getElement("sound")) {
			String name = e.getAttributes().get("name").getValue();
			Sound s = new SoundBuilder(e.getAttributes().get("file").getValue()).builde();
			soundMap.put(name, s);
		}
		System.out.println(soundMap);

		//�A�C�e���̃p�[�X
		for (XMLElement e : root.getElement("item")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			Item i = new Item(name, desc);
			if (e.hasAttribute("waitTime")) {
				i.setWaitTime(e.getAttributes().get("waitTime").getIntValue());
			}
			if (e.hasAttribute("sound")) {
				i.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("term")) {
				for (String v : e.getAttributes().get("term").safeSplit(",")) {
					i.addTerm(ActionTermStorage.getInstance().get(v));
				}
			}
			if (e.hasAttribute("slot")) {
				ItemEqipmentSlot slot = ItemEqipmentSlotStorage.getInstance().get(e.getAttributes().get("slot").getValue());
				i.setEqipmentSlot(slot);
			}
			if (e.hasAttribute("wmt")) {
				WeaponMagicType wmt = WeaponMagicTypeStorage.getInstance().get(e.getAttributes().get("wmt").getValue());
				i.setWeaponMagicType(wmt);
			}
			if (e.hasAttribute("area")) {
				i.setArea(e.getAttributes().get("area").getIntValue());
			}
			if (e.hasAttribute("sort")) {
				i.setSort(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("spellTime")) {
				i.setSpellTime(e.getAttributes().get("spellTime").getIntValue());
			}
			if (e.hasAttribute("dcs")) {
				for (String statusName : e.getAttributes().get("dcs").safeSplit(",")) {
					StatusKey key = StatusKeyStorage.getInstance().get(statusName);
					i.getDamageCalcStatusKey().add(key);
				}
			}
			//�C�x���g
			for (XMLElement ee : e.getElement("battleEvent")) {
				i.addBattleEvent(parseEvent(ee));
			}
			for (XMLElement ee : e.getElement("fieldEvent")) {
				i.addFieldEvent(parseEvent(ee));
			}
			//�A�C�e���������ʁi�X�e�[�^�X�j
			if (i.canEqip()) {
				StatusValueSet s = new StatusValueSet();
				for (XMLElement ee : e.getElement("eqStatus")) {
					String tgt = ee.getAttributes().get("tgt").getValue();
					float value = ee.getAttributes().get("value").getFloatValue();
					s.get(tgt).add(value);
				}
				i.setEqStatus(s);
			}
			//�A�C�e���������ʁi�ϐ��j
			if (i.canEqip()) {
				AttributeValueSet a = new AttributeValueSet();
				for (XMLElement ee : e.getElement("eqAttr")) {
					String tgt = ee.getAttributes().get("tgt").getValue();
					float value = ee.getAttributes().get("value").getFloatValue();
					a.get(tgt).add(value);
				}
				i.setEqAttr(a);
			}
			ItemStorage.getInstance().add(i);
		}
		ItemStorage.getInstance().printAll(System.out);

		//�U���̃p�[�X
		ActionType actionType = ActionType.ATTACK;
		for (XMLElement e : root.getElement("attack")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			CmdAction a = new CmdAction(actionType, name, desc);
			if (e.hasAttribute("waitTime")) {
				a.setWaitTime(e.getAttributes().get("waitTime").getIntValue());
			}
			if (e.hasAttribute("sound")) {
				a.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("term")) {
				for (String v : e.getAttributes().get("term").safeSplit(",")) {
					a.addTerm(ActionTermStorage.getInstance().get(v));
				}
			}
			if (e.hasAttribute("area")) {
				a.setArea(e.getAttributes().get("area").getIntValue());
			}
			if (e.hasAttribute("sort")) {
				a.setSort(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("spellTime")) {
				a.setSpellTime(e.getAttributes().get("spellTime").getIntValue());
			}
			//�C�x���g
			for (XMLElement ee : e.getElement("battleEvent")) {
				a.addBattleEvent(parseEvent(ee));
			}
			for (XMLElement ee : e.getElement("fieldEvent")) {
				a.addFieldEvent(parseEvent(ee));
			}
			getInstance().add(a);

		}

		//����U���̃p�[�X
		actionType = ActionType.SPECIAL_ATTACK;
		for (XMLElement e : root.getElement("spAttack")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			CmdAction a = new CmdAction(actionType, name, desc);
			if (e.hasAttribute("waitTime")) {
				a.setWaitTime(e.getAttributes().get("waitTime").getIntValue());
			}
			if (e.hasAttribute("sound")) {
				a.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("term")) {
				for (String v : e.getAttributes().get("term").safeSplit(",")) {
					a.addTerm(ActionTermStorage.getInstance().get(v));
				}
			}
			if (e.hasAttribute("area")) {
				a.setArea(e.getAttributes().get("area").getIntValue());
			}
			if (e.hasAttribute("sort")) {
				a.setSort(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("spellTime")) {
				a.setSpellTime(e.getAttributes().get("spellTime").getIntValue());
			}
			//�C�x���g
			for (XMLElement ee : e.getElement("battleEvent")) {
				a.addBattleEvent(parseEvent(ee));
			}
			for (XMLElement ee : e.getElement("fieldEvent")) {
				a.addFieldEvent(parseEvent(ee));
			}
			getInstance().add(a);
		}
		//���@�̃p�[�X
		actionType = ActionType.MAGIC;
		for (XMLElement e : root.getElement("magic")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			CmdAction a = new CmdAction(actionType, name, desc);
			if (e.hasAttribute("waitTime")) {
				a.setWaitTime(e.getAttributes().get("waitTime").getIntValue());
			}
			if (e.hasAttribute("sound")) {
				a.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("term")) {
				for (String v : e.getAttributes().get("term").safeSplit(",")) {
					a.addTerm(ActionTermStorage.getInstance().get(v));
				}
			}
			if (e.hasAttribute("area")) {
				a.setArea(e.getAttributes().get("area").getIntValue());
			}
			if (e.hasAttribute("sort")) {
				a.setSort(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("spellTime")) {
				a.setSpellTime(e.getAttributes().get("spellTime").getIntValue());
			}
			//�C�x���g
			for (XMLElement ee : e.getElement("battleEvent")) {
				a.addBattleEvent(parseEvent(ee));
			}
			for (XMLElement ee : e.getElement("fieldEvent")) {
				a.addFieldEvent(parseEvent(ee));
			}
			getInstance().add(a);
		}
		//���̑��s���̃p�[�X(ESCAPE�C�x���g
		actionType = ActionType.OTHER;
		for (XMLElement e : root.getElement("other")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			CmdAction a = new CmdAction(actionType, name, desc);
			if (e.hasAttribute("waitTime")) {
				a.setWaitTime(e.getAttributes().get("waitTime").getIntValue());
			}
			if (e.hasAttribute("sound")) {
				a.setSound(soundMap.get(e.getAttributes().get("sound").getValue()));
			}
			if (e.hasAttribute("term")) {
				for (String v : e.getAttributes().get("term").safeSplit(",")) {
					a.addTerm(ActionTermStorage.getInstance().get(v));
				}
			}
			if (e.hasAttribute("area")) {
				a.setArea(e.getAttributes().get("area").getIntValue());
			}
			if (e.hasAttribute("sort")) {
				a.setSort(e.getAttributes().get("sort").getIntValue());
			}
			if (e.hasAttribute("spellTime")) {
				a.setSpellTime(e.getAttributes().get("spellTime").getIntValue());
			}
			//�C�x���g
			for (XMLElement ee : e.getElement("battleEvent")) {
				a.addBattleEvent(parseEvent(ee));
			}
			for (XMLElement ee : e.getElement("fieldEvent")) {
				a.addFieldEvent(parseEvent(ee));
			}
			getInstance().add(a);
		}

		printAll(System.out);

		animationMap.clear();
		file.dispose();
	}

}