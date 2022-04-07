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

import java.awt.geom.Point2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.AnimationSprite;
import kinugasa.object.BasicSprite;
import kinugasa.util.TimeCounter;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/06/18<br>
 * @author Dra<br>
 * <br>
 */
public class MessageWindowSprite extends BasicSprite {

	private MessageWindowModel windowModel;
	private Text currentText;
	private TextStorage textStorage;
	private int lineLength = 32;
	private AnimationSprite continueIcon = null;
	private AnimationSprite selectIcon = null;

	private void resetContinueIconLocation(Point2D.Float windowOldLocation, Point2D.Float windowNewLocation) {
		System.out.println("old:" + windowOldLocation);
		System.out.println("new:" + windowNewLocation);

		float cIconXGap = continueIcon.getX() - windowOldLocation.x;
		float cIconYGap = continueIcon.getY() - windowOldLocation.y;
		
		System.out.println("X:" + cIconXGap);
		System.out.println("Y:" + cIconYGap);
		
		continueIcon.setX(windowNewLocation.x + cIconXGap);
		continueIcon.setY(windowNewLocation.y + cIconYGap);

//		continueIcon.setX(windowNewLocation.x + (continueIcon.getX() - windowOldLocation.x));
//		continueIcon.setY(windowNewLocation.y + (continueIcon.getY() - windowOldLocation.y));
	}

	public MessageWindowSprite(MessageWindowModel model, float x, float y, float width, float height) {
		super(x, y, width, height);
		this.currentText = new Text();
		this.windowModel = model;
		this.textStorage = null;
	}

	public MessageWindowSprite(String text, MessageWindowModel model, float x, float y, float width, float height) {
		super(x, y, width, height);
		this.currentText = new Text(text, TimeCounter.TRUE);
		this.windowModel = model;
		this.textStorage = null;
	}

	public MessageWindowSprite(String text, TimeCounter tc, MessageWindowModel model, float x, float y, float width, float height) {
		super(x, y, width, height);
		this.currentText = new Text(text, tc);
		this.windowModel = model;
		this.textStorage = null;
	}

	public MessageWindowSprite(String[] text, TimeCounter tc, MessageWindowModel model, float x, float y, float width, float height) {
		super(x, y, width, height);
		StringBuilder b = new StringBuilder();
		for (String line : text) {
			b.append(line);
		}
		this.currentText = new Text(b.toString(), tc);
		this.windowModel = model;
		this.textStorage = null;
	}

	public MessageWindowSprite(Text text, TimeCounter tc, MessageWindowModel model, float x, float y, float width, float height) {
		super(x, y, width, height);
		this.currentText = text;
		this.windowModel = model;
		this.textStorage = null;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (isVisible() & isExist()) {
			windowModel.draw(g, this);
		}
	}

	public MessageWindowSprite setLineLength(int lineLength) {
		this.lineLength = lineLength;
		return this;
	}

	public MessageWindowSprite setContinueIcon(AnimationSprite continueIcon) {
		this.continueIcon = continueIcon;
		return this;
	}

	public MessageWindowSprite setSelectIcon(AnimationSprite selectIcon) {
		this.selectIcon = selectIcon;
		return this;
	}

	public MessageWindowSprite setTextStorage(TextStorage textStorage) {
		this.textStorage = textStorage;
		return this;
	}

	public void setText(String text) {
		setText(new Text(text, TimeCounter.TRUE));
	}

	public void setText(Text text) {
		this.currentText = text;
	}

	public MessageWindowModel getWindowModel() {
		return windowModel;
	}

	public Text getCurrentText() {
		return currentText;
	}

	public TextStorage getTextStorage() {
		return textStorage;
	}

	public int getLineLength() {
		return lineLength;
	}

	public AnimationSprite getContinueIcon() {
		return continueIcon;
	}

	public AnimationSprite getSelectIcon() {
		return selectIcon;
	}

	public String[] split() {
		return currentText.split(lineLength);
	}

	@Override
	public MessageWindowSprite clone() {
		MessageWindowSprite result = (MessageWindowSprite) super.clone();
		result.windowModel = this.windowModel.clone();
		result.selectIcon = this.selectIcon.clone();
		result.continueIcon = this.continueIcon.clone();
		return result;
	}

	@Override
	public void setX(float x) {
		Point2D.Float oldLocation = (Point2D.Float) getLocation().clone();
		super.setX(x);
		resetContinueIconLocation(oldLocation, getLocation());
	}

	@Override
	public void setY(float y) {
		Point2D.Float oldLocation = (Point2D.Float) getLocation().clone();
		super.setY(y);
		resetContinueIconLocation(oldLocation, getLocation());
	}

	@Override
	public void setLocation(Point2D.Float location) {
		Point2D.Float oldLocation = (Point2D.Float) getLocation().clone();
		super.setLocation(location);
		resetContinueIconLocation(oldLocation, getLocation());
	}

	@Override
	public void setLocation(float x, float y) {
		Point2D.Float oldLocation = (Point2D.Float) getLocation().clone();
		super.setLocation(x, y);
		resetContinueIconLocation(oldLocation, getLocation());
	}

}
