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
package kinugasa.game.ui;

import java.util.Collection;
import java.util.LinkedList;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LoopCall;
import kinugasa.game.field4.FieldEventSystem;
import kinugasa.object.BasicSprite;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_15:05:14<br>
 * @author Shinacho<br>
 */
public class MessageWindow extends BasicSprite {

	private MessageWindowModel model;
	private TextStorage textStorage;
	private Text text;

	public MessageWindow(float x, float y, float w, float h, MessageWindowModel model) {
		this(x, y, w, h, model, new TextStorage("AUTO_FROM_MW"), Text.of(""));
	}

	public MessageWindow(float x, float y, float w, float h) {
		this(x, y, w, h, new SimpleMessageWindowModel(), new TextStorage("AUTO_FROM_MW"), Text.of(""));
	}

	public MessageWindow(float x, float y, float w, float h, Text text) {
		this(x, y, w, h, new SimpleMessageWindowModel(), new TextStorage("AUTO_FROM_MW"), text);
	}

	public MessageWindow(float x, float y, float w, float h, MessageWindowModel model, TextStorage ts, Text text) {
		super(x, y, w, h);
		this.model = model;
		this.text = text;
		this.textStorage = ts;
	}

	public Text getText() {
		return text;
	}

	@LoopCall
	@Override
	public void update() {
		text.isReaching();

		if (text.isAllVisible()) {
			if (text.getEvents() != null && !text.getEvents().isEmpty()) {
				if (!set) {
					FieldEventSystem.getInstance().setEvent(new LinkedList<>(text.getEvents()));
					set = true;
				}
			}
		}
	}
	private boolean set = false;

	public void setModel(MessageWindowModel model) {
		this.model = model;
	}

	public MessageWindowModel getModel() {
		return model;
	}

	public TextStorage getTextStorage() {
		return textStorage;
	}

	public void setTextStorage(TextStorage textStorage) {
		this.textStorage = textStorage;
	}

	public void showAllNow() {
		text.allText();
	}

	public void setText(Text text) {
		this.text = text;
		set = false;
		select = 0;
	}

	public void setTextDirect(String text) {
		setText(Text.of(text));
	}

	public void clearText() {
		setText(Text.empty());
	}

	public void setTextFromId(String id) {
		setText(textStorage.get(id));
	}

	public boolean isAllVisible() {
		return text.isAllVisible();
	}

	public String getVisibleText() {
		return text.getVisibleText();
	}

	@Override
	public void draw(GraphicsContext g) {
		model.draw(g, this);
	}

	public void reset() {
		text.reset();
		this.select = 0;
	}

	public boolean hasNext() {
		return text.hasNext();
	}

	public void next() {
		setText(textStorage.get(text.getNextId()));
		getText().reset();
		select = 0;
	}

	public void choicesNext() {
		setText(textStorage.get(getChoiceOption().getNextId()));
		getText().reset();
	}

	public boolean isChoice() {
		return (getText() instanceof Choice);
	}

	public Choice getChoice() {
		return (Choice) getText();
	}

	private int select = 0;

	public int getSelect() {
		return select;
	}

	public <T extends Nameable> T of(Storage<? extends T> s) {
		return s.get(getChoice().getOptions().get(select).getText());
	}

	public <T extends Nameable> T of(Collection<? extends T> s) {
		for (T t : s) {
			if (t.getName().equals(getChoice().getText())) {
				return t;
			}
		}
		throw new NameNotFoundException("name not found : " + getChoice() + " of / " + s);
	}

	public void close() {
		setVisible(false);
	}

	public void setSelect(int select) {
		this.select = select;
	}

	public void nextSelect() {
		if (!isChoice()) {
			return;
		}
		select++;
		if (getChoice().getOptions().size() <= select) {
			select = 0;
		}
	}

	public void prevSelect() {
		if (!isChoice()) {
			return;
		}
		select--;
		if (0 > select) {
			select = getChoice().getOptions().size() - 1;
		}
	}

	public Text getChoiceOption() {
		return getChoice().getOptions().get(select);
	}

}
