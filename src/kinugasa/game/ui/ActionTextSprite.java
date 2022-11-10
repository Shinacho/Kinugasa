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
package kinugasa.game.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kinugasa.game.ui.TextLabelModel;
import kinugasa.game.ui.TextLabelSprite;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/09/27<br>
 * @author Dra<br>
 * <br>
 */
public class ActionTextSprite extends TextLabelSprite implements ActionSupport {

	private List<Action> actions;

	public ActionTextSprite(CharSequence text, TextLabelModel labelModel, float x, float y, Action... actions) {
		super(text, labelModel, x, y);
		this.actions = new ArrayList<Action>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionTextSprite(String text, TextLabelModel labelModel, float x, float y, float w, float h, Action... actions) {
		super(text, labelModel, x, y, w, h);
		this.actions = new ArrayList<Action>();
		this.actions.addAll(Arrays.asList(actions));
	}

	@Override
	public void addAction(Action... actions) {
		this.actions.addAll(Arrays.asList(actions));
	}

	@Override
	public Action[] getActions() {
		return (Action[]) actions.toArray();
	}

	@Override
	public Action getAction() {
		return actions.isEmpty() ? null : actions.get(0);
	}

	@Override
	public void setActions(Action... actions) {
		this.actions = new ArrayList<Action>();
		this.actions.addAll(Arrays.asList(actions));
	}

	@Override
	public void exec() {
		for (int i = 0, size = actions.size(); i < size; i++) {
			actions.get(i).exec();
		}
	}

	@Override
	public void clearActions() {
		this.actions.clear();
	}

	private boolean selected = false;

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void selected() {
		selected = true;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

}