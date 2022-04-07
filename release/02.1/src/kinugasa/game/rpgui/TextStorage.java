/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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
package kinugasa.game.rpgui;

import java.awt.image.BufferedImage;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.object.ImagePainter;
import kinugasa.object.ImagePainterStorage;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.Disposable;
import kinugasa.resource.Storage;
import kinugasa.resource.sound.CachedSound;
import kinugasa.resource.sound.SoundBuilder;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.StopWatch;
import kinugasa.util.StringUtil;

/**
 * メッセージウインドウに表示可能な、フィールドマップに関連付けられたテキストを保持します.
 * <br>
 *
 * @version 1.0.0 - 2015/06/18<br>
 * @author Dra<br>
 * <br>
 */
public final class TextStorage extends Storage<Text> implements XMLFileSupport, Disposable {

	public TextStorage() {
	}
	private MessageWindowSprite messageWindowSprite;

	@Override
	public void readFromXML(String filePath)
			throws IllegalXMLFormatException, FileNotFoundException, ContentsIOException {

		StopWatch watch = new StopWatch().start();
		XMLFile reader = new XMLFile(filePath).load();
		XMLElement root = reader.getFirst();
		if (!"kinugasaMessageWindowText".equals(root.getName())) {
			throw new IllegalXMLFormatException("illegal root node name :" + root);
		}

		XMLElement messageWindowElement = root.getElement("messageWindow").get(0);
		int x = messageWindowElement.getAttributes().get("x").getIntValue();
		int y = messageWindowElement.getAttributes().get("y").getIntValue();
		int width = messageWindowElement.getAttributes().get("width").getIntValue();
		int height = messageWindowElement.getAttributes().get("height").getIntValue();
		int length = messageWindowElement.getAttributes().get("length").getIntValue();
		MessageWindowModel windowModel = MessageWindowModelStorage.getInstance().get(messageWindowElement.getAttributes().get("messageWindowModelName").getValue());
		messageWindowSprite = new MessageWindowSprite(windowModel, x, y, width, height);
		messageWindowSprite.setLineLength(length);
		//
		if (messageWindowElement.hasElement("continueIcon")) {
			messageWindowSprite.setContinueIcon(createContinueIcon(messageWindowElement.getElement("continueIcon").get(0)));
		}
		//
		List<XMLElement> textElementList = messageWindowElement.getElement("text");
		for (int i = 0, size = textElementList.size(); i < size; i++) {
			XMLElement textElement = textElementList.get(i);
			String textId = textElement.getAttributes().get("id").getValue();
			CachedSound sound = null;
			if (textElement.hasElement("sound")) {
				XMLElement soundElement = textElement.getElement("sound").get(0);
				String file = soundElement.getAttributes().get("file").getValue();
				float volume = 1f;
				if (soundElement.getAttributes().contains("volume")) {
					volume = soundElement.getAttributes().get("volume").getFloatValue();
				}
				sound = new SoundBuilder(file).setMasterGain(volume).builde();
			}
			FrameTimeCounter timeCounter
					= new FrameTimeCounter(StringUtil.parseIntCSV(textElement.getElement("speed").get(0).getAttributes().get("frame").getValue(), ","));
			String message = textElement.getElement("message").get(0).getValue();
			String[] nextId = null;
			if (textElement.hasElement("next")) {
				nextId = new String[textElement.getElement("next").size()];
				List<XMLElement> nextIdElements = textElement.getElement("next");
				for (int j = 0, nextIdSize = nextId.length; j < nextIdSize; j++) {
					nextId[j] = nextIdElements.get(j).getAttributes().get("id").getValue();
				}
			}
			add(new Text(textId, sound, timeCounter, message, nextId));
		}
		messageWindowSprite.setTextStorage(this);
		watch.stop();
		GameLog.printInfo("テキストがロードされました name=[" + reader.getName() + "] (" + watch.getTime() + " ms)");
		List<Text> text = asList();
		for (int i = 0, size = size(); i < size; i++) {
			GameLog.printInfo("- " + text.get(i));
		}
	}

	public Text[] getNextText(Text text) {
		if (!text.hasNext()) {
			GameLog.printInfo("NextTextが要求されましたが、次のテキストがありません text=[" + text + "]");
			return null;
		}
		String[] nextId = text.getNextId();
		Text[] result = new Text[nextId.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = get(nextId[i]);
		}
		return result;
	}

	public MessageWindowSprite getMessageWindowSprite() {
		return messageWindowSprite;
	}

	@Override
	public void dispose() {
		clear();
	}

	private AnimationSprite createContinueIcon(XMLElement continueIconElement) {
		int x = continueIconElement.getAttributes().get("x").getIntValue();
		int y = continueIconElement.getAttributes().get("y").getIntValue();
		int width = continueIconElement.getAttributes().get("width").getIntValue();
		int height = continueIconElement.getAttributes().get("height").getIntValue();
		FrameTimeCounter tc = new FrameTimeCounter(StringUtil.parseIntCSV(continueIconElement.getAttributes().get("frame").getValue()));
		ImagePainter painter = ImagePainterStorage.getInstance().get(continueIconElement.getAttributes().get("painterName").getValue());
		boolean visible = continueIconElement.getAttributes().get("visible").getBool();
		Animation animation = null;
		if (continueIconElement.hasElement("image")) {
			animation = createAnimationByImage(continueIconElement.getElement("image"), tc);
		} else if (continueIconElement.hasElement("spriteSheet")) {
			animation = createAnimationBySpriteSheet(continueIconElement.getElement("spriteSheet").get(0), tc);
		}
		AnimationSprite sprite = new AnimationSprite(x, y, width, height, animation, painter);
		sprite.setVisible(visible);
		return sprite;
	}

	private Animation createAnimationBySpriteSheet(XMLElement element, FrameTimeCounter tc) {
		int x = element.getAttributes().get("x").getIntValue();
		int y = element.getAttributes().get("y").getIntValue();
		int width = element.getAttributes().get("width").getIntValue();
		int height = element.getAttributes().get("height").getIntValue();
		String baseImagePath = element.getAttributes().get("src").getValue();
		BufferedImage[] images = null;
		if ("SPLIT".equals(element.getAttributes().get("cutType").getValue())) {
			images = new SpriteSheet(baseImagePath).split(width, height).images();
		} else if ("COLUMN".equals(element.getAttributes().get("cutType").getValue())) {
			images = new SpriteSheet(baseImagePath).columns(x, width, height).images();
		} else if ("ROW".equals(element.getAttributes().get("cutType").getValue())) {
			images = new SpriteSheet(baseImagePath).rows(y, width, height).images();
		}
		return new Animation(tc, images);
	}

	private Animation createAnimationByImage(List<XMLElement> imageElements, FrameTimeCounter tc) {
		SpriteSheet spriteSheet = new SpriteSheet();
		for (int i = 0, size = imageElements.size(); i < size; i++) {
			XMLElement element = imageElements.get(i);
			String baseImagePath = element.getAttributes().get("src").getValue();
			if (element.hasElement("cutter")) {
				XMLElement cutterElement = element.getElement("cutter").get(0);
				int x = cutterElement.getAttributes().get("x").getIntValue();
				int y = cutterElement.getAttributes().get("y").getIntValue();
				int width = cutterElement.getAttributes().get("width").getIntValue();
				int height = cutterElement.getAttributes().get("height").getIntValue();
				spriteSheet.add(ImageUtil.trimming(ImageUtil.load(baseImagePath), x, y, width, height));
			} else {
				spriteSheet.add(ImageUtil.load(baseImagePath));
			}
		}
		return new Animation(tc, spriteSheet.images());
	}
}
